package com.cedarhd;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.cedarhd.helpers.CrashHandler;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.utils.ByteUtil;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MemoryUtils;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.service.XGPushService;

/**
 * 首页 导航
 *
 * @author KJX
 */
public class TabMainActivity extends ActivityGroup implements
        OnTabChangeListener {
    public static TabMainActivity tabMainActivity;
    private String TAG = "TabMainActivity";
    private int page = 1;
    private TabHost mTabHost;
    private Context context;
    private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CrashHandler().init(getApplicationContext());
        ExistApplication.getInstance().addActivity(this);
        MemoryUtils.startCaughtAllException();
        tabMainActivity = this;
        context = this;
        Global.isStartMenu = true;
        setContentView(R.layout.main_new);
        setupTabHost();
        initPushNotification();
        mTabHost.setOnTabChangedListener(this);

        Intent newsIntent = new Intent(this, CompanySpaceListActivity.class);
        // menuIntent = new Intent(this, MenuNewActivity.class);
        // 动态页面
        Intent dynamicIntent = new Intent(this, DynamicNewsActivity.class);
        // Intent newsIntent = new Intent(this, SpaceFragmentActivity.class);
        Intent menuIntent = new Intent(this, MenuNewActivity.class);
        Intent settingsIntent = new Intent(this, SettingActivity.class);
        setupTab(dynamicIntent, "动态", R.drawable.chat_button_selector);
        setupTab(menuIntent, "导航", R.drawable.menu_button_selector);
        setupTab(newsIntent, "分享", R.drawable.selector_share_menu);
        setupTab(settingsIntent, "设置", R.drawable.setting_button_selector);
        // 默认显示第一页 导航
        page = getIntent().getIntExtra("currentTab", 1);
        LogUtils.i("page", "page:" + page);
        mTabHost.setCurrentTab(page); // 设置默认选中

    }

    private void changeBottomTitle() {
        // 得到当前选中选项卡的索引
        int currentIndex = mTabHost.getCurrentTab();
        // 调用tabhost中的getTabWidget()方法得到TabWidget
        TabWidget tabWidget = mTabHost.getTabWidget();
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            View view = tabWidget.getChildAt(i);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_title_tab);
            if (i == currentIndex) {
                tvTitle.setTextColor(getResources().getColor(
                        R.color.color_nav_title_pressed));
            } else {
                tvTitle.setTextColor(getResources().getColor(
                        R.color.color_nav_title_normal));
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        page = intent.getIntExtra("currentTab", 1);
        LogUtils.i("page", "onNewIntent:" + page);
        mTabHost.setCurrentTab(page); // 设置默认选中
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO 这里也会出异常
        String imei = ((TelephonyManager) context
                .getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        String token = ByteUtil.md5One(imei);
        LogUtils.i(TAG, imei + "\n" + token);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Global.isStartMenu = false;
    }

    private void setupTabHost() {
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this.getLocalActivityManager());

        // 设置TabHost切换
        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                LogUtils.i(TAG, "---->" + tabId);
            }
        });
    }

    private void setupTab(Intent intent, final String tag, int iconId) {
        View tabview = createTabView(mTabHost.getContext(), iconId, tag);
        TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview)
                .setContent(intent);
        mTabHost.addTab(setContent);
    }

    /**
     * 创建Tab导航按钮视图
     *
     * @param context
     * @param iconId
     * @param title
     * @return
     */
    private static View createTabView(final Context context, int iconId,
                                      final String title) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.tab_item_view, null);
        ImageView iv = (ImageView) view.findViewById(R.id.imageview1);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title_tab);
        iv.setImageResource(iconId);
        tvTitle.setText(title);
        return view;
    }

    /**
     * 初始化集成信鸽推送
     */
    private void initPushNotification() {
        // 开启logcat输出，方便debug，发布时请关闭
         XGPushConfig.enableDebug(this, true);
        // 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(),
        // XGIOperateCallback)带callback版本
        // 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
        // 具体可参考详细的开发指南
        // 传递的参数为ApplicationContext
        Context context = getApplicationContext();
        XGPushManager.registerPush(context, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                LogUtils.i(TAG, "推送注册成功");
            }

            @Override
            public void onFail(Object o, int i, String s) {
                LogUtils.i(TAG, "推送注册失败");
            }
        });
        // 2.36（不包括）之前的版本需要调用以下2行代码
        Intent service = new Intent(context, XGPushService.class);
        context.startService(service);

        // 获取设备唯一通行证
        final String token = XGPushConfig.getToken(context);
        LogUtils.i(TAG, "----------->\n" + token);
        new Thread(new Runnable() {
            @Override
            public void run() {
                zlServiceHelper.SetMobileDeviceToken(token, Build.MODEL);
            }
        }).start();
    }

    @Override
    public void onTabChanged(String tabId) {
        changeBottomTitle();
    }

}