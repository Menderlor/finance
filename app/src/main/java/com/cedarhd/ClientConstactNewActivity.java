package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.DiscussListHelper;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.DateAndTimePicker.ISelected;
import com.cedarhd.helpers.DictIosPickerBottomDialog;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.DictionaryQueryDialogHelper;
import com.cedarhd.helpers.DictionaryQueryDialogHelper.OnSelectedListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Client;
import com.cedarhd.models.Dict;
import com.cedarhd.models.changhui.CH客户联系记录;
import com.cedarhd.models.字典;
import com.cedarhd.models.评论;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MessageUtil;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 新建联系记录/详情
 * 
 * @author kjx
 * 
 */
public class ClientConstactNewActivity extends BaseActivity {
	public static final String TAG = "ClientConstactNewActivity";
	public static final String CLIENTTAG = "ClientTag";
	public static final String SALE_CHANCE_TAG = "SALE_CHANCE_TAG";
	public static final int RESULT_CODE_SUCCESS = 0;
	public static final int RESULT_CODE_FAILED = 1;
	public static final int SHOW_ADD_CONTACT = 2;
	private static final int SHOW_CLINET_NAME = 4; // 客户名称
	public static final int SELECT_CLIENT_CODE = 5; // 选择客户名称
	public static final int SELECT_SALE_CHANCE_CODE = 16; // 选择销售机会
	private boolean mIsNewClient = false; // 标志位是否新建客户
	private int clientId = 0;

	private String mUserSelectId = "";
	private String mUserSelectName = "";
	private List<String> photoPathList; // 要上传照片路径列表

	private Context mContext;
	private ZLServiceHelper zlServiceHelper;
	private DictionaryHelper dictionaryHelper;
	private DictionaryQueryDialogHelper dictionaryQueryDialogHelper;
	private HandlerNewContact handler;
	private AddImageHelper addImageHelper;
	private DiscussListHelper discussListHelper;

	List<评论> listDiscuss = new ArrayList<评论>();
	private CH客户联系记录 mContact;

	private TextView tvTitle;// 标题文字
	private TextView tvClientName;
	private EditText etLocation;
	private TextView tvContactTime;
	private TextView tvNextContactTime;
	private TextView tvContactType;
	private TextView tvOpinionType; // 意向程度
	private TextView tvWorkType; // 工作程度
	private TextView tvIsComplete; // 工作程度
	private EditText etContent;
	private EditText etQuestion; // 问题
	private TextView tvSaler; // 客户经理
	private TextView tvDept; // 客户经理部门
	private TextView tvJob; // 客户经理岗位
	private TextView tvCreateTime; // 创建时间

	private ImageView imageViewDone;
	private DateAndTimePicker mDateAndTimePicker;
	private HorizontalScrollViewAddImage llAddImage;
	private RelativeLayout rlDiscuss; // 评论按钮功能区
	private Button btnDiscuss; // 评论功能按钮
	private RelativeLayout rlPublishDiscuss; // 发表评论区域
	private Button btnPublishDiscuss;// 发表评论按钮
	private EditText etDiscussContent;// 评论内容输入区
	private LinearLayout rlDiscussContent; // 评论内容显示区
	private ListView lvDiscuss; // 评论列表
	private Button btnSpeek2; // 说话按钮
	private DictIosPickerBottomDialog mDictIosPickerBottomDialog;
	public static final int EDIT_CONTENT_CODE = 7;

