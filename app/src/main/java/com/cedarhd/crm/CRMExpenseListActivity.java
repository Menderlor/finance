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
import com.cedarhd.control.listview.ListViewLoader;
import com.cedarhd.control.listview.PullToRefreshAndLoadMoreListView;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.models.Client;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.QueryFilter;
import com.cedarhd.models.User;
import com.cedarhd.models.crm.Qm报销申请单;
import com.cedarhd.models.crm.报销申请单;
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
public class CRMExpenseListActivity extends BaseActivity {
	private Context mContext;
	private List<报销申请单> mList;
	private Qm报销申请单 mQm报销申请单;
	private QueryDemand mQueryDemand;
	private CommanCrmAdapter<报销申请单> mAdapter;

	private ListFilterPopupWindow mFilterPopupWindow;
	private QueryFilter mQueryFilter;

	private ImageView ivBack;
	private ImageView ivAdd;
	private ImageView ivFilter;
	private TextView tvTitle;
	private PullToRefreshAndLoadMoreListView lv;

	private ListViewLoader<报销申请单> mListViewLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expenselist);

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
		mList = new ArrayList<报销申请单>();
		mQm报销申请单 = new Qm报销申请单();
		mQm报销申请单.PageSize = 10;
		mQm报销申请单.Offset = 0;
		mQm报销申请单.OnlyCurrentUser = false;

		mQueryDemand = new QueryDemand("时间");
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_comman);
		ivAdd = (ImageView) findViewById(R.id.iv_add_comman);
		ivFilter = (ImageView) findViewById(R.id.iv_filter_comman);
		tvTitle = (TextView) findViewById(R.id.tv_title_comman);
		tvTitle.setText("报销单列表");

		ivFilter.setVisibility(View.VISIBLE);

		lv = (PullToRefreshAndLoadMoreListView) findViewById(R.id.lv_comman_loadlist);
		mAdapter = getAdapter();
		mListViewLoader = new ListViewLoader<报销申请单>(mContext,
				"SaleSummary/getExpenseList", lv, mAdapter, mQm报销申请单,
				mQueryDemand, 报销申请单.class);

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
				int dataPos = position - lv.getHeaderViewsCount();
				if (dataPos >= mList.size()) {
					return;
				}

				LogUtils.i(TAG, "pos=" + position);
				报销申请单 item报销单 = mList.get(dataPos);
				VmFormBiz.startVmFromActivity(mContext, 115, item报销单.编号, null);
			}
		});
	}

	private void reLoadData() {
		mQm报销申请单.Offset = 0;
		mListViewLoader.clearData();
		mListViewLoader.startRefresh();
	}

	private void initMoreFilter(QueryFilter filter) {
		mQm报销申请单.moreFilter = "1=1";
		if (filter != null) {
			if (filter.userId != 0) {
				mQm报销申请单.moreFilter += " AND 业务员=" + filter.userId;
			}

			if (filter.clientId != 0) {
				mQm报销申请单.moreFilter += " AND 客户=" + filter.clientId;
			}

			if (!TextUtils.isEmpty(filter.startTime)) {
				// mQm报销申请单.moreFilter += " AND 客户="
				// + filter.clientId;
			}

			if (!TextUtils.isEmpty(filter.endTime)) {
				// mQm报销申请单.moreFilter += " AND 客户="
				// + filter.clientId;
			}
		}
	}

	private CommanCrmAdapter<报销申请单> getAdapter() {
		return new CommanCrmAdapter<报销申请单>(mList, mContext,
				R.layout.item_expenselist) {
			@Override
			public void convert(int position, 报销申请单 item,
					BoeryunViewHolder viewHolder) {
				String clientName = this.getDictName("客户", item.客户);
				String userName = this.getDictName("制单人", item.制单人);

				viewHolder.setTextValue(R.id.tv_client_expense_item,
						StrUtils.pareseNull(clientName));
				viewHolder.setTextValue(R.id.tv_time_expense_item,
						DateDeserializer.getFormatTime(item.制单时间));
				viewHolder.setTextValue(R.id.tv_total_expense_item,
						StrUtils.pareseNull(item.总金额 + ""));
				viewHolder.setTextValue(R.id.tv_user_expense_item,
						StrUtils.pareseNull(userName));
			}
		};
	}
}
