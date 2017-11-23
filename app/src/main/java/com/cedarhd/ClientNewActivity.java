package com.cedarhd;

import android.content.Context;
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
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.字段描述;
import com.cedarhd.models.客户;
import com.cedarhd.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 新建客户(动态字段生成)
 * 
 * @author KJX
 * 
 */
public class ClientNewActivity extends BaseActivity {
	private final String TAG = "ClientNewActivity";
	private ZLServiceHelper zlServiceHelper;
	private Context context;
	private List<字段描述> mList;
	private LinearLayout llArea;
	GenerateChildViewHelper<客户> generateChildView;
	private ImageView ivSave;
	private final int SUCCESS_GET_DATA = 3;
	private final int FAILURE_GET_DATA = 4;
	private final int SUCCESS_POST_CLIENT = 5;
	private final int FAILURE_POST_CLIENT = 6;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS_GET_DATA:
				ProgressDialogHelper.dismiss();
				generateChildView = new GenerateChildViewHelper<客户>(mList,
						context);
				generateChildView.setmRootLayoutId(R.id.root_client_new);
				generateChildView.addChildViews(llArea);
				break;
			case FAILURE_GET_DATA:
				ProgressDialogHelper.dismiss();
				break;
			case SUCCESS_POST_CLIENT:
				Toast.makeText(context, "保存客户成功", Toast.LENGTH_SHORT).show();
				ProgressDialogHelper.dismiss();
				finish();
				break;
			case FAILURE_POST_CLIENT:
				Toast.makeText(context, "保存客户失败，请稍后再试", Toast.LENGTH_SHORT)
						.show();
				ProgressDialogHelper.dismiss();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client_new);
		initView();
		getIntentData();
	}

	/** 获取跳转传递过来的信息 */
	private void getIntentData() {
		if (getIntent() != null) {
			int isflag = getIntent().getIntExtra("ISFLAG", 908);
			客户 clent;
			if (isflag == 909) {
				clent = (客户) getIntent().getSerializableExtra("client");
				Toast.makeText(ClientNewActivity.this, clent.toString(),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private void initView() {
		context = ClientNewActivity.this;
		zlServiceHelper = new ZLServiceHelper();
		llArea = (LinearLayout) findViewById(R.id.ll_client_new);
		ivSave = (ImageView) findViewById(R.id.iv_save_client_new);
		ProgressDialogHelper.show(context, true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mList = zlServiceHelper.getFieldList(context, "客户");
					handler.sendEmptyMessage(SUCCESS_GET_DATA);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(FAILURE_GET_DATA);
				}
			}
		}).start();
	}

	public void back(View view) {
		finish();
	}

	public void save(View view) {
		List<EditText> mEtList = generateChildView.getAllEtList();
		List<TextView> mTextList = generateChildView.getAllTextList();
		LogUtils.i("save", mEtList.size() + "");
		try {
			final JSONObject jo = getClientJsonObj(mEtList, mTextList);
			ProgressDialogHelper.show(context);
			new Thread(new Runnable() {
				@Override
				public void run() {
					boolean result = zlServiceHelper.SaveClient(jo);
					if (result) {
						handler.sendEmptyMessage(SUCCESS_POST_CLIENT);
					} else {
						handler.sendEmptyMessage(FAILURE_POST_CLIENT);
					}
				}
			}).start();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得客户实体的JSONObject类型的对象
	 * 
	 * @param mEtList
	 * @param mTextList
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws JSONException
	 * @throws NumberFormatException
	 */
	private JSONObject getClientJsonObj(List<EditText> mEtList,
			List<TextView> mTextList) throws IllegalAccessException,
			IllegalArgumentException, JSONException, NumberFormatException {
		// 客户2 client = new 客户2();
		// Class c = client.getClass();
		JSONObject jo = new JSONObject();
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
			case 7:// 单选字典
				if (tv.getText() != null
						&& !TextUtils.isEmpty(tv.getText().toString())) {
					int value = Integer.parseInt(tv.getText().toString());
					// f.set(client, value);
					LogUtils.i("Json", fieldName + "---------" + value);
					jo.put(fieldName, value);
				}
				break;
			case 8: // 多选字典类型
				if (tv.getText() != null
						&& !TextUtils.isEmpty(tv.getText().toString())) {
					String value = tv.getText().toString();
					// f.set(client, value);
					LogUtils.i("Json", fieldName + "-------" + value);
					jo.put(fieldName, value);
				} else if (field.Required) {
					Toast.makeText(context, field.字段显示名 + "为必填项",
							Toast.LENGTH_SHORT).show();
					return null;
				}
				break;
			// case 5:
			// case 4:// 时间类型
			// break;
			// case 3: // bool
			// // etValue.setInputType(EditorIn);
			// break;
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
