package com.cedarhd.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.cedarhd.ExistApplication;

/***
 * 整个项目的基类activity,每个新建Activity继承该类（请勿轻易修改）
 * 
 * @author kjx
 * 
 */
public class BaseActivity extends FragmentActivity {

	/***/
	public String TAG = getClass().getSimpleName();

	public final String INFO_ERRO_SERVER = "网络不给力，请稍后再试";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 隐藏软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		ExistApplication.getInstance().addActivity(this);
	}

	/**
	 * 弹出短Toast提示信息
	 * 
	 */
	protected void showShortToast(String info) {
		Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
	}

	/******************** SKip跳转 *************************************/
	/**
	 * 跳转传值
	 * 
	 * @param class1
	 *            下一个界面
	 * @param bundle
	 *            传递的值
	 */
	protected void skip(Class<?> class1, Bundle bundle) {
		Intent intent = new Intent(this, class1);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	/**
	 * 不传值的跳转
	 * 
	 * @param class1
	 *            下一个界面
	 */
	protected void skip(Class<?> class1) {
		skip(class1, null);
	}

}
