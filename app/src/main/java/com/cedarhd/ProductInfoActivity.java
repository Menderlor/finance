package com.cedarhd;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.control.listview.BoeryunNoScrollListView;
import com.cedarhd.helpers.Global;
import com.cedarhd.models.changhui.理财产品详情;
import com.cedarhd.models.changhui.预期年化收益率;
import com.cedarhd.utils.DoubleUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import java.util.List;

/**
 * 产品详情页面
 * 
 * @author Win
 * 
 */
public class ProductInfoActivity extends BaseActivity {
	private Context context;

	private CommanAdapter<预期年化收益率> adapter;

	private 理财产品详情 productInfo;

	private int productId = -1;// 产品ID

	private ImageView iv_back;
	private TextView jijinmingcheng;
	private TextView jijinjiancheng;
	private TextView jijinguimo;
	private TextView chanpinqixian;
	private TextView jijinguanliren;
	private TextView jijinleixing;
	private TextView huankuanlaiyuan;
	private TextView rengouqidain;
	private TextView jijinfeiyong;
	private TextView jijinfeiyongbili;
	private TextView chanpinjingli;
	private TextView falvguwen;
	private TextView tuoguanyinhang;
	private BoeryunNoScrollListView lv;
	private TextView jijinyongtu;
	private TextView jiegoufenji;
	private TextView touzifanwei;
	private TextView zengxincuoshi;
	private TextView jijinxingshi;
	private TextView fenpeifangshi;
	private TextView jixifangshi;
	private TextView touzixianzhi;
	private TextView touzicelv;
	private TextView beizhu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_info);
		initData();
		findViews();

	}

	private void initData() {
		context = getBaseContext();
		if (getIntent().hasExtra("productInfo")) {
			productId = getIntent().getIntExtra("productInfo", -1);
		}

		if (productId != -1) {
			String url = Global.BASE_URL + "Wealth/GetProductById/" + productId;

			StringRequest.getAsyn(url, new StringResponseCallBack() {

				@Override
				public void onResponseCodeErro(String result) {

				}

				@Override
				public void onResponse(String response) {
					LogUtils.i(TAG, response);
					productInfo = JsonUtils.ConvertJsonObject(response,
							理财产品详情.class);
					init();
				}

				@Override
				public void onFailure(Request request, Exception ex) {

				}
			});
		}
	}

	private void findViews() {
		iv_back = (ImageView) findViewById(R.id.iv_back_add_xiansuo);
		jijinmingcheng = (TextView) findViewById(R.id.product_info_jijin_mingcheng);
		jijinjiancheng = (TextView) findViewById(R.id.product_info_jijin_jiancheng);
		jijinguimo = (TextView) findViewById(R.id.product_info_jijin_guimo);
		chanpinqixian = (TextView) findViewById(R.id.product_info_chanpin_qixian);
		jijinguanliren = (TextView) findViewById(R.id.product_info_jijin_guanliren);
		jijinleixing = (TextView) findViewById(R.id.product_info_jijin_leixing);
		huankuanlaiyuan = (TextView) findViewById(R.id.product_info_huankuan_laiyuan);
		rengouqidain = (TextView) findViewById(R.id.product_info_rengou_qidian);
		jijinfeiyong = (TextView) findViewById(R.id.product_info_jijin_feiyong);
		jijinfeiyongbili = (TextView) findViewById(R.id.product_info_jijin_feiyongbili);
		chanpinjingli = (TextView) findViewById(R.id.product_info_chanpin_jingli);
		falvguwen = (TextView) findViewById(R.id.product_info_falvguwen);
		tuoguanyinhang = (TextView) findViewById(R.id.product_info_tuoguanyinhang);
		lv = (BoeryunNoScrollListView) findViewById(R.id.product_info_lv);
		jijinyongtu = (TextView) findViewById(R.id.product_info_jijin_yongtu);
		jiegoufenji = (TextView) findViewById(R.id.product_info_jiegou_fenji);
		touzifanwei = (TextView) findViewById(R.id.product_info_touzi_fanwei);
		zengxincuoshi = (TextView) findViewById(R.id.product_info_zengxin_cuoshi);
		jijinxingshi = (TextView) findViewById(R.id.product_info_jijin_xingshi);
		fenpeifangshi = (TextView) findViewById(R.id.product_info_fenpei_fangshi);
		jixifangshi = (TextView) findViewById(R.id.product_info_jixi_fangshi);
		touzixianzhi = (TextView) findViewById(R.id.product_info_touzi_xianzhi);
		touzicelv = (TextView) findViewById(R.id.product_info_touzi_celv);
		beizhu = (TextView) findViewById(R.id.product_info_beizhu);

		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

	}

	private void init() {
		jijinmingcheng.setText(productInfo.产品名称);
		jijinjiancheng.setText(productInfo.产品简称);
		jijinguimo.setText(productInfo.产品规模);
		chanpinqixian.setText(productInfo.产品期限描述);
		jijinguanliren.setText(productInfo.产品管理人);
		jijinleixing.setText(productInfo.产品类型);
		huankuanlaiyuan.setText(productInfo.还款来源);
		rengouqidain.setText(productInfo.认购起点描述);
		jijinfeiyong.setText(productInfo.产品费用);
		jijinfeiyongbili.setText(productInfo.产品费用比例);
		chanpinjingli.setText(productInfo.产品经理);
		falvguwen.setText(productInfo.法律顾问);
		tuoguanyinhang.setText(productInfo.托管银行);

		jijinyongtu.setText(productInfo.产品用途);
		jiegoufenji.setText(productInfo.投资领域);
		zengxincuoshi.setText(productInfo.增信措施);
		jijinxingshi.setText(productInfo.基金形式);
		fenpeifangshi.setText(productInfo.收益分配规则);
		jixifangshi.setText(productInfo.清算分配规则);
		touzixianzhi.setText(productInfo.投资限制);
		touzicelv.setText(productInfo.投资策略);
		beizhu.setText(productInfo.备注);

		adapter = getAdapter(productInfo.YieldList);
		lv.setAdapter(adapter);
	}

	private CommanAdapter<预期年化收益率> getAdapter(List<预期年化收益率> list) {
		return new CommanAdapter<预期年化收益率>(list, context,
				R.layout.item_product_profit_rate) {

			@Override
			public void convert(int position, 预期年化收益率 item,
					BoeryunViewHolder viewHolder) {
				viewHolder.setTextValue(R.id.tv_item_product_qixian,
						DoubleUtils.formatFloatNumberString(item.投资期限));

				if (item.最大投资额 <= 0) {
					viewHolder.setTextValue(R.id.tv_item_product_touzie, "X>="
							+ DoubleUtils.formatFloatNumberString(item.最小投资额 / 10000));
				} else {
					
					viewHolder.setTextValue(R.id.tv_item_product_touzie,
							DoubleUtils.formatFloatNumberString(item.最小投资额 / 10000) + "<=X<" + DoubleUtils.formatFloatNumberString(item.最大投资额 / 10000));
				}
				viewHolder.setTextValue(R.id.tv_item_product_jizhun, item.收益率
						+ "");

				viewHolder.setTextValue(R.id.tv_item_product_style, item.分类
						+ "");
			}
		};
	}

}
