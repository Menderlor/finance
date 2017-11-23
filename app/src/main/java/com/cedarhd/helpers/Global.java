package com.cedarhd.helpers;

import android.content.Context;

import com.cedarhd.biz.UserBiz;
import com.cedarhd.models.Client;
import com.cedarhd.models.User;

import java.io.Serializable;
import java.util.List;

public class Global implements Serializable {

    private static final long serialVersionUID = 2432360599474211299L;
    // 通知检查间隔时间
    public static Context mContext = null;
    // public static int mNotificationTime1 = 1000 * 30;
    public static int POLLING_INTERVAL = 1000 * 30;
    public static User mUser;

    // 是否是新登录用户
    public static boolean IsNewUser = false;
    public static int mWidthPixels;
    public static int mHeightPixels;
    public static int SECTION_LIST_ADAPTER = 0;
    public static int SECTION_LIST_ADAPTER_CONTACT_HISTORY = 1;
    public static boolean DEBUG_MODE = true;
    public static final String EMAIL_READ = "1";
    public static final String EMAIL_UNREAD = "0";

    /**
     * 是否启动了导航页
     */
    public static boolean isStartMenu = false;

    // 测试服务器接口
//    public static final String IP = "www.boeryun.com";
//    public static final String BASE_URL = "http://" + IP + ":8016/";
//    public static final String BASE_URL_PROCESS = "http://" + IP + ":8016/";
//    public static final String EXTENSION = "";
//    public static final String PROCESS_URL_HEADER =
//            "http://www.boeryun.com/Form/VSheet/Form?name=";
//    public static final String BASE_FORM_URL = "http://" + IP + "/";

    // 测试服务器接口
//    public static final String IP = "testfms.cedarhd.com/"; //雪松测试地址
    public static final String IP = "testfmsapp.cedarhd.com/"; //雪松测试地址
//    public static final String IP = "testfms.cedarhd.com/api/"; //雪松测试地址
//    public static final String IP = "fmsapp.cedarhd.com/"; // 雪松正式站地址
    // public static final String IP = "183.62.246.245";
    public static final String BASE_URL = "https://" + IP;
    public static final String BASE_URL_PROCESS = "https://" + IP;
    public static final String EXTENSION = "";
    public static final String PROCESS_URL_HEADER = "https://" + IP + "Form/VSheet/Form?name=";
    public static final String BASE_FORM_URL = "https://" + IP;

    // // 正式服务器接口
//	 public static final String IP = "testfms.cedarhd.com/"; //雪松测试地址
////	  public static final String IP = "fmsmobile.cedarhd.com/"; //雪松正式站地址
//	 // public static final String IP = "183.62.246.245";
//	 public static final String BASE_URL = "https://" + IP ;
//	 public static final String BASE_URL_PROCESS = "https://" + IP;
//	 public static final String EXTENSION = "";
//	 public static final String PROCESS_URL_HEADER =
//	 "https://fmsmobile.cedarhd.com/Form/VSheet/Form?name=";
//	 public static final String BASE_FORM_URL                                              = "https://" + IP;

    // id是流程表的编号 新建则为0
    // http://www.boeryun.com/流程表单/VSheet/Form?name=订货单&id=1&uname=张少磊&coid=127&pwd=bohrsoft
    public static final int REMIND_NOTICE = 0;
    public static final int REMIND_EMAIL = 1;
    public static final int REMIND_TASK = 2;
    public static final int REMIND_ORDER = 3;
    public static final int REMIND_APPROVAL = 6;

    public static List<Client> clientList;

    public static String getPassport() {
        if (mUser == null) {
            mUser = UserBiz.getLocalSerializableUser();
        }
        return mUser.Passport;
    }
}
