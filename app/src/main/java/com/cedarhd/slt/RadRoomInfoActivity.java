package com.cedarhd.slt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.control.BoeryunSelectCountView;
import com.cedarhd.control.BoeryunSelectCountView.OnNumChanged;
import com.cedarhd.helpers.DictIosPicker;
import com.cedarhd.helpers.DictWheelPicker;
import com.cedarhd.helpers.DictionaryQueryDialogHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.rad.Rad商品;
import com.cedarhd.models.slt.Slt型号;
import com.cedarhd.models.slt.Slt房间明细;
import com.cedarhd.models.slt.Slt订单明细;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;

/***
 * 房间详情
 * 
 * @author 阚健雄 2015-11-20
 * 
 */
public class RadRoomInfoActivity extends BaseActivity {

	public static final String TAG_ROOM_INFO = "roomInfo";

	/** 订单编号 */
	public static final String TAG_ORDER_ID = "orderId";

	/**
	 * 选择产品
	 */
	private final int CODE_REQUEST_SELECT_PRODUCT = 0x01;

	private Context mContext;
	/** 选中的下单商品 */
	private Rad商品 mSelectedProduct;

	/** 选中下单的数量 */
	private int mSelectedCount = 1;

	private Slt房间明细 mRoom;
	private Slt型号 mProduct;

	private DictIosPicker mDictIosPicker;
	private DictWheelPicker mDictWheelPicker;
	private DictionaryQueryDialogHelper dictionaryQueryDialogHelper;

	private BoeryunHeaderView headerView;
	private TextView tvRoomName;
	private TextView tvRoomDirection;
	private TextView tvRoomLength;
	private TextView tvRoomWidth;
	private TextView tvRoomArea;

	private TextView tvChoice;
	private TextView tvProduct;
	private TextView tvHotSummary;
	private TextView tvPrice;
	private TextView tvCount;
	private TextView tvTotal;

