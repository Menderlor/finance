package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.Dict;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;

import java.util.List;

public class SelectDepartmnetActivity extends BaseActivity {

	/** 下载员工成功 */
	private final static int SUCCEED_GET_DICT = 1;

	/** 下载失败 */
	private final static int FAILURE_GET_DICT = 2;
	private Context context;
	private List<Dict> mList;
	private CommanAdapter<Dict> adapter;
	private HttpUtils httpUtils = new HttpUtils();
	private ListView lv;
	private ImageView ivSave;

	String selectDepts = "";
	String selectDeptsName = "";

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCEED_GET_DICT:
				ProgressDialogHelper.dismiss();
				adapter = getAdapter(mList);
				lv.setAdapter(adapter);
				break;
			case FAILURE_GET_DICT:
				ProgressDialogHelper.dismiss();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_department);
		context = this;
		findViews();
		ProgressDialogHelper.show(context, "正在加载部门...");
		downloadDepartment();
	}

	private void findViews() {
		lv = (ListView) findViewById(R.id.lv_select_department);
		ivSave = (ImageView) findViewById(R.id.iv_save_select_department);

		ivSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getSelectedDepts();

				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("selectDepts", selectDepts);
				bundle.putString("selectDeptsName", selectDeptsName);
				intent.putExtras(bundle);
				SelectDepartmnetActivity.this.setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	/***
	 * 下载部门
	 */
	private void downloadDepartment() {
		final String url = Global.BASE_URL + "Department/GetDepartmentDict";
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String result = httpUtils.httpGet(url);
					mList = JsonUtils.ConvertJsonToList(result, Dict.class);
					if (mList != null && mList.size() > 0) {
						handler.sendEmptyMessage(SUCCEED_GET_DICT);
					} else {
						handler.sendEmptyMessage(FAILURE_GET_DICT);
					}
				} catch (Exception e) {
					handler.sendEmptyMessage(FAILURE_GET_DICT);
				}

			}
		}).start();
	}

	private String getSelectedDepts() {
		if (mList == null || mList.size() == 0) {
			return "";
		} else {
			for (int i = 0; i < mList.size(); i++) {
				if ("true".equals(mList.get(i).拼音)) {
					selectDepts += mList.get(i).编号 + ",";
					selectDeptsName += mList.get(i).名称 + ",";
				}
			}
			if (!TextUtils.isEmpty(selectDepts) && selectDepts.endsWith(",")) {
				selectDepts = selectDepts
						.substring(0, selectDepts.length() - 1);
			}
			if (!TextUtils.isEmpty(selectDeptsName)
					&& selectDeptsName.endsWith(",")) {
				selectDeptsName = selectDeptsName.substring(0,
						selectDeptsName.length() - 1);
			}
		}
		return selectDepts;
	}

	private CommanAdapter<Dict> getAdapter(final List<Dict> list) {
		return new CommanAdapter<Dict>(list, context,
				R.layout.item_select_department) {
			@Override
			public void convert(final int position, Dict item,
					BoeryunViewHolder viewHolder) {
				TextView tvName = viewHolder
						.getView(R.id.tv_name_select_dept_item);
				final CheckBox cb = viewHolder
						.getView(R.id.checkbox_select_dept_item);
				tvName.setText(item.名称 + "");
				boolean isChecked = false;
				if (!TextUtils.isEmpty(mList.get(position).拼音)
						&& "true".equals(mList.get(position).拼音)) {
					isChecked = true;
				}
				cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							mList.get(position).拼音 = "true";
						} else {
							mList.get(position).拼音 = "false";
						}
					}
				});
				cb.setChecked(isChecked);
			}
		};
	}
}
