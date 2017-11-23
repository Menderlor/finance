package com.cedarhd;

/**
 * 联系人详情页面
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.GenerateChildViewHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.ReturnModel;
import com.cedarhd.models.kh联系人;
import com.cedarhd.models.字段描述;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CommunicationInfoActivity extends BaseActivity {
	private List<字段描述> mList;
	private GenerateChildViewHelper<kh联系人> generateChildView;
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	public static final String TAG = "CommunicationAddActivity";
	private kh联系人 联系人s;
	ProgressDialog dialog;
	/**
	 * 拨号
	 */
	private LinearLayout dail;
	/**
	 * 动态生成控件的区域
	 */
	private LinearLayout holder;
	/**
	 * 标题栏的返回和保存按钮
	 */
	private ImageView back, save;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communication_info);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			联系人s = (kh联系人) bundle.getSerializable(TAG);
		}
		if (联系人s != null) {// 如果客户实体不为空，动态加载页面
			ProgressDialogHelper.show(CommunicationInfoActivity.this,
					"正在为您加载客户信息..");
			getFieldInfoList();
		}
		initView();
	}

	private void initView() {
		holder = (LinearLayout) findViewById(R.id.communication_newInfo_zmy);
		back = (ImageView) findViewById(R.id.communication_zmy_back);
		dail = (LinearLayout) findViewById(R.id.ll_call_cummunication_info);
		dail.setOnClickListener(l);
		back.setOnClickListener(l);
		save = (ImageView) findViewById(R.id.communication_zmy_save);
		save.setOnClickListener(l);

	}

	public static final int UPDATE_SUCCESS = 12031;
	public static final int UPDATE_ERROR = 12032;
	private View.OnClickListener l = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.communication_zmy_back:
				finish();
				break;
			case R.id.communication_zmy_save:
				List<EditText> mEtList = generateChildView.getAllEtList();
				List<TextView> mTextList = generateChildView.getAllTextList();
				LogUtils.i("save", mEtList.size() + "");
				try {
					final JSONObject jo = getContactsJsonObj(mEtList, mTextList);
					dialog = ProgressDialog.show(
							CommunicationInfoActivity.this, "提示", "正在提交...");
					new Thread(new Runnable() {
						@Override
						public void run() {
							Boolean isok = SaveContacts(jo);
							if (isok) {
								handler.sendEmptyMessage(UPDATE_SUCCESS);
							} else {
								handler.sendEmptyMessage(UPDATE_ERROR);
							}
						}
					}).start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case R.id.ll_call_cummunication_info:
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:" + 联系人s.手机));
				startActivity(intent);
				break;
			}
		}
	};

	public String result_save;

	/** 修改联系人 */
	public boolean SaveContacts(JSONObject jo2) {
		boolean result = false;
		HttpUtils mHttpUtils = new HttpUtils();
		String methodName = "Communication/SaveContact/";
		String url = Global.BASE_URL + Global.EXTENSION + methodName;
		LogUtils.i(TAG, url);
		String strResp = null;
		try {
			strResp = mHttpUtils.postSubmit(url, jo2);
			result_save = strResp;
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
	 * 获取动态字段的实体类
	 * 
	 * @param mEtList
	 * @param mTextList
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws JSONException
	 * @throws NumberFormatException
	 */
	private JSONObject getContactsJsonObj(List<EditText> mEtList,
			List<TextView> mTextList) throws IllegalAccessException,
			IllegalArgumentException, JSONException, NumberFormatException {
		JSONObject jo = new JSONObject();
		jo.put("编号", 联系人s.编号);
		for (int i = 0; i < mEtList.size(); i++) {
			EditText et = mEtList.get(i);
			TextView tv = mTextList.get(i);
			if (TextUtils.isEmpty(et.getText().toString())) {
				// 如果文本为空，则不做处理
				continue;
			}
			LogUtils.i("save", et.getText().toString());
			字段描述 field = (字段描述) et.getTag();
			int inputType = field.输入类型;
			String fieldName = field.字段名;
			switch (inputType) {
			case 6:
			case 7:
			case 8: // 字典类型
				if (tv.getText() != null
						&& !TextUtils.isEmpty(tv.getText().toString())) {
					int value = Integer.parseInt(tv.getText().toString());
					// f.set(client, value);
					LogUtils.i("Json", fieldName + "---------" + value);
					jo.put(fieldName, value);
				}
				break;
			case 2: // 数字类型
				int value = Integer.parseInt(et.getText().toString());
				jo.put(fieldName, value);
				break;
			default:
				String result = et.getText().toString();
				jo.put(fieldName, result);
				break;
			}
		}
		return jo;
	}

	/**
	 * 获取字段
	 */
	private void getFieldInfoList() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mList = zlServiceHelper.getFieldList(
							CommunicationInfoActivity.this, "kh联系人");
					handler.sendEmptyMessage(SUCCESS_GET_DATA);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static final int SUCCESS_GET_DATA = 123;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS_GET_DATA:
				ProgressDialogHelper.dismiss();
				if (联系人s != null) {
					// 根据客户字段描述表生成控件后，将客户属性一一对应显示到页面上
					generateChildView = new GenerateChildViewHelper<kh联系人>(
							mList, 联系人s, CommunicationInfoActivity.this);
				} else {
					generateChildView = new GenerateChildViewHelper<kh联系人>(
							mList, CommunicationInfoActivity.this);
				}
				generateChildView.addChildViews(holder);
				break;
			case UPDATE_ERROR:
				dialog.dismiss();
				Toast.makeText(CommunicationInfoActivity.this,
						"修改联系人失败" + result_save, Toast.LENGTH_SHORT).show();
				break;
			case UPDATE_SUCCESS:
				dialog.dismiss();
				Toast.makeText(CommunicationInfoActivity.this, "修改联系人成功",
						Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};
}
