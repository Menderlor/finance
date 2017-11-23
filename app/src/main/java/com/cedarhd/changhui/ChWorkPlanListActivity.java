package com.cedarhd.changhui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.AvartarViewHelper;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.DateAndTimePicker.ISelected;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.Dict;
import com.cedarhd.models.User;
import com.cedarhd.models.crm.QmCustomerWorkPlan;
import com.cedarhd.models.crm.VmBase;
import com.cedarhd.models.客户工作计划;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.hb.views.PinnedSectionListView;
import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/***
 * 长汇项目工作计划
 * 
 * @author k 2015-12-14
 */
@Deprecated
public class ChWorkPlanListActivity extends BaseActivity {
	private Context mContext;
	private QmCustomerWorkPlan mQmDemand;
	private VmBase<客户工作计划> mVmBase;
	private String mUserSelectId;

	private DictionaryHelper mDictionaryHelper;
	private WorkPlanAdapter mAdapter;
	private BoeryunHeaderView headerView;
	private PinnedSectionListView psLv;
	private RelativeLayout rlSelectUser;
	private RelativeLayout rlSelectDate;
	private TextView tvSelectUser;
	private TextView tvSelectDate;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ch_work_plan_list);
		initViews();
		initData();
		setOnEvent();
		fetchServerData();
	};

	private void initData() {
		mContext = this;
		mDictionaryHelper = new DictionaryHelper(mContext);
		mUserSelectId = Global.mUser.getId();
		mQmDemand = new QmCustomerWorkPlan();
		mQmDemand.Offset = 0;
		mQmDemand.PageSize = 1000;
		mQmDemand.Time = DateDeserializer.getTodayDate();

	}

	private void initViews() {
		headerView = (BoeryunHeaderView) findViewById(R.id.header_work_plan_list);
		psLv = (PinnedSectionListView) findViewById(R.id.pinned_section_lv_work_plan_list);
		rlSelectUser = (RelativeLayout) findViewById(R.id.rl_select_user_workplan);
		rlSelectDate = (RelativeLayout) findViewById(R.id.rl_select_date_workplan);
		tvSelectDate = (TextView) findViewById(R.id.tv_select_date_workplan);
		tvSelectUser = (TextView) findViewById(R.id.tv_select_user_workplan);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		User selectUser = UserBiz.onActivityMultiUserSelected(requestCode,
				resultCode, data);
		if (selectUser != null) {
			tvSelectUser.setText(selectUser.getUserNames());
			mQmDemand.UserIds = selectUser.getUserIds();
			fetchServerData();
		}
	}

	private void setOnEvent() {
		headerView.setOnButtonClickListener(new OnButtonClickListener() {
			@Override
			public void onClickSaveOrAdd() {
				Intent intent = new Intent(mContext,
						ChWorkPlanInfoActivity.class);
				if (mVmBase != null && mVmBase.Dict != null) {
					Bundle bundle = new Bundle();
					bundle.putSerializable(ChWorkPlanInfoActivity.TAG_DICTS,
							mVmBase.Dict);
					intent.putExtras(bundle);
				}
				startActivity(intent);
			}

			@Override
			public void onClickFilter() {

			}

			@Override
			public void onClickBack() {
				finish();
			}
		});

		rlSelectUser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UserBiz.selectMultiUser(mContext, mUserSelectId);
			}
		});

		rlSelectDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DateAndTimePicker dateAndTimePicker = new DateAndTimePicker(
						mContext);
				dateAndTimePicker.showDateWheel(tvSelectDate, false);
				dateAndTimePicker.setOnSelectedListener(new ISelected() {
					@Override
					public void onSelected(String date) {
						mQmDemand.Time = date;
						fetchServerData();
					}
				});
			}
		});

		psLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				showShortToast("setOnItemClickListener");
				客户工作计划 item = mAdapter.getItem(position);
				if (item.sectionType == Item.SECTION) {
				} else {
					Intent intent = new Intent(mContext,
							ChWorkPlanInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(ChWorkPlanInfoActivity.TAG_INFO,
							item);
					bundle.putSerializable(ChWorkPlanInfoActivity.TAG_DICTS,
							mVmBase.Dict);
					intent.putExtras(bundle);
				}

			}
		});
	}

	private void fetchServerData() {
		ProgressDialogHelper.show(mContext);
		String url = Global.BASE_URL + "CustomerWorkPlan/GetWorkPlanList";
		StringRequest.postAsyn(url, mQmDemand, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
				LogUtils.d(TAG, "onResponseCodeErro");
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				try {
					LogUtils.d(TAG, "onResponse：" + response);
					mVmBase = JsonUtils.convertJsonToVmBase(response,
							客户工作计划.class);
					mAdapter = new WorkPlanAdapter(mContext, mVmBase);
					psLv.setAdapter(mAdapter);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
				LogUtils.d(TAG, "onFailure");
			}
		});
	}

	class WorkPlanAdapter extends BaseAdapter implements
			PinnedSectionListAdapter {
		private VmBase<客户工作计划> mVmBase;
		private LayoutInflater mInflater;
		private List<客户工作计划> mWorkPlanList = new ArrayList<客户工作计划>();

		public WorkPlanAdapter(Context context, VmBase<客户工作计划> vmBase) {
			mVmBase = vmBase;
			List<客户工作计划> list = vmBase.Data;
			mInflater = LayoutInflater.from(context);
			initData(list);
		}

		private void initData(List<客户工作计划> list) {
			HashMap<Integer, 客户工作计划> hashMap = new HashMap<Integer, 客户工作计划>();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					客户工作计划 item = list.get(i);
					if (!hashMap.containsKey(item.业务员)) {
						客户工作计划 sectionItem = new 客户工作计划();
						sectionItem.业务员 = item.业务员;
						sectionItem.sectionType = Item.SECTION;
						hashMap.put(item.业务员, sectionItem);
					}
				}

				Iterator<Map.Entry<Integer, 客户工作计划>> iterator = hashMap
						.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<Integer, 客户工作计划> entry = iterator.next();
					int key = entry.getKey();
					客户工作计划 wp = entry.getValue();
					mWorkPlanList.add(wp);
					for (int i = 0; i < list.size(); i++) {
						客户工作计划 item = list.get(i);
						if (item.业务员 == key) {
							mWorkPlanList.add(item);
						}
					}
				}
			}
		}

		@Override
		public int getCount() {
			return mWorkPlanList.size();
		}

		@Override
		public 客户工作计划 getItem(int position) {
			return mWorkPlanList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			客户工作计划 workplan = getItem(position);
			if (workplan.sectionType == Item.SECTION) {
				view = mInflater.inflate(R.layout.item_section_workplan_list,
						null);
				TextView tv = (TextView) view
						.findViewById(R.id.tv_user_item_workplan);
				AvartarView avartarView = (AvartarView) view
						.findViewById(R.id.avartar_item_workplan);
				new AvartarViewHelper(mContext, workplan.业务员, avartarView, 50,
						50, false);
				tv.setText("" + mDictionaryHelper.getUserNameById(workplan.业务员));
			} else {
				view = mInflater.inflate(R.layout.item_workplan_list, null);
				TextView tv = (TextView) view
						.findViewById(R.id.tv_content_item_workplan);
				TextView tvClient = (TextView) view
						.findViewById(R.id.tv_client_item_workplan);
				String clientName = getDictValue("客户", workplan.客户);
				String typeName = getDictValue("客户工作计划_工作类型", workplan.工作类型);
				tvClient.setText(clientName);
				tv.setText("计划类型："
						+ typeName
						+ "\n"
						+ DateDeserializer
								.getFormatShortTime(getItem(position).创建时间));
			}
			return view;
		}

		private String getDictValue(String dictName, int dictId) {
			List<Dict> list = mVmBase.Dict.get(dictName);
			if (list != null && list.size() >= 0) {
				for (Dict dict : list) {
					if (dictId == dict.编号) {
						return dict.名称;
					}
				}
			}
			return "";
		}

		@Override
		public int getItemViewType(int position) {
			return mWorkPlanList.get(position).sectionType;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public boolean isItemViewTypePinned(int viewType) {
			return viewType == Item.SECTION;
		}
	}

	class Item {
		public static final int ITEM = 0;
		public static final int SECTION = 1;

		public final int type;

		public Item(int type) {
			this.type = type;
		}
	}
}
