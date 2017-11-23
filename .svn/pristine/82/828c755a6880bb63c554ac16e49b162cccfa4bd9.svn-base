package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.BoeryunSearchView;
import com.cedarhd.control.BoeryunSearchView.OnSearchedListener;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.kh联系人;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 通讯录列表信息
 * 
 * @author KJX
 * 
 */
public class CommunicationListActivity extends BaseActivity {

	private List<kh联系人> m联系人list;
	/** 编辑权限 */
	private boolean isEdit;
	Kh联系人ListViewAdapter listViewAdapter;
	PullToRefreshListView listview;
	private ImageView iv_new_communication_list;
	Demand demand;
	private MyProgressBar mProgressBar;
	public static boolean isResume; // 是否在Resume中刷新
	private Context context;
	private ListViewHelperNet<kh联系人> mListViewHelperNet;
	private BoeryunSearchView searchView;
	private ZLServiceHelper zlServiceHelper;
	private QueryDemand queryDemand;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communication);
		findViews();
		new PermissionAscyTask().execute("");
		setOnClickListener();
		Init();
		reload();
	}

	private void setOnClickListener() {
		ImageView ImageViewCancel = (ImageView) findViewById(R.id.imageViewCancel_client);
		ImageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		listview.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				listview.onRefreshComplete();
				reload();
			}
		});

		// 监听搜索框文字变化
		searchView.setOnSearchedListener(new OnSearchedListener() {
			@Override
			public void OnSearched(String str) {
				search(str);
			}
		});
	}

	private void reload() {
		boolean isConnectedInternet = HttpUtils.IsHaveInternet(context);
		if (!isConnectedInternet) {
			Toast.makeText(context, "需要连接移动网络或wifi才能获取最新信息！", Toast.LENGTH_LONG)
					.show();
			// mListViewHelperNet.loadServerData(true);
		} else {
			m联系人list.clear();
			listViewAdapter.notifyDataSetChanged();
			mListViewHelperNet.loadServerData(true);
		}
	}

	private void search(String filter) {
		demand.附加条件 = "名称 like '%" + filter + "%' or 手机 like '%" + filter
				+ "%' or 文本2 like '%" + filter + "%'";
		m联系人list.clear();
		listViewAdapter.notifyDataSetChanged();
		mListViewHelperNet.setmDemand(demand);
		reload();
	}

	protected void onResume() {
		super.onResume();
		// if (isResume) {
		// isResume = false;
		reload();
		// }
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void Init() {
		demand = new Demand();
		demand.表名 = "kh联系人";
		demand.方法名 = "Communication/GetContacts/";
		demand.条件 = "";
		demand.每页数量 = 10;
		demand.偏移量 = 0;
		queryDemand = new QueryDemand();
		queryDemand.fildName = "编号";
		queryDemand.sortFildName = "Id";

		m联系人list = new ArrayList<kh联系人>();

		listViewAdapter = new Kh联系人ListViewAdapter(
				CommunicationListActivity.this,
				R.layout.communication_select_list, m联系人list, null);
		listview.setAdapter(listViewAdapter);
		mListViewHelperNet = new ListViewHelperNet<kh联系人>(context, kh联系人.class,
				demand, listview, m联系人list, listViewAdapter, mProgressBar,
				queryDemand);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView) parent;
				final kh联系人 item = (kh联系人) listView.getItemAtPosition(position);

				Intent intent = new Intent(context,
						CommunicationInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(CommunicationAddActivity.TAG, item);
				bundle.putBoolean(CommunicationAddActivity.IS_EDIT, isEdit);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		iv_new_communication_list.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						CommunicatioNew_zmy_AddActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putBoolean(CommunicationAddActivity.IS_EDIT, isEdit);
				// intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void findViews() {
		zlServiceHelper = new ZLServiceHelper();
		iv_new_communication_list = (ImageView) findViewById(R.id.iv_new_communication_list);
		mProgressBar = (MyProgressBar) findViewById(R.id.progress_clientlist);
		listview = (PullToRefreshListView) findViewById(R.id.listView_Contactslist);
		searchView = (BoeryunSearchView) findViewById(R.id.searchview_communicationlist);
		context = CommunicationListActivity.this;
	}

	class Kh联系人ListViewAdapter extends BaseAdapter {

		private Context mcontext;
		private List<kh联系人> mlist联系人;
		int mlistviewlayoutId;
		View.OnClickListener mAdapterOnclick;

		public Kh联系人ListViewAdapter(Context pContext, int listviewlayoutId,
				List<kh联系人> m联系人list, OnClickListener listener) {
			this.mlist联系人 = m联系人list;
			this.mcontext = pContext;
			this.mlistviewlayoutId = listviewlayoutId;
			this.mAdapterOnclick = listener;
		}

		@Override
		public int getCount() {
			return mlist联系人.size();
		}

		@Override
		public kh联系人 getItem(int position) {
			return mlist联系人.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null
					|| (holder = (ViewHolder) convertView.getTag()) == null) {
				convertView = View.inflate(mcontext, mlistviewlayoutId, null);
				holder = new ViewHolder();
				holder.textViewPersonnelName = (TextView) convertView
						.findViewById(R.id.name_userSelect);
				holder.textMobilePhone = (TextView) convertView
						.findViewById(R.id.user_mobilephone);
				holder.textPhone = (TextView) convertView
						.findViewById(R.id.user_phone);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			kh联系人 item = getItem(position);
			if (TextUtils.isEmpty(item.名称))
				item.名称 = "";
			if (TextUtils.isEmpty(item.文本3))
				item.文本3 = "";
			if (TextUtils.isEmpty(item.文本2))
				item.文本2 = "";

			holder.textViewPersonnelName.setText(item.名称 + "");
			holder.textMobilePhone.setText(item.文本3 + "");
			holder.textPhone.setText(item.文本2 + "");
			LogUtils.i("zhaolei", "姓名：" + item.名称);
			return convertView;
		}

		final class ViewHolder {
			public TextView textViewPersonnelName;
			public TextView textMobilePhone;
			public TextView textPhone;
		}
	}

	/***
	 * 权限判断：是否允许新建通讯录
	 * 
	 * 
	 */
	private class PermissionAscyTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			String data = zlServiceHelper.GetPermissions();
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result.contains("47")) {
				isEdit = true;
				// 具备新建通知权限
				iv_new_communication_list.setVisibility(View.VISIBLE);
			}
		}
	}

}
