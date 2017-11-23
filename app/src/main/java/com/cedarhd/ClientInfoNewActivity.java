package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.GenerateChildViewHelper;
import com.cedarhd.helpers.BoeryunTypeMapper;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.动态;
import com.cedarhd.models.字段描述;
import com.cedarhd.models.客户;
import com.cedarhd.utils.LogUtils;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 客户详情（动态生成页面）
 * 
 * @author k
 * 
 */
public class ClientInfoNewActivity extends BaseActivity {
	public static final String TAG = "ClientInfoNewActivity";
	private boolean isNew; // 是否新建
	private 客户 mClient;
	private Context context;
	private List<字段描述> mList;
	private LinearLayout llArea;
	private GenerateChildViewHelper<客户> generateChildView;
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();

	private final int SUCCESS_GET_DATA = 3;
	private final int FAILURE_GET_DATA = 4;
	private final int SUCCESS_POST_CLIENT = 5;
	private final int FAILURE_POST_CLIENT = 6;
	public final int GET_DYNAMIC_SUCCESS = 7; // 点击动态，获取客户成功
	public final int GET_DYNAMIC_FAILED = 8; // 点击动态，获取客户失败
	public static final int SUCCESS_UPDATE = 101;
	public static final int FAILURE_UPDATE = 102;
	public static final int SUCCESS_NEW = 103;
	public static final int FAILURE_NEW = 104;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case SUCCESS_UPDATE:
				Toast.makeText(context, "修改客户信息成功", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case FAILURE_UPDATE:
				Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
				break;
			case SUCCESS_NEW:
				Toast.makeText(context, "新建客户成功", Toast.LENGTH_SHORT).show();
				ClientListActivity.isResume = true;
				finish();
				break;
			case SUCCESS_GET_DATA:
				ProgressDialogHelper.dismiss();
				if (mClient != null) {
					// 根据客户字段描述表生成控件后，将客户属性一一对应显示到页面上
					generateChildView = new GenerateChildViewHelper<客户>(mList,
							mClient, context);
				} else {
					generateChildView = new GenerateChildViewHelper<客户>(mList,
							context);
				}
				generateChildView.setmRootLayoutId(R.id.root_client_info);
				generateChildView.addChildViews(llArea);
				break;
			case SUCCESS_POST_CLIENT:
				Toast.makeText(context, "保存客户成功", Toast.LENGTH_SHORT).show();
				ClientListActivity.isResume = true;
				ProgressDialogHelper.dismiss();
				finish();
				break;
			case FAILURE_POST_CLIENT:
				Toast.makeText(context, "保存客户失败，请稍后再试", Toast.LENGTH_SHORT)
						.show();
				ProgressDialogHelper.dismiss();
				break;
			case GET_DYNAMIC_SUCCESS: // 点击动态，获取客户成功
				// ProgressDialogHelper.dismiss();
				动态 dynamic = (动态) msg.obj;
				mClient = BoeryunTypeMapper.MapperTo客户(dynamic.Client);
				if (mClient != null) {
					init();
					getFieldInfoList();
				}
				break;
			case GET_DYNAMIC_FAILED: // 获取客户失败
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "加载客户失败，请稍后重试", Toast.LENGTH_LONG)
						.show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_client_info);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		findviews(); // 根据字段描述表动态生成页面
		if (getIntent() != null) {
			isNew = getIntent().getBooleanExtra(ClientInfoNewActivity.TAG,
					false);
			if (!isNew) {
				// 查看客户详情
				Bundle bundle = getIntent().getExtras();
				if (bundle != null) {
					Object obj = bundle.getSerializable(TAG);
					if (obj instanceof 客户) {
						// 客户实体是由其他页面Intent传递过来的
						mClient = (客户) obj;
					}
				}

				if (mClient != null) {
					// 如果客户实体不为空，动态加载页面
					init();
					ProgressDialogHelper.show(context, "正在加载..");
					getFieldInfoList();
				}
			} else {
				// 新建客户
				findViewById(R.id.ll_bottom_list_client_info).setVisibility(
						View.GONE);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 监听信鸽 Notification点击打开的通知
		XGPushClickedResult clickedResult = XGPushManager
				.onActivityStarted(this);
		if (clickedResult != null) {
			LogUtils.i("clickedResult", clickedResult.toString());
			String customContent = clickedResult.getCustomContent();
			LogUtils.i("CustomContent", customContent);
			try {
				JSONObject jo = new JSONObject(customContent);
				// 获取动态类型 和 数据编号
				final String dynamicType = jo.getString("dynamicType");
				final String dataId = jo.getString("dataId");
				LogUtils.i("dynami", dynamicType + "--" + dataId);
				ProgressDialogHelper.show(context);
				new Thread(new Runnable() {
					@Override
					public void run() {
						动态 dynamic = zlServiceHelper.LoadDynamicById(
								dynamicType, dataId);
						if (dynamic != null && dynamic.Client != null) {
							// 设置客户为已读
							zlServiceHelper.ReadClient(dynamic.Client, context);
							// 发送到handler中进行处理
							Message msg = handler.obtainMessage();
							msg.obj = dynamic;
							msg.what = GET_DYNAMIC_SUCCESS;
							handler.sendMessage(msg);
						} else {
							handler.sendEmptyMessage(GET_DYNAMIC_FAILED);
						}
					}
				}).start();
			} catch (JSONException e) {
				e.printStackTrace();
				LogUtils.e("erro", e.toString());
				handler.sendEmptyMessage(GET_DYNAMIC_FAILED);
			}
		}
	}

	private void findviews() {
		context = ClientInfoNewActivity.this;
		ImageView ivCancel = (ImageView) findViewById(R.id.iv_cancel_client_info);
		ImageView ivDone = (ImageView) findViewById(R.id.iv_save_client_info);
		llArea = (LinearLayout) findViewById(R.id.ll_area_client_info);
		ivCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ivDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<EditText> mEtList = generateChildView.getAllEtList();
				List<TextView> mTextList = generateChildView.getAllTextList();
				LogUtils.i("save", mEtList.size() + "");
				try {
					final JSONObject jo = getClientJsonObj(mEtList, mTextList);
					if (jo != null) {
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
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 获取客户的字段描述表生成页面
	 */
	private void getFieldInfoList() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mList = zlServiceHelper.getFieldList(context, "客户");
					handler.sendEmptyMessage(SUCCESS_GET_DATA);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void init() {
		LinearLayout llContactList = (LinearLayout) findViewById(R.id.iv_contact_list_client_info);
		LinearLayout llNewContact = (LinearLayout) findViewById(R.id.iv_contact_add_client_info);
		LinearLayout llWorkPlanList = (LinearLayout) findViewById(R.id.iv_workplan_list_client_info);
		LinearLayout llWorkPlanNew = (LinearLayout) findViewById(R.id.iv_workplan_new_client_info);
		LinearLayout llSaleChanceList = (LinearLayout) findViewById(R.id.iv_salechance_list_client_info);
		LinearLayout llSaleChanceNew = (LinearLayout) findViewById(R.id.iv_salechance_new_client_info);

		// 查看本客户的联系记录
		llContactList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClientInfoNewActivity.this,
						ClientConstactListActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putSerializable(ClientConstactListActivity.TAG, item);
				// intent.putExtras(bundle);
				Bundle bundle = new Bundle();
				bundle.putInt("ClientInfoActivity_clientId", mClient.编号);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		// 新建联系记录
		llNewContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClientInfoNewActivity.this,
						ClientConstactNewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("ClientInfoActivity_clientId", mClient.编号);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		llWorkPlanList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClientInfoNewActivity.this,
						TaskListActivityNew.class);
				Bundle bundle = new Bundle();
				bundle.putInt("ClientInfoActivity_clientId", mClient.编号);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		// 投诉建议列表
		llWorkPlanList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClientInfoNewActivity.this,
						SuggestListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("ClientInfoActivity_clientId", mClient.编号);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		// 新建投诉建议
		llWorkPlanNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClientInfoNewActivity.this,
						SuggestNewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("ClientInfoActivity_clientId", mClient.编号);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		/**
		 * 查看销售机会列表
		 */
		llSaleChanceList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClientInfoNewActivity.this,
						SaleChanceListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("ClientInfoActivity_clientId", mClient.编号);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		/**
		 * 新建销售机会
		 */
		llSaleChanceNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClientInfoNewActivity.this,
						SaleChanceInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("ClientInfoActivity_clientId", mClient.编号);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	/**
	 * 获得客户实体的JSONObject类型的对象
	 * 
	 * 如果返回空则不做处理
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
		JSONObject jo = new JSONObject();
		jo.put("编号", mClient.编号);
		for (int i = 0; i < mEtList.size(); i++) {
			EditText et = mEtList.get(i);
			TextView tv = mTextList.get(i);
			// if (TextUtils.isEmpty(et.getText().toString())) {
			// // 如果文本为空，则不做处理
			// continue;
			// }
			LogUtils.i("save", et.getText().toString());
			字段描述 field = (字段描述) et.getTag();
			int inputType = field.输入类型;
			String fieldName = field.字段名;
			switch (inputType) {
			case 6:
			case 7:
				if (tv.getText() != null
						&& !TextUtils.isEmpty(tv.getText().toString())) {
					int value = Integer.parseInt(tv.getText().toString());
					// f.set(client, value);
					LogUtils.i("Json", fieldName + "-------" + value);
					jo.put(fieldName, value);
				} else if (field.Required) {
					Toast.makeText(context, field.字段显示名 + "为必填项",
							Toast.LENGTH_SHORT).show();
					return null;
				}
				break;
			case 8: // 字典类型
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
			case 2: // 数字类型
				int value = 0;
				try {
					value = Integer.parseInt(et.getText().toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				jo.put(fieldName, value);

				if (field.Required
						&& TextUtils.isEmpty(et.getText().toString())) {
					Toast.makeText(context, field.字段显示名 + "为必填项",
							Toast.LENGTH_SHORT).show();
					return null;
				}
				break;
			default:
				String result = et.getText().toString();
				jo.put(fieldName, result);

				if (field.Required && TextUtils.isEmpty(result)) {
					Toast.makeText(context, field.字段显示名 + "为必填项",
							Toast.LENGTH_SHORT).show();
					return null;
				}
				break;
			}

		}
		return jo;
	}
}
