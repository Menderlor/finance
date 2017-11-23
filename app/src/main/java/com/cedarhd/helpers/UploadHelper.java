package com.cedarhd.helpers;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.cedarhd.imp.IOnUploadMultipleFileListener;
import com.cedarhd.models.Attach;
import com.cedarhd.utils.DESUtil;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UploadHelper {

    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 20 * 1000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码
    private static UploadHelper mInstance;
    private static Handler mHandler;

    private UploadHelper() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static UploadHelper getInstance() {
        if (mInstance == null) {
            mInstance = new UploadHelper();
        }
        return mInstance;
    }

    /**
     * 上传文件
     *
     * @param file       文件
     * @param methodName 方法名
     * @return 附件编号
     */
    private static Attach uploadFile(File file, String methodName) {
        String RequestURL = Global.BASE_URL + Global.EXTENSION + methodName;
        String result = "";
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型

        Attach attach = new Attach();
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            String passport = "";
            passport = Global.mUser.Passport;
            LogUtils.i(TAG, "passport---" + passport);
            conn.setRequestProperty("passport", passport); // 权限
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);
            if (file != null) {
                LogUtils.i(TAG, "文件不为空");
                /** 当文件不为空，把文件包装并且上传 */
                DataOutputStream dos = new DataOutputStream(
                        conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                LogUtils.i(TAG, "length:" + sb.length());//
                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024 * 1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                        .getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                LogUtils.e(TAG, "response code:" + res);
                // 获得服务端返回的响应
                InputStream input = conn.getInputStream();
                StringBuffer sb1 = new StringBuffer();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                // 服务端返回的附件编号
                String data = DESUtil.decode("@l4&#A1Q", JsonUtils.getStringValue(sb1.toString(), "Data"));
                result = JsonUtils.putStringValue(sb1.toString(), data, "Data");
                LogUtils.d(TAG, "http响应 : " + result);
                result = result.trim();
                LogUtils.d(TAG, "http响应 : " + result);
                List<Attach> list = JsonUtils.ConvertJsonToList(result,
                        Attach.class);
                if (list.size() > 0) {
                    attach = list.get(0);
                    LogUtils.e("uploadfileAttach", "attach--id=" + attach.Id
                            + " Address:" + attach.Address);
                }
            }
        } catch (MalformedURLException e) {
            LogUtils.e("uploadfile", ": " + e);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        result = result.replaceAll("\"", "");
        LogUtils.d(TAG, "服务端返回的附件编号: " + attach.Id);
        // return result;
        return attach;
    }

    /**
     * android上传文件到服务器 HTTP方式请求只能上传小文件
     *
     * @param file 需要上传的文件
     * @return 返回响应的内容:list集合 图片，路径
     */
    public static String uploadFileByHttp(File file) {
        String methodName = "FileUpDownload/fileupload/";
        // String methodName = "FileUpDownload/FileUploadGetPath/";
        Attach attach = uploadFile(file, methodName);
        return attach.Address;
    }

    /**
     * android上传一个文件到服务器,返回附件实体类
     *
     * @param file 需要上传的文件
     * @return 返回附件实体类
     */
    public static Attach uploadFileByHttpGetAttach(File file) {
        String methodName = "FileUpDownload/fileupload/";
        Attach attach = uploadFile(file, methodName);
        return attach;
    }

    /**
     * android上传文件到服务器 HTTP方式请求只能上传小文件
     *
     * @param file 需要上传的文件
     * @return 返回响应的内容:附件编号
     */
    public static String uploadFileGetAttachId(File file) {
        // String methodName = "FileUpDownload/attachupload/";
        // 以后的接口全部使用fileupload
        String methodName = "FileUpDownload/fileupload/";
        Attach attach = uploadFile(file, methodName);
        LogUtils.i(TAG, attach.toString());
        return attach.Id + "";
    }

    /**
     * 五个参数： 1、String url：指定表单提交的url地址 2、Map<String, String>
     * map：将上传控件之外的其他控件的数据信息存入map对象 3、String filePath：指定要上传到服务器的文件的客户端路径
     * 4、byte[] body_data：获取到要上传的文件的输入流信息，通过ByteArrayOutputStream流转成byte[]
     * 5、String charset：设置字符集
     */
    public static String doPostSubmitBody(Map<String, String> map,
                                          String filePath, byte[] body_data, String charset) {
        String url = Global.BASE_URL + "FileUpDownload/attachupload";
        // 设置三个常用字符串常量：换行、前缀、分界线（NEWLINE、PREFIX、BOUNDARY）；
        final String NEWLINE = "\r\n";
        final String PREFIX = "--";
        final String BOUNDARY = "#";
        HttpURLConnection httpConn = null;
        BufferedInputStream bis = null;
        DataOutputStream dos = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 实例化URL对象。调用URL有参构造方法，参数是一个url地址；
            URL urlObj = new URL(url);
            // 调用URL对象的openConnection()方法，创建HttpURLConnection对象；
            httpConn = (HttpURLConnection) urlObj.openConnection();
            // 调用HttpURLConnection对象setDoOutput(true)、setDoInput(true)、setRequestMethod("POST")；
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            httpConn.setReadTimeout(TIME_OUT);
            httpConn.setConnectTimeout(TIME_OUT);
            httpConn.setRequestMethod("POST");
            // 设置Http请求头信息；（Accept、Connection、Accept-Encoding、Cache-Control、Content-Type、User-Agent）
            httpConn.setUseCaches(false);
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            httpConn.setRequestProperty("Cache-Control", "no-cache");
            httpConn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            httpConn.setRequestProperty(
                    "User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30)");
            // 调用HttpURLConnection对象的connect()方法，建立与服务器的真实连接；
            httpConn.connect();

            // 调用HttpURLConnection对象的getOutputStream()方法构建输出流对象；
            dos = new DataOutputStream(httpConn.getOutputStream());
            // 获取表单中上传控件之外的控件数据，写入到输出流对象（根据HttpWatch提示的流信息拼凑字符串）；
            if (map != null && !map.isEmpty()) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    String value = map.get(key);
                    dos.writeBytes(PREFIX + BOUNDARY + NEWLINE);
                    dos.writeBytes("Content-Disposition: form-data; "
                            + "name=\"" + key + "\"" + NEWLINE);
                    dos.writeBytes(NEWLINE);
                    dos.writeBytes(URLEncoder.encode(value.toString(), charset));
                    // 或者写成：dos.write(value.toString().getBytes(charset));
                    dos.writeBytes(NEWLINE);
                }
            }

            // 获取表单中上传控件的数据，写入到输出流对象（根据HttpWatch提示的流信息拼凑字符串）；
            if (body_data != null && body_data.length > 0) {
                dos.writeBytes(PREFIX + BOUNDARY + NEWLINE);
                String fileName = filePath.substring(filePath
                        .lastIndexOf(File.separatorChar));
                dos.writeBytes("Content-Disposition: form-data; " + "name=\""
                        + "uploadFile" + "\"" + "; filename=\"" + fileName
                        + "\"" + NEWLINE);
                dos.writeBytes(NEWLINE);
                dos.write(body_data);
                dos.writeBytes(NEWLINE);
            }
            dos.writeBytes(PREFIX + BOUNDARY + PREFIX + NEWLINE);
            dos.flush();

            // 调用HttpURLConnection对象的getInputStream()方法构建输入流对象；
            byte[] buffer = new byte[12 * 1024];
            int c = 0;
            // 调用HttpURLConnection对象的getResponseCode()获取客户端与服务器端的连接状态码。如果是200，则执行以下操作，否则返回null；
            if (httpConn.getResponseCode() == 200) {
                bis = new BufferedInputStream(httpConn.getInputStream());
                while ((c = bis.read(buffer)) != -1) {
                    baos.write(buffer, 0, c);
                    baos.flush();
                }
            }
            // 将输入流转成字节数组，返回给客户端。
            return new String(baos.toByteArray(), charset);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dos.close();
                bis.close();
                baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 上传附件一组图片
     *
     * @param filePathList
     * @return 返回一组上传图片的附件号
     * @author KJX update 2016-5-18
     */
    public void uploadMultipleFiles(final List<String> filePathList,
                                    final IOnUploadMultipleFileListener onUploadMultipleFileListener) {
        final StringBuilder sbAttachIds = new StringBuilder(); // 存储照片上传后返回的附件号
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        /**
         * 将生产新的异步任务与使用已完成任务的结果分离开来的服务。生产者 submit 执行的任务。使用者 take
         * 已完成的任务，并按照完成这些任务的顺序处理它们的结果。
         */
        CompletionService<String> completionService = new ExecutorCompletionService<String>(
                threadPool);
        int count = 0;

        if (onUploadMultipleFileListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onUploadMultipleFileListener.onStartUpload(filePathList
                            .size());
                }
            });
        }

        for (int i = 0; i < filePathList.size(); i++) {
            final String path = filePathList.get(i);
            LogUtils.i("attachcurrentThread", path);
            completionService.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    String thumbPath = BitmapHelper.createThumbBitmap(path);
                    String attachId = UploadHelper
                            .uploadFileGetAttachId(new File(thumbPath));
                    LogUtils.i("attachcurrentThread", Thread.currentThread()
                            .getName() + "---->" + attachId);
                    return attachId;
                }
            });
        }

        for (int i = 0; i < filePathList.size(); i++) {
            String attachId;
            try {
                attachId = completionService.take().get();
                if (!TextUtils.isEmpty(attachId)) {
                    sbAttachIds.append(attachId).append(",");
                    count++;
                    final int resultCount = count;
                    if (onUploadMultipleFileListener != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                onUploadMultipleFileListener
                                        .onProgressUpdate(resultCount);
                            }
                        });
                    }
                }
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (ExecutionException e1) {
                e1.printStackTrace();
            }
        }
        threadPool.shutdown();
        String attachIds = sbAttachIds.toString();

        if (!TextUtils.isEmpty(attachIds) && attachIds.endsWith(",")) {
            attachIds = attachIds.substring(0, attachIds.length() - 1);
        }

        final String resultIds = attachIds;

        if (onUploadMultipleFileListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onUploadMultipleFileListener.onComplete(resultIds);
                }
            });
        }
    }

}
