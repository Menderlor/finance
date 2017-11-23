package com.cedarhd.control.listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ServerDataLoader;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 最新的ListView帮助类
 * 
 * 从网络下载后加载到ListView中
 * 
 * 包括下拉刷新
 * 
 * @author kjx
 * @since 2014-9-30
 */
@SuppressLint("NewApi")
public class ListViewHelperNet<T> {
	/** 当前所在Activity */
	private Context mContext;
	/** 数据加载条件类 */
	public Demand mDemand;
	public QueryDemand queryDemand;
	private final String TAG = "ListViewHelperNet"; // 标识
	private PullToRefreshListView mListView = null; // 下拉刷新的ListView

	/** 列表数据源 */
	public List<T> mDataList = null;

	private BaseAdapter mAdapter = null; // 内容适配器
	/** 是否在加载数据 */
	public Boolean mIsloading = false;
	/** 是否初次加载 */
	public Boolean mIsFirstLoad = true;
	/** 数据加载方式 默认加载顶部 */
	public ListViewLoadType mListViewLoadType = ListViewLoadType.顶部视图;
	private MyProgressBar mProgressBar;

	// 查看更多按钮
	private Button loadMoreButton;
	private LinearLayout loadMoreButton_root;
	private View loadMoreView;

	private Class<T> entityType = null;
	private int offsets = 0; // 偏移量
	private boolean isloadMoreButton = false; // 是否点击加载更多

	/*** 当前可见Item个数 :设置为4个 */
	protected int mVisibleItemCount = 4;

	public static final int LOAD_SERVER_LATEST_DATA_SUCCEEDED = 2; // 加载服务器最新数据成功
	public static final int LOAD_SERVER_EAREST_DATA_SUCCEEDED = 3; // 加载服务器更久远的数据成功
	public static final int LOAD_SERVER_DATA_FAILED = 6; // 加载服务器数据失败
	public static final int LOAD_SERVER_DATA_ISNULL = 7; // 加载服务器数据失败

	/** 数据加载Handler */
	private Handler loadDataFinishedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOAD_SERVER_DATA_FAILED:
				Toast.makeText(mContext, "加载网络数据失败，请检查网络！", Toast.LENGTH_SHORT)
						.show();
				dismissProgressBar();
				break;
			case LOAD_SERVER_DATA_ISNULL:
				LogUtils.i(TAG, "LOAD_SERVER_DATA_ISNULL");
				dismissProgressBar();
				if (mListViewLoadType == ListViewLoadType.顶部视图) {
					// 完成下拉刷新
					if (mListViewLoadType == ListViewLoadType.顶部视图) {
						mListView.onRefreshComplete();
					}
				} else {
					// 底部加载数据为空,已经没有最新数据，隐藏查看更多按钮
					Toast.makeText(mContext, "已经加载了所有数据", Toast.LENGTH_SHORT)
							.show();
					loadMoreButton.setText("查看更多");
					hiddenFootView();
				}
				break;
			case LOAD_SERVER_LATEST_DATA_SUCCEEDED: // 加载最新数据成功
				mListView.setVisibility(View.VISIBLE);
				mListView.onRefreshComplete();
				// 重新从本地数据库读
				dismissProgressBar();
				List<T> list = (List<T>) msg.obj;
				LogUtils.i(TAG,
						"LOAD_SERVER_LATEST_DATA_SUCCEEDED...." + list.size());
				// 最新数据 加在List头部
				mDataList.addAll(0, list);

				setNotifyDataSetChanged();

				setLoadMoreViewVisble();
				loadMoreButton.setText("查看更多");

				if (mLoadSuccessListsener != null) {
					mLoadSuccessListsener.onLoad(list);
				}
				break;
			case LOAD_SERVER_EAREST_DATA_SUCCEEDED:
				dismissProgressBar();

				List<T> msgList = (List<T>) msg.obj;
				mDataList.addAll(msgList);

				// 之前的数据 加在List尾部
				LogUtils.i(TAG, "size()=" + msgList.size());
				setNotifyDataSetChanged();

				setLoadMoreViewVisble();
				loadMoreButton.setText("查看更多");

