package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.adapter.SpaceViewPagerAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.fragment.AskMeFrament;

import java.util.ArrayList;
import java.util.List;

/***
 * 空间模块
 * 
 * @author KJX
 * 
 */
public class SpaceFragmentActivity extends BaseActivity {
	private final String[] title = new String[] { "公司空间", "个人空间" };
	private List<Fragment> listFragment = new ArrayList<Fragment>();
	private Resources resource;
	public static final int REQUEST_CODE_NEW_COMPANYSPACE = 0;
	private FragmentManager fm;
	private int currentPage = 0;
	private ViewPager vPager;
	private SpaceViewPagerAdapter adapter;
	private Context context;
	private TextView tvTitle;// 标题
	private TextView tv_companyspace;// 公司空间
	private TextView tv_personalspace;// 个人空间
	ImageView ivNew;
	private View view_company;
	private View view_personal;
	private List<TextView> tvList = new ArrayList<TextView>();
	private List<View> viewList = new ArrayList<View>();

	// private CompanySpaceFragment personalFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.companyspacelist02);
		findViews();
		initView();
	}

	// 初始化view
	private void findViews() {
		// TODO Auto-generated method stub
		resource = getResources();
		fm = getSupportFragmentManager();
		context = SpaceFragmentActivity.this;
		// personalFragment = new CompanySpaceFragment();
		// listFragment.add(personalFragment);
		Fragment fragment2 = new AskMeFrament();
		listFragment.add(fragment2);
		adapter = new SpaceViewPagerAdapter(fm, listFragment);
		tvTitle = (TextView) findViewById(R.id.spacetitle);
		tv_companyspace = (TextView) findViewById(R.id.tv_companyspace_list);
		tv_personalspace = (TextView) findViewById(R.id.tv_personalspace_list);
		view_company = findViewById(R.id.view_companyspace_list);
		view_personal = findViewById(R.id.view_personalspace_list);
		tvList.add(tv_companyspace);
		tvList.add(tv_personalspace);
		viewList.add(view_company);
		viewList.add(view_personal);
		vPager = (ViewPager) findViewById(R.id.vp_space_list);
		vPager.setAdapter(adapter);
		tv_companyspace.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setTab();
				currentPage = 0;
				setCurrentPager(0);
				tvTitle.setText("公司空间");
			}
		});
		tv_personalspace.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setTab();
				currentPage = 1;
				setCurrentPager(1);
				tvTitle.setText("个人空间");
			}
		});
		// 返回上一页
		ImageView ImageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		ImageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ivNew = (ImageView) findViewById(R.id.imageViewNew);
		ivNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				add帖子();
			}

		});
		vPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int pos) {
				setTab();

				currentPage = pos;
				tvList.get(pos).setTextColor(
						resource.getColor(R.color.theme_text));
				viewList.get(pos).setVisibility(View.VISIBLE);
				tvTitle.setText(title[pos]);
			}

			@Override
			public void onPageScrolled(int pos, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int pos) {

			}
		});
	}

	private void initView() {
		String permission = getIntent().getStringExtra("Permissions");
		if (!TextUtils.isEmpty(permission) && permission.contains("402")) {
			ivNew.setVisibility(View.VISIBLE);
		}
	}

	protected void add帖子() {
		// TODO Auto-generated method stub
		if (currentPage == 0) {
			// 新建工作日志
			Intent intent = new Intent(context, CompanySpaceNewActivity.class);
			startActivityForResult(intent, REQUEST_CODE_NEW_COMPANYSPACE);
		} else if (currentPage == 1) {
			// 新建周工作总结报告
			Toast.makeText(context, "新建部门帖子暂未开通", Toast.LENGTH_SHORT).show();
		} else if (currentPage == 2) {
			Toast.makeText(context, "新建个人帖子暂未开通", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 设置顶部页标签切换颜色
	 */
	private void setTab() {
		tv_companyspace.setTextColor(getResources().getColor(R.color.gray));
		tv_personalspace.setTextColor(getResources().getColor(R.color.gray));
		view_company.setVisibility(View.INVISIBLE);
		view_personal.setVisibility(View.INVISIBLE);
	}

	/**
	 * 设置当前显示页面
	 */
	public void setCurrentPager(int pos) {
		vPager.setCurrentItem(pos);
		tvList.get(pos).setTextColor(resource.getColor(R.color.theme_text));
		viewList.get(pos).setVisibility(View.VISIBLE);
	}

}
