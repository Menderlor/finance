package com.cedarhd.control.listview;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ServerDataLoader;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 测试中使用
 * 
 * @author BOHR
 * 
 */
public class ListViewHelperKjx {
	/** 当前所在Activity */
	private Context mContext;
	/** 数据加载条件类 */
	public Demand mDemand;
	public QueryDemand queryDemand; // 本地数据查询条件
	private String TAG = "ListViewHelperKjx"; // 标识
	private PullToRefreshListView mListView = null; // 下拉刷新的ListView
	private boolean isFirst = true;

	/** 列表数据源 */
	public List mDataList = null;

	private BaseAdapter mAdapter = null; // 内容适配器
	/** 是否在加载数据 */
	public Boolean mIsloading = false;
	/** 数据加载方式 默认加载顶部 */
	public ListViewLoadType mListViewLoadType = ListViewLoadType.顶部视图;
	private MyProgressBar mProgressBar;
	private LinearLayout mRefreshView;

	// 客户列表显示分类
	public boolean isSetTitle = false;// 是否设置标题

	// 查看更多按钮
	private Button loadMoreButton;
	private LinearLayout loadMoreButton_root;
	private View loadMoreView;

	private Class entityType = null;
	private int itemCountPerScreen = 20; // 每屏数量,默认20条数据
	private int offsets = 0; // 偏移量
	private boolean isloadMoreButton = false; // 是否点击加载更多

	/**
	 * 用于取最值的的字段名
	 */
	private String sortFildName;

	/**
	 * 本地数据库用于排序字段名
	 */
	private String localFildName;

	public static final int LOAD_LOCAL_DATA_SUCCEEDED = 1; // 加载本地数据成功
	public static final int LOAD_SERVER_LATEST_DATA_SUCCEEDED = 2; // 加载服务器最新数据成功
	public static final int LOAD_SERVER_EAREST_DATA_SUCCEEDED = 3; // 加载服务器最久远的数据成功
	public static final int LOAD_LOCAL_DATA_FAILED = 4; // 加载本地数据失败
	public static final int LOAD_LOCAL_DATA_ISNULL = 5; // 本地数据为空
	public static final int LOAD_SERVER_DATA_FAILED = 6; // 加载服务器数据失败
	public static final int LOAD_SERVER_DATA_ISNULL = 7; // 加载服务器数据失败

