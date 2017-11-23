package com.cedarhd.slt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.constants.enums.EnumSltOrderStatus;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.rad.Rad客户;
import com.cedarhd.models.slt.Slt订单;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.cedarhd.widget.RadAddClientDialog;
import com.cedarhd.widget.RadAddClientDialog.OnSaveSuccessedListener;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;

/***
 * 森盟预算列表
 * 
 * @author K 2015/11/16 10:59
 */
public class SltCaculateListActivity extends BaseActivity {

	private Context mContext;
	private Demand demand;
	private QueryDemand queryDemand;

	private BoeryunHeaderView headerView;
	private PullToRefreshListView lv;
	private MyProgressBar pbar;

	private CommanAdapter<Slt订单> adaper;
	private ListViewHelperNet<Slt订单> mListViewHelperNet;
	private RadAddClientDialog mAddClientDialog;
	private DictionaryHelper mDictionaryHelper;
	private List<Slt订单> mOrders;
	private int deleteAtPos;

	private final int SUCCEED_DELETE_rad_caculate = 1;
	private final int FAILURE__DELETE_rad_caculate = 2;

	/** 修改房间信息 */
	public static final int REQUEST_CODE_UPDATE_ORDER = 0x03;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCEED_DELETE_rad_caculate:
				ProgressDialogHelper.dismiss();
				Toast.makeText(mContext, "成功删除订单", Toast.LENGTH_LONG).show();

				if (deleteAtPos < mOrders.size()) {
					mOrders.remove(deleteAtPos);
					adaper.notifyDataSetChanged();
				}
				break;
			case FAILURE__DELETE_rad_caculate:
				ProgressDialogHelper.dismiss();
				Toast.makeText(mContext, "删除订单失败", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rad_caculate_list);
		initViews();
		initData();
		reload();
	}

	@Override
	protected void onStart() {
		super.onStart();
		setOnTouchListener();
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
			case REQUEST_CODE_UPDATE_ORDER:
				reload();
				break;
			default:
				break;
			}
		}
	}

	private void initData() {
		mContext = SltCaculateListActivity.this;
		mOrders = new ArrayList<Slt订单>();
		mAddClientDialog = new RadAddClientDialog(mContext);
		mDictionaryHelper = new DictionaryHelper(mContext);
		queryDemand = new QueryDemand();
		demand = new Demand();

		initListViewLoader();
	}

	private void initListViewLoader() {
		demand.表名 = "";
		// demand.方法名 = "查询_分页";
		demand.方法名 = "slt/GetOrderList/" + EnumSltOrderStatus.预算.getValue();
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 10;
		demand.偏移量 = 0;
		queryDemand.fildName = "编号";
		queryDemand.sortFildName = "编号";
		adaper = getOrderAdapter();
		lv.setAdapter(adaper);
		mListViewHelperNet = new ListViewHelperNet<Slt订单>(this, Slt订单.class,
				demand, lv, mOrders, adaper, pbar, queryDemand);
	}

	private void initViews() {
		// ivBack = (ImageView) findViewById(R.id.iv_back_rad_caculatelist);
		headerView = (BoeryunHeaderView) findViewById(R.id.header_rad_caculate_list);
		lv = (PullToRefreshListView) findViewById(R.id.lv_rad_caculate_list);
		pbar = (MyProgressBar) findViewById(R.id.progress_rad_caculatelist);

	}

	private void setOnTouchListener() {

		lv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				reload();
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position - lv.getHeaderViewsCount();
				if (pos < mOrders.size() && pos >= 0) {
					Slt订单 order = mOrders.get(pos);
					Intent intent = new Intent(mContext,
							RadRoomListActivity.class);
					intent.putExtra(RadRoomListActivity.ORDER_ID, order.编号);
					startActivityForResult(intent, REQUEST_CODE_UPDATE_ORDER);
				}
			}
		});

		headerView.setOnButtonClickListener(new OnButtonClickListener() {

			@Override
			public void onClickSaveOrAdd() {
				mAddClientDialog.show();
			}

			@Override
			public void onClickFilter() {

			}

			@Override
			public void onClickBack() {
				finish();
			}
		});

		mAddClientDialog
				.setOnSaveSuccessedListener(new OnSaveSuccessedListener() {

					@Override
					public void onSaved(Rad客户 client) {
						createOrder(client.名称, client.地址, client.电话);
					}

					@Override
					public void onErro() {

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
			mOrders.clear();
			mListViewHelperNet.setNotifyDataSetChanged();
			// mListViewHelperKjx.loadServerData(true);
			mListViewHelperNet.loadServerData(true);
		}
	}

	/**
	 * 创建一个预算订单
	 * 
	 * @param clientId
	 *            客户编号
	 */
	private void createOrder(String clientName, String address, String phoneNo) {
		ProgressDialogHelper.show(mContext, "初始化订单..");
        String url = com.cedarhd.helpers.Global.BASE_URL + "slt/createOrder/"
                + clientName + "/" + address + "/" + phoneNo;
		StringRequest.getAsyn(url, new StringResponseCallBack() {

			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
				showShortToast("初始化订单失败");
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				reload();
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
				showShortToast("初始化订单失败");
			}
		});
	}

	/***
	 * 获取订单列表适配器
	 * 
	 * @return
	 */
	private CommanAdapter<Slt订单> getOrderAdapter() {
		return new CommanAdapter<Slt订单>(mOrders, mContext,
				R.layout.item_rad_caculate_list) {
			@Override
			public void convert(final int position, final Slt订单 item,
					BoeryunViewHolder viewHolder) {
				TextView tvTime = viewHolder
						.getView(R.id.tv_time_rad_caculate_item);
				TextView tvClient = viewHolder
						.getView(R.id.tv_client_rad_caculate_item);
				TextView tvTotal = viewHolder
						.getView(R.id.tv_total_rad_caculate_item);
				TextView tvUser = viewHolder
						.getView(R.id.tv_user_rad_caculate_item);

				tvClient.setText(item.终端客户名称 + "\n电话:" + item.终端客户联系方式
						+ "\n地址：" + item.终端客户地址);
				tvTotal.setText("合计：" + item.金额合计);
				tvUser.setText("店员："
						+ mDictionaryHelper.getUserNameById(item.终端员工));
				tvTime.setText(DateDeserializer.getFormatTime(item.订货日期));
			}
		};
	}
}
