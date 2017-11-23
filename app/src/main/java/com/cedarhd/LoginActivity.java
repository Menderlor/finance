package com.cedarhd;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.InputSoftHelper;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.SharedPreferencesHelper;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.User;
import com.cedarhd.receiver.LoginCheckReceiver;
import com.cedarhd.utils.ByteUtil;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MemoryUtils;
import com.cedarhd.utils.MessageUtil;
import com.cedarhd.widget.BoeryunDialog;
import com.cedarhd.widget.BoeryunDialog.OnBoeryunDialogClickListner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static com.cedarhd.R.id.editTextUserName;

/**
 * 登录页面
 */
public class LoginActivity extends BaseActivity {
    ZLServiceHelper mDataHelper = new ZLServiceHelper();

    /**
     * 请求注册
     */
    private final int REQUEST_CODE_REGISTER = 201;

    /**
     * 注册完成后 初次登录成功
     */
    private final int SUCCESS_LOGING_FIRST = 211;

    /**
     * 注册完成后初次登录成功
     */
    private final int FAILURE_LOGING_FIRST = 212;

    private Button btnLogin;
    private EditText etCorpName;
    private EditText etUserName;
    private EditText etPassWord;
    private Context context;
    String mUname;
    String mPassword;
    String mCorpName;

    private String oldPwd;// 原始密码,保存密码
    private Boolean isNewPwd = false;// 是否手工输入过密码
    private String oldUserName;// 原始用户名
    private Boolean isNewUserName = false;// 是否手工输入过用户名
    private int pwdLen = 0; // 密码长度
    private String showPwd;// 密码框中显示的伪密码