	/** 数据加载Handler */
	private Handler loadDataFinishedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOAD_LOCAL_DATA_ISNULL:
				if (mListViewLoadType == ListViewLoadType.顶部视图) {
					// TODO 顶部视图：默认加载和下拉刷新两种方式，先访问服务器，获得数据插入数据库后，在本地显示
					// 如果本地也没有数据，则没有
					// if (!TextUtils.isEmpty(mDemand.用户编号)||queryDemand.) {
					//
					// }
					Toast.makeText(mContext, "数据为空", Toast.LENGTH_SHORT).show();
					mDataList.clear();
					NotifyDataSetChanged();
				} else if (mListViewLoadType == ListViewLoadType.底部视图) {
					boolean isConnectedInternet = HttpUtils
							.IsHaveInternet(mContext);
					if (!isConnectedInternet) {
						Toast.makeText(mContext, "需要连接移动网络或wifi才能获取最新信息！",
								Toast.LENGTH_SHORT).show();
						loadMoreButton.setText("查看更多");
					} else {
						if (isFirst) {
							isFirst = false;
							// 如果是点击加载更多，而本地数据为空，说明本地数据已经全部显示在屏幕上
							// 开启网络获得之前的数据
							loadServerData(false);
							// Toast.makeText(mContext, "本地数据为空,开启网络获取数据！",
							// Toast.LENGTH_SHORT).show();
							LogUtils.i(TAG, "底部视图-----LOAD_LOCAL_DATA_ISNULL");
						} else {
							loadMoreButton.setText("查看更多");
						}
					}

				}
				break;
			case LOAD_LOCAL_DATA_FAILED:
				LogUtils.i(TAG, "LOAD_LOCAL_DATA_FAILED....");
				break;
			case LOAD_SERVER_DATA_FAILED:
				Toast.makeText(mContext, "加载网络数据失败，请检查网络！", Toast.LENGTH_SHORT)
						.show();
				dismissProgressBar();
				break;
			case LOAD_SERVER_DATA_ISNULL:
				LogUtils.i(TAG, "LOAD_SERVER_DATA_ISNULL");
				dismissProgressBar();
				if (mListViewLoadType == ListViewLoadType.顶部视图) {
					if (mDataList == null || mDataList.size() == 0) {
						// 如果服务器数据为空，加载本地
						loadLocalData(itemCountPerScreen);
					} else {
						Toast.makeText(mContext, "已经加载了最新数据",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					// 底部加载数据为空,已经没有最新数据，隐藏查看更多按钮
					Toast.makeText(mContext, "已经加载了所有数据", Toast.LENGTH_SHORT)
							.show();
					loadMoreButton.setText("查看更多");
					// removeFootView();
				}
				break;
			case LOAD_LOCAL_DATA_SUCCEEDED: // 加载本地数据成功
				mDataList.clear();
				mDataList.addAll((List) msg.obj);
				mAdapter.notifyDataSetChanged();
				loadMoreButton.setText("查看更多");

				break;
			case LOAD_SERVER_LATEST_DATA_SUCCEEDED: // 加载最新数据成功
				dismissProgressBar();
				// 重新从本地数据库读
				loadLocalData(itemCountPerScreen);
				LogUtils.i(TAG, "LOAD_SERVER_LATEST_DATA_SUCCEEDED....");
				dismissProgressBar();
				Toast.makeText(mContext, "加载最新网络数据成功", Toast.LENGTH_SHORT)
						.show();
				// loadMoreButton.setText("查看更多");
				break;
			case LOAD_SERVER_EAREST_DATA_SUCCEEDED:
				dismissProgressBar();
				// 重新从本地数据库读,加载更早之前的数据的数据 当前的（mDataList）数量 加上新加载出来的数量
				List msgList = (List) msg.obj;
				LogUtils.i(TAG, "size()=" + msgList.size());
				loadLocalData(mDataList.size() + msgList.size());
				break;
			default:
				break;
			}
			mIsloading = false;
			loadMoreButton_root.setVisibility(View.GONE);
			if (mDataList.size() > 0) {
				mListView.setVisibility(View.VISIBLE);
				loadMoreButton_root.setVisibility(View.VISIBLE);
			}
			if (mListViewLoadType == ListViewLoadType.顶部视图) {
				mListView.onRefreshComplete();
			}
			dismissProgressBar();
		}

