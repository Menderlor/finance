package com.cedarhd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.DiscussListHelper;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.订单;
import com.cedarhd.models.评论;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单详情页面
 * 
 * @author bohr
 * 
 */
public class OrderActivity extends BaseActivity {
	private String mPictureFile = null; // 照片文件名
	private static final int CAMERA_TAKE = 1;
	public static final int NEW_CONTENT_SUCCESS = 2;
	public static final int NEW_CONTENT_FAILURE = 3;
	private final int GET_DISCUSS_SUCCESS = 5; // 获得评论列表成功
	private final int GET_DISCUSS_FAILED = 6; // 获得评论列表失败
	public static final int UPLOAD_FAILED = 7; // 上传图片成功
	public static final int UPLOAD_SUCCESS = 8; // 上传图片失败
	public static final int UPDATE_FAILED = 9; // 修改订单成功
	public static final int UPDATE_SUCCESS = 10; // 修改订单失败

	private final int ID_TV_MORE = 101;// 查看更多的id
	private List<String> photoPathList = new ArrayList<String>(); // 要上传照片路径列表

	private 订单 itemOrder;
	private int stage2; // 订单阶段
	private String stageName;
	private ORMDataHelper helper;
	private TextView tvOrderNO;
	private TextView tvStage; // 阶段
	private TextView tvProject; // 定做项目
	private TextView tvMessureTime; // 预计测量时间
	private TextView tvClientName; // 联系人
	private TextView tvPhone; // 联系电话
	private TextView tvAddress; // 联系地址
	private TextView tvTotal; // 金额
	private TextView tvSaler;
	private TextView tvDesiner;// 设计师
	// private TextView tvState;

	private DiscussListHelper discussListHelper;
	private DictionaryHelper dictionaryHelper;
	// private LinearLayout llImages; // 添加图片布局
	private HorizontalScrollViewAddImage llLoAddImage;
	private RelativeLayout rlDiscuss; // 评论按钮功能区
	// private Button btnDiscuss; // 评论功能按钮
	private ImageView ivDiscuss;// 评论功能按钮
	private RelativeLayout rlPublishDiscuss; // 发表评论区域
	// private Button btnPublishDiscuss;// 发表评论按钮
	private ImageView ivPublishDiscuss;// 发表评论按钮
	private ImageView ivQuitDiscuss; // 取消评论
	private EditText etDiscussContent;// 评论内容输入区
	private LinearLayout rlDiscussContent; // 评论内容显示区
	private ListView lvDiscuss; // 评论列表