				if (mLoadSuccessListsener != null) {
					mLoadSuccessListsener.onLoad(msgList);
				}
				break;
			default:
				break;
			}
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

	public ListViewHelperNet(Context mContext, Class<T> entityType,
			Demand mDemand, PullToRefreshListView mListView, List<T> mDataList,
			BaseAdapter mAdapter, MyProgressBar mProgressBar,
			QueryDemand queryDemand) {
		super();
		this.mContext = mContext;
		this.mDemand = mDemand;
		this.mListView = mListView;
		this.mDataList = mDataList;
		this.mAdapter = mAdapter;
		this.mProgressBar = mProgressBar;
		this.queryDemand = queryDemand;
		this.entityType = entityType;

		// 加载底部“查看更多”
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
	 * 增加底部按钮
	 */
	private void addFootView() {
		loadMoreView = View.inflate(mContext,
				R.layout.common_listview_loadmore, null);
		loadMoreButton = (Button) loadMoreView
				.findViewById(R.id.loadMoreButton);
		loadMoreButton_root = (LinearLayout) loadMoreView
				.findViewById(R.id.loadMoreButton_root);
		loadMoreButton.setText("查看更多");
		loadMoreButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
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
				// TODO
				loadServerData(false);
			}
		});
		// setLoadMoreViewVisble();
		mListView.addFooterView(loadMoreView);

		hiddenFootView();// 默认设置为不可见
	}

	public void hiddenFootView() {
		// mListView.removeFooterView(loadMoreView);
		loadMoreButton.setVisibility(View.INVISIBLE);
		loadMoreButton_root.setVisibility(View.INVISIBLE);
	}

	/***
	 * 设置底部可见状态，数据为空则不显示
	 */
	private void setLoadMoreViewVisble() {
		LogUtils.i("onScrollSet", mDataList.size() + "--" + mVisibleItemCount);
		if (mDataList == null || mDataList.size() == 0
				|| mDataList.size() <= mVisibleItemCount) {
			// 数据源个数小于或等于可见Item个数则隐藏
			loadMoreButton.setVisibility(View.GONE);
			loadMoreButton_root.setVisibility(View.GONE);
		} else {
			loadMoreButton.setVisibility(View.VISIBLE);
			loadMoreButton_root.setVisibility(View.VISIBLE);
		}
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
				// showFootView(); // TODO
			} else {
				mListViewLoadType = ListViewLoadType.底部视图;
			}
			LogUtils.i(TAG,
					"用户编号=" + mDemand.用户编号 + ",list长度=" + mDataList.size());
			startFetchServerDataThread(fetchLatest);
		}
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
					List<T> list = fetchServerData(fetchLatest);
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
	private List<T> fetchServerData(boolean fetchLatest) throws SQLException,
			IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		if (fetchLatest) {
			// 下拉刷新查看更多时
			mDemand.偏移量 = 0;
			if (mDataList != null && mDataList.size() > 0) {
				T item = mDataList.get(0);
				try {
					Field field = entityType
							.getDeclaredField(queryDemand.sortFildName);
					Object obj = field.get(item);
					String updateTime = "";
					if (obj instanceof Date) {
						Date date = (Date) field.get(item);
						updateTime = ViewHelper.formatDateToStr(date);
					} else {
						updateTime = (String) field.get(item);
					}
					mDemand.条件 = queryDemand.fildName + "> '" + updateTime
							+ "'";
					LogUtils.i(TAG, "反射取到时间值：" + updateTime);
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
					LogUtils.i(TAG, "反射异常：" + e.toString());
				}
			}
		} else {
			// mDemand.条件 = "";
			mDemand.偏移量 = mDataList.size();
		}
		// 本地一条数据都没有，则下载最新的一屏数据
		List<T> list = ServerDataLoader.getServerData(mDemand, entityType);
		return list;
	}

	/**
	 * 数据源发生变化时刷新页面
	 */
	public void setNotifyDataSetChanged() {
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	private LoadSuccessListsener<T> mLoadSuccessListsener;

	public void setOnSuccessListsener(
			LoadSuccessListsener<T> loadSuccessListsener) {
		this.mLoadSuccessListsener = loadSuccessListsener;
	}

	public interface LoadSuccessListsener<T> {
		void onLoad(List<T> list);
	}

}
