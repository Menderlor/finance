package com.cedarhd.biz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.cedarhd.changhui.ChProductListActivity;
import com.cedarhd.models.changhui.理财产品;

/***
 * 客户相关的帮助类
 * 
 * @author K 2015-9-23
 * 
 */
public class ChProductBiz {

	public static final int SELECT_PRODUCT_CODE = 116;

	/***
	 * 从客户列表选择客户
	 * 
	 * 通过 {@link startActivityForResult} 打开客户列表，请求结果码 为
	 * ClientBiz.SELECT_CLIENT_CODE
	 * 
	 * @param context
	 */
	public static void selectProduct(Fragment fragment) {
		Intent intent = new Intent(fragment.getActivity(),
				ChProductListActivity.class);
		intent.putExtra(ChProductListActivity.EXTRAS_IS_SELECT, true);
		fragment.startActivityForResult(intent, SELECT_PRODUCT_CODE);
	}

	/****
	 * 选中理财产品
	 * 
	 * @param context
	 * @param requestCode
	 *            请求码
	 * @param data
	 * @return
	 */
	public static 理财产品 onActivityGetClient(int requestCode, Intent data) {
		if (requestCode == SELECT_PRODUCT_CODE && data != null) {
			Bundle bundle = data.getExtras();
			return (理财产品) bundle
					.getSerializable(ChProductListActivity.EXTRAS_SELECT_PRODUCT);
		}
		return null;
	}

}
