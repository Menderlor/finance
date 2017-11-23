package com.cedarhd.slt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
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
import com.cedarhd.control.listview.ListViewHelperNet.LoadSuccessListsener;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.DictIosPicker;
import com.cedarhd.helpers.DictIosPicker.OnSelectedListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.slt.Slt房间明细;
import com.cedarhd.models.slt.Slt订单明细;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * 森盟房间列表
 *
 * K 2015-11-20
 */
public class RadRoomListActivity extends BaseActivity {
	public static String ORDER_ID = "order_id";

	/** 修改房间信息 */
	public static final int REQUEST_CODE_UPDATE_ROOM = 0x03;

	public static final String TAG_ROOM_INFO = "roomInfo";

	/** 订单编号 */
	private int mOrderId;
	private Context mContext;
	private DictIosPicker mDictIosPicker;

	private BoeryunHeaderView headerView;
	private PullToRefreshListView lv;
	private MyProgressBar pbar;
	private TextView tvTotal;
	private TextView tvCount;
	private ImageView ivSummit;

	private Demand demand;
	private QueryDemand queryDemand;
	private CommanAdapter<Slt房间明细> mAdapter;
	private ListViewHelperNet<Slt房间明细> mListViewHelperNet;
	private List<Slt房间明细> mRooms;
	private int deleteAtPos;

	/** 点击编辑修改数据源pos,如果是新建则 为-1 */
	private int mOpentAtPos; //

	/** 组数 */
	private int mCount;