	public class HandlerNewContact extends Handler {
		public static final int UPDATE_Contact_SUCCESS = 2;
		public static final int UPDATE_Contact_FAILED = 3;
		private final int GET_DISCUSS_SUCCESS = 5; // 获得评论列表成功
		private final int GET_DISCUSS_FAILED = 6; // 获得评论列表失败

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == UPDATE_Contact_SUCCESS) {
				ProgressDialogHelper.dismiss();
				MessageUtil.ToastMessage(mContext, "保存成功！");
				setResult(RESULT_OK);
				finish();
			}
			if (msg.what == UPDATE_Contact_FAILED) {
				ProgressDialogHelper.dismiss();
				MessageUtil.ToastMessage(mContext, "保存失败！");
			}
			if (msg.what == GET_DISCUSS_SUCCESS) {// 获得评论列表成功
				etDiscussContent.setText("");
				listDiscuss = (List<评论>) msg.obj;
				// 显示评论内容
				rlDiscussContent.setVisibility(View.VISIBLE);
				if (listDiscuss.size() == 0) {
					rlDiscussContent.setVisibility(View.GONE);
				}
				discussListHelper.setmList(listDiscuss);
			}
			if (msg.what == GET_DISCUSS_FAILED) {// 获得评论列表失败
				rlDiscussContent.setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_constact_new_activity);
		initData();
		findviews();
		initIntentData();
		setOnEvent();
	}

	private void initData() {
		mContext = ClientConstactNewActivity.this;
		handler = new HandlerNewContact();
		zlServiceHelper = new ZLServiceHelper();
		mDateAndTimePicker = new DateAndTimePicker(mContext);
		dictionaryHelper = new DictionaryHelper(mContext);
		dictionaryQueryDialogHelper = DictionaryQueryDialogHelper
				.getInstance(mContext);
		mDictIosPickerBottomDialog = new DictIosPickerBottomDialog(mContext);
		// 设置业务员默认为自己
		mUserSelectId = UserBiz.getGlobalUserId();
	}

	private void initIntentData() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			clientId = bundle.getInt("ClientInfoActivity_clientId", -1);
			if (clientId != -1) {
				// 新建，显示默认客户
				// addImageHelper = new AddImageHelper(this,
				// ClientConstactNewActivity.this, llAddImage, null, true);
			} else {
				// 联系详情
				mContact = (CH客户联系记录) bundle.get(TAG);
				tvTitle.setText("联系记录详情");
			}

			// 新建联系
			Client itemClient = (Client) bundle.get(CLIENTTAG);
			if (itemClient != null) {
				initViews(itemClient);
				clientId = itemClient.getId();
			}

		}

		addImageHelper = new AddImageHelper(this, mContext, llAddImage, null,
				true);
		if (mContact == null) {
			mIsNewClient = true; // 新建客户
			mContact = new CH客户联系记录();
			mContact.业务员 = Integer.parseInt(UserBiz.getGlobalUserId());
			mContact.时间 = ViewHelper.getDateString();
			mContact.制单时间 = ViewHelper.getDateString();
		}
		initViews(mContact);
		loadDeptAndJob();
	}

	private void findviews() {
		tvTitle = (TextView) findViewById(R.id.textViewTitle_constactNew1);
		tvClientName = (TextView) findViewById(R.id.tv_client_newconstact);
		etLocation = (EditText) findViewById(R.id.et_location_newconstact);
		tvContactTime = (TextView) findViewById(R.id.tv_ContactTime_newconstact);
		tvNextContactTime = (TextView) findViewById(R.id.tv_nextContactTime_newconstact);
		tvContactType = (TextView) findViewById(R.id.tv_contact_type_newcontact);
		tvOpinionType = (TextView) findViewById(R.id.tv_onpion_newcontact); // 意向程度
		tvWorkType = (TextView) findViewById(R.id.tv_work_type_newcontact); // 工作程度
		tvIsComplete = (TextView) findViewById(R.id.tv_isComplete_newcontact); // 工作程度
		etContent = (EditText) findViewById(R.id.et_content_newconstact);
		etQuestion = (EditText) findViewById(R.id.et_question_newconstact); // 问题
		tvSaler = (TextView) findViewById(R.id.tv_saler_newconstact);
		tvDept = (TextView) findViewById(R.id.tv_dept_newconstact);// 客户经理部门
		tvJob = (TextView) findViewById(R.id.tv_job_newconstact); // 客户经理岗位
		tvCreateTime = (TextView) findViewById(R.id.tv_createtime_newconstact); // 创建时间

		imageViewDone = (ImageView) findViewById(R.id.ivDone_contact1);
		llAddImage = (HorizontalScrollViewAddImage) findViewById(R.id.addImg_constact_new1);
		btnDiscuss = (Button) findViewById(R.id.btn_discuss_constact_new1);
		rlPublishDiscuss = (RelativeLayout) findViewById(R.id.rl_publich_discuss_constact_info1);
		btnPublishDiscuss = (Button) findViewById(R.id.btn_publich_discuss_constact_info1);
		etDiscussContent = (EditText) findViewById(R.id.et_discuss_content_constact_info);
		rlDiscuss = (RelativeLayout) findViewById(R.id.rl_discuss_constact_new1);
		rlDiscussContent = (LinearLayout) findViewById(R.id.rl_discuss_content_constact_new1);
		lvDiscuss = (ListView) findViewById(R.id.lv_discuss_constact_new1);
		tvSaler.setText(dictionaryHelper.getUserNameById(mUserSelectId));
		btnSpeek2 = (Button) findViewById(R.id.btn_speek2_constact1); // 语音 说话
		btnSpeek2.setVisibility(View.GONE);
	}

	/***
	 * 加载部门和岗位信息
	 */
	private void loadDeptAndJob() {
		String url = Global.BASE_URL + "account/LoadDeptAndJob/"
				+ UserBiz.getGlobalUserId();
		StringRequest.getAsyn(url, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
			}

			@Override
			public void onResponse(String response) {
				// showShortToast(response);
				try {
					String dataStr = JsonUtils.getStringValue(response,
							JsonUtils.KEY_DATA);
					dataStr = StrUtils.removeRex(
							StrUtils.removeRex(dataStr, "["), "]");
					HashMap<String, Dict> dictMap = convertDictsJson2Map(dataStr);
					if (dictMap != null) {
						Dict deptDict = dictMap.get("所在部门");
						if (deptDict != null) {
							tvDept.setText(StrUtils.pareseNull(deptDict.名称));
							mContact.所在部门 = deptDict.编号;
						}

						Dict jobDict = dictMap.get("所在岗位");
						if (jobDict != null) {
							tvJob.setText(StrUtils.pareseNull(jobDict.名称));
							mContact.所在部门 = jobDict.编号;
						}
					}
					LogUtils.i(TAG, "" + dictMap.size());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Request request, Exception ex) {

			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.i("kjxi", "requestCode:" + requestCode + ",resultCode"
				+ resultCode);
		if (resultCode == RESULT_OK && requestCode == 0) {
			// 取出字符串
			Bundle bundle = data.getExtras();
			mUserSelectId = bundle.getString("UserSelectId");
			mUserSelectName = bundle.getString("UserSelectName");
			tvSaler.setText(mUserSelectName);
		} else if (requestCode == ClientBiz.SELECT_CLIENT_CODE) {
			// 取出客户名称字符串
			if (data != null) {
				Bundle bundle = data.getExtras();
				String clientName = bundle.getString(ClientBiz.ClientName);
				clientId = bundle.getInt(ClientBiz.ClientId);
				if (clientId != 0) {
					mContact.客户 = clientId;
				}

				if (!TextUtils.isEmpty(clientName)) {
					tvClientName.setText(clientName);
				}
				LogUtils.i("kjxi", "clientId:" + clientId);
			}
			/*
			 * if (clientId != 0) { mContact.客户 = clientId;
			 * tvClientName.setText(dictionaryHelper
			 * .getClientNameById(clientId)); }
			 */
		} else if (resultCode == RESULT_OK) {
			// 拍照上传图片控件选择或拍照后显示
			if (requestCode == addImageHelper.CAMERA_TAKE_HELPER
					|| requestCode == addImageHelper.PICKED_PHOTO_WITH_DATA) {
				addImageHelper.refresh(requestCode, data);
			}
		}
	}

	// 显示联系详情
	private void initViews(final CH客户联系记录 item) {
		btnDiscuss.setVisibility(View.VISIBLE);
		if (discussListHelper == null) {
			discussListHelper = new DiscussListHelper(mContext, listDiscuss,
					lvDiscuss, rlDiscussContent);
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// zlServiceHelper.getDiscuss(item.编号, handler);
				} catch (Exception e) {
					Toast.makeText(mContext, "获得评论列表异常", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}).start();

		String attach = item.附件;
		if (TextUtils.isEmpty(attach)) {
			llAddImage.setVisibility(View.GONE);
		} else {
			addImageHelper = new AddImageHelper(this,
					ClientConstactNewActivity.this, llAddImage, item.附件, false);
		}
		tvClientName.setText(StrUtils.pareseNull(item.客户名称));
		etLocation.setText(StrUtils.pareseNull(item.地址));
		tvContactTime.setText(StrUtils.pareseNull(item.时间));
		tvNextContactTime.setText(StrUtils.pareseNull(item.下次联系时间));
		tvContactType.setText(StrUtils.pareseNull(item.联系形式名称));
		tvOpinionType.setText(StrUtils.pareseNull(item.意向程度名称));
		tvWorkType.setText(StrUtils.pareseNull(item.工作类型名称));
		tvIsComplete.setText((item.是否完成) ? "是" : "否");
		etContent.setText(StrUtils.pareseNull(item.内容));
		etQuestion.setText(StrUtils.pareseNull(item.问题困难)); // 问题
		tvCreateTime.setText(StrUtils.pareseNull(item.制单时间));

		if (mContact.编号 == 0) {
			tvSaler.setText(StrUtils.pareseNull(UserBiz
					.getLocalSerializableUser().UserName));
		} else {
			tvSaler.setText(StrUtils.pareseNull(item.业务员姓名));
		}
	}

	private void initViews(Client item) {
		addImageHelper = new AddImageHelper(this,
				ClientConstactNewActivity.this, llAddImage, null, true);
		String customerName = TextUtils.isEmpty(item.getCustomerName()) ? dictionaryHelper
				.getClientNameById(item.getId()) : item.getCustomerName();
		String contactName = item.getContacts();// 联系人
		tvClientName.setText(customerName);
		tvClientName.setEnabled(false);
		mUserSelectId = item.getSalesman() + "";
	}

	/**
	 * 绑定监听事件
	 */
	public void setOnEvent() {
		ImageView imageViewCancel = (ImageView) findViewById(R.id.ivCance_contact1);
		imageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		imageViewDone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!checkout()) {
					return;
				}

				photoPathList = addImageHelper.getPhotoList();
				if (photoPathList.size() > 0) {
					for (String path : photoPathList) {
						LogUtils.e("attachPath", path);
					}
				}

				ProgressDialogHelper.show(mContext);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							zlServiceHelper.updateCutomerContactRecord(
									mContact, handler, photoPathList, null);
						} catch (Exception e) {
							LogUtils.e(TAG, "" + e.getMessage());
						}
					}
				}).start();
			}
		});

		// 联系时间
		tvContactTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDateAndTimePicker.showDateWheel(tvContactTime);
				mDateAndTimePicker.setOnSelectedListener(new ISelected() {
					@Override
					public void onSelected(String date) {
						mContact.时间 = date;
					}
				});
			}
		});

		tvNextContactTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDateAndTimePicker.showDateWheel(tvNextContactTime);
				mDateAndTimePicker.setOnSelectedListener(new ISelected() {
					@Override
					public void onSelected(String date) {
						if (ViewHelper.formatStrToDate(date).getTime() < new Date()
								.getTime()) {
							showShortToast("下次联系时间必须大于当前时间");
							mContact.下次联系时间 = "";
							tvNextContactTime.setText("");
						} else {
							mContact.下次联系时间 = date;
						}
					}
				});
			}
		});

		// 选择客户名称
		tvClientName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mIsNewClient) {
					selectClientName();
				}
			}
		});

		tvContactType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dictionaryQueryDialogHelper.show("联系形式");
				dictionaryQueryDialogHelper
						.setOnSelectedListener(new OnSelectedListener() {
							@Override
							public void onSelected(字典 dict) {
								mContact.联系形式 = dict.Id;
								tvContactType.setText(StrUtils
										.pareseNull(dict.Name));
							}
						});
			}
		});

		tvOpinionType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dictionaryQueryDialogHelper.show("销售机会_意向程度");
				dictionaryQueryDialogHelper
						.setOnSelectedListener(new OnSelectedListener() {
							@Override
							public void onSelected(字典 dict) {
								mContact.意向程度 = dict.Id;
								tvOpinionType.setText(StrUtils
										.pareseNull(dict.Name));
							}
						});
			}
		});

		tvWorkType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dictionaryQueryDialogHelper.show("客户联系记录_工作类型");
				dictionaryQueryDialogHelper
						.setOnSelectedListener(new OnSelectedListener() {
							@Override
							public void onSelected(字典 dict) {
								mContact.工作类型 = dict.Id;
								tvWorkType.setText(StrUtils
										.pareseNull(dict.Name));
							}
						});
			}
		});

		tvIsComplete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String[] dicts = new String[] { "是", "否" };
				mDictIosPickerBottomDialog.show(dicts);
				mDictIosPickerBottomDialog
						.setOnSelectedListener(new DictIosPickerBottomDialog.OnSelectedListener() {
							@Override
							public void onSelected(int index) {
								mContact.是否完成 = (index == 1) ? true : false;
								tvIsComplete.setText(dicts[index]);
							}
						});
			}
		});

		// 评论，弹出发表评论区域
		btnDiscuss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rlPublishDiscuss.setVisibility(View.VISIBLE);
				// 获得焦点
				etDiscussContent.requestFocus();
				// 弹出软键盘
				InputMethodManager imm = (InputMethodManager) btnDiscuss
						.getContext().getSystemService(INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				rlDiscuss.setVisibility(View.GONE);
			}
		});

		// 发表评论
		btnPublishDiscuss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rlDiscuss.setVisibility(View.VISIBLE);
				final String content = etDiscussContent.getText().toString();
				if (!TextUtils.isEmpty(content)) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								zlServiceHelper.publishDiscuss(mContact.编号,
										content, handler);
							} catch (Exception e) {
								Toast.makeText(mContext, "查看联系记录异常",
										Toast.LENGTH_SHORT).show();
							}
						}
					}).start();
				} else {
					Toast.makeText(mContext, "评论内容不能为空", Toast.LENGTH_LONG)
							.show();
				}
				rlPublishDiscuss.setVisibility(View.GONE);
			}
		});
	}

	/**
	 * 选择用户
	 */
	private void selectSaler() {
		Intent intent = new Intent(ClientConstactNewActivity.this,
				User_SelectActivityNew_zmy.class);
		Bundle bundle = new Bundle();
		bundle.putBoolean(User_SelectActivityNew_zmy.SELECT_EMPLOYEE, true); // 单选
		intent.putExtras(bundle);
		startActivityForResult(intent, 0);
	}

	/**
	 * 提交校验
	 * 
	 * @return
	 */
	private boolean checkout() {
		if (mContact.客户 == 0) {
			showShortToast("客户不能为空");
			return false;
		}
		if (TextUtils.isEmpty(tvContactTime.getText().toString())) {
			showShortToast("联系时间不能为空");
			return false;
		}

		if (mContact.联系形式 == 0) {
			showShortToast("联系方式不能为空");
			return false;
		}

		if (mContact.工作类型 == 0) {
			showShortToast("工作类型不能为空");
			return false;
		}

		if (TextUtils.isEmpty(etContent.getText().toString())) {
			showShortToast("联系记录不能为空");
			return false;
		}

		mContact.内容 = etContent.getText().toString();
		mContact.地址 = etLocation.getText().toString();
		mContact.问题困难 = etQuestion.getText().toString();
		return true;
	}

	// 从客户列表选择客户名称
	private void selectClientName() {
		/*
		 * Intent intent = new Intent(ClientConstactNewActivity.this,
		 * ClientListActivity.class);
		 * intent.putExtra(ClientListActivity.SELECT_CLIENT, true);
		 * startActivityForResult(intent, SELECT_CLIENT_CODE);
		 */

		ClientBiz.selectClient_Changhui(mContext, true);

	}

	/**
	 * 解析表单中Fields的json数据,转为FieldInfo对象的字段名和字段值的map集合，必须是FieldInfo类型
	 * 
	 * @param jsonStr
	 *            json字符串
	 * @return
	 */
	public static HashMap<String, Dict> convertDictsJson2Map(String jsonStr) {
		Gson gson = new Gson();
		Type type = new TypeToken<HashMap<String, Dict>>() {
		}.getType(); // 指定集合对象属性
		return gson.fromJson(jsonStr, type);
	}
}