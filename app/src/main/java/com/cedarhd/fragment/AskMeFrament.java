package com.cedarhd.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.CreateVmFormActivity;
import com.cedarhd.R;
import com.cedarhd.adapter.FlowListViewAdapter;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.流程;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 待我审批页面
 * 
 * @author kjx
 * @since 2014/07/22
 */
@Deprecated
public class AskMeFrament extends Fragment {
	private String url = Global.BASE_URL;
	public String methodName; // 根据方法显示（区分）具体的页面内容

	private HttpUtils httpUtils;
	private PullToRefreshListView lv;
	private TextView emptyView;
	private MyProgressBar pbar;
	private FlowListViewAdapter adapter;
	private List<流程> list = new ArrayList<流程>();
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();

	public final int REQUEST_CODE_ASKFOR_ME = 12;// 待我审批

	public AskMeFrament() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.all_askform_fragment, null);
		lv = (PullToRefreshListView) view.findViewById(R.id.lv_all_askform);
		pbar = (MyProgressBar) view.findViewById(R.id.pbar_askforme);
		emptyView = (TextView) view.findViewById(R.id.tv_empty);
		adapter = new FlowListViewAdapter(getActivity(),
				R.layout.askforleavelist_item, list, null);
		lv.setAdapter(adapter);
		lv.setEmptyView(emptyView);
		init();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		url = url + methodName;
		LogUtils.i("onresume", "methodName onresume" + methodName);
		reload();
	}

	private void init() {
		httpUtils = new HttpUtils();
		// 待我审批 进入审批; 申请进入 查看申请详情
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView) parent;
				if (position > 0) {
					position -= 1;
				}
				流程 map = list.get(position);
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				// bundle.putInt("id", map.getId());
				// bundle.putInt("typeId", map.getClassTypeId());
				// bundle.putString("dataId", "" + map.getFormDataId());
				// // bundle.putString("typeName", map.getClassTypeName());
				// bundle.putString("typeName", map.getName()); // 表单名称
				// bundle.putString("columnName", "NextStepAudit");
				// bundle.putString("isPhoneForm", map.isPhoneData);
				bundle.putSerializable("flow", map);
				// 待我审批则显示审核输入框
				if (methodName.equals("Flow/GetApprovalFlow/")) {
					bundle.putBoolean("isAudit", true);
				} else {
					bundle.putBoolean("isAudit", false);
				}

				intent.putExtras(bundle);
				zlServiceHelper.ReadFlow(map, getActivity());

				LogUtils.i("isPhoneData", "" + map.isPhoneData);

				// if (!TextUtils.isEmpty(map.isPhoneData)
				// && "true".equals(map.isPhoneData.toLowerCase())) {
				//
				// } else {
				// intent.setClass(getActivity(), AuditFormActivity.class);
				// }
				// 改为全部采用XML的方式展现
				intent.setClass(getActivity(), CreateVmFormActivity.class);
				startActivityForResult(intent, REQUEST_CODE_ASKFOR_ME);
			}
		});

		lv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				reload();
			}
		});
	}

	private void reload() {
		// pbar.setVisibility(View.VISIBLE);
		boolean isConnectedInternet = HttpUtils.IsHaveInternet(getActivity());
		if (!isConnectedInternet) {
			if (pbar != null) {
				pbar.setVisibility(View.GONE);
			}
			Toast.makeText(getActivity(), "需要连接移动网络或wifi才能获取最新信息！",
					Toast.LENGTH_LONG).show();
		} else {
			new AskMeTask().execute(url);
		}
	}

	class AskMeTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			String result = "";
			JSONObject jo2 = new JSONObject();
			try {
				jo2.put("用户编号", Global.mUser.Id);
				result = httpUtils.postSubmit(params[0], jo2);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!TextUtils.isEmpty(result)) {
				LogUtils.i("fragmentResult", result);
				pbar.setVisibility(View.GONE);
				if (!TextUtils.isEmpty(result)) {
					list = JsonUtils.ConvertJsonToList(result, 流程.class);
					LogUtils.i("SIZE", "SIZE=" + list.size());
					if (list != null) {
						adapter.setmList(list);
						adapter.notifyDataSetChanged();
						lv.onRefreshComplete();
					}
				}
			}
		}
	}
}
