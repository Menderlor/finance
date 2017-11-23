package com.cedarhd.helpers.server;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.cedarhd.ClientConstactNewActivity;
import com.cedarhd.ClientInfoActivity;
import com.cedarhd.CompanySpaceActivity.HandlerNewContact;
import com.cedarhd.CompanySpaceNewActivity;
import com.cedarhd.NoticeListActivity;
import com.cedarhd.NoticeNewActivity;
import com.cedarhd.OrderActivity;
import com.cedarhd.OrderListActivity;
import com.cedarhd.SettingAvatarActivity;
import com.cedarhd.SignActivity.SendReasonHandler;
import com.cedarhd.SuggestNewActivity;
import com.cedarhd.TagActivity;
import com.cedarhd.TaskListActivityNew;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.helpers.BitmapHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.UploadHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.Attach;
import com.cedarhd.models.Categray;
import com.cedarhd.models.Client;
import com.cedarhd.models.Data;
import com.cedarhd.models.Demand;
import com.cedarhd.models.Dict;
import com.cedarhd.models.FormInfo;
import com.cedarhd.models.Notifications;
import com.cedarhd.models.PhotoInfo;
import com.cedarhd.models.Remind;
import com.cedarhd.models.ReturnModel;
import com.cedarhd.models.User;
import com.cedarhd.models.changhui.CH客户联系记录;
import com.cedarhd.models.changhui.工作总结报告;
import com.cedarhd.models.任务;
import com.cedarhd.models.任务分类;
import com.cedarhd.models.动态;
import com.cedarhd.models.动态已提醒;
import com.cedarhd.models.周工作总结;
import com.cedarhd.models.字典;
import com.cedarhd.models.字段描述;
import com.cedarhd.models.客户联系记录;
import com.cedarhd.models.帖子;
import com.cedarhd.models.日志;
import com.cedarhd.models.日志评论;
import com.cedarhd.models.流程;
import com.cedarhd.models.测量信息;
import com.cedarhd.models.考勤信息;
import com.cedarhd.models.联系人;
import com.cedarhd.models.订单;
import com.cedarhd.models.论坛回帖;
import com.cedarhd.models.评论;
import com.cedarhd.models.通知;
import com.cedarhd.models.邮件;
import com.cedarhd.models.部门;
import com.cedarhd.models.销售机会;
import com.cedarhd.models.问题反馈;
import com.cedarhd.services.NotificationService.RemindHandler;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.Where;
import com.squareup.okhttp.Request;
import com.tencent.android.tpush.XGPushConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 * 主要用于访问网络，调用接口
 * <p/>
 * 由于时间久远，累计了不少废弃接口，重复方法的调用
 *
 * @author K
 */
@SuppressLint("NewApi")
public class ZLServiceHelper {
    private static final String TAG = "ZLServiceHelper";
    public JSONArray jArr;
    public JSONObject jobj;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    HttpUtils mHttpUtils = new HttpUtils();