	/** 合计 */
	private double mTotal;

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case REQUEST_CODE_UPDATE_ROOM:
					if (data != null) {
						Slt房间明细 roomInfo = (Slt房间明细) data.getExtras()
								.getSerializable(TAG_ROOM_INFO);
						if (mRooms.size() > 0 && mOpentAtPos >= 0) {
							mRooms.remove(mOpentAtPos);
							mRooms.add(mOpentAtPos, roomInfo);
						} else {
							mRooms.add(roomInfo);
						}
						mListViewHelperNet.mDataList = mRooms;
						mAdapter.notifyDataSetChanged();
						initTotal();
					}
					break;
				default:
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rad_room_list);
		initViews();

		initData();
		reload();
	}

	@Override
	protected void onStart() {
		super.onStart();
		setOnTouchListener();
	}

	private void initData() {
		mContext = RadRoomListActivity.this;
		mOrderId = getIntent().getIntExtra(ORDER_ID, 0);
		mDictIosPicker = new DictIosPicker(mContext);
		mRooms = new ArrayList<Slt房间明细>();
		initListViewData(mOrderId);
	}

	private void initListViewData(int orderId) {
		queryDemand = new QueryDemand();
		demand = new Demand();
		demand.表名 = "";
		// demand.方法名 = "查询_分页";
		demand.方法名 = "slt/getRoomList/" + orderId;
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 10;
		demand.偏移量 = 0;
		queryDemand.fildName = "编号";
		queryDemand.sortFildName = "编号";
		mAdapter = getOrderAdapter();
		lv.setAdapter(mAdapter);
		mListViewHelperNet = new ListViewHelperNet<Slt房间明细>(this,
				Slt房间明细.class, demand, lv, mRooms, mAdapter, pbar, queryDemand);
	}

	private void initViews() {
		lv = (PullToRefreshListView) findViewById(R.id.lv_comman_old_style);
		pbar = (MyProgressBar) findViewById(R.id.progress_comman_old_style);
		headerView = (BoeryunHeaderView) findViewById(R.id.header_rad_room_list);
		tvCount = (TextView) findViewById(R.id.tv_count_rad_room_list);
		tvTotal = (TextView) findViewById(R.id.tv_total_rad_room_list);
		ivSummit = (ImageView) findViewById(R.id.iv_save_rad_room_list);

	}

	private void setOnTouchListener() {

		lv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				lv.onRefreshComplete();
			}
		});

		headerView.setOnButtonClickListener(new OnButtonClickListener() {

			@Override
			public void onClickSaveOrAdd() {
				Intent intent = new Intent(mContext, RadRoomInfoActivity.class);
				intent.putExtra(RadRoomInfoActivity.TAG_ORDER_ID, mOrderId);
				startActivityForResult(intent, REQUEST_CODE_UPDATE_ROOM);
				mOpentAtPos = -1; //
			}

			@Override
			public void onClickFilter() {

			}

			@Override
			public void onClickBack() {
				finish();
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				mOpentAtPos = position - lv.getHeaderViewsCount();
				if (mOpentAtPos < mRooms.size() && mOpentAtPos >= 0) {
					Slt房间明细 item房间明细 = mRooms.get(mOpentAtPos);
					Intent intent = new Intent(mContext,
							RadRoomInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(RadRoomInfoActivity.TAG_ROOM_INFO,
							item房间明细);
					intent.putExtra(RadRoomInfoActivity.TAG_ORDER_ID, mOrderId);
					intent.putExtras(bundle);
					startActivityForResult(intent, REQUEST_CODE_UPDATE_ROOM);
				}
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
										   int position, long id) {

				int pos = position - lv.getHeaderViewsCount();
				if (pos >= 0 && pos < mRooms.size()) {
					final Slt房间明细 room = mRooms.get(pos);
					mDictIosPicker.show(R.id.root_rad_room_list,
							new String[] { "删除房间 " + room.名称 });
					mDictIosPicker
							.setOnSelectedListener(new OnSelectedListener() {
								@Override
								public void onSelected(int index) {
									switch (index) {
										case 0:
											mRooms.remove(room);
											mAdapter.notifyDataSetChanged();
											deleteRoom(room);
											break;
									}
								}
							});
				}
				return true;
			}
		});

		ivSummit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isChecked()) {
					submitOrder();
				}
			}
		});

		mListViewHelperNet
				.setOnSuccessListsener(new LoadSuccessListsener<Slt房间明细>() {
					@Override
					public void onLoad(List<Slt房间明细> list) {
						initTotal();
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
			mRooms.clear();
			mListViewHelperNet.setNotifyDataSetChanged();
			mListViewHelperNet.loadServerData(true);
		}
	}

	/***
	 * 获取订单列表适配器
	 *
	 * @return
	 */
	private CommanAdapter<Slt房间明细> getOrderAdapter() {
		return new CommanAdapter<Slt房间明细>(mRooms, mContext,
				R.layout.item_rad_room_list) {
			@Override
			public void convert(final int position, final Slt房间明细 item,
								BoeryunViewHolder viewHolder) {
				TextView tvRoom = viewHolder
						.getView(R.id.tv_room_rad_room_item);
				TextView tvProduct = viewHolder
						.getView(R.id.tv_product_rad_room_item);
				TextView tvPrice = viewHolder
						.getView(R.id.tv_total_rad_room_item);
				try {
					String productInfo = "未选择";
					tvProduct.setText(productInfo + "");
					String roomInfo = item.名称 + " " + item.朝向 + " " + item.长
							+ "*" + item.宽 + " = " + item.面积 + "㎡";
					tvRoom.setText(roomInfo);
					Slt订单明细 detail = item.订单明细详情;
					if (detail != null) {
						productInfo = StrUtils.pareseNull(detail.型号名称);
						tvPrice.setText("￥" + detail.金额小计);
						tvProduct.setText(productInfo + "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	/***
	 * 初始化统计合计
	 */
	private void initTotal() {
		mTotal = 0;
		mCount = 0;
		if (mRooms != null) {
			for (Slt房间明细 room : mRooms) {
				if (room.订单明细详情 != null) {
					mTotal += room.订单明细详情.金额小计;
					mCount += room.订单明细详情.数量;
				}
			}
		}
		tvCount.setText("组数：" + mCount);
		tvTotal.setText("合计：" + mTotal);
	}

	private boolean isChecked() {
		if (mRooms != null && mRooms.size() > 0) {
			for (Slt房间明细 room : mRooms) {
				if (room.订单明细详情 == null || room.订单明细详情.型号 == 0) {
					showShortToast("还没有为" + room.名称 + "选择商品");
					return false;
				}
			}
		} else {
			showShortToast("还没有创建房间");
			return false;
		}
		return true;
	}

	/**
	 * 删除房间，默认会移除对应的订单明细
	 *
	 * @param room
	 */
	private void deleteRoom(Slt房间明细 room) {

		String url = Global.BASE_URL + "slt/removeRoom";
		StringRequest.postAsyn(url, room, new StringResponseCallBack() {

			@Override
			public void onResponseCodeErro(String result) {
				showShortToast("删除失败");
			}

			@Override
			public void onResponse(String response) {
				showShortToast("删除成功");
				initTotal();
				setResult(RESULT_OK);
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				showShortToast("删除失败");
			}
		});
	}

	/***
	 * 下单
	 */
	private void submitOrder() {
		ProgressDialogHelper.show(mContext);
		String url = Global.BASE_URL + "slt/submitOrderById/" + mOrderId;
		StringRequest.getAsyn(url, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
				showShortToast("下单失败");
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				showShortToast("下单成功");
				setResult(RESULT_OK);
				finish();
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				showShortToast("服务器访问异常");
				ProgressDialogHelper.dismiss();
			}
		});
	}
}
