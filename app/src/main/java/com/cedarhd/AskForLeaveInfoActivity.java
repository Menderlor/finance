package com.cedarhd;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.流程;

@Deprecated
public class AskForLeaveInfoActivity extends BaseActivity {

	ZLServiceHelper mZLServiceHelper = new ZLServiceHelper();
	ListView mListView;
	流程 mAskForLeave;
	private AddImageHelper addImageHelper;

	TextView mTextViewTitle;
	TextView mTextViewTime;
	TextView mTextViewPublisherName;
	TextView mTextViewReceiverName;
	TextView mTextViewContent;
	public TextView mtextViewUserId;
	public TextView mtextViewTimeType;
	public TextView mtextViewVacationType;
	public TextView mtextViewDeadline;
	// public ImageView imageViewNew;
	public HorizontalScrollViewAddImage addImageView_attachFileName;
	private Handler mHanlder = new Handler();
	private static final String PATH = Environment
			.getExternalStorageDirectory() + "/DCIM";

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.askforleaveinfo);

		Bundle bundle = this.getIntent().getExtras();
		mAskForLeave = (流程) bundle.getSerializable("Flow");

		findViews();
		setOnClickListener();
		Init();

		new Thread(new Runnable() {
			@Override
			public void run() {
				// 判断是否已读
				// if (mAskForLeave.Read==null) {
				// mZLServiceHelper.ReadFlow(mAskForLeave,
				// AskForLeaveInfoActivity.this);
				// }else {
				// if (!mAskForLeave.Read
				// .contains("'" + Global.mUser.Id + "'")) {
				// mZLServiceHelper.ReadFlow(mAskForLeave,
				// AskForLeaveInfoActivity.this);
				// }
				// }
			}
		}).start();
	}

	void Init() {
		// 请假
		// mTextViewTitle.setText(mAskForLeave.getEmployee()+"流程单");
		// mTextViewTime.setText(mAskForLeave.getUpdateTime());//DateTimeUtil.ConvertLongDateToString(mAskForLeave.ReleaseTime));
		// mTextViewPublisherName.setText(mAskForLeave.getEmployee());
		// mTextViewReceiverName.setText("接收人:" );
		// mTextViewContent.setText(mAskForLeave.getAskForLeaveCase());
		// mtextViewUserId.setText(""+mAskForLeave.getUserId());
		// mtextViewTimeType.setText("时间类型:"+mAskForLeave.getTimeType());
		// mtextViewVacationType.setText(mAskForLeave.getVacationType());
		// mtextViewDeadline.setText("流程天数:"+mAskForLeave.getAskForLeaveDeadline());

		mTextViewTitle.setText(mAskForLeave.getClassTypeName());
		mTextViewTime.setText("创建时间:" + mAskForLeave.getCraeteDate());// DateTimeUtil.ConvertLongDateToString(mAskForLeave.ReleaseTime));
		mTextViewPublisherName.setText("创建人:" + mAskForLeave.getCreateName());
		mTextViewReceiverName.setText("接收人:");
		mTextViewContent.setText("下个步骤:" + mAskForLeave.getNextStep());
		mtextViewUserId.setText("创建人ID:" + mAskForLeave.getCreate());
		mtextViewTimeType.setText("上步完成时间:"
				+ mAskForLeave.getUpStepCompleteDate());
		mtextViewVacationType.setText("状态:" + mAskForLeave.getCurrentState());
		mtextViewDeadline.setText("下步审核人:" + mAskForLeave.getNextStepAudit());

		// BitmapFactory.Options opts = new BitmapFactory.Options();
		// opts.inJustDecodeBounds = true;
		// BitmapFactory.decodeFile(PATH + "/" +
		// mAskForLeave.AttachFileName,opts);
		// int picWidth = opts.outWidth;
		// int picHeight = opts.outHeight;
		//
		// int sampleSize =MyBitmapUtils.calcSampleSize(picWidth, picHeight,
		// 100, 100);
		// opts.inSampleSize = sampleSize;
		// opts.inJustDecodeBounds = false;
		//
		// Bitmap bitmap = BitmapFactory.decodeFile(PATH+ "/" +
		// mAskForLeave.AttachFileName, opts);
		// imageView_attachFileName.setImageBitmap(bitmap);

		// addImageHelper = new AddImageHelper(this, this,
		// addImageView_attachFileName,
		// mAskForLeave.PhotoSerialNo, false);
	}

	public void setOnClickListener() {

		ImageView imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		imageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 给附件按钮("+"号的按钮，以后会改为附件)添加监听
		// imageViewNew.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(Intent.ACTION_VIEW);//调用系统的图片查看器
		//
		// Uri mUri = Uri.fromFile(new File(PATH,
		// mAskForLeave.getAttachFileName()));//图片的路径
		//
		// intent.setDataAndType(mUri, "image/*");//设置数据和格式
		//
		// startActivity(intent);
		//
		// }
		// });
		// 给imageView加监听
		// imageView_attachFileName.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(Intent.ACTION_VIEW);//调用系统的图片查看器
		//
		// Uri mUri = Uri.fromFile(new File(PATH,
		// mAskForLeave.getAttachFileName()));//图片的路径
		//
		// intent.setDataAndType(mUri, "image/*");//设置数据和格式
		//
		// startActivity(intent);
		// }
		// });

	}

	public void findViews() {
		mTextViewTitle = (TextView) findViewById(R.id.textViewTitle);
		mTextViewTime = (TextView) findViewById(R.id.textViewTime);
		mTextViewPublisherName = (TextView) findViewById(R.id.textViewPublisherName);
		mTextViewReceiverName = (TextView) findViewById(R.id.textViewReceiverName);
		mTextViewContent = (TextView) findViewById(R.id.textViewContent);
		mtextViewUserId = (TextView) findViewById(R.id.textViewUserId);
		mtextViewTimeType = (TextView) findViewById(R.id.textViewTimeType);
		mtextViewVacationType = (TextView) findViewById(R.id.textViewVacationType);
		mtextViewDeadline = (TextView) findViewById(R.id.textViewDeadline);
		// imageViewNew = (ImageView) findViewById(R.id.imageViewNew);
		// imageView_attachFileName = (ImageView)
		// findViewById(R.id.imageView_attachFileName);
		addImageView_attachFileName = (HorizontalScrollViewAddImage) findViewById(R.id.addImageView_attachFileName);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 处理照相机返回的数据
		if (resultCode == RESULT_OK) {

			if (requestCode == addImageHelper.CAMERA_TAKE_HELPER) {
				addImageHelper.refresh();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}