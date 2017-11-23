package com.cedarhd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.biz.AlarmTaskBiz;
import com.cedarhd.biz.MenuFunctionBiz;
import com.cedarhd.changhui.ChBespokeListActivity;
import com.cedarhd.changhui.ChClientInfoActivity;
import com.cedarhd.changhui.ChClientListActivity;
import com.cedarhd.changhui.ChContactListActivity;
import com.cedarhd.changhui.ChCustomWorkPlanListActivity;
import com.cedarhd.changhui.ChProductListActivity;
import com.cedarhd.constants.PreferencesConfig;
import com.cedarhd.constants.enums.EnumFunctionPoint;
import com.cedarhd.crm.CRMClewListActivity;
import com.cedarhd.crm.CRMExpenseListActivity;
import com.cedarhd.crm.CRMReceiptListActivity;
import com.cedarhd.crm.CRMSelectConpactListActivity;
import com.cedarhd.crm.SaleChanceSummaryInfoActivity;
import com.cedarhd.crm.SaleFunnelActivity;
import com.cedarhd.helpers.CheckVersionHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.ShakeListenerUtils;
import com.cedarhd.helpers.ShakeListenerUtils.OnShakeListener;
import com.cedarhd.helpers.SharedPreferencesHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Client;
import com.cedarhd.models.MenuChildItem;
import com.cedarhd.models.Remind;
import com.cedarhd.models.User;
import com.cedarhd.models.部门;
import com.cedarhd.rad.RadReportListActivity;
import com.cedarhd.services.PhoneService;
import com.cedarhd.services.TaskAlarmService;
import com.cedarhd.slt.SltApproveOrderListActivity;
import com.cedarhd.slt.SltCaculateListActivity;
import com.cedarhd.slt.SltProductTypeListActivity;
import com.cedarhd.slt.SltSaleTargetActivity;
import com.cedarhd.slt.SltScanCodeActivity;
import com.cedarhd.slt.SltShopCarListActivity;
import com.cedarhd.slt.SltShopOrderListActivity;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.widget.BoeryunDialog;
import com.j256.ormlite.dao.Dao;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.tencent.stat.StatService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 新版首页菜单，根据需要显示需要的模块
 *
 * @author Administrator
 */
@SuppressLint("NewApi")
public class MenuNewActivity extends BaseActivity {
    private final String TAG = "MenuNewActivity";
    private Context mContext;
    private GridView gridView;
    private ImageView ivAdd;
    public static boolean cancleUpdate = false;

    private CommanAdapter<MenuChildItem> mAdapter;
    private MenuFunctionBiz mFunctionBiz;
    private List<EnumFunctionPoint> mFunctions;

    private ZLServiceHelper zlServiceHelper;
    private ORMDataHelper ormDataHelper;

    private List<MenuChildItem> mGridItems;

    private NetReceiver netReceive;
    private CheckVersionHelper checkVersionHelper;

    /***
     * 网络连接警告
     */
    private RelativeLayout rl_warn_net;

    String permissions = "";
    TextView tvTitle;
    ImageView ivSpeechMenu;

    /**
     * 摇一摇监听类
     */
    ShakeListenerUtils mShakeListener;

    private SharedPreferencesHelper sharedPreferencesHelper;

