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
import com.cedarhd.models.Dict;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.QueryFilter;
import com.cedarhd.models.User;
import com.cedarhd.models.crm.QmContract;
import com.cedarhd.models.crm.合同;
import com.cedarhd.widget.ListFilterPopupWindow;
import com.cedarhd.widget.ListFilterPopupWindow.OnSelectListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * 选择合同列表
 * 
 * @author K 2015/09/29 17:07
 */
public class CRMSelectConpactListActivity extends BaseActivity {

	private Context mContext;

	/** 是否选择模式 */
	public final static String IS_SELECT_MODE = "isSelect";

	/** 表单编号 */
	public final static String TYPE_ID = "typeId";

	/** 表单名称 */
	public final static String TYPE_NAME = "typeName";

	private boolean mIsSelect;
	private int mTypeId;
	private String mTypeName;

	private List<合同> mList;
	private QmContract mQmContract;
	private QueryDemand mQueryDemand;
	private CommanCrmAdapter<合同> mAdapter;

	private ListFilterPopupWindow mFilterPopupWindow;
	private QueryFilter mQueryFilter;

	private ImageView ivBack;
	private ImageView ivFilter;
	private ImageView ivAdd;
	private PullToRefreshAndLoadMoreListView lv;

	private ListViewLoader<合同> mListViewLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_conpactlist_crm);

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
		mContext = CRMSelectConpactListActivity.this;
		mList = new ArrayList<合同>();
		mQmContract = new QmContract();
		mQmContract.PageSize = 10;
		mQmContract.Offset = 0;
		mQmContract.QueryAllUser = true;
		mQmContract.NoPager = false;
		mQueryDemand = new QueryDemand("制单时间");

		mIsSelect = getIntent().getBooleanExtra(IS_SELECT_MODE, false);
		mTypeId = getIntent().getIntExtra(TYPE_ID, 0);
		mTypeName = getIntent().getStringExtra(TYPE_NAME);
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_select_conpact);
		ivFilter = (ImageView) findViewById(R.id.iv_filter_conpactlist);
		ivAdd = (ImageView) findViewById(R.id.iv_add_conpactlist);
		lv = (PullToRefreshAndLoadMoreListView) findViewById(R.id.lv_comman_loadlist);

		mAdapter = getAdapter();

		mListViewLoader = new ListViewLoader<合同>(mContext,
				"SaleSummary/getConpactList", lv, mAdapter, mQmContract,
				mQueryDemand, 合同.class);

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
				if (position >= mList.size() || dataPos < 0) {
					return;
				}

				合同 conpact = mList.get(dataPos);

				if (mIsSelect && mTypeId != 0) {
					HashMap<String, Object> properties = new HashMap<String, Object>();
					properties.put("合同号", conpact.单号);
					Dict dict = new Dict();
					dict.编号 = conpact.客户;
					dict.名称 = mAdapter.getDictName("客户", conpact.客户);
					properties.put("客户", dict);
					// 收款单
					VmFormBiz.startNewVmFromActivity(mContext, mTypeId,
							mTypeName, properties);
					finish();
				} else {
					VmFormBiz.startVmFromActivity(mContext, conpact.流程分类表,
							conpact.编号, null);
				}

			}
		});
	}

	private CommanCrmAdapter<合同> getAdapter() {
		return new CommanCrmAdapter<合同>(mList, mContext,
				R.layout.item_conpact_list) {
			@Override
			public void convert(int position, 合同 item,
					BoeryunViewHolder viewHolder) {
				TextView tvNo = viewHolder.getView(R.id.tv_no_conpact_item);
				TextView tvType = viewHolder.getView(R.id.tv_type_conpact_item);
				TextView tyTotal = viewHolder
						.getView(R.id.tv_total_conpact_item);
				TextView tvClient = viewHolder
						.getView(R.id.tv_client_conpact_item);
				TextView tvUser = viewHolder.getView(R.id.tv_user_conpact_item);
				TextView tvTime = viewHolder.getView(R.id.tv_time_conpact_item);
				String tableName = TextUtils.isEmpty(item.表单名称) ? "合同"
						: item.表单名称;
				String userName = this.getDictName("制单人", item.制单人) + "";
				String clientName = "客户：" + this.getDictName("客户", item.客户)
						+ "";
				String time = DateDeserializer.getFormatTime(item.制单时间);

				tvNo.setText(item.单号 + "");
				tvType.setText(tableName + "");
				tyTotal.setText(item.金额 + "");
				tvUser.setText(userName);
				tvTime.setText(time + "");
				tvClient.setText(clientName + "");
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
