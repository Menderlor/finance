package com.cedarhd.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.cedarhd.CreateVmFormActivity;
import com.cedarhd.R;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.models.流程分类表;
import com.cedarhd.utils.LogUtils;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LatestFormFragment extends Fragment {
	private final String TAG = "LatestFormFragment";
	private List<流程分类表> list = new ArrayList<流程分类表>();
	private PullToRefreshListView lv;
	private TextView tvEmpty;
	private CommanAdapter<流程分类表> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.all_askform_fragment, null);

		init(view);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		reload();
		LogUtils.i(TAG, "onResume");
	}

	private void init(View view) {
		lv = (PullToRefreshListView) view.findViewById(R.id.lv_all_askform);
		tvEmpty = (TextView) view.findViewById(R.id.tv_empty);
		adapter = getAdapter();
		lv.setVisibility(View.VISIBLE);
		lv.setAdapter(adapter);
		lv.setEmptyView(tvEmpty);
		lv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				reload();
			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position - 1; // listviewHeader 为pos=0
				流程分类表 item = list.get(pos);
				// Intent intent = new Intent(getActivity(),
				// NewFormWebviewActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putSerializable("lcfl", item);
				// intent.putExtras(bundle);
				// startActivity(intent);

				// xml生成表单
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("id", 0);
				bundle.putInt("typeId", item.编号);
				bundle.putString("dataId", "0"); // 新建
				bundle.putString("typeName", item.名称);
				intent.putExtras(bundle);
				intent.setClass(getActivity(), CreateVmFormActivity.class);
				startActivity(intent);
			}
		});
	}

	private CommanAdapter<流程分类表> getAdapter() {
		return new CommanAdapter<流程分类表>(list, getActivity(),
				R.layout.item_ask_more_form) {
			@Override
			public void convert(int position, 流程分类表 item,
					BoeryunViewHolder viewHolder) {
				TextView tvName = viewHolder.getView(R.id.tv_form_name);
				tvName.setText(item.表单名称 + "");
			}
		};
	}

	/**
	 * 查询最近流程分类表
	 * 
	 * @return
	 */
	private List<流程分类表> queryLatestFormList() {
		List<流程分类表> returnLists = new ArrayList<流程分类表>();
		ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(getActivity());
		try {
			Dao<流程分类表, Integer> dao = ormDataHelper.getDao(流程分类表.class);
			// returnLists = dao.queryBuilder().orderBy("UpdateTime", false)
			// .query();
			returnLists = dao.queryBuilder().where().eq("isLatest", 1).query();
			// returnLists = dao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnLists;
	}

	private void reload() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				list = queryLatestFormList();
				LogUtils.i(TAG, "最近 size=" + list.size());
				Message msg = handler.obtainMessage();
				msg.obj = list;
				msg.what = SUCCEED_LOAD_DATA;
				handler.sendMessage(msg);
			}
		}).start();

	}

	private final int SUCCEED_LOAD_DATA = 11;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCEED_LOAD_DATA:
				list = (List<流程分类表>) msg.obj;
				adapter.addTop(list, true);
				lv.onRefreshComplete();
				break;
			default:
				break;
			}
		};
	};
}
