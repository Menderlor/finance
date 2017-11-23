package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.adapter.User_Select_LetterListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.CheckBoxListViewItem;
import com.cedarhd.control.MyLetterListView;
import com.cedarhd.control.MyLetterListView.OnTouchingLetterChangedListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.PictureUtils;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Data;
import com.cedarhd.models.Latest;
import com.cedarhd.models.User;
import com.cedarhd.models.部门;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;
import com.j256.ormlite.dao.Dao;

import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 选择员工 新页面（替代User_SelectActivity）
 * 
 * @author BOHR
 * 
 */
public class User_SelectActivityNew extends BaseActivity {

	private Context context;
	/** 搜索按钮 */
	private ImageView iv_search;

	/** 搜索的内容 */
	private String search_text;//

	/** 搜索框 */
	private EditText et_search;//

	/** 搜索框 和 搜过按钮 */
	private RelativeLayout rl_search;//
	/** 用来搜索过滤员工 */
	private LinearLayout ll_search_user;

	/** 当前viewpager的页面 页数 */
	private int mCurrentPage;//

	/** 部门员工ListView */
	private ListView mDptLv;

	/** 按部门查询的员工列表 */
	private List<User> list_dpt;//

	/** viewpager的滑动页面 */
	private ViewPager viewPager;//

	/** 用来存储viewpager里面的界面选项 */
	private List<View> page_list;

	private View latest, all, department;
	private List<TextView> list_textviews;
	private TextView tv_all, tv_department, tv_latest;
	private LayoutInflater inflater;
	private TextView tv_qiehuan;// 切换部门
	private CheckBox checkBox;// 设置全选，反选按钮
	private Dao<User, Integer> userDao;// user的Dao
	private Dao<Latest, Integer> latestDao;// latest的dao
	private ORMDataHelper ormDataHelper;// ormdataHelper的对象 用来调用其中getUserDao方法

	/**
	 * 从数据库中查询到的员工数据，按部门和按所有两个页卡的数据源
	 */
	private List<User> mList;

	/** 最近员工列表 */
	private List<CheckBoxListViewItem> mLatest_items;

	ZLServiceHelper mDataHelper = new ZLServiceHelper();

	private ListView personLv = null;// 全部列表的listview
	private ListView latestLv = null;// 最近列表的listView
	private MyLatestAdapter myLatestAdapter;
	private ImageView imageViewCancel;// 返回
	private ImageView imageViewDone;// 确定按钮

	/** 供选择的用户 */
	private List<CheckBoxListViewItem> mListViewItems = new ArrayList<CheckBoxListViewItem>();
	private List<CheckBoxListViewItem> mCheckUsers; // 选中的用户
	private PictureUtils handlerUtils;
	private User_Select_LetterListViewAdapter mUser_Select_LetterListViewAdapter;
	User_Select_LetterListViewAdapter adapter;// 查询的时候用的
	TextView mTextViewSortName;
	private boolean flag = false; // 标志位：判断是否是其他页面打开用来选择员工(单选)

	private TextView overlay;
	private MyLetterListView letterListView;

	/** 是否单选 key */
	public static final String SELECT_EMPLOYEE = "SlectEmployee";
	private Handler handler;
	private OverlayThread overlayThread;

	public static final int USER_SELECT_REQUEST_CODE = 0;
	public static final int COMMON_SORT_SELECT_RESULT_CODE = 0;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhh:mm:ss");// 日期格式化

	HandlerInitUsers handlerInit;
	// private String lastUpdateTime="20130101";
	private long lastUpdateTime = 0;

