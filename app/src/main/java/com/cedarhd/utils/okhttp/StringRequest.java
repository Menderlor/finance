package com.cedarhd.utils.okhttp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.cedarhd.BoeryunApp;
import com.cedarhd.ExistApplication;
import com.cedarhd.LoginActivity;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.helpers.Global;
import com.cedarhd.models.ReturnModel;
import com.cedarhd.utils.DESUtil;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

/**
 * 封装OkHttp异步请求网络方式*
 * <p/>
 * 通过二次封装，提供了get和post两种访问方式，把handler和子线程进行封装，调用时类似AsynTask
 * 可通过回调的方式在回调函数中直接进行UI绘制
 *
 * @author K 2015/09/16 23:48
 */
@SuppressLint("NewApi")
public class StringRequest {

    /**
     * 请求参数类型
     */
    public static final MediaType REQUEST_TYPE = MediaType
            .parse("application/json; charset=utf-8");

    /**
     * 连接超时10秒
     */
    public static final long TIME_OUT = 10;

    private static StringRequest mInstance;
    private OkHttpClient mOkHttpClient;
    private static Handler mHandler;
    private static Handler handler;


    @SuppressLint("NewApi")
    private StringRequest() {
        mOkHttpClient = new OkHttpClient();

        // 设置连接超时
        mOkHttpClient.setConnectTimeout(TIME_OUT, TimeUnit.SECONDS);

        // cookie enabled
        mOkHttpClient.setCookieHandler(new CookieManager(null,
                CookiePolicy.ACCEPT_ORIGINAL_SERVER));


        // 初始化和UI线程相关的Handlder
        mHandler = new Handler(Looper.getMainLooper());

//        handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case 301:
//                        final BoeryunDialog dialog = new BoeryunDialog(BoeryunApp.getContext(),
//                                false, "提示", "登录信息已失效，请重新登录", "", "确定");
//                        dialog.setBoeryunDialogClickListener(
//                                new BoeryunDialog.OnBoeryunDialogClickListner() {
//                                    @Override
//                                    public void onClick() {
//                                        // 取消
//                                        dialog.dismiss();
//                                        ExistApplication.getInstance().exit(true);
//                                    }
//                                }, new BoeryunDialog.OnBoeryunDialogClickListner() {
//                                    @Override
//                                    public void onClick() {
//                                        dialog.dismiss();
//                                    }
//                                });
//                        dialog.show();
//                        break;
//                }
//            }
//        };
    }

    public static StringRequest getInstance() {
        if (mInstance == null) {
            synchronized (StringRequest.class) {
                if (mInstance == null) {
                    mInstance = new StringRequest();
                }
            }
        }
        return mInstance;
    }


    /***
     * http get请求，访问结果以String类型在UI线程在回调函数中触发
     *
     * @param url      访问地址
     * @param callBack 回调接口
     */
    public static void get(String url, final StringResponseCallBack callBack) {
        // 初始化一个请求
        Request request = new Request.Builder().url(url).build();
        LogUtils.i("url", url);
        requestFromServer(request, callBack);
    }

    /***
     * http get请求，访问结果以String类型在UI线程在回调函数中触发
     *
     * @param url      访问地址
     * @param callBack 回调接口
     */
    public static void getAsyn(String url, final StringResponseCallBack callBack) {
        // 初始化一个请求
        Request request = new Request.Builder().url(url)
                .header("passport", getPassport()).build();
        LogUtils.i("url", url);
        requestFromServer(request, callBack);
    }

    /**
     * http get请求，访问结果以String类型在UI线程在回调函数中触发
     *
     * @param url      访问地址
     * @param obj      post实体对象
     * @param callBack 回调接口
     */
    public static void postAsyn(String url, Object obj,
                                final StringResponseCallBack callBack) {
        try {
            LogUtils.i("StringRequest_Post",
                    url + "\n" + JsonUtils.initJsonString(obj));
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        Request request = null;
        try {
            String json = JsonUtils.initJsonString(obj);
            RequestBody body = RequestBody.create(REQUEST_TYPE, json);
            // 初始化一个请求
            request = new Request.Builder().url(url)
                    .header("passport", getPassport()).post(body).build();
            requestFromServer(request, callBack);
        } catch (Exception e) {
            deliveryFailureResultToUI(request, e, callBack);
        }
    }

    /**
     * 获取访问网络通行令牌
     */
    private static String getPassport() {
        if (Global.mUser == null) {
            Global.mUser = UserBiz.getLocalSerializableUser();
        }
        LogUtils.i("okPassport", Global.mUser.Passport + "");
        return Global.mUser.Passport;
    }

    /**
     * 向服务器发起请求
     *
     * @param request
     * @param callBack
     */
    private static void requestFromServer(final Request request,
                                          final StringResponseCallBack callBack) {
        // 初始化一个
        Call call = getInstance().mOkHttpClient.newCall(request);
        // 执行请求
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {
                String result = response.body().string();
//                result = DESUtil.decode("@l4&#A1Q", result);
                try {
                    String data = DESUtil.decode("@l4&#A1Q", JsonUtils.getStringValue(result, "Data"));
                    result = JsonUtils.putStringValue(result, data, "Data");
                    LogUtils.i("StringRrequest", request.url() + ":::" + result);
                    ReturnModel<String> returnModel = JsonUtils
                            .pareseResult(result);
                    LogUtils.i("okresult", result);
                    // 解析状态码 1 表示成功
                    if (returnModel.Status == 1) {
                        deliverySuccessResultToUI(result, callBack);
                    } else if (returnModel.Status == 301) {
                        //状态吗==301  员工已经被停用，退出所有页面，并进入到登录页面。
                        ExistApplication.getInstance().exit(false);
                        Intent intent = new Intent(BoeryunApp.getContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("isUserStop", true);
                        BoeryunApp.getContext().startActivity(intent);
                    } else {
                        deliveryResponseCodeErroResultToUI(result, callBack);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    deliveryFailureResultToUI(request, e, callBack);
                }

            }

            @Override
            public void onFailure(Request request, IOException ex) {
                deliveryFailureResultToUI(request, ex, callBack);
            }
        });
    }

    private static void deliverySuccessResultToUI(final String result,
                                                  final StringResponseCallBack callBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onResponse(result);
            }
        });
    }

    private static void deliveryResponseCodeErroResultToUI(
            final String response, final StringResponseCallBack callBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onResponseCodeErro(response);
            }
        });
    }

    private static void deliveryFailureResultToUI(final Request request,
                                                  final Exception ex, final StringResponseCallBack callBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onFailure(request, ex);
            }
        });
    }
}
