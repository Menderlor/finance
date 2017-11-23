package com.cedarhd;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.fragment.ApplyOutStockFragment;

/***
 * 出库单列表
 * 
 * @author K
 * 
 */
public class ApplyOutStockListActivity extends BaseActivity {

	private FragmentManager fm;
	private Context context;

	private ImageView ivBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_out_stock);
		initData();
		initViews();
		initFragment();
	}

	private void initData() {
		context = ApplyOutStockListActivity.this;
		fm = getSupportFragmentManager();
	}

	private void initViews() {

	}

	private void initFragment() {
		// 创建自定义Fragment对象
		ApplyOutStockFragment fragment = new ApplyOutStockFragment();
		// 创建Fragment事务
		FragmentTransaction transaction = fm.beginTransaction();
		// 加载fragment 1.被填充容器id; 2.fragment对象； 3.填入fragment文件的描述
		transaction.add(R.id.root_out_stock_list, fragment,
				"ApplyOutStockFragment");
		// 提交事务
		transaction.commit();
	}
}
