package com.cedarhd;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.AlarmTask;

public class TaskAlarmRemindActivity extends BaseActivity {
	public static String TASK_ALARM = "taskAlarm";

	private Context mContext;
	private Vibrator mVibrator;
	private MediaPlayer mPlayer;
	private ZLServiceHelper mZlServiceHelper;

	private AlarmTask mTask;
	private TextView tvTime;
	private TextView tvContent;
	private ImageView ivCancle;
	private ImageView ivComplete;
	private ImageView ivBg;

	private AnimationDrawable animDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarmtask_remind);
		initData();
		initViews();

		startMedia();
	}

	private void startMedia() {
		mPlayer.start();

		mPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				/*
				 * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
				 */
				long[] pattern = { 100, 1500, 100, 1500 }; // 停止 开启 停止 开启
				
				// 重复两次上面的pattern
				// 如果只想震动一次，index设为-1
				mVibrator.vibrate(pattern, -1); 

			}
		});

		// MediaUtils.startMusic(this, R.raw.)
	}

	private void initData() {
		mContext = TaskAlarmRemindActivity.this;
		
		mPlayer = MediaPlayer.create(this, R.raw.alarmtask);
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		mZlServiceHelper=new ZLServiceHelper();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mTask = (AlarmTask) bundle.getSerializable(TASK_ALARM);
		}
	}

	private void initViews() {
		tvTime = (TextView) findViewById(R.id.tv_time_alarmtask);
		tvContent = (TextView) findViewById(R.id.tv_content_alarmtask);
		ivCancle = (ImageView) findViewById(R.id.iv_cancel_alarmtask);
		ivComplete = (ImageView) findViewById(R.id.iv_complete_alarmtask);
		ivBg = (ImageView) findViewById(R.id.iv_bg_alarmtask);
		// 帧动画对象
		animDrawable = (AnimationDrawable) ivBg.getBackground();

		if (mTask != null) {
			String timeStr = DateDeserializer
					.getFormatShortTime(mTask.AssignTime);
			tvTime.setText(timeStr + "");
			tvContent.setText(mTask.Content + "");
		}

		ivCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(animDrawable!=null)
				{
					animDrawable.stop();
				}

				finish();
			}
		});
		
		ivComplete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ivComplete.setImageResource(R.drawable.ico_check_green);
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						mZlServiceHelper.UpdateTaskStatus(mTask.Id, 3);
					}
				}).start();
				
				finish();
			}
		});
	}

	// 让动画随着界面启动开始动起来，必须放在onWindowFocusChanged生命周期内
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(animDrawable!=null){
			animDrawable.start();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mPlayer != null) {
			mPlayer.stop();
		}

		if (mVibrator != null) {
			mVibrator.cancel();
		}
	}
}
