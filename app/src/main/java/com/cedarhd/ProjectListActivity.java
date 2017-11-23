package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.control.BoeryunSearchView;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.models.Demand;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.项目简略;
import com.cedarhd.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;

/***
 * 项目列表
 * 
 * @author K
 * 
 */
public class ProjectListActivity extends BaseActivity {

	public static String PROJECT_INFO = "prject_info";
	private Context context;
	private PullToRefreshListView lv;
	private List<项目简略> mList;
	private CommanAdapter<项目简略> mAdapter;
	private MyProgressBar mPbar;
	private ImageView ivBack;
	private ImageView ivAdd;
	private BoeryunSearchView searchView;
	private RelativeLayout rl_search_root;

	private ListViewHelperNet<项目简略> mListViewHelperNet;
	private QueryDemand queryDemand;
	private Demand demand;

	private boolean isFling;
	public static boolean isResume; // 是否在Resume中刷新

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.activity_project_list);
		context = ProjectListActivity.this;
		initViews();
		initData();
		reload();
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_cancel_project_list);
		ivAdd = (ImageView) findViewById(R.id.iv_add_project_list);
		lv = (PullToRefreshListView) findViewById(R.id.lv_project_list);
		mPbar = (MyProgressBar) findViewById(R.id.pbar_project_list);

		lv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				lv.onRefreshComplete();
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				项目简略 item = mList.get(position - 1);

				Intent intent = new Intent(context, ProjectInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(PROJECT_INFO, item);
				intent.putExtras(bundle);
				startActivity(intent);
			}

		});

		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();

			}
		});

		ivAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context, ProjectAddActivity.class));
			}
		});
	}

	private void initData() {
		mList = new ArrayList<项目简略>();
		queryDemand = new QueryDemand();
		demand = new Demand();
		demand.用户编号 = "";
		demand.表名 = "项目管理";
		demand.方法名 = "Project/GetProjectList";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 20;
		demand.偏移量 = 0;
		queryDemand.fildName = "编号";
		queryDemand.sortFildName = "编号";
		mAdapter = new CommanAdapter<项目简略>(mList, context,
				R.layout.item_project_list) {
			@Override
			public void convert(int position, 项目简略 item,
					BoeryunViewHolder viewHolder) {
				TextView tvContent = viewHolder
						.getView(R.id.tv_content_project_item);
				tvContent.setText(item.名称 + "");
			}
		};
		lv.setAdapter(mAdapter);
		mListViewHelperNet = new ListViewHelperNet<项目简略>(this, 项目简略.class,
				demand, lv, mList, mAdapter, mPbar, queryDemand);
		mListViewHelperNet.hiddenFootView();
	}

	private void reload() {
		boolean isConnectedInternet = HttpUtils.IsHaveInternet(context);
		if (!isConnectedInternet) {
			Toast.makeText(context, "需要连接到3G或者wifi因特网才能获取最新信息！",
					Toast.LENGTH_LONG).show();
			// mListViewHelperKjx.loadLocalData();
		} else {
			mList.clear();
			mListViewHelperNet.setNotifyDataSetChanged();
			// mListViewHelperKjx.loadServerData(true);
			mListViewHelperNet.loadServerData(true);
		}
	}

}
