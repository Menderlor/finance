package com.cedarhd.control;




import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

public class MyBaseActivity extends Activity {
	/** 获取activity对象 */
	public static Activity currentACtivity;
	/**获取屏幕宽高 */
	public static int screenW, screenH;
	/** 吐司对象 */
	private Toast toast;
	/** 跳转船只*/
	private Intent intent;
	/** 提示框*/
	private AlertDialog dialog;
	/**二次退出 **/
	public boolean isClick = true;

	/*******************吐司********************************/
	/**
	 * 短时间吐司
	 * 
	 * @param msg
	 *            显示的信息
	 */
	protected void showShortToast(String msg) {
		if (toast == null) {//判断是不是第一次 是就创建 不是就直接使用
			toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		}
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setText(msg);
		toast.show();
	}

	/**
	 * 短吐司提示
	 * 
	 * @param id
	 *           字符串对应的编号
	 */
	protected void showShortToast(int id) {
		String msg = getString(id);// 获取当前id对应的字符
		showShortToast(msg);//设置显示
	}

	/**
	 * 长吐司提示
	 * 
	 * @param msg
	 *           字符串
	 */
	protected void showLongToast(String msg) {
		if (toast == null) {
			toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		}
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setText(msg);
		toast.show();
	}

	/**
	 * 长吐司提示
	 * 
	 * @param id
	 *            字符串对应的id
	 */
	protected void showLongToast(int id) {
		String msg = getString(id);
		showLongToast(msg);
	}

	/******************** SKip跳转 *************************************/
	/**
	 * 跳转传值
	 * 
	 * @param class1
	 *           下一个界面
	 * @param bundle
	 *           传递的值
	 */
	protected void skip(Class<?> class1, Bundle bundle) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(this, class1);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	/**
	 * 不传值的跳转
	 * 
	 * @param class1
	 *           下一个界面
	 */
	protected void skip(Class<?> class1) {
		skip(class1, null);
	}

	/************************** Activity的生命周期*********************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentACtivity = this;
		screenW = getWindowManager().getDefaultDisplay().getWidth();// 获取宽
		screenH = getWindowManager().getDefaultDisplay().getHeight();// 获取高
	}

	/******
	 *二次退出
	 * 
	 * @return
	 *****/
	protected void secendExit(int keyCode, KeyEvent event) {
			if (isClick) {
				// 提示toast
				Toast.makeText(this, "在按一次退出", Toast.LENGTH_LONG).show();
				isClick = false;
				// 计时
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(4000);
							isClick = true;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
			} else {
				finish();
			}
		
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}
}
