package com.cedarhd.control.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cedarhd.R;

/**
 * 滑到底部加载更多 ListView
 * 
 * 滑动到底部显示
 * 
 * @author K 2015/09/29 17:50
 */
public class LoadMoreListView extends ListView implements OnScrollListener {

	/** 是否正在加载中 */
	private boolean mIsLoading;

	/** 当前滑动状态 */
	private int mCurrentScrollState;

	/** 底部控件 */
	private View mFooterView;

	/** 底部加载更多，上拉刷新显示'加载中..'，进度条可见 */
	private TextView tvInfo;

	private ProgressBar pBar;

	private OnLoadMoreListener onLoadMoreListener;

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public LoadMoreListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public LoadMoreListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	/**
	 * @param context
	 */
	public LoadMoreListView(Context context) {
		super(context);
		initView(context);
	}

	/***
	 * 初始化控件，添加底部控件
	 * 
	 * @param context
	 */
	private void initView(Context context) {
		mFooterView = LayoutInflater.from(context).inflate(
				R.layout.item_load_more_listview, null);
		tvInfo = (TextView) mFooterView.findViewById(R.id.tv_load_more);
		pBar = (ProgressBar) mFooterView.findViewById(R.id.pbar_load_more);
		mFooterView.setVisibility(View.VISIBLE);
		mFooterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startLoadMore();
			}
		});

		loadCompleted();
		addFooterView(mFooterView);

		// 为自定义ListView控件绑定滚动监听事件
		this.setOnScrollListener(this);
	}

	// 监听滑动状态的变化
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 记录当前滑动状态
		mCurrentScrollState = scrollState;
	}

	// 监听屏幕滚动的item的数量
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// 判断是否滑动到底部：第一个可见的个数与 所有可见Item个数之和 大于等于item总数
		boolean isBottom = firstVisibleItem + visibleItemCount >= totalItemCount;
		if (!mIsLoading && isBottom
				&& (mCurrentScrollState != OnScrollListener.SCROLL_STATE_IDLE)) {
			if (onLoadMoreListener != null) {
				startLoadMore();
			}
		}
	}

	/** 开始加载更多 */
	private void startLoadMore() {
		mIsLoading = true;
		onLoadMoreListener.onLoadMore();
		showBottomLoading();
	}

	/***
	 * 底部设置为加载状态
	 */
	private void showBottomLoading() {
		mFooterView.setVisibility(View.VISIBLE);
		pBar.setVisibility(View.VISIBLE);
		tvInfo.setText("加载中..");
	}

	/**
	 * 加载数据完毕
	 */
	public void loadCompleted() {
		mIsLoading = false;
		// mFooterView.setVisibility(View.INVISIBLE);
		pBar.setVisibility(View.GONE);
		tvInfo.setText("查看更多");
	}

	/**
	 * 设置“加载更多”监听
	 * 
	 * @param onLoadMoreListener
	 */
	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		this.onLoadMoreListener = onLoadMoreListener;
	}

	/**
	 * 当listview滑动到达底部时被回调
	 */
	public interface OnLoadMoreListener {
		public void onLoadMore();
	}

}