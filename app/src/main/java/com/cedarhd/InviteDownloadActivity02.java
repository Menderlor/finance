package com.cedarhd;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.Global;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/***
 * 邀请员工下载
 * 
 * 
 */
public class InviteDownloadActivity02 extends BaseActivity {
	private String md5password;
	private Button invite;
	private EditText employee_name;
	private EditText employee_phoneNumber;
	private EditText department_name;
	private HttpUtils mUtils = new HttpUtils();
	private String sms_body = "从即日起，开始用波尔云来进行办公管理。请到此网址下载波尔云手机管理软件: http://www.boeryun.com/app/ ，";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_download_02);
		// 通过bundle 获取联系人的信（名字和电话号码）
		Bundle bundle = this.getIntent().getExtras();
		final String contactsPhonenumber = bundle
				.getString("contactsPhonenumber");
		final String contactsName = bundle.getString("contactsName");
		// 初始化组件
		inie();
		employee_name.setText(contactsName);
		employee_phoneNumber.setText(contactsPhonenumber);
		// 用随机数获取6位密码
		final int passWord = (int) (Math.random() * 1000000);
		// md5加密
		InviteDownloadActivity02 iv = new InviteDownloadActivity02();
		try {
			md5password = iv.getMD5(passWord + "");
			LogUtils.i("md5Password", md5password);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 给将要邀请的人发短信
		invite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 启用线程访问网络，获取联系人的部门、密码
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						// 邀请员工下载
						if (department_name.getText().toString().equals("")) {

							// inviteEmployee(name,
							// md5Password,department,mobile);
							LogUtils.i("password", md5password);
							inviteEmployee(contactsName, md5password,
									Global.mUser.Department,
									contactsPhonenumber);
						}
						// 邀请部门领导下载
						else {

							// inviteDepartmentLead(int
							// superDepartmentId,departmentName,leadName,md5Password,mobile);
							inviteDepartmentLead(Global.mUser.Department,
									department_name.getText().toString(),
									contactsName, md5password,
									contactsPhonenumber);
						}
					}

				}).start();
				// 短信容
				String body = sms_body + "登录时输入的单位：" + Global.mUser.CorpName
						+ ",用户输入:" + contactsName + ",密码输入" + passWord;
				// 发短信
				Uri uri = Uri.parse("smsto:" + contactsPhonenumber);
				Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
				intent.putExtra("sms_body", body);
				startActivity(intent);
			}
		});

	}

	// 通过httpget给部门负责人注册波尔云 账号 上一级部门编号（superDepartmentId） 部门名字（）密码（）
	public String inviteDepartmentLead(int superDepartmentId,
			String departmentName, String leadName, String md5Password,
			String mobile) {
		String methodName = "account/InviteDepartmentLead/" + superDepartmentId
				+ "/" + departmentName + "/" + leadName + "/" + md5Password
				+ "/" + mobile;
		String url = Global.BASE_URL + Global.EXTENSION + methodName;
		String data = "";
		String strInfo = "";

		// LogUtils.i("getContactsInfomation", url);
		data = mUtils.httpGet(url);

		LogUtils.i("inviteDepartmentLead", data);
		try {
			JSONObject jsonObject = new JSONObject(data);
			strInfo = jsonObject.get("Data").toString();
			LogUtils.i("inviteDepartmentLead", strInfo);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	// 通过httpget给员工注册波尔云帐号
	public String inviteEmployee(String name, String md5Password,
			int department, String mobile) {
		String methodName = "account/InviteEmployee/" + name + "/"
				+ md5Password + "/" + department + "/" + mobile;
		String url = Global.BASE_URL + Global.EXTENSION + methodName;
		String data = "";
		String strInfo = "";
		data = mUtils.httpGet(url);
		try {
			LogUtils.i(" inviteEmployee", data);
			JSONObject jsonObject = new JSONObject(data);
			strInfo = jsonObject.get("Data").toString();
			LogUtils.i(" inviteEmployee", strInfo);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	// md5加密
	public String getMD5(String password) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(password.getBytes());
		String psw = new BigInteger(1, md5.digest()).toString(16);
		return psw;
	}

	// 初始化视图
	private void inie() {
		employee_name = (EditText) findViewById(R.id.employee_name);
		employee_phoneNumber = (EditText) findViewById(R.id.employee_phoneNumber);
		department_name = (EditText) findViewById(R.id.department_name);
		invite = (Button) findViewById(R.id.invite);

	}

}
