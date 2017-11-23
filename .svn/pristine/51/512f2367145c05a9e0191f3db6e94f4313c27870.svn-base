package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cedarhd.adapter.ApplyViewPagerAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.constants.PreferencesConfig;
import com.cedarhd.helpers.SharedPreferencesHelper;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 新版申请与审批页面 使用ViewPager+Fragment方式
 * 
 * 旧版 ApplyListFragmentActivity
 * 
 * @author kjx
 * @since 2014/07/22 15:31
 */
public class ApplyListFragmentActivity extends BaseActivity {

	private Context mContext;
	private boolean mIsAdd;// 是否可以新建

	private Resources resource;
	private FragmentManager fm;
	private ViewPager vPager;
	private ApplyViewPagerAdapter adapter;
	private TextView tvPending;// 待我审批
	private TextView tvMyApply;// 我的申请
	private TextView tvApprove;// 所有
	private View viewPending;
	private View viewMyApply;
	private View viewApprove;

	private PopupWindow popupWindow;
	private View popupWindow_view;
	// 表单保存成功
	public static final int REQUEST_CODE_NEW_FORM = 11;

	public final int REQUEST_CODE_ASKFOR_ME = 12;// 待我审批
	public static final int APPROVAL_RESULT_SUCCEED = 13;

	private List<TextView> tvList = new ArrayList<TextView>();
	private List<View> viewList = new ArrayList<View>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apply_list);

		initData();
		initViews();
	}

	private void initData() {
		mContext = ApplyListFragmentActivity.this;
		SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(
				mContext, PreferencesConfig.APP_USER_INFO);
		String permission = sharedPreferencesHelper
				.getValue(PreferencesConfig.POINT_PERMISSION);
		// permission += ",734,"; // Test
		if (!TextUtils.isEmpty(permission) && permission.contains(",734,")) {
			mIsAdd = true;
		}
	}

	private void initViews() {
		resource = getResources();
		fm = getSupportFragmentManager();
		adapter = new ApplyViewPagerAdapter(fm);
		tvPending = (TextView) findViewById(R.id.tv_pending_apply_list);
		tvMyApply = (TextView) findViewById(R.id.tv_my_apply_list);
		tvApprove = (TextView) findViewById(R.id.tv_approve_list);
		viewPending = findViewById(R.id.view_pending_apply_list);
		viewMyApply = findViewById(R.id.view_my_apply_list);
		viewApprove = findViewById(R.id.view_approve_apply_list);
		tvList.add(tvPending);
		tvList.add(tvMyApply);
		tvList.add(tvApprove);
		viewList.add(viewPending);
		viewList.add(viewMyApply);
		viewList.add(viewApprove);

		vPager = (ViewPager) findViewById(R.id.vp_apply_list);
		vPager.setAdapter(adapter);

		tvPending.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTab();
				setCurrentPager(0);
			}
		});
		tvMyApply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTab();
				setCurrentPager(1);
			}
		});
		tvApprove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTab();
				setCurrentPager(2);
			}
		});
		ImageView ImageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		ImageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ImageView ivNew = (ImageView) findViewById(R.id.imageViewNew);
		ivNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				askFroMore();
			}
		});

		if (mIsAdd) {
			ivNew.setVisibility(View.VISIBLE);
		} else {
			ivNew.setVisibility(View.GONE);
		}

		vPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int pos) {
				setTab();
				tvList.get(pos).setTextColor(
						resource.getColor(R.color.theme_text));
				viewList.get(pos).setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageScrolled(int pos, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int pos) {

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == APPROVAL_RESULT_SUCCEED) {
			if (requestCode == REQUEST_CODE_ASKFOR_ME) {
				adapter.notifyDataSetChanged();
				LogUtils.i("onActivityResult",
						"onActivityResult REQUEST_CODE_ASKFOR_ME");
			}
		}

		if (resultCode == REQUEST_CODE_NEW_FORM
				&& requestCode == REQUEST_CODE_NEW_FORM) {
			LogUtils.i("onActivityResult", "REQUEST_CODE_NEW_FORM---表单保存成功");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 设置顶部页标签切换颜色
	 */
	private void setTab() {
		tvPending.setTextColor(getResources().getColor(R.color.gray));
		tvMyApply.setTextColor(getResources().getColor(R.color.gray));
		tvApprove.setTextColor(getResources().getColor(R.color.gray));
		viewPending.setVisibility(View.INVISIBLE);
		viewMyApply.setVisibility(View.INVISIBLE);
		viewApprove.setVisibility(View.INVISIBLE);
	}

	/**
	 * 初始化PopupWindow
	 */
	protected void initPopuptWindow() {
		// 填充pop视图
		popupWindow_view = getLayoutInflater().inflate(R.layout.pop, null,
				false);
		popupWindow = new PopupWindow(popupWindow_view,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		// 设置动画效果
		popupWindow.setAnimationStyle(R.style.AnimationFade);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 点击其他地方消失
		popupWindow_view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
					popupWindow = null;
				}
				return false;
			}
		});
		LinearLayout askforleave_approve = (LinearLayout) popupWindow_view
				.findViewById(R.id.askforleave_approve);
		LinearLayout askforleave_leave = (LinearLayout) popupWindow_view
				.findViewById(R.id.askforleave_leave);
		LinearLayout askforleave_more = (LinearLayout) popupWindow_view
				.findViewById(R.id.askforleave_more);

		// 报销控件监听
		askforleave_approve.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("id", 0);
				bundle.putInt("typeId", 115);// 115表示报销申请单
				bundle.putString("dataId", "0");
				bundle.putString("typeName", "报销单");
				intent.putExtras(bundle);
				intent.setClass(ApplyListFragmentActivity.this,
						CreateVmFormActivity.class);
				startActivityForResult(intent, REQUEST_CODE_NEW_FORM);
				popupWindow.dismiss();
			}
		});

		// 请假控件监听
		askforleave_leave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("id", 0);
				bundle.putInt("typeId", 110);// 110表示请假单
				bundle.putString("dataId", "0"); // data=0表示新建
				bundle.putString("typeName", "手机请假单");
				intent.putExtras(bundle);
				intent.setClass(ApplyListFragmentActivity.this,
						CreateVmFormActivity.class);
				startActivityForResult(intent, REQUEST_CODE_NEW_FORM);
				popupWindow.dismiss();
			}
		});

		// 更多控件监听
		askforleave_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				askFroMore();
				popupWindow.dismiss();
			}
		});
	}

	/**
	 * 设置当前显示页面
	 */
	public void setCurrentPager(int pos) {
		vPager.setCurrentItem(pos);
		tvList.get(pos).setTextColor(resource.getColor(R.color.theme_text));
		viewList.get(pos).setVisibility(View.VISIBLE);
	}

	/**
	 * 查看更多
	 */
	private void askFroMore() {
		Intent intent = new Intent();
		// intent.setClass(ApplyListFragmentActivity.this,
		// AskForLeaveMoreFormActivity.class);
		// startActivityForResult(intent, REQUEST_CODE_NEW_FORM);
		intent.setClass(ApplyListFragmentActivity.this,
				AskForLeaveMoreFormFragmentActivity.class);
		startActivity(intent);
	}
}
