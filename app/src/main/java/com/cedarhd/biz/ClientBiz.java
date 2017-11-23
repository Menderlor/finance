package com.cedarhd.biz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.cedarhd.ClientListActivity;
import com.cedarhd.changhui.ChClientListActivity;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.models.Client;
import com.cedarhd.utils.LogUtils;

/***
 * 客户相关的帮助类
 * 
 * @author K 2015-9-23
 * 
 */
public class ClientBiz {

	public static final int SELECT_CLIENT_CODE = 1006; // 选择客户名称

	public static final String ClientId = "id";
	public static final String ClientName = "clientName";

	public static final String EXTRA_CLIENT_OBJECT = "clientobject";

	/***
	 * 从客户列表选择客户
	 * 
	 * 通过 {@link startActivityForResult} 打开客户列表，请求结果码 为
	 * ClientBiz.SELECT_CLIENT_CODE
	 * 
	 * @param context
	 */
	public static void selectClient(Context context) {
		Intent intent = new Intent(context, ClientListActivity.class);
		intent.putExtra(ClientListActivity.SELECT_CLIENT, true);
		((Activity) context).startActivityForResult(intent, SELECT_CLIENT_CODE);
	}

	/***
	 * 从客户列表选择客户
	 * 
	 * 通过 {@link startActivityForResult} 打开客户列表，请求结果码 为
	 * ClientBiz.SELECT_CLIENT_CODE
	 * 
	 * @param context
	 */
	public static void selectClient(Fragment fragment) {
		Intent intent = new Intent(fragment.getActivity(),
				ClientListActivity.class);
		intent.putExtra(ClientListActivity.SELECT_CLIENT, true);
		fragment.startActivityForResult(intent, SELECT_CLIENT_CODE);
	}

	/****
	 * 选中客户完毕，显示客户
	 * 
	 * @param context
	 * @param requestCode
	 * @param data
	 * @return
	 */
	public static Client onActivityGetClient(Context context, int requestCode,
			Intent data) {
		Client client = null;
		if (requestCode == ClientBiz.SELECT_CLIENT_CODE && data != null) {
			Bundle bundle = data.getExtras();
			int clientId = bundle.getInt(ClientListActivity.ClientId);
			client = (Client) bundle.getSerializable(EXTRA_CLIENT_OBJECT);
			DictionaryHelper dictionaryHelper = new DictionaryHelper(context);
			LogUtils.i("onActivityGetClient", "clientId:" + clientId);

			if (client == null && clientId != 0) {
				client = dictionaryHelper.getClientById(clientId);
			}
		}
		return client;
	}

	/****
	 * 选中客户完毕，显示客户
	 * 
	 * @param context
	 * @param requestCode
	 *            请求码
	 * @param data
	 * @return
	 */
	public static Client onActivityGetClient(int requestCode, Intent data) {
		Client client = new Client();
		if (requestCode == ClientBiz.SELECT_CLIENT_CODE && data != null) {
			Bundle bundle = data.getExtras();
			int clientId = bundle.getInt(ClientListActivity.ClientId);
			String clientName = bundle.getString(ClientListActivity.ClientName);
			if (clientId != 0) {
				client.Id = clientId;
				client.CustomerName = clientName;
			}
		}
		return client;
	}

	/***
	 * 从客户列表选择长汇客户
	 * 
	 * 通过 {@link startActivityForResult} 打开客户列表，请求结果码 为
	 * ClientBiz.SELECT_CLIENT_CODE
	 * 
	 * @param context
	 */
	public static void selectClient_Changhui(Context context) {
		Intent intent = new Intent(context, ChClientListActivity.class);
		intent.putExtra(ChClientListActivity.EXTRA_SELECT_CLIENT, true);
		((Activity) context).startActivityForResult(intent, SELECT_CLIENT_CODE);
	}

	/***
	 * 从客户列表选择长汇客户
	 * 
	 * 通过 {@link startActivityForResult} 打开客户列表，请求结果码 为
	 * ClientBiz.SELECT_CLIENT_CODE
	 * 
	 * @param context
	 * @param isSelectMyClient
	 *            是否只查看自己的客户
	 */
	public static void selectClient_Changhui(Context context,
			boolean isSelectMyClient) {
		selectClient_Changhui(context, isSelectMyClient, "");
	}

	/***
	 * 从客户列表选择长汇客户
	 * 
	 * 通过 {@link startActivityForResult} 打开客户列表，请求结果码 为
	 * ClientBiz.SELECT_CLIENT_CODE
	 * 
	 * @param context
	 * @param isSelectMyClient
	 *            是否只查看自己的客户
	 */
	public static void selectClient_Changhui(Context context,
			boolean isSelectMyClient, String moreFilter) {
		Intent intent = new Intent(context, ChClientListActivity.class);
		intent.putExtra(ChClientListActivity.EXTRA_SELECT_CLIENT, true);
		intent.putExtra(ChClientListActivity.EXTRA_SELECT_MY_CLIENT,
				isSelectMyClient);
		if (!TextUtils.isEmpty(moreFilter)) {
			intent.putExtra(ChClientListActivity.EXTRA_QUERY_FILTER, moreFilter);
		}
		((Activity) context).startActivityForResult(intent, SELECT_CLIENT_CODE);
	}
}
