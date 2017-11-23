package com.cedarhd.control.listview;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

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

/**
 * 简易版ListView帮助类
 * 
 * 没有下拉刷新
 * 
 * @author BOHR
 * 
 */
public class ListViewSimpleHelper {
	/** 当前所在Activity */
	private Context mContext;
	/** 数据加载条件类 */
	public Demand mDemand;
	private String TAG = "ListViewHelper";
	private ListView mListView = null;

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
	private ProgressBar mProgressBar;
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
	public ListViewSimpleHelper(Activity activity, Class entityType,
			Context pContext, Demand demand, ListView listView, List dataList,
			BaseAdapter adapter, ProgressBar progressBar, int itemHeight) {
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
	public static final int LOAD_LOCAL_DATA_FAILED = -1;
	public static final int LOAD_LOCAL_DATA_ISNULL = -3;
	public static final int LOAD_SERVER_DATA_FAILED = -2;

	/** 数据加载Handler */
	Handler loadDataFinishedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {
			case LOAD_LOCAL_DATA_ISNULL:
				// Toast.makeText(mContext, "本地数据为空！",
				// Toast.LENGTH_SHORT).show();
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
					LogUtils.i("keno4", "数据加载到本地成功....View.GONE");
				}
				Toast.makeText(mContext, "加载最新网络数据成功", Toast.LENGTH_SHORT)
						.show();
				// loadMoreButton.setText("查看更多");
				break;
			case LOAD_SERVER_EAREST_DATA_SUCCEEDED:
				// 重新从本地数据库读
				loadLocalData(mDataList.size() + mDemand.每页数量);
				if (mProgressBar != null) {
					mProgressBar.setVisibility(View.GONE);
				}
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
		if (columnName.size() == 0 || columnName.get(0).equals("")) {
			mColumnName = columnName;
			mColumnLikeName = columnLikeName;
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
				values = "%''" + value + "''%";
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
		if (mThreadFetchServerData == null || !mThreadFetchServerData.isAlive()) {

		} else {
			mThreadFetchServerData.interrupt();
		}

		mThreadFetchServerData = new Thread() {
			public void run() {
				try {
					List list = fetchServerData(fetchLatest);
					// 执行完毕后给handler发送一个空消息
					Message msg = new Message();
					msg.what = fetchLatest ? LOAD_SERVER_LATEST_DATA_SUCCEEDED
							: LOAD_SERVER_EAREST_DATA_SUCCEEDED;
					msg.obj = list;
					loadDataFinishedHandler.sendMessage(msg);
					// progressDialog.dismiss();
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
					msg.what = fetchLatest ? LOAD_SERVER_LATEST_DATA_SUCCEEDED
							: LOAD_SERVER_EAREST_DATA_SUCCEEDED;
					msg.obj = list;
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
				LogUtils.i("2keno", "执行：" + entityType.getName());
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
					mDemand.方法名, 0, itemCountPerScreen, 0, mDemand.条件);
		}
		// 如果此Id已经存在，则修改之，否则插入之
		for (Object entity : list) {
			Integer id;
			if (mDemand.表名.equals("部门")) {
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
		System.out.println("===values[0]=" + values[0] == null);
		if (values[0] != null) {
			LogUtils.i("keno2", entityType.getName());
			String clz = entityType.getName();

			if (clz.equals(任务.class.getName())
					|| clz.equals(客户联系记录.class.getName())) {
				list = ServerDataLoader.getServerData(entityType, mDemand.表名,
						mDemand.方法名, 0, itemCountPerScreen, 0,
						fetchLatest ? " 最后处理时间> '" + values[0] + "'"
								: " 最后处理时间< '" + values[0] + "'", value);

				LogUtils.i("2keno", "执行：" + entityType.getName());
			} else if (clz.equals(订单.class.getName())) {
				// 订单 使用咨询时间排序 没有最后更新，所以过滤条件设为空
				list = ServerDataLoader.getServerData(entityType, mDemand.表名,
						mDemand.方法名, 0, itemCountPerScreen, 0, "");
				LogUtils.i("2keno", "执行：" + entityType.getName());
			} else {
				list = ServerDataLoader.getServerData(entityType, mDemand.表名,
						mDemand.方法名, 0, itemCountPerScreen, 0,
						fetchLatest ? " 最后更新> '" + values[0] + "'" : " 最后更新< '"
								+ values[0] + "'", value);
				LogUtils.i("keno2", "执行：" + entityType.getName());
			}
		} else {
			// 本地一条数据都没有，则下载最新的一屏数据
			list = ServerDataLoader.getServerData(entityType, mDemand.表名,
					mDemand.方法名, 0, itemCountPerScreen, 0, "", value);

		}
		System.out.println("===list.方法名=" + list.size());
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

	public void NotifyDataSetChanged() {
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}
}
