package com.cedarhd.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.cedarhd.ApplyListFragmentActivity;
import com.cedarhd.ClientConstactInfoActivity;
import com.cedarhd.ClientConstactListActivity;
import com.cedarhd.ClientListActivity;
import com.cedarhd.NoticeListActivity;
import com.cedarhd.OrderListActivity;
import com.cedarhd.R;
import com.cedarhd.TabMainActivity;
import com.cedarhd.TaskListActivityNew;
import com.cedarhd.User_SelectActivityNew;
import com.cedarhd.WorkLogActivity;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ServerCall;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Client;
import com.cedarhd.models.Data;
import com.cedarhd.models.Demand;
import com.cedarhd.models.Notifications;
import com.cedarhd.models.RemindApproval;
import com.cedarhd.models.RemindEmail;
import com.cedarhd.models.RemindNotice;
import com.cedarhd.models.RemindOrder;
import com.cedarhd.models.RemindTask;
import com.cedarhd.models.User;
import com.cedarhd.models.任务;
import com.cedarhd.models.动态;
import com.cedarhd.models.客户联系记录;
import com.cedarhd.models.已提醒;
import com.cedarhd.models.日志;
import com.cedarhd.models.日志评论;
import com.cedarhd.models.评论;
import com.cedarhd.models.通知;
import com.cedarhd.models.部门;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/***
 * 舊版推送服務
 *
 * @author Administrator
 *
 */
@SuppressLint("NewApi")
@Deprecated
public class NotificationService extends Service {
    static public String ServiceState = "";
    String TAG = "NotificationService";
    ZLServiceHelper mDataHelper = new ZLServiceHelper();
    ServerCall serverCall = new ServerCall();
    HttpUtils httpUtils = new HttpUtils();
    public RemindHandler handlerRemind = new RemindHandler();
    private static SharedPreferences spRemind;
    private static boolean notice;
    private static boolean email;
    private static boolean log;
    private static boolean client;
    private static boolean order;
    private static boolean contact;
    private static boolean task;
    private static boolean saleChance;
    private static boolean approval; // 待我审批
    Dao dao已提醒;
    private static Context mcontext;
    ORMDataHelper ormDataHelper;
    Dao<User, Integer> userDao;
    Dao<Client, Integer> clientDao;
    static Handler menuHandler;

    public static void bindContext(Context context) {
        if (mcontext == null) {
            mcontext = Global.mContext;
            spRemind = mcontext.getSharedPreferences("remind", MODE_PRIVATE);
        }
    }

    public NotificationService() {
        super();
        // startForeground(id, notification);
    }

    public NotificationService(Context context) {
        super();
        LogUtils.i("NotificationService", "NotificationService()");
        if (context != null) {
            // mcontext = Global.mContext;
            // mcontext = getApplicationContext();
            // spRemind = mcontext.getSharedPreferences("remind", MODE_PRIVATE);
            spRemind = context.getSharedPreferences("remind", MODE_PRIVATE);
        }
    }

