package com.cedarhd;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.models.字典;
import com.iflytek.cloud.SpeechUtility;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tencent.stat.StatService;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;

/**
 * 应用程序入口
 *
 * @author BOHR
 */
public class BoeryunApp extends Application {
    private static Context context;
    private ORMDataHelper dbHelper;
    private static HashMap<String, SoftReference<List<字典>>> mDictHashMap;

    /**
     *
     */
    public BoeryunApp() {
        super();
        // 必须实现一个无参数的构造
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        dbHelper = ORMDataHelper.getInstance(context);

        // 应用程序入口处调用,避免手机内存过小，杀死后台进程,造成SpeechUtility对象为null
        // 设置你申请的应用appid
        SpeechUtility.createUtility(BoeryunApp.this, "appid=537d8dde");
        // // // iTestin,崩溃测试， 集成sdk初始化（使用时会严重占用网速，导致系统变慢）
        // TestinAgent.init(context, "4263600bce57de281486505a230fd936");

        // 百度地图初始化
//        SDKInitializer.initialize(getApplicationContext());

        initSetting();
        initImageLoader();
        // initTecenMAT();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        dbHelper.close();
    }

    /**
     * 初始化提示信息
     */
    private void initSetting() {
        SharedPreferences sp = getSharedPreferences("remind", MODE_PRIVATE);
        boolean isFirst = sp.getBoolean("isFirst", true);
        if (isFirst) { // 如果是首次登录
            Editor editor = sp.edit();
            editor.putBoolean("notice", true);
            editor.putBoolean("email", false); // 邮件默认不提示
            editor.putBoolean("log", false); // 日志默认不提示
            editor.putBoolean("client", true);
            editor.putBoolean("order", true);
            editor.putBoolean("contact", true);
            editor.putBoolean("task", true);
            editor.putBoolean("saleChance", true);
            editor.putBoolean("approval", true);
            editor.putBoolean("isFirst", false); // 登录过后，修改首次登录标志位
            editor.commit();
        }
    }

    /***
     * 初始化ImageLoader，程序启动时初始化一次即可
     */
    private void initImageLoader() {
        // File cacheDir = new File(FilePathConfig.getCacheDirPath());
        File cacheDir = new File(FilePathConfig.getCacheDirPath());
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder() //
                .showImageForEmptyUri(R.drawable.img_item) //
                .showImageOnFail(R.drawable.img_item) //
                .cacheInMemory(true) //
                .cacheOnDisk(true) //
                .build();//
        ImageLoaderConfiguration config = new ImageLoaderConfiguration//
                .Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCacheSize(5 * 1024 * 1024) // 内存缓存容量5M
                .diskCacheSize(50 * 1024 * 1024) // 内存缓存容量50M
                .diskCache(new UnlimitedDiskCache(cacheDir))// 设置缓存路径
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
    }

    /***
     * 初始化腾讯数据统计
     */
    private void initTecenMAT() {
        StatService.onResume(getApplicationContext());
    }

    /***
     * 获取字典集合引用
     *
     * @return
     */
    public static HashMap<String, SoftReference<List<字典>>> getDictHashMap() {
        if (mDictHashMap == null) {
            mDictHashMap = new HashMap<String, SoftReference<List<字典>>>();
        }
        return mDictHashMap;
    }

    public static Context getContext() {
        return context;
    }

}
