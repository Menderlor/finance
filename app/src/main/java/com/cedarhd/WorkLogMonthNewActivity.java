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
public class WorkLogMonthNewActivity extends BaseActivity {
	private ImageView ivcancel;
	private ImageView ivnew;
	private TextView tv_time_month;
	private TextView tv_employee_month;
	private EditText et_finish_month;
	private EditText et_unfinished_month;
	private EditText et_plan_month;
	private EditText et_summary_month;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.work_log_month_new);

	}

	private void findviews() {
		// TODO Auto-generated method stub
		ivcancel = (ImageView) findViewById(R.id.imageViewCancel_month);
		ivnew = (ImageView) findViewById(R.id.imageViewDone_month);
		tv_time_month = (TextView) findViewById(R.id.tv_time_month);
		tv_employee_month = (TextView) findViewById(R.id.tv_employee_month);
		et_finish_month = (EditText) findViewById(R.id.et_finish_month);
		et_unfinished_month = (EditText) findViewById(R.id.et_unfinished_month);
		et_plan_month = (EditText) findViewById(R.id.et_plan_month);
		et_summary_month = (EditText) findViewById(R.id.et_summary_month);
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
