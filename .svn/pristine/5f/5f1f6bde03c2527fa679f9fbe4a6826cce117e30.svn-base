package com.cedarhd;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.LunTanDiscussListHelper;
import com.cedarhd.models.论坛回帖;

import java.util.List;

/**
 * 评论列表
 * 
 * @author Administrator
 * 
 */
public class CompanySpaceDiscussActivity extends BaseActivity {
	private LinearLayout ll_discuss;
	private ListView lv_discuss;
	List<论坛回帖> listDiscuss;
	private Context context;
	public static final String TAG = "CompanySpaceDiscussActivity";
	private ImageView ivcancel;
	private ImageView ivnew;
	private LunTanDiscussListHelper discussListHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discuss_task);
		Bundle bundle = getIntent().getExtras();
		// m帖子 = (帖子) bundle.get(TaskInfoActivity.TAG);
		listDiscuss = (List<论坛回帖>) bundle.get(CompanySpaceActivity.TAG);
		findviews();
		discussListHelper.setmList(listDiscuss);
	}

	private void findviews() {
		// TODO Auto-generated method stub
		ll_discuss = (LinearLayout) findViewById(R.id.rl_discuss_content_task_info);
		lv_discuss = (ListView) findViewById(R.id.lv_discuss_task_info);
		// zlServiceHelper = new ZLServiceHelper();
		context = CompanySpaceDiscussActivity.this;
		ivcancel = (ImageView) findViewById(R.id.ivCance_task1);
		ivcancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		ivnew = (ImageView) findViewById(R.id.ivDone_task1);
		ivnew.setVisibility(View.GONE);
		if (discussListHelper == null) {
			discussListHelper = new LunTanDiscussListHelper(context,
					listDiscuss, lv_discuss, ll_discuss);
		}
	}
}
