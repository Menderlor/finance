package com.cedarhd;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.constants.PreferencesConfig;
import com.cedarhd.control.RoundImageView;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.SharedPreferencesHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.UpdataBean;
import com.cedarhd.models.钻石积分;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 波尔云系统设置
 *
 * @author
 */
public class SettingActivity extends BaseActivity {

    private Context mContext;
    private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();

    // private LinearLayout llAvartar; // 用户信息和设置头像
    private LinearLayout llGiveSuggestion; // 检测新版本
    // private LinearLayout llNotice; // 通知提醒设置
    private LinearLayout llInvite; // 邀请员工下载
    private LinearLayout llSetPwd;// 修改密码
    /**
     * 通知设置
     */
    private LinearLayout llTixing;
    // private LinearLayout llUserInfo; // 用户信息
    private LinearLayout llShare; // 分享模块
    private LinearLayout llDiamond;// 钻石排行榜
    private TextView tvVersionTitle;
    // private TextView tvNotice;
    private TextView tvCheckVersion;
    // private TextView tvUserInfo;
    private Button btnQuit;
    private TextView tvVersion;

    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;

    /* 用来标识请求gallery的activity */
    private static final int PICKED_PHOTO_WITH_DATA = 3021;

    private String avatarPath = FilePathConfig.getAvatarDirPath();
    ZLServiceHelper mDataHelper = new ZLServiceHelper();
    private DictionaryHelper dictionaryHelper;
    private static HandlerSetting handler;
    private String version;
    private String mPermission;

    RoundImageView mImageViewCamera;
    ProgressBar mProgressBar;
    private TextView company;
    private TextView user;

    private TextView dinamol_num;// 钻石数量
    private TextView dinamol_rank;// 钻石排名

