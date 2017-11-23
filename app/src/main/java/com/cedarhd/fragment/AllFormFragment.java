package com.cedarhd.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.cedarhd.CreateVmFormActivity;
import com.cedarhd.R;
import com.cedarhd.adapter.FormAdapter;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.流程分类表;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 所有表单列表Fragment
 * 
 * @author BOHR
 * 
 */
public class AllFormFragment extends Fragment {
	public static final String TAG = "AllFormFragment";
	private Context context;
	ZLServiceHelper mZLServiceHelper = new ZLServiceHelper();
	ImageView imageViewCancel;
	private PullToRefreshListView lv;
	private FormAdapter adapter;
	private List<流程分类表> mList = new ArrayList<流程分类表>();

	public static final int REQUEST_CODE_NEW_FORM_MORE = 2;
	public static final int LOAD_SERVER_DATA_SUCCEEDED = 0;
	public static final int LOAD_SERVER_DATA_FAILED = 1;
	public static final int LISTVIEW_REFRESH_COMPLETE = 4;

	public static final int LOAD_LOCAL_DATA_FAILED = 5;
	public static final int LOAD_LOCAL_DATA_SUCCEED = 6;
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOAD_LOCAL_DATA_SUCCEED:
				ProgressDialogHelper.dismiss();
				mList = (List<流程分类表>) msg.obj;
				adapter = new FormAdapter(mList, context);
				lv.setVisibility(View.VISIBLE);
				lv.setAdapter(adapter);
				break;
			case LOAD_LOCAL_DATA_FAILED:
				reloadDataFromNet();
				break;
			case LOAD_SERVER_DATA_SUCCEEDED:
				ProgressDialogHelper.dismiss();
				String jsonData = (String) msg.obj;
				LogUtils.i(TAG,
						"LOAD_SERVER_DATA_SUCCEEDED长度=" + jsonData.length());
				mList = JsonUtils.ConvertJsonToList(jsonData, 流程分类表.class);
				for (int i = 0; i < mList.size(); i++) {
					// 保存分类信息
					流程分类表 item = mList.get(i);
					LogUtils.i("all", item.编号 + "--" + item.名称 + "--" + item.上级);
					// if (item.上级 == 0) {
					// insertOrUpdateLatest(item, true);
					// }
					insertOrUpdateLatest(item, false);
				}
				adapter = new FormAdapter(mList, context);
				lv.setVisibility(View.VISIBLE);
				lv.setAdapter(adapter);
				break;
			case LOAD_SERVER_DATA_FAILED:
				ProgressDialogHelper.dismiss();
				Toast.makeText(getActivity(), "获取网络数据失败，请稍后再试...",
						Toast.LENGTH_SHORT).show();
				break;
			case LISTVIEW_REFRESH_COMPLETE:
				ProgressDialogHelper.dismiss();
				if (lv != null) {
					lv.onRefreshComplete();
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_all_form, null);
		init(view);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtils.i(TAG, TAG + ":onResume");
	}

	private void init(View view) {
		context = getActivity();
		lv = (PullToRefreshListView) view.findViewById(R.id.lv_all_askform);
		setOnClickListener();
		// reloadDataFromNet(demand);
		queryAllFormList();
	}

	/***
	 * 加载网络数据
	 * 
	 * @param demand
	 */
	public void reloadDataFromNet() {
		// final Demand demand = new Demand();
		// // demand.方法名 = "Flow/GetWorkFlowCategory/";
		// demand.方法名 = "Flow/GetWorkFlowDefAndCategory/";
		// demand.企业编号 = Global.mUser.CorpId + "";
		// demand.用户编号 = Global.mUser.Id + "";
		// // demand.企业编号 = "";
		// // demand.用户编号 = "";
		// demand.表名 = "流程";
		// ProgressDialogHelper.show(context, "正在更新表单");
		ProgressDialogHelper.show(context, "正在更新表单");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpUtils httpUtils = new HttpUtils();
					String url = Global.BASE_URL
							+ "Flow/GetWorkFlowDefAndCategory/";
					String result = httpUtils.httpGet(url);
					Message msg = new Message();
					LogUtils.i(TAG, "" + result);
					if (result != null) {
						msg.what = LOAD_SERVER_DATA_SUCCEEDED;
						msg.obj = result;
					} else {
						msg.what = LOAD_SERVER_DATA_FAILED;
					}
					handler.sendMessage(msg);
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = LOAD_SERVER_DATA_FAILED;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 查询最近流程分类表
	 * 
	 * @return
	 */
	private void queryAllFormList() {
		Message msg = handler.obtainMessage();
		List<流程分类表> returnLists = new ArrayList<流程分类表>();
		ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(getActivity());
		try {
			Dao<流程分类表, Integer> dao = ormDataHelper.getDao(流程分类表.class);
			returnLists = dao.queryBuilder().where().eq("isLatest", 0).query();

			if (returnLists != null && returnLists.size() > 0) {
				msg.obj = returnLists;
				msg.what = LOAD_LOCAL_DATA_SUCCEED;
				handler.sendMessage(msg);
			} else {
				msg.what = LOAD_LOCAL_DATA_FAILED;
				handler.sendMessage(msg);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			msg.what = LOAD_LOCAL_DATA_FAILED;
			handler.sendMessage(msg);
		}
	}

	public void setOnClickListener() {
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position - 1; // listviewHeader 为pos=0
				// 流程分类表 item = mList.get(pos);
				流程分类表 item = adapter.getItem(pos);
				// webview打开表单页面
				// Intent intent = new Intent(getActivity(),
				// NewFormWebviewActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putSerializable("lcfl", item);
				// intent.putExtras(bundle);
				// startActivity(intent);
				LogUtils.i(TAG, "上级=" + item.上级 + "\t表单名称：" + item.表单名称
						+ "--工作流配置文件" + item.表单配置文件);
				if (TextUtils.isEmpty(item.表单配置文件) && item.上级 == 0) {
					return;
				} else {
					item.UpdateTime = new Date();
					item.isLatest = 1;
					insertOrUpdateLatest(item, true);
					// xml生成表单
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putInt("id", 0);
					bundle.putInt("typeId", item.编号);// 115表示报销申请单
					bundle.putString("dataId", "0"); // 新建
					bundle.putString("typeName", item.名称);
					intent.putExtras(bundle);
					intent.setClass(context, CreateVmFormActivity.class);
					startActivity(intent);
				}
			}
		});

		lv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(1000);
							handler.sendEmptyMessage(LISTVIEW_REFRESH_COMPLETE);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
	}

	/***
	 * 插入或修改 最近 流程分类表
	 * 
	 * @param item
	 * @param isLatest
	 *            是否是最近使用
	 */
	private void insertOrUpdateLatest(流程分类表 item, boolean isLatest) {
		ORMDataHelper helper = ORMDataHelper.getInstance(context);
		try {
			Dao<流程分类表, Integer> dao = helper.getDao(流程分类表.class);
			item.isLatest = isLatest ? 1 : 0;
			List<流程分类表> existedlist = dao.queryBuilder().where()
					.eq("编号", item.编号).and().eq("isLatest", item.isLatest)
					.query();
			if (existedlist.size() == 0) {
				dao.create(item);
			} else {
				item.set_Id(existedlist.get(0).get_Id());
				dao.update(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