	// private Button btnTest; // 测量记录
	private ImageView ivTest; // 测量记录
	private ImageView btnSave;// 保存
	private ProgressBar pBar; // 上传图片进度条
	private Context context;
	private AddImageHelper addImageHelper;
	private ContentAdapter adapter;
	private List<评论> listDiscuss = new ArrayList<评论>();
	private ZLServiceHelper zlServiceHelper;

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			if (what == NEW_CONTENT_FAILURE) {
				Toast.makeText(OrderActivity.this, "评论失败", Toast.LENGTH_SHORT)
						.show();
			}
			if (what == NEW_CONTENT_SUCCESS) {
				Toast.makeText(OrderActivity.this, "评论成功", Toast.LENGTH_SHORT)
						.show();
				reload();
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

				// 设置高度
				int height = discussListHelper.getHeight();
				LogUtils.i("height", "height:" + height);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, height);
				rlDiscussContent.setLayoutParams(params);
			}
			if (msg.what == GET_DISCUSS_FAILED) {// 获得评论列表失败
				rlDiscussContent.setVisibility(View.GONE);
			}
			if (msg.what == UPLOAD_FAILED) {// 上传图片失败
				Toast.makeText(context, "上传图片失败", Toast.LENGTH_SHORT).show();
			}
			if (msg.what == UPLOAD_SUCCESS) { // 上传图片成功
				Toast.makeText(context, "上传图片成功", Toast.LENGTH_SHORT).show();
				finish();
			}
			if (what == UPDATE_FAILED) {
				Toast.makeText(OrderActivity.this, "修改订单阶段失败",
						Toast.LENGTH_SHORT).show();
			}
			if (what == UPDATE_SUCCESS) {
				Toast.makeText(OrderActivity.this, "修改订单阶段成功",
						Toast.LENGTH_SHORT).show();
				if (!TextUtils.isEmpty(stageName)) {
					tvStage.setText(stageName);
				}
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orderinfo);
		Bundle bundle = getIntent().getExtras();
		itemOrder = (订单) bundle.getSerializable(OrderListActivity.TAG);
		LogUtils.i("kjxTest", "---->Id:" + itemOrder.Id);
		findViews(itemOrder);
		setOnClickListener();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (rlPublishDiscuss.getVisibility() == View.GONE) {
				finish();
			} else {
				rlPublishDiscuss.setVisibility(View.GONE);
				rlDiscuss.setVisibility(View.VISIBLE);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 添加上下文菜单
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("修改阶段");
		// 添加菜单项
		menu.add(0, 1, 0, "等电话上门测量");
		menu.add(0, 2, 0, "已初测");
		menu.add(0, 3, 0, "已复测");
		menu.add(0, 10, 0, "已出图");
	}

	// 选择上下文菜单内容
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// 获得选中上下文菜单选中项的编号
		stage2 = item.getItemId();
		stageName = item.getTitle().toString();
		LogUtils.i("menuPos", "获得选中上下文菜单选中项的编号:" + stage2 + "---" + stageName);
		itemOrder.setStage2(stage2);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 修改订单状态
				try {
					zlServiceHelper.UpdateOrder(itemOrder, handler);
				} catch (Exception e) {
					Toast.makeText(context, "修改订单状态异常", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}).start();
		return super.onContextItemSelected(item);
	}

	public void findViews(final 订单 item) {
		context = OrderActivity.this;
		dictionaryHelper = new DictionaryHelper(context);
		zlServiceHelper = new ZLServiceHelper();
		// 获得制定订单对应的评论列表
		// list = zlServiceHelper.getOrderContent(item.OrderNo);
		helper = ORMDataHelper.getInstance(OrderActivity.this);
		llLoAddImage = (HorizontalScrollViewAddImage) findViewById(R.id.AddImage_orderinfo);

		tvOrderNO = (TextView) findViewById(R.id.textview_oderinfo__oderNo);
		tvStage = (TextView) findViewById(R.id.textview_oderinfo_stage); // 阶段
		tvProject = (TextView) findViewById(R.id.textview_oderinfo__project); // 定做项目
		tvMessureTime = (TextView) findViewById(R.id.textview_oderinfo_measureTime); // 预计测量时间
		tvClientName = (TextView) findViewById(R.id.textview_oderinfo_clientName); // 联系人
		tvPhone = (TextView) findViewById(R.id.textview_oderinfo_phone); // 联系电话
		tvAddress = (TextView) findViewById(R.id.textview_oderinfo_address); // 联系地址
		tvTotal = (TextView) findViewById(R.id.textview_oderinfo_total); // 电话
		tvSaler = (TextView) findViewById(R.id.textview_oderinfo_saler);
		tvDesiner = (TextView) findViewById(R.id.textview_oderinfo_desiger);
		// tvState = (TextView) findViewById(R.id.textview_oderinfo_testState);

		// btnDiscuss = (Button) findViewById(R.id.btn_discuss_order_info);
		ivDiscuss = (ImageView) findViewById(R.id.iv_discuss_order_info);
		rlPublishDiscuss = (RelativeLayout) findViewById(R.id.rl_publich_discuss_order_info);
		// btnPublishDiscuss = (Button)
		// findViewById(R.id.btn_publich_discuss_order_info);
		ivPublishDiscuss = (ImageView) findViewById(R.id.iv_discuss_submit_order_info); // 发表评论
		ivQuitDiscuss = (ImageView) findViewById(R.id.iv_discuss_quit_order_info);// 退出评论
		etDiscussContent = (EditText) findViewById(R.id.et_discuss_content_order_info);
		rlDiscuss = (RelativeLayout) findViewById(R.id.rl_discuss_order_info);
		rlDiscussContent = (LinearLayout) findViewById(R.id.rl_discuss_content_info_order_info);
		lvDiscuss = (ListView) findViewById(R.id.lv_discuss_order_info);
		// btnTest = (Button) findViewById(R.id.btn_test_order_info);
		ivTest = (ImageView) findViewById(R.id.iv_test_order_info);
		btnSave = (ImageView) findViewById(R.id.iv_save_orderinfo);
		pBar = (ProgressBar) findViewById(R.id.pbar_order_info);
		// addHeader(); //查看更多评论(暂时隐藏)
		adapter = new ContentAdapter();
		lvDiscuss.setAdapter(adapter);

		if (discussListHelper == null) {
			discussListHelper = new DiscussListHelper(context, listDiscuss,
					lvDiscuss, rlDiscussContent);
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					zlServiceHelper.getDiscuss(item.getId(), handler);
				} catch (Exception e) {
					Toast.makeText(context, "获取评论列表异常", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}).start();
		tvOrderNO.setText(item.OrderNo);
		tvStage.setText(item.StageName);
		tvProject.setText(item.CustomProject);
		tvMessureTime.setText(item.MeasureTime);
		tvClientName.setText(item.ClientName);
		tvPhone.setText(item.Phone); // TODO
		// state.setText("状态:" + item.State);
		tvAddress.setText(item.Address);
		tvTotal.setText(item.Total);
		tvSaler.setText(dictionaryHelper.getUserNameById(item.SalesPerson)); // TODO
		tvDesiner.setText(dictionaryHelper.getUserNameById(item.Designer)); // TODO
		// tvState.setText("状态：" + item.State);
		addImageHelper = new AddImageHelper(this, this, llLoAddImage,
				item.Attachment, true);

		registerForContextMenu(tvStage);
	}

	public void setOnClickListener() {
		// 返回上级界面
		ImageView imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel_orderinfo);
		imageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 评论btnDiscuss
		ivDiscuss.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rlPublishDiscuss.setVisibility(View.VISIBLE);
				// 获得焦点
				etDiscussContent.requestFocus();
				// 弹出软键盘
				InputMethodManager imm = (InputMethodManager) ivDiscuss
						.getContext().getSystemService(INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				rlDiscuss.setVisibility(View.GONE); // 评论按钮隐藏
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
								zlServiceHelper.publishDiscuss(itemOrder.Id,
										content, handler);
							} catch (Exception e) {
								Toast.makeText(context, "发表评论异常",
										Toast.LENGTH_SHORT).show();
							}
						}
					}).start();
				} else {
					Toast.makeText(OrderActivity.this, "评论内容不能为空",
							Toast.LENGTH_LONG).show();
				}
				rlPublishDiscuss.setVisibility(View.GONE);
				rlDiscuss.setVisibility(View.VISIBLE);
			}
		});

		ivQuitDiscuss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rlPublishDiscuss.setVisibility(View.GONE);
				rlDiscuss.setVisibility(View.VISIBLE);
			}
		});

		// 测量记录btnTest
		ivTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, MeasureListActivity.class);
				intent.putExtra(MeasureListActivity.TAG, itemOrder.Id);
				// Intent intent = new Intent(context, TestListActivity.class);
				// intent.putExtra(TestListActivity.TAG, item.Id);
				startActivity(intent);
			}
		});

		// 保存图片
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submit();
			}
		});
	}

	/**
	 * 查看更多评论
	 * 
	 * 暂时隐藏
	 */
	private void addHeader() {
		TextView tvHeader = new TextView(context);
		tvHeader.setId(ID_TV_MORE);
		tvHeader.setTextColor(0xFF76A3C1);
		tvHeader.setClickable(true);
		tvHeader.setText("查看更多评论");
		lvDiscuss.addHeaderView(tvHeader);

		tvHeader.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int height = discussListHelper.getHeight();
				LogUtils.i("height", "height:" + height);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, height);
				rlDiscussContent.setLayoutParams(params);
			}
		});
	}

	private void reload() {
		listDiscuss.clear();
		listDiscuss = zlServiceHelper.getOrderContent(itemOrder.OrderNo);
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			// 拍照上传图片控件选择或拍照后显示
			if (requestCode == addImageHelper.CAMERA_TAKE_HELPER
					|| requestCode == addImageHelper.PICKED_PHOTO_WITH_DATA) {
				addImageHelper.refresh(requestCode, data);
			}
		}
	}

	/**
	 * 订单评论填充器
	 * 
	 * @author BOHR
	 * 
	 */
	private class ContentAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return listDiscuss.size();
		}

		@Override
		public 评论 getItem(int position) {
			return listDiscuss.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = View.inflate(OrderActivity.this,
						R.layout.order_content_layout, null);
			}
			TextView tvUserName = (TextView) view
					.findViewById(R.id.tv_userName_orderContent);
			TextView tvContent = (TextView) view
					.findViewById(R.id.tv_content_orderContent);
			评论 item = listDiscuss.get(position);
			tvUserName.setText(item.getUserId() + ":");
			tvContent.setText(item.Content);
			return view;
		}
	}

	private void submit() {
		try {
			// final Dao<任务, Integer> dao = helper.getDao(任务.class);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("确认发布上传图片吗?")
					.setCancelable(false)
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									photoPathList = addImageHelper
											.getPhotoList();
									for (String path : photoPathList) {
										LogUtils.i("attachPath", path);
									}
									if (photoPathList.size() > 0) {
										pBar.setVisibility(View.VISIBLE);
										pBar.setMax(photoPathList.size());
									}
									// dialog.cancel();
									dialog.dismiss();
									new Thread(new Runnable() {
										@Override
										public void run() {
											try {
												zlServiceHelper.UpdateOrder(
														itemOrder,
														photoPathList, pBar,
														handler);
											} catch (Exception e) {
												Toast.makeText(context,
														"发表评论异常",
														Toast.LENGTH_SHORT)
														.show();
											}

										}
									}).start();
								}
							})
					.setNegativeButton("否",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									finish();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
