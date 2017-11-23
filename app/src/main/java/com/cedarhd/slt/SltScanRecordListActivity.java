package com.cedarhd.slt;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.slt.Slt到货扫描记录;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.StrUtils;

import java.util.ArrayList;
import java.util.List;

/***
 * 到货扫描历史记录
 * 
 * @author kjx
 * 
 *         2015-12-10
 */
public class SltScanRecordListActivity extends BaseActivity {

	private Context mContext;

	private DictionaryHelper mDictionaryHelper;
	private Demand demand;
	private QueryDemand queryDemand;
	private CommanAdapter<Slt到货扫描记录> mAdapter;
	private ListViewHelperNet<Slt到货扫描记录> mListViewHelperNet;
	private List<Slt到货扫描记录> mRecorderList = new ArrayList<Slt到货扫描记录>();
	private BoeryunHeaderView headerView;
	private PullToRefreshListView lv;
	private MyProgressBar pbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_code_list);
		initViews();
		initData();
		reload();
		setOnEvent();
	}

	private void initViews() {
		lv = (PullToRefreshListView) findViewById(R.id.lv_comman_old_style);
		pbar = (MyProgressBar) findViewById(R.id.progress_comman_old_style);
		headerView = (BoeryunHeaderView) findViewById(R.id.header_slt_scan_record_list);
	}

	private void initData() {
		mContext = this;
		mDictionaryHelper = new DictionaryHelper(mContext);

		initAdapter();
		initListViewData();
	}

	private void initListViewData() {
		queryDemand = new QueryDemand();
		demand = new Demand();
		demand.表名 = "";
		// demand.方法名 = "查询_分页";
		demand.方法名 = "slt/GetScanRecordList";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 10;
		demand.偏移量 = 0;
		queryDemand.fildName = "编号";
		queryDemand.sortFildName = "编号";
		lv.setAdapter(mAdapter);
		mListViewHelperNet = new ListViewHelperNet<Slt到货扫描记录>(this,
				Slt到货扫描记录.class, demand, lv, mRecorderList, mAdapter, pbar,
				queryDemand);
	}

	private void setOnEvent() {

		headerView.setOnButtonClickListener(new OnButtonClickListener() {

			@Override
			public void onClickSaveOrAdd() {

			}

			@Override
			public void onClickFilter() {

			}

			@Override
			public void onClickBack() {
				finish();
			}
		});
	}

	private void reload() {
		boolean isConnectedInternet = HttpUtils.IsHaveInternet(mContext);
		if (!isConnectedInternet) {
			Toast.makeText(mContext, "需要连接到3G或者wifi因特网才能获取最新信息！",
					Toast.LENGTH_LONG).show();
		} else {
			mRecorderList.clear();
			mListViewHelperNet.setNotifyDataSetChanged();
			mListViewHelperNet.loadServerData(true);
		}
	}

	/***
	 * 获取订单列表适配器
	 * 
	 * @return
	 */
	private void initAdapter() {
		mAdapter = new CommanAdapter<Slt到货扫描记录>(mRecorderList, mContext,
				R.layout.item_slt_scan_code) {
			@Override
			public void convert(final int position, final Slt到货扫描记录 item,
					BoeryunViewHolder viewHolder) {
				TextView tvCode = viewHolder
						.getView(R.id.tv_code_slt_scan_code_item);
				TextView tvType = viewHolder
						.getView(R.id.tv_type_slt_scan_code_item);
				TextView tvUser = viewHolder
						.getView(R.id.tv_user_slt_scan_code_item);
				TextView tvTime = viewHolder
						.getView(R.id.tv_time_slt_scan_code_item);
				TextView tvTotal = viewHolder
						.getView(R.id.tv_total_slt_scan_code_item);

				tvCode.setText(StrUtils.pareseNull(item.条码号));
				tvType.setText(StrUtils.pareseNull(item.型号名称));
				tvTime.setText(DateDeserializer.getFormatTime(item.扫码时间));
				tvUser.setText(mDictionaryHelper.getUserNameById(item.扫描员工));
				tvTotal.setText("￥" + item.奖励金额);
			}
		};
	}
}
