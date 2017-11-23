package com.cedarhd.control.listview;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.cedarhd.base.CommanCrmAdapter;
import com.cedarhd.control.listview.PullToRefreshAndLoadMoreListView.OnLoadMoreListener;
import com.cedarhd.control.listview.PullToRefreshAndLoadMoreListView.OnRefreshListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.crm.QmBase;
import com.cedarhd.models.crm.VmBase;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * 最新的ListView数据加载帮助类
 * <p>
 * 从网络下载后加载到ListView中
 * <p>
 * 包括下拉刷新
 *
 * @author kjx
 * @since 2015-10-20
 */
public class ListViewLoader<T> {
    private final String TAG = "ListViewLoader";

    /**
     * 是否使用下拉刷新
     */
    private boolean mIsRefresh = true;

    /**
     * 访问网络方法名
     */
    private String mUrl;

    private Context mContext;
    private PullToRefreshAndLoadMoreListView mLv;

    /**
     * 过滤条件
     */
    private QmBase mQmBase;

    /**
     * 本地查询条件
     */
    private QueryDemand mQueryDemand;

    /***
     * 数据源实体 泛型类型,如 客户.class ,日志.class
     */
    private Class<T> mEntityType;

    private CommanCrmAdapter<T> mAdapter;

    /**
     * 构造函数，默认刷新
     *
     * @param mMethodName  方法名
     * @param mListView    下拉刷新，点击查看更多的 PullToRefreshAndLoadMoreListView对象
     * @param mQmBase      访问网络查询条件
     * @param mQueryDemand 本地查询条件
     * @param entityType   数据源 实体类型：客户.class
     */
    public ListViewLoader(Context context, String mMethodName,
                          PullToRefreshAndLoadMoreListView mListView,
                          CommanCrmAdapter<T> commanAdapter, QmBase mQmBase,
                          QueryDemand mQueryDemand, Class<T> entityType) {
        this(context, mMethodName, mListView, commanAdapter, mQmBase,
                mQueryDemand, entityType, true);
    }

    /**
     * @param mMethodName  方法名
     * @param mListView    下拉刷新，点击查看更多的 PullToRefreshAndLoadMoreListView对象
     * @param mQmBase      访问网络查询条件
     * @param mQueryDemand 本地查询条件
     * @param entityType   数据源 实体类型：客户.class
     */
    public ListViewLoader(Context context, String mMethodName,
                          PullToRefreshAndLoadMoreListView mListView,
                          CommanCrmAdapter<T> commanAdapter, QmBase mQmBase,
                          QueryDemand mQueryDemand, Class<T> entityType, boolean isRefresh) {
        this.mContext = context;
        this.mLv = mListView;
        this.mQmBase = mQmBase;
        this.mQueryDemand = mQueryDemand;
        this.mAdapter = commanAdapter;
        this.mEntityType = entityType;
        this.mUrl = Global.BASE_URL + mMethodName;
        this.mIsRefresh = isRefresh;
        mLv.setAdapter(mAdapter);

        setEvent();

        mLv.setSerarchViewVisible(true);// 默认搜索框可见
        startRefresh();
    }

    public void startRefresh() {
        mLv.setFootViewVisible(false);
        mLv.startRefresh(); // 开始显示刷新头部
    }

    public void clearData() {
        mAdapter.clearData();
    }

    /**
     * 设置内容为空显示内容
     */
    private void setEmptyView() {
        LogUtils.i(TAG, "setEmptyView");
        // View emptyView = LayoutInflater.from(mContext).inflate(
        // R.layout.empty_view_lv, null);
        // emptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
        // LayoutParams.MATCH_PARENT));
        // emptyView.setVisibility(View.GONE);
        // ((ViewGroup) mLv.getParent()).addView(emptyView);
        // mLv.setEmptyView(emptyView);
    }