    public class HandlerSetting extends Handler {
        public static final int UPLOAD_SUCCESS = 1;
        public static final int UPLOAD_FAILURE = 2;
        public static final int SHOW_IMAGE_SUCCESS = 3;
        public static final int SHOW_IMAGE_FAILUREE = 4;
        public static final int ERROR_GET_UPDATEINFO = 5; // 获取新版本
        public static final int NOT_SHOW_UPDATE_DIALOG = 6; // 已是最新版本
        public static final int SHOW_UPDATE_DIALOG = 7; // 显示更新对话框

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what == UPLOAD_SUCCESS) {
                Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
            } else if (what == UPLOAD_FAILURE) {
                Toast.makeText(mContext, "上传头像失败", Toast.LENGTH_SHORT).show();
            } else if (what == SHOW_IMAGE_SUCCESS) {
                byte[] data = (byte[]) msg.obj;
                Bitmap avatar = BitmapFactory.decodeByteArray(data, 0,
                        data.length);
                LogUtils.i("leo2", "----->" + data.length);
                mImageViewCamera.setImageBitmap(avatar);
            } else if (what == SHOW_IMAGE_FAILUREE) { // 显示头像失败，默认显示本地
                mImageViewCamera.setImageResource(R.drawable.camera);
            } else if (what == ERROR_GET_UPDATEINFO) {
                //
            } else if (what == NOT_SHOW_UPDATE_DIALOG) {
                //
            } else if (what == SHOW_UPDATE_DIALOG) {
                // 显示更新对话框
                showUpdateDialog();
            } else if (what == SUCCESS) {
                diamondl_list = getStatiesDiamond(result);
                for (int i = 0; i < diamondl_list.size(); i++) {
                    if (Global.mUser.UserName
                            .equals(diamondl_list.get(i).接收人姓名)) {
                        dinamol_num
                                .setText(String.valueOf(diamondl_list.get(i).钻石数量));
                        dinamol_rank.setText("排名:  " + String.valueOf(i + 1));
                        return;
                    } else {
                        dinamol_num.setText(String.valueOf(0));
                        dinamol_rank.setText("排名:  " + "暂无");
                    }
                }
            } else if (what == NO) {
                Toast.makeText(mContext, "服务器异常", Toast.LENGTH_SHORT).show();
            } else if (what == END_INITENT_ERROR) {
                Toast.makeText(mContext, "网络错误", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initData();
        findViews();
        findViews_photo();
        setOnClickListener();
        setOnListener();

        initPermissionPoint();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPhoto();
    }

    private void initData() {
        mContext = SettingActivity.this;
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(
                mContext, PreferencesConfig.APP_USER_INFO);
        mPermission = sharedPreferencesHelper
                .getValue(PreferencesConfig.POINT_PERMISSION);
        LogUtils.i(TAG, mPermission);
    }

    public void setOnClickListener() {
        mImageViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,
                        SettingAvatarActivity.class);
                startActivity(intent);
            }
        });

        /** 修改密码 */
        llSetPwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,
                        SetPasswordActivity.class));
            }
        });
    }

    public void findViews_photo() {
        new File(avatarPath).mkdirs();

        mImageViewCamera = (RoundImageView) findViewById(R.id.imageViewCamera1);
        handler = new HandlerSetting();
        version = ViewHelper.getVersionName(mContext); // 获得应用程序版本号
        dictionaryHelper = new DictionaryHelper(getApplicationContext());

        company = (TextView) findViewById(R.id.tv_userinfo_setting_company);
        user = (TextView) findViewById(R.id.tv_useinfo_setting_user);
        company.setText(Global.mUser.UserName);
        user.setText(Global.mUser.CorpName);

        initPhoto();
    }

    private void initPhoto() {
        final String avataUrl = dictionaryHelper.getUserPhoto(Global.mUser.Id); // 头像地址
        LogUtils.i("avataUrl", avataUrl);
        // 如果本地数据库中存有用户头像信息
        if (!TextUtils.isEmpty(avataUrl) && avataUrl.contains("\\")) {
            ImageLoader.getInstance().displayImage(Global.BASE_URL + avataUrl,
                    mImageViewCamera, Global.getPassport());
        }
    }

    /***
     * 初始化功能点，根据权限显示对应功能点
     */
    private void initPermissionPoint() {
        if (!TextUtils.isEmpty(mPermission)) {
            // mPermission += ",736,737,738,739,740,"; // Test
            if (mPermission.contains(",737,")) {
                // 提醒设置
                // llTixing.setVisibility(View.VISIBLE);
            }
            llTixing.setVisibility(View.GONE);
            // 邀请下载（738），钻石排行榜（736），问题反馈（739），提醒设置（737），好用就分享下（740）
            if (mPermission.contains(",738,")) {
                // llInvite.setVisibility(View.VISIBLE);
            }
            if (mPermission.contains(",736,")) {
                // llDiamond.setVisibility(View.VISIBLE);
            }
            if (mPermission.contains(",740,")) {
                llShare.setVisibility(View.VISIBLE);
            }
            if (mPermission.contains(",739,")) {
                llGiveSuggestion.setVisibility(View.VISIBLE);
            }
        }
    }

    private UpdataBean updateInfo;
    private ProgressDialog mPd;

    /**
     * 显示升级更新的提示对话框
     */
    private void showUpdateDialog() {
        /**
         * 1 创建Builder
         *
         * 2 给builder 设置属性 :标题 提示信息 图标 按钮
         *
         * 3 创建Dialog
         *
         * 4 显示dialog
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请升级");
        builder.setMessage(updateInfo.getDesc());
        builder.setCancelable(false);// 屏蔽后退按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mPd = new ProgressDialog(mContext);
                mPd.setMessage("正在下载最新的apk");
                mPd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mPd.show();
                // new Thread(new DownloadApkTask()).start();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // loginMainUI();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_setting, menu);
        return true;
    }

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
     * 退出波尔云,关闭应用程序,点击确定将不再收到新通知
     */
    protected void closeApplication() {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setIcon(R.drawable.quit);
        builder.setMessage("退出后将不在收到新消息提醒");
        builder.setTitle("是否退出");
        builder.setPositiveButton("确认",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 清空设备编号
                                zlServiceHelper
                                        .clearMobileDeviceTokenV710(mContext);
                            }
                        }).start();
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

    private void findViews() {
        llGiveSuggestion = (LinearLayout) findViewById(R.id.ll_check_ad);
        llInvite = (LinearLayout) findViewById(R.id.ll_invite_download);
        llSetPwd = (LinearLayout) findViewById(R.id.ll_set_pwd);
        llShare = (LinearLayout) findViewById(R.id.ll_share_setting);
        llDiamond = (LinearLayout) findViewById(R.id.diamond_list);
        dinamol_num = (TextView) findViewById(R.id.dinamol_zmy_num);
        dinamol_rank = (TextView) findViewById(R.id.zmy_setting_rank);
        llTixing = (LinearLayout) findViewById(R.id.tixing_set);
        getDiamondl();
        // llUserInfo = (LinearLayout) findViewById(R.id.ll_userinfo_setting);
        tvVersionTitle = (TextView) findViewById(R.id.tv_avatar_setting);
        // tvNotice = (TextView) findViewById(R.id.tv_notice_setting);
        tvVersion = (TextView) findViewById(R.id.tv_version_setting);
        // tvUserInfo = (TextView) findViewById(R.id.tv_userinfo_setting);
        tvCheckVersion = (TextView) findViewById(R.id.tv_check_ad);
        btnQuit = (Button) findViewById(R.id.btn_quit_setting);
        // company = (TextView) findViewById(R.id.tv_userinfo_setting_company);
        // user = (TextView) findViewById(R.id.tv_useinfo_setting_user);
        // user.setText(Global.mUser.UserName);
        // company.setText(Global.mUser.CorpName);
        tvVersion.setText(ViewHelper.getVersionName(this));
    }

    private void setOnListener() {

//        tvVersionTitle.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showShortToast("检测新版本");
//                // UmengUpdateAgent.forceUpdate(mContext);
//
//                PgyUpdateManager.register(SettingActivity.this);
//            }
//        });

//        tvVersion.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showShortToast("检测新版本");
//                // UmengUpdateAgent.forceUpdate(mContext);
//                PgyUpdateManager.register(SettingActivity.this);
//            }
//        });

        llDiamond.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DiamondListActivity.class);
                startActivity(intent);
            }
        });
        llTixing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RemindActivity.class);
                startActivity(intent);
            }
        });

        llGiveSuggestion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 给波尔云提意见
                Intent intent = new Intent(mContext,
                        GiveSuggestionActivity.class);
                startActivity(intent);
            }
        });

        // 邀请下载
        llInvite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, InvitationActivity.class);
                startActivity(intent);
            }
        });

        llShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                shareApplication();
                // DictionaryQueryDialogHelper.getInstance(mContext)
                // .showDialog(tvShare, "产品型号");
            }
        });

        btnQuit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesHelper spfl = new SharedPreferencesHelper(
                        mContext, "ZL.Phone.UserInfo");
                spfl.putValue("isExist", "true");// 标识是否退出
                // Intent intent = new Intent(mContext,
                // LoginActivity.class);
                // startActivity(intent);
                // finish();
                closeApplication();

            }
        });
    }

    private void shareApplication() {
        String strInfo = "推荐您使用一款移动办公软件波尔云来进行手机办公管理。下载后即可使用手机快速办公: http://a.app.qq.com/o/simple.jsp?pkgname=com.cedarhd";
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);
        share_intent.setType("text/plain");
        share_intent.putExtra(Intent.EXTRA_SUBJECT, "f分享");
        share_intent.putExtra(Intent.EXTRA_TEXT, strInfo);
        share_intent = Intent.createChooser(share_intent, "分享");
        startActivity(share_intent);
    }

    private HttpUtils httpUtils;
    private String url;
    /**
     * 用于接收返回的数据
     */
    private String result;
    /**
     * 当获取成功时发送的值
     */
    public static final int SUCCESS = 100;
    /** 当获取失败时发送的值 */
    /**
     * 网络错误时发送
     */
    public static final int END_INITENT_ERROR = 102;
    public static final int NO = 101;
    private List<钻石积分> diamondl_list;

    /**
     * 开启线程来获取钻石排名
     */
    private void getDiamondl() {
        httpUtils = new HttpUtils();
        // url = Global.BASE_URL + Global.EXTENSION
        // + "Diamond/GetDiamondCountList" + "/" + "接收人"+ "/"+true+ "/"+0+
        // "/"+0+ "/"+"接收人="+Global.mUser.Id;
        url = Global.BASE_URL + Global.EXTENSION
                + "Diamond/GetDiamondScoreList" + "/" + "0" + "/" + "1000";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    result = httpUtils.httpGet(url);
                    LogUtils.i("out", url + ":result" + result);
                    if (result.equals("网络错误")) {
                        handler.sendEmptyMessage(END_INITENT_ERROR);
                    } else {
                        if (getStatus(result) == 1) {
                            handler.sendEmptyMessage(SUCCESS);
                        } else {
                            handler.sendEmptyMessage(NO);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取钻石数量统计的集合
     *
     * @throws JSONException
     */
    private List<钻石积分> getStatiesDiamond(String str) {
        List<钻石积分> list = new ArrayList<钻石积分>();
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONArray array;
            array = jsonObject.getJSONArray("Data");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                钻石积分 diamondl = new 钻石积分();
                diamondl.接收人 = object.getInt("接收人");
                diamondl.接收人姓名 = object.getString("接收人姓名");
                diamondl.赞总计 = object.getInt("赞总计");
                diamondl.赞数量 = object.getInt("赞数量");
                diamondl.钻石数量 = object.getInt("钻石数量");
                list.add(diamondl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取状态信息
     */
    private int getStatus(String str) throws JSONException {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(str);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonObject.getInt("Status");
    }

}
