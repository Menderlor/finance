package com.cedarhd.helpers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.cedarhd.MenuNewActivity;
import com.cedarhd.R;
import com.cedarhd.models.UpdataBean;
import com.cedarhd.services.UpdateInfoService;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.ShareUtils;

import java.io.File;

/**
 * 检测系统新版本封装类
 *
 * @author BOHR
 */
public class CheckVersionHelper {
    private String version;
    private int versionCode;
    private Context context;
    private UpdataBean updateInfo;
    private ProgressDialog mPd;
    private AlertDialog.Builder builder;
    private Dialog dialog;
    private UpdateInfoService updateInfoService = new UpdateInfoService();
    HttpUtils httpUtils = new HttpUtils();
    private boolean isAuto = true; // 是否自动检测，自动检测不提示 当前已是最新版本,默认不提示
    public static final int ERROR_GET_UPDATEINFO = 1; // 获取新版本失败
    public static final int NOT_SHOW_UPDATE_DIALOG = 2; // 已是最新版本
    public static final int SHOW_UPDATE_DIALOG = 3; // 显示更新对话框
    public static final int ERROR_DOWNLOAD_APK = 4; // 下载apk失败
    public static final int SUCCESS_DOWNLOAD_APK = 5; // 下载apk成功
    public static final int SDCARD_NOT_EXIST = 6; // sd卡不存在
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ERROR_GET_UPDATEINFO:// 下载apk失败

                    break;
                case NOT_SHOW_UPDATE_DIALOG: // 已经是最新版本，不显示
                    if (!isAuto) {
                        // showNotUpdateDialog();
                        showUpdateNotification();
                    }
                    break;
                case SHOW_UPDATE_DIALOG: // 显示更新对话框
                    showUpdateDialog();
                    break;
                case SUCCESS_DOWNLOAD_APK: // 下载apk成功，进入安装页面
                    mPd.dismiss();
                    installApk();
                    break;
                case ERROR_DOWNLOAD_APK: // 下载apk失败
                    mPd.dismiss();
                    break;
            }
        }
    };

    public CheckVersionHelper(Context context) {
        super();
        this.context = context;
        version = ViewHelper.getVersionName(context);
        versionCode = ViewHelper.getVersionCode(context);
        LogUtils.i("Version", version + "---" + versionCode);
    }

    /**
     * 检测版本帮助类的构造方法
     *
     * @param context 当前上下文
     * @param isAuto  是否自动检测，自动检测不提示 当前已是最新版本
     */
    public CheckVersionHelper(Context context, boolean isAuto) {
        this.context = context;
        this.isAuto = isAuto;
        version = ViewHelper.getVersionName(context);
        versionCode = ViewHelper.getVersionCode(context);
        LogUtils.i("Version", version + "---" + versionCode);
    }

    /**
     * 安装应用
     */
    private void installApk() {
        // 安装需要权限
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String name = HttpUtils.getFileName(updateInfo.getApkUrl());
        File file = new File(Environment.getExternalStorageDirectory(), name);
        String path2 = file.getAbsolutePath().trim();

        LogUtils.i("installApk2", path2);
        intent.setDataAndType(Uri.fromFile(new File(path2)),
                "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 提示安装成功
        context.startActivity(intent);
    }

    /**
     * 检查版本的任务
     */
    public final class CheckVersionTask implements Runnable {
        @Override
        public void run() {
            try {
                String path = context.getString(R.string.version_url);
                try {
                    updateInfo = updateInfoService.getUpdateInfo(path);
                    if (updateInfo == null) {
                        LogUtils.i("version", "获取最新版本信息失败");
                        Message msg = new Message();
                        msg.what = ERROR_GET_UPDATEINFO;
                        handler.sendMessage(msg);
                    } else {
                        // 如果当前安装版本VersionName和服务器配置信息VersionName一致
                        if (version.equals(updateInfo.getVersionName())) {
                            // 当前已经是最新版本，不用显示更新的对话框
                            Message msg = new Message();
                            msg.what = NOT_SHOW_UPDATE_DIALOG;
                            handler.sendMessage(msg);
                        } else {
                            // if (versionCode < updateInfo.getVersion())
                            // 如果当前安装版本VersionName和服务器配置信息VersionName一致,且服务器版本高于当前版本
                            // 显示更新的对话框
                            Message msg = new Message();
                            msg.what = SHOW_UPDATE_DIALOG;
                            handler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("version", "获取最新版本信息失败");
                    Message msg = new Message();
                    msg.what = ERROR_GET_UPDATEINFO;
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                Toast.makeText(context, "检测新版本异常", Toast.LENGTH_SHORT).show();
            }

        }
    }


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
        if (builder == null) {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("软件更新");
        builder.setMessage(updateInfo.getDesc());
//        builder.setCancelable(false);// 屏蔽后退按钮
        builder.setPositiveButton("立即更新",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        mPd = new ProgressDialog(context);
//                        mPd.setMessage("正在下载最新版本的应用");
//                        // mPd.setIndeterminate(false);// false代表根据程序进度确切的显示进度
//                        mPd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // 设置进度条的形状
//                        mPd.show();
//                        new Thread(new DownloadApkTask()).start();
                        ShareUtils.launchAppDetail(context, "com.cedarhd", "");
                    }
                });
        builder.setNegativeButton("以后再说",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                         loginMainUI();
                        MenuNewActivity.cancleUpdate = true;
                    }
                });
        if (dialog == null) {
            dialog = builder.create();
        }
        dialog.setCancelable(false);
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 显示升级更新的提示对话框
     */
    private void showUpdateNotification() {
        context.startService(new Intent(context, DownloadIntentService.class));
    }

    /**
     * 已是最新版本，无需更新
     */
    private void showNotUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("更新").setMessage("已是最新版本，无需更新")
                .setPositiveButton("确定", null).setNegativeButton("取消", null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 下载apk
     */
    private final class DownloadApkTask implements Runnable {
        @Override
        public void run() {
            try {
                boolean result = HttpUtils.download(
                        context.getString(R.string.apk_url), mPd);
                if (result) {
                    handler.sendEmptyMessage(SUCCESS_DOWNLOAD_APK);
                } else {
                    handler.sendEmptyMessage(ERROR_DOWNLOAD_APK);
                }
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(ERROR_DOWNLOAD_APK);
            }
        }
    }

    public class DownloadIntentService extends IntentService {
        public DownloadIntentService() {
            super("Download");
        }

        public DownloadIntentService(String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            try {
                // boolean result = HttpUtils.download(
                // context.getString(R.string.apk_url), mPd);
                // if (result) {
                // handler.sendEmptyMessage(SUCCESS_DOWNLOAD_APK);
                // } else {
                // handler.sendEmptyMessage(ERROR_DOWNLOAD_APK);
                // }
                String result = httpUtils.get(context
                        .getString(R.string.version_url));
                LogUtils.i("result", result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
