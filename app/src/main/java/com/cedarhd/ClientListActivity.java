package com.cedarhd;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.adapter.CustomerListViewAdapter;
import com.cedarhd.adapter.ExpandAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperKjx;
import com.cedarhd.control.listview.ListViewHelperKjx.OnLocalDataLoadded;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.helpers.BoeryunTypeMapper;
import com.cedarhd.helpers.DictIosPicker;
import com.cedarhd.helpers.DictIosPicker.OnSelectedListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.listener.OnTouchListener_Search;
import com.cedarhd.listener.TextWatcher_Search;
import com.cedarhd.listener.TextWatcher_Search.TextWatcher_SearchListener;
import com.cedarhd.models.Categray;
import com.cedarhd.models.Client;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.Node;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.客户;
import com.cedarhd.utils.LogUtils;
import com.hanvon.HWCloudManager;
import com.j256.ormlite.dao.Dao;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户列表
 * 
 * 使用了动态字段集合
 * 
 * @author KJX update 2015-03-09
 */
public class ClientListActivity extends BaseActivity {
	private String TAG = "ClientListActivity";
	public static final String SELECT_CLIENT = "SlectClient";
	private final int SlectUserCode = 1;
	public static final String ClientId = "id";
	public static final String ClientName = "clientName";
	private int mClientId; // 客户Id
	private String mUserSelectId = ""; // 业务员id

	private DictIosPicker mIosPicker;

	/** 标识是否选择客户信息 */
	private boolean isSelectClient = false;
	private PullToRefreshListView mListView;
	private Context mContext;
	private EditText mSearchView;
	private DrawerLayout drawerLayout;
	private ListView lvDrawer;
	private ImageView ivCategray;
	private TextView tvTitle;
	private Client item客户;
	List<Client> mList;
	List<Client> mAllList; // 全部客户信息，查询的时候用到
	List<Client> m客户ListFilter;
	private CustomerListViewAdapter mListAdapter;
	private MyProgressBar mProgressBar;
	// private ListViewHelper mListViewHelper = null;
	private ListViewHelperKjx listViewHelperKjx;
	private QueryDemand queryDemand; // 本地查询条件
	private Demand demand;
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	public static boolean isResume; // 是否在Resume中刷新
	private List<Node> allNodes = new ArrayList<Node>();
	private List<Node> showNodes = new ArrayList<Node>();

	public static final int SUCCESS_UPDATE = 101;
	public static final int FAILURE_UPDATE = 102;
	public static final int READ_UPDATE = 3;
	/** 是名片检测线程结束之后发送的消息 */
	public static final int RESULTS_END = 5;
	/** 用于接受返回的字符串信息 */
	public String result;
	private HWCloudManager manager;
	/** 提示框 */
	private ProgressDialog dialog;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case SUCCESS_UPDATE:
				Toast.makeText(mContext, "修改业务员成功", Toast.LENGTH_SHORT).show();
				break;
			case FAILURE_UPDATE:
				Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
				break;
			case READ_UPDATE:
				mListAdapter.notifyDataSetChanged();
				break;
			case RESULTS_END:
				dialog.dismiss();
				/** 应该在这里解析数据 */
				// Toast.makeText(ClientListActivity.this, result,
				// Toast.LENGTH_SHORT).show();
				// Toast.makeText(ClientListActivity.this,
				// getJson(result).toString(), Toast.LENGTH_LONG).show();
				// Intent intent = new Intent(ClientListActivity.this,
				// ClientNewActivity.class);
				// intent.putExtra("client", getJson(result));
				// intent.putExtra("ISFLAG", 909);

				if (TextUtils.isEmpty(result)) {
					showShortToast("扫描名片失败！");
				}