    /**
     * 设置下拉 和点击更多监听事件
     */
    private void setEvent() {
        mLv.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMore();
            }
        });

        mLv.setOnLoadMore(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMore();
            }
        });

    }

    /**
     * 上拉刷新/点击底部 查看更多
     */
    private void loadMore() {
        fetchServerData(true);
    }

    /**
     * 下拉刷新
     */
    private void refreshMore() {
        fetchServerData(false);
    }

    /***
     * 获取网络数据
     *
     * @param isLoadMore
     *            是否底部刷新加载更多
     */
    private void fetchServerData(final boolean isLoadMore) {
        if (!HttpUtils.IsHaveInternet(mContext)) {
            Toast.makeText(mContext, "请检查网络连接！", Toast.LENGTH_SHORT).show();
            if (isLoadMore) {
                mLv.loadCompleted();
            } else {
                mLv.onRefreshComplete();
            }
            return;
        }

        if (isLoadMore) {
            mQmBase.Offset = mAdapter.getCount();
        } else {// 下拉刷新
            mQmBase.Offset = 0;
            List<T> dataList = mAdapter.getDataList();
            if (dataList != null && dataList.size() > 0) {
                mQmBase.Filter = getRefreshFilter(dataList);
            } else {
                mQmBase.Filter = "";
            }

            if (TextUtils.isEmpty(mQmBase.Filter)) {
                mQmBase.Filter = "1=1";
            }

            if (!TextUtils.isEmpty(mQmBase.moreFilter)) {
                mQmBase.Filter += " AND " + mQmBase.moreFilter;
            }
        }

        LogUtils.i(TAG, mQmBase.toString());
        StringRequest.postAsyn(mUrl, mQmBase, new StringResponseCallBack() {
            @Override
            public void onResponseCodeErro(String result) {
                LogUtils.i(TAG, "onResponseCodeErro");

                onComplete(isLoadMore, null);
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                LogUtils.i(TAG, "onFailure" + ex.getMessage());
                onComplete(isLoadMore, null);
            }

            @Override
            public void onResponse(String response) {
                try {
                    LogUtils.i(TAG, "onResponse" + response);
                    VmBase<T> vmBase = JsonUtils.convertJsonToVmBase(response,
                            mEntityType);
                    if (vmBase != null && vmBase.Data != null) {
                        mAdapter.addDict(vmBase.Dict);
                    }
                    onComplete(isLoadMore, vmBase.Data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /***
     * 获取下拉刷新过滤条件
     *
     * @param dataList
     *            当前数据源
     * @return 过滤条件
     */
    private String getRefreshFilter(List<T> dataList) {
        String filter = "";
        T item = dataList.get(0); // 根据排序规则 最值
        try {
            Field field = mEntityType
                    .getDeclaredField(mQueryDemand.sortFildName);
            Object obj = field.get(item);
            String maxValue = "";
            if (obj instanceof Date) {
                Date date = (Date) field.get(item);
                maxValue = ViewHelper.formatDateToStr(date);
            } else {
                maxValue = (String) (field.get(item) + "");
            }
            filter = mQueryDemand.fildName + ">'" + maxValue + "'";
            LogUtils.i(TAG, "反射取到最大值：" + maxValue);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            LogUtils.i(TAG, "反射异常：" + e.toString());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.toString() + "");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.toString() + "");
        } catch (Exception e) {
            LogUtils.e(TAG, e.toString() + "");
        }

        return filter;
    }

    /***
     * 加载数据完毕
     *
     * @param isLoadMore
     *            是否是底部加载更多
     * @param list
     *            加载到数据源
     */
    private void onComplete(final boolean isLoadMore, List<T> list) {
        if (isLoadMore) {
            mLv.loadCompleted();
            if (list != null && list.size() > 0) {
                // 底部查看更多
                mAdapter.addBottom(list, false);
                Toast.makeText(mContext, "加载了 " + list.size() + " 条新数据",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "已经加载了全部数据", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            mLv.onRefreshComplete();

            if (list != null && list.size() > 0) {
                // 下拉刷新
                mAdapter.addTop(list, true);

//				mLv.setSerarchViewVisible(false);// 搜索框不可见

                Toast.makeText(mContext, "加载了 " + list.size() + " 条新数据",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "没有更多啦！", Toast.LENGTH_SHORT).show();
            }
        }

        if (mAdapter.getCount() <= 0) {
            setEmptyView();
        }
    }

    private OnFetchServerDataListener<T> mOnFetchServerDataListener;

    public void setFetchServerDataListener(
            OnFetchServerDataListener<T> onFetchServerDataListener) {
        this.mOnFetchServerDataListener = onFetchServerDataListener;
    }

    public interface OnFetchServerDataListener<T> {
        void onFetched(VmBase<T> vmBase, boolean isLoadmore);

        /**
         * 网络访问异常
         */
        void onFailure();
    }

    public void setQmBase(QmBase mQmBase) {
        this.mQmBase = mQmBase;
    }

    /***
     * 是否使用下拉刷新
     *
     * @param isRefresh
     */
    public void setIsRefresh(boolean isRefresh) {
        mIsRefresh = isRefresh;
    }

}
