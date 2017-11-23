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
import com.cedarhd.control.listview.BoeryunNoScrollListView;
import com.cedarhd.models.changhui.理财产品购买合同收益;
import com.cedarhd.models.changhui.理财产品购买合同收益明细;
import com.cedarhd.utils.DoubleUtils;

import java.util.List;

/**
 * 收益计算器--根据输入的信息查看收益信息
 * 
 * @author Win
 * 
 */
public class ProfitCaculatorInfoActivity extends BaseActivity {
	
	private Context context;
	private 理财产品购买合同收益 profit ;
	private CommanAdapter<理财产品购买合同收益明细> adapter;
	
	private ImageView iv_back;
	
	private BoeryunNoScrollListView lv;
	private TextView tv_product_name;
	private TextView tv_touzi_jine;
	private TextView tv_chanpin_leixing;
	private TextView tv_touzi_qixian;
	private TextView tv_dakuan_riqi;
	private TextView tv_shouyi_qixiri;
	private TextView tv_touziqi_kaishi;
	private TextView tv_touziqi_jieshu;
	private TextView tv_yewu_jizhun;
	private LinearLayout ll_contract;
	private LinearLayout ll_client;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profit_caculator_info);
		findViews();
		initData();
		
	}
	
	
	private void findViews() {
		iv_back = (ImageView) findViewById(R.id.iv_cancel_calculator);
		tv_product_name = (TextView) findViewById(R.id.tv_caculator_info_product_name);
		tv_touzi_jine = (TextView) findViewById(R.id.tv_caculator_info_touzi_jine);
		tv_touzi_qixian = (TextView) findViewById(R.id.tv_caculator_info_touzi_qixian);
		tv_dakuan_riqi = (TextView) findViewById(R.id.tv_caculator_info_dakuan_riqi);
		tv_shouyi_qixiri = (TextView) findViewById(R.id.tv_caculator_info_shouyi_qixiri);
		tv_touziqi_kaishi = (TextView) findViewById(R.id.tv_caculator_info_touziqi_kaishi);
		tv_touziqi_jieshu = (TextView) findViewById(R.id.tv_caculator_info_touziqi_jieshu);
		tv_yewu_jizhun = (TextView) findViewById(R.id.tv_caculator_info_yewu_jizhun);
		lv = (BoeryunNoScrollListView) findViewById(R.id.lv_caculator_info_product_info);
		ll_contract = (LinearLayout) findViewById(R.id.ll_profit_contract_id);
		ll_client = (LinearLayout) findViewById(R.id.ll_profit_client_name);
		tv_chanpin_leixing = (TextView) findViewById(R.id.tv_caculator_info_product_style);
		
		ll_contract.setVisibility(View.GONE);
		ll_client.setVisibility(View.GONE);
		
		iv_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private void initData() {
		context = getBaseContext();
		if (getIntent().hasExtra("caculateProfit")) {
			Bundle bundle = getIntent().getBundleExtra("caculateProfit");
			if (bundle != null) {
				profit = (理财产品购买合同收益) bundle.get("caculateProfitInfo");
				if(profit != null) {
					tv_product_name.setText(profit.理财产品名称);
					tv_touzi_jine.setText(DoubleUtils.formatFloatNumber(profit.投资金额));
					tv_touzi_qixian.setText(profit.投资期限 + "");
					tv_dakuan_riqi.setText(profit.打款日期.substring(0, 10));
					tv_shouyi_qixiri.setText(profit.收益起息日.substring(0, 10));
					tv_touziqi_kaishi.setText(profit.投资期开始.substring(0, 10));
					tv_touziqi_jieshu.setText(profit.投资期结束.substring(0, 10));
					tv_yewu_jizhun.setText(profit.收益率 + "%");
					tv_chanpin_leixing.setText(profit.收益率分类);
					adapter = getAdapter(profit.收益明细);
					lv.setAdapter(adapter);
				}
			}
		}
	}
	
	private CommanAdapter<理财产品购买合同收益明细> getAdapter(List<理财产品购买合同收益明细> list) {
		return new CommanAdapter<理财产品购买合同收益明细>(list, context, R.layout.item_caculator_info_profit) {
			
			@Override
			public void convert(int position, 理财产品购买合同收益明细 item,
					BoeryunViewHolder viewHolder) {
				viewHolder.setTextValue(R.id.tv_item_profit_info_qici, item.序号+ "");
				viewHolder.setTextValue(R.id.tv_item_profit_info_zhixiri, item.止息日.substring(0, 10));
				viewHolder.setTextValue(R.id.tv_item_profit_info_fuxi_tianshu, item.付息天数+ "");
				viewHolder.setTextValue(R.id.tv_item_profit_info_fuxi_jine,DoubleUtils.formatFloatNumber(item.付息金额));
				viewHolder.setTextValue(R.id.tv_item_profit_info_benjin_fanhuan, DoubleUtils.formatFloatNumber(item.本金返还));
				viewHolder.setTextValue(R.id.tv_item_profit_info_heji, DoubleUtils.formatFloatNumber(item.本息合计));
			}
		};
	}
}