		/**
		 * 隐藏进度条
		 */
		private void dismissProgressBar() {
			if (mProgressBar != null) {
				mProgressBar.setVisibility(View.GONE);
			}
		}
	};

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
	 *            网络查询条件实体
	 * @param queryDemand
	 *            本地数据库查询条件
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
	 * @since 2014-07-19
	 */
	public ListViewHelperKjx(Activity activity, Class entityType,
			Context pContext, Demand demand, QueryDemand queryDemand,
			PullToRefreshListView listView, List dataList, BaseAdapter adapter,
			MyProgressBar progressBar) {
		this.mContext = pContext;
		this.mDemand = demand;
		this.mListView = listView;
		this.mDataList = dataList;
		this.mAdapter = adapter;
		this.mProgressBar = progressBar;
		this.entityType = entityType;
		this.queryDemand = queryDemand;

		if (!TextUtils.isEmpty(queryDemand.sortFildName)) {
			sortFildName = queryDemand.sortFildName;
		} else {
			sortFildName = "UpdateTime";
		}

		if (!TextUtils.isEmpty(queryDemand.localFildName)) {
			localFildName = queryDemand.localFildName;
		} else {
			localFildName = sortFildName;
		}

		addFootView();
	}

	/**
	 * 设置查询条件
	 * 
	 * @param mDemand
	 */
	public void setmDemand(Demand mDemand) {
		this.mDemand = mDemand;
	}

	/**
	 * 设置查询条件
	 * 
	 * @param mDemand
	 */
	public void setQueryDemand(QueryDemand queryDemand) {
		this.queryDemand = queryDemand;
	}

	/**
	 * 重置查询条件
	 * 
	 * @param demand
	 * @param queryDemand
	 */
	private void setDemand(Demand demand, QueryDemand queryDemand) {
		this.mDemand = demand;
		this.queryDemand = queryDemand;
	}

	/**
	 * 增加底部按钮
	 */
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
				isFirst = true; // 标志位：防止重复刷新
				// 设置按钮文字
				if (mIsloading) {
					// 如果正在加载数据 则不做处理
					return;
				}
				loadMoreButton.setText("正在加载中...");
				// 加一条线程，使isloadMoreButton在一秒内恢复到原始值
				mListViewLoadType = ListViewLoadType.底部视图;
				isloadMoreButton = true;
				// 设置偏移量,偏移量参数为当前显示的总条数
				offsets = mDataList.size();
				LogUtils.i("listviewH", "设置偏移量--" + offsets);
				loadLocalData(mDataList.size() + itemCountPerScreen);
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
			}
		});
		mListView.addFooterView(loadMoreView);
	}

	public void removeFootView() {
		// mListView.removeFooterView(loadMoreView);
		loadMoreButton.setVisibility(View.GONE);
		loadMoreButton_root.setVisibility(View.GONE);
	}

	/**
	 * 从服务器加载网络数据
	 * 
	 * @param fetchLatest
	 *            是否下载最新数据
	 */
	public void loadServerData(boolean fetchLatest) {
		boolean isNetAvailable = HttpUtils.IsHaveInternet(mContext);
		if (isNetAvailable) {
			if (mProgressBar != null) {
				// 从服务器加载数据时 进度条显示
				mProgressBar.setVisibility(View.VISIBLE);
			}
			// 每次访问网络时都判断一下 是获取最新数据 还是之前的数据（判断是顶部下拉 还是底部）
			if (fetchLatest) {
				mListViewLoadType = ListViewLoadType.顶部视图;
			} else {
				mListViewLoadType = ListViewLoadType.底部视图;
			}
			LogUtils.i(TAG,
					"用户编号=" + mDemand.用户编号 + ",list长度=" + mDataList.size());
			LogUtils.i(TAG, "queryDemand=" + queryDemand.likeDemand.size());
			startFetchServerDataThread(fetchLatest);
		}
	}

	public void loadLocalData() {
		loadLocalData(itemCountPerScreen);
	}

	/**
	 * 加载本地数据
	 * 
	 * @param loadCount
	 *            加载数量
	 */
	private void loadLocalData(final long loadCount) {
		// TODO Auto-generated method stub
		final ORMDataHelper helper = ORMDataHelper.getInstance(mContext);
		LogUtils.i("keno7", entityType.getName());
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<String> list = new ArrayList<String>();
				try {
					Dao dao = helper.getDao(entityType);
					QueryBuilder builder = dao.queryBuilder();
					addLocalFilter(builder);
					list = builder.offset((long) 0).limit(loadCount)
							.orderBy(localFildName, false).query();
					LogUtils.i(TAG, "---offsets:" + offsets + "------size="
							+ list.size());
					Message msg = new Message();
					if (list != null && list.size() != 0) {
						if (mListViewLoadType == ListViewLoadType.底部视图) {
							if (list.size() - mDataList.size() <= 0) {
								// 如果再次查询的个数长度和当前显示的个数相同，则本地数据已经全部加载了
								msg.what = LOAD_LOCAL_DATA_ISNULL;
								// LOAD_SERVER_DATA_ISNULL
							} else {
								msg.what = LOAD_LOCAL_DATA_SUCCEEDED;
								msg.obj = list;
							}
						} else {
							msg.what = LOAD_LOCAL_DATA_SUCCEEDED;
							msg.obj = list;
						}
					} else {
						// 如果再次查询的个数长度和当前显示的个数相同，则本地数据已经全部加载了
						msg.what = LOAD_LOCAL_DATA_ISNULL;
					}
					loadDataFinishedHandler.sendMessage(msg);
				} catch (Exception ex) {
					Message msg = new Message();
					msg.what = LOAD_LOCAL_DATA_FAILED;
					msg.obj = ex;
					loadDataFinishedHandler.sendMessage(msg);
				} finally {
					// helper.close();
				}
			}
		}).start();
	}

	private Thread mThreadFetchServerData;

	/**
	 * 开始网络获取数据
	 * 
	 * @param fetchLatest
	 *            如果getlatest=false，则取比本地最早的updateTime之前的数据中的最新的一屏数据 ，
	 *            fetchLatest =true 对应下拉刷新，获取最新数据
	 */
	public void startFetchServerDataThread(final boolean fetchLatest) {
		// 判断线程是否重复开启
		if (mThreadFetchServerData == null || !mThreadFetchServerData.isAlive()) {
		} else {
			mThreadFetchServerData.interrupt();
		}

		mThreadFetchServerData = new Thread() {
			public void run() {
				try {
					// 获得网络上最新下载的数据
					List list = fetchServerData(fetchLatest);
					// 执行完毕后给handler发送一个空消息
					LogUtils.i(TAG, "size()=" + list.size());
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
					msg.what = LOAD_SERVER_DATA_FAILED;
					loadDataFinishedHandler.sendMessage(msg);
					LogUtils.i("erroListViewHelper", "" + ex);
				}
			};
		};
		mThreadFetchServerData.start();
	}

	/**
	 * 获取服务器最新的一屏数据，更新本地数据（本地没有则插入，本地有则更新）
	 * 
	 * @param fetchLatest
	 *            如果getlatest=false，则取比本地最早的updateTime之前的数据中的最新的一屏数据 ，
	 *            fetchLatest =true 对应下拉刷新，获取最新数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List fetchServerData(boolean fetchLatest) throws SQLException,
			IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		ORMDataHelper helper = ORMDataHelper.getInstance(mContext);
		Dao dao = helper.getDao(entityType);
		QueryBuilder builder = dao.queryBuilder();

		addLocalFilter(builder);

		GenericRawResults result = null;
		if (fetchLatest) {
			result = dao.queryRaw(builder
					.selectRaw("max(" + sortFildName + ")")
					.prepareStatementString());
		} else {
			result = dao.queryRaw(builder
					.selectRaw("min(" + sortFildName + ")")
					.prepareStatementString());
		}
		String[] values = (String[]) result.getFirstResult();
		List list = null;
		String filter = "";
		if (values[0] != null) {
			String timeFilter = "";
			if (fetchLatest) {
				timeFilter = "> '" + values[0] + "'";
			} else {
				timeFilter = "< '" + values[0] + "'";
			}

			filter = queryDemand.fildName;
			if (TextUtils.isEmpty(filter)) {
				filter = "最后更新";
			}
			// String clz = entityType.getName();
			// if (clz.equals(任务.class.getName())
			// || clz.equals(客户联系记录.class.getName())) {
			// filter = "最后处理时间" + timeFilter;
			// } else {
			// filter = "最后更新" + timeFilter;
			// }
			filter += timeFilter;
			LogUtils.i(TAG, "执行：" + filter);
			list = ServerDataLoader.getServerData(entityType, mDemand, filter);
		} else {
			// 本地一条数据都没有，则下载最新的一屏数据
			list = ServerDataLoader.getServerData(entityType, mDemand, filter);
		}
		insertOrUpdate(dao, list);
		// helper.close();
		return list;
	}

	/**
	 * * 添加本地数据库的查询条件
	 * 
	 * @param builder
	 * @param builder
	 * @return 是否添加过滤条件
	 */
	private boolean addLocalFilter(QueryBuilder builder) throws SQLException {
		boolean result = false;
		/**
		 * 添加相等的查询条件
		 */
		if (queryDemand.eqDemand.size() > 0) {
			LogUtils.i("queryDemand", "queryDemand.eqDemand.size() > 0");
			Iterator iterator = queryDemand.eqDemand.entrySet().iterator();
			Where where = builder.where();
			while (iterator.hasNext()) {
				Entry entry = (Entry) iterator.next();
				String key = entry.getKey().toString();
				String val = entry.getValue().toString();
				if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(val)) {
					where.eq(key, val).or();
					LogUtils.i(TAG, key + "---" + val);
					result = true;
				}
			}
		}

		if (queryDemand.likeDemand.size() > 0) {
			LogUtils.i("queryLikeDemand",
					"queryLikeDemand.likeDemand.size() > 0");
			Iterator iterator = queryDemand.likeDemand.entrySet().iterator();
			Where where = builder.where();
			while (iterator.hasNext()) {
				Entry entry = (Entry) iterator.next();
				String key = entry.getKey().toString();
				String val = entry.getValue().toString();
				if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(val)) {
					where.like(key, "'%" + val.trim() + "%'").or();
					LogUtils.i(TAG, key + "---" + "%" + val.trim() + "%");
					result = true;
				}
			}
		}

		if (queryDemand.eqListOrDemand.size() > 0) {
			LogUtils.i(TAG, "queryDemand.eqListOrDemand.size() > 0");
			Iterator iterator = queryDemand.eqListOrDemand.entrySet()
					.iterator();
			Where where = builder.where();
			while (iterator.hasNext()) {
				Entry entry = (Entry) iterator.next();
				String key = entry.getKey().toString();
				List<String> list = (List<String>) entry.getValue();
				if (!TextUtils.isEmpty(key)) {
					for (String val : list) {
						where.eq(key, val).or();
						LogUtils.i(TAG, key + "---" + val);
					}
					result = true;
				}
			}
		}
		return result;
	}

	/**
	 * 将网络返回数据，存入本地数据库，如果存在则修改，没有则插入
	 * 
	 * @param dao
	 * @param list
	 */
	private void insertOrUpdate(Dao dao, List list)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, SQLException {
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
					LogUtils.i(TAG, "###############修改了数据库#############");
				} else {
					dao.create(entity);
					LogUtils.i(TAG, "###############插入了数据库#############");
				}
			}
		}
	}

	/**
	 * 
	 */
	public void NotifyDataSetChanged() {
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
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
			e.printStackTrace();
		}
		LogUtils.i(TAG, "SIZE--" + list.size());
		// helper.close();
		return list;
	}

	/**
	 * 根据过滤条件，获得数据库表中满足条件的所有数据
	 * 
	 * @return
	 */
	public List getAllList_filter() {
		// TODO Auto-generated method stub
		ORMDataHelper helper = ORMDataHelper.getInstance(mContext);
		Dao dao;
		List list = null;
		try {
			dao = helper.getDao(entityType);
			QueryBuilder builder = dao.queryBuilder();
			addLocalFilter(builder);
			LogUtils.i("keno7", entityType.getName());
			list = builder.query();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// helper.close();
		}
		LogUtils.i(TAG, "SIZE--" + list.size());

		return list;
	}

	public void SetOnDataLoad(OnLocalDataLoadded callCack) {
		callCack.doSomeThing(getAllList_filter().size() + "");
	}

	/**
	 * 本地数据加载完毕监听
	 * 
	 * @author BOHR
	 * 
	 */
	public interface OnLocalDataLoadded {
		void doSomeThing(String result);
	}

	/*
	 */
	public void reI() {
		mDemand.页码 = 0;
		mDemand.偏移量 = 0;
		loadMoreButton.setText("查看更多...");
		loadMoreButton.setVisibility(View.VISIBLE);
		loadMoreButton_root.setVisibility(View.VISIBLE);
	}
}
