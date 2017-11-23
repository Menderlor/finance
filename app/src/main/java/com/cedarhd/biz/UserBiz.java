package com.cedarhd.biz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.cedarhd.User_SelectActivityNew_zmy;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.helpers.Global;
import com.cedarhd.models.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * 处理员工相关业务逻辑
 *
 * @author K 2015/09/27 16:17
 */
public class UserBiz {
    /**
     * 选择单个员工
     */
    public static final int SELECT_SINAL_USER_REQUEST_CODE = 301;

    /**
     * 选择多个员工
     */
    public static final int SELECT_MULTI_USER_REQUEST_CODE = 302;

    /**
     * 单选员工
     */
    public static void selectSinalUser(Context context) {
        Intent intent = new Intent(context, User_SelectActivityNew_zmy.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(User_SelectActivityNew_zmy.SELECT_EMPLOYEE, true); // 单选
        intent.putExtras(bundle);
        ((Activity) context).startActivityForResult(intent,
                SELECT_SINAL_USER_REQUEST_CODE);
    }

    /**
     * 选择多名员工
     */
    public static void selectMultiUser(Context context, String userSelectId) {
        Intent intent = new Intent(context, User_SelectActivityNew_zmy.class);
        Bundle bundle = new Bundle();
        bundle.putString("UserSelectId", userSelectId);
        intent.putExtras(bundle);
        ((Activity) context).startActivityForResult(intent,
                SELECT_MULTI_USER_REQUEST_CODE);
    }

    /***
     * 选择员工成功后返回 编号和姓名
     *
     * @param requestCode 请求码
     * @param resultCode  请求结果码
     * @param data        返回intent
     * @return user 选中用户信息,编号和姓名分别存在id和name中
     */
    public static User onActivityUserSelected(int requestCode, int resultCode,
                                              Intent data) {
        User user = new User();
        if (resultCode == Activity.RESULT_OK
                && requestCode == SELECT_SINAL_USER_REQUEST_CODE) {
            Bundle bundle = data.getExtras();
            String refUserSelectId = bundle.getString("UserSelectId");
            refUserSelectId = refUserSelectId.replace("'", "").replace(";", "");
            String refUserSelectName = bundle.getString("UserSelectName");
            user.setId(refUserSelectId);
            user.setUserName(refUserSelectName);
        }
        return user;
    }

    /***
     * 选择多个员工成功后返回 编号和姓名
     *
     * @param requestCode 请求码
     * @param resultCode  请求结果码
     * @param data        返回intent
     * @return user 选中用户信息
     */
    public static User onActivityMultiUserSelected(int requestCode,
                                                   int resultCode, Intent data) {
        User user = new User();
        if (resultCode == Activity.RESULT_OK
                && requestCode == SELECT_MULTI_USER_REQUEST_CODE) {
            Bundle bundle = data.getExtras();
            String refUserSelectId = bundle.getString("UserSelectId");
            refUserSelectId = refUserSelectId.replace("'", "").replace(";", "");
            String refUserSelectName = bundle.getString("UserSelectName");
            user.setUserIds(refUserSelectId);
            user.setUserNames(refUserSelectName);
        }
        return user;
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

    /**
     * 获取全局员工对象，持有登录用户的信息
     */
    public static User getGlobalUser() {
        if (Global.mUser == null) {
            Global.mUser = getLocalSerializableUser();
        }
        return Global.mUser == null ? new User() : Global.mUser;
    }

    /**
     * 获取全局员工对象，持有登录用户的信息
     */
    public static String getGlobalUserId() {
        return TextUtils.isEmpty(getGlobalUser().Id) ? "0" : getGlobalUser().Id;
    }

    /**
     * 获取全局员工对象，持有登录用户的信息
     */
    public static int getGlobalUserIntegerId() {
        try {
            return Integer.parseInt(getGlobalUserId());
        } catch (Exception e) {
        }
        return 0;
    }
}
