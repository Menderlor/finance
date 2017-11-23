package com.cedarhd.slt;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.slt.Slt销售目标;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SltSaleTargetActivity extends BaseActivity {

	private Context mContext;

	private String mUserId;
	private int mYear;

	/** 默认值 */
	private List<Double> mDefaultDatas;
	private DictionaryHelper mDictionaryHelper;
	private Slt销售目标 mTarget;
	private BaseAdapter mAdapter;
	private BoeryunHeaderView headerView;
	private PullToRefreshListView lv;
	private MyProgressBar pbar;

	private TextView tvUser;
	private TextView tvYear;
	private ImageView ivPre;
	private ImageView ivNext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slt_sale_target);
		initViews();
		initData();
	}

	@Override
	protected void onStart() {
		super.onStart();

		pbar.setVisibility(View.GONE);
		lv.setVisibility(View.VISIBLE);
		fetchServerData();
		setOnEvent();
	}

	private void initViews() {
		lv = (PullToRefreshListView) findViewById(R.id.lv_comman_normal_style);
		pbar = (MyProgressBar) findViewById(R.id.progress_comman_normal_style);
		headerView = (BoeryunHeaderView) findViewById(R.id.header_slt_sale_target);
		tvUser = (TextView) findViewById(R.id.tv_user_slt_sale_target);
		tvYear = (TextView) findViewById(R.id.tv_year_sale_target);
		ivPre = (ImageView) findViewById(R.id.iv_pre_slt_sale_target);
		ivNext = (ImageView) findViewById(R.id.iv_next_slt_sale_target);
		pbar.setVisibility(View.GONE);
	}

	private void initData() {
		mContext = this;
		mUserId = Global.mUser.Id;
		mYear = Calendar.getInstance().get(Calendar.YEAR);
		mTarget = new Slt销售目标();
		mDictionaryHelper = new DictionaryHelper(mContext);

		mDefaultDatas = new ArrayList<Double>();
		for (int i = 0; i < 12; i++) {
			mDefaultDatas.add((double) 0);
		}
		if (mTarget.Targets == null) {
			mTarget.Targets = mDefaultDatas;
		}
		initAdapter();
		lv.setAdapter(mAdapter);
		String userName = mDictionaryHelper
				.getDepartNameById(Global.mUser.Department + "")
				+ ">"
				+ mDictionaryHelper.getUserNameById(mUserId);
		tvUser.setText(userName + "");
	}

	private void initAdapter() {
		mAdapter = new BaseAdapter() {
			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				View view = LayoutInflater.from(mContext).inflate(
						R.layout.item_slt_target, null);
				final EditText etText = (EditText) view
						.findViewById(R.id.tv_total_slt_target_item);
				final TextView tvText = (TextView) view
						.findViewById(R.id.tv_time_slt_target_item);
				etText.setText("" + mTarget.Targets.get(position));
				tvText.setText(mTarget.Year + "年 " + (position + 1) + "月");
				etText.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {
						if (!TextUtils.isEmpty(s.toString())) {
							try {
								double value = Double.parseDouble(s.toString());
								mTarget.Targets.set(position, value);
							} catch (Exception e) {
								LogUtils.e(TAG, "" + e.getMessage());
							}
						}
					}
				});
				return view;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mTarget.Targets.size();
			}
		};
	}

	private void setOnEvent() {
		headerView.setOnButtonClickListener(new OnButtonClickListener() {

			@Override
			public void onClickSaveOrAdd() {
				submitTargetData();
			}

			@Override
			public void onClickFilter() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onClickBack() {
				finish();
			}
		});

		ivPre.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mYear--;
				fetchServerData();
				tvYear.setText("" + mYear);
			}
		});

		ivNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mYear++;
				fetchServerData();
				tvYear.setText("" + mYear);
			}
		});
	}

	private void fetchServerData() {
		ProgressDialogHelper.show(mContext);
		String url = Global.BASE_URL + "ShopReport/GetSaleTarget/" + mUserId
				+ "/" + mYear;
		StringRequest.getAsyn(url, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
				showShortToast("加载网络数据失败");
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				mTarget = JsonUtils.ConvertJsonObject(response, Slt销售目标.class);
				if (mTarget != null && mTarget.Targets != null) {
					// mAdapter.addBottom(mTarget.Targets, true);
					mAdapter.notifyDataSetChanged();
				} else {
					mTarget = new Slt销售目标();
					if (mTarget.Targets == null) {
						mTarget.Targets = mDefaultDatas;
					}
					// mAdapter.addBottom(mTarget.Targets, true);
					mAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				showShortToast("服务器访问失败");
				ProgressDialogHelper.dismiss();
			}
		});
	}

	private void submitTargetData() {
		ProgressDialogHelper.show(mContext);
		String url = Global.BASE_URL + "ShopReport/SaveTargetList/";
		mTarget.StaffId = Integer.parseInt(mUserId);
		mTarget.Year = mYear;
		StringRequest.postAsyn(url, mTarget, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
				showShortToast("保存失败");
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				showShortToast("保存成功");
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				showShortToast("服务器访问失败");
				ProgressDialogHelper.dismiss();
			}
		});
	}

	// private void setTargetValue() {
	// new AlertDialog.Builder(mContext).setTitle("请输入")
	// .setIcon(android.R.drawable.ic_dialog_info)
	// .setView(new EditText(mContext)).setPositiveButton("确定", null)
	// .setNegativeButton("取消", null).show();
	// }
}
