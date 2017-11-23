package com.cedarhd.crm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanCrmAdapter;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.biz.VmFormBiz;
import com.cedarhd.control.BoeryunSearchView.OnSearchedListener;
import com.cedarhd.control.listview.ListViewLoader;
import com.cedarhd.control.listview.PullToRefreshAndLoadMoreListView;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.models.Client;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.QueryFilter;
import com.cedarhd.models.User;
import com.cedarhd.models.crm.QmContract;
import com.cedarhd.models.crm.收款单简易版;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.widget.ListFilterPopupWindow;
import com.cedarhd.widget.ListFilterPopupWindow.OnSelectListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 收款单列表
 * 
 * 2015-10-20
 */
public class CRMReceiptListActivity extends BaseActivity {
	private Context mContext;
	private List<收款单简易版> mList;
	private QmContract mQmContract;
	private QueryDemand mQueryDemand;
	private CommanCrmAdapter<收款单简易版> mAdapter;

	private ListFilterPopupWindow mFilterPopupWindow;
	private QueryFilter mQueryFilter;

	private ImageView ivBack;
	private ImageView ivAdd;
	private ImageView ivFilter;
	private TextView tvTitle;
	private PullToRefreshAndLoadMoreListView lv;

	private ListViewLoader<收款单简易版> mListViewLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receipt_list);

		initData();
		initViews();
		setOnEvent();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case UserBiz.SELECT_SINAL_USER_REQUEST_CODE:
				User user = UserBiz.onActivityUserSelected(requestCode,
						resultCode, data);
				mQueryFilter.userId = Integer.parseInt(user.Id);
				mQueryFilter.userName = user.UserName;
				mFilterPopupWindow.updateUserFilter(mQueryFilter);
				break;
			case ClientBiz.SELECT_CLIENT_CODE:
				Client client = ClientBiz
						.onActivityGetClient(requestCode, data);
				mQueryFilter.clientId = client.Id;
				mQueryFilter.clientName = client.CustomerName;
				mFilterPopupWindow.updateClientFilter(mQueryFilter);
				break;
			default:
				break;
			}
		}
	}

	private void initData() {
		mContext = this;
		mList = new ArrayList<收款单简易版>();
		mQmContract = new QmContract();
		mQmContract.PageSize = 10;
		mQmContract.Offset = 0;
		mQmContract.OnlyCurrentUser = false;

		mQueryDemand = new QueryDemand("时间");
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_comman);
		ivAdd = (ImageView) findViewById(R.id.iv_add_comman);
		ivFilter = (ImageView) findViewById(R.id.iv_filter_comman);
		tvTitle = (TextView) findViewById(R.id.tv_title_comman);
		tvTitle.setText("收款单列表");

		ivFilter.setVisibility(View.VISIBLE);

		lv = (PullToRefreshAndLoadMoreListView) findViewById(R.id.lv_comman_loadlist);
		mAdapter = getAdapter();
		mListViewLoader = new ListViewLoader<收款单简易版>(mContext,
				"SaleSummary/getReceiptList", lv, mAdapter, mQmContract,
				mQueryDemand, 收款单简易版.class);

		// 初始化
		mQueryFilter = new QueryFilter();
		mFilterPopupWindow = new ListFilterPopupWindow(ivFilter, mContext);
	}

	private void setOnEvent() {
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ivFilter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mFilterPopupWindow.show(mQueryFilter);
				mFilterPopupWindow.setOnClickListener(new OnSelectListener() {
					@Override
					public void onSelect(QueryFilter filter) {
						mQueryFilter = filter;
						showShortToast(filter.userName + "\n"
								+ filter.clientName);
						initMoreFilter(filter);
						reLoadData();
					}

					@Override
					public void onStartSelect(QueryFilter filter) {
						mQueryFilter = filter;
					}
				});
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position >= mList.size()) {
					return;
				}
				int dataPos = position - lv.getHeaderViewsCount();
				LogUtils.i(TAG, "pos=" + position);
				收款单简易版 item收款单简易版 = mList.get(dataPos);
				VmFormBiz.startVmFromActivity(mContext, 185, item收款单简易版.编号,
						null);
			}
		});

		lv.mSearchView.setOnSearchedListener(new OnSearchedListener() {
			@Override
			public void OnSearched(String str) {
				mQmContract.Offset = 0;
				mQmContract.moreFilter = "单号 like '%" + str + "%'";
				mListViewLoader.clearData();
				mListViewLoader.startRefresh();
			}
		});
	}

	private void reLoadData() {
		mQmContract.Offset = 0;
		mListViewLoader.clearData();
		mListViewLoader.startRefresh();
	}

	private void initMoreFilter(QueryFilter filter) {
		mQmContract.moreFilter = "1=1";
		if (filter != null) {
			if (filter.userId != 0) {
				mQmContract.moreFilter += " AND 业务员=" + filter.userId;
			}

			if (filter.clientId != 0) {
				mQmContract.moreFilter += " AND 客户=" + filter.clientId;
			}

			if (!TextUtils.isEmpty(filter.startTime)) {
				// mQmContract.moreFilter += " AND 客户="
				// + filter.clientId;
			}

			if (!TextUtils.isEmpty(filter.endTime)) {
				// mQmContract.moreFilter += " AND 客户="
				// + filter.clientId;
			}
		}
	}

	private CommanCrmAdapter<收款单简易版> getAdapter() {
		return new CommanCrmAdapter<收款单简易版>(mList, mContext,
				R.layout.item_clewlist) {
			@Override
			public void convert(int position, 收款单简易版 item,
					BoeryunViewHolder viewHolder) {
				TextView tvClient = viewHolder
						.getView(R.id.tv_client_clewlist_item);
				TextView tvContact = viewHolder
						.getView(R.id.tv_contacts_clewlist_item);
				TextView tvPhone = viewHolder
						.getView(R.id.tv_phone_clewlist_item);
				TextView tvProduct = viewHolder
						.getView(R.id.tv_product_clewlist_item);
				TextView tvTime = viewHolder
						.getView(R.id.tv_time_item_clewlist);

				tvClient.setText(StrUtils.pareseNull(item.单号));
				tvContact.setText(StrUtils.pareseNull(item.合同号));
				tvPhone.setText(StrUtils.pareseNull(item.订单));
				tvTime.setText(DateDeserializer.getFormatTime(item.时间));
			}
		};
	}
}
