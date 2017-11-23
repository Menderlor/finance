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
import com.cedarhd.control.BoeryunSearchView.OnSearchedListener;
import com.cedarhd.control.listview.ListViewLoader;
import com.cedarhd.control.listview.PullToRefreshAndLoadMoreListView;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.models.Client;
import com.cedarhd.models.Dict;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.QueryFilter;
import com.cedarhd.models.User;
import com.cedarhd.models.crm.QmContract;
import com.cedarhd.models.crm.线索;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.widget.ListFilterPopupWindow;
import com.cedarhd.widget.ListFilterPopupWindow.OnSelectListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * Crm线索列表
 * 
 * @author K 2015年10月
 */
public class CRMClewListActivity extends BaseActivity {
	private Context mContext;
	private List<线索> mList;
	private QmContract mQmContract;
	private QueryDemand mQueryDemand;
	private CommanCrmAdapter<线索> mAdapter;

	private ListFilterPopupWindow mFilterPopupWindow;
	private QueryFilter mQueryFilter;

	private ImageView ivBack;
	private ImageView ivFilter;
	private ImageView ivAdd;

	private PullToRefreshAndLoadMoreListView lv;

	private ListViewLoader<线索> mListViewLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clewlist);
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
		mContext = CRMClewListActivity.this;
		mList = new ArrayList<线索>();
		mQmContract = new QmContract();
		mQmContract.PageSize = 10;
		mQmContract.Offset = 0;
		mQueryDemand = new QueryDemand("编号");

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_clewlist);
		ivFilter = (ImageView) findViewById(R.id.iv_filter_clewlist);
		ivAdd = (ImageView) findViewById(R.id.iv_add_clewlist);
		lv = (PullToRefreshAndLoadMoreListView) findViewById(R.id.lv_load_clewlist);
		mAdapter = getAdapter();
		mListViewLoader = new ListViewLoader<线索>(mContext,
				"SaleSummary/GetClewList", lv, mAdapter, mQmContract,
				mQueryDemand, 线索.class);

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
				int pos = position - lv.getHeaderViewsCount();
				if (pos < 0 || pos >= mList.size()) {
					return;
				}
				HashMap<String, List<Dict>> dictionarys = mAdapter
						.getmDictionarys();
				Intent intent = new Intent(mContext,
						CRMAddXiansuoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(CRMAddXiansuoActivity.CLEW,
						mList.get(pos));
				bundle.putSerializable(CRMAddXiansuoActivity.DICTIONARYS,
						dictionarys);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		lv.mSearchView.setOnSearchedListener(new OnSearchedListener() {

			@Override
			public void OnSearched(String str) {
				mQmContract.moreFilter = "公司名称 like '%" + str + "%'";
				reLoadData();
			}
		});

	}

	private CommanCrmAdapter<线索> getAdapter() {
		return new CommanCrmAdapter<线索>(mList, mContext, R.layout.item_clewlist) {
			@Override
			public void convert(int position, 线索 item,
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
				tvClient.setText(StrUtils.pareseNull(item.公司名称));
				tvContact.setText(StrUtils.pareseNull(item.联系人));
				tvPhone.setText(StrUtils.pareseNull(item.联系电话));
				tvTime.setText(DateDeserializer.getFormatTime(item.创建时间));
			}
		};
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
}