				Intent intent = new Intent(ClientListActivity.this,
						ClientInfoNewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(ClientInfoNewActivity.TAG,
						getJson(result));
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_list_activity);
		manager = new HWCloudManager(ClientListActivity.this,
				"71563764-166f-4fa6-900c-51a30ec6a90a");
		isSelectClient = getIntent().getBooleanExtra(SELECT_CLIENT, false);
		initData();
		findViews();
		setOnClickListener();
		init();
		initDrawer();
		reload();
	}

	private String picPath;
	private File file;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 判断是什么请求
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CAMERA:
				picPath = file.getAbsolutePath();
				dialog = ProgressDialogHelper.show(mContext, "识别中..");
				new Thread(new ScanHW()).start();
				break;
			case PHOTO:
				Uri uri = data.getData();
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(uri, proj, null,
						null, null);
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				picPath = cursor.getString(column_index);
				dialog = ProgressDialog.show(ClientListActivity.this, null,
						"正在检测请稍后...");
				new Thread(new ScanHW()).start();
				break;
			}
		}

		LogUtils.i("keno21", requestCode + "----" + resultCode);
		if (resultCode == RESULT_OK && requestCode == SlectUserCode) {
			// 取出字符串
			Bundle bundle = data.getExtras();
			mUserSelectId = bundle.getString("UserSelectId");
			if (!TextUtils.isEmpty(mUserSelectId)) {
				String str = mUserSelectId.split(";")[0];
				mUserSelectId = str.replace("'", "");
				LogUtils.i("keno21", mUserSelectId);
				int salerId = Integer.parseInt(mUserSelectId);
				item客户.setSalesman(salerId);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 分配业务员
						try {
							zlServiceHelper.updateCustomer(item客户, handler);
						} catch (Exception e) {
							Toast.makeText(mContext, "分配业务员异常",
									Toast.LENGTH_SHORT).show();
						}
					}
				}).start();

			}
		}
	}

	public class ScanHW implements Runnable {

		@Override
		public void run() {
			result = manager.cardLanguage("chns", picPath);
			LogUtils.i("scanResult", result + "");
			handler.sendEmptyMessage(RESULTS_END);
		}
	}

	/**
	 * json解析数据
	 */
	private 客户 getJson(String result) {
		JSONObject object;
		客户 client = null;
		LogUtils.i("out", result);
		if (result != null) {
			client = new 客户();
			try {
				object = new JSONObject(result);
				if (object.get("code").equals("0")) {
					LogUtils.i("out", object.getString("name").length()
							+ "changdu");
					client.联系人 = getStrings(object.getString("name"));
					client.名称 = getStrings(object.getString("name"));
					client.手机 = getStrings(object.getString("mobile"));
					client.地址 = getStrings(object.getString("addr"));
					client.电话 = getStrings(object.getString("tel"));
					client.名称 = getStrings(object.getString("comp"));
					client.QQ = getStrings(object.getString("QQ"));
					client.网址 = getStrings(object.getString("web"));
					client.邮箱 = getStrings(object.getString("email"));
					int userId = Integer.parseInt(Global.mUser.Id);
					client.创建人 = userId;
					client.业务员 = userId;
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		LogUtils.i("out", client.toString());
		return client;
	}

	private String getStrings(String str) {
		String s = null;
		if (str.length() > 4) {
			s = (String) str.subSequence(2, str.length() - 2);
		} else {
			s = "";
		}
		return s;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isResume) {
			reload();
			isResume = false;
		}
	}

	private void init() {

		if (!isSelectClient) {
			try {
				// 先清空本地数据
				Dao<Client, ?> dao = ORMDataHelper.getInstance(mContext)
						.getDao(Client.class);
				int deleteN = dao.deleteBuilder().delete();
				LogUtils.i(TAG, "删除：" + deleteN);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		mListAdapter = new CustomerListViewAdapter(this,
				R.layout.customerlist_listviewlayout, mList);
		mListView.setAdapter(mListAdapter);
		queryDemand = new QueryDemand();
		queryDemand.fildName = "最后更新";
		queryDemand.sortFildName = "UpdateTime";
//		 mListViewHelper = new ListViewHelper(ClientListActivity.this,
//		 客户.class,
//		 this, demand, mListView, mList, mListAdapter, mProgressBar, 80);
		listViewHelperKjx = new ListViewHelperKjx(ClientListActivity.this,
				Client.class, mContext, demand, queryDemand, mListView, mList,
				mListAdapter, mProgressBar);
	}

	private void initData() {
		mContext = ClientListActivity.this;
		mIosPicker = new DictIosPicker(mContext);
		mList = new ArrayList<Client>();
		demand = new Demand();
		demand.表名 = "客户";
//		demand.方法名 = "Customer/GetCustomerList";
		demand.方法名 = "Customer/GetCustomers";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 40;
		demand.偏移量 = 0;
	}

	public void findViews() {
		final Resources res = getResources();
		mSearchView = (EditText) findViewById(R.id.editTextFilter);
		tvTitle = (TextView) findViewById(R.id.tv_title_client_list);
		TextWatcher_Search textWatcher_Search = new TextWatcher_Search(
				mSearchView, res.getDrawable(R.drawable.txt_search_default),
				res.getDrawable(R.drawable.txt_search_clear));
		textWatcher_Search
				.setTextWatcher_SearchListener(new TextWatcher_SearchListener() {
					@Override
					public void onSearch(String str) {
						searchFilter(str);
					}
				});
		mSearchView.addTextChangedListener(textWatcher_Search);
		mSearchView.setOnTouchListener(new OnTouchListener_Search(mSearchView,
				mContext));
		mSearchView.setCompoundDrawablesWithIntrinsicBounds(null, null,
				res.getDrawable(R.drawable.txt_search_default), null);
		ivCategray = (ImageView) findViewById(R.id.iv_categray);
		mListView = (PullToRefreshListView) findViewById(R.id.listView_clientlist);
		mProgressBar = (MyProgressBar) findViewById(R.id.progress_clientlist);
		mProgressBar.setVisibility(View.GONE);
	}

	private void initDrawer() {
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		lvDrawer = (ListView) findViewById(R.id.lv_drawer_client_list);
		// drawerLayout.openDrawer(Gravity.LEFT);
		new CategrayTask().execute("");
		lvDrawer.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				listViewHelperKjx.isSetTitle = true; // 设置顶部标题
				Node itemNode = null;
				if (position < showNodes.size()) {
					itemNode = showNodes.get(position);
				}
				if (itemNode == null) {
					LogUtils.i("Node", position + " Node null！");
					return;
				}
				List<Integer> list = getClientCategray(itemNode.getId());
				List<String> filterList = new ArrayList<String>();
				for (Integer i : list) {
					LogUtils.i("ClientList", "分类：" + i);
					filterList.add(i + "");
				}

				if (itemNode.getName() != null
						&& itemNode.getName().equals("所有")) {
					queryDemand.clearAll();
				} else {
					queryDemand.eqListOrDemand.clear();
					queryDemand.eqListOrDemand
							.put("Classification", filterList);
				}
				// reload();
				mList.clear();
				mListAdapter.setM客户List(mList);
				listViewHelperKjx.loadLocalData();
				final Node selectNode = itemNode;
				listViewHelperKjx.SetOnDataLoad(new OnLocalDataLoadded() {
					@Override
					public void doSomeThing(String result) {
						tvTitle.setText(selectNode.getName() + "(" + result
								+ ")");
					}
				});
				drawerLayout.closeDrawer(Gravity.LEFT);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void searchFilter(String filter) {
		demand.附加条件 = "名称 like '%" + filter + "%'";
		listViewHelperKjx.setmDemand(demand);
		reload();

		// // TODO
		// // mList = mListViewHelper.getAllList();
		// // 查询 从所有数据中查询
		// if (mAllList == null || mAllList.size() == 0) {
		// // mAllList = listViewHelperKjx.getAllList();
		// mAllList = listViewHelperKjx.getAllList_filter();
		// }
		//
		// LogUtils.i(TAG, "mList-->" + mList.size());
		// if (filter.replaceAll(" ", "").length() <= 0) {
		// BaseAdapter simpleAdapter = GetSimpleAdapter(mAllList);
		// mListView.setAdapter(simpleAdapter);
		// } else {
		// m客户ListFilter = new ArrayList<Client>();
		// for (Client bean : mAllList) {
		// String str = bean.CustomerName;
		// String contactsStr = bean.Contacts + "";
		// if (!TextUtils.isEmpty(str)
		// && (str.contains(filter) || contactsStr
		// .contains(filter))) {
		// m客户ListFilter.add(bean);
		// }
		// }
		// BaseAdapter simpleAdapter = GetSimpleAdapter(m客户ListFilter);
		// mListView.setAdapter(simpleAdapter);
		// simpleAdapter.notifyDataSetChanged();
		// if (mProgressBar != null) {
		// mProgressBar.setVisibility(View.GONE);
		// }
		// }
	}

	private CustomerListViewAdapter GetSimpleAdapter(List list) {
		CustomerListViewAdapter mCustomerListViewAdapter = new CustomerListViewAdapter(
				this, R.layout.customerlist_listviewlayout, list);
		return mCustomerListViewAdapter;
	}

	/** 相机 */
	public static final int CAMERA = 101;
	/** 图库 */
	public static final int PHOTO = 102;

	public void setOnClickListener() {
		ImageView ImageViewCancel = (ImageView) findViewById(R.id.imageViewCancel_client);
		ImageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 添加客户按钮
		ImageView ivDone = (ImageView) findViewById(R.id.imageViewNew_client);
		// 添加客户点击事件
		ivDone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mIosPicker.show(R.id.drawer_layout, new String[] { "手动添加",
						"名片扫描" });
				mIosPicker.setOnSelectedListener(new OnSelectedListener() {
					@Override
					public void onSelected(int index) {
						switch (index) {
						case 0:
							Intent intent = new Intent(ClientListActivity.this,
									ClientNewActivity.class);
							startActivity(intent);
							break;
						case 1:
							selectPhotoType();
							break;
						}

					}
				});
			}
		});

		ivCategray.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawerLayout.openDrawer(Gravity.LEFT);
			}
		});

		mListView
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						listViewHelperKjx.mListViewLoadType = ListViewLoadType.顶部视图;
						try {
							// 下拉刷新 导入数据
							listViewHelperKjx.loadServerData(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView) parent;
				final Client item = (Client) listView
						.getItemAtPosition(position);
				if (isSelectClient) { // 选择客户,返回用户id
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putInt(ClientId, item.getId());
					bundle.putString(ClientName, item.getCustomerName());
					bundle.putSerializable(ClientBiz.EXTRA_CLIENT_OBJECT, item);
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					// 点击查看客户详情
					// 客户转化为 中文属性
					客户 client = BoeryunTypeMapper.MapperTo客户(item);
					Intent intent = new Intent(ClientListActivity.this,
							ClientInfoNewActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(ClientInfoNewActivity.TAG, client);
					intent.putExtras(bundle);
					new Thread(new Runnable() {
						@Override
						public void run() {
							LogUtils.i("keno0", item.toString());
							try {
								zlServiceHelper.ReadClient(item, mContext,
										handler);
							} catch (Exception e) {
								// Toast.makeText(context, "查看客户异常",
								// Toast.LENGTH_SHORT).show();
							}
						}
					}).start();
					startActivity(intent);

					readClient(position, item);
				}
			}
		});

		// 选中item长按跳转到选择员工界面
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView) parent;
				item客户 = (Client) listView.getItemAtPosition(position);
				mClientId = item客户.getId(); // 客户Id
				selectSaler();
				return false;
			}
		});
	}

	private void reload() {
		if (!isSelectClient) {
		}
		deleteLocalDb();
		mList.clear();
		mListAdapter.notifyDataSetChanged();
		listViewHelperKjx.loadServerData(true);
	}

	/** 清空数据库 */
	private void deleteLocalDb() {
		try {
			ORMDataHelper.getInstance(mContext).getDao(Client.class)
					.deleteBuilder().delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置客户为已读
	 * 
	 * @param position
	 *            ListView点中项的编号
	 * @param item
	 */
	private void readClient(int position, final Client item) {
		if (!TextUtils.isEmpty(item.ReadTime)) {
			return;
		}
		mListAdapter.getM客户List().get(position - 1).ReadTime = ViewHelper
				.getDateString();
		mListAdapter.notifyDataSetChanged();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					zlServiceHelper.ReadDynamic(item.Id, 7);
				} catch (Exception e) {
					LogUtils.e("erro", "查看客户异常:" + e);
				}
			}
		}).start();
	}

	// 选择员工页面
	private void selectSaler() {
		Intent intent = new Intent(ClientListActivity.this,
				User_SelectActivityNew_zmy.class);
		Bundle bundle = new Bundle();
		bundle.putString("UserSelectId", mUserSelectId);
		intent.putExtras(bundle);
		startActivityForResult(intent, SlectUserCode);
	}

	/**
	 * 获得客户分类,遍历子分类
	 * 
	 * @param node
	 *            当前客户分类节点编号
	 * @return
	 */
	private List<Integer> getClientCategray(int node) {
		List<Integer> list = new ArrayList<Integer>();
		list.add(node);
		List<Client> clientCategray = zlServiceHelper
				.getClientLocalCategray(mContext);
		for (int i = 0; i < clientCategray.size(); i++) {
			Client item = clientCategray.get(i);
			if (node == item.getParentNode()) {
				list.add(item.getClassification());
				traverse(item.getClassification(), list, clientCategray);
			}
		}
		return list;
	}

	/**
	 * 遍历
	 * 
	 * @param list
	 * @param clientCategray
	 */
	private void traverse(int node, List<Integer> list,
			List<Client> clientCategray) {
		for (int i = 0; i < clientCategray.size(); i++) {
			Client item = clientCategray.get(i);
			if (node == item.getParentNode()) {
				list.add(item.getClassification());
				traverse(item.getClassification(), list, clientCategray);
			}
		}
	}

	private class CategrayTask extends AsyncTask<String, Void, List<Client>> {
		@Override
		protected List<Client> doInBackground(String... params) {
			// List<客户> listCategray = zlServiceHelper
			// .getClientLocalCategray(context);
			List<Categray> listCategray = zlServiceHelper
					.getClientCategray(mContext);
			LogUtils.i("size", "listCategray size=" + listCategray.size());
			Categray categray = null;
			Node firstNode = null;
			if (listCategray != null && listCategray.size() > 0) {
				for (int i = 0; i < listCategray.size(); i++) {
					categray = listCategray.get(i);
					Node itemNode = new Node(categray.Id, categray.Name,
							categray.parentId);
					if (categray.Id == 1) {
						firstNode = itemNode;
						firstNode.setLeftPadding(5);
					}
					allNodes.add(itemNode);
				}
			}
			showNodes.add(firstNode);
			setShowList(firstNode);
			initShowList(firstNode);
			return null;
		}

		@Override
		protected void onPostExecute(List<Client> result) {
			super.onPostExecute(result);
			// ClientCategrayAdapter categrayAdapter = new
			// ClientCategrayAdapter(
			// result, context);
			// lvDrawer.setAdapter(categrayAdapter);
			ExpandAdapter expandAdapter = new ExpandAdapter(mContext, null,
					showNodes);
			lvDrawer.setAdapter(expandAdapter);
		}
	}

	// 递归函数
	private void initShowList(Node mNode) {
		if (mNode != null) {
			int leftPadding = mNode.getLeftPadding();
			if (mNode.getChildList() != null && mNode.getChildList().size() > 0) {
				List<Node> childList = mNode.getChildList();
				for (int i = 0; i < childList.size(); i++) {
					Node node = childList.get(i);
					node.setLeftPadding((int) ViewHelper.dip2px(mContext, 20)
							+ leftPadding);
					showNodes.add(node);
					initShowList(node);
				}
			}
		}
	}

	// 递归函数
	private void setShowList(Node mNode) {
		if (mNode != null) {
			// mNode.setLeftPadding(leftPadding);
			int nodeId = mNode.getId(); // 取得当前节点的分类编号
			if (allNodes != null && allNodes.size() > 0) {
				List<Node> childList = new ArrayList<Node>();
				// leftPadding += 20;
				for (int i = 0; i < allNodes.size(); i++) {
					Node itemNode = allNodes.get(i);
					if (itemNode.getParentId() == nodeId) {
						setShowList(itemNode);
						childList.add(itemNode);
						// itemNode.setLeftPadding(leftPadding);
						// showNodes.add(itemNode);
					}
				}
				mNode.setChildList(childList);
			}
		}
	}

	private void selectPhotoType() {
		mIosPicker.show(R.id.drawer_layout, new String[] { "拍照扫描", "从图库中选择" });
		mIosPicker.setOnSelectedListener(new OnSelectedListener() {
			@Override
			public void onSelected(int index) {
				switch (index) {
				case 0:
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					file = new File(FilePathConfig.getCacheDirPath(),
							"temp.jpg");
					Uri uri1 = Uri.fromFile(file);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri1);
					intent.putExtra("return-data", false);
					startActivityForResult(intent, CAMERA);
					break;
				case 1:
					Intent intent2 = new Intent(Intent.ACTION_PICK);
					intent2.setType("image/*");
					startActivityForResult(intent2, PHOTO);
					break;
				}
			}
		});
	}
}