    private TextView findpassword; // 找回密码
    private LoginCheckReceiver receiver;// 校验登录密码的广播接收器
    private TextView register;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MemoryUtils.startCaughtAllException();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 设置不待机
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        registerBroadCaster();
        findViews();
        init();
        setOnClickListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            // 页面销毁时，取消注册
            unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_REGISTER:
                    if (data != null) {
                        final User user = (User) data.getExtras().getSerializable(
                                RegisterActivity.TAG);
                        if (user != null) {
                            final BoeryunDialog dialog = new BoeryunDialog(context,
                                    true, "邀请员工", "注册成功，是否立即邀请员工", "取消", "确定");
                            dialog.setBoeryunDialogClickListener(
                                    new OnBoeryunDialogClickListner() {
                                        @Override
                                        public void onClick() {
                                            setUserInfo(user);
                                            // 取消
                                            dialog.dismiss();
                                        }
                                    }, new OnBoeryunDialogClickListner() {
                                        @Override
                                        public void onClick() {
                                            setUserInfo(user);
                                            if (isNewPwd) { // 如果新输入的密码
                                                user.PassWord = ByteUtil
                                                        .md5One(user.PassWord);
                                            }
                                            startFirstLogin(user.UserName,
                                                    user.PassWord, user.CorpName);
                                            dialog.dismiss();
                                        }
                                    });
                            dialog.show();
                        }
                    }
                    break;
            }
        }

    }

    private void init() {
        context = LoginActivity.this;
        // 头像存放路径
        String avatarPath = FilePathConfig.getAvatarDirPath();
        File file = new File(avatarPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        initEdPwd();

        if (getIntent().getBooleanExtra("isUserStop", false)) {  //员工已经停用
            final BoeryunDialog dialog = new BoeryunDialog(context,
                    false, "提示", "登录信息已失效，请重新登录", "", "确定");
            dialog.setBoeryunDialogClickListener(
                    new BoeryunDialog.OnBoeryunDialogClickListner() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    }, new BoeryunDialog.OnBoeryunDialogClickListner() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
        }
    }

    private void findViews() {
        btnLogin = (Button) findViewById(R.id.buttonLogin);
        etCorpName = (EditText) findViewById(R.id.editTextCorpName);
        etUserName = (EditText) findViewById(editTextUserName);
        etPassWord = (EditText) findViewById(R.id.editTextPassWord);
        findpassword = (TextView) findViewById(R.id.findpassword);
        register = (TextView) findViewById(R.id.register);

        // etCorpName.setVisibility(View.GONE);
    }

    /**
     * 注册广播接收器，检测密码变化
     */
    private void registerBroadCaster() {
        receiver = new LoginCheckReceiver();
        // 定义过滤器
        IntentFilter filter = new IntentFilter();
        // 定义广播响应Intent指定的代号
        filter.addAction("loginCheck");
        // 动态注册 BroadcastReceiver
        registerReceiver(receiver, filter);
    }

    /* 初始化文本框 */
    private void initEdPwd() {
        SharedPreferencesHelper spfl = new SharedPreferencesHelper(
                LoginActivity.this, "ZL.Phone.UserInfo");
        mCorpName = spfl.getValue("corpName");
        oldUserName = spfl.getValue("userName");
        // mCorpName = "波尔云";
        // oldUserName = "阚健雄";
        oldPwd = spfl.getValue("pwd");
        pwdLen = spfl.getIntValue("pwdLen");
        etUserName.setText(oldUserName);
        if (!TextUtils.isEmpty(mCorpName)) {
            etCorpName.setText(mCorpName);
        }
        if (!TextUtils.isEmpty(oldPwd) && pwdLen != 0) {
            // 为了安全，不在本地保存密码，密码文本框的是经过md5加密的密文，截取造成
            showPwd = oldPwd.substring(0, pwdLen);
            etPassWord.setText(showPwd);
        }
    }

    private void setOnClickListener() {
        // 找回密码
        findpassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(LoginActivity.this,
                        FindPasswordActivity.class);
                startActivity(intent);
            }
        });
        // 点击登录
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidate()) {
                    setNewFlag();
                    /** 判断是否网络登录 */
                    if (!HttpUtils.IsHaveInternet(context)) {
                        MessageUtil.AlertMessage(context, "登录失败", "请检查网络连接！");
                        return;
                    } else { // 有网络的情况
                        // checkInternetConnected();
                        // 有网络直接登录
                        String mPwd = etPassWord.getText().toString();
                        LogUtils.d("keno51", mPwd);
                        if (isNewPwd) { // 如果新输入的密码
                            mPwd = ByteUtil.md5One(mPwd);
                        } else {
                            mPwd = oldPwd;
                        }
                        LogUtils.i("keno51", mPwd);
                        startLogin(mUname, mPwd, mCorpName);
                    }
                }
            }

            /**
             * 空校验完毕，根据用户名和密码设置标志位
             */
            private void setNewFlag() {
                if (!etUserName.getText().toString().equals(oldUserName)) {
                    isNewUserName = true; // 用户名如果手工填入过，不是默认的
                } else {
                    isNewUserName = false;
                }
                if (!etPassWord.getText().toString().equals(showPwd)) {
                    isNewPwd = true; // 密码如果手工填入过
                    pwdLen = etPassWord.getText().toString().length();
                    LogUtils.i("keno5", "len:" + pwdLen);
                } else {
                    isNewPwd = false;
                }
                LogUtils.i("keno5", "isNewUserName:" + isNewUserName);
            }
        });
        // 注册
        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                startActivityForResult(intent, REQUEST_CODE_REGISTER);
            }
        });
    }

    // 定义Handler对象
    private Handler handler = new Handler() {
        @Override
        // 当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 只要执行到这里就关闭对话框
            ProgressDialogHelper.dismiss();
            if (msg.what == 0) { // 登录失败
                String info = (String) msg.obj;
                Toast.makeText(context, "登录失败：" + info, Toast.LENGTH_SHORT)
                        .show();
            } else if (msg.what == 1) { // 1代表登录成功
                // 登录完成后,记住用户信息
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, TabMainActivity.class);
                startActivity(intent);

                removeAllNotification();
                InputSoftHelper.hiddenSoftInput(context, etPassWord);
                saveUserInfo();
                LogUtils.i("loginsuccess", "登录成功，完成后");
                finish();
            } else if (msg.what == 123) {// 联网后能访问网络，开始登录
                String mPwd = etPassWord.getText().toString();
                LogUtils.d("keno51", mPwd);
                if (isNewPwd) { // 如果新输入的密码
                    mPwd = ByteUtil.md5One(mPwd);
                } else {
                    mPwd = oldPwd;
                }
                LogUtils.i("keno51", mPwd);
                startLogin(mUname, mPwd, mCorpName);
            } else if (msg.what == 456) {// 联网后不能访问网络
                Toast.makeText(context, "网络连接异常", Toast.LENGTH_SHORT).show();
            } else if (msg.what == SUCCESS_LOGING_FIRST) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, InvitationActivity.class);
                startActivity(intent);
            } else if (msg.what == FAILURE_LOGING_FIRST) {
                Toast.makeText(context, "登录失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 登录
     *
     * @param uname    用户名
     * @param pwd      密码
     * @param corpName 单位名
     */
    private void startLogin(final String uname, final String pwd,
                            final String corpName) {
        LogUtils.i("keno4", uname + "-->" + pwd + "-->" + corpName);
        // 构建一个下载进度条
        ProgressDialogHelper.show(context, "正在登录...");
        new Thread() {
            public void run() {
                try {
                    mDataHelper.login(uname, pwd, corpName, handler);
                } catch (Exception ex) {
                    Message message = handler.obtainMessage();
                    message.what = 0;
                    message.obj = "网络连接异常，请尝试重新登录！";
                    handler.sendMessage(message);
                }
            }
        }.start();
    }

    /**
     * 登录
     *
     * @param uname    用户名
     * @param pwd      密码
     * @param corpName 单位名
     */
    private void startFirstLogin(final String uname, final String pwd,
                                 final String corpName) {
        LogUtils.i("keno4", uname + "-->" + pwd + "-->" + corpName);
        // 构建一个下载进度条
        ProgressDialogHelper.show(context, "初始化..");
        new Thread() {
            public void run() {
                try {
                    boolean isSuccess = mDataHelper.login(uname, pwd, corpName);
                    if (isSuccess) {
                        handler.sendEmptyMessage(SUCCESS_LOGING_FIRST);
                    } else {
                        handler.sendEmptyMessage(FAILURE_LOGING_FIRST);
                    }
                } catch (Exception ex) {
                    handler.sendEmptyMessage(FAILURE_LOGING_FIRST);
                }
            }
        }.start();
    }

    // 登录完成后
    public void saveUserInfo() {
        SharedPreferencesHelper spfl = new SharedPreferencesHelper(
                LoginActivity.this, "ZL.Phone.UserInfo");
        if (isChangeCorp() || isChangeUser()) {
            // 如果切换企业登录
            LogUtils.i("oldCorpId", "切换登录企业或账号");
        }
        spfl.putValue("Al", "1");
        spfl.putValue("isExist", "false");// 标识是否退出
        spfl.putValue("corpName", Global.mUser.CorpName);
        spfl.putValue("userName", Global.mUser.UserName);
        spfl.putValue("pwd", Global.mUser.PassWord);
        spfl.putValue("userId", Global.mUser.Id.toString());
        spfl.putValue("corpId", String.format("%d", Global.mUser.CorpId));
        spfl.putValue("passport", Global.mUser.Passport);
        spfl.putIntValue("dptId", Global.mUser.Department);
        spfl.putIntValue("Position", Global.mUser.Position);
        spfl.putValue("admin", Global.mUser.Admin);
        if (pwdLen > 0) {
            spfl.putIntValue("pwdLen", pwdLen); // 记录密码长度
        }
        LogUtils.i("keno54", "dptId" + Global.mUser.Department);

        putLocalSerializableObj();
    }

    /**
     * 登录失败后，清空登录信息
     */
    public void clearUserInfo() {
        SharedPreferencesHelper spfl = new SharedPreferencesHelper(
                LoginActivity.this, "ZL.Phone.UserInfo");
        if (isChangeCorp()) {
            // 如果切换企业登录
            LogUtils.i("oldCorpId", "如果切换企业登录");
        }

        spfl.putValue("Al", "1");
        spfl.putValue("isExist", "false");// 标识是否退出
        // spfl.putValue("corpName", Global.mUser.CorpName);
        spfl.putValue("corpName", "");
        spfl.putValue("userName", "");
        spfl.putValue("pwd", "");
        // spfl.putValue("userId", Global.mUser.Id.toString());
        // spfl.putValue("corpId", String.format("%d", Global.mUser.CorpId));
        // spfl.putValue("passport", Global.mUser.Passport);
        // spfl.putIntValue("dptId", Global.mUser.dptId);
        // spfl.putValue("admin", Global.mUser.Admin);
        // if (pwdLen > 0) {
        // spfl.putIntValue("pwdLen", pwdLen); // 记录密码长度
        // }
        // LogUtils.i("keno54", "dptId" + Global.mUser.dptId);
    }

    /**
     * 是否切换了企业
     */
    public boolean isChangeCorp() {
        boolean isChangedCorp = false; // 是否改变了企业
        SharedPreferencesHelper spfl = new SharedPreferencesHelper(
                LoginActivity.this, "ZL.Phone.UserInfo");
        String oldCorpId = spfl.getValue("corpId");
        LogUtils.i("UserInfo", "oldCorpId---" + oldCorpId);
        if (!TextUtils.isEmpty(oldCorpId)
                && !oldCorpId.equals(Global.mUser.CorpId + "")) {
            isChangedCorp = true;
            // spfl.putValue("corpId", Global.mUser.CorpId + ""); // 保存当前登录企业
            // 如果切换了登录企业，则清空数据库中所有数据
            ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
            ormDataHelper.deleteOldDb();
        }
        return isChangedCorp;
    }

    /**
     * 是否切换了登录用户
     */
    public boolean isChangeUser() {
        boolean isChangedUser = false; // 是否改变了企业
        SharedPreferencesHelper spfl = new SharedPreferencesHelper(
                LoginActivity.this, "ZL.Phone.UserInfo");
        String oldUserId = spfl.getValue("userId");
        LogUtils.i("UserInfo", "oldCorpId---" + oldUserId);
        if (!TextUtils.isEmpty(oldUserId)
                && !oldUserId.equals(Global.mUser.Id + "")) {
            isChangedUser = true;
            // spfl.putValue("corpId", Global.mUser.CorpId + ""); // 保存当前登录企业
            // 如果切换了登录企业，则清空数据库中所有数据
            ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
            ormDataHelper.deleteOldDb();
        }
        return isChangedUser;
    }

    /**
     * 文本框提交空校验
     */
    private boolean checkValidate() {
        // if (etCorpName.getText() == null
        // || etCorpName.getText().toString().replaceAll(" ", "").length() <= 0)
        // {
        // MessageUtil.AlertMessage(LoginActivity.this, "登录失败", "企业名不能为空！");
        // return false;
        // } else {
        // mCorpName = etCorpName.getText().toString().replaceAll(" ", "");
        // }

        mCorpName = "雪松金融";
//		mCorpName = "金融CRM演示";

        if (etUserName.getText() == null
                || etUserName.getText().toString().replaceAll(" ", "").length() <= 0) {
            MessageUtil.AlertMessage(LoginActivity.this, "登录失败", "用户名不能为空！");
            return false;
        } else {
            mUname = etUserName.getText().toString().replaceAll(" ", "");
        }
        if (etPassWord.getText() == null
                || etPassWord.getText().toString().replaceAll(" ", "").length() <= 0) {
            MessageUtil.AlertMessage(LoginActivity.this, "登录失败", "密码不能为空！");
            return false;
        } else {
            mPassword = etPassWord.getText().toString().replaceAll(" ", "");
        }
        return true;
    }

    /**
     * 存入本地序列化对象
     */
    private void putLocalSerializableObj() {
        try {
            File file = new File(FilePathConfig.getThumbDirPath());
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(FilePathConfig.getThumbDirPath()
                    + FilePathConfig.getLocalSerilizeFileName());
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(Global.mUser);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 移除顶部通知栏所有通知
     */
    private void removeAllNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancelAll();
    }

    private void setUserInfo(final User user) {
        isNewPwd = true;
        etCorpName.setText(user.CorpName + "");
        etUserName.setText(user.UserName + "");
        etPassWord.setText(user.PassWord + "");
    }
}