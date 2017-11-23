package com.cedarhd.control.listview;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ServerDataLoader;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.任务;
import com.cedarhd.models.客户联系记录;
import com.cedarhd.models.订单;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.Where;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ListViewHelper {
	/** 当前所在Activity */
	private Context mContext;
	/** 数据加载条件类 */
	public Demand mDemand;
	private String TAG = "ListViewHelper";
	private PullToRefreshListView mListView = null;

	// public ListViewOnScrollListener mListViewOnScrollListener = null;
	/** 列表数据源 */
	public List mDataList = null;
	private BaseAdapter mAdapter = null;

	// 是否是第一次获取数据
	private Boolean mIsFirstLoadData = true;
	/** 是否在加载数据 */
	public Boolean mIsloading = false;
	/** 数据加载方式 */
	public ListViewLoadType mListViewLoadType = ListViewLoadType.顶部视图;
	private MyProgressBar mProgressBar;
	Button loadMoreButton;
	LinearLayout loadMoreButton_root;
	View loadMoreView;

	private Class entityType = null;
	private int itemCountPerScreen = 1;
	List<String> mColumnName = new ArrayList<String>();// 数据库的列名(相等关系,==)
	List<String> mColumnLikeName = new ArrayList<String>();// 数据库的列名(包含关系,like)
	String mValue = "";// 数据库的列名
	private boolean isloadMoreButton = false;

	/**
	 * ListView帮助类
	 * 
	 * @param activity
	 *            当前activity
	 * @param entityType
	 *            实体类型
	 * @param pContext
	 *            当前上下文
	 * @param demand
	 *            查询条件实体
	 * @param listView
	 *            listView对象
	 * @param dataList
	 *            数据源
	 * @param adapter
	 *            内容适配器
	 * @param progressBar
	 *            进度条
	 * @param itemHeight
	 *            子填充项高度
	 */
	public ListViewHelper(Activity activity, Class entityType,
			Context pContext, Demand demand, PullToRefreshListView listView,
			List dataList, BaseAdapter adapter, MyProgressBar progressBar,
			int itemHeight) {
		this.mContext = pContext;
		this.mDemand = demand;
		this.mListView = listView;
		this.mDataList = dataList;
		this.mAdapter = adapter;
		this.mProgressBar = progressBar;
		this.entityType = entityType;
		WindowManager manage = activity.getWindowManager();
		Display display = manage.getDefaultDisplay();
		int screenHeight = display.getHeight();
		itemCountPerScreen = (screenHeight - 40 - 40) / itemHeight;
		setEmptyView();
		addFootView();
	}

	public ListViewHelper(Activity activity, Class entityType,
			Context pContext, Demand demand, PullToRefreshListView listView,
			List dataList, BaseAdapter adapter) {
		this.mContext = pContext;
		this.mDemand = demand;
		this.mListView = listView;
		this.mDataList = dataList;
		this.mAdapter = adapter;

		// WindowManager manage = activity.getWindowManager();
		// Display display = manage.getDefaultDisplay();
		// int screenHeight = display.getHeight();
		// itemCountPerScreen = (screenHeight - 40 - 40) / itemHeight;
		// boolean isNetAvailable = HttpUtils.IsHaveInternet(mContext);

		// mListViewDataLoader = new ListViewDataLoader(
		// demand.表名, demand.方法名, entityType, 20,
		// activity, mContext,
		// loadDataFinishedHandler, mDemand.偏移量, mDemand.条件);

		// mListViewOnScrollListener = new ListViewOnScrollListener(
		// mContext, mListView, this);
		// mListView.setOnScrollListener(mListViewOnScrollListener);
		setEmptyView();
		addFootView();
	}

	/**
	 * 设置内容为空显示内容
	 */
	public void setEmptyView() {
		TextView emptyView = new TextView(mContext);
		emptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		emptyView.setGravity(Gravity.CENTER);
		emptyView.setTextSize(ViewHelper.dip2px(mContext, 20));
		emptyView.setText("数据为空");
		emptyView.setVisibility(View.GONE);
		((ViewGroup) mListView.getParent()).addView(emptyView);
		mListView.setEmptyView(emptyView);
	}

	public Demand getmDemand() {
		return mDemand;
	}

	public void setmDemand(Demand mDemand) {
		this.mDemand = mDemand;
	}

	public List getDataList() {
		List dataList = new ArrayList();
		for (int i = 0; i < mDataList.size(); i++) {
			dataList.add(mDataList.get(i));
		}
		return dataList;
	}

	private void addFootView() {
		loadMoreView = View.inflate(mContext,
				R.layout.common_listview_loadmore, null);
		loadMoreButton = (Button) loadMoreView
				.findViewById(R.id.loadMoreButton);
		loadMoreButton_root = (LinearLayout) loadMoreView
				.findViewById(R.id.loadMoreButton_root);
		loadMoreButton.setText("查看更多...");
		loadMoreButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 设置按钮文字
				if (mIsloading) {
					return;
				}
				loadMoreButton.setText("正在加载中...");

				if (mColumnName.size() == 0 || mColumnName.get(0).equals("")) {
					if (loadLocalData(mDataList.size() + itemCountPerScreen) <= 0) {
						startFetchServerDataThread(false);
					}
				} else {
					if (loadLocalData(mDataList.size() + itemCountPerScreen,
							mColumnName, mColumnLikeName, mValue) <= 0) {
						startFetchServerDataThread(false);
					}
				}
				// 加一条线程，使isloadMoreButton在一秒内恢复到原始值
				isloadMoreButton = true;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(1000);
							isloadMoreButton = false;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
				// if (loadLocalData(mDataList.size() + itemCountPerScreen) <=
				// 0) {
				// // 如果本地数据库没有了，还要到服务器上去下载
				// // 暂时还是不考虑了吧。一般来说
				// startFetchServerDataThread(false);
				// }
			}
		});
		mListView.addFooterView(loadMoreView);
	}

	public void removeFootView() {
		mListView.removeFooterView(loadMoreView);
	}

	// public void loadLocalData() {
	// int lastShowItem = mListView.getAdapter().getCount() - 2;
	// mListViewDataLoader.mOffset = lastShowItem;// + "";
	// mIsloading = true;
	//
	// try {
	// List list = mListViewDataLoader.loadLocalData();
	// mDataList.clear();
	// mDataList.addAll(list);
	// mAdapter.notifyDataSetChanged();
	// // loadMoreButton.setText("查看更多...");
	// } catch (Exception e) {
	// LogUtils.e("ListViewHelper", "从local加载数据出错：" + e.getMessage());
	// }
	// }

	public void loadServerData(boolean fetchLatest) {
		boolean isNetAvailable = HttpUtils.IsHaveInternet(mContext);
		if (isNetAvailable) {
			if (mProgressBar != null) {
				// 从服务器加载数据时 进度条显示
				mProgressBar.setVisibility(View.VISIBLE);
			}
			startFetchServerDataThread(fetchLatest);
		}
	}

	public void loadServerData(boolean fetchLatest, String value) {
		boolean isNetAvailable = HttpUtils.IsHaveInternet(mContext);
		if (isNetAvailable) {
			if (mProgressBar != null) {
				// 从服务器加载数据时 进度条显示
				mProgressBar.setVisibility(View.VISIBLE);
			}
			startFetchServerDataThread(fetchLatest, value);
		}
	}

	public static final int LOAD_LOCAL_DATA_SUCCEEDED = 1;
	public static final int LOAD_SERVER_LATEST_DATA_SUCCEEDED = 2;
	public static final int LOAD_SERVER_EAREST_DATA_SUCCEEDED = 3;
	public static final int LOAD_SERVER_DATA_ISNULL = 4;
	public static final int LOAD_LOCAL_DATA_FAILED = -1;
	public static final int LOAD_LOCAL_DATA_ISNULL = -3;
	public static final int LOAD_SERVER_DATA_FAILED = -2;

	/** 数据加载Handler */
	Handler loadDataFinishedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			removeFootView();
			switch (msg.what) {
			case LOAD_LOCAL_DATA_ISNULL:
				mDataList.clear();
				mAdapter.notifyDataSetChanged();
				Toast.makeText(mContext, "本地数据为空！", Toast.LENGTH_SHORT).show();
				// mListView.setVisibility(View.VISIBLE);
				// loadMoreButton.setText("查看更多");
				break;
			case LOAD_LOCAL_DATA_FAILED:
				// Toast.makeText(mContext, "加载本地数据失败！", Toast.LENGTH_SHORT)
				// .show();
				// loadMoreButton.setText("查看更多");
				break;
			case LOAD_SERVER_DATA_FAILED:
				// Toast.makeText(mContext, "加载网络数据失败，请检查网络！",
				// Toast.LENGTH_SHORT)
				// .show();
				// loadMoreButton.setText("查看更多");
				break;
			case LOAD_LOCAL_DATA_SUCCEEDED:
				mDataList.clear();
				mDataList.addAll((List) msg.obj);
				mAdapter.notifyDataSetChanged();
				addFootView();
				loadMoreButton.setText("查看更多");
				break;
			case LOAD_SERVER_DATA_ISNULL:
				if (mProgressBar != null) {
					mProgressBar.setVisibility(View.GONE);
				}
				Toast.makeText(mContext, "网络数据为空！", Toast.LENGTH_SHORT).show();
				addFootView();
				loadMoreButton.setText("查看更多");
				break;
			case LOAD_SERVER_LATEST_DATA_SUCCEEDED:
				// 重新从本地数据库读
				// loadLocalData(itemCountPerScreen);
				loadLocalData(mColumnName, mColumnLikeName, mValue);
				// mDataList.addAll(0, (List)msg.obj);
				// mAdapter.notifyDataSetChanged();
				LogUtils.i("keno4", "数据加载到本地成功....");
				if (mProgressBar != null) {
					mProgressBar.setVisibility(View.GONE);
				}
				Toast.makeText(mContext, "加载最新网络数据成功", Toast.LENGTH_SHORT)
						.show();
				loadMoreButton.setText("查看更多");
				break;
			case LOAD_SERVER_EAREST_DATA_SUCCEEDED:
				// 重新从本地数据库读
				loadLocalData(mDataList.size() + mDemand.每页数量);
				// mDataList.addAll((List)msg.obj);
				// mAdapter.notifyDataSetChanged();
				// loadMoreButton.setText("查看更多");
				break;
			default:
				break;
			}
			mIsloading = false;
			// loadMoreButton_root.setVisibility(View.GONE);
			if (mDataList.size() > 0) {
				mListView.setVisibility(View.VISIBLE);
				// loadMoreButton_root.setVisibility(View.VISIBLE);
			}

			if (mListViewLoadType == ListViewLoadType.顶部视图) {
				if (!isloadMoreButton) {
					mListView.onRefreshComplete();
				}
			}
		}
	};

	public int loadLocalData() {
		return loadLocalData(itemCountPerScreen);
	}

	/**
	 * 根据字段的名字和值，查询本地数据库
	 * 
	 * @param columnName
	 *            字段的名字
	 * @param value
	 *            字段的值
	 * @return
	 */
	public int loadLocalData(List<String> columnName,
			List<String> columnLikeName, String value) {
		if ((columnName.size() == 0 || columnName.get(0).equals(""))
				&& (columnLikeName.size() == 0 || columnLikeName.get(0).equals(
						""))) {
			mColumnName = columnName;
			// mColumnLikeName = columnLikeName;
			mValue = value;
			return loadLocalData(itemCountPerScreen);
		} else {
			mColumnName = columnName;
			mColumnLikeName = columnLikeName;
			mValue = value;
			return loadLocalData(itemCountPerScreen, columnName,
					columnLikeName, value);
		}
	}

	public int loadLocalData(long loadCount) {
		try {
			// TODO Auto-generated method stub
			ORMDataHelper helper = ORMDataHelper.getInstance(mContext);
			Dao dao;
			dao = helper.getDao(entityType);
			List list = null;
			LogUtils.i("keno7", entityType.getName());
			if (entityType.getName().equals("客户联系记录")) {
				list = dao.queryBuilder().offset((long) mDemand.偏移量)
						.limit(loadCount).orderBy("最后处理时间", false).query();
			} else {
				list = dao.queryBuilder().offset((long) mDemand.偏移量)
						.limit(loadCount).orderBy("UpdateTime", false).query();
			}
			Message msg = new Message();
			msg.what = LOAD_LOCAL_DATA_SUCCEEDED;
			msg.obj = list;
			loadDataFinishedHandler.sendMessage(msg);
			return list.size();
		} catch (Exception ex) {
			Message msg = new Message();
			msg.what = LOAD_LOCAL_DATA_FAILED;
			msg.obj = ex;
			loadDataFinishedHandler.sendMessage(msg);
		}
		return 0;
	}

	/**
	 * 重载的加载本地数据的方法
	 * 
	 * @param loadCount
	 *            用来确定查询数据库的位置，如查询数据库10~15的数据
	 * @param columnName
	 *            字段的名字，精确查询数据库，如传入Publisher
	 * @param columnLikeName
	 *            字段的名字，相似查询数据库
	 * @param value
	 *            字段的值，一般是员工Id
	 * @return
	 */
	public int loadLocalData(long loadCount, List<String> columnName,
			List<String> columnLikeName, String value) {
		try {
			// TODO Auto-generated method stub
			ORMDataHelper helper = ORMDataHelper.getInstance(mContext);
			Dao dao;
			dao = helper.getDao(entityType);
			List list = new ArrayList();

			Where where;
			if (mDemand.表名.equals("流程")) {
				where = dao.queryBuilder().offset((long) mDemand.偏移量)
						.limit(loadCount).orderBy("CraeteDate", false).where();// like(columnName,
																				// value).query();
			} else if (mDemand.表名.equals("考勤信息")) {
				where = dao.queryBuilder().offset((long) mDemand.偏移量)
						.limit(loadCount).orderBy("AttendanceDate", false)
						.where();// like(columnName, value).query();
			} else {
				where = dao.queryBuilder().offset((long) mDemand.偏移量)
						.limit(loadCount).orderBy("UpdateTime", false).where();// like(columnName,
																				// value).query();
			}

			int columnNo = columnName.size();
			int columnLikeNo = columnLikeName.size();
			for (int i = 0; i < columnNo; i++) {
				if (columnNo == i + 1 && columnLikeNo == 0) {
					where = where.eq(columnName.get(i), value);
				} else {
					where = where.eq(columnName.get(i), value).or();
				}
			}
			String values = "";
			if (columnLikeNo > 0) {
				// values = "%''" + value + "''%";
				values = "%" + value + "%";
			}
			for (int i = 0; i < columnLikeNo; i++) {
				if (columnLikeNo == i + 1) {
					where = where.like(columnLikeName.get(i), values);
				} else {
					where = where.like(columnLikeName.get(i), values).or();
				}
			}

			list = where.query();
			// TODO 打印本地查询的数据
			LogUtils.i(TAG, "===loadLocalDataList=" + list.size());

			Message msg = new Message();
			if (list.size() == 0) {
				msg.what = LOAD_LOCAL_DATA_ISNULL;
			} else {
				msg.what = LOAD_LOCAL_DATA_SUCCEEDED;
			}
			msg.obj = list;
			loadDataFinishedHandler.sendMessage(msg);
			return list.size();
		} catch (Exception ex) {
			Message msg = new Message();
			msg.what = LOAD_LOCAL_DATA_FAILED;
			msg.obj = ex;
			loadDataFinishedHandler.sendMessage(msg);

		}
		return 0;
	}

	private Thread mThreadFetchServerData;

	public void startFetchServerDataThread(final boolean fetchLatest) {
		// // 构建一个下载进度条
		// final ProgressDialog progressDialog = ProgressDialog.show(context,
		// tableName + "加载",
		// "正在加载数据...");
		if (mThreadFetchServerData == null || !mThreadFetchServerData.isAlive()) {

		} else {
			// mThread.destroy();
			mThreadFetchServerData.interrupt();
		}

		mThreadFetchServerData = new Thread() {
			public void run() {
				try {
					List list = fetchServerData(fetchLatest);
					// 执行完毕后给handler发送一个空消息
					Message msg = new Message();
					if (list == null || list.size() == 0) {
						msg.what = LOAD_SERVER_DATA_ISNULL;
					} else {
						msg.what = fetchLatest ? LOAD_SERVER_LATEST_DATA_SUCCEEDED
								: LOAD_SERVER_EAREST_DATA_SUCCEEDED;
						msg.obj = list;
					}
					loadDataFinishedHandler.sendMessage(msg);
				} catch (Exception ex) {
					Message msg = new Message();
					msg.obj = ex;
					LogUtils.e("exception", "" + ex);
					msg.what = LOAD_SERVER_DATA_FAILED;
					loadDataFinishedHandler.sendMessage(msg);
				}
			};
		};
		mThreadFetchServerData.start();
	}

	public void startFetchServerDataThread(final boolean fetchLatest,
			final String value) {
		if (mThreadFetchServerData == null || !mThreadFetchServerData.isAlive()) {
		} else {
			mThreadFetchServerData.interrupt();
		}
		mThreadFetchServerData = new Thread() {
			public void run() {
				try {
					List list = fetchServerData(fetchLatest, value);
					// 执行完毕后给handler发送一个空消息
					Message msg = new Message();
					if (list == null || list.size() == 0) {
						msg.what = LOAD_SERVER_DATA_ISNULL;
					} else {
						msg.what = fetchLatest ? LOAD_SERVER_LATEST_DATA_SUCCEEDED
								: LOAD_SERVER_EAREST_DATA_SUCCEEDED;
						msg.obj = list;
					}
					loadDataFinishedHandler.sendMessage(msg);
					// progressDialog.dismiss();
				} catch (Exception ex) {
					Message msg = new Message();
					msg.obj = ex;
					msg.what = LOAD_SERVER_DATA_FAILED;
					loadDataFinishedHandler.sendMessage(msg);
				}
			};
		};
		mThreadFetchServerData.start();
	}

	/**
	 * 获取服务器最新的一屏数据，更新本地数据（本地没有则插入，本地有则更新） 如果 getlatest =
	 * false，则取比本地最早的updateTime还早的数据中的最新的一屏数据
	 */
	@SuppressWarnings("unchecked")
	private List fetchServerData(boolean fetchLatest) throws ParseException,
			SQLException, IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		ORMDataHelper helper = ORMDataHelper.getInstance(mContext);
		Dao dao = helper.getDao(entityType);
		GenericRawResults result = null;
		if (fetchLatest) {
			result = dao.queryRaw(dao.queryBuilder()
					.selectRaw("max(UpdateTime)").prepareStatementString());
		} else {
			result = dao.queryRaw(dao.queryBuilder()
					.selectRaw("min(UpdateTime)").prepareStatementString());
		}
		String[] values = (String[]) result.getFirstResult();
		List list = null;
		if (values[0] != null) {
			// list = ServerDataLoader.getServerData(entityType, mDemand.表名,
			// mDemand.方法名, 0, itemCountPerScreen, 0,
			// fetchLatest ? " 最后更新> '" + values[0] + "'" : " 最后更新< '"
			// + values[0] + "'");
			LogUtils.i("keno2result", entityType.getName());
			String clz = entityType.getName();
			if (clz.equals(任务.class.getName())
					|| clz.equals(客户联系记录.class.getName())) {
				list = ServerDataLoader.getServerData(entityType, mDemand.表名,
						mDemand.方法名, 0, itemCountPerScreen, 0,
						fetchLatest ? " 最后处理时间> '" + values[0] + "'"
								: " 最后处理时间< '" + values[0] + "'");
				// list = ServerDataLoader.getServerData(entityType, mDemand.表名,
				// mDemand.方法名, 0, itemCountPerScreen, 0, "");
				LogUtils.i("2kenoUpdate", "执行：" + entityType.getName());
			} else if (clz.equals(订单.class.getName())) {
				// 订单 使用咨询时间排序 没有最后更新，所以过滤条件设为空
				list = ServerDataLoader.getServerData(entityType, mDemand.表名,
						mDemand.方法名, 0, itemCountPerScreen, 0, "");
				LogUtils.i("2keno", "执行：" + entityType.getName());
			} else {
				list = ServerDataLoader.getServerData(entityType, mDemand.表名,
						mDemand.方法名, 0, itemCountPerScreen, 0,
						fetchLatest ? " 最后更新> '" + values[0] + "'" : " 最后更新< '"
								+ values[0] + "'");
				LogUtils.i("keno2", "执行：" + entityType.getName());
			}
		} else {
			// 本地一条数据都没有，则下载最新的一屏数据
			list = ServerDataLoader.getServerData(entityType, mDemand.表名,
					mDemand.方法名, 0, itemCountPerScreen, 0, "");
			LogUtils.e(TAG, mDemand.方法名 + "---" + itemCountPerScreen);
		}
		// ArrayList newEntities = new ArrayList();
		// List existedIds = helper.getExistedLocalIds(entityType, list);
		//
		// 如果此Id已经存在，则修改之，否则插入之
		for (Object entity : list) {
			Integer id;
			if (// mDemand.表名.equals("流程")||
				// mDemand.表名.equals("考勤信息")||//表的字段更改了
			mDemand.表名.equals("部门")) {
				id = (Integer) entity.getClass().getMethod("get编号")
						.invoke(entity);
				List existedItem = dao.queryForEq("编号", id);
				if (existedItem != null && existedItem.size() > 0) {
					Object oe = existedItem.get(0);
					int _id = (Integer) oe.getClass().getMethod("get_Id")
							.invoke(oe);
					entity.getClass()
							.getMethod("set_Id", new Class[] { int.class })
							.invoke(entity, _id);
				} else {
					dao.create(entity);
				}
			} else {
				id = (Integer) entity.getClass().getMethod("getId")
						.invoke(entity);
				List existedItem = dao.queryForEq("Id", id);
				if (existedItem != null && existedItem.size() > 0) {
					Object oe = existedItem.get(0);
					int _id = (Integer) oe.getClass().getMethod("get_Id")
							.invoke(oe);
					entity.getClass()
							.getMethod("set_Id", new Class[] { int.class })
							.invoke(entity, _id);
					dao.update(entity);
				} else {
					dao.create(entity);
				}
			}
		}
		return list;
	}

	/**
	 * 获取服务器最新的一屏数据，更新本地数据（本地没有则插入，本地有则更新）
	 * 
	 * 如果 getlatest =false，则取比本地最早的updateTime还早的数据中的最新的一屏数据
	 */
	@SuppressWarnings("unchecked")
	private List fetchServerData(boolean fetchLatest, String value)
			throws ParseException, SQLException, IllegalArgumentException,
			SecurityException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		ORMDataHelper helper = ORMDataHelper.getInstance(mContext);
		Dao dao = helper.getDao(entityType);
		GenericRawResults result = null;
		if (fetchLatest) {
			result = dao.queryRaw(dao.queryBuilder()
					.selectRaw("max(UpdateTime)").prepareStatementString());
		} else {
			result = dao.queryRaw(dao.queryBuilder()
					.selectRaw("min(UpdateTime)").prepareStatementString());
		}
		String[] values = (String[]) result.getFirstResult();

		List list = null;
		LogUtils.i("out", "===values[0]=");
		if (values[0] != null) {
			LogUtils.i("fetchServerData", entityType.getName());
			String clz = entityType.getName();
			LogUtils.i("fetchServerData", "执行：" + entityType.getName());
			if (clz.equals(任务.class.getName())
					|| clz.equals(客户联系记录.class.getName())) {
				// list = ServerDataLoader.getServerData(entityType, mDemand.表名,
				// mDemand.方法名, 0, itemCountPerScreen, 0,
				// fetchLatest ? " 最后处理时间> '" + values[0] + "'"
				// : " 最后处理时间< '" + values[0] + "'", value);

				list = ServerDataLoader.getServerData(entityType, mDemand.表名,
						mDemand.方法名, 0, itemCountPerScreen, 0, "");
			} else if (clz.equals(订单.class.getName())) {
				// 订单 使用咨询时间排序 没有最后更新，所以过滤条件设为空
				list = ServerDataLoader.getServerData(entityType, mDemand.表名,
						mDemand.方法名, 0, itemCountPerScreen, 0, "");
			} else {
				list = ServerDataLoader.getServerData(entityType, mDemand.表名,
						mDemand.方法名, 0, itemCountPerScreen, 0,
						fetchLatest ? " 最后更新> '" + values[0] + "'" : " 最后更新< '"
								+ values[0] + "'", value);
			}
		} else {
			// 本地一条数据都没有，则下载最新的一屏数据
			list = ServerDataLoader.getServerData(entityType, mDemand.表名,
					mDemand.方法名, 0, itemCountPerScreen, 0, "", value);

		}
		LogUtils.i("out", "===list.方法名=" + list.size());
		// ArrayList newEntities = new ArrayList();
		// List existedIds = helper.getExistedLocalIds(entityType, list);
		//
		// 如果此Id已经存在，则修改之，否则插入之
		for (Object entity : list) {
			Integer id;
			if (// mDemand.表名.equals("流程")||
				// mDemand.表名.equals("考勤信息")||//表的字段更改了
			mDemand.表名.equals("部门")) {
				id = (Integer) entity.getClass().getMethod("get编号")
						.invoke(entity);
				List existedItem = dao.queryForEq("编号", id);
				if (existedItem != null && existedItem.size() > 0) {
					Object oe = existedItem.get(0);
					int _id = (Integer) oe.getClass().getMethod("get_Id")
							.invoke(oe);
					entity.getClass()
							.getMethod("set_Id", new Class[] { int.class })
							.invoke(entity, _id);
				} else {
					dao.create(entity);
				}
			} else {
				id = (Integer) entity.getClass().getMethod("getId")
						.invoke(entity);
				List existedItem = dao.queryForEq("Id", id);
				if (existedItem != null && existedItem.size() > 0) {
					Object oe = existedItem.get(0);
					int _id = (Integer) oe.getClass().getMethod("get_Id")
							.invoke(oe);
					entity.getClass()
							.getMethod("set_Id", new Class[] { int.class })
							.invoke(entity, _id);
					dao.update(entity);
				} else {
					dao.create(entity);
				}
			}

			// 如果此Id已经存在，则修改之，否则插入之
			// if (existedIds.contains(id))
			// dao.update(entity);
			// else
			// dao.create(entity);
		}
		return list;
	}

	/**
	 * 获得数据库表的所有数据，用于在查询
	 * 
	 * @return
	 */
	public List getAllList() {
		// TODO Auto-generated method stub
		ORMDataHelper helper = ORMDataHelper.getInstance(mContext);
		Dao dao;
		List list = null;
		try {
			dao = helper.getDao(entityType);
			LogUtils.i("keno7", entityType.getName());
			if (entityType.getName().equals("客户联系记录")) {
				list = dao.queryForAll();
			} else {
				list = dao.queryForAll();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogUtils.i(TAG, "SIZE--" + list.size());
		return list;
	}

	public void NotifyDataSetChanged() {
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}
}
