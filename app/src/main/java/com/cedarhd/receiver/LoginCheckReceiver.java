package com.cedarhd.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cedarhd.ExistApplication;
import com.cedarhd.LoginActivity;
import com.cedarhd.utils.LogUtils;

/**
 * @Description: 登录密码校验,当服务器密码发生变化，提示用户重新登录
 * @author kjx
 * @date 2014/07/17 10:51
 * 
 */
public class LoginCheckReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.i("LoginCheckReceiver", "LoginCheckReceiver收到广播"
				+ context.getClass().getName());
		Toast.makeText(context, "登录失败，用户名或密码错误！", Toast.LENGTH_LONG).show();
		// TabMainActivity.tabMainActivity.finish();

		ExistApplication.getInstance().exit(false);
		Intent i = new Intent(context, LoginActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
}
