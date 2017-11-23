package com.cedarhd.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.cedarhd.BoeryunApp;
import com.cedarhd.ExistApplication;
import com.cedarhd.LoginActivity;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.ReturnModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class HttpUtils { // 从服务器端下载到Json数据，也就是个字符串
    private final static int timeout = 15;

    public String get(String url) throws Exception {
        try {
            LogUtils.d("httpUrl", "get(url)------>" + url);
            StringBuilder sb = new StringBuilder();
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpConnectionParams.setConnectionTimeout(httpGet.getParams(),
                    timeout * 1000);
            HttpConnectionParams.setSoTimeout(httpGet.getParams(),
                    timeout * 1000);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = null;
            if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                httpEntity = httpResponse.getEntity();
            }

            if (httpEntity != null) {
                InputStream instream = httpEntity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(instream));

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                isExit(sb.toString());
                String result = "";
                String data = DESUtil.decode("@l4&#A1Q", JsonUtils.getStringValue(sb.toString(), "Data"));
                result = JsonUtils.putStringValue(sb.toString(), data, "Data");
                return result;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtils.d("keno4", "HttpUtils.get()--->网络错误" + e);
            e.printStackTrace();
            return "网络错误";
        }
        return "";
    }

    public String post(String url, JSONObject jo2) throws Exception {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        // HttpConnectionParams.setConnectionTimeout(httpPost.getParams(),
        // timeout * 1000);
        // HttpConnectionParams.setSoTimeout(httpPost.getParams(),
        // timeout * 1000);

        StringEntity se;

        String strResp = null;
        try {
            se = new StringEntity(jo2.toString(), HTTP.UTF_8);
            se.setContentType("application/json");
            // httpPost.setHeader("passport", Global.mUser.Passport);
            // LogUtils.d("kjx", "passport====" + Global.mUser.Passport);
            httpPost.setEntity(se);
            // httpPost.setEntity(new UrlEncodedFormEntity(se));

            HttpResponse httpResponse = httpClient.execute(httpPost);

            if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                strResp = EntityUtils.toString(httpResponse.getEntity());
            } else {
                // strResp = "$no_found_date$";
                strResp = null;
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            httpPost.abort();
        }
        isExit(strResp);
        String result = "";
        String data = DESUtil.decode("@l4&#A1Q", JsonUtils.getStringValue(strResp, "Data"));
        result = JsonUtils.putStringValue(strResp, data, "Data");
        return result;
    }

    /**
     * 公用的post方法
     *
     * @param url 网络地址
     * @param jo2 封装成JSONObject对象的上传数据
     * @return
     * @throws Exception
     */
    public String postSubmit(String url, JSONObject jo2) throws Exception {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        // 设置超时 10秒访问超时
        HttpConnectionParams.setSoTimeout(httpPost.getParams(), timeout * 1000);
        StringEntity se;
        String strResp = null;
        try {
            if (Global.mUser == null) {
                Global.mUser = ZLServiceHelper.getLocalSerializableUser();
            }
            se = new StringEntity(jo2.toString(), HTTP.UTF_8);
            se.setContentType("application/json"); // 原版
            // se.setContentType("application/x-www-form-urlencoded; charset=utf-8");
            // 添加passport通行证
            // LogUtils.d("myPassport", Global.mUser.Passport);
            httpPost.setHeader("passport", Global.mUser.Passport);
            httpPost.setEntity(se);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            strResp = EntityUtils.toString(httpResponse.getEntity());
            LogUtils.d("url_postSubmit_getStatusLine", url + "-------->"
                    + httpResponse.getStatusLine().getStatusCode() + strResp);
            if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                strResp = EntityUtils.toString(httpResponse.getEntity());
            } else {
                strResp = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("erro", "" + e);
        } finally {
            httpPost.abort();
        }
        isExit(strResp);
        String result = "";
        String data = DESUtil.decode("@l4&#A1Q", JsonUtils.getStringValue(strResp, "Data"));
        result = JsonUtils.putStringValue(strResp, data, "Data");
        return result;
    }


    public String doGet(String url) throws Exception {

        URL localURL = new URL(url);

        URLConnection connection = localURL.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

        httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setConnectTimeout(timeout * 1000);
        httpURLConnection.setRequestMethod("GET"); //设定请求方式
        httpURLConnection.connect();

        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;

        if (httpURLConnection.getResponseCode() >= 300) {
            throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }

        try {
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }

        } finally {

            if (reader != null) {
                reader.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

        }

        String result = "";
        String data = DESUtil.decode("@l4&#A1Q", JsonUtils.getStringValue(resultBuffer.toString(), "Data"));
        result = JsonUtils.putStringValue(resultBuffer.toString(), data, "Data");
        return result;
    }

    /**
     * @param url
     * @param jo2
     * @return post方法提交json字符串
     * @throws Exception
     */
    public String postJsonSubmit(String url, String jo2) throws Exception {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        // HttpConnectionParams.setConnectionTimeout(httpPost.getParams(),
        // timeout * 1000);
        // HttpConnectionParams.setSoTimeout(httpPost.getParams(),
        // timeout * 1000);
        StringEntity se;

        String strResp = null;
        try {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            // NameValuePair nameValuePair = new BasicNameValuePair("fields",
            // jo2);
            NameValuePair nameValuePair = new BasicNameValuePair("fields", jo2);
            list.add(nameValuePair);
            // 添加通行证
            httpPost.setHeader("passport", Global.mUser.Passport);
            httpPost.setEntity(new UrlEncodedFormEntity(list, "utf-8"));
            // httpPost.setEntity(new UrlEncodedFormEntity(se));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                strResp = EntityUtils.toString(httpResponse.getEntity());
            } else {
                // strResp = "$no_found_date$";
                strResp = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpPost.abort();
        }

        LogUtils.d("keno22", strResp);
        return strResp;
    }

    /**
     * 是否联网网络
     *
     * @param context
     * @return
     */
    public static boolean IsHaveInternet(final Context context) {
        try {
            ConnectivityManager manger = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manger.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 自定义AppachHttpGet访问网络(带有权限)
     *
     * @param url url地址
     */
    public String httpGet(String url) {
        StringBuilder sb = new StringBuilder();
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            if (Global.mUser == null) {
                Global.mUser = ZLServiceHelper.getLocalSerializableUser();
            }
            String passport = Global.mUser.Passport;
            httpGet.setHeader("passport", passport);
            HttpConnectionParams.setConnectionTimeout(httpGet.getParams(),
                    timeout * 1000);
            HttpConnectionParams.setSoTimeout(httpGet.getParams(),
                    timeout * 1000);
            LogUtils.d("passportkurl", url);
            LogUtils.d("passportk", passport);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = null;
            LogUtils.d("passportk", "code="
                    + httpResponse.getStatusLine().getStatusCode());

            if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                httpEntity = httpResponse.getEntity();
            }
            if (httpEntity != null) {
                InputStream instream = httpEntity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(instream));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    LogUtils.d("jsonkeno2", line);
                }

                String result = "";
                String data = DESUtil.decode("@l4&#A1Q", JsonUtils.getStringValue(sb.toString(), "Data"));
                result = JsonUtils.putStringValue(sb.toString(), data, "Data");
                return result;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtils.d("jsonkeno", "HttpUtils.get()--->网络错误:" + e);
            e.printStackTrace();
            return "网络错误";
        }
        return sb.toString();
    }

    /**
     * 自定义AppachHttpPost访问网络(带有权限)
     *
     * @param url url地址
     */
    public String httpPost(String url) {
        StringBuilder sb = new StringBuilder();
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String passport = Global.mUser.Passport;
            httpPost.setHeader("passport", passport);
            HttpConnectionParams.setConnectionTimeout(httpPost.getParams(),
                    timeout * 3000);
            HttpConnectionParams.setSoTimeout(httpPost.getParams(),
                    timeout * 3000);

            LogUtils.d("passportkurl", url);
            LogUtils.d("passportk", passport);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = null;
            if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                httpEntity = httpResponse.getEntity();
            }
            if (httpEntity != null) {
                InputStream instream = httpEntity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(instream));

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    LogUtils.d("jsonkeno2", line);
                }
                String result = "";
                String data = DESUtil.decode("@l4&#A1Q", JsonUtils.getStringValue(sb.toString(), "Data"));
                result = JsonUtils.putStringValue(sb.toString(), data, "Data");
                return result;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtils.d("jsonkeno", "HttpUtils.get()--->网络错误:" + e);
            e.printStackTrace();
            return "网络错误";
        }
        isExit(sb.toString());
        return DESUtil.decode("@l4&#A1Q", sb.toString());
    }

    /**
     * 自定义AppachHttpGet访问网络,下载数据
     *
     * @param url url地址
     */
    public void downloadData(String url, Handler handler) {
        int index = url.lastIndexOf("\\");
        String avatarName = url.substring(index + 1, url.length());
        // 头像存放路径
        String avatarPath = FilePathConfig.getAvatarDirPath() + File.separator
                + avatarName;
        Message msg = handler.obtainMessage();
        url = Global.BASE_URL + Global.EXTENSION + url;
        LogUtils.d("leo2", url);
        try {
            url = url.replace("\\", "/");
            // url = URLEncoder.encode(url, "UTF-8");
            LogUtils.d("leo2", url);
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            // httpGet.setHeader("passport", Global.mUser.Passport);
            HttpConnectionParams.setConnectionTimeout(httpGet.getParams(),
                    timeout * 10000);
            HttpConnectionParams.setSoTimeout(httpGet.getParams(),
                    timeout * 10000);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = null;
            if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                httpEntity = httpResponse.getEntity();
            }
            if (httpEntity != null) {
                InputStream is = httpEntity.getContent();
                int contentLen = (int) httpEntity.getContentLength();
                final ByteArrayOutputStream out = new ByteArrayOutputStream(
                        contentLen > 0 ? contentLen : 1024);
                File file = new File(avatarPath);
                FileOutputStream fos = new FileOutputStream(file);
                int len = -1;
                byte[] b = new byte[1024];
                while ((len = is.read(b)) != -1) {
                    out.write(b, 0, len);
                    fos.write(b, 0, len);
                }
                byte[] data = out.toByteArray();
                LogUtils.d("leo2", "长度：" + data.length);
                msg.obj = out.toByteArray();
                // msg.what =
                // SettingsActivity.HandlerSetting.SHOW_IMAGE_SUCCESS;
                msg.what = 3; // 3代表成功
                handler.sendMessage(msg);
                fos.close();
                out.close();
                is.close();
            }
        } catch (Exception e) {
            LogUtils.d("kjx6", "网络错误,下载失败" + e.getMessage());
            e.printStackTrace();
            msg.obj = new byte[0];
            // msg.what = SettingsActivity.HandlerSetting.SHOW_IMAGE_FAILUREE;
            msg.what = 4;
            handler.sendMessage(msg);
        }
    }

    /**
     * 自定义AppachHttpGet访问网络,下载数据
     *
     * @param url     图片地址
     * @param handler
     * @param iView   要显示头像的ImageView
     */
    public void downloadData(String url, Handler handler, ImageView iView) {
        // int index = url.lastIndexOf("\\");
        int index = 0;
        if (url.contains("\\")) {
            index = url.lastIndexOf("\\");
        } else if (url.contains("/")) {
            index = url.lastIndexOf("/");
        }
        String avatarName = url.substring(index + 1, url.length());
        // 头像存放路径
        String avatarPath = FilePathConfig.getAvatarDirPath() + File.separator
                + avatarName;

        Message msg = handler.obtainMessage();
        msg.obj = iView;
        if (!url.contains(Global.BASE_URL + Global.EXTENSION)) {
            url = Global.BASE_URL + Global.EXTENSION + url;
        }
        LogUtils.d("picUrl3", url);
        try {
            url = url.replace("\\", "/");
            LogUtils.d("picUrl3", url);
            LogUtils.d("picUrl3", avatarName);
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            // httpGet.setHeader("passport", Global.mUser.Passport);
            HttpConnectionParams.setConnectionTimeout(httpGet.getParams(),
                    timeout * 10000);
            HttpConnectionParams.setSoTimeout(httpGet.getParams(),
                    timeout * 10000);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = null;
            if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                httpEntity = httpResponse.getEntity();
            }
            if (httpEntity != null) {
                InputStream is = httpEntity.getContent();
                int contentLen = (int) httpEntity.getContentLength();
                final ByteArrayOutputStream out = new ByteArrayOutputStream(
                        contentLen > 0 ? contentLen : 1024);
                File file = new File(avatarPath);
                FileOutputStream fos = new FileOutputStream(file);
                int len = -1;
                byte[] b = new byte[1024];
                while ((len = is.read(b)) != -1) {
                    out.write(b, 0, len);
                    fos.write(b, 0, len);
                }
                byte[] data = out.toByteArray();
                LogUtils.d("leo2", "长度：" + data.length);
                // Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                // data.length);
                // iView.setTag(bitmap);

                // iView.setTag(avatarName); // 图片名称保存在图片控件的tag中
                // msg.what = 3; // 3代表成功,下载完成
                // handler.sendMessage(msg);

                int ivPos = 0;
                try {
                    ivPos = Integer.parseInt(iView.getTag().toString());
                    if (ivPos >= 0) {
                        // 缓存位置信息
                        msg.arg2 = ivPos;
                    }
                } catch (Exception e) {
                    LogUtils.e("--", "===v.getTag().toString()="
                            + iView.getTag().toString());
                }
                iView.setTag(avatarName); // 图片名称保存在图片控件的tag中
                msg.what = 3; // 3代表成功,下载完成
                handler.sendMessage(msg);

                fos.close();
                out.close();
                is.close();
            }
        } catch (Exception e) {
            LogUtils.d("kjx6", "网络错误,下载失败" + e.getMessage());
            e.printStackTrace();
            msg.what = 4;
            handler.sendMessage(msg);
        }
    }

    /**
     * 自定义AppachHttpGet访问网络,下载数据
     *
     * @param url     图片地址
     * @param handler
     * @param iView   要显示头像的ImageView
     * @param postion 开启下载线程的postion
     */
    public void downloadData(String url, Handler handler, ImageView iView,
                             int postion) {
        int pos = (Integer) iView.getTag(); // 控件中的pos
        int index = url.lastIndexOf("\\");
        String avatarName = url.substring(index + 1, url.length());
        // 头像存放路径
        String avatarPath = FilePathConfig.getAvatarDirPath() + File.separator
                + avatarName;
        Message msg = handler.obtainMessage();
        msg.obj = iView;
        url = Global.BASE_URL + Global.EXTENSION + url;
        LogUtils.d("leo2", url);
        try {
            url = url.replace("\\", "/");
            // url = URLEncoder.encode(url, "UTF-8");
            LogUtils.d("leo2", url);
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            // httpGet.setHeader("passport", Global.mUser.Passport);
            HttpConnectionParams.setConnectionTimeout(httpGet.getParams(),
                    timeout * 1000);
            HttpConnectionParams.setSoTimeout(httpGet.getParams(),
                    timeout * 1000);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = null;
            if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                httpEntity = httpResponse.getEntity();
            }
            if (httpEntity != null) {
                InputStream is = httpEntity.getContent();
                int contentLen = (int) httpEntity.getContentLength();
                final ByteArrayOutputStream out = new ByteArrayOutputStream(
                        contentLen > 0 ? contentLen : 1024);
                File file = new File(avatarPath);
                FileOutputStream fos = new FileOutputStream(file);
                int len = -1;
                byte[] b = new byte[1024];
                while ((len = is.read(b)) != -1) {
                    out.write(b, 0, len);
                    fos.write(b, 0, len);
                }
                byte[] data = out.toByteArray();
                LogUtils.d("leo2", "长度：" + data.length);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                        data.length);
                iView.setTag(bitmap);
                // msg.what =
                // SettingsActivity.HandlerSetting.SHOW_IMAGE_SUCCESS;
                msg.arg1 = pos;
                msg.arg2 = postion;
                msg.what = 3; // 3代表成功
                handler.sendMessage(msg);

                fos.close();
                out.close();
                is.close();
            }
        } catch (Exception e) {
            LogUtils.d("kjx6", "网络错误,下载失败" + e.getMessage());
            e.printStackTrace();
            msg.what = 4;
            handler.sendMessage(msg);
        }
    }

    /**
     * 访问网络,下载图片数据,并存储到本地DCIM目录
     *
     * @param url url地址
     * @return byte[] 图片的数组
     */
    public static byte[] downloadData(String url) {
        int index = url.lastIndexOf("/");
        // int index = url.lastIndexOf("\\");
        String avatarName = url.substring(index + 1, url.length());
        // 头像存放路径
        String avatarPath = Environment.getExternalStorageDirectory() + "/DCIM"
                + File.separator + avatarName;

        // url = Global.BASE_URL + Global.EXTENSION + url;
        try {
            // url = URLEncoder.encode(url, "UTF-8");
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            // httpGet.setHeader("passport", Global.mUser.Passport);
            HttpConnectionParams.setConnectionTimeout(httpGet.getParams(),
                    timeout * 1000);
            HttpConnectionParams.setSoTimeout(httpGet.getParams(),
                    timeout * 1000);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = null;
            if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                httpEntity = httpResponse.getEntity();
            }
            if (httpEntity != null) {
                InputStream is = httpEntity.getContent();
                int contentLen = (int) httpEntity.getContentLength();
                final ByteArrayOutputStream out = new ByteArrayOutputStream(
                        contentLen > 0 ? contentLen : 1024);
                File file = new File(avatarPath);
                FileOutputStream fos = new FileOutputStream(file);
                int len = -1;
                byte[] b = new byte[1024];
                while ((len = is.read(b)) != -1) {
                    out.write(b, 0, len);
                    fos.write(b, 0, len);
                }
                byte[] data = out.toByteArray();
                LogUtils.d("leo2", "长度：" + data.length);

                fos.close();
                out.close();
                is.close();
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 访问网络,下载图片数据,并存储到本地DCIM目录
     *
     * @param url url地址
     * @return byte[] 图片的数组
     */
    public static byte[] downloadData(String url, String fileName) {
        // 文件存放路径
        String filePath = FilePathConfig.getCacheDirPath() + File.separator
                + fileName;
        LogUtils.i("filePath", filePath);
        try {
            HttpClient httpClient = new DefaultHttpClient();
            LogUtils.i("url", url);
            url = url.replace("\\", "/");
            LogUtils.d("url", url);
            HttpGet httpGet = new HttpGet(url);
            // httpGet.setHeader("passport", Global.mUser.Passport);
            HttpConnectionParams.setConnectionTimeout(httpGet.getParams(),
                    timeout * 1000);
            HttpConnectionParams.setSoTimeout(httpGet.getParams(),
                    timeout * 1000);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = null;
            if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                httpEntity = httpResponse.getEntity();
            }
            if (httpEntity != null) {
                InputStream is = httpEntity.getContent();
                int contentLen = (int) httpEntity.getContentLength();
                final ByteArrayOutputStream out = new ByteArrayOutputStream(
                        contentLen > 0 ? contentLen : 1024);
                File file = new File(filePath);
                FileOutputStream fos = new FileOutputStream(file);
                int len = -1;
                byte[] b = new byte[1024];
                while ((len = is.read(b)) != -1) {
                    out.write(b, 0, len);
                    fos.write(b, 0, len);
                }
                byte[] data = out.toByteArray();
                LogUtils.d("leo2", "长度：" + data.length);

                fos.close();
                out.close();
                is.close();
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载文件到sdcard
     *
     * @param path 下载路径
     * @param mPd  进度条对话框
     * @return
     * @throws Exception
     */
    public static boolean download(String path, ProgressDialog mPd)
            throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            mPd.setMax(conn.getContentLength() / 1024); // 设置进度条最大值 百分百
            InputStream is = conn.getInputStream();
            File file = new File(FilePathConfig.getCacheDirPath(),
                    getFileName(path));
            LogUtils.d("apkNameDown", getFileName(path));
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            int count = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                count = count + len / 1024;
                mPd.setProgress(count);
            }
            fos.flush();
            is.close();
            fos.close();
            return true;
        }
        return false;
    }

    /**
     * 根据路径 得到文件名称
     *
     * @param path
     * @return
     */
    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }


    /**
     * 判断用户是否被停用
     *
     * @param string
     */
    private void isExit(String string) {

        ReturnModel<String> returnModel = JsonUtils
                .pareseResult(string);
        if (returnModel != null && returnModel.Status == 301) {
            //状态吗==301  员工已经被停用，退出所有页面，并进入到登录页面。
            ExistApplication.getInstance().exit(false);
            Intent intent = new Intent(BoeryunApp.getContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("isUserStop", true);
            BoeryunApp.getContext().startActivity(intent);

        }
    }

}
