package com.cedarhd;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanCrmAdapter;
import com.cedarhd.control.listview.PullToRefreshAndLoadMoreListView;
import com.cedarhd.helpers.Global;
import com.cedarhd.models.Demand;
import com.cedarhd.models.任务;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;

/** 定时任务提醒页面 */
public class SettingAlarmTaskActivity extends BaseActivity {

	private Context mContext;
	private List<任务> mList;
	private Demand mDemand;
	private CommanCrmAdapter<任务> mAdapter;

	private ImageView ivBack;
	private ImageView ivAdd;
	private TextView tvTitle;
	private PullToRefreshAndLoadMoreListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_alarm_task);

		initData();
		initViews();
		setOnEvent();
	}

	private void initData() {
		mContext = this;
		mList = new ArrayList<任务>();
		mDemand = new Demand();
		mDemand.每页数量 = 10;
		mDemand.偏移量 = 0;
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_comman);
		ivAdd = (ImageView) findViewById(R.id.iv_add_comman);
		tvTitle = (TextView) findViewById(R.id.tv_title_comman);
		tvTitle.setText("任务定时提醒");
		lv = (PullToRefreshAndLoadMoreListView) findViewById(R.id.lv_comman_loadlist);
		mAdapter = getAdapter();
		lv.setAdapter(mAdapter);
	}

	private void setOnEvent() {
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position >= mList.size()) {
					return;
				}

			}
		});
	}

	private void fetchServerData() {
		String url = Global.BASE_URL + "task/GetAlarmTaskList";
		StringRequest.postAsyn(url, mDemand, new StringResponseCallBack() {

			@Override
			public void onResponseCodeErro(String result) {
				showShortToast("加载网络数据失败");
			}

			@Override
			public void onResponse(String response) {
				mList = JsonUtils.ConvertJsonToList(response, 任务.class);
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				showShortToast("服务器异常错误");
			}
		});
	}

	private CommanCrmAdapter<任务> getAdapter() {
		return new CommanCrmAdapter<任务>(mList, mContext, R.layout.item_clewlist) {
			@Override
			public void convert(int position, 任务 item,
					BoeryunViewHolder viewHolder) {
				// TextView tvClient = viewHolder
				// .getView(R.id.tv_client_clewlist_item);
				// TextView tvContact = viewHolder
				// .getView(R.id.tv_contacts_clewlist_item);
				// TextView tvPhone = viewHolder
				// .getView(R.id.tv_phone_clewlist_item);
				// TextView tvProduct = viewHolder
				// .getView(R.id.tv_product_clewlist_item);
				// TextView tvTime = viewHolder
				// .getView(R.id.tv_time_item_clewlist);
				//
				// tvClient.setText(StrUtils.pareseNull(item.单号));
				// tvContact.setText(StrUtils.pareseNull(item.合同号));
				// tvPhone.setText(StrUtils.pareseNull(item.订单));
				// tvTime.setText(DateDeserializer.getFormatTime(item.时间));
			}
		};
	}
}