    private boolean mIsAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_new);
        // UmengUpdateAgent.setUpdateOnlyWifi(false);
        // UmengUpdateAgent.update(this);

        init();
        findviews();
        getPermission();
        setOnClickListener();
        createDialog();
        registerNetReceiver();
        // createNotificationService(); //创建通知服务

        // 监听来电提醒
        createPhoneService();

        if (mIsAlarm) {
            createAlarmService();
        }
        downLoadData();
        // sendAlarm(); //注册闹钟

    }

    private void createAlarmService() {
        startService(new Intent(mContext, TaskAlarmService.class));
    }

    /**
     * 注册广播监听网络状态
     */
    private void registerNetReceiver() {
        IntentFilter intentFilterNet = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilterNet.addCategory(Intent.CATEGORY_DEFAULT);
        netReceive = new NetReceiver();
        registerReceiver(netReceive, intentFilterNet);
    }

    AlertDialog dialog;
    private String statusStr;
    private static final int SUCCCESS_READ = 1021;

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MenuNewActivity.this);
        builder.setTitle("提示");
        builder.setMessage("是否将数据设置为已读");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        statusStr = httpUtils.httpGet(path);
                        LogUtils.i("out", statusStr + path);
                        JSONObject object;
                        try {
                            object = new JSONObject(statusStr);
                            if (object.getInt("Status") == 1) {

                                fetchRemind();
                                handler.sendEmptyMessage(SUCCCESS_READ);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", null);
        dialog = builder.create();
    }

    private String path = Global.BASE_URL + Global.EXTENSION
            + "ReadStatus/SetAllDataRead";
    HttpUtils httpUtils = new HttpUtils();
    private ImageView iv_main;

    private class shakeLitener implements OnShakeListener {

        public void onShake() {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        }
    }

    /**
     * 根据权限加载模块
     */
    private void getPermission() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    zlServiceHelper.GetPermissions(messageHandler);
                } catch (Exception e) {
                    LogUtils.e(TAG, "获取权限异常" + e);
                }
            }
        }).start();
    }

    /**
     * 初始化参数
     */
    private void init() {
        mContext = MenuNewActivity.this;
        zlServiceHelper = new ZLServiceHelper();
        checkVersionHelper = new CheckVersionHelper(mContext, false);
        ormDataHelper = ORMDataHelper.getInstance(mContext);
        sharedPreferencesHelper = new SharedPreferencesHelper(mContext,
                PreferencesConfig.APP_USER_INFO);
        mFunctionBiz = new MenuFunctionBiz();

        mIsAlarm = sharedPreferencesHelper.getBooleanValue(
                PreferencesConfig.IS_OPEN_ALAMR_TASK, false);
    }

    private void findviews() {
        iv_main = (ImageView) findViewById(R.id.iv_main);
        tvTitle = (TextView) findViewById(R.id.tv_title_menu);
        ivAdd = (ImageView) findViewById(R.id.iv_add_menu);
        ivSpeechMenu = (ImageView) findViewById(R.id.iv_voice_menu);
        // TODO 隐藏语音按钮
        // ivSpeechMenu.setVisibility(View.GONE);

        if (Global.mUser != null && !TextUtils.isEmpty(Global.mUser.CorpName)) {
            String title = Global.mUser.CorpName;
            // String title = "中华老字号广州白云山蜜炼川贝枇杷膏";
            if (!TextUtils.isEmpty(title)) {
                int screenWidth = (int) (ViewHelper.getScreenWidth(mContext) - ViewHelper
                        .dip2px(mContext, 60));
                int txtSize = (int) tvTitle.getTextSize();
                int len = title.length();
                if (len > 25) {
                    title = title.substring(0, 23) + "..";
                    len = 25;
                }
                if (screenWidth < txtSize * len) {
                    // 缩小字体
                    txtSize = screenWidth / len;
                    tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtSize);
                    tvTitle.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                }
            }
            tvTitle.setText(title);
        }
        rl_warn_net = (RelativeLayout) findViewById(R.id.rl_warn_net_menu);
        gridView = (GridView) findViewById(R.id.gv_menu);

        mFunctions = mFunctionBiz.getDefaultFunctions();
        mGridItems = mFunctionBiz.getFunctions(mFunctions);

        LogUtils.i("mGridItems", "--" + mGridItems.size());
        mAdapter = getAdapter(mGridItems);
        gridView.setAdapter(mAdapter);
        // mAdapter.notifyDataSetChanged();
        // LogUtils.i("mGridItems", "--" + mAdapter.getCount());
    }

    /**
     * 模块模块点击事件监听
     *
     * @param
     * @param newActivity 新跳转页面
     */
    private void setMemuItemClickListener(final Class<?> newActivity) {
        Intent intent = new Intent();
        intent.setClass(MenuNewActivity.this, newActivity);
        startActivity(intent);
    }

    /**
     * 创建通知服务
     */
    private void createPhoneService() {
        Global.mContext = mContext;
        ActivityManager mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> listServices = mActivityManager
                .getRunningServices(1000);
        // final String notificationService =
        // "com.cedarhd.services.NotificationService";
        final String notificationService = "com.cedarhd.services.PhoneService";
        boolean isServiceStart = hasNotificationServiceStart(listServices,
                notificationService);
        if (!isServiceStart) {
            // Toast.makeText(context, "启动电话监听了", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, PhoneService.class);
            startService(intent); // 启动服务
        } else {
            LogUtils.e("service", "service 已启动！");
            // DynamicRemindService mService = DynamicRemindService
            // .getServiceInstance(context);
            // mService.downLoadData();
        }
    }

    private boolean hasNotificationServiceStart(
            List<ActivityManager.RunningServiceInfo> list,
            String notiServiceName) {
        for (ActivityManager.RunningServiceInfo service : list) {
            if (notiServiceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取未读数量 红色数字
     */
    private void fetchRemind() {
        if (HttpUtils.IsHaveInternet(mContext)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Remind remind = zlServiceHelper.GetRemindCount();
                    if (remind != null) {
                        Message msg = messageHandler.obtainMessage();
                        msg.obj = remind;
                        msg.what = GET_REMIND_SUCCESS;
                        messageHandler.sendMessage(msg);
                    } else {
                        messageHandler.sendEmptyMessage(GET_REMIND_FAILED);
                    }
                }
            }).start();
        } else {
            // // Toast toast = Toast.makeText(context,
            // // "需要连接到3G或者wifi因特网才能获取最新信息！",
            // // Toast.LENGTH_SHORT);
            // toast.setGravity(Gravity.CENTER, 0, 0);
            // toast.show();
        }
    }

    // 绑定监听事件
    private void setOnClickListener() {


        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (cancleUpdate) {
                    showShortToast("更新至新版本方可使用");
                } else {
                    MenuChildItem item = mGridItems.get(position);
                    EnumFunctionPoint point = item.ponit;
                    switch (point) {
                        case NOTICE: // 通知
                            setMemuItemClickListener(NoticeListActivity.class);
                            break;
                        case LOG: // 日志
                            setMemuItemClickListener(WorkLogListFragmentActivity.class);
                            break;
                        case ATTANCE: // 考勤
                            setMemuItemClickListener(TagActivity.class);
                            break;
                        case CLIENT: // 客户
                            setMemuItemClickListener(ClientListActivity.class);
                            break;
                        case CONTACTS: // 联系
                            setMemuItemClickListener(ClientConstactListActivity.class);
                            break;
                        case TASK: // 任务
                            boolean isCalendar = sharedPreferencesHelper
                                    .getBooleanValue(
                                            PreferencesConfig.IS_CALENDER_MODE_OPEN_TASK,
                                            true);
                            if (isCalendar) {
                                setMemuItemClickListener(TaskCalenderActivity.class);
                            } else {
                                setMemuItemClickListener(TaskTabListActivity.class);
                            }
                            break;
                        case APPLY: // 申请
                            setMemuItemClickListener(ApplyListFragmentActivity.class);
                            break;
                        case ORDER: // 订单
                            setMemuItemClickListener(OrderListActivity.class);
                            break;
                        case ADVERTISEMENT: // 广告：更多功能
                            setMemuItemClickListener(AdvertisementActivity.class);
                            // setMemuItemClickListener(SelectCityActivity.class);
                            // new
                            // CityPicker(MenuNewActivity.this).show(R.id.root_menu);
                            break;
                        // case R.layout.feedback_menu_item:// 反馈
                        // setMemuItemClickListener(FeedbackListActivity.class);
                        // break;
                        case COMMUNICATION:// 通讯录
                            setMemuItemClickListener(CommunicationListActivity.class);
                            break;
                        case PRODUCT:// 产品
                            setMemuItemClickListener(ProductListActivity.class);
                            break;
                        case PROJECT:// 项目
                            setMemuItemClickListener(ProjectListActivity.class);
                            break;
                        case APPLY_INBOX:// 入库
                            setMemuItemClickListener(ApplyInStockListActivity.class);
                            break;
                        case APPLY_OUTBOX:// 出库
                            setMemuItemClickListener(ApplyOutStockListActivity.class);
                            break;
                        case SALECHANCE: // 销售机会
                            setMemuItemClickListener(SaleChanceListActivity.class);
                            break;
                        case SALESUMARY: // 销售漏斗统计机会
                            setMemuItemClickListener(SaleFunnelActivity.class);
                            break;
                        case RANKING: // 排行榜
                            setMemuItemClickListener(SaleChanceSummaryInfoActivity.class);
                            break;
                        case CLEW: // 线索
                            setMemuItemClickListener(CRMClewListActivity.class);
                            break;
                        case CONPACT: // 合同
                            setMemuItemClickListener(CRMSelectConpactListActivity.class);
                            break;
                        case RECEIPET: // 收款单
                            setMemuItemClickListener(CRMReceiptListActivity.class);
                            break;
                        case EXPENSE: // 报销榜
                            setMemuItemClickListener(CRMExpenseListActivity.class);
                            break;
                        case RAD_PRODUCTLIST: // 森盟产品列表
                            // setMemuItemClickListener(RadProductListActivity.class);
                            setMemuItemClickListener(SltProductTypeListActivity.class);
                            break;
                        case RAD_ORDER: // 森盟订单列表
                            // setMemuItemClickListener(RadShopOrderListActivity.class);
                            setMemuItemClickListener(SltShopOrderListActivity.class);
                            break;
                        case RAD_CACULATE: // 森盟预算列表
                            setMemuItemClickListener(SltCaculateListActivity.class);
                            break;
                        case SLT_PRODUCTLIST: // 森盟预算列表
                            setMemuItemClickListener(SltCaculateListActivity.class);
                            break;
                        case SLT_SHOPCAR_LIST: // 森盟购物车列表
                            setMemuItemClickListener(SltShopCarListActivity.class);
                            break;
                        case SLT_APPROVE_ORDER: // 森盟订单审批表
                            setMemuItemClickListener(SltApproveOrderListActivity.class);
                            break;
                        case RAD_REPORT:// 上报模块
                            setMemuItemClickListener(RadReportListActivity.class);
                            break;
                        case RAD_SCAN_CODE:// 到货扫描
                            setMemuItemClickListener(SltScanCodeActivity.class);
                            break;
                        case SLT_SALE_TARGET:// 销售目标
                            setMemuItemClickListener(SltSaleTargetActivity.class);
                            break;
                        case CHANGHUI_WORK_PLAN:// 长汇工作计划
                            // setMemuItemClickListener(ChWorkPlanListActivity.class);
                            setMemuItemClickListener(ChCustomWorkPlanListActivity.class);
                            break;
                        case CHANGHUI_PRODUCT_LIST:// 长汇产品列表
                            // setMemuItemClickListener(ChWorkPlanListActivity.class);
                            setMemuItemClickListener(ChProductListActivity.class);
                            break;
                        case CHANGHUI_CONTACT_LIST:// 长汇合同列表
                            // setMemuItemClickListener(ChWorkPlanListActivity.class);
                            setMemuItemClickListener(ChContactListActivity.class);
                            break;
                        case CHANGHUI_BESPOKE_LIST:// 长汇预约列表
                            // setMemuItemClickListener(ChWorkPlanListActivity.class);
                            setMemuItemClickListener(ChBespokeListActivity.class);
                            break;
                        case CHANGHUI_CLIENT_LIST:// 长汇客户列表
                            // setMemuItemClickListener(ChWorkPlanListActivity.class);
                            setMemuItemClickListener(ChClientListActivity.class);
                            break;
                        case COMPANY_SPACE://公司空间
                            setMemuItemClickListener(CompanyShareSpaceActivity.class);
                            break;
                        case PROFIT_CALCULATOR://收益计算器
                            setMemuItemClickListener(ProfitCaculatorActivity.class);
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        ivAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPopwindow();
            }
        });

        rl_warn_net.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openSettingNet(mContext);
            }
        });

        iv_main.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // String url = "http://www.bohrsoft.com";
                // BrowserUtils.openBrowser(mContext, url);
            }
        });

        /** 打开语音菜单页面 */
        ivSpeechMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SpeechMenuActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateVersion();
        boolean isConnectedInternet = HttpUtils.IsHaveInternet(mContext);
        if (!isConnectedInternet) {
            Toast.makeText(mContext, "请检测网络连接，需要3G或者wifi网络才能获取最新信息！",
                    Toast.LENGTH_LONG).show();
        } else {
            fetchRemind();
        }

        LogUtils.i("MenuNewActivity", "获取焦点");
        // 摇一摇
        mShakeListener = new ShakeListenerUtils(MenuNewActivity.this);
        mShakeListener.setOnShakeListener(new shakeLitener());

        // 页面开始，腾讯统计数据
        StatService.onResume(this);

        // loadBanner();
    }

    private void loadBanner() {
        String bannerUrl = "http://crmhw.changhw.com:8018/banner.png";
        File bannerFile = ImageLoader.getInstance().getDiskCache()
                .get(bannerUrl);
        if (bannerFile != null) {
            try {
                long disctance = new Date().getTime()
                        - bannerFile.lastModified();
                LogUtils.i("createtime", "disctance=" + disctance);
                if (disctance > 1000 * 60 * 3) {
                    ImageLoader.getInstance().getDiskCache().remove(bannerUrl);
                    ImageLoader.getInstance().getMemoryCache()
                            .remove(bannerUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ImageLoader.getInstance().displayImage(bannerUrl, iv_main, "");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mShakeListener.stop();
        LogUtils.i("MenuNewActivity", "onPause---");
    }

    @Override
    protected void onStop() {
        super.onStop();

        LogUtils.i("MenuNewActivity", "onStop()---页面被停止");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.i("MenuNewActivity", "onDestroy() ---页面被销毁");
        unregisterReceiver(netReceive);// 取消广播注册

        // 页面结束，腾讯统计数据
        StatService.onPause(this);
    }

    /**
     * 打开系统网络设置
     *
     * @param context
     */
    public static void openSettingNet(Context context) {
        Intent intent = null;
        // 判断手机系统的版本 即API大于10 就是3.0或以上版
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
            LogUtils.d("Menu", "api level 10");
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings",
                    "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
            LogUtils.d("Menu", "api level less 10");
        }
        context.startActivity(intent);
    }

    public static final int GET_EMAIL_DATA_SUCCESS = 230;
    public static final int GET_EMAIL_DATA_FAILED = 231;

    public static final int UPLOAD_SUCCESS = 232;
    public static final int UPLOAD_FAILURE = 233;
    public static final int SHOW_IMAGE_SUCCESS = 234;
    public static final int SHOW_IMAGE_FAILUREE = 235;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_EMAIL_DATA_SUCCESS: // 获得未读数量
                    int count = msg.arg1;
                    if (count > 0) {
                    }
                    break;
                case UPLOAD_SUCCESS:
                    Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
                    break;
                case UPLOAD_FAILURE:
                    Toast.makeText(mContext, "上传头像失败", Toast.LENGTH_SHORT).show();
                    break;
                case SHOW_IMAGE_SUCCESS:
                    byte[] data = (byte[]) msg.obj;
                    Bitmap avatar = BitmapFactory.decodeByteArray(data, 0,
                            data.length);
                    LogUtils.i("leo2", "----->" + data.length);
                    // iv_main.setImageBitmap(avatar);
                    break;
                case SHOW_IMAGE_FAILUREE:
                    // iv_main.setImageResource(R.drawable.logo_hn);
                    break;
                case SUCCCESS_READ:
                    Toast.makeText(MenuNewActivity.this, "所有数据已经设置为已读",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public final int GET_REMIND_SUCCESS = 0;
    public final int GET_REMIND_FAILED = 1;
    public final int GET_LOAD_PERMISSION = 3; // 获得模块权限
    public Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int whatMsg = msg.what;
            switch (whatMsg) {
                case GET_REMIND_SUCCESS:
                    Remind remind = (Remind) msg.obj;
                    remindMenu(remind);
                    break;
                case GET_REMIND_FAILED:
                    break;
                case GET_LOAD_PERMISSION:
                    permissions = (String) msg.obj;
                    sharedPreferencesHelper.putValue(
                            PreferencesConfig.POINT_PERMISSION, permissions);
                    loadPermissionPoint(getShowDefaultChanghuiPoints());
                    if (permissions != null) {
                        loadPermissionPoint(getShowPointsByPermission());
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 显示小红字提醒
     *
     * @param remind
     */
    private void remindMenu(Remind remind) {
        for (MenuChildItem item : mGridItems) {
            EnumFunctionPoint point = item.ponit;
            switch (point) {
                case NOTICE: // 通知
                    item.count = Integer.parseInt(remind.Notice);
                    break;
                case LOG: // 日志
                    item.count = Integer.parseInt(remind.Log);
                    // item.count = 0; //
                    break;
                case ATTANCE: // 考勤
                    // item.count = Integer.parseInt(remind.);
                    break;
                case CLIENT: // 客户
                    item.count = Integer.parseInt(remind.Client);
                    break;
                case CONTACTS: // 联系
                    item.count = Integer.parseInt(remind.Contacts);
                    break;
                case TASK: // 任务
                    item.count = Integer.parseInt(remind.Task);
                    break;
                case APPLY: // 申请
                    item.count = remind.Apply;
                    break;
                case SALECHANCE: // 销售机会
                    item.count = remind.SaleChance;
                    break;
                case SALESUMARY: // 销售漏斗统计机会
                    // item.count = Integer.parseInt(remind.Log);
                    break;
                // case ORDER: // 订单
                // setMemuItemClickListener(OrderListActivity.class);
                // break;
                // case ADVERTISEMENT: // 广告：更多功能
                // setMemuItemClickListener(AdvertisementActivity.class);
                // break;
                // // case R.layout.feedback_menu_item:// 反馈
                // // setMemuItemClickListener(FeedbackListActivity.class);
                // // break;
                // case COMMUNICATION:// 通讯录
                // setMemuItemClickListener(CommunicationListActivity.class);
                // break;
                // case PRODUCT:// 产品
                // setMemuItemClickListener(ProductListActivity.class);
                // break;
                // case PROJECT:// 项目
                // setMemuItemClickListener(ProjectListActivity.class);
                // break;
                // case APPLY_INBOX:// 入库
                // setMemuItemClickListener(ApplyInStockListActivity.class);
                // break;
                // case APPLY_OUTBOX:// 出库
                // setMemuItemClickListener(ApplyOutStockListActivity.class);
                // break;
                default:
                    break;
            }
        }
        // mAdapter.addBottom(mGridItems, true);
        // mAdapter.notifyDataSetChanged();
        mAdapter = getAdapter(mGridItems);
        gridView.setAdapter(mAdapter);
    }

    long maxUpdateTime = 0;

    /**
     * 启动默认下载，距离上次登录下载时间间隔为1小时则开始下载
     */
    public void downLoadData() {
        long nowDate = new Date().getTime(); // 当前时间
        SharedPreferencesHelper spfl = new SharedPreferencesHelper(mContext,
                "ZL.Phone.UserInfo");
        String last_login_time = spfl
                .getValue(PreferencesConfig.LAST_DOWN_USER_TIME);
        Date lastTime = ViewHelper.formatStrToDate(last_login_time); // 上次登录时间
        long distanceTime = nowDate - lastTime.getTime(); // 距离上次登录下载时间间隔
        LogUtils.i("menuNew", "last_login_time:" + last_login_time + "\n"
                + distanceTime);
        boolean users_is_null = false;
        try {
            List<User> list = ormDataHelper.getUserDao().queryBuilder()
                    .orderBy("lastUpdateDate", false).query();
            if (list.size() == 0) {
                users_is_null = true;
            } else {
                // 获取最大 最后更新时间
                maxUpdateTime = list.get(0).lastUpdateDate.getTime();
                LogUtils.i("lastUpdateDate", maxUpdateTime + "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 初次登录
        // 时间间隔大于5分钟,或者 上次登录时间为空
        // distanceTime > 1000 * 60 * 60 ||
        if (TextUtils.isEmpty(last_login_time) || users_is_null
                || distanceTime > 1000 * 60 * 1) {
            // 登录成功，保存当前日期
            spfl.putValue(PreferencesConfig.LAST_DOWN_USER_TIME,
                    ViewHelper.getDateString());
            ProgressDialogHelper.show(mContext, "更新用户信息...");
            // 启动线程下载User数据
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 下载部门员工信息
                    // zlServiceHelper.GetSys_User_Department(0, handlerRemind);
                    zlServiceHelper
                            .getAllUserList(maxUpdateTime, handlerRemind);
                    // 下载部门信息
                    zlServiceHelper.GetSys_Department(handlerRemind);
                    // 下载客户信息
                    // zlServiceHelper.getClientList(handlerRemind);
                    // 下载省市县字典
                    // zlServiceHelper.GetProvinceDicts(handlerRemind);
                }
            }).start();

            if (mIsAlarm) {
                AlarmTaskBiz.downloadAlarmTaskList(mContext);
            }
        }
    }

    public static final int GET_USERS_DATA_FAILED = 6;
    public static final int GET_USERS_DATA_SUCCESS = 7;
    public static final int GET_CLIENT_DATA_FAILED = 9;
    public static final int GET_CLIENT_DATA_SUCCESS = 10;
    public static final int GET_CLIENT_DATA_ISNULL = 11;

    public static final int GET_DEPARTMENT_SUCCEEDED = 14; // 获得部门信息成功
    // 处理下载逻辑
    private Handler handlerRemind = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int whatMsg = msg.what;
            switch (whatMsg) {
                case GET_USERS_DATA_FAILED:
                    Toast.makeText(mContext, "更新用户信息失败！", Toast.LENGTH_SHORT)
                            .show();
                    ProgressDialogHelper.dismiss();
                    break;
                case GET_USERS_DATA_SUCCESS:
                    ProgressDialogHelper.dismiss();
                    LogUtils.i("keno_json_hanler", "netData download  success");
                    List<User> list = (List<User>) msg.obj;

                    LogUtils.i("keno_json_hanler",
                            "netData download " + list.size());
                    try {
                        if (ormDataHelper == null) {
                            ormDataHelper = ORMDataHelper
                                    .getInstance(getApplicationContext());
                        }
                        Dao<User, Integer> userDao = ormDataHelper
                                .getDao(User.class);
                        if (list != null && list.size() > 0) {
                            // userDao.deleteBuilder().delete();
                            for (User user : list) {
                                List<User> deleteUsers = userDao.queryBuilder()
                                        .where().eq("Id", user.Id).query();
                                userDao.delete(deleteUsers);
                                userDao.create(user);
                            }
                        }
                    } catch (Exception e1) {
                        LogUtils.e(TAG, "用户列表更新失败：" + e1);
                    }
                    break;
                case GET_CLIENT_DATA_SUCCESS: // 获得客户列表成功
                    List<Client> listClient = (List<Client>) msg.obj;
                    try {
                        if (ormDataHelper == null) {
                            ormDataHelper = ORMDataHelper
                                    .getInstance(getApplicationContext());
                        }
                        Dao<Client, Integer> clientDao = ormDataHelper
                                .getDao(Client.class);
                        if (listClient != null && listClient.size() > 0) {
                            clientDao.deleteBuilder().delete();
                            for (Client client : listClient) {
                                clientDao.create(client);
                            }
                        }
                    } catch (Exception e1) {
                        LogUtils.e(TAG, "客户列表更新失败" + e1);
                    }
                    break;
                case GET_DEPARTMENT_SUCCEEDED:
                    List<部门> listDept = (List<部门>) msg.obj;
                    try {
                        Dao<部门, Integer> deptDao = ormDataHelper.getDao(部门.class);
                        deptDao.deleteBuilder().delete();
                        for (int i = 0; i < listDept.size(); i++) {
                            int num = deptDao.create(listDept.get(i));
                            LogUtils.i("insertDept", i + "------" + num);
                        }
                    } catch (Exception e1) {
                        LogUtils.e(TAG, "部门更新失败" + e1);
                    }
                    break;
                case GET_CLIENT_DATA_FAILED:
                    break;
                case GET_CLIENT_DATA_ISNULL:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    private long lastClickTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - lastClickTime > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                lastClickTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出波尔云
     */
    protected void dialog() {
        AlertDialog.Builder builder = new Builder(MenuNewActivity.this);
        builder.setMessage("确定要退出吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    /**
     * 弹出新建日志，通知，任务，快捷键
     */
    private void showAddPopwindow() {
        // 获得LayoutInflater的另外一种方式
        LayoutInflater inflater = (LayoutInflater) LayoutInflater
                .from(mContext);
        // 加载指定布局作为PopupWindow的显示内容
        View contentView = inflater.inflate(R.layout.pop_menu_add, null);
        int[] location = new int[2];
        ivAdd.getLocationOnScreen(location);
        int mScreenWidth = ((Activity) mContext).getWindowManager()
                .getDefaultDisplay().getWidth();
        int mScreenHeight = ((Activity) mContext).getWindowManager()
                .getDefaultDisplay().getHeight();
        Rect rect = new Rect();
        ((Activity) mContext).getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top; // 状态栏高度
        int realHeight = mScreenHeight - statusBarHeight;

        int popHeight = 110; // 弹出Popup的高度，默认只显示日志和任务
        if (!TextUtils.isEmpty(permissions)) {
            if (permissions.contains("389")) { // 有新建通知的权限
                popHeight += 55;
            }
            if (permissions.contains("447")) {// 有新建通讯录的权限
                popHeight += 55;
            }
        }
        // 初始化popupWindow,指定显示内容和宽高
        PopupWindow popupWindow = new PopupWindow(contentView,
                (int) ViewHelper.dip2px(mContext, 150),
                (int) ViewHelper.dip2px(mContext, popHeight));
        int locationX = mScreenWidth - (int) ViewHelper.dip2px(mContext, 150);
        // int locationY = realHeight - ivAdd.getHeight();
        int locationY = location[1] + ivAdd.getHeight();
        LogUtils.i(TAG, "y=" + locationY);
        findViewsFromPopUp(contentView, popupWindow);
        // setPopFoucus();
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(ivAdd, Gravity.NO_GRAVITY, locationX,
                locationY);
    }

    private void findViewsFromPopUp(View contentView,
                                    final PopupWindow popupWindow) {

        if (!TextUtils.isEmpty(permissions)) {
            if (permissions.contains(",72,")) { // 有新建客户的权限
                contentView.findViewById(R.id.ll_add_task_menu)
                        .setVisibility(View.VISIBLE);
                contentView.findViewById(R.id.ll_add_task_menu)
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LogUtils.i(TAG, "新建联系记录");
                                popupWindow.dismiss();
                                if (permissions.contains(",72,")) {
                                    Intent intent = new Intent(MenuNewActivity.this,
                                            ClientConstactNewActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(mContext, "没有权限,请联系管理员！",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }

        if (!TextUtils.isEmpty(permissions)) {
            if (permissions.contains(",840,")) { // 有新建客户的权限
                contentView.findViewById(R.id.ll_add_log_menu)
                        .setVisibility(View.VISIBLE);
                contentView.findViewById(R.id.ll_add_log_menu)
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LogUtils.i(TAG, "新建客户");
                                popupWindow.dismiss();
                                if (permissions.contains(",840,")) {
                                    // 有新建客户的权限
                                    Intent intent = new Intent(mContext,
                                            ChClientInfoActivity.class);
                                    intent.putExtra("isNewClient", true);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(mContext, "没有权限,请联系管理员！",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }

        if (!TextUtils.isEmpty(permissions)) {
            if (permissions.contains("389")) { // 有新建通知的权限
                contentView.findViewById(R.id.ll_add_notice_menu)
                        .setVisibility(View.VISIBLE);
                contentView.findViewById(R.id.ll_add_notice_menu)
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LogUtils.i(TAG, "新建通知");
                                popupWindow.dismiss();
                                if (permissions.contains("389")) {
                                    // 具备新建通知权限
                                    Intent intent = new Intent(mContext,
                                            NoticeNewActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(mContext, "没有权限,请联系管理员！",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            if (permissions.contains("447")) {// 有新建通讯录的权限
                contentView.findViewById(R.id.ll_add_contact_menu)
                        .setVisibility(View.VISIBLE);
                contentView.findViewById(R.id.ll_add_contact_menu)
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LogUtils.i(TAG, "新建联系人");
                                popupWindow.dismiss();
                                Intent intent = new Intent(mContext,
                                        CommunicationAddActivity.class);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    /**
     * 监听网络状态
     */
    private class NetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(
                    ConnectivityManager.CONNECTIVITY_ACTION)) {
                // 获得系统网络连接管理服务
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                // 获得网络连接信息
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable()
                        && networkInfo.isConnected()) {
                    rl_warn_net.setVisibility(View.GONE);
                } else {
                    rl_warn_net.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private CommanAdapter<MenuChildItem> getAdapter(
            List<MenuChildItem> gridItems) {
        return new CommanAdapter<MenuChildItem>(gridItems, mContext,
                R.layout.item_grid_menu) {
            @Override
            public void convert(int position, MenuChildItem item,
                                BoeryunViewHolder viewHolder) {
                if (item.ponit == EnumFunctionPoint.XUESONG_NULL) {
                    // 显示分割线
                    viewHolder.getView(R.id.rl_root_grid_item_rl)
                            .setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.getView(R.id.rl_root_grid_item_rl)
                            .setVisibility(View.VISIBLE);
                    viewHolder
                            .setTextValue(R.id.tv_title_grid_item, item.title);
                    viewHolder.setImageResoure(R.id.iv_ico_grid_item, item.ico);
                    TextView tvCount = viewHolder
                            .getView(R.id.tv_count_grid_item);
                    if (item.count != 0) {
                        tvCount.setVisibility(View.VISIBLE);
                        tvCount.setText("" + item.count);
                    } else {
                        tvCount.setVisibility(View.GONE);
                        tvCount.setText("" + item.count);
                    }
                }

            }
        };
    }

    /**
     * 根据显示
     */
    private List<EnumFunctionPoint> getShowDefaultChanghuiPoints() {
        List<EnumFunctionPoint> functions = new ArrayList<EnumFunctionPoint>();
        // /* 长汇功能点 */
        // functions.add(EnumFunctionPoint.CHANGHUI_WORK_PLAN); // 工作计划

//        functions.add(EnumFunctionPoint.CONTACTS);
//        // functions.add(EnumFunctionPoint.ADVERTISEMENT); // 产品列表
//        functions.add(EnumFunctionPoint.CHANGHUI_PRODUCT_LIST); // 产品列表
//        functions.add(EnumFunctionPoint.CHANGHUI_CONTACT_LIST); // 合同列表
//        functions.add(EnumFunctionPoint.CHANGHUI_BESPOKE_LIST); // 预约列表
//        functions.add(EnumFunctionPoint.CHANGHUI_CLIENT_LIST); // 客户列表
//        functions.add(EnumFunctionPoint.ATTANCE);
//        functions.add(EnumFunctionPoint.COMPANY_SPACE);
//        functions.add(EnumFunctionPoint.PROFIT_CALCULATOR);
        return functions;
    }

    /**
     * 根据显示
     */
    private List<EnumFunctionPoint> getShowPointsByPermission() {
        List<EnumFunctionPoint> functions = new ArrayList<EnumFunctionPoint>();

        // int len = 0;
        if (permissions.contains(",47,")) {
            // 通讯录
            // len = functions.size() - 1;
            // functions.add(EnumFunctionPoint.COMMUNICATION);
        }

        if (permissions.contains(",741,")) {
            // 考勤登记
            // len = functions.size() - 1;
            // functions.add(EnumFunctionPoint.ATTANCE);
        }

        if (permissions.contains(",742,")) {
            // 更多功能
            // len = functions.size() - 1;
            functions.add(EnumFunctionPoint.ADVERTISEMENT);
        }

        if (permissions.contains(",14,")) {
            // len = functions.size() - 1;
            // functions.add(EnumFunctionPoint.CLIENT); // 客户模块

            // mFunctions.add(EnumFunctionPoint.RAD_PRODUCTLIST);
            // mFunctions.add(EnumFunctionPoint.RAD_SCAN_CODE);
            // mFunctions.add(EnumFunctionPoint.RAD_REPORT);
            // mFunctions.add(EnumFunctionPoint.RAD_ORDER);

            // // TODO 森拉特订单审批,根据权限添加
            // mFunctions.add(EnumFunctionPoint.SLT_APPROVE_ORDER);
            // mFunctions.add(EnumFunctionPoint.RAD_CACULATE);
            // mFunctions.add(EnumFunctionPoint.SLT_SHOPCAR_LIST);
            // mFunctions.add(EnumFunctionPoint.SLT_SALE_TARGET);

        }

        if (permissions.contains(",96,")) {
            // 销售机会模块
            // len = functions.size() - 1;
            // functions.add(EnumFunctionPoint.SALECHANCE);

            // // 销售漏斗
            // mFunctions.add(EnumFunctionPoint.SALESUMARY);
            // mFunctions.add(EnumFunctionPoint.RANKING);
            // mFunctions.add(EnumFunctionPoint.CLEW); // 线索
            // mFunctions.add(EnumFunctionPoint.CONPACT); // 合同
            // mFunctions.add(EnumFunctionPoint.RECEIPET); // 收款单
            // mFunctions.add(EnumFunctionPoint.EXPENSE); // 报销
        }

        if (permissions.contains("72")) {
            // 联系记录
            // len = functions.size() - 1;
            // functions.add(EnumFunctionPoint.CONTACTS);
        }

        if (permissions.contains("336")) {
            // 项目列表
            // len = functions.size() - 1;
//            functions.add(EnumFunctionPoint.PROJECT);
        }

        if (permissions.contains("572")) {
            // 产品管理
            // len = functions.size() - 1;
//            functions.add(EnumFunctionPoint.PRODUCT);
        }

        if (permissions.contains("574")) {
            // 出库列表
            // len = functions.size() - 1;
//            functions.add(EnumFunctionPoint.APPLY_OUTBOX);
        }

        if (permissions.contains("575")) {
            // 入库列表
            // len = functions.size() - 1;
//            functions.add(EnumFunctionPoint.APPLY_INBOX);
        }

        if (permissions.contains("180".concat(","))) {
            // 橱柜订单
            // len = functions.size() - 1;
//            functions.add(EnumFunctionPoint.ORDER);
        }
        if (permissions.contains("279")) {
            // 问题反馈
            // len = showList.size() - 1;
            // showList.add(EnumFunction.FEEDBACK.getValue());
        }
        if (permissions.contains("224")) {
            // 客户投诉建议
            // len = showList.size() - 1;
            // showList.add(EnumFunction.SUGGEST.getValue());
        }

        // if (permissions.contains("652")) {
        // // 客户经理经理日报
        // len = functions.size() - 1;
        // functions.add(EnumFunctionPoint.CHANGHUI_WORK_PLAN);
        // }
        //
        // if (permissions.contains("653")) {
        // // 部门经理日报
        // // len = showList.size() - 1;
        // // showList.add(EnumFunction.SUGGEST.getValue());
        // }
        if (permissions.contains(",624,")) {
            // 长汇产品
            functions.add(EnumFunctionPoint.CHANGHUI_PRODUCT_LIST);
        }

        if (permissions.contains(",58,")) {
            // 通知
            functions.add(EnumFunctionPoint.NOTICE);
        }

        if (permissions.contains(",793,") || permissions.contains(",625,")) {
            // 合同
            functions.add(EnumFunctionPoint.CHANGHUI_CONTACT_LIST);
        }

        if (permissions.contains(",790,") || permissions.contains(",626,")) {
            // 预约
            functions.add(EnumFunctionPoint.CHANGHUI_BESPOKE_LIST);
        }
        if (permissions.contains(",72,")) {
            // 联系记录
            functions.add(EnumFunctionPoint.CONTACTS);
        }
        if (permissions.contains(",630,")) {
            // 收益计算器
            functions.add(EnumFunctionPoint.PROFIT_CALCULATOR);
        }
        if (permissions.contains(",68,")) {
            // 公司空间
            functions.add(EnumFunctionPoint.COMPANY_SPACE);
        }
        if (permissions.contains(",14,") || permissions.contains(",807,")) {
            // 客户
            functions.add(EnumFunctionPoint.CHANGHUI_CLIENT_LIST);
        }


        return functions;
    }

    private void loadPermissionPoint(List<EnumFunctionPoint> functionPoints) {
        if (functionPoints != null && functionPoints.size() > 0) {
            mFunctions.addAll(mFunctions.size(), functionPoints);
            mGridItems = mFunctionBiz.getFunctions(mFunctions);

            if (mGridItems.size() % 4 != 0) {
                int nullCount = 4 - mGridItems.size() % 4;
                for (int i = 0; i < nullCount; i++) {
                    // 补充空白块
                    EnumFunctionPoint nullPoint = EnumFunctionPoint.XUESONG_NULL;
                    MenuChildItem item = new MenuChildItem(R.drawable.logo, "",
                            nullPoint);
                    mGridItems.add(item);
                }
            }

            mAdapter.addBottom(mGridItems, true);
        }

    }

    /***
     * 检查版本更新
     */
    private void updateVersion() {
        PgyUpdateManager.register(MenuNewActivity.this,
                new UpdateManagerListener() {
                    @Override
                    public void onUpdateAvailable(final String result) {
                        final AppBean appBean = getAppBeanFromString(result);

                        final BoeryunDialog updateDialog = new BoeryunDialog(
                                MenuNewActivity.this, true, "更新", "检测到新版本"
                                + appBean.getVersionName() + "，请及时更新",
                                "取消", "确定");
                        updateDialog.setBoeryunDialogClickListener(
                                new BoeryunDialog.OnBoeryunDialogClickListner() {
                                    @Override
                                    public void onClick() {
                                        updateDialog.dismiss();
                                        cancleUpdate = true;
                                    }
                                }, new BoeryunDialog.OnBoeryunDialogClickListner() {
                                    @Override
                                    public void onClick() {
                                        startDownloadTask(MenuNewActivity.this,
                                                appBean.getDownloadURL());
                                        updateDialog.dismiss();
                                    }
                                });
                        updateDialog.show();
                    }

                    @Override
                    public void onNoUpdateAvailable() {
                        cancleUpdate = false;
//                        Toast.makeText(MenuNewActivity.this, "已经是最新版本",
//                                Toast.LENGTH_SHORT).show();
                    }
                });
//        try {
//            // 版本更新
//            new Thread(checkVersionHelper.new CheckVersionTask()).start();
//        } catch (Exception e) {
//            Toast.makeText(mContext, "更新版本异常", Toast.LENGTH_SHORT).show();
//        }
    }

}