    public static void bindHandle(Handler handler) {
        menuHandler = handler;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        LogUtils.i("Service", "onBind");
        ServiceState = "onBind";
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        LogUtils.i("Service", "onUnbind");
        ServiceState = "onUnbind";
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("Service", "onCreate");
        ServiceState = "onCreate";
        Notification notification = new Notification(R.drawable.logo,
                "波尔云移动办公", System.currentTimeMillis());
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        Intent notificationIntent = new Intent(this, TabMainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(this, "波尔云运行中", "关闭后将不再收到新消息提醒",
                pendingIntent);
        startForeground(1152, notification);

        // ORMDataHelper helper = new ORMDataHelper(this);
        ORMDataHelper helper = ORMDataHelper.getInstance(this);
        try {
            dao已提醒 = helper.getDao(已提醒.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        downLoadData();
    }

    /**
     * 启动默认下载
     */
    public void downLoadData() {
        // 启动线程下载User数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 下载部门员工信息
                mDataHelper.GetSys_User_Department(0, handlerRemind);
                // 下载部门信息
                mDataHelper.GetSys_Department(handlerRemind);
                // 下载客户信息
                mDataHelper.getClientList(handlerRemind);
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i("ServiceonDestroy", "onDestroy");
        ServiceState = "onDestroy";
        mHandler.removeCallbacks(mHeartBeat);
        mHandler.removeCallbacks(mHeartBeatDisscuss);
        if (myNotiManager != null) {
            myNotiManager.cancel(0);
            myNotiManager.cancel(1);
        }
    }

    @Override
    public void onStart(Intent intent, int startid) {
        super.onStart(intent, startid);
        LogUtils.i("Service", "onStart");
        ServiceState = "onStart";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i("Service", "onStartCommand");
        mHandler.postDelayed(mHeartBeat, Global.POLLING_INTERVAL * 3);
        // 初次启动
        mHandlerDisscuss.postDelayed(mHeartBeatDisscuss,
                Global.POLLING_INTERVAL * 3);
        return super.onStartCommand(intent, flags, startId);
        // return START_STICKY;
    }

    NotificationManager myNotiManager = null;
    private Handler mHandler = new Handler();
    private Runnable mHeartBeat = new Runnable() {
        public void run() {
            HeartBeatRunnable r = new HeartBeatRunnable();
            new Thread(r).start();
            // 隔多长时间检查一次
            mHandler.postDelayed(mHeartBeat, Global.POLLING_INTERVAL);
        }
    };

    private Handler mHandlerDisscuss = new Handler();
    private Runnable mHeartBeatDisscuss = new Runnable() {
        public void run() {
            // **勿删 心跳监听评论
            // HeartBeatThread_Discuss t = new HeartBeatThread_Discuss();
            // new Thread(t).start();
            // // 隔五分钟检测一次检查一次
            // mHandlerDisscuss.postDelayed(mHeartBeatDisscuss, 2 * 60 * 1000);

            HeartBeatThread_Dynamic r = new HeartBeatThread_Dynamic();
            new Thread(r).start();
            mHandlerDisscuss.postDelayed(mHeartBeatDisscuss, 15 * 1000);
        }
    };

    private static boolean mIsRunning = false;
    private static boolean mIsRunning_Discuss = false;
    private static boolean mIsRunning_Dynamic = false;

    /**
     * 心跳机制监听新更新数据
     *
     * @author bohr
     *
     */
    class HeartBeatRunnable implements Runnable {
        public void run() {
            if (mIsRunning)
                return;
            mIsRunning = true;
            try {
                notice = spRemind.getBoolean("notice", false);
                email = spRemind.getBoolean("email", false);
                log = spRemind.getBoolean("log", false);
                client = spRemind.getBoolean("client", false);
                order = spRemind.getBoolean("order", false);
                contact = spRemind.getBoolean("contact", false);
                task = spRemind.getBoolean("task", false);
                saleChance = spRemind.getBoolean("saleChance", false);
                approval = spRemind.getBoolean("approval", false);
                if (notice) { // 配置文件中需要打开通知提醒
                    notificationRemind(通知.class, "Notice/GetNoticeList",
                            "最后更新", GET_NOTICE_REMIND_SUCCEEDED);
                }
                // List<RemindEmail> listEmail = (List<RemindEmail>)mDataHelper.
                // GetRemindEMail();//之前的方法，不通用
                if (email) {
                    List<RemindEmail> listEmail = (List<RemindEmail>) mDataHelper
                            .GetRemind(RemindEmail.class,
                                    "Email/GetNewestEmail");
                    LogUtils.i("remindEmail", "==listEmail==" + listEmail);
                    if (listEmail != null && listEmail.size() > 0) {
                        Message msg = new Message();
                        msg.what = RemindHandler.GET_EMAIL_REMIND;
                        msg.obj = listEmail;
                        handlerRemind.sendMessage(msg);
                    } else {
                        // LogUtils.i("NotificationService", "没有未读邮件");
                    }
                }

                if (client) {
                    // 新客户提醒
                    notificationRemind(Client.class,
                            "Customer/GetCustomerList", "最后更新",
                            GET_CLINENT_REMIND_SUCCEEDED);
                }
                if (contact) {
                    // 新联系记录提醒
                    notificationRemind(客户联系记录.class,
                            "Customer/GetCustomerContactRecordList", "最后更新",
                            GET_CLINENT_CONTACTS_REMIND_SUCCEEDED);
                }
                if (task) {
                    // 新任务提醒
                    notificationRemind(任务.class, "Task/GetOtherList", "最后处理时间",
                            GET_TASK_REMIND_SUCCEEDED);
                }
                if (order) {
                    // 下载最新未读订单
                    List<RemindOrder> listOrder = (List<RemindOrder>) mDataHelper
                            .GetRemind(RemindOrder.class,
                                    "Cabinet/GetNewestOrder");
                    LogUtils.i("remind", "==list==" + listOrder);
                    if (listOrder != null && listOrder.size() > 0) {
                        Message msg = new Message();
                        msg.what = RemindHandler.GET_ORDER_REMIND;
                        msg.obj = listOrder;
                        handlerRemind.sendMessage(msg);
                    } else {
                        // LogUtils.i("NotificationService", "没有未读任务");
                    }
                }

                if (approval) {
                    // 下载最新未读待我审批
                    List<RemindApproval> listApprovals = (List<RemindApproval>) mDataHelper
                            .GetRemind(RemindApproval.class,
                                    "Flow/GetNewestApproval");
                    LogUtils.i("remindApproval", "==list==" + listApprovals);
                    if (listApprovals != null && listApprovals.size() > 0) {
                        Message msg = new Message();
                        msg.what = RemindHandler.GET_APPROVAL_REMIND;
                        msg.obj = listApprovals;
                        handlerRemind.sendMessage(msg);
                    } else {
                        // LogUtils.i("NotificationService", "没有未读任务");
                    }
                }
            } catch (Exception e) {
                LogUtils.i("NotificationService_Erro", "message:" + e);
            }
            mIsRunning = false;
        }

        /**
         * 顶部通知提醒
         *
         * @param classType
         *            提醒类型 泛型类型 (通知.class)
         * @param methodName
         *            方法名 获得最新提醒的接口名称
         * @param filedName
         *            最后更新,最后处理时间（mysql中用于排序的字段名）
         * @param msgWhat
         *            消息
         * @author kjx
         * @throws SQLException
         * @throws JSONException
         */
        private <T> void notificationRemind(final Class<T> classType,
                                            String methodName, String filedName, final int msgWhat)
                throws SQLException, JSONException {
            LogUtils.i("resultServer", classType.getName() + "," + methodName
                    + "=====notificationRemind");
            // final ORMDataHelper helper = new ORMDataHelper(mcontext);
            final ORMDataHelper helper = ORMDataHelper.getInstance(mcontext);
            final Dao<T, Integer> dao = helper.getDao(classType);
            final List<T> ormList = dao.queryForAll();
            String filter = "";
            if (ormList == null || ormList.size() == 0) {
                LogUtils.i("resultServer", classType.getName() + ","
                        + methodName + "本地空的哦");
            } else {
                GenericRawResults result = dao.queryRaw(dao.queryBuilder()
                        .selectRaw("max(UpdateTime)").prepareStatementString());
                String[] values = (String[]) result.getFirstResult();
                filter = filedName + "> '" + values[0] + "'";
            }

            final String url = Global.BASE_URL + methodName;
            LogUtils.i("resultServer", classType.getName() + "," + methodName
                    + "---\n过滤条件：" + filter);
            final JSONObject jo2 = new JSONObject();
            jo2.put("条件", filter);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String http_Result = httpUtils.postSubmit(url, jo2);
                        LogUtils.i("resultHttp", classType.getName() + "----"
                                + http_Result);
                        List<T> list = JsonUtils.ConvertJsonToList(http_Result,
                                classType);
                        if (list != null && list.size() > 0) {
                            LogUtils.i("resultMsg", classType.getName()
                                    + "----" + "收到新提醒");
                            Message msg = handlerNew.obtainMessage();
                            msg.obj = list;
                            msg.what = msgWhat;
                            handlerNew.sendMessage(msg);
                            for (int i = 0; i < list.size(); i++) {
                                // 使用反射获得id
                                Field idField = classType.getField("Id");
                                int id = idField.getInt(list.get(i));
                                LogUtils.i("resultMsg", "id------->" + id);
                                DeleteBuilder builder = dao.deleteBuilder();
                                builder.where().eq("Id", id);
                                int count = builder.delete();
                                LogUtils.i("resultMsg", "count------->" + count);
                                dao.create(list.get(i));
                                LogUtils.i(TAG,
                                        "###############插入一条#############");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.e("resultErr", "" + e.getMessage());
                    }
                }
            }).start();
        }
    }

    /**
     * 心跳机制监听日志评论,联系记录评论
     *
     * @author bohr
     *
     */
    class HeartBeatThread_Discuss implements Runnable {
        public void run() {
            if (mIsRunning_Discuss)
                return;
            mIsRunning_Discuss = true;
            String methodName = "Notification/GetNewestNotification";
            Notifications notifications = mDataHelper
                    .GetCommentRemind(methodName);
            List<日志评论> logDiscussList = notifications.logCommentList;
            List<评论> contactsCommentList = notifications.contactsCommentList;
            if (logDiscussList != null && logDiscussList.size() > 0) {
                LogUtils.i("Notifications", "Log size=" + logDiscussList.size());
                日志评论 it日志评论 = logDiscussList.get(0);

                SendCommentRemind(it日志评论.内容, it日志评论.发表人, it日志评论.日志编号, 1);
            }
            if (contactsCommentList != null && contactsCommentList.size() > 0) {
                LogUtils.i("Notifications",
                        "联系记录 size=" + contactsCommentList.size());
                评论 it联系记录评论 = contactsCommentList.get(0);
                SendCommentRemind(it联系记录评论.Content, it联系记录评论.userId,
                        it联系记录评论.OrderNo, 2);
            }
            mIsRunning_Discuss = false;
        }
    }

    /**
     * 心跳机制监听最新动态
     *
     * @author kjx
     * @since 2014/10/09 11:24
     */
    class HeartBeatThread_Dynamic implements Runnable {
        public void run() {
            LogUtils.i("HeartBeatThread_Dynamic", "HeartBeatThread_Dynamic");
            if (mIsRunning_Dynamic) {
                LogUtils.i("HeartBeatThread_Dynamic", "mIsRunning_Dynamic");
                return;
            }
            mIsRunning_Dynamic = true;
            Demand demand = new Demand();
            demand.表名 = "";
            demand.用户编号 = "";
            demand.方法名 = "dynamic/getDynamicList";
            demand.条件 = "";
            demand.附加条件 = "isnull(已读)";
            demand.每页数量 = 100; // 不分页
            demand.偏移量 = 0;
            List<动态> listDynamic = mDataHelper.GetDynamicList(serverCall,
                    demand);
            LogUtils.i("GetDynamicList",
                    "GetDynamicList size=" + listDynamic.size());
            if (listDynamic.size() > 0) {
                sendDynamicRemind(listDynamic);
            }
            mIsRunning_Dynamic = false;
        }
    }

    /**
     * 动态提醒
     */
    private void sendDynamicRemind(List<动态> listDynamic) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Android3.0增加了Notification.Builder类，该类可以轻松地创建Notification对象。
        Notification.Builder builder = new Notification.Builder(this);
        // 设置通知自动消失
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.icon);
        // 状态栏弹出显示文字，如果不设置则顶部通知看不到该通知的任何内容，需要下拉后才能看到通知
        builder.setTicker("波尔云提示您有" + listDynamic.size() + "条新动态...");
        // 通知的内容和标题
        builder.setContentTitle("您有" + listDynamic.size() + "条新动态...");
        builder.setContentText(listDynamic.get(0).Content);
        // 设置通知的默认铃声和震动
        builder.setDefaults(Notification.DEFAULT_SOUND);
        // TODO 点击通知的跳转意图
        Intent intent = new Intent(this, TabMainActivity.class);
        intent.putExtra("currentTab", 0);
        PendingIntent pIntent = PendingIntent.getActivity(this, 1, intent,
                PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pIntent);
        Notification notification = builder.build();
        manager.notify(0, notification);
    }

    /**
     * 通知提醒
     *
     * @param str
     */
    void SendNoticeRemind(String str) {
        myNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(Global.mContext,
                NoticeListActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		/* 创建PendingIntent作为设置递延运行的Activity */
        PendingIntent appIntent = PendingIntent.getActivity(Global.mContext, 0,
                notifyIntent, 0);
		/* 创建Notication，并设置相关参数 */
        Notification myNoti = new Notification();
		/* 设置statusbar显示的icon */
        myNoti.icon = R.drawable.menu_notification_01;
		/* 设置statusbar显示的文字信息 */
        myNoti.tickerText = "未读通知";
		/* 设置notification发生时同时发出默认声音 */
        // myNoti.defaults=Notification.DEFAULT_SOUND;
		/* 设置Notification留言条的参数 */
        myNoti.setLatestEventInfo(Global.mContext, "未读通知", "您收到" + str + "条通知",
                appIntent);
        // 设置自动清除
        myNoti.ledARGB = 0xff00ff00;
        myNoti.ledOnMS = 300;
        myNoti.ledOffMS = 1000;
        myNoti.flags |= Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_SHOW_LIGHTS;
        myNoti.defaults |= Notification.DEFAULT_SOUND;
        NoticeListActivity.isResume = true; // 刷新标志位
		/* 送出Notification */
        myNotiManager.notify(0, myNoti);
    }

    /**
     * 收到新评论提醒
     *
     * @param content
     *            评论内容
     * @param CommentUserId
     *            评论人
     * @param OrderNo
     *            被评论日志（或联系记录）编号 评论人
     * @param type
     *            通知类型：1-日志评论， 2-联系记录评论
     */
    private void SendCommentRemind(String content, int CommentUserId,
                                   String OrderNo, int type) {
        LogUtils.i("notificaitonCo", content + "--" + CommentUserId + "--"
                + OrderNo);
        if (myNotiManager == null) {
            myNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        // String CommentName = dictionaryHelper.getUserNameById(CommentUserId);
        // String CommentName = dictionaryHelper.getUserNameById(CommentUserId);
        Intent notifyIntent = new Intent();
        String title = "";
        if (type == 1) {
            notifyIntent = getLogIntent(OrderNo);
            if (notifyIntent == null) {
                // 如果改意图不存在，则只能跳转到日志列表页面
                notifyIntent = new Intent(Global.mContext,
                        WorkLogActivity.class);
            }
            // title = CommentName + "评论了日志";
            title = "收到日志评论";
            // title = "评论了日志";
        } else if (type == 2) {
            notifyIntent = getContactIntent(OrderNo);
            if (notifyIntent == null) {
                // 如果改意图不存在，则只能跳转到联系列表页面
                notifyIntent = new Intent(Global.mContext,
                        ClientConstactListActivity.class);
            }
            // title = CommentName + "评论了联系记录";
            title = "收到联系记录评论";
            // title = "评论了联系记录";
        }
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		/* 创建PendingIntent作为设置递延运行的Activity */
        PendingIntent appIntent = PendingIntent.getActivity(Global.mContext, 0,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		/* 创建Notication，并设置相关参数 */
        Notification myNoti = new Notification();
		/* 设置statusbar显示的icon */
        myNoti.icon = R.drawable.logo;
		/* 设置statusbar显示的文字信息 */
        myNoti.tickerText = "您有新评论";
		/* 设置Notification留言条的参数 */
        myNoti.setLatestEventInfo(Global.mContext, title, "内容:" + content,
                appIntent);
        // 设置自动清除
        myNoti.ledARGB = 0xff00ff00;
        myNoti.ledOnMS = 300;
        myNoti.ledOffMS = 1000;
        myNoti.flags |= Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_SHOW_LIGHTS;
        myNoti.defaults |= Notification.DEFAULT_SOUND;
        // NoticeListActivity.isResume = true; // 刷新标志位
		/* 送出Notification */
        myNotiManager.notify(0, myNoti);
    }

    /**
     * 获得日志页面的跳转意图
     *
     * @param orderNo
     *            日志编号
     * @return 如果该日志不存在则返回null
     */
    private Intent getLogIntent(String orderNo) {
        日志 item = mDataHelper.getWorkLogById(orderNo, Global.mContext);
        if (item == null) {
            return null;
        } else {
            Intent intent = new Intent(Global.mContext, WorkLogActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Log", item);
            LogUtils.i("keno2", "id:" + item.Id);
            intent.putExtras(bundle);
            return intent;
        }
    }

    /**
     * 获得联系记录页面的跳转意图
     *
     * @param orderNo
     *            联系记录编号
     * @return 如果该日志不存在则返回null
     */
    private Intent getContactIntent(String orderNo) {
        客户联系记录 item = mDataHelper.getContactById(orderNo, Global.mContext);
        if (item == null) {
            return null;
        } else {
            Intent intent = new Intent(Global.mContext,
                    ClientConstactInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ClientConstactInfoActivity.TAG, item);
            intent.putExtras(bundle);
            return intent;
        }
    }

    /**
     * 任务提醒
     *
     * @param str
     */
    void SendTaskRemind(String str) {
        myNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent notifyIntent = new Intent(Global.mContext,
                TaskListActivityNew.class);
        // notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
		/* 创建PendingIntent作为设置递延运行的Activity */
        PendingIntent appIntent = PendingIntent.getActivity(Global.mContext, 0,
                notifyIntent, 0);
		/* 创建Notication，并设置相关参数 */
        Notification myNoti = new Notification();
		/* 设置statusbar显示的icon */
        myNoti.icon = R.drawable.calendar_mini;
		/* 设置statusbar显示的文字信息 */
        myNoti.tickerText = "未读任务";
		/* 设置notification发生时同时发出默认声音 */
        // myNoti.defaults=Notification.DEFAULT_SOUND;
		/* 设置Notification留言条的参数 */
        myNoti.setLatestEventInfo(Global.mContext, "未读任务",
                "您收到 " + str + "条任务", appIntent);
        // 设置自动清除
        myNoti.ledARGB = 0xff00ff00;
        myNoti.ledOnMS = 300;
        myNoti.ledOffMS = 1000;
        myNoti.flags |= Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_SHOW_LIGHTS;
        myNoti.defaults |= Notification.DEFAULT_SOUND;

        TaskListActivityNew.isResume = true;// 刷新订单列表
		/* 送出Notification */
        myNotiManager.notify(1, myNoti);
    }

    /**
     * 订单提醒
     *
     * @param str
     */
    void SendOrderRemind(String str) {
        myNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(Global.mContext,
                OrderListActivity.class);
        // notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
		/* 创建PendingIntent作为设置递延运行的Activity */
        PendingIntent appIntent = PendingIntent.getActivity(Global.mContext, 0,
                notifyIntent, 0);
		/* 创建Notication，并设置相关参数 */
        Notification myNoti = new Notification();
		/* 设置statusbar显示的icon */
        myNoti.icon = R.drawable.menu_order;
		/* 设置statusbar显示的文字信息 */
        myNoti.tickerText = "未读订单";
		/* 设置notification发生时同时发出默认声音 */
        // myNoti.defaults=Notification.DEFAULT_SOUND;
		/* 设置Notification留言条的参数 */
        myNoti.setLatestEventInfo(Global.mContext, "未读订单", "您收到" + str + "件订单",
                appIntent);
        // 设置自动清除
        myNoti.ledARGB = 0xff00ff00;
        myNoti.ledOnMS = 300;
        myNoti.ledOffMS = 1000;
        myNoti.flags |= Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_SHOW_LIGHTS;
        myNoti.defaults |= Notification.DEFAULT_SOUND;
        OrderListActivity.isResume = true;// 刷新订单列表
		/* 送出Notification */
        myNotiManager.notify(1, myNoti);
    }

    /**
     * 待我审批 提醒
     *
     * @param str
     */
    private void SendApprovalRemind(String str) {
        myNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(Global.mContext,
                ApplyListFragmentActivity.class);
        // notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
		/* 创建PendingIntent作为设置递延运行的Activity */
        PendingIntent appIntent = PendingIntent.getActivity(Global.mContext, 0,
                notifyIntent, 0);
		/* 创建Notication，并设置相关参数 */
        Notification myNoti = new Notification();
		/* 设置statusbar显示的icon */
        myNoti.icon = R.drawable.a_icon8;
		/* 设置statusbar显示的文字信息 */
        myNoti.tickerText = "待我审批";
        myNoti.setLatestEventInfo(Global.mContext, "待我审批申请", "您收到" + str
                + "条申请", appIntent);
        // 设置自动清除
        myNoti.ledARGB = 0xff00ff00;
        myNoti.ledOnMS = 300;
        myNoti.ledOffMS = 1000;
        myNoti.flags |= Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_SHOW_LIGHTS;
        myNoti.defaults |= Notification.DEFAULT_SOUND;
		/* 送出Notification */
        myNotiManager.notify(1, myNoti);
    }

    /**
     * 顶部通知栏 kehu提醒
     *
     * @param str
     */
    private void sendClientRemind(String str) {
        myNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(Global.mContext,
                ClientListActivity.class);
        // notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
		/* 创建PendingIntent作为设置递延运行的Activity */
        PendingIntent appIntent = PendingIntent.getActivity(Global.mContext, 0,
                notifyIntent, 0);
		/* 创建Notication，并设置相关参数 */
        Notification myNoti = new Notification();
		/* 设置statusbar显示的icon */
        myNoti.icon = R.drawable.a_icon5;
		/* 设置statusbar显示的文字信息 */
        myNoti.tickerText = "新客户";
		/* 设置notification发生时同时发出默认声音 */
        // myNoti.defaults=Notification.DEFAULT_SOUND;
		/* 设置Notification留言条的参数 */
        myNoti.setLatestEventInfo(Global.mContext, "新客户", "系统为您分配了" + str
                + "位新客户", appIntent);
        // 设置自动清除
        myNoti.ledARGB = 0xff00ff00;
        myNoti.ledOnMS = 300;
        myNoti.ledOffMS = 1000;
        myNoti.flags |= Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_SHOW_LIGHTS;
        myNoti.defaults |= Notification.DEFAULT_SOUND;
        ClientListActivity.isResume = true;// 刷新订单列表
		/* 送出Notification */
        myNotiManager.notify(1, myNoti);
    }

    /**
     * 顶部通知栏 kehu提醒
     *
     * @param str
     */
    private void sendContactRemind(String str) {
        myNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(Global.mContext,
                ClientConstactListActivity.class);
        // notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
		/* 创建PendingIntent作为设置递延运行的Activity */
        PendingIntent appIntent = PendingIntent.getActivity(Global.mContext, 0,
                notifyIntent, 0);
		/* 创建Notication，并设置相关参数 */
        Notification myNoti = new Notification();
		/* 设置statusbar显示的icon */
        myNoti.icon = R.drawable.a_icon6;
		/* 设置statusbar显示的文字信息 */
        myNoti.tickerText = "新联系记录";
		/* 设置notification发生时同时发出默认声音 */
        // myNoti.defaults=Notification.DEFAULT_SOUND;
		/* 设置Notification留言条的参数 */
        myNoti.setLatestEventInfo(Global.mContext, "新联系记录", "您收到" + str
                + "条联系记录", appIntent);
        // 设置自动清除
        myNoti.ledARGB = 0xff00ff00;
        myNoti.ledOnMS = 300;
        myNoti.ledOffMS = 1000;
        myNoti.flags |= Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_SHOW_LIGHTS;
        myNoti.defaults |= Notification.DEFAULT_SOUND;
        // ClientConstactListActivity.isResume = true;// 刷新客户联系记录
		/* 送出Notification */
        myNotiManager.notify(1, myNoti);
    }

    public class RemindHandler extends Handler {
        public static final int GET_NOTICE_REMIND = 0;
        public static final int GET_EMAIL_REMIND = 1;

        public static final int UPLOAD_PHOTODATA_FAILED = 2;
        public static final int UPLOAD_PHOTODATA_SUCCEEDED = 3;

        // public static final int UPLOAD_DATA_FAILED = 4;
        // public static final int UPLOAD_DATA_SUCCEEDED = 5;

        public static final int GET_USERS_DATA_FAILED = 6;
        public static final int GET_USERS_DATA_SUCCESS = 7;
        public static final int GET_TASK_REMIND = 8;
        public static final int GET_CLIENT_DATA_FAILED = 9;
        public static final int GET_CLIENT_DATA_SUCCESS = 10;
        public static final int GET_CLIENT_DATA_ISNULL = 11;

        public static final int GET_ORDER_REMIND = 12; // 获得订单提醒
        public static final int GET_APPROVAL_REMIND = 13; // 获得待我审批提醒

        public static final int GET_DEPARTMENT_SUCCEEDED = 14; // 获得部门信息成功

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            int whatMsg = msg.what;
            switch (whatMsg) {
                case GET_NOTICE_REMIND:
                    List<RemindNotice> listNotice = (List<RemindNotice>) msg.obj;
                    if (listNotice != null) {
                        int count = judgeNoticeExistedOrNot(listNotice);
                        // count=0表示数据库里有数据,1表示数据库没有数据
                        if (count > 0) {
                            SendNoticeRemind(count + "");
                            // 存入Notice提醒的数量
                            SharedPreferences sp = mcontext.getSharedPreferences(
                                    "RemindCount", MODE_PRIVATE);
                            Editor editor = sp.edit();
                            editor.putInt("NOTICE", count);
                            editor.commit();
                        }
                    }
                    break;
                case GET_EMAIL_REMIND:
                    List<RemindEmail> listEmail = (List<RemindEmail>) msg.obj;
                    if (listEmail != null) {
                        // int count = judgeEmailExistedOrNot(listEmail);
                        // TODO 邮件功能暂停
                        int count = 0;
                        if (count > 0) {
                            // SendEmailRemind(count + "");
                            // Message msgEmail = obtainMessage();
                            // msgEmail.what =
                            // TaskListActivityNew.GET_EMAIL_DATA_SUCCESS;
                            // msgEmail.arg1 = count;
                            // menuHandler.sendMessage(msgEmail);
                        }
                    }
                    break;
                case GET_TASK_REMIND:
                    List<RemindTask> listTask = (List<RemindTask>) msg.obj;
                    if (listTask != null) {
                        int count = judgeTaskExistedOrNot(listTask);
                        if (count > 0) {
                            SendTaskRemind(count + "");
                        }
                    }
                    break;
                case GET_ORDER_REMIND:
                    List<RemindOrder> listOrder = (List<RemindOrder>) msg.obj;
                    if (listOrder != null) {
                        int count = judgeOrderExistedOrNot(listOrder);
                        if (count > 0) {
                            SendOrderRemind(count + "");
                        }
                    }
                    break;
                case GET_APPROVAL_REMIND:// 待我审批提醒
                    List<RemindApproval> listApprovals = (List<RemindApproval>) msg.obj;
                    if (listApprovals != null) {
                        int count = judgeApprovalExistedOrNot(listApprovals);
                        if (count > 0) {
                            SendApprovalRemind(count + "");
                        }
                    }
                    break;
                case UPLOAD_PHOTODATA_SUCCEEDED:
                    Toast.makeText(mcontext, "图片上传成功", Toast.LENGTH_SHORT).show();
                    // progressBar_showupload.setVisibility(View.INVISIBLE);
                    break;
                case UPLOAD_PHOTODATA_FAILED:
                    Toast.makeText(mcontext, "图片上传失败", Toast.LENGTH_SHORT).show();
                    // progressBar_showupload.setVisibility(View.INVISIBLE);
                    break;
                // case UPLOAD_DATA_FAILED:
                // Toast.makeText(mcontext, "申请失败", Toast.LENGTH_SHORT).show();
                // break;
                // case UPLOAD_DATA_SUCCEEDED:
                // Toast.makeText(mcontext, "申请成功", Toast.LENGTH_SHORT).show();
                // break;
                case GET_USERS_DATA_FAILED:
                    // Toast.makeText(mcontext, "插入用户信息失败",
                    // Toast.LENGTH_SHORT).show();
                    break;
                case GET_USERS_DATA_SUCCESS:
                    LogUtils.i("keno_json_hanler", "netData download  success");
                    List<Data> list_data = (List<Data>) msg.obj;
                    List<User> list = new ArrayList<User>();
                    // 将服务器返回的数据转化为User对象中的数据
                    for (int i = 0; i < list_data.size(); i++) {
                        Data data = list_data.get(i);
                        User user = new User();
                        user.setId(data.getId() + "");
                        user.setDptId(data.getDepartment());
                        user.setCorpId(data.getCorpId());
                        user.setUserName(data.getUserName());
                        user.setAdmin(data.isAdmin());
                        user.setAvatar(data.getAvatar());
                        user.setAvatarURI(data.getAvatarURI());
                        LogUtils.i(
                                "keno_json_hanler",
                                data.getAvatarURI() == null ? "" : data
                                        .getAvatarURI());
                        String result = data.getLastUpdateDate();
                        long time = User_SelectActivityNew.changeTimeToInt(result);
                        user.setUpdateTime(time);
                        user.setCorpId(data.getCorpId());
                        list.add(user);
                    }
                    try {
                        // ormDataHelper = OpenHelperManager.getHelper(mcontext,
                        // ORMDataHelper.class);
                        // ormDataHelper = new ORMDataHelper(mcontext);
                        ormDataHelper = ORMDataHelper.getInstance(mcontext);
                        userDao = ormDataHelper.getUserDao();
                        List<User> list_ormlite = null;
                        list_ormlite = userDao.queryForAll();
                        updateOrmlite(list, list_ormlite);
                    } catch (Exception e1) {
                        LogUtils.i(TAG, "用户名册更新失败");
                    } finally {
                        // ormDataHelper.close();
                    }
                    break;
                case GET_CLIENT_DATA_SUCCESS: // 获得客户列表成功
                    List<Client> listClient = (List<Client>) msg.obj;
                    // 将服务器返回的数据转化为User对象中的数据
                    try {
                        // ormDataHelper = OpenHelperManager.getHelper(mcontext,
                        // ORMDataHelper.class);
                        // ormDataHelper = new ORMDataHelper(mcontext);
                        ormDataHelper = ORMDataHelper.getInstance(mcontext);
                        List<Client> list_ormlite = null;
                        clientDao = ormDataHelper.getClientDao();
                        list_ormlite = clientDao.queryForAll();
                        updateClientOrmlite(listClient, list_ormlite);
                    } catch (Exception e1) {
                        LogUtils.i(TAG, "用户名册更新失败");
                    } finally {
                        // ormDataHelper.close();
                    }
                    break;
                case GET_DEPARTMENT_SUCCEEDED:
                    List<部门> listDept = (List<部门>) msg.obj;
                    try {
                        ormDataHelper = ORMDataHelper.getInstance(mcontext);
                        Dao<部门, Integer> deptDao = ormDataHelper.getDao(部门.class);
                        deptDao.deleteBuilder().delete();
                        for (int i = 0; i < listDept.size(); i++) {
                            int num = deptDao.create(listDept.get(i));
                            LogUtils.i("insertDept", i + "------" + num);
                        }
                    } catch (Exception e1) {
                        LogUtils.i(TAG, "部门更新失败");
                    }
                    break;
                case GET_CLIENT_DATA_FAILED:
                    break;
                case GET_CLIENT_DATA_ISNULL:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public static final int GET_NOTICE_REMIND_SUCCEEDED = 0;
    public static final int GET_EMAIL_REMIND_SUCCEEDED = 1;
    public static final int GET_CLINENT_REMIND_SUCCEEDED = 2;
    public static final int GET_CLINENT_CONTACTS_REMIND_SUCCEEDED = 3;
    public static final int GET_TASK_REMIND_SUCCEEDED = 8;
    public static final int GET_ORDER_REMIND_SUCCEEDED = 12; // 获得订单提醒
    public static final int GET_APPROVAL_REMIND_SUCCEEDED = 13; // 获得待我审批提醒

    /**
     * 新方式获得通知提醒
     *
     */
    private Handler handlerNew = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_NOTICE_REMIND_SUCCEEDED:
                    List<通知> list = (List<通知>) msg.obj;
                    SendNoticeRemind(list.size() + "");
                    break;
                case GET_TASK_REMIND_SUCCEEDED:
                    List<任务> tasklist = (List<任务>) msg.obj;
                    SendTaskRemind(tasklist.size() + "");
                    break;
                case GET_CLINENT_REMIND_SUCCEEDED:
                    List<Client> clientlist = (List<Client>) msg.obj;
                    sendClientRemind(clientlist.size() + "");
                    break;
                case GET_CLINENT_CONTACTS_REMIND_SUCCEEDED:
                    List<客户联系记录> contactlist = (List<客户联系记录>) msg.obj;
                    sendContactRemind(contactlist.size() + "");
                    break;
                default:
                    break;
            }
        };
    };

    /**
     * 数据库更新操作
     *
     * @param list
     * @param list_ormlite
     */
    public void updateOrmlite(List<User> list, List<User> list_ormlite) {
        // 比较从网络上获取的list中user的id 与数据库中数据的id是不是有一样的
        // 如果有 则更新操作，如果没有 则进行插入操作
        for (int i = 0; i < list.size(); i++) {
            int num = 0;
            for (int j = 0; j < list_ormlite.size(); j++) {
                if (list.get(i).getId().equals(list_ormlite.get(j).getId())
                        && list.get(i).getCorpId() == list_ormlite.get(j)
                        .getCorpId()) {
                    try {
                        // 修改数据库数据
                        userDao.update(list.get(i));

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (Global.mUser.getCorpId() != list_ormlite.get(j)
                            .getCorpId()) {
                        try {
                            userDao.delete(list_ormlite.get(j));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    num++;
                }
            }
            if (num == list_ormlite.size()) {
                try {
                    // 插入数据
                    userDao.create(list.get(i));
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 数据库更新操作
     *
     * @param list
     * @param list_ormlite
     */
    public void updateClientOrmlite(List<Client> list, List<Client> list_ormlite) {
        // 比较从网络上获取的list中user的id 与数据库中数据的id是不是有一样的
        // 如果有 则更新操作，如果没有 则进行插入操作
        for (int i = 0; i < list.size(); i++) {
            int num = 0;
            for (int j = 0; j < list_ormlite.size(); j++) {
                if (list.get(i).getId() == list_ormlite.get(j).getId()) {
                    try {
                        // 修改数据库数据
                        clientDao.update(list.get(i));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    num++;
                }
            }
            if (num == list_ormlite.size()) {
                try {
                    // 插入数据
                    clientDao.create(list.get(i));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断通知是否存在
     *
     * @param list
     * @return
     */
    public int judgeNoticeExistedOrNot(List<RemindNotice> list) {
        // TODO Auto-generated method stub
        int count = 0;
        for (RemindNotice notice : list) {
            int id = notice.Id;
            try {
                QueryBuilder<已提醒, Integer> qb = dao已提醒.queryBuilder();
                Where<已提醒, Integer> where = qb.where();
                where.eq("Id", id);
                where.and();
                where.eq("Classifi", Global.REMIND_NOTICE);
                List<已提醒> listQ = dao已提醒.query(qb.prepare());
                if (listQ != null && listQ.size() > 0) {

                } else {
                    count++;
                    已提醒 item = new 已提醒();
                    item.Id = id;
                    item.Classifi = Global.REMIND_NOTICE;
                    dao已提醒.create(item);
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * 判断邮件是否存在
     *
     * @param list
     * @return
     */
    public int judgeEmailExistedOrNot(List<RemindEmail> list) {
        // TODO Auto-generated method stub
        int count = 0;

        for (RemindEmail email : list) {
            int id = email.Id;
            try {
                QueryBuilder<已提醒, Integer> qb = dao已提醒.queryBuilder();
                Where<已提醒, Integer> where = qb.where();
                where.eq("Id", id);
                where.and();
                where.eq("Classifi", Global.REMIND_EMAIL);
                List<已提醒> listQ = dao已提醒.query(qb.prepare());
                if (listQ != null && listQ.size() > 0) {

                } else {
                    count++;
                    已提醒 item = new 已提醒();
                    item.Id = id;
                    item.Classifi = Global.REMIND_EMAIL;
                    dao已提醒.create(item);
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return count;
    }

    /**
     * 判断任务是否存在
     *
     * @param list
     * @return
     */
    public int judgeTaskExistedOrNot(List<RemindTask> list) {
        // TODO Auto-generated method stub
        int count = 0;
        for (RemindTask task : list) {
            int id = task.Id;
            try {
                QueryBuilder<已提醒, Integer> qb = dao已提醒.queryBuilder();
                Where<已提醒, Integer> where = qb.where();
                where.eq("Id", id);
                where.and();
                where.eq("Classifi", Global.REMIND_TASK);
                List<已提醒> listQ = dao已提醒.query(qb.prepare());
                if (listQ != null && listQ.size() > 0) {
                } else {
                    count++;
                    已提醒 item = new 已提醒();
                    item.Id = id;
                    item.Classifi = Global.REMIND_TASK;
                    int i = dao已提醒.create(item);
                    LogUtils.i("intRemind", "remind=" + i);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * 判断订单是否存在
     *
     * @param list
     * @return
     */
    public int judgeOrderExistedOrNot(List<RemindOrder> list) {
        // TODO Auto-generated method stub
        int count = 0;
        for (RemindOrder order : list) {
            int id = order.Id;
            try {
                QueryBuilder<已提醒, Integer> qb = dao已提醒.queryBuilder();
                Where<已提醒, Integer> where = qb.where();
                where.eq("Id", id);
                where.and();
                where.eq("Classifi", Global.REMIND_ORDER);
                List<已提醒> listQ = dao已提醒.query(qb.prepare());
                if (listQ != null && listQ.size() > 0) {

                } else {
                    count++;
                    已提醒 item = new 已提醒();
                    item.Id = id;
                    item.Classifi = Global.REMIND_ORDER;
                    dao已提醒.create(item);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * 判断待我审批是否存在
     *
     * @param list
     * @return
     */
    public int judgeApprovalExistedOrNot(List<RemindApproval> list) {
        int count = 0;
        for (RemindApproval order : list) {
            int id = order.Id;
            try {
                QueryBuilder<已提醒, Integer> qb = dao已提醒.queryBuilder();
                Where<已提醒, Integer> where = qb.where();
                where.eq("Id", id);
                where.and();
                where.eq("Classifi", Global.REMIND_APPROVAL);
                // 获得最新未读 判断手机数据库已提醒中是否存在数据，如果不存在则插入，并统计数量
                List<已提醒> listQ = dao已提醒.query(qb.prepare());
                if (listQ != null && listQ.size() > 0) {

                } else {
                    count++;
                    已提醒 item = new 已提醒();
                    item.Id = id;
                    item.Classifi = Global.REMIND_APPROVAL;
                    dao已提醒.create(item);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

}
