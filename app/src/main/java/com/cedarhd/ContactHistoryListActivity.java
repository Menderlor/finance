package com.cedarhd;

import com.cedarhd.base.BaseActivity;

@Deprecated
public class ContactHistoryListActivity extends BaseActivity {
	//
	// PullToRefreshListView mListView;
	// // GetListViewData mGetListViewData;
	// BaseAdapter simpleAdapter;
	//
	// // List<HashMap<String, Object>> hashMaplst;
	// List<客户联系记录> m客户联系记录List;
	// ListViewHelper mListViewHelper;
	//
	// boolean mFromSalesChance = false;
	// boolean mFromCustomerDetail = false;
	// String mCustomerNameId;
	// String mSalesChanceId;
	// String mCustomerId;
	//
	// 销售机会 m销售机会;
	//
	// MyProgressBar mProgressBarLoading;
	//
	// /** Called when the activity is first created. */
	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	// setContentView(R.layout.contact_history_list);
	// // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
	// // R.layout.title_contact_history_main);
	//
	// Bundle bundle = this.getIntent().getExtras();
	// if (bundle != null) {
	// // mCustomerNameId = bundle.getString("CustomerId");
	// // if (mCustomerNameId != null) {
	// // mFromCustomerDetail = true;
	// // Toast.makeText(this, "Customer Id" + mCustomerNameId,
	// // Toast.LENGTH_SHORT).show();
	// // } else {
	// // mSalesChanceId = bundle.getString("SalesChanceId");
	// // mFromSalesChance = true;
	// // Toast.makeText(this, "SalesChance Id" + mSalesChanceId,
	// // Toast.LENGTH_SHORT).show();
	// // }
	// m销售机会 = (销售机会) bundle.getSerializable("SalesChance");
	// mFromSalesChance = true;
	// mSalesChanceId = String.valueOf(m销售机会.getId());
	// mCustomerId = String.valueOf(m销售机会.getCustomerId());
	// } else {
	// // MyApplication.getInstance().addActivity(this);
	// }
	//
	// findViews();
	// setOnClickListener();
	// Init();
	//
	// IntentFilter intentFilter = new IntentFilter();
	// intentFilter.addAction(ACTION_NEW_CONTACT_HISTORY);
	// this.registerReceiver(broadcastReceiver, intentFilter);
	// }
	//
	// void Init() {
	// String stringDateBegin = "2013-01-01";
	// String stringDateEnd = "2113-12-31";
	// Demand demand = new Demand();
	// demand.表名 = "客户联系记录";
	// demand.方法名 = "客户联系记录_分页";
	// demand.条件 = " 销售机会 = " + mSalesChanceId;
	// demand.附加条件 = "";
	// demand.每页数量 = 10;
	// demand.偏移量 = 0;
	//
	// m客户联系记录List = new ArrayList<客户联系记录>();
	//
	// simpleAdapter = GetSimpleAdapter(m客户联系记录List);
	// mListView.setAdapter(simpleAdapter);
	//
	// mListViewHelper = new ListViewHelper(this, 客户联系记录.class,
	// ContactHistoryListActivity.this, demand, mListView,
	// m客户联系记录List, simpleAdapter, mProgressBarLoading, 40);
	// mListViewHelper.loadLocalData();
	// }
	//
	// public void setOnClickListener() {
	// // ImageView ImageViewCancel = (ImageView)
	// // findViewById(R.id.imageViewCancel);
	// // ImageViewCancel.setOnClickListener(new View.OnClickListener() {
	// // @Override
	// // public void onClick(View v) {
	// // if (mFromCustomerDetail || mFromSalesChance) {
	// // finish();
	// // } else {
	// // Back();
	// // }
	// // }
	// // });
	// //
	// // ImageView ImageViewNew = (ImageView) findViewById(R.id.imageViewNew);
	// // ImageViewNew.setOnClickListener(new View.OnClickListener() {
	// // @Override
	// // public void onClick(View v) {
	// // New();
	// // }
	// // });
	//
	// // if (mFromCustomerDetail || mFromSalesChance) {
	// // ImageViewNew.setVisibility(View.INVISIBLE);
	// // }
	//
	// mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	// @SuppressWarnings("unchecked")
	// @Override
	// public void onItemClick(AdapterView<?> parent, View view,
	// int position, long id) {
	//
	// ListView listView = (ListView) parent;
	// // HashMap<String, Object> map = (HashMap<String, Object>)
	// // listView
	// // .getItemAtPosition(position);
	// 客户联系记录 item = (客户联系记录) listView.getItemAtPosition(position);
	//
	// Intent intent = new Intent(ContactHistoryListActivity.this,
	// ContactHistoryActivity.class);
	// Bundle bundle = new Bundle();
	// bundle.putSerializable("ContactHistory", item);
	// intent.putExtras(bundle);
	//
	// startActivity(intent);
	//
	// }
	// });
	//
	// mListView
	// .setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
	//
	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View v,
	// ContextMenuInfo menuInfo) {
	// // TODO Auto-generated method stub
	// menu.add(0, Menu.FIRST, Menu.NONE, "删除");
	// }
	// });
	// }
	//
	// @Override
	// public boolean onContextItemSelected(MenuItem item) {
	// // TODO Auto-generated method stub
	// int position = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
	// switch (item.getItemId()) {
	// case Menu.FIRST:
	// int contactHistoryId = m客户联系记录List.get(position - 1).getId();
	//
	// deleteContactHistoryFromLocal(contactHistoryId);
	// deleteContactHistoryFromServer(contactHistoryId);
	// m客户联系记录List.remove(position - 1);
	// simpleAdapter.notifyDataSetChanged();
	// break;
	// default:
	// break;
	// }
	// return super.onContextItemSelected(item);
	// }
	//
	// private void deleteContactHistoryFromLocal(final int contactHistoryId) {
	// // TODO Auto-generated method stub
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	//
	// // sqlManager.open();
	// // boolean flag = sqlManager
	// // .deleteContactHistory(contactHistoryId);
	// // if (flag) {
	// // handlerDelete.sendEmptyMessage(0);
	// // } else {
	// // handlerDelete.sendEmptyMessage(1);
	// // }
	//
	// }
	// }).start();
	// }
	//
	// private void deleteContactHistoryFromServer(final int contactHistoryId) {
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// try {
	// ZLServiceHelper dataHelper = new ZLServiceHelper();
	// boolean flag = dataHelper
	// .deleteContactHistory(contactHistoryId);
	// if (flag) {
	// handlerDelete.sendEmptyMessage(2);
	// } else {
	// handlerDelete.sendEmptyMessage(3);
	// }
	// } catch (Exception e) {
	// Toast.makeText(ContactHistoryListActivity.this, "删除联系记录异常",
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	//
	// }).start();
	// }
	//
	// private Handler handlerDelete = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// // TODO Auto-generated method stub
	// super.handleMessage(msg);
	// switch (msg.what) {
	// // delete from local success
	// case 0:
	// Toast.makeText(ContactHistoryListActivity.this,
	// "该客户联系记录从本地删除成功！", Toast.LENGTH_SHORT).show();
	// break;
	// // delete from local failed
	// case 1:
	// Toast.makeText(ContactHistoryListActivity.this,
	// "该客户联系记录从本地删除失败！", Toast.LENGTH_SHORT).show();
	// break;
	// // delete from server success
	// case 2:
	// Toast.makeText(ContactHistoryListActivity.this,
	// "该客户联系记录从服务器删除成功！", Toast.LENGTH_SHORT).show();
	// break;
	// // delete from server failed
	// case 3:
	// Toast.makeText(ContactHistoryListActivity.this,
	// "该客户联系记录从服务器删除失败！", Toast.LENGTH_SHORT).show();
	// break;
	// default:
	// break;
	// }
	// }
	// };
	//
	// public void findViews() {
	// // mbuttonNew = (Button) findViewById(R.id.buttonNew);
	// mListView = (PullToRefreshListView)
	// findViewById(R.id.listViewContactHistoryList);
	// mProgressBarLoading = (MyProgressBar)
	// findViewById(R.id.progressBarLoading);
	// }
	//
	// // SimpleAdapter GetSimpleAdapter(List<HashMap<String, Object>>
	// hashMaplst)
	// // {
	// // SimpleAdapter simpleAdapter = new SimpleAdapter(this, hashMaplst,
	// // R.layout.noticelist_listviewlayout, new String[] { "Title",
	// // "PublisherName", "ReleaseTime", "Content_Jabridged" },
	// // new int[] { R.id.textViewTitle, R.id.textViewPublisherName,
	// // R.id.textViewTime, R.id.textViewContent });
	// // return simpleAdapter;
	// // }
	//
	// BaseAdapter GetSimpleAdapter(List<客户联系记录> list) {
	// // ContactHistoryListViewAdapter mContactHistoryListViewAdapter = new
	// // ContactHistoryListViewAdapter(
	// // ContactHistoryListActivity.this,
	// // R.layout.contacthistorylist_listviewlayout, list, null);
	// return mContactHistoryListViewAdapter;
	// }
	//
	// ProgressDialog progressDialog = null;
	// final static int CREATE_CONTACTHISTORY_SUCCEEDED = 1;
	// final static int CREATE_CONTACTHISTORY_FAILED = 2;
	//
	// public void onCustomerSelected(final int customerId) {
	// progressDialog = ProgressDialog.show(ContactHistoryListActivity.this,
	// "请稍后", "签到中。。。");
	// progressDialog.setCancelable(true);
	//
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// try {
	// ZLServiceHelper dataHelper = new ZLServiceHelper();
	// // TODO Auto-generated method stub
	// boolean returnFlag = dataHelper.NewContactHistory(
	// customerId, -1,
	// DateTimeUtil.ConvertLongDateToString(new Date()));
	// if (returnFlag) {
	// handler.sendEmptyMessage(CREATE_CONTACTHISTORY_SUCCEEDED);
	// } else {
	// handler.sendEmptyMessage(CREATE_CONTACTHISTORY_FAILED);
	// }
	// } catch (Exception e) {
	// Toast.makeText(ContactHistoryListActivity.this, "新建联系记录异常",
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	//
	// }).start();
	// }
	//
	// public void newContactHistory() {
	// // AlertDialogSelectCustomer dlg = new AlertDialogSelectCustomer(this,
	// // ContactHistoryListActivity.this,
	// // this);
	// // dlg.show();
	//
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// try {
	// ZLServiceHelper dataHelper = new ZLServiceHelper();
	// boolean returnFlag = dataHelper.NewContactHistory(
	// Integer.parseInt(mCustomerId),
	// Integer.parseInt(mSalesChanceId),
	// DateTimeUtil.ConvertLongDateToString(new Date()));
	// if (returnFlag) {
	// handler.sendEmptyMessage(CREATE_CONTACTHISTORY_SUCCEEDED);
	// } else {
	// handler.sendEmptyMessage(CREATE_CONTACTHISTORY_FAILED);
	// }
	// } catch (Exception e) {
	// Toast.makeText(ContactHistoryListActivity.this, "新建联系记录异常",
	// Toast.LENGTH_SHORT);
	// }
	// }
	//
	// }).start();
	//
	// // Intent intent = new Intent(ContactHistoryListActivity.this,
	// // ContactHistoryActivity.class);
	// // Bundle bundle = new Bundle();
	// // bundle.putSerializable("ContactHistory", null);
	// // intent.putExtras(bundle);
	// //
	// // startActivity(intent);
	//
	// // final List<SimpleCustomer> mListSimpleCustomer;
	// // final List<String> mListCustomer;
	// // final DataHelper helper = new DataHelper();
	// // mListSimpleCustomer = helper.getCustomers();
	// // mListCustomer = new ArrayList<String>();
	// // for (SimpleCustomer customer:mListSimpleCustomer) {
	// // mListCustomer.add(customer.CustomerName);
	// // }
	// //
	// // LayoutInflater inflater =
	// // LayoutInflater.from(ContactHistoryListActivity.this);
	// // final View view = inflater.inflate(R.layout.choose_customer_dialog,
	// // null);
	// // final ListView listViewCustomer = (ListView)
	// // view.findViewById(R.id.listViewCustomer);
	// // ArrayAdapter adapter = new ArrayAdapter<String>(this,
	// // R.layout.dialog_listview_textview, mListCustomer);
	// // listViewCustomer.setAdapter(adapter);
	// //
	// // final AlertDialog chooseDialog = new
	// // AlertDialog.Builder(ContactHistoryListActivity.this)
	// // .setTitle("请选择客户：")
	// // .setIcon(android.R.drawable.ic_dialog_info)
	// // .setView(view)
	// // .setNegativeButton("取消", new DialogInterface.OnClickListener(){
	// //
	// // @Override
	// // public void onClick(DialogInterface dialog, int which) {
	// // // TODO Auto-generated method stub
	// // dialog.dismiss();
	// // }
	// //
	// // })
	// // .show();
	// //
	// // listViewCustomer.setOnItemClickListener(new OnItemClickListener() {
	// //
	// // @Override
	// // public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
	// // long arg3) {
	// // // TODO Auto-generated method stub
	// // final int customerId = mListSimpleCustomer.get(arg2).Id;
	// //
	// // Toast.makeText(ContactHistoryListActivity.this, "客户：" +
	// // mListSimpleCustomer.get(arg2).CustomerName + " Id: "
	// // + mListSimpleCustomer.get(arg2).Id, Toast.LENGTH_SHORT).show();
	// // new Thread(new Runnable(){
	// //
	// // @Override
	// // public void run() {
	// // // TODO Auto-generated method stub
	// // boolean returnFlag = helper.NewContactHistory(customerId, -1,
	// // Global.ConvertLongDateToString(new Date()));
	// // if (returnFlag) {
	// // handlerDelete.sendEmptyMessage(1);
	// // } else {
	// // handlerDelete.sendEmptyMessage(0);
	// // }
	// // }
	// //
	// // }).start();
	// // chooseDialog.dismiss();
	// // }
	// // });
	// }
	//
	// public static String ACTION_NEW_CONTACT_HISTORY =
	// "com.cedarhd.action.new.contact_history";
	//
	// BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// // TODO Auto-generated method stub
	// String action = intent.getAction();
	//
	// if (action.equals(ACTION_NEW_CONTACT_HISTORY)) {
	// LogUtils.e("guojianwen", "Add new ContactHistory");
	// newContactHistory();
	//
	// }
	// }
	// };
	//
	// @Override
	// protected void onDestroy() {
	// // TODO Auto-generated method stub
	// this.unregisterReceiver(broadcastReceiver);
	// super.onDestroy();
	// }
	//
	// @Override
	// protected void onResume() {
	// // TODO Auto-generated method stub
	// super.onResume();
	// }
	//
	// Handler handler = new Handler() {
	//
	// @Override
	// public void handleMessage(Message msg) {
	// // TODO Auto-generated method stub
	// super.handleMessage(msg);
	// switch (msg.what) {
	// case CREATE_CONTACTHISTORY_SUCCEEDED:
	// Toast.makeText(ContactHistoryListActivity.this, "新建客户联系记录成功！",
	// Toast.LENGTH_SHORT).show();
	// // if(progressDialog!=null) progressDialog.dismiss();
	// break;
	// case CREATE_CONTACTHISTORY_FAILED:
	// Toast.makeText(ContactHistoryListActivity.this, "新建客户联系记录失败！",
	// Toast.LENGTH_SHORT).show();
	// // if(progressDialog!=null) progressDialog.dismiss();
	// break;
	// default:
	// break;
	// }
	// // finish();
	// // Refresh();
	// reLoad();
	// }
	// };
	//
	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// // TODO Auto-generated method stub
	// return super.onPrepareOptionsMenu(menu);
	// }
	//
	// protected void reLoad() {
	// // TODO Auto-generated method stub
	// m客户联系记录List.clear();
	// simpleAdapter.notifyDataSetChanged();
	// mListViewHelper.loadLocalData();
	// }
	//
	// void Search() {
	//
	// }
	//
	// // @Override
	// // public boolean onOptionsItemSelected(MenuItem item) {
	// // // TODO Auto-generated method stub
	// // int item_id = item.getItemId();
	// //
	// // switch (item_id) {
	// // case R.id.item_Refresh:
	// // // Refresh();
	// // break;
	// // default:
	// // return false;
	// // }
	// // return true;
	// // }
	// //
	// // @Override
	// // public boolean onCreateOptionsMenu(Menu menu) {
	// // // TODO Auto-generated method stub
	// // if (!(mFromCustomerDetail || mFromSalesChance)) {
	// // MenuInflater menuInflater = new MenuInflater(this);
	// // menuInflater.inflate(R.menu.menu_customerlist, menu);
	// // }
	// //
	// // return true;
	// // }
	//
	// // void Refresh() {
	// // mGetListViewData.mLoadDataType = LoadDataType.服务器;
	// // // mListViewOnScrollListener.InitData();
	// // mGetListViewData.GetNewData();
	// // }
	//
	// void Back() {
	// // Intent intent = new Intent();
	// // intent.setClass(this.getApplicationContext(),
	// // SalesManagementListActivity.class);
	// // startActivity(intent);
	// finish();
	// }
	//
	// // public boolean onKeyDown(int keyCode, KeyEvent event) {
	// // if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	// // // 这里重写返回键
	// // if (mFromCustomerDetail || mFromSalesChance) {
	// // finish();
	// // } else {
	// // Intent intent = new Intent(ContactHistoryListActivity.this,
	// // SalesManagementListActivity.class);
	// // startActivity(intent);
	// // finish();
	// // }
	// // return true;
	// // }
	// // return false;
	// // }
	//
	// private Handler hanlerFetchDataFinish = new Handler() {
	//
	// @Override
	// public void handleMessage(Message msg) {
	// // TODO Auto-generated method stub
	// super.handleMessage(msg);
	// notifyDataSetChanged();
	// }
	//
	// };
	//
	// private void notifyDataSetChanged() {
	// mListView.setAdapter(simpleAdapter);
	// }
}
