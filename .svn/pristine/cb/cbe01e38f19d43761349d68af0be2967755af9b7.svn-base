package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.SharedPreferencesHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.User;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MessageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.Date;

/***
 * 展示logo 导航页
 *
 * @author K
 */
public class NavActivity extends BaseActivity {

    private Context mContext;
    private ZLServiceHelper mZlServiceHelper;
    private ImageView ivMain;

    private final int SUCCEED_LOGIN = 1;
    private final int FAILURE_LOGIN = 2;
    // 定义Handler对象
    private Handler handler = new Handler() {
        @Override
        // 当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCEED_LOGIN:
                    Intent intent = new Intent();
                    intent.setClass(mContext, TabMainActivity.class);
                    startActivity(intent);
                    LogUtils.i("loginsuccess", "登录成功，完成后");
                    ProgressDialogHelper.dismiss();
                    finish();
                    break;
                case FAILURE_LOGIN:
                    SharedPreferencesHelper spfl = new SharedPreferencesHelper(
                            mContext, "ZL.Phone.UserInfo");
                    spfl.putValue("isExist", "true");
                    String result = (String) msg.obj;
                    LogUtils.i("loginFailure", "登录失败：" + result);
                    Intent intent2 = new Intent();
                    intent2.setClass(mContext, LoginActivity.class);
                    startActivity(intent2);
                    ProgressDialogHelper.dismiss();
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        init();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLoginedUser();
    }

    private void init() {
        mContext = NavActivity.this;
        mZlServiceHelper = new ZLServiceHelper();
    }

    private void initViews() {
        ivMain = (ImageView) findViewById(R.id.iv_main_nav);
        // loadBanner(ivMain);
    }

    /**
     * 直接登录
     *
     * @return
     */
    private void startLoginedUser() {
        SharedPreferencesHelper spfl = new SharedPreferencesHelper(mContext,
                "ZL.Phone.UserInfo");
        String al = spfl.getValue("Al");
        String existFlag = spfl.getValue("isExist");
        String corpName = spfl.getValue("corpName");
        // String corpId = spfl.getValue("corpId");
        String userNameLogin = spfl.getValue("userName");
        String userId = spfl.getValue("userId");
        String pwd = spfl.getValue("pwd");
        String passport = spfl.getValue("passport");
        String admin = spfl.getValue("admin");
        int dptId = spfl.getIntValue("dptId");
        int position = spfl.getIntValue("Position");
        // LogUtils.i("deptId", corpId + "----" + dptId);
        LogUtils.i("keno5", "Al=" + al + "--" + userId + "---" + userNameLogin
                + ",depId---" + dptId + ",existFlag=" + existFlag);
        if (!"false".equals(existFlag)) {
            startLoginActivity();
        } else if (!(userId == null || corpName == null
                || userNameLogin == null || pwd == null || passport == null || existFlag == null)
                && al.equals("1")) {
            Global.mUser = new User();
            Global.mUser.CorpId = dptId;
            Global.mUser.CorpName = corpName;
            Global.mUser._id = Integer.parseInt(userId);
            Global.mUser.Id = userId;
            Global.mUser.UserName = userNameLogin;
            Global.mUser.PassWord = pwd;
            Global.mUser.Department = dptId;
            Global.mUser.Passport = passport;
            Global.mUser.Admin = admin;
            Global.mUser.Position = position;
            /** 判断是否网络登录 */
            if (!HttpUtils.IsHaveInternet(mContext)) {
                MessageUtil.AlertMessage(mContext, "登录失败", "请检查网络连接！");
                startLoginActivity();
            } else { // 联网登录
                ProgressDialogHelper.show(mContext, "登录中");
                directLogin(userNameLogin, pwd, corpName);
            }
        } else {
            startLoginActivity();
        }
    }

    /**
     * 直接跳转等登录页面
     */
    private void startLoginActivity() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                    handler.sendEmptyMessage(FAILURE_LOGIN);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 直接登录
     *
     * @param uname    用户名
     * @param pwd      密码
     * @param corpName 单位名
     */
    private void directLogin(final String uname, final String pwd,
                             final String corpName) {
        LogUtils.i("keno4directLogin", uname + "-->" + pwd + "-->" + corpName);
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(300);
                    boolean isSucced = mZlServiceHelper.login(uname, pwd,
                            corpName);
                    if (isSucced) {
                        handler.sendEmptyMessage(SUCCEED_LOGIN);
                    } else {
                        handler.sendEmptyMessage(FAILURE_LOGIN);
                    }
                    // 登录完成后,记住用户信息
                } catch (Exception ex) {
                    handler.sendEmptyMessage(FAILURE_LOGIN);
                }
            }
        }.start();
    }

    private void loadBanner(ImageView iv_main) {
        String bannerUrl = "http://crmhw.changhw.com:8018/login.png";
        File bannerFile = ImageLoader.getInstance().getDiskCache()
                .get(bannerUrl);
        if (bannerFile != null) {
            try {
                long disctance = new Date().getTime()
                        - bannerFile.lastModified();
                LogUtils.i("createtime", "disctance=" + disctance);
                if (disctance > 1000 * 60 * 1) {// 时间间隔
                    // bannerFile.delete();
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
}
