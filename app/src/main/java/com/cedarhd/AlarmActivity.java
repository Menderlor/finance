package com.cedarhd;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.utils.MediaUtils;

public class AlarmActivity extends BaseActivity {
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		String content = getIntent().getStringExtra("content");

		MediaUtils.startMusic(context, R.raw.collect_diamonds_02);

		Builder builder = new AlertDialog.Builder(context);
		View view = View.inflate(context, R.layout.boeryun_dialog, null);
		final TextView tvTitle = (TextView) view
				.findViewById(R.id.tv_title_dialog_boeryun);
		final TextView tvContent = (TextView) view
				.findViewById(R.id.tv_content_dialog_boeryun);
		final Button btnCancle = (Button) view
				.findViewById(R.id.btn_cancel_dialog_boeryun);
		final Button btnOk = (Button) view
				.findViewById(R.id.btn_ok_dialog_boeryun);
		btnOk.setText("知道了");
		btnCancle.setVisibility(View.GONE);
		tvTitle.setText("任务提醒");
		tvContent.setText(content);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		final AlertDialog dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();

		// // 显示对话框
		// new AlertDialog.Builder(AlarmActivity.this).setTitle("提醒")
		// .setMessage(content)
		// .setPositiveButton("知道了", new OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		// AlarmActivity.this.finish();
		// }
		// }).create().show();
	}
}
