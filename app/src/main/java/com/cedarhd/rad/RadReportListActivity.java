package com.cedarhd.rad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.rad.Rad上报数据;
import com.cedarhd.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;

/***
 * 上报列表
 * 
 * @author K
 * 
 */
public class RadReportListActivity extends BaseActivity {

	private final int REQUESTCODE_UPDATE_REPORT = 0x1;
	private final int REQUESTCODE_ADD_REPORT = 0x2;

	private Context mContext;
	private Demand demand;
	private QueryDemand queryDemand;
	private ListViewHelperNet<Rad上报数据> mListViewHelperNet;
	private DictionaryHelper mDictionaryHelper;

	private BoeryunHeaderView headerView;
	private PullToRefreshListView lv;
	private MyProgressBar pbar;

	private CommanAdapter<Rad上报数据> mAdaper;
	private List<Rad上报数据> mReportList;
	protected int mPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rad_report_list);
		initViews();
		initData();
		reload();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			switch (requestCode) {
			case REQUESTCODE_UPDATE_REPORT:// 修改
				if (data != null && data.getExtras() != null) {
					Rad上报数据 report = (Rad上报数据) data.getExtras()
							.getSerializable(
									RadReportInfoActivity.TAG_REPORT_INFO);
					mReportList.remove(mPos);
					mAdaper.addTop(report, false);
				}
				break;
			case REQUESTCODE_ADD_REPORT:// 新建
				if (data != null && data.getExtras() != null) {
					Rad上报数据 report = (Rad上报数据) data.getExtras()
							.getSerializable(
									RadReportInfoActivity.TAG_REPORT_INFO);
					mAdaper.addTop(report, false);
				}
				break;

			default:
				break;
			}
		}
	}

	private void initData() {
		mContext = RadReportListActivity.this;
		mReportList = new ArrayList<Rad上报数据>();
		queryDemand = new QueryDemand();
		mDictionaryHelper = new DictionaryHelper(mContext);
		demand = new Demand();
		demand.表名 = "";
		// demand.方法名 = "查询_分页";
		demand.方法名 = "ShopReport/GetReportDataList";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 10;
		demand.偏移量 = 0;
		queryDemand.fildName = "编号";
		queryDemand.sortFildName = "编号";
		mAdaper = getOrderAdapter();
		lv.setAdapter(mAdaper);
		mListViewHelperNet = new ListViewHelperNet<Rad上报数据>(this,
				Rad上报数据.class, demand, lv, mReportList, mAdaper, pbar,
				queryDemand);
	}

	private void initViews() {
		headerView = (BoeryunHeaderView) findViewById(R.id.header_rad_report_list);
		lv = (PullToRefreshListView) findViewById(R.id.lv_comman_old_style);
		pbar = (MyProgressBar) findViewById(R.id.progress_comman_old_style);
		setOnTouchListener();
	}

	private void setOnTouchListener() {

		headerView.setOnButtonClickListener(new OnButtonClickListener() {

			@Override
			public void onClickSaveOrAdd() {
				Rad上报数据 selectReport = getMyTodayReport();
				if (selectReport == null) {
					startActivityForResult(new Intent(mContext,
							RadReportInfoActivity.class),
							REQUESTCODE_ADD_REPORT);
				} else {
					// 已存在今天数据 都直接传递上报数据
					showShortToast("已上报");
					openReportInfo(selectReport);
				}
			}

			@Override
			public void onClickFilter() {

			}

			@Override
			public void onClickBack() {
				finish();
			}
		});

		lv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				lv.onRefreshComplete();
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 记录查看记录在数据源中的位置
				mPos = position - lv.getHeaderViewsCount();
				if (mPos < mReportList.size() && mPos >= 0) {
					Rad上报数据 reportInfo = mReportList.get(mPos);
					openReportInfo(reportInfo);
				}
			}
		});
	}

	private void reload() {
		boolean isConnectedInternet = HttpUtils.IsHaveInternet(mContext);
		if (!isConnectedInternet) {
			Toast.makeText(mContext, "需要连接到3G或者wifi因特网才能获取最新信息！",
					Toast.LENGTH_LONG).show();
			// mListViewHelperKjx.loadLocalData();
		} else {
			mReportList.clear();
			mListViewHelperNet.setNotifyDataSetChanged();
			// mListViewHelperKjx.loadServerData(true);
			mListViewHelperNet.loadServerData(true);
		}
	}

	/***
	 * 获取订单列表适配器
	 * 
	 * @return
	 */
	private CommanAdapter<Rad上报数据> getOrderAdapter() {
		return new CommanAdapter<Rad上报数据>(mReportList, mContext,
				R.layout.item_rad_report_list) {
			@Override
			public void convert(final int position, final Rad上报数据 item,
					BoeryunViewHolder viewHolder) {
				TextView tvVisit = viewHolder
						.getView(R.id.tv_visit_rad_report_item);
				TextView tvAsk = viewHolder
						.getView(R.id.tv_ask_rad_report_item);
				TextView tvDeal = viewHolder
						.getView(R.id.tv_deal_rad_report_item);
				TextView tvUser = viewHolder
						.getView(R.id.tv_user_rad_report_item);
				TextView tvTime = viewHolder
						.getView(R.id.tv_time_rad_report_item);

				String orderNo = getResources().getString(R.string.order_no);
				String totalStr = getResources()
						.getString(R.string.order_total);
				tvVisit.setText("进店：" + item.进店人数);
				tvAsk.setText("咨询：" + item.咨询人数);
				tvDeal.setText("成交：" + item.成交人数);
				tvUser.setText("上报人："
						+ mDictionaryHelper.getUserNameById(item.上报人));
				tvTime.setText(DateDeserializer.getFormatDate(item.上报时间));
			}
		};
	}

	/** 获取我的今天上报数据 */
	private Rad上报数据 getMyTodayReport() {
		for (int i = 0; i < mReportList.size(); i++) {
			Rad上报数据 report = mReportList.get(i);
			String reportDate = DateDeserializer.getFormatDate(report.上报时间);
			String todayDate = DateDeserializer.getFormatDate(ViewHelper
					.getDateToday());
			if (todayDate.equals(reportDate)
					&& (report.上报人 + "").equals(Global.mUser.getId())) {
				mPos = i; // 记录打开位置
				return report;
			}
		}
		return null;
	}

	private void openReportInfo(Rad上报数据 reportInfo) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(RadReportInfoActivity.TAG_REPORT_INFO,
				reportInfo);
		Intent intent = new Intent(mContext, RadReportInfoActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, REQUESTCODE_UPDATE_REPORT);
	}
}
