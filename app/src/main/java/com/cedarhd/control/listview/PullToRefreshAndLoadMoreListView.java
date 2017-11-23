package com.cedarhd.control.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.control.BoeryunSearchView;
import com.cedarhd.utils.DateTimeUtil;
import com.cedarhd.utils.LogUtils;

/***
 * 下拉刷新,底部点击可查看更多
 * 
 * @author K 2015-10-20
 */
public class PullToRefreshAndLoadMoreListView extends ListView implements
		OnScrollListener {
	private final static String TAG = PullToRefreshAndLoadMoreListView.class
			.getSimpleName();

	/** 上次更新时间 */
	private long mLastUpdateTime;
	/** header的状态 */
	/** 下拉刷新 */
	private final static int PULL_TO_REFRESH = 0;
	/** 释放刷新 */
	private final static int RELEASE_TO_REFRESH = 1;
	/** 刷新中.. */
	private final static int REFRESHING = 2;
	/** 刷新完成 */
	private final static int DONE = 3;

	private LayoutInflater inflater;
	private LinearLayout mHeaderView;
	private TextView mTipsText;
	private TextView mUpdateTimeText;
	private ImageView mArrowView;
	private ImageView mIvLoading;
	private RotateAnimation animRotate;
	private RotateAnimation pbarRotate;
	private RotateAnimation animReverseRotate;

	/** 底部控件 */
	private View mFooterView;
	/** 底部加载更多，上拉刷新显示'加载中..'，进度条可见 */
	private TextView tvInfo;
	private ProgressBar pBar;
	/** 是否正在加载中 */
	private boolean mIsLoading;

	private OnLoadMoreListener mOnLoadMoreListener;

	private boolean isRecored;

	/** 默认paddingTop为 header高度的负值，使header在屏幕外不可见 **/
	private int mHeaderViewPaddingTop;

	/** header布局xml文件原始定义的paddingTop */
	private int mHeaderOrgPaddingTop;

	/** 搜索内容 */
	private String mHintSearch;

	/** 搜索框 */
	public BoeryunSearchView mSearchView;

	private GestureDetector gestureDetector;

	private int mPullState;

	public OnRefreshListener refreshListener;
	public OnLastItemVisibleListener lastItemVisibleListener;
	private boolean lastItemVisible;

	/** 第一个Item是否可见 */
	private boolean isFirstItemVisible;

	public interface OnRefreshListener {
		public void onRefresh();
	}

	/**
	 * 当listview滑动到达底部时被回调
	 */
	public interface OnLoadMoreListener {
		public void onLoadMore();
	}

	public interface OnLastItemVisibleListener {
		public void onLastItemVisible(int lastIndex);
	}

	public PullToRefreshAndLoadMoreListView(Context context) {
		this(context, null);
	}

	public PullToRefreshAndLoadMoreListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context);

	}

	private void init(Context context) {
		mLastUpdateTime = System.currentTimeMillis();

		initArrowAnimation();

		initProgressAnimation();
		initPullHeader(context);
		initFooterView(context);

		// 为自定义ListView控件绑定滚动监听事件
		setOnScrollListener(this);
		gestureDetector = new GestureDetector(context, gestureListener);
	}

	/***
	 * 实例化下拉ListView的Header布局
	 * 
	 * @param context
	 */
	private void initPullHeader(Context context) {
		inflater = LayoutInflater.from(context);
		mHeaderView = (LinearLayout) inflater.inflate(
				R.layout.pull_to_refresh_head, null);
		mArrowView = (ImageView) mHeaderView
				.findViewById(R.id.head_arrowImageView);
		mIvLoading = (ImageView) mHeaderView
				.findViewById(R.id.head_progressBar);
		mTipsText = (TextView) mHeaderView.findViewById(R.id.head_tipsTextView);
		mUpdateTimeText = (TextView) mHeaderView
				.findViewById(R.id.head_updatetimeTextView);

		mHeaderOrgPaddingTop = mHeaderView.getPaddingTop();
		measureView(mHeaderView);
		mHeaderViewPaddingTop = -mHeaderView.getMeasuredHeight();
		setHeaderPaddingTop(mHeaderViewPaddingTop);
		mHeaderView.invalidate();
		addHeaderView(mHeaderView);
		mSearchView = new BoeryunSearchView(context);
		addHeaderView(mSearchView);
	}

	/***
	 * 初始化控件，添加底部控件
	 * 
	 * @param context
	 */
	private void initFooterView(Context context) {
		mFooterView = LayoutInflater.from(context).inflate(
				R.layout.item_load_more_listview, null);
		tvInfo = (TextView) mFooterView.findViewById(R.id.tv_load_more);
		pBar = (ProgressBar) mFooterView.findViewById(R.id.pbar_load_more);
		mFooterView.setVisibility(View.INVISIBLE);

		// 点击更多 开始加载
		mFooterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mIsLoading) {
					onLoadMore();
				}
			}
		});

		loadCompleted();
		addFooterView(mFooterView);
	}

	/**
	 * 加载数据完毕
	 */
	public void loadCompleted() {
		mIsLoading = false;
		pBar.setVisibility(View.GONE);
		tvInfo.setText("查看更多");

	}

	/** 开始加载更多 */
	private void onLoadMore() {
		// 底部设置为加载状态
		mFooterView.setVisibility(View.VISIBLE);
		pBar.setVisibility(View.VISIBLE);
		tvInfo.setText("加载中...");
		mIsLoading = true;
		if (mOnLoadMoreListener != null) {
			mOnLoadMoreListener.onLoadMore();
		}
	}

	private void setHeaderPaddingTop(int paddingTop) {
		mHeaderView.setPadding(mHeaderView.getPaddingLeft(), paddingTop,
				mHeaderView.getPaddingRight(), mHeaderView.getPaddingBottom());
	}

	/**
	 * 实例化下拉箭头动画
	 */
	private void initArrowAnimation() {
		// 定义一个旋转角度为0 到-180度的动画，时长100ms
		animRotate = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animRotate.setInterpolator(new LinearInterpolator());
		animRotate.setDuration(100);
		animRotate.setFillAfter(true);

		animReverseRotate = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animReverseRotate.setInterpolator(new LinearInterpolator());
		animReverseRotate.setDuration(100);
		animReverseRotate.setFillAfter(true);
	}
	
	/**
	 * 给搜索框设置提示文字
	 */
	public void setHintText(String text) {
		mSearchView.setHintText(text);
	}

	/**
	 * 加载等待进度条
	 */
	private void initProgressAnimation() {
		pbarRotate = new RotateAnimation(0, 359,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		pbarRotate.setInterpolator(new LinearInterpolator());
		pbarRotate.setDuration(400);
		pbarRotate.setRepeatCount(-1);
	}

	public void onScroll(AbsListView view, int firstVisiableItem,
			int visibleItemCount, int totalItemCount) {
		isFirstItemVisible = firstVisiableItem == 0 ? true : false;

		boolean loadMore = firstVisiableItem + visibleItemCount >= totalItemCount;

		if (loadMore) {
			if (mPullState != REFRESHING && lastItemVisible == false
					&& lastItemVisibleListener != null) {
				lastItemVisible = true;
				// including Header View,here using totalItemCount - 2
				lastItemVisibleListener.onLastItemVisible(totalItemCount - 2);
			}
		} else {
			lastItemVisible = false;
		}

		LogUtils.i(TAG, totalItemCount + "-" + visibleItemCount);
		// 减去header 和footer个数
		if ((totalItemCount - 2) > visibleItemCount) {
			// TODO 底部加载更多按钮
			mFooterView.setVisibility(VISIBLE);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// OnScrollListener.SCROLL_STATE_FLING :手指离开屏幕甩动中
		// OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:手指正在屏幕上滑动中
		// OnScrollListener.SCROLL_STATE_IDLE: 闲置的，未滑动
		Log.i("onScroll", "onScrollStateChanged");
	}

	public boolean dispatchTouchEvent(MotionEvent event) {
		if (onTouched.onTouchEvent(event)) {
			return true;
		}
		return super.dispatchTouchEvent(event);
	}

	private interface OnTouchEventListener {
		public boolean onTouchEvent(MotionEvent ev);
	}

	private OnTouchEventListener onTouched = new OnTouchEventListener() {
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_CANCEL:
				// case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_UP:
				if (isRecored) {
					requestDisallowInterceptTouchEvent(false);
					if (mPullState != REFRESHING) {
						if (mPullState == PULL_TO_REFRESH) {
							mPullState = DONE;
							changeHeaderViewByState(mPullState);

							// mSearchView.setVisibility(View.VISIBLE);
						} else if (mPullState == RELEASE_TO_REFRESH) {
							mPullState = REFRESHING;
							changeHeaderViewByState(mPullState);
							onRefresh();
						}
					}
					isRecored = false;
					return true;
				}
				break;
			}
			return gestureDetector.onTouchEvent(event);
		}
	};

	/** 自定义手势探测器 */
	private OnGestureListener gestureListener = new OnGestureListener() {
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			int deltaY = (int) (e1.getY() - e2.getY());
			if (mPullState != REFRESHING) {
				// 第一个可见，且手势下拉
				if (!isRecored && isFirstItemVisible && deltaY < 0) {
					isRecored = true;
					requestDisallowInterceptTouchEvent(true);
					changeHeaderViewByState(PULL_TO_REFRESH); // TODO kxj
				}
				if (isRecored) {
					int paddingTop = mHeaderView.getPaddingTop();
					// 释放刷新的过程
					if (paddingTop < 0 && paddingTop > mHeaderViewPaddingTop) {
						if (mPullState == RELEASE_TO_REFRESH) {
							changeHeaderViewByState(PULL_TO_REFRESH);
						}
						mPullState = PULL_TO_REFRESH;
					} else if (paddingTop >= 0) {
						if (mPullState == PULL_TO_REFRESH) {
							changeHeaderViewByState(RELEASE_TO_REFRESH);
						}
						mPullState = RELEASE_TO_REFRESH;
					}

					// 根据手指滑动状态动态改变header高度
					int topPadding = (int) (mHeaderViewPaddingTop - deltaY / 2);
					mHeaderView.setPadding(mHeaderView.getPaddingLeft(),
							topPadding, mHeaderView.getPaddingRight(),
							mHeaderView.getPaddingBottom());
					mHeaderView.invalidate();
					return true;
				}
			}
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}
	};

	/**
	 * 改变刷新状态时，调用该方法来改变headerView 显示的内容
	 * 
	 * @param state
	 *            刷新状态
	 */
	private void changeHeaderViewByState(int state) {
		switch (state) {
		case RELEASE_TO_REFRESH:
			mIvLoading.clearAnimation();
			mIvLoading.setVisibility(View.GONE);
			mTipsText.setVisibility(View.VISIBLE);
			mArrowView.setVisibility(View.VISIBLE);
			mArrowView.clearAnimation();
			mArrowView.startAnimation(animRotate);
			mTipsText.setText(R.string.pull_to_refresh_release_label);
			break;
		case PULL_TO_REFRESH:
			mIvLoading.clearAnimation();
			mIvLoading.setVisibility(View.GONE);

			mTipsText.setVisibility(View.VISIBLE);
			mArrowView.setVisibility(View.VISIBLE);
			mArrowView.clearAnimation();
			mArrowView.startAnimation(animReverseRotate);
			mTipsText.setText(R.string.pull_to_refresh_pull_label);

			mUpdateTimeText.setText("更新于："
					+ DateTimeUtil.convertTimeToFormat(mLastUpdateTime));
			break;
		case REFRESHING:
			// 设置paddingTop为原始paddingTop
			setHeaderPaddingTop(mHeaderOrgPaddingTop);
			// 设置header布局为不可点击，进度条转圈中..
			mHeaderView.invalidate();

			mArrowView.clearAnimation();
			mArrowView.setVisibility(View.GONE);

			mIvLoading.setVisibility(View.VISIBLE);
			mIvLoading.clearAnimation();
			mIvLoading.startAnimation(pbarRotate);
			mTipsText.setText(R.string.pull_to_refresh_refreshing_label);

			break;
		case DONE:
			// 设置header消失动画
			if (mHeaderViewPaddingTop - 1 < mHeaderView.getPaddingTop()) {
				ResetAnimimation animation = new ResetAnimimation(mHeaderView,
						mHeaderViewPaddingTop, false);
				animation.setDuration(300);
				mHeaderView.startAnimation(animation);
			}

			mIvLoading.clearAnimation();
			mIvLoading.setVisibility(View.GONE);
			mArrowView.setVisibility(View.VISIBLE);
			mArrowView.clearAnimation();
			mArrowView.setImageResource(R.drawable.ic_pulltorefresh_arrow);

			mTipsText.setText(R.string.pull_to_refresh_pull_label);
			setSelection(0); // listview显示到第一个Item
			break;
		}
	}

	/** 调用ListView下拉刷新 */
	public void startRefresh() {
		setSelection(0);
		mPullState = REFRESHING;
		changeHeaderViewByState(mPullState);
		onRefresh();
	}

	/**
	 * 监听下拉刷新
	 * 
	 * @param refreshListener
	 */
	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	/***
	 * 滑动到底部监听
	 * 
	 * @param listener
	 */
	public void setOnLastItemVisibleListener(OnLastItemVisibleListener listener) {
		this.lastItemVisibleListener = listener;
	}

	/***
	 * 滑动到底部监听
	 * 
	 * @param listener
	 */
	public void setOnLoadMore(OnLoadMoreListener onLoadMoreListener) {
		this.mOnLoadMoreListener = onLoadMoreListener;
	}

	/** 下拉刷新完成 */
	public void onRefreshComplete() {
		mPullState = DONE;
		changeHeaderViewByState(mPullState);
		LogUtils.i(
				"updateTime",
				"updateTime："
						+ DateTimeUtil.convertTimeToFormat(mLastUpdateTime));
		// 记录刷新时间
		mLastUpdateTime = System.currentTimeMillis();
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	/***
	 * 计算headView的width及height值
	 * 
	 * @param child
	 *            计算控件对象
	 */
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	/**
     * 设置搜索框显示状态
     **/
    public void setSerarchViewVisible(boolean isVisible) {
        if (isVisible) {
        	LogUtils.i("PullToRefreshAndLoadMoreListView", "搜索框显示");
            if (getHeaderViewsCount() == 1) {
                addHeaderView(mSearchView);
            }
        } else {
            LogUtils.i("PullToRefreshAndLoadMoreListView", "搜索框隐藏");
            if (getHeaderViewsCount() == 2) {
                removeHeaderView(mSearchView);
            }
        }
    }

	/** 底部查看更多可见状态 **/
	public void setFootViewVisible(boolean isVisible) {
		if (isVisible) {
			mFooterView.setVisibility(View.VISIBLE);
		} else {
			mFooterView.setVisibility(View.GONE);
		}
	}

	/** 消失动画 */
	public class ResetAnimimation extends Animation {
		private int targetHeight;
		private int originalHeight;
		private int extraHeight;
		private View view;
		private boolean down;
		private int viewPaddingBottom;
		private int viewPaddingRight;
		private int viewPaddingLeft;

		protected ResetAnimimation(View view, int targetHeight, boolean down) {
			this.view = view;
			this.viewPaddingLeft = view.getPaddingLeft();
			this.viewPaddingRight = view.getPaddingRight();
			this.viewPaddingBottom = view.getPaddingBottom();
			this.targetHeight = targetHeight;
			this.down = down;
			originalHeight = view.getPaddingTop();
			extraHeight = this.targetHeight - originalHeight;
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {

			int newHeight;
			newHeight = (int) (targetHeight - extraHeight
					* (1 - interpolatedTime));
			view.setPadding(viewPaddingLeft, newHeight, viewPaddingRight,
					viewPaddingBottom);
			view.requestLayout();
		}

		@Override
		public void initialize(int width, int height, int parentWidth,
				int parentHeight) {
			super.initialize(width, height, parentWidth, parentHeight);
		}
	}

}