    /**
     * 获取当天日志
     */
    public 日志 getTodaykLog() {
        日志 log日志 = null;
        String methodName = "Log/GetTodayLog/";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("GetTodayLog", url);
        List<日志> list = null;
        String data = "";
        try {
            data = mHttpUtils.httpGet(url);
            LogUtils.i("GetTodayLog", "------>data:" + data);
            if (data != null && !data.equals("网络错误")) {
                list = JsonUtils.ConvertJsonToList(data, 日志.class);
                LogUtils.i("GetTodayLog", "------>size:" + list.size());
                if (list != null && list.size() > 0) {
                    log日志 = list.get(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("keno2Exception", "------>Exception:" + e.getMessage());
        }
        return log日志;
    }

    /**
     * 根据指定id查询本地对应的日志
     *
     * @param id
     * @param context
     * @return 没有查到则返回null
     */
    public 日志 getWorkLogById(String id, Context context) {
        日志 item日志 = null;
        try {
            ORMDataHelper helper = ORMDataHelper.getInstance(context);
            Dao<日志, Integer> dao = helper.getDao(日志.class);
            List<日志> list = dao.queryBuilder().where().eq("Id", id).query();
            if (list != null && list.size() > 0) {
                LogUtils.i("notificaitonCo", "list.size()--" + list.size());
                item日志 = list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("keno2Exception", "------>Exception:" + e.getMessage());
        }
        return item日志;
    }

    /**
     * 日志修改，写日志
     */
    public boolean UpdateLog(String log, String planContent) {
        Boolean b = false;
        String methodName = "Log/UpdateLog";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        try {
            JSONObject jo = new JSONObject();
            jo.put("Content", log);
            jo.put("PlanContent", planContent);
            LogUtils.i("keno3", url);
            String result = mHttpUtils.postSubmit(url, jo);
            result = JsonUtils.parseStatus(result);
            LogUtils.i("keno3", "RESULT" + result);
            if ("1".equals(result)) {
                b = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("keno3", "RESULT" + e);
        }
        return b;
    }

    /**
     * 用户头像修改
     */
    public void updateAvatar(Attach attach, Context context, Handler handler) {
        String methodName = "Account/UpdateAvatar/";
        // String Parameter = avatarUrl;
        String requestUrl = Global.BASE_URL + Global.EXTENSION + methodName
                + "/" + attach.Id;
        Message message = new Message();
        // 创建一个服务器端日志实体类收的的json object
        try {
            JSONObject jo = new JSONObject();
            // jo.put("Id", Global.mUser.Id);
            // jo.put("AttachId", attach.Id);
            // String address = mHttpUtils.postSubmit(requestUrl, jo);
            LogUtils.d("uploadresult_url", requestUrl);
            String result = mHttpUtils.httpGet(requestUrl);
            LogUtils.i("uploadresult", result);
            if (result.contains("true")) {
                int num = updateAvatarDB(attach.Id + "", attach.Address,
                        context, handler);
                if (num == 1) {
                    message.what = SettingAvatarActivity.HandlerSetting.UPLOAD_SUCCESS;
                }
            } else {
                message.what = SettingAvatarActivity.HandlerSetting.UPLOAD_FAILURE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.what = SettingAvatarActivity.HandlerSetting.UPLOAD_FAILURE;
        }
        handler.sendMessage(message);
    }

    /**
     * 用户头像修改,修改本地数据库
     */
    private int updateAvatarDB(String AttachId, String Address,
                               Context context, Handler handler) {
        ORMDataHelper helper = ORMDataHelper.getInstance(context);
        Dao<User, Integer> dao = helper.getUserDao();
        User user = new User();
        user.setId(Global.mUser.Id);
        try {
            List<User> list = dao.queryForMatching(user);
            if (list.size() > 0) {
                user = list.get(0);
            }
            user.setAvatar(AttachId);
            user.setAvatarURI(Address);
            int num = dao.update(user);
            return num;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 客户联系记录修改新建（无附件）
     */
    public void updateCutomerContactRecord(客户联系记录 item, Handler handler) {
        String methodName = "Customer/EditCutomerContactRecord/";
        String requestUrl = Global.BASE_URL + Global.EXTENSION + methodName;
        Message message = new Message();
        // 创建一个服务器端日志实体类收的的json object
        try {
            JSONObject jo = new JSONObject();
            jo.put("Id", item.getId());
            jo.put("Saler", item.getSaler());
            jo.put("Customer", item.getCustomer());
            jo.put("ChanceId", item.getChanceId());
            jo.put("AgreementId", item.getAgreementId());
            jo.put("ProjectId", item.getProjectId());
            jo.put("Status", item.getStatus());
            jo.put("Contacts", item.getContacts());
            jo.put("Content", item.getContent());
            jo.put("Remarks", item.getRemarks());
            jo.put("UpdateTime", item.getUpdateTime());
            jo.put("LastProcessTime", item.getLastProcessTime());
            jo.put("Preparer", item.getPreparer());
            jo.put("PrepareTime", item.getPrepareTime());
            jo.put("Attachments", item.getAttachments());
            String result = mHttpUtils.postSubmit(requestUrl, jo);
            LogUtils.i("keno8", result);
            // // 解析提交返回的数据
            // JSONObject jsonObject = new JSONObject(result);
            // result = jsonObject.get("Data").toString();
            // result = result.substring(1, result.length() - 1);
            if (result != null) {
                message.what = ClientConstactNewActivity.HandlerNewContact.UPDATE_Contact_SUCCESS;
            } else {
                message.what = ClientConstactNewActivity.HandlerNewContact.UPDATE_Contact_FAILED;
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.what = ClientConstactNewActivity.HandlerNewContact.UPDATE_Contact_FAILED;
        }
        handler.sendMessage(message);
    }

    // TODO

    /**
     * 客户联系记录修改新建（附件）
     *
     * @param item          客户联系记录
     * @param handler
     * @param photoPathList 图像地址列表
     * @param pBar          进度条
     */
    public void updateCutomerContactRecord(客户联系记录 item, Handler handler,
                                           List<String> photoPathList, ProgressBar pBar) {
        String methodName = "Customer/EditCutomerContactRecord/";
        String requestUrl = Global.BASE_URL + Global.EXTENSION + methodName;
        Message message = new Message();
        // 创建一个服务器端日志实体类收的的json object
        try {
            String attachMent = item.getAttachments();
            if (photoPathList != null) {
                if (photoPathList.size() > 0) {
                    attachMent = uploadAttachPhotos(photoPathList, pBar);
                    LogUtils.i("Attachments", photoPathList.get(0));
                    LogUtils.i("Attachments", attachMent);
                    item.Attachments = attachMent;
                }
            }
            JSONObject jo = JsonUtils.initJsonObj(item);
            String result = mHttpUtils.postSubmit(requestUrl, jo);
            String status = JsonUtils.parseStatus(result);
            LogUtils.i("keno8", "result：" + result);
            if ("1".equals(status)) {
                // 成功
                message.what = 2;
            } else {
                message.what = 3;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("update", "" + e);
            message.what = 3;
        }
        handler.sendMessage(message);
    }

    /**
     * 客户联系记录修改新建（附件）
     *
     * @param item          客户联系记录
     * @param handler
     * @param photoPathList 图像地址列表
     * @param pBar          进度条
     */
    public void updateCutomerContactRecord(final CH客户联系记录 item,
                                           Handler handler, List<String> photoPathList, ProgressBar pBar) {
        String methodName = "Customer/SaveCustomerContactRecord/";
        String requestUrl = Global.BASE_URL + Global.EXTENSION + methodName;
        Message message = new Message();
        // 创建一个服务器端日志实体类收的的json object
        try {
            String attachMent = item.附件;
            if (photoPathList != null) {
                if (photoPathList.size() > 0) {
                    attachMent = uploadAttachPhotos(photoPathList, pBar);
                    LogUtils.i("Attachments", photoPathList.get(0));
                    LogUtils.i("Attachments", attachMent);
                    item.附件 = attachMent;
                }
            }
            JSONObject jo = JsonUtils.initJsonObj(item);
            String result = mHttpUtils.postSubmit(requestUrl, jo);
            String status = JsonUtils.parseStatus(result);
            LogUtils.i("keno8", "result：" + result);
            if ("1".equals(status)) {
                // 成功
                message.what = 2;
            } else {
                message.what = 3;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("update", "" + e);
            message.what = 3;
        }
        handler.sendMessage(message);
    }

    /**
     * 发布任务
     *
     * @param item    任务对象
     * @param handler
     * @param pBar    进度条
     * @return
     */
    public void PublishTask(任务 item, List<String> photoPathList,
                            Handler handler, ProgressBar pBar) {
        // String methodName = "Task/AddTask";
        String methodName = "Task/AddTaskToExecutors";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = "";
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("Id", item.Id);
            jo2.put("CorpId", Global.mUser.CorpId);
            jo2.put("UserId", Global.mUser.Id);
            // jo2.put("Id", map.get("Id").toString());
            jo2.put("Publisher", Global.mUser.Id); // 发布人
            jo2.put("Executor", item.Executor); // 任务执行人默认就是自己
            jo2.put("ExecutorList", item.ExecutorList); // 任务执行人默认就是自己
            String participant = item.getParticipant();
            participant = participant.replace("'", "").replace(";", ",");
            jo2.put("Participant", participant);
            jo2.put("Title", item.getTitle());
            jo2.put("Content", item.getContent());
            jo2.put("Time", item.Time); // 发布时间(当前日期)
            jo2.put("AssignTime", item.getAssignTime()); // 开始时间
            LogUtils.i("pynewtasktime", item.getAssignTime());
            jo2.put("Deadline", item.getAssignTime()); // 指定完成时间
            jo2.put("Status", item.Status); // 默认为启动状态
            jo2.put("ClientId", item.ClientId);
            jo2.put("Categroy", item.Categroy);// 任务分类id
            LogUtils.i("pyCategroynew", "Categroy:" + item.Categroy);
            LogUtils.i("addtask", item.toString());
            if (photoPathList != null && photoPathList.size() > 0) {
                String attachMent = uploadAttachPhotos(photoPathList, pBar);
                LogUtils.i("Attachment", photoPathList.get(0));
                LogUtils.i("Attachment", attachMent);
                jo2.put("Attachment", attachMent); // 附件
            }
            strResp = mHttpUtils.postSubmit(url, jo2);
            LogUtils.e("kjx", "发布结果：" + strResp);
            // 解析提交返回的数据
            JSONObject jsonObject = new JSONObject(strResp);
            strResp = jsonObject.get("Data").toString();
            strResp = strResp.substring(1, strResp.length() - 1);
            LogUtils.i("kjx", "data：" + strResp);
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(6);
        }
        if (strResp.equals("true")) {
            handler.sendEmptyMessage(5);
        } else {
            handler.sendEmptyMessage(6);
        }
    }

    /**
     * 修改任务
     *
     * @param item 任务对象
     * @return
     */
    public boolean EditTask(任务 item) {
        boolean isSuccess = false;
        String methodName = "Task/EditTask";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = "";
        JSONObject jo2;
        try {
            jo2 = JsonUtils.initJsonObj(item);
            LogUtils.i(TAG, "jo2：" + jo2.toString());
            strResp = mHttpUtils.postSubmit(url, jo2);
            LogUtils.i("kjx", "发布结果：" + strResp);
            if (!strResp.equals("0")) {
                isSuccess = true;
            }
        } catch (IllegalArgumentException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isSuccess;
    }

    /**
     * 修改任务
     *
     * @param item 任务对象
     * @return
     */
    public boolean AddTaskToExecutors(任务 item) {
        boolean isSuccess = false;
        String methodName = "Task/AddTaskToExecutors";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = "";
        JSONObject jo2;
        try {
            jo2 = JsonUtils.initJsonObj(item);
            LogUtils.i(TAG, "jo2：" + jo2.toString());
            strResp = mHttpUtils.postSubmit(url, jo2);
            LogUtils.i("kjx", "发布结果：" + strResp);
            if (!strResp.equals("0")) {
                isSuccess = true;
            }
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    /**
     * 删除任务
     *
     * @param taskId 任务编号
     * @return
     */

    public boolean deleteTask(int taskId) {
        boolean isSuccess = false;
        String methodName = "Task/DeleteTask/" + taskId;
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = "";
        try {
            strResp = mHttpUtils.httpGet(url);
            LogUtils.i("kjx", "发布结果：" + strResp);
            String status = JsonUtils.parseStatus(strResp);
            if ("1".equals(status)) {
                isSuccess = true;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return isSuccess;
    }

    /**
     * 上传附件一组图片
     *
     * @param filePathList
     * @return 返回一组上传图片的附件号
     * @author KJX
     */
    public List<Attach> uploadAttachPhotos(List<String> filePathList) {
        List<Attach> attchList = new ArrayList<Attach>();
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        /**
         * 将生产新的异步任务与使用已完成任务的结果分离开来的服务。生产者 submit 执行的任务。使用者 take
         * 已完成的任务，并按照完成这些任务的顺序处理它们的结果。
         */
        CompletionService<Attach> completionService = new ExecutorCompletionService<Attach>(
                threadPool);
        for (int i = 0; i < filePathList.size(); i++) {
            final String path = filePathList.get(i);
            LogUtils.i("attachcurrentThread", path);
            completionService.submit(new Callable<Attach>() {
                @Override
                public Attach call() throws Exception {
                    String thumbPath = BitmapHelper.createThumbBitmap(path);
                    Attach attach = UploadHelper
                            .uploadFileByHttpGetAttach(new File(thumbPath));
                    LogUtils.i("attachcurrentThread", Thread.currentThread()
                            .getName() + "---->" + attach.Id);
                    return attach;
                }
            });
        }

        for (int i = 0; i < filePathList.size(); i++) {
            Attach attach;
            try {
                attach = completionService.take().get();
                if (attach != null) {
                    attchList.add(attach);
                }
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (ExecutionException e1) {
                e1.printStackTrace();
            }
        }
        threadPool.shutdown();
        // String attachIds = sbAttachIds.toString();
        // if (!TextUtils.isEmpty(attachIds) && attachIds.endsWith(",")) {
        // attachIds = attachIds.substring(0, attachIds.length() - 1);
        // }
        return attchList;
    }

    /**
     * 上传附件一组图片
     *
     * @param filePathList
     * @return 返回一组上传图片的附件号
     * @author KJX
     */
    public String uploadAttachPhotos(List<String> filePathList, ProgressBar pBar) {
        final StringBuilder sbAttachIds = new StringBuilder(); // 存储照片上传后返回的附件号
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        /**
         * 将生产新的异步任务与使用已完成任务的结果分离开来的服务。生产者 submit 执行的任务。使用者 take
         * 已完成的任务，并按照完成这些任务的顺序处理它们的结果。
         */
        CompletionService<String> completionService = new ExecutorCompletionService<String>(
                threadPool);
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
                    if (pBar != null) {
                        pBar.incrementProgressBy(1);
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
        return attachIds;
    }

    /**
     * 上传附件一张图片
     *
     * @param filePath 上传文件的绝对路径
     * @param pBar     显示上传进度
     * @return 返回上传图片的附件号
     */
    public String uploadAttachPhoto(final String filePath, ProgressBar pBar) {
        if (pBar != null) {
            pBar.setVisibility(View.VISIBLE);
        }
        String result = "";
        LogUtils.i("onActivity_upload", "filePath---------" + filePath);
        File file = new File(filePath);
        LogUtils.i("onActivity_upload", "长度---------" + file.getTotalSpace());
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        /**
         * 将生产新的异步任务与使用已完成任务的结果分离开来的服务。生产者 submit 执行的任务。使用者 take
         * 已完成的任务，并按照完成这些任务的顺序处理它们的结果。
         */
        CompletionService<String> completionService = new ExecutorCompletionService<String>(
                threadPool);
        completionService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String thumbPath = BitmapHelper.createThumbBitmap(filePath);
                String attachId = UploadHelper.uploadFileGetAttachId(new File(
                        thumbPath));
                LogUtils.i("attachcurrentThread", "attachId=" + attachId);
                return attachId;
            }
        });
        try {
            // 取得执行结果
            result = completionService.take().get();
            result = result.replaceAll("\"", "");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        threadPool.shutdown();

        if (pBar != null) {
            pBar.setVisibility(View.GONE);
        }
        return result;
    }

    /**
     * 修改任务状态
     *
     * @param taskId
     * @param handler
     */
    public void updateTask(int taskId, int statutsId, Handler handler) {
        String methodName = "Task/UpdateTask/";
        String Parameter = taskId + "/" + statutsId;
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        LogUtils.i("keno2", url);
        Message message = new Message();
        // 创建一个服务器端日志实体类收的的json object
        try {
            JSONObject jo = new JSONObject();
            // jo.put("Id", 0);
            // jo.put("TaskId", taskId);
            // jo.put("StatusId", statutsId);
            LogUtils.i("keno", url);
            String result = mHttpUtils.postSubmit(url, jo);
            // 解析提交返回的数据
            JSONObject jsonObject = new JSONObject(result);
            result = jsonObject.get("Data").toString();
            result = result.substring(1, result.length() - 1);
            LogUtils.i("keno3", "RESULT" + result);
            if (result != null && !result.equals("网络错误")) {
                Boolean b = new Boolean(result);
                if (b.booleanValue()) {
                    message.what = TaskListActivityNew.HandlerTaskList.UPDATE_TASK_SUCCESS;
                } else {
                    message.what = TaskListActivityNew.HandlerTaskList.UPDATE_TASK_FAILED;
                }
            } else {
                message.what = TaskListActivityNew.HandlerTaskList.UPDATE_TASK_FAILED;
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.what = TaskListActivityNew.HandlerTaskList.UPDATE_TASK_FAILED;
        }
        handler.sendMessage(message);
    }

    /**
     * 修改任务状态
     *
     * @param taskId
     * @param statutsId 任务状态
     */
    public void UpdateTaskStatus(int taskId, int statutsId) {
        String methodName = "Task/UpdateTaskStatus/";
        String Parameter = taskId + "/" + statutsId;
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        LogUtils.i("keno2", url);
        Message message = new Message();
        // 创建一个服务器端日志实体类收的的json object
        try {
            LogUtils.i("keno", url);
            String result = mHttpUtils.httpGet(url);
            // 解析提交返回的数据
            JSONObject jsonObject = new JSONObject(result);
            result = jsonObject.get("Data").toString();
            result = result.substring(1, result.length() - 1);
            LogUtils.i("keno3", "RESULT" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 读通知 同时跟新服务器和本地数据
     *
     * @param notice  通知实体
     * @param context 上下文
     * @return
     */
    public void ReadNotice(通知 notice, Context context, Handler handler) {
        String methodName = "Notice/ReadNotice";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = null;
        try {
            JSONObject jo = new JSONObject();
            jo.put("Id", notice.Id);
            jo.put("CorpId", Global.mUser.CorpId);
            jo.put("UserId", Global.mUser.Id);

            strResp = mHttpUtils.postSubmit(url, jo);

            // true or false标志当前用户已读或未读
            if (!TextUtils.isEmpty(notice.Read)
                    || notice.Read.trim().equalsIgnoreCase("0")) {
                notice.setRead("1");
            }
            // 现在不需要本地数据
            // ORMDataHelper helper = ORMDataHelper.getInstance(context);
            // Dao<通知, Integer> dao = helper.getDao(通知.class);
            // int num = dao.update(notice);
            // LogUtils.i("keno0", "修改num----》" + num);
            // if (num > 0) {
            //
            // }
            handler.sendEmptyMessage(NoticeListActivity.SUCESS_READ_NOTICE);
        } catch (Exception e) {
            LogUtils.e("设置通知为已查看失败。", "网络问题。");
        }
    }

    /***
     * 读通知 跟新服务器为已读
     *
     * @param notice  通知实体
     * @param context 上下文
     * @return
     */
    public void ReadNotice(通知 notice, Context context) {
        String methodName = "Notice/ReadNotice";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = null;
        try {
            JSONObject jo = new JSONObject();
            jo.put("Id", notice.Id);
            jo.put("CorpId", Global.mUser.CorpId);
            jo.put("UserId", Global.mUser.Id);
            strResp = mHttpUtils.postSubmit(url, jo);

            // true or false标志当前用户已读或未读
            if (!TextUtils.isEmpty(notice.Read)
                    || notice.Read.trim().equalsIgnoreCase("0")) {
                notice.setRead("1");
            }
        } catch (Exception e) {
            LogUtils.e("设置通知为已查看失败。", "网络问题。" + e);
        }
    }

    /***
     * 读日志 同时跟新服务器和本地数据
     *
     * @param item    日志
     * @param context 上下文
     * @return
     */
    public void ReadLog(日志 item, Context context, Handler handler) {
        String methodName = "Log/ReadLog";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;

        String strResp = null;
        try {
            JSONObject jo = new JSONObject();
            jo.put("Id", item.Id);

            strResp = mHttpUtils.postSubmit(url, jo);

            // 修改本地数据库
            String read = item.Readed == null ? "" : item.Readed;

            if (!read.contains(Global.mUser.Id)) {
                item.Readed = Global.mUser.Id + "";
            }

            // if (!read.equals(Global.mUser.Id)) { // 未读
            // item.Readed = Global.mUser.Id;
            // ORMDataHelper helper = ORMDataHelper.getInstance(context);
            // Dao<日志, Integer> dao = helper.getDao(日志.class);
            // int num = dao.update(item);
            // LogUtils.i("keno0", "修改num----》" + num);
            // if (num > 0) {
            // handler.sendEmptyMessage(3);// 修改日志成功
            // }
            // }
            handler.sendEmptyMessage(3);// 修改日志成功
        } catch (Exception e) {
            LogUtils.e("readErro", "设置日志已读失败" + e);
        }
    }

    /***
     * 读日志 跟新服务器,设置日志为已读
     *
     * @param item    日志
     * @param context 上下文
     * @return
     */
    public void ReadLog(日志 item, Context context) {
        String methodName = "Log/ReadLog";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;

        String strResp = null;
        try {
            JSONObject jo = new JSONObject();
            jo.put("Id", item.Id);
            strResp = mHttpUtils.postSubmit(url, jo);
        } catch (Exception e) {
            LogUtils.e("readErro", "设置日志已读失败" + e);
        }
    }

    /**
     * 读流程
     */
    public Boolean ReadFlow(流程 flow, Context context) {
        // 3.12日，目前地址未完成
        String methodName = "Notice/ReadNotice";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;

        String strResp = null;
        try {
            JSONObject jo = new JSONObject();
            jo.put("Id", flow.Id);
            jo.put("CorpId", Global.mUser.CorpId);
            jo.put("UserId", Global.mUser.Id);

            strResp = mHttpUtils.postSubmit(url, jo);
            strResp = "true";// 测试用
            if (strResp == null) {
                return false;
            }

            if (flow.Read == null || !flow.Read.contains(Global.mUser.Id)) {
                flow.Read = flow.Read + "'" + Global.mUser.Id + "';";
            }
            ORMDataHelper helper = ORMDataHelper.getInstance(context);
            Dao<流程, Integer> dao = helper.getDao(流程.class);
            int num = dao.update(flow);
            LogUtils.i("keno0", "修改num----》" + num);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtils.e("设置流程为已查看失败。", "网络问题。");
        }

        return new Boolean(strResp);
    }

    /**
     * 读任务 同时跟新服务器和本地数据
     *
     * @param notice  通知实体
     * @param context 上下文
     * @return
     */
    public void ReadTask(任务 task, Context context, Handler handler) {
        String methodName = "Task/ReadTask";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;

        String strResp = null;
        try {
            JSONObject jo = new JSONObject();
            jo.put("Id", task.Id);
            jo.put("CorpId", Global.mUser.CorpId);
            jo.put("UserId", Global.mUser.Id);
            strResp = mHttpUtils.postSubmit(url, jo);

            ORMDataHelper helper = ORMDataHelper.getInstance(context);
            Dao<任务, Integer> dao = helper.getDao(任务.class);
            int num = dao.update(task);
            handler.sendEmptyMessage(3);
            LogUtils.i("keno0", "修改num----》" + num);
        } catch (Exception e) {
            LogUtils.e("设置任务为已查看失败。", "网络问题。");
        }

        if (task.ReadTime == null || !task.ReadTime.contains(Global.mUser.Id)) {
            task.ReadTime = task.ReadTime + "," + Global.mUser.Id + ",";
        }
        ORMDataHelper helper = ORMDataHelper.getInstance(context);
        Dao<任务, Integer> dao;
        try {
            dao = helper.getDao(任务.class);
            int num = dao.update(task);

            LogUtils.i("keno0", "修改num----》" + num);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 读任务，更新服务器设置任务为已读
     *
     * @param notice  任务实体
     * @param context 当前上下文
     * @return
     */
    public void ReadTask(任务 task, Context context) {
        String methodName = "Task/ReadTask";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;

        String strResp = null;
        try {
            JSONObject jo = new JSONObject();
            jo.put("Id", task.Id);
            jo.put("CorpId", Global.mUser.CorpId);
            jo.put("UserId", Global.mUser.Id);
            strResp = mHttpUtils.postSubmit(url, jo);
        } catch (Exception e) {
            LogUtils.e("设置任务为已查看失败。", "网络问题。");
        }
    }

    /**
     * 读订单
     *
     * @param notice  通知实体
     * @param context 上下文
     * @return
     */
    public void ReadOrder(订单 item, Context context, Handler handler) {
        String methodName = "Cabinet/ReadCabinet";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = null;
        try {
            LogUtils.d("resultkjx", url);
            JSONObject jo = new JSONObject();
            jo.put("OrderNo", item.Id); //
            strResp = mHttpUtils.postSubmit(url, jo);
            LogUtils.i("resultkjx", "编号:" + item.Id + "-->" + strResp);

            // 修改本地数据库
            String read = item.Readed;
            String userId = Global.mUser.Id;
            String leftUserId = "," + userId;
            String rightUserId = userId + ",";
            if (TextUtils.isEmpty(read)
                    || (!read.contains(leftUserId) && !read
                    .contains(rightUserId))) {
                if (TextUtils.isEmpty(read)) {
                    item.Readed = "";
                }
                LogUtils.i("keno0", "修改num----》" + item.Readed);
                item.Readed = item.Readed + Global.mUser.Id + ",";
            }
            ORMDataHelper helper = ORMDataHelper.getInstance(context);
            Dao<订单, Integer> dao;
            try {
                dao = helper.getDao(订单.class);
                int num = dao.update(item);
                if (num > 0) {
                    handler.sendEmptyMessage(OrderListActivity.SUCESS_READED);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            LogUtils.e("设置任务为已查看失败。", "网络问题。" + e);
        }
    }

    /**
     * 读客户列表
     *
     * @param item    客户
     * @param context 上下文
     * @return
     */
    public void ReadClient(Client item, Context context, Handler handler) {
        String methodName = "Customer/ReadCustomer";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = null;
        try {
            LogUtils.d("resultkjx", url);
            JSONObject jo = new JSONObject();
            jo.put("Id", item.getId()); //
            strResp = mHttpUtils.postSubmit(url, jo);
            LogUtils.i("resultkjx", "编号:" + item.getId() + "-->" + strResp);
        } catch (Exception e) {
            LogUtils.e("readErro", "设置客户已读失败" + e);
        }

        // 修改本地数据库
        String read = item.Readed == null ? "" : item.Readed;
        if (!read.equals(Global.mUser.Id)) {
            item.Readed = Global.mUser.Id;
            ORMDataHelper helper = ORMDataHelper.getInstance(context);
            Dao<Client, Integer> dao;
            try {
                dao = helper.getDao(Client.class);
                int num = dao.update(item);
                LogUtils.i("keno0", "修改num----》" + num);
                if (num > 0) {
                    handler.sendEmptyMessage(3);// 设置客户信息为已读成功
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        LogUtils.i("keno0", "修改num----》" + item.Readed);
    }

    /**
     * 读客户列表,更新服务器客户为已读
     *
     * @param item    客户
     * @param context 上下文
     * @return
     */
    public void ReadClient(Client item, Context context) {
        String methodName = "Customer/ReadCustomer";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = null;
        try {
            LogUtils.d("resultkjx", url);
            JSONObject jo = new JSONObject();
            jo.put("Id", item.getId());
            strResp = mHttpUtils.postSubmit(url, jo);
            LogUtils.i("resultkjx", "编号:" + item.getId() + "-->" + strResp);
        } catch (Exception e) {
            LogUtils.e("readErro", "设置客户已读失败" + e);
        }

    }

    /**
     * 读联系记录
     *
     * @param item    联系记录
     * @param context 上下文
     * @return
     */
    public void ReadContacts(客户联系记录 item, Context context, Handler handler) {
        String methodName = "Customer/ReadContact";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = null;
        try {
            JSONObject jo = new JSONObject();
            jo.put("Id", item.getId()); //
            strResp = mHttpUtils.postSubmit(url, jo);
            LogUtils.i("resultkjx", "编号:" + item.getId() + "-->" + strResp);
        } catch (Exception e) {
            LogUtils.e("readErro", "设置客户已读失败" + e);
        }
        // 修改本地数据库
        String read = item.Readed == null ? "" : item.Readed;
        if (!read.equals(Global.mUser.Id)) {
            item.Readed = Global.mUser.Id;
            ORMDataHelper helper = ORMDataHelper.getInstance(context);
            Dao<客户联系记录, Integer> dao;
            try {
                dao = helper.getDao(客户联系记录.class);
                int num = dao.update(item);
                LogUtils.i("keno0", "修改num----》" + num);
                if (num > 0) {
                    handler.sendEmptyMessage(3);// 修改日志成功
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读销售机会
     *
     * @param item    联系记录
     * @param context 上下文
     * @return
     */
    public void ReadSaleChance(销售机会 item, Context context) {
        String methodName = "Customer/ReadOpp";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = null;
        try {
            JSONObject jo = new JSONObject();
            jo.put("Id", item.getId()); //
            strResp = mHttpUtils.postSubmit(url, jo);
            LogUtils.i("resultkjx", "编号:" + item.getId() + "-->" + strResp);
        } catch (Exception e) {
            LogUtils.e("readErro", "设置客户已读失败" + e);
        }
        // 修改本地数据库
        String read = item.getRead();
        String userId = Global.mUser.Id;
        String leftUserId = "," + userId;
        String rightUserId = userId + ",";
        if (TextUtils.isEmpty(read)
                || (!read.contains(leftUserId) && !read.contains(rightUserId))) {
            if (TextUtils.isEmpty(read)) {
                item.setRead("");
            }
            LogUtils.i("keno0", "修改num----》" + item.getRead());
            item.setRead(item.getRead() + Global.mUser.Id + ",");
        }
        ORMDataHelper helper = ORMDataHelper.getInstance(context);
        Dao<销售机会, Integer> dao;
        try {
            dao = helper.getDao(销售机会.class);
            dao.update(item);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新建客户
     */
    public boolean NewCustomer(String name, String contacts, String industry,
                               String address, String province, String city, String phone) {
        // String md5 = ByteUtil.md5One();
        String methodName = "ChangeCustomerInfo";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;

        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("CorpId", Global.mUser.CorpId);
            jo2.put("UserId", Global.mUser.Id);
            jo2.put("CustomerName", name);
            jo2.put("Contacts", contacts);
            jo2.put("TradeName", industry);
            jo2.put("Address", address);
            jo2.put("ProvinceName", province);
            jo2.put("CityName", city);
            jo2.put("Phone", phone);
            // jo2.put("RegisterTime", registerDate);

            try {
                strResp = mHttpUtils.post(url, jo2);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (strResp == null) {
                return false;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new Boolean(strResp);
    }

    /**
     * 修改客户
     *
     * @param id
     * @return
     * @author k
     */
    public void updateCustomer(Client item, Handler handler) {
        String methodName = "customer/EditCustomer/";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("keno09", url);
        String data = "";
        try {
            JSONObject jo = new JSONObject();
            jo.put("Id", item.getId());
            jo.put("CustomerName", item.getCustomerName());
            jo.put("Contacts", item.getContacts());
            LogUtils.i("keno110", "---->Salesman:" + item.getSalesman());
            jo.put("Salesman", item.getSalesman());
            jo.put("RegisterTime", item.getRegisterTime());
            jo.put("Address", item.getAddress());
            jo.put("Phone", item.getPhone());
            jo.put("LastContactDate", item.getLastContactDate());
            jo.put("UpdateTime", item.getUpdateTime());
            // 返回结果为客户id
            data = mHttpUtils.postSubmit(url, jo);
            int id = Integer.parseInt(data);
            if (item.getId() != 0) {
                // 修改客户信息
                if (item.getId() == id) {
                    LogUtils.i("keno19", "修改成功：" + data);
                    handler.sendEmptyMessage(ClientInfoActivity.SUCCESS_UPDATE);
                }
            } else {// 新建成功，返回客户id
                if (id != 0) {
                    handler.sendEmptyMessage(ClientInfoActivity.SUCCESS_NEW);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("keno19", "-->" + e);
            handler.sendEmptyMessage(ClientInfoActivity.FAILURE_UPDATE);
        }
    }

    /**
     * 修改客户，分配业务员
     *
     * @param Id      客户ID
     * @param salerId 业务员ID
     * @return
     */
    public boolean updateSaler(int Id, int salerId) {
        String methodName = "/ChangeCustomerInfo/";
        String param = Id + "/Saler/" + salerId;
        String url = Global.BASE_URL + Global.EXTENSION + methodName + param;
        LogUtils.i("keno09", url);
        String data = "";
        try {
            JSONObject jo = new JSONObject();
            jo.put("Id", Id);
            jo.put("Saler", salerId);
            data = mHttpUtils.postSubmit(url, jo);
            LogUtils.e("keno09", data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Boolean(data);
    }

    // 删除客户
    public boolean deleteCustomer(int id) {
        boolean flag = false;

        String methodName = "删除客户/";
        String Parameter = id + "/" + Global.mUser.Id + "/"
                + Global.mUser.CorpId;
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        String data = "";
        try {
            data = mHttpUtils.get(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Boolean(data);
    }

    /**
     * 新建销售机会
     */
    public boolean NewSalesChance(int customerId, String name, String industry,
                                  String address, String province, String city, String phone,
                                  String content) {
        // String md5 = ByteUtil.md5One();
        String methodName = "新建销售机会";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;

        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("CorpId", Global.mUser.CorpId);
            jo2.put("UserId", Global.mUser.Id);
            jo2.put("CustomerId", customerId);
            jo2.put("CustomerName", name);
            jo2.put("TradeName", industry);
            jo2.put("Address", address);
            jo2.put("ProvinceName", province);
            jo2.put("CityName", city);
            jo2.put("Phone", phone);
            // jo2.put("RegisterTime", registerDate);
            jo2.put("Content", content);

            try {
                strResp = mHttpUtils.post(url, jo2);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (strResp == null) {
                return false;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new Boolean(strResp);
    }

    public boolean NewSalesChance(int customerId, String content) {
        // String md5 = ByteUtil.md5One();
        String methodName = "新建销售机会_新";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;

        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("CorpId", Global.mUser.CorpId);
            jo2.put("UserId", Global.mUser.Id);
            jo2.put("CustomerId", customerId);
            // jo2.put("RegisterTime", registerDate);
            jo2.put("Content", content);

            try {
                strResp = mHttpUtils.post(url, jo2);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (strResp == null) {
                return false;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new Boolean(strResp);
    }

    public boolean deleteSalesChance(int id) {
        boolean flag = false;

        String methodName = "删除销售机会/";
        String Parameter = id + "/" + Global.mUser.Id + "/"
                + Global.mUser.CorpId;
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        String data = "";
        try {
            data = mHttpUtils.get(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Boolean(data);
    }

    /**
     * 新建工作计划
     */
    public boolean NewWorkPlan(int customerId, int salesChanceId,
                               String userId, String planTime, String classification, int state,
                               String content) {
        // String md5 = ByteUtil.md5One();
        String methodName = "新建工作计划";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;

        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("CorpId", Global.mUser.CorpId);
            jo2.put("UserId", Global.mUser.Id);
            jo2.put("CustomerId", customerId);
            jo2.put("SalesChanceId", salesChanceId);
            // jo2.put("Salesman", userId);
            jo2.put("PlanTime_str", planTime);
            jo2.put("Classification", classification);
            jo2.put("State", state);
            // jo2.put("RegisterTime", registerDate);
            jo2.put("Content", content);

            try {
                strResp = mHttpUtils.post(url, jo2);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (strResp == null) {
                return false;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new Boolean(strResp);
    }

    public boolean deleteWorkPlan(int workPlanId) {
        // TODO Auto-generated method stub
        boolean flag = false;

        String methodName = "删除工作计划/";
        String Parameter = workPlanId + "/" + Global.mUser.Id + "/"
                + Global.mUser.CorpId;
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        String data = "";
        try {
            data = mHttpUtils.get(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Boolean(data);
    }

    public boolean updateWorkPlanState(int workPlanId, int state) {
        // TODO Auto-generated method stub
        boolean flag = false;

        String methodName = "更新工作计划状态/";
        String Parameter = workPlanId + "/" + state + "/" + Global.mUser.Id
                + "/" + Global.mUser.CorpId;
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        String data = "";
        try {
            data = mHttpUtils.get(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Boolean(data);
    }

    /* 新建联系拜访记录 */
    public boolean NewContactHistory(int customerId, int salesChanceId,
                                     String time) {
        String methodName = "新建联系拜访记录";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;

        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("CorpId", Global.mUser.CorpId);
            jo2.put("UserId", Global.mUser.Id);
            jo2.put("CustomerId", customerId);
            // jo2.put("Salesman", userId);
            jo2.put("SalesChance", salesChanceId);
            // jo2.put("RegisterTime", registerDate);
            jo2.put("Time_str", time);

            try {
                strResp = mHttpUtils.post(url, jo2);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (strResp == null) {
                return false;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new Boolean(strResp);
    }

    public boolean deleteContactHistory(int contactHistoryId) {
        // TODO Auto-generated method stub
        boolean flag = false;

        String methodName = "删除联系拜访记录/";
        String Parameter = contactHistoryId + "/" + Global.mUser.Id + "/"
                + Global.mUser.CorpId;
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        String data = "";
        try {
            data = mHttpUtils.get(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Boolean(data);
    }

    public boolean NewContactHistoryDetail(int classification, String content,
                                           int contactHistoryId) {
        String methodName = "新建联系拜访记录明细";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;

        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("CorpId", Global.mUser.CorpId);
            jo2.put("UserId", Global.mUser.Id);
            jo2.put("Classification", classification);
            // jo2.put("Salesman", userId);
            jo2.put("Content", content);
            // jo2.put("RegisterTime", registerDate);
            jo2.put("ContactHistoryId", contactHistoryId);

            try {
                strResp = mHttpUtils.post(url, jo2);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (strResp == null) {
                return false;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new Boolean(strResp);
    }

    public boolean deleteContactHistoryDetail(int contactHistoryDetailId) {
        // TODO Auto-generated method stub
        boolean flag = false;

        String methodName = "删除联系拜访记录明细/";
        String Parameter = contactHistoryDetailId + "/" + Global.mUser.Id + "/"
                + Global.mUser.CorpId;
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        String data = "";
        try {
            data = mHttpUtils.get(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Boolean(data);
    }

    /**
     * 发布通知
     *
     * @param receiverId     接收人Id
     * @param title          标题
     * @param content        通知内容
     * @param ExpirationTime 发布时间
     * @return
     */
    public Boolean WriteNotice(String receiverId, String title, String content,
                               String ExpirationTime, String Departmant, boolean isAllUser,
                               String attachMnet, Handler handler, ProgressBar progressBar) {
        String methodName = "Notice/AddNotice";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("WriteNotice", url);
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("Publisher", Global.mUser.Id);
            jo2.put("Personnel", receiverId);
            jo2.put("Title", title);
            jo2.put("Content", content);
            jo2.put("Department", Departmant);
            jo2.put("isAllUser", isAllUser);
            jo2.put("Attachment", attachMnet); // 附件
            LogUtils.i("post_submit", receiverId + "---" + title + "---"
                    + content);
            LogUtils.d("post_notice", jo2.toString() + "");
//            if (photoPathList.size() > 0) {
//                String attachMent = uploadAttachPhotos(photoPathList,
//                        progressBar);
//                LogUtils.i("Attachment", photoPathList.get(0));
//                attachMent = attachMent.replace("\"", "");
//                LogUtils.i("Attachment", attachMent);
//                jo2.put("Attachment", attachMent); // 附件
//            }

            try {
                LogUtils.i("noticeInfo", "OK");
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.i("noticeInfo", strResp);
                // 解析提交返回的数据
                JSONObject jsonObject = new JSONObject(strResp);
                strResp = jsonObject.get("Data").toString();
                strResp = strResp.substring(1, strResp.length() - 1);

                if ("true".equals(strResp)) {
                    handler.sendEmptyMessage(NoticeNewActivity.UPDATA_SUCCESED);
                } else {
                    handler.sendEmptyMessage(NoticeNewActivity.UPDATA_FAILED);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("erroResult", e + "");
                handler.sendEmptyMessage(NoticeNewActivity.UPDATA_FAILED);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Boolean(strResp);
    }

    /**
     * 请假申请
     */
    public Boolean WriteAskForLeave(String userSelectId, String employee,
                                    String timeType, String deadLine, String cause,
                                    String vacationType, String photoSerialNo, String updateTime) {
        String methodName = "AskForLeave/ApplyForLeave/";

        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("testkeno", url);
        LogUtils.i("testkeno", "CorpId:" + Global.mUser.CorpId);
        LogUtils.i("testkeno", "用户ID:" + Global.mUser.Id);
        LogUtils.i("testkeno", "发布人ID:" + Global.mUser.Id);
        LogUtils.i("testkeno", "发布人Name:" + Global.mUser.UserName);
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("CorpId", Global.mUser.CorpId);
            // jo2.put("UserName", Global.mUser.UserName);
            jo2.put("UserId", Global.mUser.Id);
            jo2.put("Publisher", Global.mUser.UserName);
            jo2.put("UserSelectId", userSelectId);
            jo2.put("Employee", employee);
            jo2.put("TimeType", timeType);
            jo2.put("DeadLine", deadLine);
            jo2.put("Cause", cause);
            jo2.put("VacationType", vacationType);
            jo2.put("PhotoSerialNo", photoSerialNo);
            jo2.put("UpdateTime", updateTime);
            // jo2.put("ReleaseTime", ViewHelper.getDateString());
            // jo2.put("ExpirationTime",
            // Global.ConvertStringDateToDate(ExpirationTime) );
            try {
                strResp = mHttpUtils.postSubmit(url, jo2);
                // 解析提交返回的数据
                JSONObject jsonObject = new JSONObject(strResp);
                strResp = jsonObject.get("Data").toString();
                strResp = strResp.substring(1, strResp.length() - 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Boolean(strResp);
    }

    /**
     * 表单数据保存
     *
     * @param formInfo 表单对象
     * @return 保存成功返回表单的编号
     */
    public String WriteFormInfo(FormInfo formInfo) {
        // String methodName = "Flow/SaveSheet/"; //旧的接口
        String methodName = "Flow/SaveData/";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;

        LogUtils.i("testkeno", url);
        String strResp = null;
        try {
            // 通过Gson将对象转化为json
            Gson gson = new Gson();
            // 将表单信息已字符串的形式提交
            String obj = gson.toJson(formInfo);
            JSONObject jo2 = new JSONObject();
            // String jsonData = URLEncoder.encode(obj, "utf-8");
            LogUtils.d("testkeno4", "obj-->" + obj);
            jo2.put("Content", obj); // 表单内容
            try {
                // strResp = mHttpUtils.postJsonSubmit(url, obj);
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.e("testkeno4", "formId-->" + strResp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strResp;
    }

    /**
     * 表单数据保存（2015版）
     *
     * @param formInfo 表单对象
     * @return 保存成功返回表单的编号
     */
    public String WriteFormInfoNew(FormInfo formInfo) {
        // 和WriteFormInfo()的区别：提交表单实体，包括表单明细
        String methodName = "Flow/SaveFormData/";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("testkeno", url);
        String strResp = null;
        try {
            // JSONObject jo2 = JsonUtils.initJsonObj(formInfo, FormInfo.class);
            JSONObject jo2 = JsonUtils.initJsonObj(formInfo);
            String json = jo2.toString();
            LogUtils.i("testkeno2_json", json);
            try {
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.e("testkeno4", "result-->" + strResp);
                strResp = JsonUtils.pareseData(strResp);
                strResp = strResp.substring(1, strResp.length() - 1);
                LogUtils.e("testkeno4", "formId-->" + strResp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strResp;
    }

    /**
     * 表单数据提交
     *
     * @param formId     表单编号
     * @param categoryId 表单名称（流程分类名称（唯一））
     * @return 是否提交表单成功
     */
    public boolean submitFormInfo(int formId, String typeName) {
        boolean result = false;
        // String methodName = "Flow/Submit/";
        String methodName = "Workflow/Submit/";
        String url = Global.BASE_URL + Global.EXTENSION + methodName + formId
                + "/" + typeName;
        LogUtils.i("testkeno", url);
        String strResp = null;
        try {
            try {
                strResp = mHttpUtils.httpGet(url);
                if (!TextUtils.isEmpty(strResp) && strResp.contains("Data")) {
                    JSONObject jsonObject = new JSONObject(strResp);
                    String data = jsonObject.getString("Data");
                    LogUtils.i("submitData", data);
                    if (data.contains("提交成功")) {
                        result = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 表单数据提交
     *
     * @param formId     表单编号
     * @param categoryId 表单名称（流程分类名称（唯一））
     * @param auditorIds 审核人，没有则为空
     * @return 是否提交表单成功 2015-03-16
     */
    public String submitFormInfoNew(String formId, String typeName,
                                    String auditorIds) {
        String methodName = "Workflow/Submit/";
        String url = Global.BASE_URL + Global.EXTENSION + methodName + formId
                + "/" + typeName;
        if (!TextUtils.isEmpty(auditorIds)) {
            url += "/" + auditorIds;
        }
        LogUtils.i("testkeno", url);
        String strResp = "";
        try {
            try {
                strResp = mHttpUtils.httpGet(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strResp;
    }

    /**
     * 表单数据审核
     *
     * @param opinion 意见
     * @param id      表单流程号
     * @return 是否审核表单成功
     */
    public boolean submitApproval(String opinion, int id) {
        // String methodName = "Flow/Audit/"; //旧接口
        String methodName = "Workflow/Audit/";
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + "?workflowId=" + id + "&opinion=" + opinion;
        LogUtils.i("testkeno", url);
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("CorpId", Global.mUser.CorpId);
            jo2.put("UserId", Global.mUser.Id);
            jo2.put("Publisher", Global.mUser.UserName);
            jo2.put("workflowId", id);
            jo2.put("opinion", opinion);
            try {
                // strResp = mHttpUtils.postSubmit(url, jo2);
                strResp = mHttpUtils.get(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Boolean(strResp);
    }

    /**
     * 表单数据审核
     *
     * @param opinion    意见
     * @param attachId   附件编号
     * @param id         表单流程号 flow.id
     * @param isPass     是否通过,true为通过,false为否决
     * @param auditorIds 指定审核人,可为空
     * @author kjx
     */
    public void submitApprovalNew(String opinion, String attachId, int id,
                                  boolean isPass, Handler handler) {
        List<User> aduitList = null;
        // type=1:通过 2：否决
        // 此方法为审核与否决共用，type用来标识
        int type = 2;
        String typeName = "否决";
        if (isPass) {
            type = 1;
            typeName = "审核";
        }
        String methodName = "Workflow/Audit";
        // [GET("Audit/{workflowId}/{opinion}/{type}")]
        String url = Global.BASE_URL + Global.EXTENSION + methodName + "/" + id
                + "/" + opinion + "/" + type;
        if (!TextUtils.isEmpty(attachId)) {
            url += "/" + attachId;
        }
        LogUtils.i("testkenoAudit", url);
        String strResp = null;
        try {
            strResp = mHttpUtils.httpGet(url);
            String returnMsg = JsonUtils.parseLoginMessage(strResp);
            aduitList = JsonUtils.pareseJsonToList(returnMsg, User.class);
            LogUtils.i("testkenoAudit", strResp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Message msg = handler.obtainMessage();
        // msg.obj = typeName;
        // msg.obj = "";

        ReturnModel<String> returnModel = JsonUtils.pareseResult(strResp);

        if (returnModel.Status == 1) {
            msg.what = 3;
            msg.obj = aduitList;
            handler.sendMessage(msg); // 成功
        } else if (returnModel.Status == 0) {
            msg.what = 4;
            handler.sendMessage(msg); // 否决
        }
        // if (!strResp.isEmpty() && strResp.contains("error:0")) {
        // msg.what = 3;
        // handler.sendMessage(msg); // 成功
        // } else {
        // msg.what = 4;
        // handler.sendMessage(msg); // 否决
        // }
    }

    /**
     * 表单数据审核
     *
     * @param opinion  意见
     * @param attachId 附件编号
     * @param id       表单流程号
     * @param isPass   是否通过,true为通过,false为否决
     * @para aduitorIds 审核人编号
     * @author kjx
     */
    public void submitApprovalNew(String opinion, String attachId, int id,
                                  String aduitorIds, boolean isPass, Handler handler) {
        if (TextUtils.isEmpty(opinion)) {
            opinion = "无意见";
        }
        List<User> aduitList = null;
        // type=1:通过 2：否决
        // 此方法为审核与否决共用，type用来标识
        int type = 2;
        String typeName = "否决";
        if (isPass) {
            type = 1;
            typeName = "审核";
        }
        String methodName = "Workflow/Audit";
        // [GET("Audit/{workflowId}/{opinion}/{type}")]
        String url = Global.BASE_URL + Global.EXTENSION + methodName + "/" + id
                + "/" + opinion + "/" + type;
        LogUtils.i(TAG, url);
        if (!TextUtils.isEmpty(aduitorIds)) {
            url += "/" + aduitorIds;
        }
        if (TextUtils.isEmpty(attachId)) {
            attachId = "0";
        }
        url += "/" + attachId;
        LogUtils.i("testkenoAudit", url);
        String strResp = null;
        try {
            strResp = mHttpUtils.httpGet(url);
            String returnMsg = JsonUtils.parseLoginMessage(strResp);
            aduitList = JsonUtils.pareseJsonToList(returnMsg, User.class);
            LogUtils.i("testkenoAudit", strResp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Message msg = handler.obtainMessage();
        // msg.obj = typeName;
        // msg.obj = "";

        ReturnModel<String> returnModel = JsonUtils.pareseResult(strResp);

        if (returnModel.Status == 1) {
            msg.what = 3;
            msg.obj = aduitList;
            handler.sendMessage(msg); // 成功
        } else if (returnModel.Status == 0) {
            msg.what = 4;
            handler.sendMessage(msg); // 否决
        }
    }

    /**
     * 表单数据否决
     *
     * @param opinion 意见
     * @param id      表单流程号
     * @return 是否否决表单成功
     */
    public boolean submitVoteDown(String opinion, int id) {
        String methodName = "Flow/Refuse/";

        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + "?workflowId=" + id + "&opinion=" + opinion;
        LogUtils.i("testkeno", url);
        LogUtils.i("testkeno", "CorpId:" + Global.mUser.CorpId);
        LogUtils.i("testkeno", "用户ID:" + Global.mUser.Id);
        LogUtils.i("testkeno", "发布人ID:" + Global.mUser.Id);
        LogUtils.i("testkeno", "发布人Name:" + Global.mUser.UserName);
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("CorpId", Global.mUser.CorpId);
            jo2.put("UserId", Global.mUser.Id);
            jo2.put("Publisher", Global.mUser.UserName);
            jo2.put("workflowId", id);
            jo2.put("opinion", opinion);
            try {
                strResp = mHttpUtils.postSubmit(url, jo2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Boolean(strResp);
    }

    // /** 获取通知 */
    // public List<Notice> GetNotices() {
    // // String md5 = ByteUtil.md5One();
    // String methodName = "通知/";
    // String Parameter = Global.mUser.Id + "/" + Global.mUser.CorpId;
    // String url = Global.BASE_URL + Global.EXTENSION + methodName + Parameter;
    // List<Notice> list = null;
    // String data = "";
    // try {
    // data = mHttpUtils.get(url);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    //
    // list = JsonUtils.parseNoticeFromJson(data);
    //
    // return list;
    // }

    @Deprecated
    /** 查询并分页 */
    public List<HashMap<String, Object>> GetSelect_Paging(int pageNumber,
                                                          int pageSize, int index, String methodName, String tableName,
                                                          String filter, String filterAppend) {
        // String md5 = ByteUtil.md5One();
        // String methodName = methodName;
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        List<HashMap<String, Object>> hashMaplst = new ArrayList<HashMap<String, Object>>();
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("企业编号", Global.mUser.CorpId);
            jo2.put("用户编号", Global.mUser.Id);
            jo2.put("表名", tableName);
            jo2.put("条件", filter);
            jo2.put("附加条件", filterAppend);
            jo2.put("索引页", pageNumber);
            jo2.put("每页数量", pageSize);
            jo2.put("偏移量", index);

            try {
                strResp = mHttpUtils.post(url, jo2);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (strResp == null) {
                strResp = "";
                return null;
            }

            // hashMaplst = JsonUtils.ConvertJsonToList(strResp);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return hashMaplst;
    }

    /**
     * 手机码
     *
     * @param latitude
     * @param longitude
     * @param address
     * @param attachId      附件编号
     * @param phoneSerialNo 手机码
     * @param handler
     */
    public void Attendance1(String latitude, String longitude, String address,
                            String attachId, String phoneSerialNo, Handler handler) {
        // String methodName = "签到";
        String methodName = "Attendance/SignIn";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("TagActivity", " 签到图片id:" + attachId + "---" + longitude
                + "---" + latitude + "---" + address + "---");
        // List<Notice> list = null;
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            // jo2.put("corpId", Global.mUser.CorpId);
            // jo2.put("userId", Global.mUser.Id);
            jo2.put("Longitude", longitude);
            jo2.put("Latitude", latitude);
            jo2.put("PositionSignIn", address);
            jo2.put("PhotoSerialNo", attachId);
            jo2.put("PhoneSerialNO", phoneSerialNo);
            try {
                strResp = mHttpUtils.postSubmit(url, jo2);
                // 解析提交返回的数据
                JSONObject jsonObject = new JSONObject(strResp);
                strResp = jsonObject.get("Data").toString();
                String msg = jsonObject.get("Message").toString();
                // strResp = strResp.substring(1, strResp.length() - 1);
                LogUtils.i("TagActivity", strResp);
                if (strResp.contains("true")) {
                    if (msg.contains("迟到")) {
                        // 迟到
                        handler.sendEmptyMessage(TagActivity.ATTANCE_IS_LATER);
                    } else {
                        handler.sendEmptyMessage(TagActivity.ATTANCE_SUCCESS);
                    }
                } else {
                    handler.sendEmptyMessage(TagActivity.ATTANCE_FAILURE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /***
     * 签退
     *
     * @param latitude
     * @param longitude
     * @param address
     * @param photoSerialNo
     * @return
     */
    public void Attendance2(String latitude, String longitude, String address,
                            String photoSerialNo, String phoneSerialNo, Handler handler) {
        String methodName = "Attendance/SignOut";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            // jo2.put("CorpId", Global.mUser.CorpId);
            // jo2.put("UserId", Global.mUser.Id);
            jo2.put("OutLongitude", longitude);
            jo2.put("OutLatitude", latitude);
            jo2.put("PositionSignOut", address);
            jo2.put("PhotoSingnOut", photoSerialNo); // 签退图片id
            jo2.put("PhoneSerialNO", phoneSerialNo);
            LogUtils.i("TagActivity", " 签退图片id:" + photoSerialNo + "---"
                    + longitude + "---" + latitude + "---" + address + "---");
            try {
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.e("TagActivity", strResp);
                // 解析提交返回的数据
                JSONObject jsonObject = new JSONObject(strResp);
                strResp = jsonObject.get("Data").toString();
                String msg = jsonObject.get("Message").toString();
                // strResp = strResp.substring(1, strResp.length() - 1);
                LogUtils.i("TagActivity", strResp);
                if (strResp.contains("true")) {
                    if (msg.contains("早退")) {
                        // 早退
                        handler.sendEmptyMessage(TagActivity.ATTANCE_IS_EARLY);
                    } else {
                        handler.sendEmptyMessage(TagActivity.ATTANCE_SUCCESS);
                    }

                } else {
                    handler.sendEmptyMessage(TagActivity.ATTANCE_FAILURE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 签退
     */
    public boolean Attendance2Old(String latitude, String longitude,
                                  String address) {
        String methodName = "签退";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        List<HashMap<String, Object>> hashMaplst = new ArrayList<HashMap<String, Object>>();
        // List<Notice> list = null;
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("企业编号", Global.mUser.CorpId);
            jo2.put("用户编号", Global.mUser.Id);
            jo2.put("经度", latitude);
            jo2.put("纬度", longitude);
            jo2.put("地理位置_签退", address);

            try {
                strResp = mHttpUtils.post(url, jo2);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (strResp == null) {
                strResp = "";
            }

            // list = JsonUtils.parseNoticeFromJson(strResp);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new Boolean(strResp);
    }

    /***
     * 提交迟到原因
     */
    public void sendLateReason(String id, String lateReason, Handler handler) {
        String methodName = "提交迟到原因/";
        String Parameter = Global.mUser.Id + "/" + Global.mUser.CorpId + "/"
                + id + "/" + lateReason;
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        String data = "";
        try {
            data = mHttpUtils.get(url);

            Message message = new Message();
            if (data != null && data.equals("网络错误")) {
                message.what = SendReasonHandler.SEND_LATE_REASON_FAILED;
            } else if (data != null) {

                Boolean b = new Boolean(data);
                if (b.booleanValue()) {
                    message.what = SendReasonHandler.SEND_LATE_REASON_SUCCESS;
                } else {
                    message.what = SendReasonHandler.SEND_LATE_REASON_FAILED;
                }
            }
            handler.sendMessage(message);

        } catch (Exception e) {

        }

    }

    /***
     * 提交早退原因
     */
    public void sendEarlyReason(String id, String earlyReason, Handler handler) {
        String methodName = "提交早退原因/";
        String Parameter = Global.mUser.Id + "/" + Global.mUser.CorpId + "/"
                + id + "/" + earlyReason;
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        String data = "";
        try {
            data = mHttpUtils.get(url);

            Message message = new Message();
            if (data != null && data.equals("网络错误")) {
                message.what = SendReasonHandler.SEND_EARLY_REASON_FAILED;
            } else if (data != null) {

                Boolean b = new Boolean(data);
                if (b.booleanValue()) {
                    message.what = SendReasonHandler.SEND_EARLY_REASON_SUCCESS;
                } else {
                    message.what = SendReasonHandler.SEND_EARLY_REASON_FAILED;
                }
            }
            handler.sendMessage(message);

        } catch (Exception e) {

        }
    }

    /**
     * 获取部门用户 (旧方法)
     */
    public void GetSys_User_Department(long lastUpdateTime, Handler handler) {
        String url = Global.BASE_URL
                + "Department/GetUserListByLastDate?lastDate=" + lastUpdateTime;
        LogUtils.i("kjxTest", url);
        List<Data> list = null;
        String data = "";
        try {
            // data = mHttpUtils.get(url);
            data = mHttpUtils.httpGet(url);
            LogUtils.i("keno_json", data);
            Message message = new Message();
            if (data != null && !data.equals("网络错误")) {
                list = JsonUtils.ConvertJsonToList(data, Data.class);
                LogUtils.d("json_list", "======" + list.size());
                if (list != null && list.size() >= 0) {
                    if (lastUpdateTime == 0) {
                        message.what = RemindHandler.GET_USERS_DATA_SUCCESS;
                        message.obj = list;
                    } else {
                        // message.what =
                        // HandlerInitUsers.GET_USERS_DATA_SUCCESS;
                        message.what = RemindHandler.GET_USERS_DATA_SUCCESS;
                        message.obj = list;
                    }
                } else {
                    if (lastUpdateTime == 0) {
                        message.what = RemindHandler.GET_USERS_DATA_FAILED;
                    } else {
                        // message.what =
                        // HandlerInitUsers.GET_USERS_DATA_FAILED;
                        message.what = RemindHandler.GET_USERS_DATA_FAILED;
                    }
                }
                handler.sendMessage(message);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得省市县字典集合信息
     * <p/>
     * 新版服务中使用
     *
     * @param handler
     */
    @SuppressWarnings("unchecked")
    public void GetProvinceDicts(Handler handler) {
        String url = Global.BASE_URL + "GetProvinceDicts/GetProvinceDicts";
        LogUtils.i("GetProvinceDicts", url);
        String data = "";
        try {
            List<List<字典>> list = new ArrayList<List<字典>>();
            data = mHttpUtils.httpGet(url);
            LogUtils.i("GetProvinceDicts", data);
            list = (List<List<字典>>) JsonUtils.ConvertJsonToList(data,
                    list.getClass());
            LogUtils.d("GetProvinceDicts", list.size() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得全体员工信息
     * <p/>
     * 新版服务中使用
     *
     * @param lastUpdateTime
     * @param handler
     */
    public void getAllUserList(Long maxUpdateTime, Handler handler) {
        String url = Global.BASE_URL
                + "Department/GetUserListByLastDate?lastDate=" + maxUpdateTime;
        LogUtils.i("getAllUserList", url);
        List<User> list = null;
        String data = "";
        try {
            data = mHttpUtils.httpGet(url);
            LogUtils.i("keno_json", data);
            Message message = new Message();
            if (data != null && !data.equals("网络错误")) {
                list = JsonUtils.ConvertJsonToList(data, User.class);
                LogUtils.d("json_list", "======" + list.size());
                if (list != null && list.size() >= 0) {
                    message.what = 7;
                    message.obj = list;
                } else {
                    message.what = 6;
                }
                handler.sendMessage(message);
            } else {
                message.what = 6;
                handler.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得全体员工信息
     * <p/>
     * 新版服务中使用
     *
     * @param lastUpdateTime long类型的时间，如果加载全部 maxUpdateTime=0
     */
    public List<User> downloadAllUserList(long maxUpdateTime) {
        String url = Global.BASE_URL
                + "Department/GetUserListByLastDate?lastDate=" + maxUpdateTime;
        LogUtils.i("getAllUserList", url);
        List<User> list = null;
        String data = "";
        try {
            data = mHttpUtils.httpGet(url);
            LogUtils.i("keno_json", data);
            if (data != null && !data.equals("网络错误")) {
                list = JsonUtils.ConvertJsonToList(data, User.class);
                LogUtils.d("json_list", "======" + list.size());
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 获取部门信息 （旧方法）
     */
    public void GetSys_Department(Handler handler) {
        String url = Global.BASE_URL
                + "Department/GetDepartmentListByLastDate?lastDate=0";
        LogUtils.i("kjxTest", url);
        List<部门> list = null;
        String data = "";
        try {
            data = mHttpUtils.httpGet(url);
            LogUtils.i("keno_json_dept", data);
            Message message = new Message();
            if (data != null && !data.equals("网络错误")) {
                list = JsonUtils.ConvertJsonToList(data, 部门.class);
                LogUtils.d("json_list_dept", "======" + list.size());
                if (list != null && list.size() >= 0) {
                    message.what = RemindHandler.GET_DEPARTMENT_SUCCEEDED;
                    message.obj = list;
                } else {
                    // message.what = RemindHandler.GET_USERS_DATA_FAILED;
                }
                handler.sendMessage(message);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取部门信息 ）
     * <p/>
     * 新版服务中使用
     */
    public void getDepartmentList(Handler handler) {
        String url = Global.BASE_URL
                + "Department/GetDepartmentListByLastDate?lastDate=0";
        LogUtils.i("kjxTest", url);
        List<部门> list = null;
        String data = "";
        try {
            data = mHttpUtils.httpGet(url);
            LogUtils.i("keno_json_dept", data);
            Message message = new Message();
            if (data != null && !data.equals("网络错误")) {
                list = JsonUtils.ConvertJsonToList(data, 部门.class);
                LogUtils.d("json_list_dept", "======" + list.size());
                if (list != null && list.size() >= 0) {
                    message.what = 14;
                    message.obj = list;
                } else {
                    // message.what = RemindHandler.GET_USERS_DATA_FAILED;
                }
                handler.sendMessage(message);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过网络获取客户列表
     */
    public void getClientList(Handler handler) {
        String methodName = "Customer/GetCustomerList/";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.e("keno_json", "======" + url);
        List<Client> list = null;
        String data = "";
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("表名", "客户");
            // data = mHttpUtils.get(url);
            data = mHttpUtils.postSubmit(url, jo2);
            LogUtils.d("keno_json", data);
            Message message = new Message();
            if (data != null && !data.equals("网络错误")) {
                list = JsonUtils.ConvertJsonToList(data, Client.class);
                LogUtils.e("keno_json", "======" + list.size());
                if (list != null && list.size() >= 0) {
                    message.what = RemindHandler.GET_CLIENT_DATA_SUCCESS;
                    message.obj = list;
                } else {
                    message.what = RemindHandler.GET_CLIENT_DATA_FAILED;
                }
                handler.sendMessage(message);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录获得取得用户信息
     *
     * @throws Exception
     */
    public void login(String uname, final String pwd, final String corpName, final Handler handler) {
        String methodName = "Account/GetUserByCorpName/";
        String Parameter = uname + "/" + pwd + "/" + corpName;
        final String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        LogUtils.i("keno5", url);


        StringRequest.get(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String data) {
                LogUtils.i("login", data);
                List<User> list = null;
                String msg = JsonUtils.parseLoginMessage(data);
                String status = JsonUtils.parseStatus(data);
                LogUtils.i("login", "msg:" + msg);

                if (!TextUtils.isEmpty(status) && status.equals("0")) {
                    LogUtils.i("erroMsg", "" + msg);
                    // 登录失败
                    Message message = handler.obtainMessage();
                    message.what = 0;
                    message.obj = msg;
                    handler.sendMessage(message);
                } else {
                    list = JsonUtils.ConvertJsonToList(data, User.class);
                    if (list != null && list.size() > 0) {
                        // 登录成功后得到服务器返回的当前登录的用户信息，并将其保存应用全局变量中
                        Global.mUser = list.get(0);
                        Global.mUser.PassWord = pwd;
                        Global.mUser.CorpName = corpName;
                        Global.mUser.Admin = list.get(0).Admin;
                        Global.mUser.CorpId = list.get(0).getCorpId();
                        Global.mUser.Department = list.get(0).getDptId();
                        // BoeryunApp.Global.mUser.Passport = Global.mUser.Passport;
                        LogUtils.i("passport2", Global.mUser.Passport);
                        LogUtils.i("passport2", "dptId=" + Global.mUser.Department);
                    }
                    handler.sendEmptyMessage(1);
                }
            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {
                String msg = JsonUtils.parseLoginMessage(result);
                Message message = handler.obtainMessage();
                message.what = 0;
                message.obj = msg;
                handler.sendMessage(message);
            }
        });
//        try {
//            data = mHttpUtils.get(url);
//            LogUtils.i("login", data);
//            String msg = JsonUtils.parseLoginMessage(data);
//            String status = JsonUtils.parseStatus(data);
//            LogUtils.i("login", "msg:" + msg);
//
//            if (!TextUtils.isEmpty(status) && status.equals("0") || "网络错误".equals(data)) {
//                LogUtils.i("erroMsg", "" + msg);
//                // 登录失败
//                Message message = handler.obtainMessage();
//                message.what = 0;
//                message.obj = msg;
//                handler.sendMessage(message);
//            } else {
//                list = JsonUtils.ConvertJsonToList(data, User.class);
//                if (list != null && list.size() > 0) {
//                    // 登录成功后得到服务器返回的当前登录的用户信息，并将其保存应用全局变量中
//                    Global.mUser = list.get(0);
//                    Global.mUser.PassWord = pwd;
//                    Global.mUser.CorpName = corpName;
//                    Global.mUser.Admin = list.get(0).Admin;
//                    Global.mUser.CorpId = list.get(0).getCorpId();
//                    Global.mUser.Department = list.get(0).getDptId();
//                    // BoeryunApp.Global.mUser.Passport = Global.mUser.Passport;
//                    LogUtils.i("passport2", Global.mUser.Passport);
//                    LogUtils.i("passport2", "dptId=" + Global.mUser.Department);
//                }
//                handler.sendEmptyMessage(1);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }


//    /**
//     * 登录获得取得用户信息
//     *
//     * @throws Exception
//     */
//    public void login(String uname, final String pwd, final String corpName, final Handler handler) {
//        String methodName = "Account/GetUserByCorpName/";
//        String Parameter = uname + "/" + pwd + "/" + corpName;
//        String url = Global.BASE_URL + Global.EXTENSION + methodName
//                + Parameter;
//        LogUtils.i("keno5", url);
//        StringRequest.getAsyn(url, new StringResponseCallBack() {
//            @Override
//            public void onResponse(String response) {
//                List<User> list = null;
//                String msg = JsonUtils.parseLoginMessage(response);
//                String status = JsonUtils.parseStatus(response);
//                LogUtils.i("login", "msg:" + msg);
//
//                if (!TextUtils.isEmpty(status) && status.equals("0")) {
//                    LogUtils.i("erroMsg", "" + msg);
//                    // 登录失败
//                    Message message = handler.obtainMessage();
//                    message.what = 0;
//                    message.obj = msg;
//                    handler.sendMessage(message);
//                } else {
//                    list = JsonUtils.ConvertJsonToList(response, User.class);
//                    if (list != null && list.size() > 0) {
//                        // 登录成功后得到服务器返回的当前登录的用户信息，并将其保存应用全局变量中
//                        Global.mUser = list.get(0);
//                        Global.mUser.PassWord = pwd;
//                        Global.mUser.CorpName = corpName;
//                        Global.mUser.Admin = list.get(0).Admin;
//                        Global.mUser.CorpId = list.get(0).getCorpId();
//                        Global.mUser.Department = list.get(0).getDptId();
//                        // BoeryunApp.Global.mUser.Passport = Global.mUser.Passport;
//                        LogUtils.i("passport2", Global.mUser.Passport);
//                        LogUtils.i("passport2", "dptId=" + Global.mUser.Department);
//                    }
//                    handler.sendEmptyMessage(1);
//                }
//            }
//
//            @Override
//            public void onFailure(Request request, Exception ex) {
//
//            }
//
//            @Override
//            public void onResponseCodeErro(String result) {
//
//            }
//        });
//
//    }

    /**
     * 登录获得取得用户信息
     *
     * @throws Exception
     */
    public boolean login(String uname, String pwd, String corpName) {
        boolean result = false;
        String methodName = "Account/GetUserByCorpName/";
        String Parameter = uname + "/" + pwd + "/" + corpName;
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        LogUtils.i("keno5", url);
        List<User> list = null;
        String data = "";
        try {
            data = mHttpUtils.get(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.i("login", data);
        String msg = JsonUtils.parseLoginMessage(data);
        String status = JsonUtils.parseStatus(data);
        LogUtils.i("login", "msg:" + msg);

        if (!TextUtils.isEmpty(status) && status.equals("0")) {
            LogUtils.i("erroMsg", "" + msg);
            // 登录失败
            result = false;
        } else {
            list = JsonUtils.ConvertJsonToList(data, User.class);
            if (list != null && list.size() > 0) {
                // 登录成功后得到服务器返回的当前登录的用户信息，并将其保存应用全局变量中
                Global.mUser = list.get(0);
                Global.mUser.PassWord = pwd;
                Global.mUser.CorpName = corpName;
                Global.mUser.Admin = list.get(0).Admin;
                Global.mUser.CorpId = list.get(0).getCorpId();
                Global.mUser.Department = list.get(0).getDptId();
                // BoeryunApp.Global.mUser.Passport = Global.mUser.Passport;
                LogUtils.i("passport2", Global.mUser.Passport);
                LogUtils.i("passport2", "dptId=" + Global.mUser.Department);
            }
            result = true;
        }
        return result;
    }

    /**
     * 主界面提醒(访问网络)
     *
     * @param handler
     * @author k
     */
    @Deprecated
    public void GetRemind(Handler handler) {
        // String md5 = ByteUtil.md5One();
        String methodName = "Task/GetNewestCount";
        // String Parameter = Global.mUser.Id + "/" + Global.mUser.CorpId;
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        // + Parameter;
        LogUtils.i("GetRemind", url);
        List<Remind> list = null;
        String data = "";
        try {
            data = mHttpUtils.httpPost(url);
            LogUtils.d("GetRemind", "data:" + data);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("remind", e.toString());
        }
    }

    /**
     * 访问网络获得主界面红色小数字提醒
     *
     * @author k
     * @since 2015-03-05
     */
    public Remind GetRemindCount() {
        // String md5 = ByteUtil.md5One();
        String methodName = "ReadStatus/GetRemindCount";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("GetRemind", url);
        Remind remind = null;
        try {
            String data = mHttpUtils.httpGet(url);
            LogUtils.d("GetRemind", "data:" + data);
            if (!TextUtils.isEmpty(data) && !data.equals("网络错误")) {
                List<Remind> list = JsonUtils.ConvertJsonToList(data,
                        Remind.class);
                LogUtils.d("GetRemind", "data:" + list.size());
                if (list.size() != 0) {
                    remind = list.get(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("remind", "" + e.toString());
        }
        return remind;
    }

    /**
     * 主界面提醒（获得本地未读数量）
     *
     * @param handler
     */
    public void GetLocalRemind(Handler handler, Context context, String userId) {
        LogUtils.i("noticeNum", userId);
        // 由于数据库没统一可能有如下两种格式
        String newuserId = "%''" + userId + "'';%"; // 查询 '3';'13';'20';
        String newuserId2 = "%," + userId + ",%"; // 查询3,13,20
        String leftUserId = "%," + userId + "%";
        String rightUserId = "%" + userId + ",%";
        // userId = "%" + userId + "%";
        LogUtils.i("RemindRead", newuserId);
        Message message = new Message();
        Remind remind = new Remind();
        ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
        Dao<通知, Integer> noticeDao = null;
        Dao<任务, Integer> taskDao = null;
        Dao<邮件, Integer> emailDao = null;
        Dao<订单, Integer> orderDao = null;
        Dao<日志, Integer> logDao = null;
        Dao<Client, Integer> clientDao = null;
        Dao<客户联系记录, Integer> contactsDao = null;
        try {
            noticeDao = ormDataHelper.getDao(通知.class);
            emailDao = ormDataHelper.getDao(邮件.class);
            taskDao = ormDataHelper.getDao(任务.class);
            orderDao = ormDataHelper.getDao(订单.class);
            logDao = ormDataHelper.getDao(日志.class);
            clientDao = ormDataHelper.getDao(Client.class);
            contactsDao = ormDataHelper.getDao(客户联系记录.class);
            // taskDao = ormDataHelper.getDao(任务.class);
            // TODO 满足条件：1.发布人为自己 2，接收人有自己 3,为空
            // int noticeReceiveCount = noticeDao.queryBuilder().where().not()
            // .like("Read", newuserId).and().eq("Publisher", userId)
            // .query().size();
            // int noticePubCount = noticeDao.queryBuilder().where().not()
            // .like("Read", newuserId).and().like("Personnel", newuserId)
            // .query().size();
            // int noticeAnd = noticeDao.queryBuilder().where().not()
            // .like("Read", newuserId).and().like("Personnel", newuserId)
            // .and().eq("Publisher", userId).query().size();
            // int noticeCount = noticeReceiveCount + noticePubCount -
            // noticeAnd;
            // noticeDao.queryBuilder().
            // LogUtils.i("noticeNum", "noticeReceiveCount：" +
            // noticeReceiveCount
            // + "------noticePubCount:" + noticePubCount
            // + "------noticeAnd:" + noticeAnd);
            Where<通知, Integer> noticeWhere = noticeDao.queryBuilder().where();
            // int noticeCount = noticeWhere.eq("Read", "0")
            // .and().eq("Personnel", Global.mUser.Id).query().size();

            int noticeCount = noticeWhere
                    .and(noticeWhere.or(noticeWhere.eq("Read", "false"),
                            noticeWhere.eq("Read", "False"),
                            noticeWhere.eq("Read", "FALSE")),
                            noticeWhere.or(noticeWhere.eq("Personnel",
                                    Global.mUser.Id), noticeWhere.eq(
                                    "Publisher", Global.mUser.Id))).query()
                    .size();
            // // 未读任务 满足条件：1.执行人为自己 2，参与人有自己 3,发布人不是自己（自己发布默认为已读）
            Where<任务, Integer> taskWhere = taskDao.queryBuilder().where();
            int sizeN = taskDao.queryForAll().size();
            taskWhere.and(taskWhere.or(
                    taskWhere.not().like("Readed", rightUserId),
                    taskWhere.isNull("Readed")), taskWhere.or(
                    taskWhere.eq("Executor", userId),
                    taskWhere.like("Participant", newuserId)), taskWhere.not()
                    .eq("Publisher", userId));
            int taskCount = taskWhere.query().size();

            // // 任务 满足条件：1.执行人为自己 2，参与人有自己
            Where<订单, Integer> orderWhere = orderDao.queryBuilder().where();
            int sizeOrder = taskDao.queryForAll().size();
            orderWhere.and(orderWhere.not().like("Readed", leftUserId),
                    orderWhere.not().like("Readed", rightUserId),
                    orderWhere.eq("Designer", userId));
            int orderCount = orderWhere.query().size();

            Where<日志, Integer> logWhere = logDao.queryBuilder().where();
            // logWhere.and(logWhere.not().like("Readed", leftUserId), logWhere
            // .not().like("Readed", rightUserId), logWhere.eq(
            // "Personnel", userId));
            // 改进后日志的未读数量
            logWhere.not().eq("Readed", userId);
            int logCount = logWhere.query().size();

            // 客户
            Where<Client, Integer> clientWhere = clientDao.queryBuilder()
                    .where();
            // clientWhere.and(clientWhere.not().like("Readed", leftUserId),
            // clientWhere.not().like("Readed", rightUserId));
            clientWhere.not().eq("Readed", userId);
            int clientCount = clientWhere.query().size();

            // 客户联系记录
            Where<客户联系记录, Integer> contactsWhere = contactsDao.queryBuilder()
                    .where();
            // contactsWhere.and(contactsWhere.not().like("Readed", leftUserId),
            // contactsWhere.not().like("Readed", rightUserId));
            contactsWhere.not().eq("Readed", userId);
            int contactsCount = contactsWhere.query().size();

            LogUtils.i("noticeNum", "taskCount:" + sizeN
                    + "----taskReceiveCount：" + taskCount);
            LogUtils.i("noticeNum", "noticeCount:" + noticeCount);
            LogUtils.i("noticeNum", "orderWhere:" + orderCount);
            LogUtils.i("noticeNum", "日志数量:" + logCount);
            LogUtils.i("noticeNum", "未读客户数量:" + clientCount);
            LogUtils.i("noticeNum", "未读联系记录数量:" + contactsCount);

            // int taskCount = taskReceiveCount + taskPubCount - taskAnd;
            // int noticeCount = noticeDao.queryBuilder().where().not()
            // .like("Read", userId).query().size();
            // ****/
            // int noticeCount = noticeDao.queryBuilder().where().not()
            // .like("Read", userId).and().like("Personnel", userId).or()
            // .like("PublisherName", userId).query().size();

            int emailCount = emailDao.queryBuilder().where().not()
                    .like("Read", newuserId).or().isNull("Read").query().size();
            LogUtils.d("RemindRead", "Email--->" + emailCount);
            // int taskCount = taskDao.queryBuilder().where().not()
            // .like("Read", userId).query().size();
            remind.Notice = noticeCount + "";
            remind.Email = emailCount + "";
            remind.Task = taskCount + "";
            remind.Order = orderCount + "";
            remind.Log = logCount + "";
            remind.Client = clientCount + "";
            remind.Contacts = contactsCount + "";
            // message.what =
            // MenuNewActivity.UnreadMessagesHandler.GET_DATA_SUCCESS;
            message.obj = remind;
            handler.sendMessage(message);
        } catch (SQLException e) {
            e.printStackTrace();
            LogUtils.e("RemindRead", "error:" + e);
        }
    }

    /**
     * 员工权限 k
     */
    public void GetPermissions(Handler handler) {
        String methodName = "权限/Get员工所有权限/";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String data = "";
        try {
            data = mHttpUtils.httpGet(url);
            if (!data.equals("")) {
                JSONObject jsonObjectsStart = new JSONObject(data);
                data = "," + jsonObjectsStart.get("Data").toString() + ",";
                LogUtils.i("GetPermissions", data);
                Message msg = handler.obtainMessage();
                msg.obj = data;
                msg.what = 3;
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("GetPermissions", e + "");
        }
    }

    /**
     * 员工权限 k
     */
    public String GetPermissions() {
        String methodName = "权限/Get员工所有权限/";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String data = "";
        try {
            data = mHttpUtils.httpGet(url);
            if (!data.equals("")) {
                JSONObject jsonObjectsStart = new JSONObject(data);
                data = jsonObjectsStart.get("Data").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("pydata", e + "");
        }
        return data;
    }

    /**
     * 提醒，通用
     *
     * @param type       模型类
     * @param methodName 方法名
     * @return
     */
    @Deprecated
    public <T> List<T> GetRemind(Class<T> type, String methodName) {
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        // + Parameter;
        List<T> list = null;
        String data = "";
        try {
            JSONObject jo2 = new JSONObject();

            data = mHttpUtils.postSubmit(url, jo2);
            if (!data.equals("")) {
                // 解析提交返回的数据
                JSONObject jsonObject = new JSONObject(data);
                data = jsonObject.get("Data").toString();
                data = data.substring(1, data.length() - 1);
                // list = JsonUtils.parseRemindEmailFromJson(data);//之前的方法，不通用
                // list = (List<T>) JsonUtils
                // .parseMoreRemindJsonToList(data, type);
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获得评论提醒
     *
     * @param methodName 方法名
     * @return
     */
    public Notifications GetCommentRemind(String methodName) {
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        Notifications notifications = new Notifications();
        String data = "";
        try {
            JSONObject jo2 = new JSONObject();
            data = mHttpUtils.postSubmit(url, jo2);
            if (!TextUtils.isEmpty(data)) {
                // 解析提交返回的数据
                JSONObject jsonObject = new JSONObject(data);
                data = jsonObject.get("Data").toString();
                LogUtils.i("Notifications", data);
                // 去除首尾[]
                data = data.substring(1, data.length() - 1);
                LogUtils.d("Notifications", data);
                Type type = new TypeToken<Notifications>() {
                }.getType();
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                notifications = gson.fromJson(data, type);

                return notifications == null ? new Notifications()
                        : notifications;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notifications;
    }

    /**
     * 获得动态列表
     *
     * @param methodName 方法名
     * @return
     */
    public List<动态> GetDynamicList(ServerCall serverCall, Demand demand) {
        List<动态> list = new ArrayList<动态>();
        String data = "";
        try {
            data = serverCall.makeServerCalll_Post(demand);
            LogUtils.i("ServerCallTestURLDemand", data);
            if (!TextUtils.isEmpty(data)) {
                // 解析提交返回的数据
                list = JsonUtils.ConvertJsonToList(data, 动态.class);
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 将全部动态信息设置为已读
     */
    public void ReadDynamicList(Context context) {
        HttpUtils httpUtils = new HttpUtils();
        String url = Global.BASE_URL + "dynamic/ReadAllDynamic";
        String data = "";
        try {
            data = httpUtils.httpGet(url);
            if (!TextUtils.isEmpty(data)) {
                LogUtils.i("ReadDynamicList", data);
                JSONObject jsonObject = new JSONObject(data);
                String status = jsonObject.getString("Status");
                if (!TextUtils.isEmpty(status) && status.equals("1")) {
                    // 把全部设置为已读后，同步删除本地缓存
                    deleteDynamicRemind(context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据编号设置动态为已读
     *
     * @param id      动态编号
     * @param context
     */
    public void ReadDynamicById(int id, Context context) {
        HttpUtils httpUtils = new HttpUtils();
        String url = Global.BASE_URL + "dynamic/ReadDynamic/" + id;
        String data = "";
        try {
            data = httpUtils.httpGet(url);
            LogUtils.i(TAG, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param id       数据编号
     * @param dataType 动态类型 1: 公告通知 2:员工日志 3:任务 4:申请审批 5:邮件 6:通讯录 7:客户 8:客户联系记录
     *                 9:销售机会
     */
    public void ReadDynamic(int id, int dataType) {
        HttpUtils httpUtils = new HttpUtils();
        String url = Global.BASE_URL + "ReadStatus/SetRead/" + id + "/"
                + dataType;
        LogUtils.i(TAG, url);
        String data = "";
        try {
            data = httpUtils.httpGet(url);
            LogUtils.i(TAG, data);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "" + e);
        }
    }

    /**
     * 删除所用本地缓存动态提醒
     */
    private void deleteDynamicRemind(Context context) {
        ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
        try {
            Dao<动态已提醒, Integer> dao = ormDataHelper.getDao(动态已提醒.class);
            DeleteBuilder<动态已提醒, Integer> deleteBuilder = dao.deleteBuilder();
            int sum = deleteBuilder.delete();
            LogUtils.i("deleteDynamicRemind", "删除动态：" + sum);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isCustomerExist(String customerName) {
        boolean exists = false;

        String methodName = "客户是否存在/";
        String Parameter = customerName + "/" + Global.mUser.Id + "/"
                + Global.mUser.CorpId;
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        String data = "";
        try {
            data = mHttpUtils.get(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Boolean(data);
    }

    /**
     * 修改订单
     */
    public void UpdateOrder(订单 item, List<String> photoPathList,
                            ProgressBar pBar, Handler handler) {
        Message msg = handler.obtainMessage();
        String methodName = "Cabinet/SaveOrder";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("attachPath", url);
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("Id", item.Id); // 订单id:为0新建
            jo2.put("Stage2", item.Stage2); // 阶段
            // jo2.put("CorpId", Global.mUser.CorpId);
            // jo2.put("OrderNo", item.OrderNo);
            // jo2.put("Total", item.Total);
            // jo2.put("ClientName", item.ClientName);
            // jo2.put("OrderTime", item.OrderTime);
            // jo2.put("AccountApproveTime", item.AccountApproveTime);
            // jo2.put("MeasureTime", item.MeasureTime);
            // jo2.put("State", item.State);
            // jo2.put("UpdateTime", item.UpdateTime); // 咨询时间
            if (photoPathList.size() > 0) {
                String attachMent = uploadAttachPhotos(photoPathList, pBar);
                String attachMents = "";
                LogUtils.i("Attachment", photoPathList.get(0));
                LogUtils.i("Attachment", attachMent);
                if (!TextUtils.isEmpty(item.Attachment)) {
                    attachMents = item.Attachment + "," + attachMent;
                } else {
                    attachMents = item.Attachment;
                }
                LogUtils.d("Attachment", attachMents);
                jo2.put("Attachment", attachMents); // 附件
            }
            try {
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.i("attachPath", strResp);
                if ("true".equals(strResp)) {
                    handler.sendEmptyMessage(OrderActivity.UPLOAD_SUCCESS);
                }
                // 解析提交返回的数据
            } catch (Exception e) {
                LogUtils.e("attachPath", "-->" + e);
                e.printStackTrace();
                handler.sendEmptyMessage(OrderActivity.UPLOAD_FAILED);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtils.i("attachPath", "" + e);
        }
    }

    /**
     * 修改订单
     */
    public void UpdateOrder(订单 item, Handler handler) {
        Message msg = handler.obtainMessage();
        String methodName = "Cabinet/SaveOrder";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("attachPath", url);
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("Id", item.Id); // 订单id:为0新建
            jo2.put("Stage2", item.Stage2); // 阶段
            strResp = mHttpUtils.postSubmit(url, jo2);
            LogUtils.i("attachPath", strResp);
            if ("true".equals(strResp)) {
                handler.sendEmptyMessage(OrderActivity.UPDATE_SUCCESS);
            } else {
                handler.sendEmptyMessage(OrderActivity.UPDATE_FAILED);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtils.i("attachPath", "" + e);
        } catch (Exception e) {
            LogUtils.i("attachPath", "" + e);
            handler.sendEmptyMessage(OrderActivity.UPDATE_FAILED);
        }
    }

    /**
     * 获取订单评论的任务
     */
    public List<评论> getOrderContent(int orderNo) {
        return getOrderContent(orderNo + "");
    }

    /**
     * 获取订单评论的任务
     */
    public List<评论> getOrderContent(String orderNo) {
        // String md5 = ByteUtil.md5One();
        String methodName = "Cabinet/GetCabinetDiscussById/";
        String Parameter = orderNo;
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        LogUtils.i("kjxTest--->url", url + "\t:" + Global.mUser.Passport);
        List<评论> list = new ArrayList<评论>();
        String data = "";
        try {
            data = mHttpUtils.httpGet(url);
            LogUtils.i("kjxData", "length-->" + data);
            list = JsonUtils.parseContentFromJson(data).getData();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("kjxData", "kjxData Exception:");
        }
        return list;
    }

    /**
     * 新加订单评论计划
     */
    public void newOrderContent(String oderNo, String content, Handler handler) {
        // String md5 = ByteUtil.md5One();
        String methodName = "Cabinet/CabinetDiscuss/";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("kjxTest", "=====" + url);
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("CorpId", Global.mUser.CorpId);
            jo2.put("Publisher", Global.mUser.Id);
            jo2.put("OrderNo", oderNo);
            jo2.put("Content", content);
            try {
                strResp = mHttpUtils.postSubmit(url, jo2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (strResp != null) {
                handler.sendEmptyMessage(OrderActivity.NEW_CONTENT_SUCCESS);
            } else {
                handler.sendEmptyMessage(OrderActivity.NEW_CONTENT_FAILURE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取评论列表的任务 通用版
     *
     * @param orderNo 表单编号
     * @return 评论列表
     */
    public void getDiscuss(int orderNo, Handler handler) {
        String orderNoString = orderNo + "";
        getDiscuss(orderNoString, handler);
    }

    /**
     * 获取评论列表的任务 通用版
     *
     * @param orderNo 表单编号
     * @return 评论列表
     */
    public void getDiscuss(String orderNo, Handler handler) {
        Message msg = handler.obtainMessage();
        String methodName = "Customer/GetDiscussList?Type=1&OrderNo=";
        String Parameter = orderNo;
        // String Parameter = "111";
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        List<评论> list = new ArrayList<评论>();
        String data = "";
        try {
            data = mHttpUtils.httpPost(url);
            LogUtils.i("kjxData", "length-->" + data);
            if (!TextUtils.isEmpty(data)) {
                list = JsonUtils.parseContentFromJson(data).getData();
            }
            msg.obj = list;
            msg.what = 5;// 获得评论列表成功
        } catch (Exception e) {
            e.printStackTrace();
            msg.obj = e;
            msg.what = 6;
            LogUtils.e("kjxData", "kjxData Exception:" + e);
        } finally {
            handler.sendMessage(msg);
        }
    }

    /**
     * 获取日志评论列表的任务
     *
     * @param orderNo 表单编号
     * @return 日志评论列表暂用评论的实体类
     */
    public void getLogDiscuss(String orderNo, Handler handler) {
        Message msg = handler.obtainMessage();
        String methodName = "Log/GetLogDiscussList?&OrderNo=";
        String Parameter = orderNo;
        // String Parameter = "111";
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        List<日志评论> list = new ArrayList<日志评论>();
        String data = "";
        try {
            data = mHttpUtils.httpPost(url);
            LogUtils.i("kjxData", "length-->" + data);
            if (!TextUtils.isEmpty(data)) {
                // list = JsonUtils.parseLogContentFromJson(data).getData();
                list = JsonUtils.ConvertJsonToList(data, 日志评论.class);
                LogUtils.i("GET_DISCUSS_SUCCESS", " 获得评论列表成功:" + list.size());
            }
            msg.obj = list;
            msg.what = 5;// 获得评论列表成功
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
            msg.obj = e;
            msg.what = 6;
            LogUtils.e("kjxData", "kjxData Exception:" + e);
            handler.sendMessage(msg);
        }
    }

    /**
     * 获取联系记录评论列表
     *
     * @param orderNo 表单编号
     * @return 联系记录评论列表
     * <p/>
     * 2014-08-22
     */
    public void getContactsDiscuss(String orderNo, Handler handler) {
        Message msg = handler.obtainMessage();
        String methodName = "Customer/GetContactsDiscussList/";
        String url = Global.BASE_URL + Global.EXTENSION + methodName + orderNo;
        List<评论> list = new ArrayList<评论>();
        String data = "";
        try {
            data = mHttpUtils.httpGet(url);
            LogUtils.i("kjxData", "length-->" + data);
            if (!TextUtils.isEmpty(data)) {
                list = JsonUtils.ConvertJsonToList(data, 评论.class);
            }
            msg.obj = list;
            msg.what = 5;// 获得评论列表成功
        } catch (Exception e) {
            e.printStackTrace();
            msg.obj = e;
            msg.what = 6;
            LogUtils.e("kjxData", "kjxData Exception:" + e);
        } finally {
            handler.sendMessage(msg);
        }
    }

    /***
     * 新加评论 通用接口
     *
     * @param oderNo  表单单号
     * @param content 评论内容
     * @param handler
     */
    public void publishDiscuss(int oderNo, String content, Handler handler) {
        String order = oderNo + "";
        publishDiscuss(order, content, handler);
    }

    /***
     * 新加评论 通用接口
     *
     * @param oderNo  表单单号
     * @param content 评论内容
     * @param handler
     */
    public void publishDiscuss(String oderNo, String content, Handler handler) {
        String methodName = "Customer/AddDiscuss";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("kjxTest", "=====" + url);
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("Type", 1);
            jo2.put("OrderNo", oderNo);
            jo2.put("Content", content);
            jo2.put("Date", ViewHelper.getDateString());
            jo2.put("userId", Global.mUser.Id);
            try {
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.i("zlservice", strResp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject(strResp);
            strResp = jsonObject.get("Data").toString();

            LogUtils.e("zlservice", strResp);
            if (strResp.equals("[true]")) {
                handler.sendEmptyMessage(2);// ??????
                // getDiscuss(oderNo, handler);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     * 日志评论
     *
     * @param oderNo  表单单号
     * @param content 评论内容
     */
    public boolean publishLogDiscuss(String oderNo, String content) {
        boolean isResult = false;
        String methodName = "Log/AddLogDiscuss";
        // String methodName = "Customer/AddDiscuss";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("kjxTest", "=====" + url);
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("日志编号", oderNo);
            jo2.put("内容", content);
            jo2.put("发表时间", ViewHelper.getDateString());
            jo2.put("发表人", Global.mUser.Id);
            try {
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.i("zlservice", strResp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject(strResp);
            strResp = jsonObject.get("Data").toString();

            LogUtils.e("zlservice", strResp);
            if (strResp.equals("[true]")) {
                isResult = true;// 评论成功
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.toString() + "");
        }
        return isResult;
    }

    /***
     * 新加日志评论 通用接口
     *
     * @param oderNo  表单单号
     * @param content 评论内容
     * @param handler
     */
    public void publishLogDiscuss(String oderNo, String content, Handler handler) {
        String methodName = "Log/AddLogDiscuss";
        // String methodName = "Customer/AddDiscuss";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("kjxTest", "=====" + url);
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("日志编号", oderNo);
            jo2.put("内容", content);
            jo2.put("发表时间", ViewHelper.getDateString());
            jo2.put("发表人", Global.mUser.Id);
            try {
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.i("zlservice", strResp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject(strResp);
            strResp = jsonObject.get("Data").toString();

            LogUtils.e("zlservice", strResp);
            if (strResp.equals("[true]")) {
                handler.sendEmptyMessage(2);// 评论成功
                // getDiscuss(oderNo, handler);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.toString() + "");
        }
    }

    /***
     * 新加日志联系记录评论
     *
     * @param oderNo  联系记录编号
     * @param content 评论内容
     * @param handler
     */
    public void publishContactsDiscuss(String oderNo, String content,
                                       Handler handler) {
        String methodName = "Customer/AddContactsDiscuss";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("kjxTest", "=====" + url);
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("OrderNo", oderNo);
            jo2.put("Content", content);
            try {
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.i("zlservice", oderNo + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject(strResp);
            strResp = jsonObject.get("Data").toString();
            if (strResp.equals("[true]")) {
                handler.sendEmptyMessage(2);// 评论成功
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id取得员工信息
     *
     * @param userIds 员工id集 如‘12’;'13';'14';
     * @return
     */
    public String getNamesById(String userIds) {
        String methodName = "Account/GetEmployeeNameById/";
        String Parameter = Global.mUser.CorpId + "/" + userIds;
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        String userNames = "";
        try {
            userNames = mHttpUtils.get(url);
            LogUtils.i("keno5", userIds + "----->" + userNames);
            return userNames;
        } catch (Exception e) {
            return "服务器连接失败！";
        }
    }

    /**
     * 获得本地客户列表
     *
     * @param context 上下文
     * @return 客户列表List<Map<String, Object>>
     * @author kjx
     */
    public List<Map<String, Object>> getClientList(Context context) {
        List<Client> list = new ArrayList<Client>();
        ORMDataHelper helper = ORMDataHelper.getInstance(context);
        try {
            Dao<Client, Integer> dao = helper.getDao(Client.class);
            list = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        LogUtils.i("keno9", "length---->" + list.size());

        List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            Client item = list.get(i);
            LogUtils.i("keno9", item.getCustomerName());
            map.put("id", list.get(i).getId());
            map.put("clientName", list.get(i).getCustomerName());
            listItem.add(map);
        }
        return listItem;
    }

    /**
     * 获得图片地址
     *
     * @param context 上下文
     * @param photoNo 图片序列号 12,13,14这种
     * @return 图片地址
     */
    public String getPhotoAddr(Context context, String photoNo) {
        String methodName = "FileUpDownload/GetAttachments?ids=";
        String Parameter = photoNo;
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        String photoAddress = "";
        try {
            LogUtils.i("photourl", url);
            photoAddress = mHttpUtils.httpGet(url);
            LogUtils.i("photoAddress", photoAddress);
            JSONObject jsonObjectsStart = new JSONObject(photoAddress);
            photoAddress = jsonObjectsStart.get("Data").toString();
            return photoAddress;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 根据图片id获得图片地址
     *
     * @param context 上下文
     * @param photoNo 图片序列号1022
     * @return 图片地址
     */
    public String getPhotoUrl(Context context, String photoNo, Handler handler) {
        String methodName = "FileUpDownload/GetAttachments?ids=";
        String Parameter = photoNo;
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        String photoAddress = "";
        String avataUrl = "";
        try {
            photoAddress = mHttpUtils.httpGet(url);
            LogUtils.i("photoAddress", photoAddress);
            // List<Attach> list = JsonUtils.ConvertJsonToList(photoAddress,
            // Attach.class);
            // if (list.size() > 0) {
            // attach = list.get(0);
            // }
            JSONObject jsonObjectsStart = new JSONObject(photoAddress);
            photoAddress = jsonObjectsStart.get("Data").toString();

            LogUtils.i("attachId", photoNo + "\n" + photoAddress);
            try {
                JSONObject jObject = new JSONObject(photoAddress.substring(1,
                        photoAddress.length() - 1));
                avataUrl = (String) jObject.get("Address");
                LogUtils.i("attachIdUrl", avataUrl);
                Message msg = handler.obtainMessage();
                msg.obj = avataUrl;
                msg.what = 11; //
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return photoAddress;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获得一组附件
     *
     * @param context      上下文
     * @param attachmentNo 附件序列号 12,13,14这种
     * @return 附件地址
     */
    public List<Attach> getAttachmentAddr(Context context, String attachmentNo) {
        List<Attach> attachs = new ArrayList<Attach>();
        String methodName = "FileUpDownload/GetAttachments?ids=";
        String Parameter = attachmentNo;
        String url = Global.BASE_URL + Global.EXTENSION + methodName
                + Parameter;
        String attachmentAddress = "";
        try {
            attachmentAddress = mHttpUtils.httpGet(url);
            LogUtils.i("attachmentAddress", attachmentAddress);
            // List<Attach> list = JsonUtils.ConvertJsonToList(photoAddress,
            // Attach.class);
            // if (list.size() > 0) {
            // attach = list.get(0);
            // }
            JSONObject jsonObjectsStart = new JSONObject(attachmentAddress);
            attachmentAddress = jsonObjectsStart.get("Data").toString();
            JSONArray jsonArray = new JSONArray(attachmentAddress);
            for (int i = 0; i < jsonArray.length(); i++) {
                Attach attach = new Attach();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int Id = jsonObject.getInt("Id");
                String address = jsonObject.getString("Address");
                String nameStr = jsonObject.getString("Name");
                String Suffix = jsonObject.getString("Suffix");
                if (!TextUtils.isEmpty(address) && address.contains("\\")) {
                    attach.setId(Id);
                    attach.setAddress(address);
                    attach.setName(nameStr);
                    attach.setSuffix(Suffix);
                    attachs.add(attach);
                    LogUtils.d("kenoJson", Id + "----" + address);
                }
            }
            return attachs;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据附件id获得一组照片信息
     *
     * @param context
     * @param ids
     * @param photoNo 图片序列号
     * @return
     */
    public List<PhotoInfo> getPhotoInfos(Context context, String ids) {
        ids = formatAttachIds(ids);
        List<PhotoInfo> photoInfos = new ArrayList<PhotoInfo>();
        String json = getPhotoAddr(context, ids);
        LogUtils.i("jsondata", json);
        try {
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() == 0) {
                return photoInfos;
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                PhotoInfo pInfo = new PhotoInfo();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String Id = jsonObject.getString("Id");
                String address = jsonObject.getString("Address");
                String suffix = jsonObject.getString("Suffix");
                if (!TextUtils.isEmpty(address) && address.contains("\\")) {
                    if (!TextUtils.isEmpty(suffix)
                            && (suffix.equalsIgnoreCase("png")
                            || suffix.equalsIgnoreCase("jpg") || suffix
                            .equalsIgnoreCase("gif"))) {
                        int index = address.lastIndexOf("\\");
                        String picName = address.substring(index + 1,
                                address.length()); // 图片名
                        pInfo.setId(Id);
                        pInfo.setAddress(address);
                        pInfo.setPhotoSerialNo(ids);
                        pInfo.setName(picName);
                        photoInfos.add(pInfo);
                        LogUtils.d("kenoJson", Id + "----" + address);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return photoInfos;
    }

    /* 格式化附件号 12,13,14 */
    private String formatAttachIds(String ids) {
        if (ids == null || ids.equals("")) {
            return "";
        }
        if (ids.contains("'")) {
            ids = ids.replace("'", "");
        }
        if (ids.contains("\"")) {
            ids = ids.replace("\"", "");
        }
        if (ids.contains(",")) {
            if (ids.endsWith(";")) { // 针对数据表 12;13;
                ids = ids.substring(0, ids.length() - 1);
            }
        }
        return ids;
    }

    /**
     * 提交销售机会
     */
    public void submitSalesChance(销售机会 item, Handler handler) {
        Message msg = handler.obtainMessage();
        String methodName = "Customer/AddBusinessOpp";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("CustomerId", item.getCustomerId());
            jo2.put("CustomerName", item.getCustomerName());
            jo2.put("Contacts", item.getContacts()); // 发布人
            jo2.put("Salesman", item.getSalesman());
            jo2.put("Phone", item.getPhone());
            jo2.put("Address", item.getAddress());
            jo2.put("Content", item.getContent());
            jo2.put("PlanContactTime", item.getPlanContactTime());
            jo2.put("EstimatedAmount", item.getEstimatedAmount());
            jo2.put("ActualAmount", item.getActualAmount());
            jo2.put("ContactState", item.getContactState());
            strResp = mHttpUtils.postSubmit(url, jo2);
            LogUtils.i("kjx2", "发布结果：" + strResp);
            // 解析提交返回的数据
            JSONObject jsonObject = new JSONObject(strResp);
            strResp = jsonObject.get("Data").toString();
            strResp = strResp.substring(1, strResp.length() - 1);
            LogUtils.i("kjx2", "result：" + strResp);
            msg.obj = strResp;
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
            msg.obj = e;
            handler.sendMessage(msg);
        }
    }

    /**
     * 提交橱柜测量信息
     */
    public void submitTestInfo(Handler handler, 测量信息 item,
                               List<String> photoPathList, ProgressBar pBar) {
        Message msg = handler.obtainMessage();
        String methodName = "Cabinet/SaveMeasaurement";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("Id", item.getId()); // Id不为0，修改
            LogUtils.i("3keno1", "-->" + item.getId());
            jo2.put("Date", item.getDate());
            jo2.put("Address", item.getAddress());
            jo2.put("Designer", item.getDesigner()); // 发布人
            jo2.put("Remark", item.getRemark());
            jo2.put("Chance", item.getChance());
            jo2.put("Staff", item.getStaff());
            jo2.put("Workflow", item.getWorkflow());
            jo2.put("Order", item.getOrder());
            jo2.put("Type", item.getType());
            LogUtils.i("Attachment", item.toString());
            if (photoPathList.size() > 0) {
                // 上传图片
                String attachMent = uploadAttachPhotos(photoPathList, pBar);
                String attachMents = "";
                LogUtils.i("Attachment", photoPathList.get(0));
                LogUtils.i("Attachment", attachMent);
                if (!TextUtils.isEmpty(item.getAttachments())) {
                    attachMents = item.getAttachments() + "," + attachMent;
                } else {
                    attachMents = attachMent;
                }
                LogUtils.d("Attachment", attachMents);
                jo2.put("Attachments", attachMents); // 附件
            }
            strResp = mHttpUtils.postSubmit(url, jo2);
            LogUtils.i("kjx2", "发布结果：" + strResp);
            if ("true".equals(strResp)) {
                msg.what = 3;
                msg.obj = strResp;
                handler.sendMessage(msg);
            } else {
                msg.what = 4;
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg.what = 4;
            msg.obj = e;
            handler.sendMessage(msg);
        }
    }

    /**
     * 获得距离（大于）当前时间最近的任务开始时间
     *
     * @param context
     * @return
     */
    public 任务 getLatelyTaskTime(Context context) {
        任务 latelyItem = new 任务();
        ORMDataHelper helper = ORMDataHelper.getInstance(context);
        try {
            Dao<任务, Integer> dao = helper.getDao(任务.class);
            List<任务> list = dao.queryForAll();
            Date date;
            Date now = new Date();
            Date minDate = null;
            for (任务 item : list) {
                try {
                    if (TextUtils.isEmpty(item.AssignTime)) {
                        return latelyItem;
                    }
                    date = sdf.parse(item.AssignTime);
                    if (date.after(now)) {// 条:1：当前时间之后
                        if (minDate == null) {
                            minDate = date; // 初次赋值
                        }
                        // 条件2：取最小
                        if (date.before(minDate)) {
                            minDate = date;
                            latelyItem = item;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            // LogUtils.d("kenoDate", now.toLocaleString());
            // LogUtils.d("kenoDate", minDate.toLocaleString());
            // where.le("AssignTime", ViewHelper.getDateString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        LogUtils.i("kenoDate", latelyItem.toString());
        return latelyItem;
    }

    public List<任务> getTaskList(Context context, String date) {
        List<任务> list = new ArrayList<任务>();
        ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
        try {
            Dao<任务, Integer> dao = ormDataHelper.getDao(任务.class);
            list = dao.queryBuilder().where()
                    .like("AssignTime", "%" + date + "%").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void isConnectedIntentent(Handler handler) {
        String methodName = "account/GetInternet";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String getBaidu = "";
        try {
            Message msg = new Message();
            getBaidu = mHttpUtils.get(url);
            LogUtils.i("pybaidu", getBaidu);
            if (getBaidu.contains("true")) {
                msg.what = 123;
            } else {
                msg.what = 456;
            }
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.i("pybaidu1", getBaidu);

    }

    // 问题反馈
    public void ReadFeedback(问题反馈 task, Context context, Handler handler) {
        String methodName = "Task/ReadTask";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;

        String strResp = null;
        try {
            JSONObject jo = new JSONObject();
            jo.put("Id", task.Id);
            jo.put("CorpId", Global.mUser.CorpId);
            jo.put("UserId", Global.mUser.Id);
            strResp = mHttpUtils.postSubmit(url, jo);

            ORMDataHelper helper = ORMDataHelper.getInstance(context);
            Dao<问题反馈, Integer> dao = helper.getDao(问题反馈.class);
            int num = dao.update(task);
            handler.sendEmptyMessage(3);
            LogUtils.i("keno0", "修改num----》" + num);
        } catch (Exception e) {
            LogUtils.e("设置任务为已查看失败。", "网络问题。");
        }

        if (task.Readed == null || !task.Readed.contains(Global.mUser.Id)) {
            task.Readed = task.Readed + "," + Global.mUser.Id + ",";
        }
        ORMDataHelper helper = ORMDataHelper.getInstance(context);
        Dao<问题反馈, Integer> dao;
        try {
            dao = helper.getDao(问题反馈.class);
            int num = dao.update(task);

            LogUtils.i("keno0", "修改num----》" + num);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 发布问题反馈
     *
     * @param item     问题反馈对象
     * @param filePath 上传文件列表
     * @param handler
     * @param pBar     进度条
     * @return
     */
    public void PublishFeedback(问题反馈 item, List<String> photoPathList,
                                Handler handler, ProgressBar pBar) {
        String methodName = "FeedBacks/EditFeedBacks";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = "";
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("Id", item.Id);
            jo2.put("CorpId", Global.mUser.CorpId);
            jo2.put("UserId", Global.mUser.Id);
            // jo2.put("Id", map.get("Id").toString());
            jo2.put("Publisher", Global.mUser.Id); // 发布人
            jo2.put("Executor", item.Executor); // 任务执行人默认就是自己
            String participant = item.getParticipant();
            participant = participant.replace("'", "").replace(";", ",");
            jo2.put("Participant", participant);
            jo2.put("Title", item.getTitle());
            jo2.put("Content", item.getContent());
            jo2.put("Time", item.Time); // 发布时间(当前日期)
            jo2.put("AssignTime", item.getAssignTime()); // 开始时间
            jo2.put("Deadline", item.getAssignTime()); // 指定完成时间
            jo2.put("Status", 1); // 默认为启动状态
            jo2.put("ClientId", item.ClientId);
            LogUtils.i("addtask", item.toString());
            if (photoPathList != null && photoPathList.size() > 0) {
                String attachMent = uploadAttachPhotos(photoPathList, pBar);
                LogUtils.i("Attachment", photoPathList.get(0));
                LogUtils.i("Attachment", attachMent);
                jo2.put("Attachment", attachMent); // 附件
            }
            try {
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.i("kjx", "发布结果：" + strResp);
                // 解析提交返回的数据
                JSONObject jsonObject = new JSONObject(strResp);
                strResp = jsonObject.get("Data").toString();
                strResp = strResp.substring(1, strResp.length() - 1);
                LogUtils.i("kjx", "data：" + strResp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (strResp != null) {
            handler.sendEmptyMessage(5);
            LogUtils.i("py", "data：" + strResp);
        } else {
            handler.sendEmptyMessage(6);
            LogUtils.i("py1", "data：" + strResp);
        }
    }

    /**
     * 修改问题反馈
     *
     * @param item    问题反馈对象
     * @param handler
     * @return
     */
    public void EditFeedback(问题反馈 item, Handler handler) {
        String methodName = "FeedBacks/EditFeedBacks";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = "";
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("Id", item.Id);
            jo2.put("CorpId", Global.mUser.CorpId);
            jo2.put("UserId", Global.mUser.Id);
            // jo2.put("Id", map.get("Id").toString());
            jo2.put("Publisher", Global.mUser.Id); // 发布人
            jo2.put("Executor", item.Executor); // 任务执行人默认就是自己
            // String participant = item.getParticipant();
            jo2.put("Participant", item.Participant);
            LogUtils.i("pyParticipant", item.Participant);
            jo2.put("Title", item.getTitle());
            jo2.put("Content", item.getContent());
            jo2.put("Time", item.Time); // 发布时间(当前日期)
            jo2.put("AssignTime", item.getAssignTime()); // 指定完成时间
            jo2.put("Deadline", item.getAssignTime()); // 指定完成时间
            jo2.put("Status", 1); // 默认为启动状态
            jo2.put("ClientId", item.ClientId);
            jo2.put("UpdateTime", item.Time);// 修改最后时间
            LogUtils.i("edittask", item.toString());
            try {
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.i("kjx", "发布结果：" + strResp);
                // 解析提交返回的数据
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册新企业
     *
     * @param corpName 企业名
     * @param pwd      密码
     * @param phone    电话号码
     * @param contacts 联系人
     * @param handler
     */
    public boolean RegisterCorp(String corpName, String pwd, String phone,
                                String contacts) {
        boolean result = false;
        String methodName = "account/RegisterCorp/";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = "";
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("Name", corpName);
            jo2.put("Password", pwd);
            jo2.put("Phone", phone);
            jo2.put("Contacts", contacts);
            strResp = mHttpUtils.post(url, jo2);
            LogUtils.i("kjxre", "发布结果：" + strResp);
            // JSONObject jsonObject = new JSONObject(strResp);
            // int resultStatus = jsonObject.getInt("Status");
            ReturnModel<String> returnModel = JsonUtils.pareseResult(strResp);
            // Message msg = handler.obtainMessage();
            if (returnModel.Status == 1) {
                // msg.what = 3; // 成功
                result = true;
            } else if (returnModel.Status == 0) {
                // msg.what = 2; // 失败
            }
            // msg.obj = returnModel.Message;
            // handler.sendMessage(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发布公司空间帖子
     *
     * @param title   标题
     * @param content 帖子内容
     * @return
     */
    public Boolean WriteCompanySpace(String title, int board, String content,
                                     String ExpirationTime, List<String> photoPathList, Handler handler,
                                     ProgressBar progressBar) {
        String methodName = "tiezi/AddTiezi";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("WriteCompanySpace", url);
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("Poster", Global.mUser.Id);
            // jo2.put("Personnel", receiverId);
            jo2.put("Title", title);
            jo2.put("board", board);
            jo2.put("Content", content);
            jo2.put("PostTime", ExpirationTime);
            LogUtils.i("post_submit", "---" + title + "---" + board + "---"
                    + content + "---" + ExpirationTime);
            // jo2.put("ReleaseTime", ExpirationTime);
            if (photoPathList.size() > 0) {
                String attachMent = uploadAttachPhotos(photoPathList,
                        progressBar);
                LogUtils.i("Attachment", photoPathList.get(0));
                attachMent = attachMent.replace("\"", "");
                LogUtils.i("Attachment", attachMent);
                jo2.put("Attachment", attachMent); // 附件
            }

            try {
                LogUtils.i("CompanySpaceInfo", "OK");
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.i("CompanySpaceInfo", strResp);
                // 解析提交返回的数据
                JSONObject jsonObject = new JSONObject(strResp);
                String str = jsonObject.get("Status").toString();
                LogUtils.i("pystrResp", str);
                if (str.contains("1")) {
                    handler.sendEmptyMessage(CompanySpaceNewActivity.UPDATA_SUCCESED);
                } else {
                    handler.sendEmptyMessage(CompanySpaceNewActivity.UPDATA_FAILED);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("erroResult", e + "");
                handler.sendEmptyMessage(NoticeNewActivity.UPDATA_FAILED);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Boolean(strResp);
    }

    /**
     * 新建公司空间帖子
     *
     * @param title   标题
     * @param content 帖子内容
     * @return
     */
    public Boolean addCompanySpace(帖子 tiezi, Handler handler) {
        String methodName = "tiezi/AddTiezi";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("WriteCompanySpace", url);
        String strResp = null;
        try {
            JSONObject jo2 = JsonUtils.initJsonObj(tiezi);
            strResp = mHttpUtils.postSubmit(url, jo2);
            LogUtils.i("CompanySpaceInfo", strResp);
            // 解析提交返回的数据
            JSONObject jsonObject = new JSONObject(strResp);
            String str = jsonObject.get("Status").toString();
            LogUtils.i("pystrResp", str);
            if (str.contains("1")) {
                handler.sendEmptyMessage(CompanySpaceNewActivity.UPDATA_SUCCESED);
            } else {
                handler.sendEmptyMessage(CompanySpaceNewActivity.UPDATA_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("erroResult", e + "");
            handler.sendEmptyMessage(NoticeNewActivity.UPDATA_FAILED);
        }
        return new Boolean(strResp);
    }

    /**
     * 获得客户本地分类
     *
     * @return
     */
    public List<Client> getClientLocalCategray(Context context) {
        List<Client> list = new ArrayList<Client>();
        ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
        try {
            Dao<Client, Integer> dao = ormDataHelper.getDao(Client.class);
            // 升序排序
            list = dao.queryBuilder().groupBy("Classification")
                    .orderBy("Classification", true).where()
                    .isNotNull("ClassificationName").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获得客户分类（网络分类）
     *
     * @return
     */
    public List<Categray> getClientCategray(Context context) {
        List<Categray> list = new ArrayList<Categray>();
        String url = Global.BASE_URL + "Client/GetClientCategraty";
        // String url = Global.BASE_URL + "Client/GetClientDataCategraty";
        LogUtils.i("getClientCategray", url);
        HttpUtils httpUtils = new HttpUtils();
        String result = httpUtils.httpGet(url);
        LogUtils.i("getClientCategray", result);
        list = JsonUtils.ConvertJsonToList(result, Categray.class);
        LogUtils.i("size", "size=" + list.size());
        return list;
    }

    /**
     * 获得客户字段描述表
     *
     * @return
     */
    public List<字段描述> getFieldList(Context context, String tableName)
            throws Exception {
        List<字段描述> list = new ArrayList<字段描述>();
        String url = Global.BASE_URL + "account/Get表字段描述s/" + tableName;
        HttpUtils httpUtils = new HttpUtils();
        String result = httpUtils.httpGet(url);
        LogUtils.i("getFieldList", url + "\n" + result);
        list = JsonUtils.ConvertJsonToList(result, 字段描述.class);
        LogUtils.i("size", "size=" + list.size());
        return list;
    }

    /**
     * ??????????
     *
     * @param clientId       ???
     * @param content        ????
     * @param ExpirationTime ???????
     * @return
     * @author py 2014.8.18
     */
    public Boolean WriteSuggest(int clientId, String content,
                                String ExpirationTime, List<String> photoPathList, Handler handler,
                                ProgressBar progressBar) {
        String methodName = "FeedBacks/EditClientComplaint";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("WriteSuggest", url);
        LogUtils.i("WriteSuggest", ExpirationTime);
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("ClientId", clientId);
            jo2.put("Content", content);
            jo2.put("Time", ExpirationTime);
            if (photoPathList.size() > 0) {
                String attachMent = uploadAttachPhotos(photoPathList,
                        progressBar);
                LogUtils.i("Attachment", photoPathList.get(0));
                attachMent = attachMent.replace("\"", "");
                LogUtils.i("Attachment", attachMent);
                jo2.put("Attachment", attachMent); // ????
            }
            try {
                LogUtils.i("suggestInfo", "OK");
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.i("suggestInfo", strResp);
                // ???????????????
                JSONObject jsonObject = new JSONObject(strResp);
                // strResp = jsonObject.get("Data").toString();
                String str = jsonObject.get("Status").toString();
                LogUtils.i("pystrResp", str);
                if (str.contains("1")) {
                    handler.sendEmptyMessage(SuggestNewActivity.UPDATA_SUCCESED);
                } else {
                    handler.sendEmptyMessage(SuggestNewActivity.UPDATA_FAILED);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("erroResult", e + "");
                handler.sendEmptyMessage(SuggestNewActivity.UPDATA_FAILED);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Boolean(strResp);
    }

    /**
     * ?????????
     *
     * @param clientId       ???
     * @param content        ????
     * @param ExpirationTime ???????
     * @return
     * @author py 2014.8.18
     */
    public Boolean EditSuggest(int id, int clientId, String content,
                               String ExpirationTime, Handler handler, String Attachment) {
        String methodName = "FeedBacks/EditClientComplaint";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("WriteSuggest", url);
        LogUtils.i("WriteSuggest", ExpirationTime);
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();

            jo2.put("ClientId", clientId);
            jo2.put("Id", id);
            jo2.put("Content", content);
            jo2.put("Time", ExpirationTime);
            jo2.put("Attachment", Attachment);
            try {
                LogUtils.i("suggestInfo", "OK");
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.i("suggestInfo", strResp);
                // ???????????????
                JSONObject jsonObject = new JSONObject(strResp);
                String str = jsonObject.get("Status").toString();
                LogUtils.i("pystrResp", str);
                if (str.contains("1")) {
                    handler.sendEmptyMessage(SuggestNewActivity.UPDATA_SUCCESED);
                } else {
                    handler.sendEmptyMessage(SuggestNewActivity.UPDATA_FAILED);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("erroResult", e + "");
                handler.sendEmptyMessage(SuggestNewActivity.UPDATA_FAILED);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Boolean(strResp);
    }

    /***
     * 新加帖子回复
     *
     * @param oderNo  表单单号
     * @param content 帖子内容
     * @param handler
     * @author py 2014.8.21
     */
    public void publishRepaly(int oderNo, String content, String time,
                              String id, Handler handler) {
        String methodName = "tiezi/AddTieziDiscuss";
        // String methodName = "Customer/AddDiscuss";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("pyreply_url", "=====" + url);
        LogUtils.i("pycompany", content);
        LogUtils.i("pycompany", oderNo + "");
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("内容", content);
            jo2.put("论坛发帖", oderNo);
            try {
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.i("zlservice", strResp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject(strResp);
            String str = jsonObject.get("Status").toString();
            LogUtils.i("pystrResp", str);
            if (str.contains("1")) {
                handler.sendEmptyMessage(2);// 评论成功
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     * 新加帖子回复
     *
     * @param oderNo  表单单号
     * @param content 帖子内容
     * @param handler
     * @author zmy 2014.8.21
     */
    public String publishRepaly(int oderNo, String content) {
        String str = null;
        String methodName = "tiezi/AddTieziDiscuss";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("pyreply_url", "=====" + url);
        LogUtils.i("pycompany", content);
        LogUtils.i("pycompany", oderNo + "");
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("内容", content);
            jo2.put("论坛发帖", oderNo);
            try {
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.i("zlservice", strResp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject(strResp);
            str = jsonObject.get("Status").toString();
            LogUtils.i("zmy", str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    /***
     * 删除回复
     *
     * @param oderNo  表单单号
     * @param content 帖子内容
     * @param handler
     */
    public String deleteTiezi(int tieziId) {
        String str = null;
        String methodName = "tiezi/DeleteTiezi/" + tieziId;
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("pyreply_url", "=====" + url);
        String strResp = null;
        try {
            try {
                strResp = mHttpUtils.httpGet(url);
                LogUtils.i("zlservice", strResp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject(strResp);
            str = jsonObject.get("Status").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 获取论坛回帖列表
     *
     * @param orderNo 表单编号
     * @author py 2014.8.21
     */
    public void getReply(int orderNo, HandlerNewContact handler) {
        Message msg = handler.obtainMessage();
        String methodName = "tiezi/GetTieziDiscussList/";
        String Parameter = orderNo + "";
        String url = Global.BASE_URL + methodName + Parameter;
        List<论坛回帖> list = new ArrayList<论坛回帖>();
        String data = "";
        try {

            LogUtils.i("kjxtieziData", "url-->" + url);
            data = mHttpUtils.httpGet(url);
            LogUtils.i("kjxtieziData", "length-->" + data);
            if (!TextUtils.isEmpty(data)) {
                list = JsonUtils.ConvertJsonToList(data, 论坛回帖.class);
            }
            msg.obj = list;
            msg.what = 5;// 获得回帖列表成功
        } catch (Exception e) {
            e.printStackTrace();
            msg.obj = e;
            msg.what = 6;
            LogUtils.e("kjxData", "kjxData Exception:" + e);
        } finally {
            handler.sendMessage(msg);
        }
    }

    /**
     * 清除临时日志
     *
     * @param context
     */
    public void deleteTempLog(Context context) {
        ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
        try {
            Dao<日志, Integer> dao = ormDataHelper.getDao(日志.class);
            DeleteBuilder<日志, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq("isTemp", "true");
            int num = deleteBuilder.delete();
            LogUtils.i("deleteTempLog", "deleteTempLog=" + num);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 清除临时联系记录
     *
     * @param context
     */
    public void deleteTempContact(Context context) {
        ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
        try {
            Dao<客户联系记录, Integer> dao = ormDataHelper.getDao(客户联系记录.class);
            DeleteBuilder<客户联系记录, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq("isTemp", "true");
            int num = deleteBuilder.delete();
            LogUtils.i("deleteTempContact", "deleteTempContact=" + num);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取任务评论列表
     *
     * @param orderNo 表单编号
     * @return 任务评论列表
     * <p/>
     * py 2014-08-28
     */
    public List<评论> getTaskDiscuss(String orderNo) {
        String methodName = "Task/GetCommentList?&OrderNo=";
        String url = Global.BASE_URL + Global.EXTENSION + methodName + orderNo;
        List<评论> list = new ArrayList<评论>();
        String data = "";
        try {
            data = mHttpUtils.httpPost(url);
            LogUtils.i("pytaskurl", "-->" + url);
            LogUtils.i("pytaskData", "length-->" + data);
            if (!TextUtils.isEmpty(data)) {
                list = JsonUtils.ConvertJsonToList(data, 评论.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("kjxData", "kjxData Exception:" + e);
        }
        return list;
    }

    /***
     * 新加任务评论
     *
     * @param oderNo  联系记录编号
     * @param content 评论内容
     * @param handler
     */
    public void publishTaskDiscuss(String oderNo, String content,
                                   Handler handler) {
        String methodName = "Task/AddTaskComment";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("kjxTest", "=====" + url);
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("OrderNo", oderNo);
            jo2.put("Content", content);
            try {
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.i("zlservice", strResp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject(strResp);
            strResp = jsonObject.get("Data").toString();
            if (strResp.equals("[true]")) {
                handler.sendEmptyMessage(2);// 评论成功
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     * 新加任务评论
     *
     * @param oderNo  任务编号
     * @param content 评论内容
     */
    public boolean publishTaskDiscuss(String oderNo, String content) {
        boolean isSuccess = false;
        String methodName = "Task/AddTaskComment";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = null;
        try {
            JSONObject jo2 = new JSONObject();
            jo2.put("OrderNo", oderNo);
            jo2.put("Content", content);
            try {
                strResp = mHttpUtils.postSubmit(url, jo2);
                LogUtils.i("zlservice", strResp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject(strResp);
            strResp = jsonObject.get("Data").toString();
            // String statusStr = JsonUtils.getStringValue(strResp,
            // JsonUtils.KEY_STATUS);
            // if (statusStr.equals("1")) {
            if (strResp.equals("[true]")) {
                // LogUtils.i(TAG, "status=" + statusStr);
                // handler.sendEmptyMessage(2);// 评论成功
                isSuccess = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    /**
     * 根据指定id查询本地对应的联系记录
     *
     * @param id
     * @param context
     * @return 没有查到则返回null
     */
    public 客户联系记录 getContactById(String id, Context context) {
        客户联系记录 item = null;
        try {
            ORMDataHelper helper = ORMDataHelper.getInstance(context);
            Dao<客户联系记录, Integer> dao = helper.getDao(客户联系记录.class);
            List<客户联系记录> list = dao.queryBuilder().where().eq("Id", id).query();
            if (list != null && list.size() > 0) {
                LogUtils.i("notificaitonCo", "客户联系记录.size()--" + list.size());
                item = list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("keno2Exception", "------>Exception:" + e.getMessage());
        }
        return item;
    }

    /**
     * 清除考勤列表中临时数据
     *
     * @param context
     */
    public void deleteTempTagInfo(Context context) {
        ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
        try {
            Dao<考勤信息, Integer> dao = ormDataHelper.getDao(考勤信息.class);
            DeleteBuilder<考勤信息, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq("isTemp", "true");
            int num = deleteBuilder.delete();
            LogUtils.i("deleteTempTagInfo", "deleteTempTagInfo=" + num);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * （公共方法）清除缓存数据
     *
     * @param context
     * @param classType  实体类型
     * @param columnName 排序字段名称
     * @param isSaveTemp 是否保存缓存缓存,true:保存10条缓存，false:则清空所有数据
     */
    public void deleteTempData(Context context, Class classType,
                               String columnName, boolean isSaveTemp) {
        ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
        try {
            Dao dao = ormDataHelper.getDao(classType);
            if (isSaveTemp) {
                List list = dao.queryBuilder().offset((long) 10)
                        .limit((long) 10000).orderBy(columnName, false).query();
                int queryNum = list.size();
                LogUtils.d("onDestroy", "查询数量=" + queryNum);
                int deleteNum = dao.delete(list);
                // deleteBuilder.setWhere(deleteBuilder.where());
                // int num = deleteBuilder.delete();
                LogUtils.i("onDestroy", "deleteTempTagInfo=" + deleteNum);
            } else {
                dao.deleteBuilder().delete();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取任务分类列表
     *
     * @return 任务分类列表
     * <p/>
     * py 2014-08-28
     */
    public void getTaskClassify(Handler handler) {
        Message msg = handler.obtainMessage();
        String methodName = "Task/GetCategrayList";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        List<任务分类> list = new ArrayList<任务分类>();
        String data = "";
        try {
            data = mHttpUtils.httpPost(url);
            LogUtils.i("pytaskClassifyurl", "-->" + url);
            LogUtils.i("pytaskClassifyData", "length-->" + data);
            if (!TextUtils.isEmpty(data)) {
                list = JsonUtils.ConvertJsonToList(data, 任务分类.class);
            }
            msg.obj = list;
            msg.what = 7;// 获得任务分类列表成功
        } catch (Exception e) {
            e.printStackTrace();
            msg.obj = e;
            msg.what = 8;
            LogUtils.e("kjxData", "kjxData Exception:" + e);
        } finally {
            handler.sendMessage(msg);
        }
    }

    // 获取联系状态
    /*
     * @author py 2014.09.15
	 */
    public String getConstactStatus() {
        String methodName = "Dictionary/GetDict/联系状态";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        String strResp = "";
        String data = "";
        try {
            data = mHttpUtils.httpGet(url);
            JSONObject jsonObject = new JSONObject(data);
            strResp = jsonObject.get("Data").toString();
            LogUtils.i("statusSpLit1", strResp);
            strResp = strResp.replaceAll("\"", "");
            strResp = strResp.replace("{", "");
            strResp = strResp.replace("}", "");
            strResp = strResp.replace("[", "");
            strResp = strResp.replace("]", "");
            LogUtils.i("statusSpLit1", strResp);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strResp;
    }

    /**
     * 根据字典名称获得一个字典的集合
     *
     * @param dictName 字典名称
     * @return
     */
    public List<字典> getDictList(String dictName) {
        String url = Global.BASE_URL + "Dictionary/GetDictJson/" + dictName;

        LogUtils.i("getDict", url);
        List<字典> list = new ArrayList<字典>();
        HttpUtils httpUtils = new HttpUtils();
        String data = httpUtils.httpGet(url);
        LogUtils.i("getDict", data);
        list = JsonUtils.ConvertJsonToList(data, 字典.class);
        return list;
    }

    /***
     * 根据字典名称获得一个字典的集合
     *
     * @param dictTableName      字典表名称,如果是普通字典表，其余两个参数可传null
     * @param originalColumnName 原始数据库列表 AS 字典.名称
     * @param filter             附加条件
     * @return
     */
    public List<字典> getCustomDicts(String dictTableName,
                                   String originalColumnName, String 附加条件) {
        String url = Global.BASE_URL + "Dictionary/GetCustomDicts/";

        LogUtils.i("getDict", url);
        List<字典> list = new ArrayList<字典>();
        HttpUtils httpUtils = new HttpUtils();
        JSONObject jo = new JSONObject();
        try {
            jo.put("dictTableName", dictTableName);
            jo.put("originalColumnName", originalColumnName);
            jo.put("附加条件", 附加条件);
            LogUtils.i("getCustomDicts", jo.toString() + "");
            String data = httpUtils.postSubmit(url, jo);
            LogUtils.i("getDict", data);
            list = JsonUtils.ConvertJsonToList(data, 字典.class);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获得部门信息列表
     *
     * @return
     */
    public List<部门> getDepartmentList(Context context) {
        List<部门> list = new ArrayList<部门>();
        ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
        try {
            Dao<部门, Integer> dao = ormDataHelper.getDao(部门.class);
            list = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 根据字典名称获得多项字典表
     *
     * @param dictNames 员工,企业
     * @return
     */
    public List<List<Dict>> GetDicts(String dictNames) {
        List<List<Dict>> list = new ArrayList<List<Dict>>();
        String methodName = "dictionary/Get多个字典的字典项";
        String url = Global.BASE_URL + Global.EXTENSION + methodName + "/"
                + dictNames;
        String strResp = null;

        strResp = mHttpUtils.httpGet(url);
        LogUtils.i(TAG, strResp);
        JSONObject jsonObjectsStart;

        Type type = new TypeToken<List<List<Dict>>>() {
        }.getType(); // 指定集合对象属性
        Gson gson = new Gson();
        try {
            jsonObjectsStart = new JSONObject(strResp);
            strResp = jsonObjectsStart.get("Data").toString();

            list = gson.fromJson(strResp, type);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            LogUtils.e(TAG, "" + e);
        }
        return list;
    }

    /**
     * 新建客户
     */
    public boolean SaveClient(JSONObject jo2) {
        boolean result = false;
        String methodName = "Customer/Save客户";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i(TAG, url);
        String strResp = null;
        try {
            strResp = mHttpUtils.postSubmit(url, jo2);
            LogUtils.e(TAG, "" + strResp);
            ReturnModel<String> returnModel = JsonUtils.pareseResult(strResp);
            if (returnModel.Status == 1) {
                result = true;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            LogUtils.e(TAG, "" + e1);
        }
        return result;
    }

    /**
     * 保存设备token
     *
     * @param device_token 设备token
     * @return
     */
    public boolean SetMobileDeviceToken(String device_token, String deviceName) {
        boolean result = false;
        String methodName = "account/SetMobileDeviceTokenV15630/"
                + device_token.trim().replace(" ", "") + "/android/"
                + deviceName.trim().replace(" ", "");
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i(TAG, url);
        String strResp = null;
        try {
            strResp = mHttpUtils.httpGet(url);
            LogUtils.e(TAG, "" + strResp);
            ReturnModel<String> returnModel = JsonUtils.pareseResult(strResp);
            if (returnModel.Status == 1) {
                result = true;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            LogUtils.e(TAG, "" + e1);
        }
        return result;
    }

    /**
     * 清空设备token
     *
     * @param device_token 设备token
     * @return
     */
    public boolean clearMobileDeviceToken1() {
        boolean result = false;
        String methodName = "account/clearMobileDeviceToken/android";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i(TAG, url);
        String strResp = null;
        try {
            strResp = mHttpUtils.httpGet(url);
            LogUtils.e(TAG, "" + strResp);
            ReturnModel<String> returnModel = JsonUtils.pareseResult(strResp);
            if (returnModel.Status == 1) {
                result = true;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            LogUtils.e(TAG, "" + e1);
        }
        return result;
    }

    /**
     * 清空设备token
     *
     * @param device_token 设备token
     * @return
     */
    public boolean clearMobileDeviceTokenV710(Context context) {
        boolean result = false;
        String token = XGPushConfig.getToken(context);
        String methodName = "account/clearMobileDeviceTokenV710/android/";
        String url = Global.BASE_URL + Global.EXTENSION + methodName + token;
        LogUtils.i(TAG, url);
        String strResp = null;
        try {
            strResp = mHttpUtils.httpGet(url);
            LogUtils.e(TAG, "" + strResp);
            ReturnModel<String> returnModel = JsonUtils.pareseResult(strResp);
            if (returnModel.Status == 1) {
                result = true;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            LogUtils.e(TAG, "" + e1);
        }
        return result;
    }

    /**
     * 根据编号 获得动态信息(动态信息可能包含有日志，任务，通知中的某一项)
     *
     * @param dynamicType 动态类型
     * @param dataId      数据编号（日志，任务...的编号）
     * @return
     */
    public 动态 LoadDynamicById(String dynamicType, String dataId) {
        动态 dynamic = null;
        String methodName = "dynamic/LoadDynamicById/" + dynamicType + "/"
                + dataId;
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i(TAG, url);
        String strResp = null;
        try {
            strResp = mHttpUtils.httpGet(url);
            LogUtils.e(TAG, "" + strResp);
            ReturnModel<String> returnModel = JsonUtils.pareseResult(strResp);
            if (returnModel.Status == 1) {
                List<动态> list = JsonUtils.ConvertJsonToList(strResp, 动态.class);
                if (list != null && list.size() > 0) {
                    dynamic = list.get(0);
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            LogUtils.e(TAG, "" + e1);
        }
        return dynamic;
    }

    /**
     * 保存客户联系人
     *
     * @param item
     * @param handler
     * @return
     */
    public boolean SaveContactPerson(联系人 mContacts) {
        boolean isResult = false;
        String methodName = "Communication/SaveContactPerson";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i("attachPath", url);
        String strResp = null;
        try {
            JSONObject jo2 = JsonUtils.initJsonObj(mContacts, 联系人.class);
            strResp = mHttpUtils.postSubmit(url, jo2);
            ReturnModel<String> returnModel = JsonUtils.pareseResult(strResp);
            if (returnModel.Status == 1) { // 保存成功
                isResult = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtils.i("attachPath", "" + e);
        } catch (Exception e) {
            LogUtils.i("attachPath", "" + e);
        }
        return isResult;
    }

    /**
     * 递归查询所有子部门编号
     *
     * @param pId      部门编号
     * @param context
     * @param sBuilder 存放返回部门及子部门 如：1,4,52,53,54,59,60,61,5,
     */
    public void getAllDeptAndSubDept(String pId, Context context,
                                     StringBuilder sBuilder) {
        ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
        try {
            Dao<部门, Integer> dao = ormDataHelper.getDao(部门.class);
            List<部门> deptList = dao.queryBuilder().where().eq("上级", pId)
                    .query();
            for (int i = 0; i < deptList.size(); i++) {
                // 1,2,3
                sBuilder.append(deptList.get(i).get编号() + ",");
                // 递归
                getAllDeptAndSubDept(deptList.get(i).get编号() + "", context,
                        sBuilder);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /***
     * 发钻石
     *
     * @param count    数量
     * @param from     发钻人
     * @param to       接收人
     * @param dataType 数据类型 1: 公告通知 2:员工日志 3:任务 4:申请审批 5:邮件 6:通讯录 7:客户 8:客户联系记录
     *                 9:销售机会
     * @param dataId   数据编号
     * @return
     */
    public boolean giveDiamond(int count, int from, int to, int dataType,
                               int dataId) {
        boolean isSuccess = false;
        String methodName = "Diamond/GiveDiamond/" + count + "/" + from + "/"
                + to + "/" + dataType + "/" + dataId;
        String url = Global.BASE_URL_PROCESS + methodName;
        String result = mHttpUtils.httpGet(url);
        String stautu = JsonUtils.parseStatus(result);
        if (!TextUtils.isEmpty(stautu) && stautu.contains("1")) {
            isSuccess = true;
        }
        return isSuccess;
    }

    /***
     * 取消发钻石
     *
     * @param count    数量
     * @param from     发钻人
     * @param to       接收人
     * @param dataType 数据类型 1: 公告通知 2:员工日志 3:任务 4:申请审批 5:邮件 6:通讯录 7:客户 8:客户联系记录
     *                 9:销售机会
     * @param dataId   数据编号
     * @return
     */
    public boolean removeDiamond(int from, int dataType, int dataId) {
        boolean isSuccess = false;
        String methodName = "Diamond/RemoveDiamond/" + from + "/" + dataType
                + "/" + dataId;

        String url = Global.BASE_URL_PROCESS + methodName;
        String result = mHttpUtils.httpGet(url);
        String stautu = JsonUtils.parseStatus(result);
        if (!TextUtils.isEmpty(stautu) && stautu.contains("1")) {
            isSuccess = true;
        }
        return isSuccess;
    }

    /***
     * 取消点赞
     *
     * @param count    数量
     * @param from     发钻人
     * @param to       接收人
     * @param dataType 数据类型 1: 公告通知 2:员工日志 3:任务 4:申请审批 5:邮件 6:通讯录 7:客户 8:客户联系记录
     *                 9:销售机会
     * @param dataId   数据编号
     * @return
     */
    public boolean RemoveSupport(int from, int dataType, int dataId) {
        boolean isSuccess = false;
        String methodName = "Diamond/RemoveSupport/" + from + "/" + dataType
                + "/" + dataId;

        String url = Global.BASE_URL_PROCESS + methodName;
        String result = mHttpUtils.httpGet(url);
        String stautu = JsonUtils.parseStatus(result);
        if (!TextUtils.isEmpty(stautu) && stautu.contains("1")) {
            isSuccess = true;
        }
        return isSuccess;
    }

    /***
     * 点赞
     *
     * @param from     点赞人
     * @param to       接收人
     * @param dataType 数据类型 1: 公告通知 2:员工日志 3:任务 4:申请审批 5:邮件 6:通讯录 7:客户 8:客户联系记录
     *                 9:销售机会
     * @param dataId   数据编号
     * @return
     */
    public boolean giveSupport(String from, String to, int dataType, int dataId) {
        boolean isSuccess = false;
        String methodName = "Diamond/GiveSupport/" + from + "/" + to + "/"
                + dataType + "/" + dataId;
        String url = Global.BASE_URL_PROCESS + methodName;
        String result = mHttpUtils.httpGet(url);
        String stautu = JsonUtils.parseStatus(result);
        if (!TextUtils.isEmpty(stautu) && stautu.contains("1")) {
            isSuccess = true;
        }
        return isSuccess;
    }

    /**
     * 周工作总结
     */
    public boolean SaveWeekLog(周工作总结 weekLog) {
        boolean result = false;
        String methodName = "log/SaveWeekLog";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i(TAG, url);
        String strResp = null;
        try {
            JSONObject jo2 = JsonUtils.initJsonObj(weekLog);
            strResp = mHttpUtils.postSubmit(url, jo2);
            LogUtils.e(TAG, "" + strResp);
            ReturnModel<String> returnModel = JsonUtils.pareseResult(strResp);
            if (returnModel.Status == 1) {
                result = true;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            LogUtils.e(TAG, "" + e1);
        }
        return result;
    }

    /**
     * 周工作总结
     */
    public boolean SaveWorkReport(工作总结报告 weekLog) {
        boolean result = false;
        String methodName = "log/SaveWorkReport";
        String url = Global.BASE_URL + Global.EXTENSION + methodName;
        LogUtils.i(TAG, url);
        String strResp = null;
        try {
            JSONObject jo2 = JsonUtils.initJsonObj(weekLog);
            strResp = mHttpUtils.postSubmit(url, jo2);
            LogUtils.e(TAG, "" + strResp);
            ReturnModel<String> returnModel = JsonUtils.pareseResult(strResp);
            if (returnModel.Status == 1) {
                result = true;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            LogUtils.e(TAG, "" + e1);
        }
        return result;
    }

    /**
     * 存入本地序列化对象
     */
    public static User getLocalSerializableUser() {
        try {
            FileInputStream fs = new FileInputStream(
                    FilePathConfig.getThumbDirPath()
                            + FilePathConfig.getLocalSerilizeFileName());
            ObjectInputStream ois = new ObjectInputStream(fs);
            User user = (User) ois.readObject();
            ois.close();
            return user;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
