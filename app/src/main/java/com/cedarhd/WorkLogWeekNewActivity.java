package com.cedarhd;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;

/** 已废弃，采用Fragment的形式 */
@Deprecated
public class WorkLogWeekNewActivity extends BaseActivity {
	private ImageView ivcancel;
	private ImageView ivnew;
	private TextView tv_time_week;
	private TextView tv_employee_week;
	private EditText et_finish_week;
	private EditText et_unfinished_week;
	private EditText et_plan_week;
	private EditText et_summary_week;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.work_log_week_new);
		findviews();
		setonclicklistener();
	}

	private void findviews() {
		// TODO Auto-generated method stub
		ivcancel = (ImageView) findViewById(R.id.imageViewCancel_week);
		ivnew = (ImageView) findViewById(R.id.imageViewDone_week);
		tv_time_week = (TextView) findViewById(R.id.tv_time_week);
		tv_employee_week = (TextView) findViewById(R.id.tv_employee_week);
		et_finish_week = (EditText) findViewById(R.id.et_finish_week);
		et_unfinished_week = (EditText) findViewById(R.id.et_unfinished_week);
		et_plan_week = (EditText) findViewById(R.id.et_plan_week);
		et_summary_week = (EditText) findViewById(R.id.et_summary_week);
	}

	private void setonclicklistener() {
		// TODO Auto-generated method stub
		ivcancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		ivnew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
	}
}
