package com.cedarhd.control.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;

import com.cedarhd.R;
import com.cedarhd.base.CommanCrmAdapter;
import com.cedarhd.helpers.Global;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.crm.QmBase;
import com.cedarhd.models.crm.VmBase;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.List;

/**
 * 最新的ListView帮助类
 * 
 * 从网络下载后加载到ListView中
 * 
 * 包括下拉刷新
 * 
 * @author kjx
 * @since 2015-10-13
 */
public class ListViewHelper2015<T> {
	private final String TAG = "ListViewHelper2015";

	/** 访问网络方法名 */
	private String mUrl;

	private Context mContext;
	private ListView mListView;

	// private VmBase<T> mVmBase;

	/** 过滤条件 */
	private QmBase mQmBase;

	/** 本地查询条件 */
	private QueryDemand mQueryDemand;

	private Class<T> mEntityType;

	private CommanCrmAdapter<T> mAdapter;

	/**
	 * @param mMethodName
	 *            方法名
	 * @param mListView
	 *            ListView对象
	 * @param mQmBase
	 *            访问网络查询条件
	 * @param mQueryDemand
	 *            本地查询条件
	 * @param entityType
	 *            数据源 实体类型：客户.class
	 */
	public ListViewHelper2015(Context context, String mMethodName,
			ListView mListView, CommanCrmAdapter<T> commanAdapter,
			QmBase mQmBase, QueryDemand mQueryDemand, Class<T> entityType) {
		this.mContext = context;
		this.mListView = mListView;
		this.mQmBase = mQmBase;
		this.mQueryDemand = mQueryDemand;
		this.mAdapter = commanAdapter;
		this.mEntityType = entityType;
		this.mUrl = Global.BASE_URL + mMethodName;

		mListView.setAdapter(mAdapter);
		setEmptyView();
	}

	/**
	 * 设置内容为空显示内容
	 */
	private void setEmptyView() {
		View emptyView = LayoutInflater.from(mContext).inflate(
				R.layout.empty_view_lv, null);
		emptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		emptyView.setVisibility(View.GONE);
		((ViewGroup) mListView.getParent()).addView(emptyView);
		mListView.setEmptyView(emptyView);
	}

	/** 上拉刷新/点击底部 查看更多 */
	public void loadMore() {
		fetchServerData(true);
	}

	/** 下拉刷新 */
	public void refreshMore(boolean isClearOldData) {
		if (isClearOldData) {
			mAdapter.clearData();
		}
		refreshMore();
	}

	/** 下拉刷新 */
	public void refreshMore() {
		fetchServerData(false);
	}

	/***
	 * 获取网络数据
	 * 
	 * @param isLoadMore
	 *            是否底部刷新加载更多
	 */
	private void fetchServerData(final boolean isLoadMore) {
		if (isLoadMore) {
			mQmBase.Offset = mAdapter.getCount();
		} else {
			mQmBase.Offset = 0;
		}

		StringRequest.postAsyn(mUrl, mQmBase, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				LogUtils.i(TAG, "onResponseCodeErro");
			}

			@Override
			public void onResponse(String response) {
				try {
					LogUtils.i(TAG, "onResponse");
					VmBase<T> vmBase = JsonUtils.convertJsonToVmBase(response,
							mEntityType);
					List<T> list = vmBase.Data;
					if (list != null && list.size() > 0) {
						mAdapter.addDict(vmBase.Dict);
						if (isLoadMore) {
							mAdapter.addBottom(list, false);
						} else {
							mAdapter.addTop(list, false);
						}
					}

					//
					if (mOnFetchServerDataListener != null) {
						mOnFetchServerDataListener
								.onFetched(vmBase, isLoadMore);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				LogUtils.i(TAG, "onFailure" + ex.getMessage());
			}
		});
	}

	private OnFetchServerDataListener<T> mOnFetchServerDataListener;

	public void setFetchServerDataListener(
			OnFetchServerDataListener<T> onFetchServerDataListener) {
		this.mOnFetchServerDataListener = onFetchServerDataListener;
	}

	public interface OnFetchServerDataListener<T> {
		void onFetched(VmBase<T> vmBase, boolean isLoadmore);

		/** 网络访问异常 */
		void onFailure();
	}
}
