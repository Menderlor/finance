package com.cedarhd;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.adapter.ConstactStatusListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.BoeryunCommentItemView;
import com.cedarhd.control.DiscussListHelper;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.helpers.BoeryunTypeMapper;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.动态;
import com.cedarhd.models.客户联系记录;
import com.cedarhd.models.日志评论;
import com.cedarhd.models.评论;
import com.cedarhd.models.销售机会;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MessageUtil;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 联系详情页面 ，新建联系和联系详情使用的是同一个UI布局，但由于多个页面同时跳转到这个页面，逻辑判断较多，不便管理，所以分为两个页面
 * 新建联系ClientConstactNewActivity
 * 
 * @author bohr
 * 
 */
public class ClientConstactInfoActivity extends BaseActivity implements
		OnClickListener {
	public static final String TAG = "ClientConstactInfoActivity";
	public static final String CLIENTTAG = "ClientTag";
	private final int ID_TV_MORE = 101;// 查看更多的id

	public static final int RESULT_CODE_SUCCESS = 0;
	public static final int RESULT_CODE_FAILED = 1;
	// public static final int SHOW_ADD_CONTACT = 2;
	private static final int SHOW_DATAPICKExpirationTime = 13;
	private static final int SHOW_CLINET_NAME = 14; // 客户名称
	public static final int SELECT_CLIENT_CODE = 15; // 选择客户名称
	public static final int SELECT_SALE_CHANCE_CODE = 16; // 选择销售机会
	private boolean isNewClient = false; // 标志位是否新建客户
	// private boolean isEdit; // 默认不可编辑
	private int clientId;
	private String ConstactStatus;
	private int statusid = 0;// 状态编号
	private List<Integer> statusIndex = new ArrayList<Integer>();
	private List<String> statusList = new ArrayList<String>();
	private List<Map<String, Object>> list; // 客户列表
	private int mYear;
	private int mMonth;
	private int mDay;
	private String mUserSelectId = "";
	private String mUserSelectName = "";

	private 销售机会 mSaleChance;
	private int saleChanceId;
	private Context context;
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();;
	private DictionaryHelper dictionaryHelper;
	private HandlerNewContact handler;
	private HandlerUpdateContact handlerUpdate;
	private AddImageHelper addImageHelper;
	private DiscussListHelper discussListHelper;
	List<评论> listDiscuss = new ArrayList<评论>();
	private 客户联系记录 item;
	private TextView tvTitle;
	private EditText etClientName;
	private EditText etContactName;
	private EditText etContactTime;
	private EditText etContactStatus;
	private EditText etContactContent;
	private EditText etSalerName;
	private EditText etSaleChance;
	private ImageView imageViewDone;
	private ProgressBar pBar;
	private HorizontalScrollViewAddImage llAddImage;
	private LinearLayout rlDiscuss; // 评论按钮功能区
	// private Button btnDiscuss; // 评论功能按钮
	private RelativeLayout rlPublishDiscuss; // 发表评论区域
	// private Button btnPublishDiscuss;// 发表评论按钮
	private EditText etDiscussContent;// 评论内容输入区
	private LinearLayout rlDiscussContent; // 评论内容显示区
	private ListView lvDiscuss; // 评论列表
	private ImageView ivQuitDiscuss; // 取消评论
	private ImageView ivPublishDiscuss;// 发表评论按钮
	// private Button btnSpeek2; // 说话按钮
	// ImageView ivKeybord2;// 键盘输入
	private LinearLayout llDiscuss;// 评论区
	private LinearLayout llContactStatus;// 联系阶段选择区
	private EditText etDiscuss;
	private TextView btnDiscussCount;
	public static final int EDIT_CONTENT_CODE = 7; // 编辑任务内容
	private LinearLayout llDiscussCount;// 联系阶段选择区
	private ConstactStatusListViewAdapter adapter;
	private String str;// 联系的内容
	private LinearLayout ClentConatct_root;// 评论显示区
	private LinearLayout Clentcontact_hot;// 热门评论
	private ImageButton Taking;

	/**
	 * 新评论
	 * 
	 * @author BOHR
	 * 
	 */
	public class HandlerNewContact extends Handler {
		// public static final int GET_LOG_NOW_SUCCESS = 0;
		// public static final int GET_LOG_NOW_FAILED = 1;
		public static final int UPDATE_Contact_SUCCESS = 2;
		public static final int UPDATE_Contact_FAILED = 3;
		public static final int UPDATE_Contacts_SUCCESS = 1201;
		public static final int UPDATE_Contacts_FAILED = 1202;
		// public static final int INITVIEW = 4; // 初始化
		private final int GET_DISCUSS_SUCCESS = 5; // 获得评论列表成功
		private final int GET_DISCUSS_FAILED = 6; // 获得评论列表失败

		public final int GET_DYNAMIC_SUCCESS = 7; // 点击动态，获取联系记录成功
		public final int GET_DYNAMIC_FAILED = 8; // 点击动态，获取联系记录失败

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == UPDATE_Contact_SUCCESS) { // 评论成功
				MessageUtil.ToastMessage(context, "评论成功！");
				llDiscuss.setVisibility(View.VISIBLE);
				if (discussListHelper == null) {
					discussListHelper = new DiscussListHelper(context,
							listDiscuss, lvDiscuss, rlDiscussContent);
				}
				loadDiscussList();
			}
			if (msg.what == UPDATE_Contact_FAILED) {
				MessageUtil.ToastMessage(context, "修改失败！");
			}
			if (msg.what == GET_DISCUSS_SUCCESS) {// 获得评论列表成功
				etDiscussContent.setText("");
				listDiscuss = (List<评论>) msg.obj;
				List<日志评论> list_rizhi = new ArrayList<日志评论>();
				for (int i = 0; i < listDiscuss.size(); i++) {
					list_rizhi.add(BoeryunTypeMapper.MapperTo评论中文(listDiscuss
							.get(i)));
				}
				new BoeryunCommentItemView(context, list_rizhi,
						ClentConatct_root).createCommentView();
				// 显示评论内容
				rlDiscussContent.setVisibility(View.GONE);
				ClentConatct_root.setVisibility(View.VISIBLE);
				Clentcontact_hot.setVisibility(View.VISIBLE);
				if (listDiscuss.size() == 0) {
					rlDiscussContent.setVisibility(View.GONE);
					ClentConatct_root.setVisibility(View.GONE);
					Clentcontact_hot.setVisibility(View.GONE);
				}
				discussListHelper.setmList(listDiscuss);
				btnDiscussCount.setText(listDiscuss.size() + "");
			}
			if (msg.what == GET_DISCUSS_FAILED) {// 获得评论列表失败
				rlDiscussContent.setVisibility(View.GONE);
				ClentConatct_root.setVisibility(View.GONE);
				Clentcontact_hot.setVisibility(View.GONE);
			}

			if (msg.what == GET_DYNAMIC_SUCCESS) {
				ProgressDialogHelper.dismiss();
				动态 dynamic = (动态) msg.obj;
				item = dynamic.Contacts;
				if (item != null) {
					initViews(item);
					init();
				}
			}
			if (msg.what == GET_DYNAMIC_FAILED) {
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "加载联系记录失败，请稍后重试", Toast.LENGTH_LONG)
						.show();
			}
			if (msg.what == UPDATE_Contact_SUCCESS) { // 评论成功
				MessageUtil.ToastMessage(context, "评论成功！");
				etDiscuss.setText("");
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							zlServiceHelper.getContactsDiscuss(item.getId()
									+ "", handler);
						} catch (Exception e) {
							Toast.makeText(context, "评论异常", Toast.LENGTH_SHORT)
									.show();
						}
					}
				}).start();
				String orderNo = item.getId() + "";
				zlServiceHelper.getDiscuss(orderNo, handler);
			}
			if (msg.what == UPDATE_Contact_FAILED) {
				MessageUtil.ToastMessage(context, "评论失败！");
			}

		}
	}

	/**
	 * 修改联系记录
	 * 
	 * @author BOHR
	 */
	public class HandlerUpdateContact extends Handler {
		public static final int UPDATE_Contact_SUCCESS = 2;
		public static final int UPDATE_Contact_FAILED = 3;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ProgressDialogHelper.dismiss();
			if (msg.what == UPDATE_Contact_SUCCESS) {
				MessageUtil.ToastMessage(context, "保存成功！");
				ClientConstactListActivity.isResume = true;
				finish();
			}
			if (msg.what == UPDATE_Contact_FAILED) {
				MessageUtil.ToastMessage(context, "保存失败！");
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_constact_info_activity);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		findviews();
		setOnClickListener();
		loadStausDict();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			// 联系详情
			item = (客户联系记录) bundle.get(TAG);
			if (item != null) {
				statusid = item.getStatus();
				saleChanceId = item.ChanceId;
				initViews(item);
				init();
			}
		} else {
			init();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 监听信鸽 Notification点击打开的通知
		XGPushClickedResult clickedResult = XGPushManager
				.onActivityStarted(this);
		if (clickedResult != null) {
			// Toast.makeText(this, "来自信鸽：" + clickedResult.toString(),
			// Toast.LENGTH_LONG).show();
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
						if (dynamic != null && dynamic.Contacts != null) {
							// 设置联系记录为已读
							// zlServiceHelper.ReadLog(dynamic.Log, context);

							// 发送到handler中进行处理
							Message msg = handler.obtainMessage();
							msg.obj = dynamic;
							msg.what = handler.GET_DYNAMIC_SUCCESS;
							handler.sendMessage(msg);
						} else {
							handler.sendEmptyMessage(handler.GET_DYNAMIC_FAILED);
						}
					}
				}).start();
			} catch (JSONException e) {
				e.printStackTrace();
				LogUtils.e("erro", e.toString());
				handler.sendEmptyMessage(handler.GET_DYNAMIC_FAILED);
			}
		}
	}

	/**
	 * 加载状态字典表
	 */
	private void loadStausDict() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				//
				ConstactStatus = zlServiceHelper.getConstactStatus()
						.replaceAll("\"", "");
				LogUtils.i("pystatusSpLit", "" + ConstactStatus);
				String[] StringResult = ConstactStatus.split(",");
				for (int i = 0; i < StringResult.length; i++) {
					LogUtils.d("statusSpLit2", "" + StringResult[i]);
					statusIndex.add(Integer.parseInt(StringResult[i].split(":")[0]));
					statusList.add(StringResult[i].split(":")[1]);
				}
				// Message message = new Message();
				// message.what = handler.INITVIEW;
				// handler.sendMessage(message);
			}
		}).start();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CODE_FAILED);
		super.onBackPressed();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case SHOW_DATAPICKExpirationTime:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SHOW_DATAPICKExpirationTime:
			return new DatePickerDialog(this, mDateExpirationTimeSetListener,
					mYear, mMonth, mDay);
		}
		return null;
	}

	// 从客户列表选择客户名称
	private void selectClientName() {
		Intent intent = new Intent(ClientConstactInfoActivity.this,
				ClientListActivity.class);
		intent.putExtra(ClientListActivity.SELECT_CLIENT, true);
		startActivityForResult(intent, SELECT_CLIENT_CODE);
	}

	// 从销售机会列表选择销售机会名称
	private void selectSaleChance() {
		Intent intent = new Intent(ClientConstactInfoActivity.this,
				SaleChanceListActivity.class);
		intent.putExtra(SaleChanceListActivity.SELECT_SALE_CHANCE, true);
		startActivityForResult(intent, SELECT_SALE_CHANCE_CODE);
	}

	// // 添加上下文菜单
	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View v,
	// ContextMenuInfo menuInfo) {
	// super.onCreateContextMenu(menu, v, menuInfo);
	// menu.setHeaderTitle("选择状态");
	// // 添加菜单项
	// for (int i = 0; i < statusList.length; i++) {
	// menu.add(0, i, 0, statusList[i]);
	// }
	// }
	//
	// // 选择上下文菜单内容
	// @Override
	// public boolean onContextItemSelected(MenuItem item) {
	// // 获得选中上下文菜单中的内容
	// // AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
	// // .getMenuInfo();
	// // int position = acmi.position;
	// // 获得选中上下文菜单的Id
	// statusIndex = item.getItemId();
	// LogUtils.i(TAG, "pos:" + statusIndex);
	// etContactStatus.setText(statusList[statusIndex]);
	// return super.onContextItemSelected(item);
	// }

	/**
	 * 创建对话框中的ListView
	 * 
	 * @return
	 */
	private ListView createListView() {
		ListView lv = new ListView(getApplicationContext());
		list = zlServiceHelper.getClientList(getApplicationContext());
		SimpleAdapter adapter = new SimpleAdapter(
				ClientConstactInfoActivity.this, list,
				R.layout.item_clientname_newconstact, new String[] { "id",
						"clientName" }, new int[] { R.id.tv_id_newcontact_item,
						R.id.tv_name_newcontact_item });
		TextView tvFooter = new TextView(getApplicationContext());
		// tvFooter.setTextColor(0xFF0);
		tvFooter.setText("创建一个新的客户....");
		lv.addFooterView(tvFooter);
		lv.setAdapter(adapter);
		return lv;
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
			etSalerName.setText(mUserSelectName);
		} else if (resultCode == RESULT_OK && requestCode == SELECT_CLIENT_CODE) {
			// 取出客户名称字符串
			Bundle bundle = data.getExtras();
			clientId = bundle.getInt(ClientListActivity.ClientId);
			LogUtils.i("kjxi", "clientId:" + clientId);
			if (clientId != 0) {
				etClientName.setText(dictionaryHelper
						.getClientNameById(clientId));
			}
		} else if (resultCode == RESULT_OK
				&& requestCode == SELECT_SALE_CHANCE_CODE) {
			// 取出销售机会
			Bundle bundle = data.getExtras();
			if (bundle != null) {
				try {
					mSaleChance = (销售机会) bundle
							.getSerializable(SaleChanceListActivity.SELECT_SALE_CHANCE);
					// 显示销售机会的内容
					etSaleChance.setText("" + mSaleChance.getContent());
					saleChanceId = mSaleChance.getId();

					etClientName.setText("" + mSaleChance.getCustomerName());
					clientId = mSaleChance.getCustomerId();
				} catch (Exception e) {
					LogUtils.e(TAG, "" + e);
				}
			}
		} else if (resultCode == RESULT_OK && requestCode == EDIT_CONTENT_CODE) {
			// 取出任务内容
			Bundle bundle = data.getExtras();
			// etContent = bundle.getInt(ClientListActivity.ClientId);
			etContactContent.setText(bundle
					.getString(TaskContentActivity.Content));

		} else if (resultCode == RESULT_OK) {
			// 拍照上传图片控件选择或拍照后显示
			if (requestCode == addImageHelper.CAMERA_TAKE_HELPER
					|| requestCode == addImageHelper.PICKED_PHOTO_WITH_DATA) {
				addImageHelper.refresh(requestCode, data);
			}
		}

	}

	private void addHeader() {
		TextView tvHeader = new TextView(context);
		tvHeader.setId(ID_TV_MORE);
		tvHeader.setTextColor(0xFF28B69B);
		tvHeader.setTextSize(14);
		tvHeader.setBackgroundColor(0xFFEEEEEE);
		tvHeader.setClickable(true);
		tvHeader.setText("查看更多评论");
		AbsListView.LayoutParams tvparams = new AbsListView.LayoutParams(
				Global.mWidthPixels, (int) ViewHelper.dip2px(context, 35));
		tvHeader.setPadding(20, 20, 0, 0);
		tvHeader.setLayoutParams(tvparams);
		lvDiscuss.addHeaderView(tvHeader);
		tvHeader.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// int height = discussListHelper.getHeight();
				// LogUtils.i("height", "height:" + height);
				// LinearLayout.LayoutParams params = new
				// LinearLayout.LayoutParams(
				// LayoutParams.FILL_PARENT, height);
				// rlDiscussContent.setLayoutParams(params);
				Intent intent = new Intent(ClientConstactInfoActivity.this,
						ClientContactDiscussActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(ClientConstactInfoActivity.TAG, item);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void findviews() {
		// 设置软键盘不弹出
		// etClientName.setInputType(InputType.TYPE_NULL);
		dictionaryHelper = new DictionaryHelper(ClientConstactInfoActivity.this);
		tvTitle = (TextView) findViewById(R.id.textViewTitle_constactNew1);
		etClientName = (EditText) findViewById(R.id.etClientName_newconstact1);
		etContactName = (EditText) findViewById(R.id.etContactName_newconstact1);
		etContactTime = (EditText) findViewById(R.id.etContactTime_newconstact1);
		etContactStatus = (EditText) findViewById(R.id.etContactStatus_newconstact1);
		etContactContent = (EditText) findViewById(R.id.editTextContent_constact);
		etSalerName = (EditText) findViewById(R.id.et_saler_newconstact1);
		etSaleChance = (EditText) findViewById(R.id.etSalechance_newconstact1);
		imageViewDone = (ImageView) findViewById(R.id.ivDone_contact1);
		pBar = (ProgressBar) findViewById(R.id.progressbar_addconstact1);
		llAddImage = (HorizontalScrollViewAddImage) findViewById(R.id.addImg_constact_new1);
		// btnDiscuss = (Button) findViewById(R.id.btn_discuss_constact_new1);
		rlPublishDiscuss = (RelativeLayout) findViewById(R.id.rl_publich_discuss_constact_info);
		// btnPublishDiscuss = (Button)
		// findViewById(R.id.btn_publich_discuss_constact_info1);
		etDiscussContent = (EditText) findViewById(R.id.et_discuss_content_constact_info1);
		etDiscussContent.setOnFocusChangeListener(onFocusAutoClearListener);
		rlDiscuss = (LinearLayout) findViewById(R.id.rl_discuss_constact_new1);
		rlDiscuss.setBackgroundColor(0xFFEEEEEE);
		rlDiscussContent = (LinearLayout) findViewById(R.id.rl_discuss_content_constact_new1);
		lvDiscuss = (ListView) findViewById(R.id.lv_discuss_constact_new1);
		ivQuitDiscuss = (ImageView) findViewById(R.id.iv_discuss_quit_constact_info1);// 退出评论
		ivPublishDiscuss = (ImageView) findViewById(R.id.iv_discuss_submit_constact_info1); // 发表评论
		llDiscuss = (LinearLayout) findViewById(R.id.ll_discuss_constact_new1);
		// registerForContextMenu(etContactStatus);
		// 设置业务员默认为自己
		mUserSelectId = Global.mUser.Id;
		etSalerName.setText(dictionaryHelper.getUserNameById(Global.mUser.Id));
		etContactTime.setText(ViewHelper.getDateString());
		// etContactStatus.setText(statusList[statusIndex]);

		context = ClientConstactInfoActivity.this;
		handler = new HandlerNewContact();
		handlerUpdate = new HandlerUpdateContact();

		addImageHelper = new AddImageHelper(this,
				ClientConstactInfoActivity.this, llAddImage, null, false);
		// btnSpeek2 = (Button) findViewById(R.id.btn_speek2_constact1); // 语音
		// 说话
		// ivKeybord2 = (ImageView) findViewById(R.id.iv_keybord2_constact1);//
		// 键盘
		// btnSpeek2.setOnClickListener(this);
		// ivKeybord2.setOnClickListener(this);
		etContactName.setOnClickListener(this);
		llContactStatus = (LinearLayout) findViewById(R.id.ll_ContactStatus);

		etDiscuss = (EditText) findViewById(R.id.et_constant_discuss);
		// etDiscuss.setFocusable(false);
		btnDiscussCount = (TextView) findViewById(R.id.tv_count);
		llDiscussCount = (LinearLayout) findViewById(R.id.ll_btn);
		llDiscuss.setVisibility(View.VISIBLE);
		ClentConatct_root = (LinearLayout) findViewById(R.id.ll_root_comment_clentcontact_info);
		Clentcontact_hot = (LinearLayout) findViewById(R.id.ll_hot_comment_clentcontact_info);
		Taking = (ImageButton) findViewById(R.id.ib_constant_zan);
	}

	// 显示联系详情
	private void initViews(final 客户联系记录 item) {
		clientId = item.getCustomer(); // 默认客户
		// btnDiscuss.setVisibility(View.VISIBLE);
		// addHeader();
		if (discussListHelper == null) {
			discussListHelper = new DiscussListHelper(context, listDiscuss,
					lvDiscuss, rlDiscussContent);
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				zlServiceHelper.getContactsDiscuss(item.getId() + "", handler);
			}
		}).start();

		String attach = item.getAttachments();
		if (TextUtils.isEmpty(attach)) {
			llAddImage.setVisibility(View.GONE);
		} else {
			addImageHelper = new AddImageHelper(this,
					ClientConstactInfoActivity.this, llAddImage,
					item.Attachments, false);
		}
		DictionaryHelper dictionaryHelper = new DictionaryHelper(
				ClientConstactInfoActivity.this);
		// String clientNameString = dictionaryHelper.getClientNameById(item
		// .getCustomer());
		String clientNameString = item.getClientName() + "";
		etClientName.setText(clientNameString);
		etContactName.setText(item.getContacts());
		etContactTime.setText(ViewHelper.getDateString(item.getUpdateTime()));
		LogUtils.i("pyinfostatus1", statusList.size() + "");
		LogUtils.i("pyinfostatus2", item.getStatus() + "");
		etContactStatus.setText(item.getStatusName());
		// if (item.getStatus() <= statusList.size() && item.getStatus() >= 1) {
		// int pos = statusIndex.indexOf(item.getStatus());
		// if (pos >= 0 && pos < statusList.size()) {
		// etContactStatus.setText(statusList.get(pos));
		// }
		// } else {
		// etContactStatus.setText("");
		// }
		str = item.getContent();
		etContactContent.setText(item.getContent());
		// String ContentString = item.getContent();
		// etContactContent.setTag(false);
		// caculateHeight(etContactContent, ContentString);
		// LogUtils.i("pyetcontent", ContentString);
		// etContactContent.setTag(false);
		// caculateHeight(etContactContent, item.getContent());

		etSalerName.setText(dictionaryHelper.getUserNameById(item.getSaler()));

		String saleChanceContent = TextUtils.isEmpty(item.ChanceContent) ? ""
				: item.ChanceContent;
		etSaleChance.setText(saleChanceContent);
		tvTitle.setText("联系详情");
		// 铅笔图标，点击变成可编辑状态
		// isEdit = false;
		// imageViewDone.setImageResource(R.drawable.menu_2);
		// imageViewDone.setVisibility(View.GONE);
		etClientName.setTag(false);
		caculateHeight(etClientName, clientNameString);

		etClientName.setEnabled(false);
		etContactName.setEnabled(false);
		etContactTime.setEnabled(false);
		etSalerName.setEnabled(false);
		// etContactStatus.setEnabled(false);
		etContactContent.setEnabled(false);
		etSaleChance.setEnabled(false);
	}

	private void init() {
		// 去除提示信息
		// etClientName.setHint("请选择客户名称...");
		// etContactName.setHint("请输入联系人姓名...");
		// etContactContent.setHint("请输入联系内容...");
		etContactStatus.setHint("请选择状态...");
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		etClientName.setEnabled(true);
		etContactName.setEnabled(true);
		// etContactTime.setEnabled(true);
		etSalerName.setEnabled(true);
		etContactStatus.setEnabled(true);
		etContactContent.setEnabled(true);
		etSaleChance.setEnabled(true);
	}

	/**
	 * 绑定监听事件
	 */
	String content;// 评论内容

	public void setOnClickListener() {
		Taking.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new SpeechDialogHelper(ClientConstactInfoActivity.this,
						ClientConstactInfoActivity.this, etDiscuss, true);
			}
		});
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
				// if (!isEdit) {
				// isEdit = true;
				// imageViewDone.setImageResource(R.drawable.check);
				// init();
				// return;
				// }
				if (!checkout()) {
					Toast.makeText(getApplicationContext(), "必选项不能为空，保存失败！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				item.setCustomer(clientId);
				item.setContacts(etContactName.getText().toString());
				// item.setUpdateTime(etContactTime.getText().toString());
				if (!TextUtils.isEmpty(mUserSelectId)
						&& mUserSelectId.contains(";")) {
					LogUtils.i("keno19", mUserSelectId);
					String str = mUserSelectId.split(";")[0];
					mUserSelectId = str.replace("'", "");
				}
				int salerId = Integer.parseInt(mUserSelectId);
				item.setSaler(salerId);// 业务员
				item.setChanceId(saleChanceId);
				item.setStatus(statusid);
				item.setContent(etContactContent.getText().toString());
				ProgressDialogHelper.show(context);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							zlServiceHelper.updateCutomerContactRecord(item,
									handlerUpdate, null, pBar);
						} catch (Exception e) {
							Toast.makeText(context, "提交异常", Toast.LENGTH_SHORT)
									.show();
						}
					}
				}).start();
			}
		});

		etContactTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(SHOW_DATAPICKExpirationTime);
			}
		});
		etContactTime.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					showDialog(SHOW_DATAPICKExpirationTime);
				}
			}
		});
		etSalerName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectSaler();
			}
		});
		etSalerName.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					selectSaler();
				}
			}
		});

		// 选择客户名称
		etClientName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isNewClient) {
					// showDialog(SHOW_CLINET_NAME);
					selectClientName();
				}
			}
		});
		etClientName.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (!isNewClient) {
						// showDialog(SHOW_CLINET_NAME);
						selectClientName();
					}
				} else {
					isNewClient = false;
				}
			}
		});

		etSaleChance.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectSaleChance();
			}
		});

		// // 评论，弹出发表评论区域
		// btnDiscuss.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// rlPublishDiscuss.setVisibility(View.VISIBLE);
		// // 获得焦点
		// // etDiscussContent.requestFocus();
		// // 弹出软键盘
		// InputMethodManager imm = (InputMethodManager) btnDiscuss
		// .getContext().getSystemService(INPUT_METHOD_SERVICE);
		// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		// rlDiscuss.setVisibility(View.GONE);
		// }
		// });

		// // 发表评论
		// btnPublishDiscuss.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// rlDiscuss.setVisibility(View.VISIBLE);
		// final String content = etDiscussContent.getText().toString();
		// if (!TextUtils.isEmpty(content)) {
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// zlServiceHelper.publishContactsDiscuss(
		// item.Id + "", content, handler);
		// }
		// }).start();
		// } else {
		// Toast.makeText(context, "评论内容不能为空", Toast.LENGTH_LONG)
		// .show();
		// }
		// rlPublishDiscuss.setVisibility(View.GONE);
		//
		// }
		// });
		ivQuitDiscuss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rlPublishDiscuss.setVisibility(View.GONE);
				rlDiscuss.setVisibility(View.VISIBLE);
			}
		});
		// 发表评论btnPublishDiscuss
		ivPublishDiscuss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rlDiscuss.setVisibility(View.VISIBLE);
				final String content = etDiscussContent.getText().toString();
				if (!TextUtils.isEmpty(content)) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								zlServiceHelper.publishContactsDiscuss(item.Id
										+ "", content, handler);
							} catch (Exception e) {
								Toast.makeText(context, "发表评论异常",
										Toast.LENGTH_SHORT).show();
							}
						}
					}).start();
				} else {
					Toast.makeText(ClientConstactInfoActivity.this, "评论内容不能为空",
							Toast.LENGTH_LONG).show();
				}
				rlPublishDiscuss.setVisibility(View.GONE);
				rlDiscuss.setVisibility(View.VISIBLE);
			}
		});

		/*
		 * 联系状态
		 * 
		 * @author py 2014.8.15
		 */
		llContactStatus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				LayoutInflater inflater = LayoutInflater
						.from(ClientConstactInfoActivity.this);// 渲染器
				View dialog = inflater.inflate(R.layout.taskclassifydialog,
						null);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						ClientConstactInfoActivity.this);
				builder.setTitle("请选择状态");
				builder.setView(dialog);
				final AlertDialog alertdialog = builder.create();
				ListView listView = (ListView) dialog
						.findViewById(R.id.lv_classify);
				adapter = new ConstactStatusListViewAdapter(
						ClientConstactInfoActivity.this, statusList, null);
				listView.setAdapter(adapter);
				listView.setItemsCanFocus(false);
				listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						String item = statusList.get(arg2);
						// item.Id;
						etContactStatus.setText(item);
						statusid = statusIndex.get(arg2);
						alertdialog.dismiss();
					}

				});
				alertdialog.show();
			}
		});
		llContactStatus.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				LayoutInflater inflater = LayoutInflater
						.from(ClientConstactInfoActivity.this);// 渲染器
				View dialog = inflater.inflate(R.layout.taskclassifydialog,
						null);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						ClientConstactInfoActivity.this);
				builder.setTitle("请选择状态");
				builder.setView(dialog);
				final AlertDialog alertdialog = builder.create();
				ListView listView = (ListView) dialog
						.findViewById(R.id.lv_classify);
				adapter = new ConstactStatusListViewAdapter(
						ClientConstactInfoActivity.this, statusList, null);
				listView.setAdapter(adapter);
				listView.setItemsCanFocus(false);
				listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						String item = statusList.get(arg2);
						// item.Id;
						etContactStatus.setText(item);
						statusid = statusIndex.get(arg2);
						alertdialog.dismiss();
					}

				});
				alertdialog.show();
			}
		});
		etContactStatus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				LayoutInflater inflater = LayoutInflater
						.from(ClientConstactInfoActivity.this);// 渲染器
				View dialog = inflater.inflate(R.layout.taskclassifydialog,
						null);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						ClientConstactInfoActivity.this);
				builder.setTitle("请选择状态");
				builder.setView(dialog);
				final AlertDialog alertdialog = builder.create();
				ListView listView = (ListView) dialog
						.findViewById(R.id.lv_classify);
				adapter = new ConstactStatusListViewAdapter(
						ClientConstactInfoActivity.this, statusList, null);
				listView.setAdapter(adapter);
				listView.setItemsCanFocus(false);
				listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						String item = statusList.get(arg2);
						// item.Id;
						etContactStatus.setText(item);
						statusid = statusIndex.get(arg2);
						alertdialog.dismiss();
					}

				});
				alertdialog.show();
			}
		});
		etContactStatus.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				LayoutInflater inflater = LayoutInflater
						.from(ClientConstactInfoActivity.this);// 渲染器
				View dialog = inflater.inflate(R.layout.taskclassifydialog,
						null);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						ClientConstactInfoActivity.this);
				builder.setTitle("请选择状态");
				builder.setView(dialog);
				final AlertDialog alertdialog = builder.create();
				ListView listView = (ListView) dialog
						.findViewById(R.id.lv_classify);
				adapter = new ConstactStatusListViewAdapter(
						ClientConstactInfoActivity.this, statusList, null);
				listView.setAdapter(adapter);
				listView.setItemsCanFocus(false);
				listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						String item = statusList.get(arg2);
						// item.Id;
						etContactStatus.setText(item);
						statusid = statusIndex.get(arg2);
						alertdialog.dismiss();
					}

				});
				alertdialog.show();
			}
		});
		etDiscuss.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				rlPublishDiscuss.setVisibility(View.VISIBLE);
				// InputMethodManager imm = (InputMethodManager) etDiscuss
				// .getContext().getSystemService(INPUT_METHOD_SERVICE);
				// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				rlDiscuss.setVisibility(View.GONE);
			}
		});
		etContactContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ClientConstactInfoActivity.this,
						ClientConstactContentActivity.class);
				// intent.putExtra(TaskContentActivity.EDITECONTENT, true);
				Bundle bundle = new Bundle();
				bundle.putSerializable(ClientConstactInfoActivity.TAG, str);
				intent.putExtras(bundle);
				startActivityForResult(intent, EDIT_CONTENT_CODE);
				// startActivity(intent);
			}
		});
		// etContactContent.setOnFocusChangeListener(new OnFocusChangeListener()
		// {
		//
		// @Override
		// public void onFocusChange(View arg0, boolean arg1) {
		// // TODO Auto-generated method stub
		// if (arg1) {
		// Intent intent = new Intent(ClientConstactInfoActivity.this,
		// ClientConstactContentActivity.class);
		// // intent.putExtra(TaskContentActivity.EDITECONTENT, true);
		// Bundle bundle = new Bundle();
		// bundle.putSerializable(ClientConstactInfoActivity.TAG, str);
		// intent.putExtras(bundle);
		// startActivityForResult(intent, EDIT_CONTENT_CODE);
		// // startActivity(intent);
		// }
		//
		// }
		// });
		llDiscussCount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// // TODO Auto-generated method stub
				// Intent intent = new Intent(ClientConstactInfoActivity.this,
				// ClientContactDiscussActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putSerializable(ClientConstactInfoActivity.TAG, item);
				// intent.putExtras(bundle);
				// startActivity(intent);
				content = etDiscuss.getText().toString().trim();
				if (!TextUtils.isEmpty(content)) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								zlServiceHelper.publishContactsDiscuss(item.Id
										+ "", content, handler);
							} catch (Exception e) {
								Toast.makeText(context, "发表评论异常",
										Toast.LENGTH_SHORT).show();
							}
						}
					}).start();
				} else {
					Toast.makeText(ClientConstactInfoActivity.this, "评论内容不能为空",
							Toast.LENGTH_LONG).show();
				}

			}
		});
		etDiscuss.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// // TODO Auto-generated method stub
				// Intent intent = new Intent(ClientConstactInfoActivity.this,
				// ClientConstactDiscussActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putSerializable(TAG, item);
				// intent.putExtras(bundle);
				// startActivity(intent);

			}
		});
	}

	/**
	 * 选择用户
	 */
	private void selectSaler() {
		Intent intent = new Intent(ClientConstactInfoActivity.this,
				User_SelectActivityNew_zmy.class);
		Bundle bundle = new Bundle();
		// bundle.putString("UserSelectId", mUserSelectId);
		bundle.putBoolean(User_SelectActivityNew_zmy.SELECT_EMPLOYEE, true); // 单选
		intent.putExtras(bundle);
		startActivityForResult(intent, 0);
	}

	private void updateDateFromDisplay() {
		etContactTime.setText(new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay));
	}

	private DatePickerDialog.OnDateSetListener mDateExpirationTimeSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDateFromDisplay();
		}
	};

	/**
	 * 提交校验
	 * 
	 * @return
	 */
	private boolean checkout() {
		String clientName = etClientName.getText().toString();
		String contactName = etContactName.getText().toString();
		String time = etContactTime.getText().toString();
		String saler = etSalerName.getText().toString();
		String status = etContactStatus.getText().toString();
		String content = etContactContent.getText().toString();

		if (TextUtils.isEmpty(clientName) || TextUtils.isEmpty(contactName)
				|| TextUtils.isEmpty(time) || TextUtils.isEmpty(saler)
				|| TextUtils.isDigitsOnly(status) || TextUtils.isEmpty(content)) {
			return false;
		}
		return true;
	}

	/**
	 * 根据文字内容计算重绘EditText的高度
	 * 
	 * @param linearLayout
	 *            父控件
	 * @param editText
	 *            文本控件
	 * @param contents
	 *            文字内容
	 */
	private void caculateHeight(final EditText editText, final String contents) {
		// 监听控件绘制
		ViewTreeObserver vto = editText.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				Object editTag = editText.getTag();
				boolean hasMessured = (editTag == null) ? false
						: (Boolean) editTag;
				if (hasMessured == false) {
					// hasMessured = true;
					editText.setTag(true);
					int width = editText.getWidth(); // 控件宽度
					int height = editText.getHeight(); // 控件高度
					LogUtils.i("contents", contents + "---》width=" + width
							+ ",height=" + height);
					if (width != 0 && height != 0) {
						int len = TextUtils.isEmpty(contents) ? 0 : contents
								.length(); // 字数
						float px = editText.getTextSize(); // 得到字体像素
						double length = Math.floor(width / px); // 能容纳字母个数
						if (len > length) {
							// int llWidth =
							// linearLayout.getLayoutParams().width;
							LinearLayout linearLayout = (LinearLayout) editText
									.getParent();
							int llWidth = linearLayout.getLayoutParams().width;
							int offset = (int) (len / length); // 计算出需要行数
							linearLayout
									.setLayoutParams(new LinearLayout.LayoutParams(
											llWidth, (int) (height + px
													* offset)));
						}
					}
				}
				return true;
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getId();
		switch (id) {
		// case R.id.btn_speek2_constact1:
		// new SpeechDialogHelper(context, this, etContactContent, true);
		// break;
		// case R.id.iv_keybord2_constact1:
		// // ivKeybord2.setVisibility(View.GONE);
		// // btnSpeek2.setVisibility(View.GONE);
		// // mEditTextContent.setVisibility(View.VISIBLE);
		// // ivSpeek2.setVisibility(View.VISIBLE);
		// etContactContent.requestFocus();
		// // 弹出软键盘
		// InputMethodManager m2 = (InputMethodManager) etContactName
		// .getContext().getSystemService(INPUT_METHOD_SERVICE);
		// m2.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		// break;
		case R.id.etContactName_newconstact1:
			new SpeechDialogHelper(context, this, etContactName, false);
			break;
		default:
			break;
		}
	}

	/**
	 * 加载评论列表
	 */
	private void loadDiscussList() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					zlServiceHelper.getContactsDiscuss(item.getId() + "",
							handler);
				} catch (Exception e) {
					Toast.makeText(context, "评论异常", Toast.LENGTH_SHORT).show();
				}
			}
		}).start();
	}

	public static OnFocusChangeListener onFocusAutoClearListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			EditText textView = (EditText) v;
			String hint;
			if (hasFocus) {
				hint = textView.getHint().toString();
				textView.setTag(hint);
				textView.setHint("");
			} else {
				hint = textView.getTag().toString();
				textView.setHint(hint);
			}
		}
	};

}