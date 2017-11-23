package com.cedarhd;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.cedarhd.adapter.ContactHistoryDetailListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.ListItemContactHistory;
import com.cedarhd.helpers.BaiduLocator;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.联系拜访记录;
import com.cedarhd.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author bohr
 * 
 */
public class ContactHistoryActivity extends BaseActivity implements
		BDLocationListener {

	联系拜访记录 mContactHistory;
	// GetListViewData mGetListViewData;
	List<HashMap<String, Object>> hashMaplst;

	ImageView imageViewCancel;
	ImageView imageViewNew;
	TextView textViewTitle;
	TextView textViewLocation;
	ListView mListView;

	ContactHistoryDetailListViewAdapter contactHistoryDetailListViewAdapter;

	List<HashMap<String, Object>> mTerminalCondition;
	List<HashMap<String, Object>> mContendCondition;
	List<HashMap<String, Object>> mImproveMatters;
	List<HashMap<String, Object>> mCustomerQuestions;
	List<HashMap<String, Object>> mTrainMatters;
	List<HashMap<String, Object>> mOthers;

	ArrayList<ListItemContactHistory> mSectionListItem;

	ProgressBar mProgressBarLoading;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// MyApplication.getInstance().addActivity(this);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.contact_history);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_contact_history_detail);

		Bundle bundle = this.getIntent().getExtras();
		mContactHistory = (联系拜访记录) bundle.getSerializable("ContactHistory");

		findViews();
		setOnClickListener();

		// 获取位置
		try {
			BaiduLocator.requestLocation(getApplicationContext(),
					ContactHistoryActivity.this);
			// progressDialog = ProgressDialog.show(ContactHistoryActivity.this,
			// "请稍后", "定位中。。。");
			// progressDialog.setCancelable(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Init();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (BaiduLocator.startedOrNot()) {
			BaiduLocator.stop();
		}
		super.onDestroy();
	}

	private void Init() {
		// TODO Auto-generated method stub
		Demand demand = new Demand();
		demand.表名 = "联系拜访记录明细";
		demand.方法名 = "联系拜访记录明细_获取";
		demand.条件 = " 联系拜访记录 = " + mContactHistory.getId() + "";// +
																// mContactHistory.get("Id");
		demand.附加条件 = mContactHistory.getId() + "";// mContactHistory.get("Id").toString();
		demand.每页数量 = 10;
		demand.偏移量 = 0;

		// Toast.makeText(this, "客户: " + mContactHistory.get("CustomerName"),
		// Toast.LENGTH_SHORT).show();

		mTerminalCondition = new ArrayList<HashMap<String, Object>>();
		mContendCondition = new ArrayList<HashMap<String, Object>>();
		mImproveMatters = new ArrayList<HashMap<String, Object>>();
		mCustomerQuestions = new ArrayList<HashMap<String, Object>>();
		mTrainMatters = new ArrayList<HashMap<String, Object>>();
		mOthers = new ArrayList<HashMap<String, Object>>();

		// hashMaplst = new ArrayList<HashMap<String, Object>>();
		// mGetListViewData = new GetListViewData(this, demand, hashMaplst,
		// null, null, hanlerFetchDataFinish
		// , mProgressBarLoading);
		// mGetListViewData.InitData();
	}

	private void setOnClickListener() {
		// TODO Auto-generated method stub
		imageViewCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Back();
				finish();
			}
		});

		imageViewNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				New();
			}
		});

		mListView
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						// TODO Auto-generated method stub
						menu.add(0, Menu.FIRST, Menu.NONE, "删除");
					}
				});

		((Button) findViewById(R.id.buttonTakePicture))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent imageCaptureIntent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(new File("/mnt/sdcard/test.jpg")));
						startActivityForResult(imageCaptureIntent,
								RESULT_CAPTURE_IMAGE);
					}
				});
	}

	private final static int RESULT_CAPTURE_IMAGE = 1001;

	// 照相结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case RESULT_CAPTURE_IMAGE:// 拍照
			if (resultCode == RESULT_OK) {
				LogUtils.i("拍照", "照相完成");
			}
			break;
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int position = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
		switch (item.getItemId()) {
		case Menu.FIRST:
			int contactHistoryDetailId = Integer.parseInt(mSectionListItem
					.get(position).mId);
			deleteContactHistoryDetailFromLocal(contactHistoryDetailId);
			deleteContactHistoryDetailFromServer(contactHistoryDetailId);
			mSectionListItem.remove(position);
			contactHistoryDetailListViewAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	private void deleteContactHistoryDetailFromServer(
			final int contactHistoryDetailId) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					ZLServiceHelper dataHelper = new ZLServiceHelper();
					boolean flag = dataHelper
							.deleteContactHistoryDetail(contactHistoryDetailId);
					if (flag) {
						handlerDelete.sendEmptyMessage(2);
					} else {
						handlerDelete.sendEmptyMessage(3);
					}
				} catch (Exception e) {
					Toast.makeText(ContactHistoryActivity.this, "下载数据异常",
							Toast.LENGTH_SHORT).show();
				}
			}

		}).start();
	}

	private void deleteContactHistoryDetailFromLocal(
			final int contactHistoryDetailId) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// SQLiteManagerEX sqlManager = new
				// SQLiteManagerEX(ContactHistoryActivity.this);
				// sqlManager.open();
				// boolean flag =
				// sqlManager.deleteContactHistoryDetail(contactHistoryDetailId);
				// if (flag) {
				// handlerDelete.sendEmptyMessage(0);
				// } else {
				// handlerDelete.sendEmptyMessage(1);
				// }

			}
		}).start();
	}

	private Handler handlerDelete = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			// delete from local success
			case 0:
				Toast.makeText(ContactHistoryActivity.this,
						"该联系拜访记录明细从本地删除成功！", Toast.LENGTH_SHORT).show();
				break;
			// delete from local failed
			case 1:
				Toast.makeText(ContactHistoryActivity.this,
						"该联系拜访记录明细从本地删除失败！", Toast.LENGTH_SHORT).show();
				break;
			// delete from server success
			case 2:
				Toast.makeText(ContactHistoryActivity.this,
						"该联系拜访记录明细从服务器删除成功！", Toast.LENGTH_SHORT).show();
				break;
			// delete from server failed
			case 3:
				Toast.makeText(ContactHistoryActivity.this,
						"该联系拜访记录明细从服务器删除失败！", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

	protected void New() {
		// TODO Auto-generated method stub
		final List<String> mListClassification = new ArrayList<String>();
		mListClassification.add("终端情况");
		mListClassification.add("竞品情况");
		mListClassification.add("改进事项");
		mListClassification.add("客户问题");
		mListClassification.add("培训事项");
		mListClassification.add("其它");

		LayoutInflater inflater = LayoutInflater
				.from(ContactHistoryActivity.this);
		final View view = inflater.inflate(R.layout.choose_customer_dialog,
				null);
		final ListView listViewClassification = (ListView) view
				.findViewById(R.id.listViewCustomer);
		ArrayAdapter adapter = new ArrayAdapter<String>(this,
				R.layout.dialog_listview_textview, mListClassification);
		listViewClassification.setAdapter(adapter);

		final AlertDialog chooseDialog = new AlertDialog.Builder(
				ContactHistoryActivity.this).setTitle("请选择类别：")
				.setIcon(android.R.drawable.ic_dialog_info).setView(view)
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}

				}).show();

		listViewClassification
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						final int classificationId = arg2 + 1;

						Toast.makeText(
								ContactHistoryActivity.this,
								"类别：" + mListClassification.get(arg2) + " Id: "
										+ classificationId, Toast.LENGTH_SHORT)
								.show();

						LayoutInflater inflater = LayoutInflater
								.from(ContactHistoryActivity.this);
						final View view = inflater.inflate(
								R.layout.new_sales_chance_dialog, null);
						final EditText editTextContent = (EditText) view
								.findViewById(R.id.editTextDialogContent);

						new AlertDialog.Builder(ContactHistoryActivity.this)
								.setTitle("请输入内容：")
								.setIcon(android.R.drawable.ic_dialog_info)
								.setView(view)
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												final String editTextContentValue = editTextContent
														.getText().toString();
												final String sId = mContactHistory
														.getId() + "";
												if (!editTextContentValue
														.equals("")) {
													new Thread(new Runnable() {
														@Override
														public void run() {
															try {
																ZLServiceHelper dataHelper = new ZLServiceHelper();
																boolean returnFlag = dataHelper
																		.NewContactHistoryDetail(
																				classificationId,
																				editTextContentValue,
																				Integer.decode(sId));
																if (returnFlag) {
																	handler.sendEmptyMessage(1);
																} else {
																	handler.sendEmptyMessage(0);
																}
															} catch (Exception e) {
																Toast.makeText(
																		ContactHistoryActivity.this,
																		"联系记录异常",
																		Toast.LENGTH_SHORT)
																		.show();
															}
														}

													}).start();

												} else {
													Toast.makeText(
															ContactHistoryActivity.this,
															"内容不能为空！",
															Toast.LENGTH_SHORT)
															.show();
												}
												dialog.dismiss();
											}

										})
								.setNegativeButton("取消",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												dialog.dismiss();
											}

										}).show();

						// new Thread(new Runnable(){
						//
						// @Override
						// public void run() {
						// // TODO Auto-generated method stub
						// boolean returnFlag =
						// helper.NewContactHistory(customerId, -1,
						// Global.ConvertLongDateToString(new Date()));
						// if (returnFlag) {
						// handler.sendEmptyMessage(1);
						// } else {
						// handler.sendEmptyMessage(0);
						// }
						// }
						//
						// }).start();
						chooseDialog.dismiss();
					}
				});
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Toast.makeText(ContactHistoryActivity.this, "新建联系拜访记录明细失败！",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(ContactHistoryActivity.this, "新建联系拜访记录明细成功！",
						Toast.LENGTH_SHORT).show();
				Refresh();
				break;
			default:
				break;
			}
		}

	};

	// void Back(){
	// Intent intent = new Intent();
	// intent.setClass(this.getApplicationContext(),
	// ContactHistoryListActivity.class);
	// startActivity(intent);
	// }

	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	// // 这里重写返回键
	// Intent intent = new Intent(ContactHistoryActivity.this,
	// ContactHistoryListActivity.class);
	// startActivity(intent);
	// return true;
	// }
	// return false;
	// }

	private void findViews() {
		// TODO Auto-generated method stub
		mListView = (ListView) findViewById(R.id.listView1);
		imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		imageViewNew = (ImageView) findViewById(R.id.imageViewNew);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		textViewLocation = (TextView) findViewById(R.id.textViewLocation);
		mProgressBarLoading = (ProgressBar) findViewById(R.id.progressBarLoading);
	}

	private Handler hanlerFetchDataFinish = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			notifyDataSetChanged();
		}
	};

	private void notifyDataSetChanged() {
		mTerminalCondition.clear();
		mContendCondition.clear();
		mImproveMatters.clear();
		mCustomerQuestions.clear();
		mTrainMatters.clear();
		mOthers.clear();

		for (HashMap<String, Object> hashMap : hashMaplst) {
			String classification = hashMap.get("Classification").toString();

			switch (Integer.decode(classification)) {
			case 1:
				mTerminalCondition.add(hashMap);
				break;
			case 2:
				mContendCondition.add(hashMap);
				break;
			case 3:
				mImproveMatters.add(hashMap);
				break;
			case 4:
				mCustomerQuestions.add(hashMap);
				break;
			case 5:
				mTrainMatters.add(hashMap);
				break;
			case 6:
				mOthers.add(hashMap);
				break;
			default:
				break;
			}
		}

		mSectionListItem = collectData();
		contactHistoryDetailListViewAdapter = new ContactHistoryDetailListViewAdapter(
				this, R.layout.contact_history_detail_list_listviewlayout,
				mSectionListItem);
		mListView.setAdapter(contactHistoryDetailListViewAdapter);
	}

	private ArrayList<ListItemContactHistory> collectData() {
		// TODO Auto-generated method stub
		ArrayList<ListItemContactHistory> list = new ArrayList<ListItemContactHistory>();
		for (HashMap<String, Object> map : mTerminalCondition) {
			list.add(new ListItemContactHistory(map.get("Id").toString(), map
					.get("Content").toString(), "终端情况    "));
		}

		for (HashMap<String, Object> map : mContendCondition) {
			list.add(new ListItemContactHistory(map.get("Id").toString(), map
					.get("Content").toString(), "竞品情况    "));
		}

		for (HashMap<String, Object> map : mImproveMatters) {
			list.add(new ListItemContactHistory(map.get("Id").toString(), map
					.get("Content").toString(), "改进事项    "));
		}

		for (HashMap<String, Object> map : mCustomerQuestions) {
			list.add(new ListItemContactHistory(map.get("Id").toString(), map
					.get("Content").toString(), "客户问题    "));
		}

		for (HashMap<String, Object> map : mTrainMatters) {
			list.add(new ListItemContactHistory(map.get("Id").toString(), map
					.get("Content").toString(), "培训事项    "));
		}

		for (HashMap<String, Object> map : mOthers) {
			list.add(new ListItemContactHistory(map.get("Id").toString(), map
					.get("Content").toString(), "其它    "));
		}
		return list;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int item_id = item.getItemId();

		switch (item_id) {
		case R.id.item_Refresh:
			Refresh();
			break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater menuInflater = new MenuInflater(this);
		menuInflater.inflate(R.menu.menu_customerlist, menu);
		return true;
	}

	void Refresh() {

	}

	ProgressDialog progressDialog = null;

	@Override
	public void onReceiveLocation(BDLocation location) {
		// TODO Auto-generated method stub
		if (location == null)
			return;
		String sb = "";
		String address = "";
		sb += "time : ";
		sb += location.getTime();
		sb += "\nerror code : ";
		sb += location.getLocType();
		sb += "\nlatitude : ";
		sb += location.getLatitude();
		sb += "\nlontitude : ";
		sb += location.getLongitude();
		sb += "\nradius : ";
		sb += location.getRadius();
		if (location.getLocType() == BDLocation.TypeGpsLocation) {
			sb += "\nspeed : ";
			sb += location.getSpeed();
			sb += "\nsatellite : ";
			sb += location.getSatelliteNumber();
		} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
			sb += "\naddr : ";
			address = location.getAddrStr();
			sb += address;
		}

		LogUtils.i("BDLocationListener guojianwen", sb.toString());
		// progressDialog.dismiss();
		textViewLocation.setText("位置：" + address);
		textViewLocation.invalidate();

		if (BaiduLocator.startedOrNot()) {
			BaiduLocator.stop();
		}
	}
}
