package com.cedarhd;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.biz.DictionaryBiz;
import com.cedarhd.control.listview.BoeryunNoScrollListView;
import com.cedarhd.helpers.Global;
import com.cedarhd.models.Dict;
import com.cedarhd.models.changhui.ProfitPost;
import com.cedarhd.models.changhui.收益详情;
import com.cedarhd.models.changhui.理财产品购买合同;
import com.cedarhd.models.changhui.理财产品购买合同收益;
import com.cedarhd.utils.DoubleUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.List;

public class ProfitInfoNewActivity extends BaseActivity {

	private Context context;
	private 理财产品购买合同收益 profit;
	private CommanAdapter<收益详情> adapter;
	private List<收益详情> profitList;

	private ImageView iv_back;
	private TextView tv_tittle;

	private BoeryunNoScrollListView lv;
	private TextView tv_product_name;
	private TextView tv_touzi_jine;
	private TextView tv_touzi_qixian;
	private TextView tv_dakuan_riqi;
	private TextView tv_shouyi_qixiri;
	private TextView tv_touziqi_kaishi;
	private TextView tv_touziqi_jieshu;
	private TextView tv_yewu_jizhun;
	private TextView tv_contact_id;
	private TextView tv_client_name;
	private TextView tv_profit_style;
	private LinearLayout ll_contract;
	private LinearLayout ll_client;
	private HashMap<String, List<Dict>> dictionarys;

	private int contactId = -1;
	
	private 理财产品购买合同 mContract;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profit_caculator_info);
		findViews();
		initData();

	}

	private void findViews() {
		iv_back = (ImageView) findViewById(R.id.iv_cancel_calculator);
		tv_tittle = (TextView) findViewById(R.id.textViewTitle);
		tv_product_name = (TextView) findViewById(R.id.tv_caculator_info_product_name);
		tv_touzi_jine = (TextView) findViewById(R.id.tv_caculator_info_touzi_jine);
		tv_touzi_qixian = (TextView) findViewById(R.id.tv_caculator_info_touzi_qixian);
		tv_dakuan_riqi = (TextView) findViewById(R.id.tv_caculator_info_dakuan_riqi);
		tv_shouyi_qixiri = (TextView) findViewById(R.id.tv_caculator_info_shouyi_qixiri);
		tv_touziqi_kaishi = (TextView) findViewById(R.id.tv_caculator_info_touziqi_kaishi);
		tv_touziqi_jieshu = (TextView) findViewById(R.id.tv_caculator_info_touziqi_jieshu);
		tv_yewu_jizhun = (TextView) findViewById(R.id.tv_caculator_info_yewu_jizhun);
		tv_contact_id = (TextView) findViewById(R.id.tv_caculator_info_contract_id);
		tv_profit_style = (TextView) findViewById(R.id.tv_caculator_info_product_style);
		tv_client_name = (TextView) findViewById(R.id.tv_caculator_info_client_name);
		lv = (BoeryunNoScrollListView) findViewById(R.id.lv_caculator_info_product_info);
		ll_contract = (LinearLayout) findViewById(R.id.ll_profit_contract_id);
		ll_client = (LinearLayout) findViewById(R.id.ll_profit_client_name);
		
		tv_tittle.setText("收益详情");

		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

//	private void initData() {
//		context = getBaseContext();
//		if (getIntent().hasExtra("caculateProfit")) {
//			Bundle bundle = getIntent().getBundleExtra("caculateProfit");
//			if (bundle != null) {
//				profit = (理财产品购买合同收益) bundle.get("caculateProfitInfo");
//				if (profit != null) {
//					tv_product_name.setText(profit.理财产品名称);
//					tv_touzi_jine.setText(DoubleUtils
//							.formatFloatNumber(profit.投资金额));
//					tv_touzi_qixian.setText(profit.投资期限 + "");
//					tv_dakuan_riqi.setText(profit.打款日期.substring(0, 10));
//					tv_shouyi_qixiri.setText(profit.收益起息日.substring(0, 10));
//					tv_touziqi_kaishi.setText(profit.投资期开始.substring(0, 10));
//					tv_touziqi_jieshu.setText(profit.投资期结束.substring(0, 10));
//					tv_yewu_jizhun.setText(profit.收益率 + "%");
//					adapter = getAdapter(profit.收益明细);
//					lv.setAdapter(adapter);
//				}
//			}
//		}
//	}

	private void initData() {
		context = getBaseContext();

		ProfitPost post = null;
		

		if (getIntent().hasExtra("profitInfo")) {
			mContract = (理财产品购买合同) getIntent().getSerializableExtra("profitInfo");
			dictionarys = (HashMap<String, List<Dict>>) getIntent().getSerializableExtra("dictionarys");
			
			if(mContract != null && dictionarys!= null) {
				tv_product_name.setText(DictionaryBiz.getDictName(dictionarys, "理财产品", mContract.理财产品));
				tv_contact_id.setText(mContract.合同编号);
				tv_client_name.setText(DictionaryBiz.getDictName(dictionarys, "客户",
						mContract.客户));
				tv_touzi_jine.setText(DoubleUtils
						.formatFloatNumber(mContract.认购金额小写));
				tv_touzi_qixian.setText(mContract.投资期限 + "");
				tv_dakuan_riqi.setText(mContract.打款时间.substring(0, 10));
				tv_shouyi_qixiri.setText(mContract.投资起息日.substring(0, 10));
				tv_touziqi_kaishi.setText(mContract.投资起算日.substring(0, 10));
				tv_touziqi_jieshu.setText(mContract.投资到期日.substring(0, 10));
				tv_yewu_jizhun.setText(mContract.预期年化收益率 + "%");
				tv_profit_style.setText(mContract.预期年化收益率分类);
				
				post = new ProfitPost();
				post.setContractId(mContract.编号);
			}
			
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
					response = response.substring(0, 34)
							+ response.substring(85, response.length() - 2);
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

	private CommanAdapter<收益详情> getAdapter(List<收益详情> list) {
		return new CommanAdapter<收益详情>(list, context,
				R.layout.item_caculator_info_profit) {

			@Override
			public void convert(int position, 收益详情 item,
					BoeryunViewHolder viewHolder) {
				viewHolder.setTextValue(R.id.tv_item_profit_info_qici, position
						+ 1 + "");
				viewHolder.setTextValue(R.id.tv_item_profit_info_zhixiri,
						item.本次止息日.substring(0, 10));
				viewHolder.setTextValue(R.id.tv_item_profit_info_fuxi_tianshu,
						item.付息天数 + "");
				viewHolder.setTextValue(R.id.tv_item_profit_info_fuxi_jine,
						DoubleUtils.formatFloatNumber(item.付息金额));
				viewHolder.setTextValue(
						R.id.tv_item_profit_info_benjin_fanhuan,
						DoubleUtils.formatFloatNumber(item.本金返还));
				viewHolder.setTextValue(R.id.tv_item_profit_info_heji,
						DoubleUtils.formatFloatNumber(item.本息合计));
			}
		};
	}
}
