package com.cedarhd;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.models.changhui.ProfitPost;
import com.cedarhd.models.changhui.收益详情;
import com.cedarhd.utils.DateTimeUtil;
import com.cedarhd.utils.DoubleUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import java.util.List;

/**
 * 收益详情
 * 
 * @author Win
 * 
 */
public class ProfitInfoActivity extends BaseActivity {

	private Context context;

	private BoeryunHeaderView headerView;

	private ListView lv;

	private List<收益详情> profitList;

	private int contactId = -1;// 合同编号

	private CommanAdapter<收益详情> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profit_info);
		initData();
		findViews();
		setOnEvent();

	}

	private void initData() {
		context = getBaseContext();

		ProfitPost post = null;

		if (getIntent().hasExtra("profitInfo")) {
			contactId = getIntent().getIntExtra("profitInfo", -1);
			post = new ProfitPost();
			post.setContractId(contactId);
		}

		if (post != null) {
			String url = Global.BASE_URL + "Wealth/GetContractProfit";

			StringRequest.postAsyn(url, post, new StringResponseCallBack() {

				@Override
				public void onResponseCodeErro(String result) {
					showShortToast("网络不给力，请稍后再试");
				}

				@Override
				public void onResponse(String response) {
					response = response.substring(0, 34) + response.substring(85, response.length() - 2);
					LogUtils.i(TAG, response);
					profitList = JsonUtils.ConvertJsonToList(response,
							收益详情.class);
					adapter = getAdapter(profitList);
					lv.setAdapter(adapter);
				}

				@Override
				public void onFailure(Request request, Exception ex) {

				}
			});
		}

	}

	private void findViews() {
		headerView = (BoeryunHeaderView) findViewById(R.id.header_profit_info);
		lv = (ListView) findViewById(R.id.lv_profit_info);
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

	private CommanAdapter<收益详情> getAdapter(List<收益详情> list) {
		return new CommanAdapter<收益详情>(list, context, R.layout.item_profit_info) {

			@Override
			public void convert(int position, 收益详情 item,
					BoeryunViewHolder viewHolder) {
				TextView tv_qixi = viewHolder.getView(R.id.tv_item_profit_qixi);
				TextView tv_zhixi = viewHolder
						.getView(R.id.tv_item_profit_zhixi);
				TextView tv_day_fuxi = viewHolder
						.getView(R.id.tv_item_profit_day_fuxi);
				TextView tv_money_fuxi = viewHolder
						.getView(R.id.tv_item_profit_money_fuxi);
				TextView tv_return_benjin = viewHolder
						.getView(R.id.tv_item_profit_return_benjin);
				TextView tv_total_benjin = viewHolder
						.getView(R.id.tv_item_profit_total_benxi);
				TextView tv_status = viewHolder
						.getView(R.id.tv_item_profit_status);
				LinearLayout ll_benjin = viewHolder.getView(R.id.ll_benjin);
				
				tv_qixi.setText(DateTimeUtil.ConvertDateToString(item.本次起息日));
				tv_zhixi.setText(DateTimeUtil.ConvertDateToString(item.本次起息日));
				tv_day_fuxi.setText(item.付息天数 + "");
				tv_money_fuxi.setText(DoubleUtils.formatFloatNumber(item.付息金额));
				tv_return_benjin.setText(DoubleUtils.formatFloatNumber(item.本金返还));
				tv_total_benjin.setText(DoubleUtils.formatFloatNumber(item.本息合计));
				tv_status.setText(item.StatusName);

				if (item.赎回状态 == 2) {
					ll_benjin.setVisibility(View.VISIBLE);
				} else {
					ll_benjin.setVisibility(View.GONE);
				}

			}
		};
	}
}
