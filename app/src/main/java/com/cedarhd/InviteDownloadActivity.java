package com.cedarhd;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.NewDepartMent;
import com.cedarhd.models.ReturnModel;
import com.cedarhd.receiver.MySendReciver;
import com.cedarhd.utils.ByteUtil;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 邀请员工下载
 * 
 * 
 * @since 2014/08/28 15:21
 */
public class InviteDownloadActivity extends BaseActivity {
	private ListView mContactslist;
	private String uri_raw_contacts = "content://com.android.contacts/raw_contacts";
	private String uri_contacts_phones = "content://com.android.contacts/data/phones";
	private ContentResolver resolver = null;
	private List<Map<String, String>> mtotalList = null;
	private ContactsAdatper adapter;
	private EditText search;
	private MyProgressBar pBar;
	private List<Map<String, String>> mNewList = new ArrayList<Map<String, String>>();
	private List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
	private Button All_button;
	private ImageButton back;
	private HttpUtils mUtils = new HttpUtils();
	private Map<String, String> map = new HashMap<String, String>();
	private String md5password;
	String contactsPhonenumber;
	String contactsName;
	private String sms_body = "即日起,用波尔云进行办公管理.请到此下载波尔云手机管理软件:http://www.bohrsoft.com/down.html ";
	int passWord;
	// 接收返回部门的字符串
	String backstring;
	List<String> listEmp;
	// dialog相关
	EditText text_dia;
	Button btn_dia1, btn_dia2;
	AlertDialog alertDialog, dialog2;
	String name;
	EditText editText_dialog2, edit_2;
	TextView tview;
	private MySendReciver mySendReciver;
	NewDepartMent departMent_info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_download);
		departMent_info = (NewDepartMent) getIntent().getExtras().get("depart");
		mContactslist = (ListView) findViewById(R.id.contactsList);
		search = (EditText) findViewById(R.id.contacts_search);
		pBar = (MyProgressBar) findViewById(R.id.pbar_invite);

		IntentFilter filter = new IntentFilter(TELEPHONY_SERVICE);
		mySendReciver = new MySendReciver();
		registerReceiver(mySendReciver, filter);
		// 请求部门的线程
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = Global.BASE_URL + Global.EXTENSION
						+ "Department/GetCommonDepartmentName";
				backstring = mUtils.httpGet(url);
				LogUtils.i("out", backstring);
				handler.sendEmptyMessage(END_EMP);

			}
		}).start();
		// 给搜素设置监听
		search.addTextChangedListener(watcher);
		resolver = getContentResolver();
		All_button = (Button) findViewById(R.id.All_button);
		back = (ImageButton) findViewById(R.id.download_back);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		// 用随机数获取6位密码
		passWord = (int) (Math.random() * 1000000);
		// md5加密
		// InviteDownloadActivity02 iv = new InviteDownloadActivity02();
		// try {
		// // md5password = iv.getMD5(passWord + "");
		//
		// } catch (NoSuchAlgorithmException e) {
		// e.printStackTrace();
		// }

		md5password = ByteUtil.md5One(passWord + "");
		LogUtils.i("md5Password", md5password);
		pBar.setVisibility(View.VISIBLE);
		new QueryTask().execute();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mySendReciver != null) {
			unregisterReceiver(mySendReciver);
		}
	}

	private List<String> getJSON1(String data, String TAG) {
		List<String> list = new ArrayList<String>();
		String str = null;
		try {
			// 获取json对象
			JSONObject jsonObject = new JSONObject(data);
			// 通过键名来获取对应的类
			int status = jsonObject.getInt("Status");
			if (status == 1) {
				str = jsonObject.getString(TAG);
				JSONArray array = jsonObject.getJSONArray(TAG);
				for (int i = 0; i < array.length(); i++) {
					list.add(array.getString(i));
				}

			} else {
				Toast.makeText(InviteDownloadActivity.this,
						"数据异常，可能是网络原因，请确定络正常连接后在试！！！", Toast.LENGTH_LONG)
						.show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	// 创建部门线程
	private void createEmp(final EditText edit_2, List<String> list) {
		LogUtils.i("out", list.get(1));
		for (int i = 0; i < list.size(); i++) {
			if (!edit_2.getText().toString().trim().equals(list.get(i))) {
				// 开启线程将
				new Thread(new Runnable() {
					@Override
					public void run() {
						String url = null;
						try {
							url = Global.BASE_URL
									+ Global.EXTENSION
									+ URLEncoder.encode(edit_2.getText()
											.toString(), "UTF-8");
							String res = mUtils.httpGet(url);
						} catch (UnsupportedEncodingException e) {
							// TODO
							// Auto-generated
							// catch block
							e.printStackTrace();
						}

					}
				}).start();
			}
		}
	}

	// TextWatcher接口
	private TextWatcher watcher = new TextWatcher() {
		// 文字变化前时
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			mNewList.clear();
			if (!search.getText().equals("")) {
				String input_info = search.getText().toString();
				mNewList = getNewList(input_info);
				LogUtils.i("onTextChanged", input_info);
				adapter = new ContactsAdatper(InviteDownloadActivity.this,
						mNewList);
				mContactslist.setAdapter(adapter);
			}
		}

		// 文字变化前
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
		}

		// 文字变化后
		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub

		}
	};

	// 当editetext变化时调用的方法，来判断所输入是否包含在所属数据中
	private List<Map<String, String>> getNewList(String input) {
		// 遍历mtotalList
		for (int i = 0; i < mtotalList.size(); i++) {
			Map<String, String> mp1 = mtotalList.get(i);
			LogUtils.i("getNewList", input + i);
			String name = mp1.get("display_name");
			String number = mp1.get("phones");
			// 将电话或者姓名包括所输入的内容整到一个新集合中
			if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(number)
					&& (name.indexOf(input) != -1 || number.contains(input))) {
				mList.add(mp1);
				LogUtils.i("getNewList", mList.get(0).get("display_name")
						+ mList.get(0).get("phones"));
			}
			// LogUtils.i("getNewList",
			// "============"+mList.get(0).get("display_name")+mList.get(0).get("phones"));
		}
		return mList;

	}

	// 查询联系人信息
	private List<Map<String, String>> selectContactsInfo() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Cursor cursor = resolver.query(Uri.parse(uri_raw_contacts),
				new String[] { "_id", "display_name" }, null, null, null);
		while (cursor.moveToNext()) {
			Map<String, String> map = new HashMap<String, String>();
			String raw_contact_id = cursor.getString(0);
			String display_name = cursor.getString(1);
			map.put("raw_contact_id", raw_contact_id);
			map.put("display_name", display_name);
			// 获取联系人的所有电话号码信息
			Cursor cursorPhones = resolver.query(
					Uri.parse(uri_contacts_phones), new String[] { "data1" },
					"raw_contact_id=?", new String[] { raw_contact_id }, null);
			StringBuilder sb = new StringBuilder();
			while (cursorPhones.moveToNext()) {
				sb.append(cursorPhones.getString(0));
				sb.append(",");
			}
			map.put("phones", sb.toString());

			list.add(map);
			cursorPhones.close();

		}
		// cursor.close();

		return list;
	}

	// private List<E>
	// 自定义适配器，适配联系人的姓名和电话
	class ContactsAdatper extends BaseAdapter {
		private LayoutInflater contactsInflater;
		private List<Map<String, String>> totalList;

		public ContactsAdatper(Context contactscontext,
				List<Map<String, String>> totalList) {
			super();

			contactsInflater = LayoutInflater.from(contactscontext);
			this.totalList = totalList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return totalList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return totalList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = contactsInflater.inflate(
						R.layout.item_invite_download, null);
				holder.contactsName = (TextView) convertView
						.findViewById(R.id.contacts_names);
				holder.contactsPhonenumber = (TextView) convertView
						.findViewById(R.id.contacts_phones);
				holder.sendMessage = (ImageView) convertView
						.findViewById(R.id.iv_invites);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 适配联系人的姓名
			holder.contactsName.setText(totalList.get(position).get(
					"display_name"));
			// 适配联系人的电话号码
			holder.contactsPhonenumber.setText(totalList.get(position).get(
					"phones"));
			holder.sendMessage.getResources().getDrawable(
					R.drawable.ic_launcher);
			// 给ImageView 设置监听事件,跳转并传值
			holder.sendMessage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					contactsPhonenumber = totalList.get(position).get("phones");
					contactsName = totalList.get(position).get("display_name");
					Builder builder = new Builder(InviteDownloadActivity.this);
					View view = LayoutInflater
							.from(InviteDownloadActivity.this).inflate(
									R.layout.show_dialog1, null);
					alertDialog = builder.create();
					alertDialog.setView(view, 0, 0, 0, 0);
					alertDialog.show();
					text_dia = (EditText) view.findViewById(R.id.show_name_dia);
					btn_dia1 = (Button) view.findViewById(R.id.button_yes);
					btn_dia2 = (Button) view.findViewById(R.id.button_no);
					text_dia.setText(totalList.get(position)
							.get("display_name"));
					showInviteDialog();

					btn_dia2.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							alertDialog.dismiss();
						}
					});
				}
			});
			return convertView;
		}

		class ViewHolder {
			TextView contactsName;
			TextView contactsPhonenumber;
			ImageView sendMessage;
		}
	}

	public static final int END_EMP = 2;
	private final int SUCCEDD_INVATED = 1;
	private final int FAILURE_INVATED = 3;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCEDD_INVATED:
				ProgressDialogHelper.dismiss();
				Toast.makeText(InviteDownloadActivity.this,
						"添加用户" + contactsName + "到数据库", Toast.LENGTH_SHORT)
						.show();
				String body = sms_body + "@登录时输入的单位：" + Global.mUser.CorpName
						+ ",用户输入:" + contactsName + ",密码输入" + passWord;
				Intent intent = new Intent(InviteDownloadActivity.this,
						SmsActity.class);
				Bundle bundle = new Bundle();
				if (contactsPhonenumber.endsWith(",")) {
					bundle.putString("tels", contactsPhonenumber.substring(0,
							contactsPhonenumber.length() - 1));
				} else {
					bundle.putString("tels", contactsPhonenumber);
				}
				bundle.putString("body", body);
				intent.putExtras(bundle);
				startActivity(intent);
				// dialog2.dismiss();
				break;
			case END_EMP:
				listEmp = getJSON1(backstring, "Data");
				break;
			case FAILURE_INVATED:
				String result = (String) msg.obj;
				ProgressDialogHelper.dismiss();
				Toast.makeText(InviteDownloadActivity.this, "邀请失败：" + result,
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};

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
		LogUtils.i("METhodNAME", methodName);
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

	/***
	 * 通过httpget给员工注册波尔云帐号
	 * 
	 * @param name
	 *            姓名
	 * @param md5Password
	 *            加密后的登录密码
	 * @param department
	 *            部门
	 * @param mobile
	 *            手机号
	 * @return
	 */
	public ReturnModel<String> inviteEmployee(String name, String md5Password,
			int department, String mobile) {
		ReturnModel<String> returnModel = new ReturnModel<String>();
		String methodName = "account/InviteEmployee/" + name + "/"
				+ md5Password + "/" + department + "/" + mobile;
		String url = Global.BASE_URL + Global.EXTENSION + methodName;
		String data = "";
		try {
			data = mUtils.httpGet(url);
			returnModel = JsonUtils.pareseResult(data);
			LogUtils.i("inviteEmployee", returnModel.Status
					+ returnModel.Message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnModel;

	}

	// md5加密
	public String getMD5(String password) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(password.getBytes());
		String psw = new BigInteger(1, md5.digest()).toString(16);
		return psw;
	}

	/** 显示邀请下载对话框 */
	private void showInviteDialog() {
		btn_dia1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				ProgressDialogHelper.show(InviteDownloadActivity.this,
						"正在邀请...");
				// 启用线程访问网络，获取联系人的部门、密码
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						// 邀请员工下载
						// inviteEmployee(name,
						// md5Password,department,mobile);
						// 获取文本框中输入的名字
						contactsName = text_dia.getText().toString();
						LogUtils.i("password", md5password);
						ReturnModel<String> returnModel = inviteEmployee(
								contactsName, md5password, departMent_info.编号,
								contactsPhonenumber.replace(" ", ""));
						if (returnModel.Status == 1) {
							handler.sendEmptyMessage(SUCCEDD_INVATED);
						} else {
							Message msg = handler.obtainMessage();
							// 重名注册
							msg.obj = returnModel.Message;
							msg.what = FAILURE_INVATED;
							handler.sendMessage(msg);
						}
					}
				}).start();
			}
		});
	}

	private class QueryTask extends
			AsyncTask<Void, String, List<Map<String, String>>> {
		protected List<Map<String, String>> doInBackground(Void... params) {
			mtotalList = selectContactsInfo();
			if (mtotalList == null) {
				mtotalList = new ArrayList<Map<String, String>>();
			}
			return mtotalList;
		}

		@Override
		protected void onPostExecute(List<Map<String, String>> mtotalList) {
			super.onPostExecute(mtotalList);
			pBar.setVisibility(View.GONE);
			adapter = new ContactsAdatper(InviteDownloadActivity.this,
					mtotalList);
			mContactslist.setAdapter(adapter);
		}

	}
}