	private BoeryunSelectCountView selectCountView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rad_room_info);
		init();
		initViews();
		setOnEvent();
		showData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CODE_REQUEST_SELECT_PRODUCT:
				if (data != null && data.getExtras() != null) {
					mProduct = (Slt型号) data.getExtras().getSerializable(
							SltProductTypeListActivity.TYPE_INFO);
					mRoom.订单明细详情.型号 = mProduct.编号;
					mRoom.订单明细详情.型号名称 = mProduct.名称;
					mRoom.订单明细详情.单价 = mProduct.单价;
					mRoom.订单明细详情.中心距 = mProduct.中心距;
					mRoom.订单明细详情.主管规格 = mProduct.主管规格;
					mRoom.订单明细详情.图号 = mProduct.图号;
					mRoom.订单明细详情.排档方式 = mProduct.排档方式;
					mRoom.订单明细详情.接口口径 = mProduct.接口口径;
					mRoom.订单明细详情.是否防腐 = mProduct.是否防腐;
					mRoom.订单明细详情.颜色 = mProduct.颜色;
					mRoom.订单明细详情.主管规格 = mProduct.主管规格;
					mRoom.订单明细详情.片数 = selectCountView.getNum();
					mRoom.订单明细详情.数量 = 1;
					mRoom.订单明细详情.每组价格 = mRoom.订单明细详情.单价
							* selectCountView.getNum();
					mRoom.订单明细详情.片数小计 = 1 * selectCountView.getNum();
					mRoom.订单明细详情.金额小计 = mRoom.订单明细详情.单价 * mRoom.订单明细详情.片数小计;

					tvChoice.setText("选中:" + mProduct.名称);
					tvProduct.setText("图号：" + StrUtils.pareseNull(mProduct.图号)
							+ "\n" + "系列：" + StrUtils.pareseNull(mProduct.系列名称)
							+ "\n" + "颜色：" + StrUtils.pareseNull(mProduct.颜色名称)
							+ "\n" + "中心距：" + StrUtils.pareseNull(mProduct.中心距)
							+ "\n" + "进水方式："
							+ StrUtils.pareseNull(mProduct.进水方式名称) + "\n"
							+ "接口口径：" + StrUtils.pareseNull(mProduct.接口口径名称)
							+ "\n" + "挡管规格："
							+ StrUtils.pareseNull(mProduct.挡管规格) + "\n"
							+ "主管规格：" + StrUtils.pareseNull(mProduct.主管规格)
							+ "\n" + "规格高宽厚："
							+ StrUtils.pareseNull(mProduct.规格高宽厚) + "\n"
							+ "档距_头距_空位间距："
							+ StrUtils.pareseNull(mProduct.档距_头距_空位间距) + "\n"
							+ "单片热量：" + mProduct.单片热量 + " W\n");
					tvPrice.setText("单价：" + mProduct.单价);
					tvTotal.setText("合计：" + mRoom.订单明细详情.金额小计);
				}
				break;
			}
		}
	}

	private void showData() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mRoom = (Slt房间明细) bundle.getSerializable(TAG_ROOM_INFO);
		}
		int orderId = getIntent().getIntExtra(TAG_ORDER_ID, 0);

		if (mRoom == null) {
			mRoom = new Slt房间明细();
			mRoom.名称 = "主卧";
			mRoom.长 = 5;
			mRoom.宽 = 4;
			mRoom.面积 = 20;
			mRoom.朝向 = "朝南";
		}

		if (mRoom.订单明细详情 == null) {
			mRoom.订单明细详情 = new Slt订单明细();
		} else {
			if (TextUtils.isEmpty(mRoom.订单明细详情.型号名称 + "")) {
				tvChoice.setText("请选择商品型号");
			} else {
				tvChoice.setText(mRoom.订单明细详情.型号名称 + "");
				tvProduct.setText("图号：" + mRoom.订单明细详情.图号);
			}
		}

		if (mRoom.订单明细详情.订单 == 0) {
			mRoom.订单明细详情.订单 = orderId;
		}

		tvRoomName.setText("" + mRoom.名称);
		tvRoomDirection.setText("" + mRoom.朝向);
		tvRoomLength.setText("" + mRoom.长);
		tvRoomWidth.setText("" + mRoom.宽);
		tvRoomArea.setText("" + mRoom.面积);

	}

	private void init() {
		mContext = RadRoomInfoActivity.this;
		dictionaryQueryDialogHelper = DictionaryQueryDialogHelper
				.getInstance(mContext);
		mDictIosPicker = new DictIosPicker(mContext);
		mDictWheelPicker = new DictWheelPicker(mContext);

	}

	private void initViews() {
		headerView = (BoeryunHeaderView) findViewById(R.id.header_rad_room_info);
		tvRoomName = (TextView) findViewById(R.id.tv_room_name_rad_room_info);
		tvRoomDirection = (TextView) findViewById(R.id.tv_room_direction_rad_room_info);
		tvRoomLength = (TextView) findViewById(R.id.tv_room_length_rad_room_info);
		tvRoomWidth = (TextView) findViewById(R.id.tv_room_width_rad_room_info);
		tvRoomArea = (TextView) findViewById(R.id.tv_room_area_rad_room_info);

		tvChoice = (TextView) findViewById(R.id.tv_choice_rad_room_info);
		tvProduct = (TextView) findViewById(R.id.tv_product_rad_room_info);
		tvHotSummary = (TextView) findViewById(R.id.tv_hot_sum_rad_room_info);
		tvPrice = (TextView) findViewById(R.id.tv_price_rad_room_info);
		tvCount = (TextView) findViewById(R.id.tv_count_rad_room_info);
		tvTotal = (TextView) findViewById(R.id.tv_total_rad_room_info);

		selectCountView = (BoeryunSelectCountView) findViewById(R.id.select_count_pianshu_slt_room_info);
	}

	private void setOnEvent() {

		tvChoice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext,
						SltProductTypeListActivity.class);
				intent.putExtra(SltProductTypeListActivity.TYPE_SELECT, true);
				startActivityForResult(intent, CODE_REQUEST_SELECT_PRODUCT);
			}
		});

		headerView.setOnButtonClickListener(new OnButtonClickListener() {
			@Override
			public void onClickSaveOrAdd() {
				if (mRoom.订单明细详情 == null || mRoom.订单明细详情.型号 == 0) {
					showShortToast("请先选择产品型号");
					return;
				}

				mRoom.订单明细详情.每组价格 = mRoom.订单明细详情.单价 * selectCountView.getNum();
				mRoom.订单明细详情.片数小计 = 1 * selectCountView.getNum();
				mRoom.订单明细详情.金额小计 = mRoom.订单明细详情.单价 * mRoom.订单明细详情.片数小计;
				saveRoom();
			}

			@Override
			public void onClickFilter() {

			}

			@Override
			public void onClickBack() {
				finish();
			}
		});

		tvRoomName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String arr[] = { "主卧", "大次卧", "小次卧", "客厅", "书房", "厨房",
						"卫生间", "储物间", "其他" };
				mDictIosPicker.show(R.id.root_rad_room_info, arr);
				mDictIosPicker
						.setOnSelectedListener(new DictIosPicker.OnSelectedListener() {
							@Override
							public void onSelected(int index) {
								String roomName = arr[index];
								mRoom.名称 = roomName;
								tvRoomName.setText(roomName);
							}
						});
			}
		});

		tvRoomDirection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String arr[] = { "东", "南", "西", "北" };
				mDictIosPicker.show(R.id.root_rad_room_info, arr);
				mDictIosPicker
						.setOnSelectedListener(new DictIosPicker.OnSelectedListener() {
							@Override
							public void onSelected(int index) {
								String roomName = arr[index];
								mRoom.朝向 = roomName;
								tvRoomDirection.setText(roomName);
							}
						});
			}
		});

		tvRoomLength.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final List<String> datas = new ArrayList<String>();
				double distance = 0.1;
				final String format = "%1$.1f";
				for (int i = 1; i <= 90; i++) {
					datas.add(String.format(format, (1 + distance * i)));
				}
				mDictWheelPicker.showDateWheel(R.id.root_rad_room_info, datas);
				mDictWheelPicker
						.setOnSelectedListener(new DictWheelPicker.OnSelectedListener() {
							@Override
							public void onSelected(int index) {
								mRoom.长 = (int) (Double.parseDouble(datas
										.get(index)) * 10) / 10.0;
								double area = mRoom.长 * mRoom.宽;
								mRoom.面积 = area;
								tvRoomLength.setText(mRoom.长 + "");
								tvRoomArea.setText(String.format(format, area));
							}
						});
			}
		});

		tvRoomWidth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final List<String> datas = new ArrayList<String>();
				double distance = 0.1;
				final String format = "%1$.1f";
				for (int i = 1; i <= 90; i++) {
					datas.add(String.format(format, (1 + distance * i)));
				}
				mDictWheelPicker.showDateWheel(R.id.root_rad_room_info, datas);
				mDictWheelPicker
						.setOnSelectedListener(new DictWheelPicker.OnSelectedListener() {
							@Override
							public void onSelected(int index) {
								mRoom.宽 = (int) (Double.parseDouble(datas
										.get(index)) * 10) / 10.0;
								double area = mRoom.长 * mRoom.宽;
								mRoom.面积 = area;
								tvRoomWidth.setText(mRoom.宽 + "");
								tvRoomArea.setText(String.format(format, area));
							}
						});
			}
		});

		tvRoomArea.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final List<String> datas = new ArrayList<String>();
				double distance = 1;
				final String format = "%1$.1f";
				for (int i = 1; i <= 99; i++) {
					datas.add(String.format(format, (1 + distance * i)));
				}
				mDictWheelPicker.showDateWheel(R.id.root_rad_room_info, datas);
				mDictWheelPicker
						.setOnSelectedListener(new DictWheelPicker.OnSelectedListener() {
							@Override
							public void onSelected(int index) {
								double area = (Double.parseDouble(datas
										.get(index)) * 10) / 10.0;
								mRoom.面积 = (int) area;
								tvRoomArea.setText(String.format(format, area));
							}
						});

			}
		});

		// 片数选择
		selectCountView.setOnNumChangedeListener(new OnNumChanged() {
			@Override
			public void onchange(int value) {
				mRoom.订单明细详情.片数 = value;

				if (mProduct != null) {
					tvHotSummary
							.setText("累计热量：" + value * mProduct.单片热量 + " w");
				}
			}
		});
	}

	private void saveRoom() {
		ProgressDialogHelper.show(mContext, "保存中..");
		String url = Global.BASE_URL + "slt/saveRoom";
		StringRequest.postAsyn(url, mRoom, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
				showShortToast("保存失败");
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				showShortToast("保存成功");
				Intent intent = new Intent();
				Bundle data = new Bundle();
				data.putSerializable(RadRoomListActivity.TAG_ROOM_INFO, mRoom);
				intent.putExtras(data);
				setResult(RESULT_OK, intent);
				finish();
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
				showShortToast("网络连接异常");
			}
		});
	}
}
