package com.cedarhd;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.models.Department;
import com.cedarhd.models.部门;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Deprecated
public class DepartmentActivity1 extends BaseActivity {
	private LinearLayout content;
	private TextView textView2;// 显示所选内容的文本
	private ImageView button;// 确定按钮
	private int select_dptId = -1;
	private long lastUpdateTime = 0;
	private MyHandler handler;
	private Dao dao;
	private ORMDataHelper helper;
	private List<部门> list_orm = new ArrayList<部门>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.departmentactivity1);
		init();
	}

	/**
	 * 使用递归加载布局
	 * 
	 * @param list_first
	 *            第一层的数据集合
	 * @param list_orm
	 *            数据库查询到的数据
	 * @param laout
	 *            最外层的布局
	 */
	private void getChildList(final List<部门> list_first, List<部门> list_orm,
			LinearLayout laout) {
		for (int i = 0; i < list_first.size(); i++) {
			List<部门> list = new ArrayList<部门>();
			LinearLayout layout_group = new LinearLayout(
					DepartmentActivity1.this);
			layout_group.setOrientation(LinearLayout.VERTICAL);
			final LinearLayout layout_child = new LinearLayout(
					DepartmentActivity1.this);
			layout_child.setOrientation(LinearLayout.VERTICAL);
			layout_child.setVisibility(View.GONE);
			final TextView textView = new TextView(DepartmentActivity1.this);
			textView.setText(list_first.get(i).get名称());
			textView.setTextSize(20);
			textView.setTextColor(Color.BLACK);
			textView.setPadding(30, 5, 5, 5);
			final int k = i;
			textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					textView2.setText(textView.getText().toString().trim());
					select_dptId = list_first.get(k).get编号();
					if (layout_child.isShown()) {
						layout_child.setVisibility(View.GONE);
					} else {
						layout_child.setVisibility(View.VISIBLE);
					}
				}
			});
			layout_group.addView(textView);
			// 加入每一层的子布局数据源
			for (int j = 0; j < list_orm.size(); j++) {
				if (list_orm.get(j).get上级() == list_first.get(i).get编号()) {
					list.add(list_orm.get(j));
				}
			}
			LogUtils.e("********", "******" + list);
			layout_group.addView(layout_child);
			laout.addView(layout_group);
			// 递归调用遍历子布局中的元素
			getChildList(list, list_orm, layout_child);
		}
	}

	private long changeTimeToInt(String time) {
		String last = time.replaceAll("-", " ");
		last = last.replace(":", " ");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd hh MM ss");
		try {
			Date date = sdf.parse(last);
			return date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;

	}

	/**
	 * @author wangxiaojian 接受网络数据
	 */
	private class MyHandler extends Handler {
		// @SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				List<Department> list = (List<Department>) msg.obj;
				LogUtils.e("---", "=====>>>>" + list);
				// 本地数据库没有数据 则直接插入服务器返回的数据
				if (list_orm.size() == 0) {
					for (int i = 0; i < list.size(); i++) {
						try {
							Department department = list.get(i);
							部门 bumen = new 部门();
							bumen.set编号(department.get编号());
							bumen.set上级(department.get上级());
							bumen.set停用(department.get停用());
							bumen.set名称(department.get名称());
							bumen.set负责人(department.get负责人());
							bumen.set名称(department.get名称());
							bumen.setUpdateTime(department.get最后更新());
							dao.create(bumen);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					for (int i = 0; i < list.size(); i++) {
						int num = 0;
						for (int j = 0; j < list_orm.size(); j++) {
							// 如果数据库中有该数据 则修改
							if (list.get(i).get编号() == list_orm.get(j).get编号()) {
								// update
								try {
									/**
									 * userDao.updateRaw(
									 * "update user set pic_url=" +
									 * list.get(i).getPic_url() + ",Id=" +
									 * list.get(i).getId() + ",UserName=" +
									 * list.get(i).getUserName() + ",dptId=" +
									 * list.get(i).getDptId() + "where Id=" +
									 * list.get(i).getId());
									 */
									String time = list.get(i).get最后更新() + "";
									time = time.replaceAll("-", " ");
									time = time.replaceAll(":", " ");
									dao.updateRaw("update 部门 set 名称="
											+ list.get(i).get名称()
											+ ",上级="
											+ list.get(i).get上级()
											+ ",负责人="
											+ list.get(i).get负责人()
											+ ",停用="
											+ list.get(i).get停用()
											+ ",最后更新="
											+ changeTimeToInt(list.get(i)
													.get最后更新()) + " where 编号="
											+ list.get(i).get编号());
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							} else {
								num++;
							}
						}
						// 如果数据库中没有该数据 则直接插入
						if (num == list_orm.size()) {
							try {
								Department department = list.get(i);
								部门 bumen = new 部门();
								bumen.set上级(department.get上级());
								bumen.set停用(department.get停用());
								bumen.set名称(department.get名称());
								bumen.set负责人(department.get负责人());
								bumen.set名称(department.get名称());
								bumen.setUpdateTime(department.get最后更新());
								dao.create(bumen);

							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}

				try {
					list_orm = dao.queryForAll();
					List<部门> list_first = new ArrayList<部门>();
					for (int i = 0; i < list_orm.size(); i++) {
						if (list_orm.get(i).get上级() == -1) {
							list_first.add(list_orm.get(i));
							break;
						}
					}
					content.removeAllViews();
					getChildList(list_first, list_orm, content);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 初始化 加入布局
	 */
	@SuppressWarnings("unchecked")
	private void init() {
		content = (LinearLayout) findViewById(R.id.content);
		helper = ORMDataHelper.getInstance(this);
		try {
			dao = helper.getDao(部门.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		handler = new MyHandler();
		// 首先查询数据库
		try {
			list_orm = dao.queryForAll();
			List<部门> list_first = new ArrayList<部门>();
			for (int i = 0; i < list_orm.size(); i++) {
				if (list_orm.get(i).get上级() == -1) {
					list_first.add(list_orm.get(i));
					break;
				}
			}
			LogUtils.e("*****************", "===>>>" + list_first);
			getChildList(list_first, list_orm, content);
			for (int i = 0; i < list_orm.size(); i++) {
				// kjx update
				// if (lastUpdateTime < list_orm.get(i).get最后更新()) {
				// lastUpdateTime = list_orm.get(i).get最后更新();
				// }
			}
			LogUtils.e("--------", "=====>>>" + list_orm);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 在有网络的情况下 访问网络更新数据
		if (HttpUtils.IsHaveInternet(this)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						// 得到json字符串
						HttpUtils httpUtils = new HttpUtils();
						String path = "http://www.tt-sms.net:8076/Department/GetDepartmentListByLastDate?lastDate=";
						String jsonResult = httpUtils.httpGet(path
								+ lastUpdateTime);
						// 得到集合
						List<Department> list = JsonUtils.ConvertJsonToList(
								jsonResult, Department.class);
						Log.d("---->>>", "=============" + list);
						// 发送消息更新数据
						Message message = Message.obtain();
						message.what = 1;
						message.obj = list;
						handler.sendMessage(message);
					} catch (Exception e) {
						Toast.makeText(DepartmentActivity1.this, "更新数据异常",
								Toast.LENGTH_SHORT).show();
					}
				}
			}).start();
		}

		textView2 = (TextView) findViewById(R.id.textView2);

		// 点击确定按钮后返回到按部门选择界面
		button = (ImageView) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String select = textView2.getText().toString().trim();
				if (TextUtils.isEmpty(select)) {
					Toast.makeText(DepartmentActivity1.this, "请选择部门",
							Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent();
					intent.putExtra("select", select);
					intent.putExtra("select_dpt", select_dptId);
					setResult(1001, intent);
					finish();
				}
			}
		});

	}
}
