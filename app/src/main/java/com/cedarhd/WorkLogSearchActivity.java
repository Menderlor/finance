package com.cedarhd;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.server.ZLServiceHelper;

import java.util.Calendar;

/** 已废弃式 */
@Deprecated
public class WorkLogSearchActivity extends BaseActivity {
	ZLServiceHelper mDataHelper = new ZLServiceHelper();

	EditText mEditTextReceiverName;
	EditText mEditTextContent;

	public String mUserSelectId = "";
	public String mUserSelectName = "";

	private EditText mShowDateFrom = null;
	private Button pickDateFrom = null;
	private EditText mShowDateTo = null;
	private Button pickDateTo = null;

	private static final int SHOW_DATAPICKFrom = 0;
	private static final int SHOW_DATAPICKTo = 1;

	private int mYear;
	private int mMonth;
	private int mDay;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.log_search);
		setTitle("日志查询");

		findViews();
		setOnClickListener();
		Init();
	}

	@SuppressWarnings("unchecked")
	void Init() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		setDateTime();

		// Bundle bundle = this.getIntent().getExtras();
		// if(bundle!=null)
		// {
		// mTextViewTitle_T.setText("任务");
		// mTask = (HashMap<String, Object>) bundle.getSerializable("Task");
		// mUserSelectId = mTask.get("Executor").toString();
		// mEditTextReceiverName.setText(mTask.get("ExecutorName").toString());
		// mEditTextTitle.setText(mTask.get("Title").toString());
		// mShowDateFrom.setText(mTask.get("Time").toString());
		// mShowDateTo.setText(mTask.get("AssignTime").toString());
		// }
	}

	public void setOnClickListener() {
		Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		Button buttonSend = (Button) findViewById(R.id.buttonSend);
		buttonSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(WorkLogSearchActivity.this,
						MenuNewActivity.class);
				Bundle b = new Bundle();
				b.putString("filter", GetFilter());
				i.putExtras(b);
				WorkLogSearchActivity.this.setResult(RESULT_OK, i);
				WorkLogSearchActivity.this.finish();
			}
		});

		Button buttonSelectPerson = (Button) findViewById(R.id.buttonSelectPerson);
		buttonSelectPerson.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WorkLogSearchActivity.this,
						User_SelectActivityNew.class);
				Bundle bundle = new Bundle();
				bundle.putString("UserSelectId", mUserSelectId);
				intent.putExtras(bundle);
				startActivityForResult(intent, 0);
			}
		});

		pickDateFrom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Message msg = new Message();
				if (pickDateFrom.equals((Button) v)) {
					msg.what = WorkLogSearchActivity.SHOW_DATAPICKFrom;
				}
				WorkLogSearchActivity.this.dateandtimeHandler.sendMessage(msg);
			}
		});

		pickDateTo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Message msg = new Message();
				if (pickDateTo.equals((Button) v)) {
					msg.what = WorkLogSearchActivity.SHOW_DATAPICKTo;
				}
				WorkLogSearchActivity.this.dateandtimeHandler.sendMessage(msg);
			}
		});

		mEditTextReceiverName.setKeyListener(null);
		mShowDateFrom.setKeyListener(null);
		mShowDateTo.setKeyListener(null);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			// 取出字符串
			Bundle bundle = data.getExtras();
			mUserSelectId = bundle.getString("UserSelectId");
			mUserSelectName = bundle.getString("UserSelectName");
			// if(mUserSelectName.length() > 0)
			// {
			mEditTextReceiverName.setText(mUserSelectName);
			// }
		}
	}

	public void findViews() {
		Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(WorkLogSearchActivity.this,
						MenuNewActivity.class);
				WorkLogSearchActivity.this.setResult(RESULT_CANCELED, i);
				WorkLogSearchActivity.this.finish();
			}
		});

		Button buttonSend = (Button) findViewById(R.id.buttonSend);
		buttonSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(WorkLogSearchActivity.this,
						MenuNewActivity.class);
				Bundle b = new Bundle();
				b.putString("filter", GetFilter());
				i.putExtras(b);
				WorkLogSearchActivity.this.setResult(RESULT_OK, i);
				WorkLogSearchActivity.this.finish();
			}
		});

		mShowDateFrom = (EditText) findViewById(R.id.showdateFrom);
		pickDateFrom = (Button) findViewById(R.id.pickdateFrom);

		mShowDateTo = (EditText) findViewById(R.id.showdateTo);
		pickDateTo = (Button) findViewById(R.id.pickdateTo);

		mEditTextReceiverName = (EditText) findViewById(R.id.editTextReceiverName);
		mEditTextContent = (EditText) findViewById(R.id.editTextContent);
	}

	String GetFilter() {
		String filter = " 编号 > 0 ";

		if (mEditTextContent.getText().toString().replaceAll(" ", "").length() > 0) {
			filter += " and " + " 内容  like '%"
					+ mEditTextContent.getText().toString().replaceAll(" ", "")
					+ "%'";
		}

		if (mShowDateFrom.getText().toString().replaceAll(" ", "").length() > 0) {
			filter += " and " + " 时间   > '"
					+ mShowDateFrom.getText().toString().replaceAll(" ", "")
					+ "'";
		}

		if (mShowDateTo.getText().toString().replaceAll(" ", "").length() > 0) {
			filter += " and " + " 时间  > '"
					+ mShowDateTo.getText().toString().replaceAll(" ", "")
					+ "'";
		}

		return filter;
	}

	// [start] 设置日期
	/**
	 * 设置日期
	 */
	private void setDateTime() {
		final Calendar c = Calendar.getInstance();

		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		// updateDateFromDisplay();
		// updateDateToDisplay();
	}

	/**
	 * 更新起始日期显示
	 */
	private void updateDateFromDisplay() {
		mShowDateFrom.setText(new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay));
	}

	/**
	 * 更新结束日期显示
	 */
	private void updateDateToDisplay() {
		mShowDateTo.setText(new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay));
	}

	/**
	 * 日期控件的事件
	 */
	private DatePickerDialog.OnDateSetListener mDateFromSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;

			updateDateFromDisplay();
		}
	};
	private DatePickerDialog.OnDateSetListener mDateToSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;

			updateDateToDisplay();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SHOW_DATAPICKFrom:
			return new DatePickerDialog(this, mDateFromSetListener, mYear,
					mMonth, mDay);
		case SHOW_DATAPICKTo:
			return new DatePickerDialog(this, mDateToSetListener, mYear,
					mMonth, mDay);
		}

		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case SHOW_DATAPICKFrom:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		case SHOW_DATAPICKTo:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		}
	}

	/**
	 * 处理日期控件的Handler
	 */
	Handler dateandtimeHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WorkLogSearchActivity.SHOW_DATAPICKFrom:
				showDialog(SHOW_DATAPICKFrom);
				break;
			case WorkLogSearchActivity.SHOW_DATAPICKTo:
				showDialog(SHOW_DATAPICKTo);
				break;
			}
		}

	};
	// [end]

}