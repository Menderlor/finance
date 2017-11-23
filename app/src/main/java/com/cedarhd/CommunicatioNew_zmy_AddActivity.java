package com.cedarhd;

import android.app.ProgressDialog;
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

/**
 * 通讯录-新建动态联系人
 * 
 * @author zmy
 * @since 2015-03-02
 */
public class CommunicatioNew_zmy_AddActivity extends BaseActivity {
	private ImageView back;
	private ImageView save;
	private LinearLayout layout;
	private List<字段描述> mList;
	private GenerateChildViewHelper<kh联系人> generateChildView;
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	public static final int SAVE_SUCCESS = 519;
	public static final int SAVE_ERROR = 520;
	/**
	 * 获取字段成功
	 */
	public static final int GET_FILED_INFO = 521;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communication_add_zmy);
		findViews();
		getFieldInfoList();
	}

	/**
	 * 获取字段
	 */
	private void getFieldInfoList() {
		ProgressDialogHelper.show(CommunicatioNew_zmy_AddActivity.this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mList = zlServiceHelper.getFieldList(
							CommunicatioNew_zmy_AddActivity.this, "kh联系人");
					handler.sendEmptyMessage(GET_FILED_INFO);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void findViews() {
		back = (ImageView) findViewById(R.id.communication_zmy_add_back);
		back.setOnClickListener(l);
		save = (ImageView) findViewById(R.id.communication_zmy_add_save);
		save.setOnClickListener(l);
		layout = (LinearLayout) findViewById(R.id.communication_newadd_zmy);
	}

	ProgressDialog dialog;
	private View.OnClickListener l = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.communication_zmy_add_back:
				finish();
				break;
			case R.id.communication_zmy_add_save:
				List<EditText> mEtList = generateChildView.getAllEtList();
				List<TextView> mTextList = generateChildView.getAllTextList();
				LogUtils.i("save", mEtList.size() + "");
				try {
					final JSONObject jo = getContactsJsonObj(mEtList, mTextList);
					dialog = ProgressDialog.show(
							CommunicatioNew_zmy_AddActivity.this, "提示",
							"正在提交...");
					new Thread(new Runnable() {
						@Override
						public void run() {
							Boolean isok = SaveContacts(jo);
							if (isok) {
								handler.sendEmptyMessage(SAVE_SUCCESS);
							} else {
								handler.sendEmptyMessage(SAVE_ERROR);
							}
						}
					}).start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
	};

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SAVE_SUCCESS:
				dialog.dismiss();
				Toast.makeText(CommunicatioNew_zmy_AddActivity.this, "新建成功",
						Toast.LENGTH_SHORT).show();
				finish();
				break;
			case SAVE_ERROR:
				dialog.dismiss();
				Toast.makeText(CommunicatioNew_zmy_AddActivity.this,
						"新建失败" + result_save, Toast.LENGTH_SHORT).show();
				break;
			case GET_FILED_INFO:
				ProgressDialogHelper.dismiss();
				generateChildView = new GenerateChildViewHelper<kh联系人>(mList,
						CommunicatioNew_zmy_AddActivity.this);
				generateChildView.addChildViews(layout);
				break;
			}
		};
	};
	public static final String TAG = "CommunicationNew_zmy_add";
	private String result_save;

	/** 新建联系人 */
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
		// jo.put("编号", 联系人s.编号);
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

}