	// private
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_select);
		context = this;
		initData();
		findViews();
		setOnClickListener();
		initAllUserList();
		initDepartmentUserList();
		initLatestUserList();
	}

	/** 初始化数据变量 */
	private void initData() {
		// userDao的初始化以及操作
		userDao = getHelper().getUserDao();
		handlerInit = new HandlerInitUsers();
		page_list = new ArrayList<View>();
		List<Latest> mLatestList = new ArrayList<Latest>();
		inflater = LayoutInflater.from(this);
		all = inflater.inflate(R.layout.all, null);
		latest = inflater.inflate(R.layout.latest, null);
		department = inflater.inflate(R.layout.department, null);
		handlerUtils = new PictureUtils();
		mCheckUsers = new ArrayList<CheckBoxListViewItem>();
		flag = getIntent().getBooleanExtra(SELECT_EMPLOYEE, false);
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null && bundle.keySet().contains("UserSelectId")) {
			String UserSelectId = bundle.getString("UserSelectId");
			if (UserSelectId != null && UserSelectId.length() > 0) {
				// TODO
				// SelectUsers(UserSelectId);
			}
		}
	}

	public static long changeTimeToInt(String time) {
		String last = time.replaceAll("-", " ");
		last = last.replace(":", " ");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd hh MM ss");
		try {
			Date date = sdf.parse(last);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;

	}

	/** 初始化 所有 用户列表 */
	private void initAllUserList() {
		try {
			// 查询所有的用户
			mList = userDao.queryForAll();
			handler = new Handler();
			overlayThread = new OverlayThread();
			initOverlay();
			// 直接先获取本地数据
			InitCheckBoxListView("err");
			convertDataToLetterList(mList);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (mList.size() == 0 && HttpUtils.IsHaveInternet(this)) {
			// 有网的时候,网络更新员工列表
			InitCheckBoxListView("-1");
		}

	}

	/** 初始化 部门 用户列表 */
	private void initDepartmentUserList() {
		try {
			String deptId = Global.mUser.Department + "";
			StringBuilder sBuilder = new StringBuilder("");
			mDataHelper.getAllDeptAndSubDept(deptId, context, sBuilder);
			LogUtils.i("sBuilder", sBuilder.toString());
			String depts = sBuilder.toString();
			if (TextUtils.isEmpty(depts)) {
				// 没有下级子部门
				list_dpt = userDao.queryBuilder().where()
						.eq("Department", deptId).query();
			} else {
				depts += deptId;
				List<?> list = CollectionUtils.arrayToList(depts.split(","));
				// 数据没有dptId 查询id
				list_dpt = userDao.queryBuilder().where()
						.in("Department", list).query();
			}
			// 得到登录用户所在部门的名称
			Dao dao = ormDataHelper.getDao(部门.class);
			List<部门> list_bumen = dao.queryBuilder().where()
					.eq("编号", Global.mUser.Department).query();
			LogUtils.i("detId", Global.mUser.UserName + " "
					+ Global.mUser.Department);
			if (list_bumen != null && list_bumen.size() > 0) {
				tv_qiehuan.setText(list_bumen.get(0).get名称());
			}
			LogUtils.d("---->>>", "1-->>>" + list_dpt.size());
			mDptLv.setAdapter(mUser_Select_LetterListViewAdapter);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** 初始化 最近员工列表 */
	private void initLatestUserList() {
		try {
			latestDao = ormDataHelper.getLatestDao();
			List<Latest> mLatestList = latestDao.queryBuilder()
					.orderBy("date", false).limit((long) 20).query();
			mLatest_items = convertDataLatestToCheckBoxListViewItem(mLatestList);
			myLatestAdapter = new MyLatestAdapter();
			latestLv.setAdapter(myLatestAdapter);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void Filter(String Id) {
		InitCheckBoxListView(Id);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case USER_SELECT_REQUEST_CODE:
			if (resultCode == COMMON_SORT_SELECT_RESULT_CODE) {
				// 取出字符串
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					String filter = bundle.getString("SortId");
					mTextViewSortName.setText("当前部门："
							+ bundle.getString("SortName"));
					// 查询
					Filter(filter);
				}
			}
			break;
		case 1000: // 选择部门
			if (resultCode == 1001) {
				viewPager.setCurrentItem(1);
				mCurrentPage = 1;
				// setBackgroud(1);
				setTextColor(1);
				tv_qiehuan.setText(data.getStringExtra("DeptSelectName"));
				LogUtils.i("DeptSelectId", data.getStringExtra("DeptSelectId"));
				initDptData(data);
			}
			break;
		default:
			break;
		}

	}

	// 初始化查询按部门的数据
	private void initDptData(Intent data) {
		try {
			String deptId = data.getStringExtra("DeptSelectId");
			StringBuilder sBuilder = new StringBuilder("");
			mDataHelper.getAllDeptAndSubDept(deptId, context, sBuilder);
			LogUtils.i("sBuilder", sBuilder.toString());
			String depts = sBuilder.toString();
			if (TextUtils.isEmpty(depts)) {
				// 没有下级子部门
				list_dpt = userDao.queryBuilder().where()
						.eq("Department", deptId).query();
			} else {
				depts += deptId;
				List<?> list = CollectionUtils.arrayToList(depts.split(","));
				// 数据没有dptId 查询id
				list_dpt = userDao.queryBuilder().where()
						.in("Department", list).query();
			}
			convertDataToLetterList(list_dpt);
			mUser_Select_LetterListViewAdapter.notifyDataSetChanged();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 页卡内容适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(page_list.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			container.addView(page_list.get(position));
			return page_list.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return page_list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
	}

	// 设置viewpager 标签的按钮背景色
	private void setBackgroud(int position) {
		for (int i = 0; i < list_textviews.size(); i++) {
			if (i == position) {
				list_textviews.get(i).setBackgroundColor(0xFFECEADE);
			} else {
				list_textviews.get(i).setBackgroundColor(0x00000000);
			}
		}
	}

	// 设置viewpager 标签按钮的字体颜色
	private void setTextColor(int position) {
		for (int i = 0; i < list_textviews.size(); i++) {
			if (i == position) {
				// list_textviews.get(i).setTextColor(R.color.nav_title);
				list_textviews.get(i).setTextColor(0XFF71B8A9);
			} else {
				// list_textviews.get(i).setTextColor(R.color.zl_title);
				list_textviews.get(i).setTextColor(0XFF202020);
			}
		}
	}

	public void setOnClickListener() {
		// 搜索按钮点击
		iv_search = (ImageView) findViewById(R.id.iv_search);
		iv_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				search_text = et_search.getText().toString().trim();
				et_search.setText("");
				if (TextUtils.isEmpty(search_text)) {
					Toast.makeText(getApplicationContext(), "请输入搜索内容",
							Toast.LENGTH_SHORT).show();
				} else {
					switch (mCurrentPage) {
					case 0: // 最近
						try {
							List<Latest> mLatestList = latestDao.queryBuilder()
									.where()
									.like("userName", "%" + search_text + "%")
									.query();
							myLatestAdapter.notifyDataSetChanged();
							if (mLatestList.size() == 0) {
								Toast.makeText(getApplicationContext(),
										"没有查询到您要搜索的内容~", Toast.LENGTH_SHORT)
										.show();
							}

							// 918
							// initDataLatest(list_latestOrmlite);

						} catch (SQLException e) {
							e.printStackTrace();
						}
						break;
					case 1: // 部门
						try {
							list_dpt = userDao.queryBuilder().where()
									.like("UserName", "%" + search_text + "%")
									.query();
							if (list_dpt.size() == 0) {
								Toast.makeText(getApplicationContext(),
										"没有查询到您要搜索的内容~", Toast.LENGTH_SHORT)
										.show();
							}

							// 918
							// initData(list_dpt);

							convertDataToLetterList(mList);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case 2: // s所有
						try {
							mList = userDao.queryBuilder().where()
									.like("UserName", "%" + search_text + "%")
									.query();

							// 918
							// initData(list_ormlite);
							convertDataToLetterList(mList);
							mCheckUsers.clear();
							// 查询所有员工中要搜索的人员
							search_all();

						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;

					}
				}

			}

			private void search_all() {
				mListViewItems.clear();
				for (User s : mList) {
					CheckBoxListViewItem cbItem1 = new CheckBoxListViewItem(
							s.AvatarURI, s.Id, s.UserName, false, s.Department);
					mListViewItems.add(cbItem1);
				}

				mUser_Select_LetterListViewAdapter.setmList(mListViewItems);
				mUser_Select_LetterListViewAdapter.notifyDataSetChanged();

				// adapter = new User_Select_LetterListViewAdapter(
				// Slect_User_Activity.this,
				// R.layout.user_select_listviewlayout, mListViewItems,
				// myAdapterCBListener);
				// personList.setAdapter(adapter);
			}
		});
		letterListView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());

		mTextViewSortName = (TextView) findViewById(R.id.textViewSortName);

		// 切换部门的初始化操作
		tv_qiehuan = (TextView) findViewById(R.id.tv_qiehuan);
		tv_qiehuan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 打开部门选择页面
				Intent intent = new Intent(User_SelectActivityNew.this,
						DepartmentActivity.class);
				startActivityForResult(intent, 1000);
			}
		});
		// viewPager 初始化操作
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(new MyPagerAdapter());
		viewPager.setCurrentItem(2);
		mCurrentPage = 2;
		// setBackgroud(2);
		setTextColor(2);
		if (flag) {
			checkBox.setVisibility(View.GONE);
		} else {
			checkBox.setVisibility(View.VISIBLE);
		}
		tv_qiehuan.setVisibility(View.GONE);
		// 页面滑动监听
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				mCurrentPage = arg0;
				switch (arg0) {
				case 0:
					mListViewItems.clear();
					// initDataLatest(list_latestOrmlite);

					latestLv.setAdapter(myLatestAdapter);

					// setBackgroud(0);
					setTextColor(0);
					tv_qiehuan.setVisibility(View.GONE);
					checkBox.setVisibility(View.GONE);
					ll_search_user.setVisibility(View.GONE);

					break;
				case 1:
					mListViewItems.clear();
					// initData(list_dpt);

					convertDataToLetterList(list_dpt);

					// setBackgroud(1);
					setTextColor(1);
					rl_search.setVisibility(View.GONE);
					tv_qiehuan.setVisibility(View.VISIBLE);
					checkBox.setVisibility(View.GONE);
					ll_search_user.setVisibility(View.VISIBLE);

					break;
				case 2:
					// mListViewItems.clear();
					// TODO
					convertDataToLetterList(mList);
					// setBackgroud(2);
					setTextColor(2);
					tv_qiehuan.setVisibility(View.GONE);
					ll_search_user.setVisibility(View.VISIBLE);
					rl_search.setVisibility(View.VISIBLE); // 搜索功能可用

					if (flag) {
						checkBox.setVisibility(View.GONE);
					} else {
						checkBox.setVisibility(View.VISIBLE);
					}
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		// 返回键的操作
		imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		imageViewCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		imageViewDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					SelectAll();
				} else {
					UnSelectAll();
				}
			}
		});

		/**
		 * 全部员工列表
		 */
		personLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				photoDispose(mListViewItems.get(position));
				if (flag) {
					onBackPressed();
				}
			}
		});

		/**
		 * 按部门选择员工
		 */
		mDptLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				for (int i = 0; i < mListViewItems.size(); i++) {
					LogUtils.d("photoDI按部门选择员工", mListViewItems.get(i).Name);
				}
				photoDispose(mListViewItems.get(position));
				if (flag) {
					onBackPressed();
				}
			}
		});

		// 最近列表点击事件
		// latestList.setEnabled(false);// 不可点击
		latestLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				photoDispose(mLatest_items.get(position));
				// CheckBoxListViewItem item = list_latest_items.get(position);
				// list_latest_items.get(position).IsChecked = !item.IsChecked;
				myLatestAdapter.notifyDataSetChanged();
				if (flag) {
					onBackPressed();
				}
			}
		});
	}

	/**
	 * 选择员工
	 * 
	 * @param position
	 *            选中员工在list中的位置
	 */
	private void photoDispose(CheckBoxListViewItem item) {
		// CheckBoxListViewItem item = mListViewItems.get(position);
		for (int i = 0; i < mListViewItems.size(); i++) {
			LogUtils.i("photoDI", "" + mListViewItems.get(i).Name);
		}
		item.IsChecked = !item.IsChecked;// 取反操作
		if (item.IsChecked) {
			boolean isAdd = true;
			for (CheckBoxListViewItem cbItem : mCheckUsers) {
				if (cbItem.Id.equals(item.Id)) {
					isAdd = false;
				}
			}
			if (isAdd) {
				CheckBoxListViewItem cbItem = new CheckBoxListViewItem(
						item.pic_url, item.Id, item.Name, item.IsChecked,
						item.dptId);
				mCheckUsers.add(cbItem);
			}
		} else {
			try {
				List<CheckBoxListViewItem> delList = new ArrayList<CheckBoxListViewItem>();
				for (CheckBoxListViewItem cbItem : mCheckUsers) {
					if (cbItem.Id.equals(item.Id)) {
						delList.add(cbItem);
					}
				}
				mCheckUsers.removeAll(delList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		initAdapter();
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent();
		Bundle b = new Bundle();
		b.putString("UserSelectId", GetSelectedUserId());
		b.putString("UserSelectName", getSelectedUserName());
		i.putExtras(b);

		for (int j = 0; j < mCheckUsers.size(); j++) {
			Latest latest = new Latest();
			latest.setId(Integer.parseInt(mCheckUsers.get(j).getId()));
			latest.setId(Integer.parseInt(mCheckUsers.get(j).getId()));
			latest.setPic_url(mCheckUsers.get(j).getPic_url());
			latest.setUserName(mCheckUsers.get(j).getName());
			latest.setDate(System.currentTimeMillis());
			LogUtils.i("dept_UserSelectId",
					"--->>>" + Integer.parseInt(mCheckUsers.get(j).getId()));
			// boolean isCreate = true;
			// for (int k = 0; k < this.list_latestOrmlite.size(); j++) {
			// if (list_latestOrmlite.get(k).getId() == latest.getId()) {
			// isCreate = false;
			// }
			// }
			// if (isCreate) {
			// try {
			// latestDao.create(latest);
			// } catch (SQLException e) {
			// e.printStackTrace();
			// LogUtils.e("SQLException", e + "");
			// }
			// }

			try {
				// latestDao.createOrUpdate(latest);
				latestDao.createIfNotExists(latest);
			} catch (SQLException e) {
				e.printStackTrace();
				LogUtils.e("SQLException", e + "");
			}

		}
		User_SelectActivityNew.this.setResult(RESULT_OK, i);
		finish();
		super.onBackPressed();
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {
		@Override
		public void onTouchingLetterChanged(final String s) {
			if (mUser_Select_LetterListViewAdapter.alphaIndexer.get(s) != null) {
				int position = mUser_Select_LetterListViewAdapter.alphaIndexer
						.get(s);
				personLv.setSelection(position);
				overlay.setText(mUser_Select_LetterListViewAdapter.sections[position]
						+ "");
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				// 延迟一秒后执行，让overlay为不可见
				handler.postDelayed(overlayThread, 100);
			}
		}

	}

	/**
	 * 初始化汉语拼音首字母弹出提示框
	 */
	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.common_letter_overlay,
				null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	public void findViews() {
		// 确定键的操作
		imageViewDone = (ImageView) findViewById(R.id.imageViewDone);
		ll_search_user = (LinearLayout) findViewById(R.id.ll_search_user);
		// 搜索框
		et_search = (EditText) findViewById(R.id.editText1);
		rl_search = (RelativeLayout) findViewById(R.id.rl_search_select_user);
		et_search.requestFocus(); // 获得焦点
		// 全选框
		checkBox = (CheckBox) findViewById(R.id.checkBox1);
		// VIEWPAGER 相关的初始化
		page_list = new ArrayList<View>();
		inflater = LayoutInflater.from(this);
		all = inflater.inflate(R.layout.all, null);
		latest = inflater.inflate(R.layout.latest, null);
		department = inflater.inflate(R.layout.department, null);
		page_list.add(latest);
		page_list.add(department);
		page_list.add(all);

		// 初始化viewpager标签的相关控件
		list_textviews = new ArrayList<TextView>();
		tv_all = (TextView) findViewById(R.id.tv_all);
		tv_department = (TextView) findViewById(R.id.tv_department);
		tv_latest = (TextView) findViewById(R.id.tv_latest);
		// 所有按钮点击事件
		tv_all.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTextColor(2);
				viewPager.setCurrentItem(2);
				mCurrentPage = 2;
				tv_qiehuan.setVisibility(View.GONE);

				if (flag) {
					checkBox.setVisibility(View.GONE);
				} else {
					checkBox.setVisibility(View.VISIBLE);
				}
			}
		});
		// 按部门 按钮点击事件
		tv_department.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// mListViewItems.clear();
				// initData(list_dpt);
				// setBackgroud(1);
				viewPager.setCurrentItem(1);
				mCurrentPage = 1;
				tv_qiehuan.setVisibility(View.VISIBLE);
				checkBox.setVisibility(View.GONE);
			}
		});
		// 最近按钮的点击事件
		tv_latest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTextColor(0);
				viewPager.setCurrentItem(0);
				mCurrentPage = 0;
				tv_qiehuan.setVisibility(View.GONE);
				checkBox.setVisibility(View.GONE);
			}
		});
		list_textviews.add(tv_latest);
		list_textviews.add(tv_department);
		list_textviews.add(tv_all);

		letterListView = (MyLetterListView) all
				.findViewById(R.id.MyLetterListView01);

		personLv = (ListView) all.findViewById(R.id.listView1);
		latestLv = (ListView) latest.findViewById(R.id.lv_latest);
		latestLv.setAdapter(myLatestAdapter);
		mDptLv = (ListView) department.findViewById(R.id.listView1);
	}

	/**
	 * Latest的适配器，使用BaseAdapter
	 * 
	 * @author wangxiaojian
	 * 
	 */
	private class MyLatestAdapter extends BaseAdapter {
		/**
		 * @author wangxiaojian listView的性能优化
		 */

		private ViewHoulder holder;

		private class ViewHoulder {
			TextView tv_name;// 用户名
			ImageView iv_photo;// 用户头像
			TextView tv_time;// 时间
			ImageView iv_selected; // 选中
		}

		@Override
		public int getCount() {
			return mLatest_items.size();
		}

		@Override
		public CheckBoxListViewItem getItem(int pos) {
			return mLatest_items.get(pos);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if (view == null) {
				view = inflater.inflate(
						R.layout.user_selectlatest_listviewlayout, arg2, false);
				holder = new ViewHoulder();
				holder.tv_name = (TextView) view
						.findViewById(R.id.textView1_selectlatest);
				holder.iv_photo = (ImageView) view
						.findViewById(R.id.iv_photo_selectlatest);
				holder.tv_time = (TextView) view
						.findViewById(R.id.tv_time_selectlatest);
				holder.iv_selected = (ImageView) view
						.findViewById(R.id.iv_userSelect);
				view.setTag(holder);
			} else {
				holder = (ViewHoulder) view.getTag();
			}
			CheckBoxListViewItem item = mLatest_items.get(pos);
			holder.tv_name.setText(item.getName());
			holder.iv_photo.setTag(pos);
			if (!TextUtils.isEmpty(item.getPic_url())) {
				handlerUtils.setAvatar(item.getPic_url(), holder.iv_photo, pos);
			} else {
				holder.iv_photo.setImageResource(R.drawable.tx);
			}
			// holder.tv_time.setText(sdf.format(item..get(pos).getDate()));

			if (item.IsChecked) {
				holder.iv_selected.setVisibility(View.VISIBLE);
				view.setBackgroundResource(R.color.mail_unread_bg);
			} else {
				holder.iv_selected.setVisibility(View.INVISIBLE);
				view.setBackgroundResource(R.color.mail_read_bg);
			}
			return view;
		}
	}

	// 选择传入的Id
	void SelectUsers(String str) {
		for (CheckBoxListViewItem cbItem1 : mListViewItems) {
			if (!str.contains("'" + cbItem1.Id + "';")) {
				continue;
			}

			cbItem1.IsChecked = true;
			boolean isAdd = true;
			// 判断是否已经选择过了
			for (CheckBoxListViewItem cbItem2 : mCheckUsers) {
				if (cbItem2.Id.equals(cbItem1.Id)) {
					isAdd = false;
				}
			}
			//
			if (isAdd) {
				CheckBoxListViewItem cbItem = new CheckBoxListViewItem(
						cbItem1.pic_url, cbItem1.Id, cbItem1.Name,
						cbItem1.IsChecked, cbItem1.dptId);
				mCheckUsers.add(cbItem);
			}
		}
		initAdapter();
	}

	// 全部选择
	void SelectAll() {
		for (CheckBoxListViewItem cbItem1 : mListViewItems) {
			cbItem1.IsChecked = true;
			boolean isAdd = true;
			// 判断是否已经选择过了
			for (CheckBoxListViewItem cbItem2 : mCheckUsers) {
				if (cbItem2.Id.equals(cbItem1.Id)) {
					isAdd = false;
				}
			}
			//
			if (isAdd) {
				CheckBoxListViewItem cbItem = new CheckBoxListViewItem(
						cbItem1.pic_url, cbItem1.Id, cbItem1.Name,
						cbItem1.IsChecked, cbItem1.dptId);
				mCheckUsers.add(cbItem);
			}
		}
		initAdapter();
	}

	// //全部取消
	// void UnSelectAll(){
	// List<CheckBoxListViewItem> delList = new
	// ArrayList<CheckBoxListViewItem>();
	// for (CheckBoxListViewItem cbItem1 : mListViewItems) {
	// cbItem1.IsChecked = false;
	// for (CheckBoxListViewItem cbItem2 : mCheckUsers) {
	// if (cbItem2.Id.equals(cbItem1.Id)) {
	// delList.add(cbItem2);
	// }
	// }
	// }
	// mCheckUsers.removeAll(delList);
	// initAdapter();
	// }
	// 反选
	void UnSelectAll() {
		List<CheckBoxListViewItem> delList = new ArrayList<CheckBoxListViewItem>();
		for (CheckBoxListViewItem cbItem1 : mListViewItems) {
			cbItem1.IsChecked = !cbItem1.IsChecked;
			if (cbItem1.IsChecked) {
				CheckBoxListViewItem cbItem = new CheckBoxListViewItem(
						cbItem1.pic_url, cbItem1.Id, cbItem1.Name,
						cbItem1.IsChecked, cbItem1.dptId);
				mCheckUsers.add(cbItem);
			} else {
				for (CheckBoxListViewItem cbItem2 : mCheckUsers) {
					if (cbItem2.Id.equals(cbItem1.Id)) {
						delList.add(cbItem2);
						break;
					}
				}
			}
		}
		mCheckUsers.removeAll(delList);
		initAdapter();
	}

	private String GetSelectedUserId() {
		String str = "";
		for (CheckBoxListViewItem cb : mCheckUsers) {
			// TODO kjx UPDATE
			// str += "'" + cb.Id + "';";
			str += cb.Id + ",";
		}
		if (str.endsWith(",")) {
			str = str.substring(0, str.length() - 1);
		}
		LogUtils.i("---->>>", str);
		return str;
	}

	private String getSelectedUserName() {
		String str = "";
		for (CheckBoxListViewItem cb : mCheckUsers) {
			str += cb.Name + ",";
		}
		if (str.length() > 0) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	private String GetSelectUserId2() {
		String str = "";
		return str;
	}

	String GetSelectUserName2() {
		String str = "";

		return str;
	}

	// 设置overlay不可见
	private class OverlayThread implements Runnable {
		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}

	}

	// [start] CheckBoxListView
	// 数据初始化
	private void InitCheckBoxListView(final String department) {
		if (department.equals("err")) {

			if (mListViewItems == null)
				mListViewItems = new ArrayList<CheckBoxListViewItem>();

			if (mCheckUsers == null)
				mCheckUsers = new ArrayList<CheckBoxListViewItem>();

		}
		// else
		// mCheckUsers.clear();

		// 获取数据源
		if (!department.equals("err")) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					// String time=lastUpdateTime.substring(0,
					// 4)+"-"+lastUpdateTime.substring(4,
					// 6)+"-"+lastUpdateTime.substring(6, 8);
					try {
						mDataHelper.GetSys_User_Department(lastUpdateTime,
								handlerInit);
					} catch (Exception e) {
						Toast.makeText(User_SelectActivityNew.this, "获取员工信息异常",
								0).show();
					}
				}
			}).start();
		}
	}

	// 刷新适配器
	public void initAdapter() {
		// if (mUser_Select_LetterListViewAdapter == null) {
		// mUser_Select_LetterListViewAdapter = new
		// User_Select_LetterListViewAdapter(
		// User_SelectActivityNew.this,
		// R.layout.user_select_listviewlayout, mListViewItems,
		// myAdapterCBListener);
		// personList.setAdapter(mUser_Select_LetterListViewAdapter);
		// } else {
		// mUser_Select_LetterListViewAdapter.setmList(mListViewItems);
		// mUser_Select_LetterListViewAdapter.notifyDataSetChanged();
		// }

		if (mUser_Select_LetterListViewAdapter == null) {
			mUser_Select_LetterListViewAdapter = new User_Select_LetterListViewAdapter(
					User_SelectActivityNew.this,
					R.layout.user_select_listviewlayout, mListViewItems,
					myAdapterCBListener);
			personLv.setAdapter(mUser_Select_LetterListViewAdapter);
		} else {
			mUser_Select_LetterListViewAdapter.setmList(mListViewItems);
			mUser_Select_LetterListViewAdapter.notifyDataSetChanged();
		}
	}

	private View.OnClickListener myAdapterCBListener = new OnClickListener() {
		public void onClick(View v) {
			boolean isChecked = ((CheckBox) v).isChecked();
			CheckBoxListViewItem item = (CheckBoxListViewItem) ((CheckBox) v)
					.getTag();
			if (isChecked) {
				boolean isAdd = true;
				for (CheckBoxListViewItem cbItem : mCheckUsers) {
					if (cbItem.Id.equals(item.Id)) {
						isAdd = false;
					}
				}
				if (isAdd) {
					CheckBoxListViewItem cbItem = new CheckBoxListViewItem(
							item.pic_url, item.Id, item.Name, item.IsChecked,
							item.dptId);
					mCheckUsers.add(cbItem);
				}
			} else {
				try {
					List<CheckBoxListViewItem> delList = new ArrayList<CheckBoxListViewItem>();
					for (CheckBoxListViewItem cbItem : mCheckUsers) {
						if (cbItem.Id.equals(item.Id)) {
							delList.add(cbItem);
						}
					}
					mCheckUsers.removeAll(delList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	public class HandlerInitUsers extends Handler {
		public static final int GET_USERS_DATA_SUCCESS = 0;
		public static final int GET_USERS_DATA_FAILED = 1;

		@Override
		public void handleMessage(Message msg) {
			int whatMsg = msg.what;
			switch (whatMsg) {
			case GET_USERS_DATA_SUCCESS:
				LogUtils.i("--->>>", "netData download  success");
				List<Data> list_data = (List<Data>) msg.obj;
				List<User> list = new ArrayList<User>();
				// 将服务器返回的数据转化为User对象中的数据
				for (int i = 0; i < list_data.size(); i++) {
					Data data = list_data.get(i);
					// LogUtils.d("keno_json", data.toString());
					User user = new User();
					user.setId(data.getId() + "");
					user.setDptId(data.getDepartment());
					user.setUserName(data.getUserName());
					user.setAvatar(data.getAvatar());
					String result = data.getLastUpdateDate();
					long time = changeTimeToInt(result);
					user.setUpdateTime(time);
					user.setCorpId(data.getCorpId());
					list.add(user);
				}
				// 直接将服务器数据写到数据库
				// writeToOrmlite(list, list_ormlite_on);
				// 数据库操作服务器返回来的数据（更新、插入）
				// TODO
				updateOrmlite(list, mList);
				break;
			case GET_USERS_DATA_FAILED:
				break;
			}
			super.handleMessage(msg);
		}

		// 数据库更新操作
		public void updateOrmlite(List<User> list, List<User> list_ormlite) {
			// 比较从网络上获取的list中user的id 与数据库中数据的id是不是有一样的
			// 如果有 则更新操作，如果没有 则进行插入操作
			for (int i = 0; i < list.size(); i++) {
				int num = 0;
				for (int j = 0; j < list_ormlite.size(); j++) {
					if (list_ormlite.get(i).getCorpId() != Global.mUser.CorpId) {
						User user = new User();
						// 删除数据库中 其他单位的员工信息
						user.setCorpId(list_ormlite.get(i).getCorpId());
						try {
							userDao.delete(user);
							LogUtils.i("delete", "userDao delete");
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					if (list.get(i).getId().equals(list_ormlite.get(j).getId())) {
						try {
							// 修改数据库数据
							userDao.update(list.get(i));
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else {
						num++;
					}
				}
				if (num == list_ormlite.size()) {

					try {
						// 插入数据
						userDao.create(list.get(i));
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}
			}
			// 更新完成后 通知listview 数据发生改变
			try {
				list_ormlite = userDao.queryBuilder().where()
						.eq("CorpId", Global.mUser.CorpId).query();
				// list_ormlite=userDao.queryForAll();
				convertDataToLetterList(mList);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// 删除数据库操作
		public void deleteTableUser(List<User> list, List<User> list_ormlite) {
			// 比较从网络上获取的list中user的id 与数据库中数据的id是不是有一样的
			// 如果有 则更新操作，如果没有 则进行插入操作
			for (int i = 0; i < list.size(); i++) {
				int num = 0;
				for (int j = 0; j < list_ormlite.size(); j++) {
					if (list.get(i).getId().equals(list_ormlite.get(j).getId())) {
						try {
							// 修改数据库数据
							userDao.update(list.get(i));
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else {
						num++;
					}
				}
				if (num == list_ormlite.size()) {

					try {
						// 插入数据
						userDao.create(list.get(i));
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}
			}
			// 更新完成后 通知listview 数据发生改变
			try {
				list_ormlite = userDao.queryBuilder().where()
						.eq("CorpId", Global.mUser.CorpId).query();
				convertDataToLetterList(list);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 把最近员工集合转为 CheckBoxListViewItem 集合
	 * 
	 * @param list
	 */
	private List<CheckBoxListViewItem> convertDataLatestToCheckBoxListViewItem(
			List<Latest> list) {
		List<CheckBoxListViewItem> items = new ArrayList<CheckBoxListViewItem>();
		for (Latest s : list) {
			CheckBoxListViewItem cbItem1 = new CheckBoxListViewItem(
					s.getPic_url(), s.getId() + "", s.getUserName(), false, 0);
			for (CheckBoxListViewItem cbItem2 : mCheckUsers) {
				if (cbItem2.Id.equals(s.getId() + "")) {
					cbItem1.IsChecked = true;
				}
			}
			items.add(cbItem1);
		}
		return items;
	}

	/**
	 * 把员工集合转为 CheckBoxListViewItem 集合
	 * 
	 * @param list
	 */
	private void convertDataToLetterList(List<User> list) {
		mListViewItems.clear();
		for (User s : list) {
			CheckBoxListViewItem cbItem1 = new CheckBoxListViewItem(
					s.AvatarURI, s.getId() + "", s.getUserName(), false, 0);
			for (CheckBoxListViewItem cbItem2 : mCheckUsers) {
				if (cbItem2.Id.equals(s.getId() + "")) {
					cbItem1.IsChecked = true;
				}
			}
			mListViewItems.add(cbItem1);
		}
		initAdapter();
	}

	// 得到ormdatahelper对象
	public ORMDataHelper getHelper() {
		// if (ormDataHelper == null) {
		// ormDataHelper = OpenHelperManager.getHelper(this,
		// ORMDataHelper.class);
		// }
		ormDataHelper = ORMDataHelper.getInstance(this);
		return ormDataHelper;
	}

	// 及时对数据库进行管理
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// if (ormDataHelper != null) {
		// OpenHelperManager.releaseHelper();
		// ormDataHelper = null;
		// }

		if (overlay != null) {
			WindowManager windowManager = (WindowManager) this
					.getSystemService(Context.WINDOW_SERVICE);
			windowManager.removeView(overlay);
		}

		LogUtils.i("ondestroy", "mListViewItems.size=" + mListViewItems.size());
	}

}