package com.cedarhd;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.adapter.InviationAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyFlowLayout;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.LoadImage;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.GroupModel;
import com.cedarhd.models.NewDepartMent;
import com.cedarhd.models.NewStaff;
import com.cedarhd.models.User;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 邀请下载页面
 */
@SuppressLint("NewApi")
public class InvitationActivity extends BaseActivity {

	public static final int GET_USERLIST_BY_DEPT_SUCCEED = 1;
	public static final int END_DELEDT = 2;
	public static final int END_BITMAP = 3;
	private static final int SUCCEED_GET_ALL_USER = 10;
	public static final int GETDEPARTMENT_SUCCEED = 609;
	private static final int GET_USERLIST_BY_DEPT_ERROR = 1201;

	private Context context;
	private GridView show_info;
	private ImageButton in_back;
	private TextView view_num, departMent_new_name, user_name_invia;
	private PorterShapeImageView user_phito;
	private String result;

	/**
	 * 获取当前人所在的部门信息
	 */
	String resultDepartment;
	public HttpUtils utils = new HttpUtils();

	private InviationAdapter mAdapter;
	private String numpeople;
	private LoadImage loadImage;
	Bitmap bitmap = null;
	private ImageView invi_back_new;

	/***
	 * 部门编号集合
	 */
	List<String> mDepts = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invitation);
		context = InvitationActivity.this;
		loadImage = new LoadImage(this, null);
		view_num = (TextView) findViewById(R.id.user_num);
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}

	private void initData() {
		if (HttpUtils.IsHaveInternet(InvitationActivity.this)) {
			ProgressDialogHelper.show(context, "更新员工信息...");
			getAllUserInfo();

			if (mDepts.size() == 0) {
				downloadCommanDept();
			}
		} else {
			Toast.makeText(InvitationActivity.this, "暂无网络连接请检查网络后重新返回此界面...",
					Toast.LENGTH_LONG).show();
		}
	}

	private void getDepartment() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = Global.BASE_URL + Global.EXTENSION
						+ "account/GetDepartments/" + "编号="
						+ Global.mUser.Department;
				resultDepartment = utils.httpGet(url);
				try {
					if (resultDepartment != null) {
						// 获取json对象
						JSONObject jsonObject = new JSONObject(resultDepartment);
						// 通过键名来获取对应的类
						int status = jsonObject.getInt("Status");
						if (status == 1) {
							handler.sendEmptyMessage(GETDEPARTMENT_SUCCEED);
						} else {
							handler.sendEmptyMessage(GET_USERLIST_BY_DEPT_ERROR);
						}
					} else {
						handler.sendEmptyMessage(GET_USERLIST_BY_DEPT_ERROR);
					}
				} catch (Exception e) {
				}
			}
		}).start();
	}

	/**
	 * 根据部门编号 获取员工列表
	 * 
	 * @param departMent
	 *            部门编号
	 */
	private void getAllUsersByDept(final int departMent) {
		ProgressDialogHelper.show(context, "加载部门信息..");
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = Global.BASE_URL + Global.EXTENSION
						+ "account/GetEmployeeListByDeptId/" + departMent;
				try {
					result = utils.httpGet(url);
					getNum();
					if (result != null) {
						// 获取json对象
						JSONObject jsonObject = new JSONObject(result);
						// 通过键名来获取对应的类
						int status = jsonObject.getInt("Status");
						if (status == 1) {
							handler.sendEmptyMessage(GET_USERLIST_BY_DEPT_SUCCEED);
						} else {
							handler.sendEmptyMessage(GET_USERLIST_BY_DEPT_ERROR);
						}
					} else {
						handler.sendEmptyMessage(GET_USERLIST_BY_DEPT_ERROR);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 获取部门列表
	 */
	private List<NewDepartMent> list_depart;
	public static final int GETDEPARTLIST = 6091;

	private void getDepartmentList(final NewDepartMent departMent) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = Global.BASE_URL + Global.EXTENSION
						+ "account/GetDepartments/" + "上级=" + departMent.编号;
				resultDepartment = utils.httpGet(url);
				try {
					if (resultDepartment != null) {
						// 获取json对象
						JSONObject jsonObject = new JSONObject(resultDepartment);
						// 通过键名来获取对应的类
						int status = jsonObject.getInt("Status");
						if (status == 1) {
							handler.sendEmptyMessage(GETDEPARTLIST);
						} else {
							handler.sendEmptyMessage(GET_USERLIST_BY_DEPT_ERROR);
						}
					} else {
						handler.sendEmptyMessage(GET_USERLIST_BY_DEPT_ERROR);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
	}

	private void getNum() {
		// String str = utils.httpGet(Global.BASE_URL + Global.EXTENSION
		// + "account/GetEmployeeCount");
		// numpeople = getJSON1(str, "Data").substring(1,
		// getJSON1(str, "Data").length() - 1);
		// handler.sendEmptyMessage(11);
	}

	private List<User> list_user;

	// String res;

	/** 获取所有员工的信息 */
	private void getAllUserInfo() {
		try {
			new Thread(new Runnable() {
				String url = Global.BASE_URL
						+ "Department/GetUserListByLastDate?lastDate=0";

				@Override
				public void run() {
					String reuslt = utils.httpGet(url);
					list_user = JsonUtils.ConvertJsonToList(reuslt, User.class);
					handler.sendEmptyMessage(SUCCEED_GET_ALL_USER);
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 下载常用部门 */
	private void downloadCommanDept() {
		try {
			new Thread(new Runnable() {
				String url = Global.BASE_URL
						+ "Department/GetCommonDepartmentName";

				@Override
				public void run() {
					String res = utils.httpGet(url);
					try {
						JSONObject jsonObject = new JSONObject(res);
						JSONArray jsonarray = jsonObject.getJSONArray("Data");
						for (int i = 0; i < jsonarray.length(); i++) {
							mDepts.add(jsonarray.getString(i));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取用户头像
	private void getBitmap(final String url) {
		final String uri = Global.BASE_URL + Global.EXTENSION + url;
		new Thread(new Runnable() {
			@Override
			public void run() {
				bitmap = loadImage.geBitmap(uri);
				handler.sendEmptyMessage(END_BITMAP);
			}
		}).start();
	}

	public Boolean isstaff = false, isdepart = false;
	List<NewStaff> list_staff;
	NewDepartMent department_info;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_USERLIST_BY_DEPT_SUCCEED: // 获取员工列表
				ProgressDialogHelper.dismiss();
				list_staff = JsonUtils
						.ConvertJsonToList(result, NewStaff.class);
				isstaff = true;
				getGroupMode();
				break;
			case END_BITMAP:
				if (bitmap != null) {
					user_phito.setImageBitmap(bitmap);
				} else {
					user_phito.setImageDrawable(getResources().getDrawable(
							R.drawable.tx));
				}
				break;
			case SUCCEED_GET_ALL_USER:
				getDepartment();
				break;
			case 11:
				view_num.setText(numpeople);
				break;
			case GET_USERLIST_BY_DEPT_ERROR:
				Toast.makeText(InvitationActivity.this, "服务器异常请联系本公司",
						Toast.LENGTH_LONG).show();
			case GETDEPARTMENT_SUCCEED:
				ProgressDialogHelper.dismiss();
				department_info = new NewDepartMent();
				try {
					JSONObject jsonObject = new JSONObject(resultDepartment);
					JSONArray jsonarray = jsonObject.getJSONArray("Data");
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject jsonObject1 = (JSONObject) jsonarray.get(i);
						department_info.上级 = jsonObject1.getInt("上级");
						department_info.代码 = jsonObject1.getString("代码");
						department_info.停用 = jsonObject1.getBoolean("停用");
						department_info.分管负责人 = jsonObject1.getString("分管负责人");
						department_info.分管负责人名称 = jsonObject1
								.getString("分管负责人名称");
						department_info.名称 = jsonObject1.getString("名称");
						department_info.地址 = jsonObject1.getString("地址");
						department_info.排序 = jsonObject1.getInt("排序");
						department_info.电话 = jsonObject1.getString("电话");
						department_info.编号 = jsonObject1.getInt("编号");
						department_info.负责人 = jsonObject1.getInt("负责人");
						department_info.负责人名称 = jsonObject1.getString("负责人名称");
						department_info.额度 = jsonObject1.getInt("额度");
						departMent_new_name.setText(department_info.名称);

					}
					/**
					 * 先获取部门信息 在获取部门下属员工部门下属列表
					 */
					getAllUsersByDept(department_info.编号);

					/**
					 * 获取部门下属列表
					 */
					getDepartmentList(department_info);

					for (int i = 0; i < list_user.size(); i++) {
						if (Integer.parseInt(list_user.get(i).Id) == department_info.负责人) {
							user_name_invia.setText(list_user.get(i).UserName);
							getBitmap(list_user.get(i).AvatarURI);
							break;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case GETDEPARTLIST:
				list_depart = JsonUtils.ConvertJsonToList(resultDepartment,
						NewDepartMent.class);
				isdepart = true;
				getGroupMode();
				break;
			case CREATE_DEPARTMENT_SUCCESS:
				ProgressDialogHelper.dismiss();
				Toast.makeText(InvitationActivity.this, "创建成功",
						Toast.LENGTH_LONG).show();
				/**
				 * 先获取部门信息 在获取部门下属员工部门下属列表
				 */
				getAllUsersByDept(department_info.编号);
				/**
				 * 获取部门下属列表
				 */
				getDepartmentList(department_info);
				break;
			case CREATE_DEPARTMENT_ERROR:
				ProgressDialogHelper.dismiss();
				Toast.makeText(InvitationActivity.this, "创建失败" + msg.obj,
						Toast.LENGTH_LONG).show();
				break;
			}
		};
	};

	/**
	 * 当部门与员工全部解析完成的时候就要设置适配器
	 */
	List<GroupModel> groupModels;

	/***
	 * 显示员工到页面
	 */
	private void getGroupMode() {
		groupModels = new ArrayList<GroupModel>();
		if (isdepart == true && isstaff == true) {
			for (int j = 0; j < list_depart.size(); j++) {
				GroupModel groupModel = new GroupModel();
				groupModel.编号 = list_depart.get(j).编号;
				groupModel.名称 = list_depart.get(j).名称;
				groupModel.isdepart = true;
				groupModels.add(groupModel);
			}
			for (int i = 0; i < list_staff.size(); i++) {
				GroupModel groupModel = new GroupModel();
				groupModel.Id = list_staff.get(i).Id;
				groupModel.AvatarURI = list_staff.get(i).AvatarURI;
				groupModel.Admin = list_staff.get(i).Admin;
				groupModel.userName = list_staff.get(i).UserName;
				groupModel.isdepart = false;
				groupModels.add(groupModel);
			}
			groupModels.add(new GroupModel());
		}
		mAdapter = new InviationAdapter(InvitationActivity.this, show_info);
		mAdapter.addBottom(groupModels, true);
		show_info.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}

	private String getJSON1(String data, String TAG) {
		String str = null;
		try {
			// 获取json对象
			JSONObject jsonObject = new JSONObject(data);
			// 通过键名来获取对应的类
			int status = jsonObject.getInt("Status");
			if (status == 1) {
				str = jsonObject.getString(TAG);
			} else {
				Toast.makeText(InvitationActivity.this,
						"数据异常，可能是网络原因，请确定络正常连接后在试！！！", Toast.LENGTH_LONG)
						.show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return str;
	}

	// 记录id
	public int id;
	// 记录编号
	public int postion;

	private void init() {
		in_back = (ImageButton) findViewById(R.id.in_back);
		show_info = (GridView) findViewById(R.id.in_gridView);
		user_phito = (PorterShapeImageView) findViewById(R.id.user_photo);
		departMent_new_name = (TextView) findViewById(R.id.departMent_new_name);
		user_name_invia = (TextView) findViewById(R.id.user_name_invia);
		invi_back_new = (ImageView) findViewById(R.id.invi_back_new);
		setonclicklistener();
	}

	public PopupWindow popupWindow;
	View view;
	/**
	 * 部门 , 员工
	 */
	public Button department, clerk;
	MyFlowLayout mTagLayout;
	Dialog dialog_createdepart;

	private void startactivity() {
		Intent intent = new Intent(InvitationActivity.this,
				TabMainActivity.class);
		startActivity(intent);
	}

	private void setonclicklistener() {
		in_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				startactivity();
			}
		});
		invi_back_new.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		show_info
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						postion = arg2;
						Builder builder = new AlertDialog.Builder(
								InvitationActivity.this);
						String[] str_depart = { "删除部门" };
						// String[] str = { "删除员工", "停用员工" };
						// 删除员工功能隐藏
						String[] str = { "停用员工" };
						if (postion < mAdapter.getData().size() - 1) {
							if (mAdapter.getData().get(postion).isdepart) {
								builder.setItems(str_depart,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												switch (which) {
												case 0:
													id = mAdapter.getData()
															.get(postion).编号;
													final String url = Global.BASE_URL
															+ Global.EXTENSION
															+ "account/DeleteDepartment/"
															+ id;
													if (Boolean
															.parseBoolean(Global.mUser.Admin)
															|| department_info.负责人 == Integer
																	.parseInt(Global.mUser.Id)) {
														Builder builder = new AlertDialog.Builder(
																InvitationActivity.this);
														builder.setTitle("提示");
														builder.setMessage("确定删除该部门吗?");
														builder.setPositiveButton(
																"确定",
																new DialogInterface.OnClickListener() {
																	@Override
																	public void onClick(
																			DialogInterface dialog,
																			int which) {
																		delete(url);
																		mAdapter.getData()
																				.remove(postion);
																		mAdapter.notifyDataSetChanged();
																	}
																});
														builder.setNegativeButton(
																"取消", null);
														builder.show();
													} else {
														Toast.makeText(
																InvitationActivity.this,
																"权限不足无法删除部门",
																Toast.LENGTH_LONG)
																.show();
													}
													break;
												}
											}
										});
							} else {
								builder.setItems(str,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												switch (which) {
												case 1: // 删除员工
													id = mAdapter.getData()
															.get(postion).Id;
													if ((id + "")
															.equals(Global.mUser
																	.getId())) {
														Toast.makeText(
																context,
																"不允许删除自己的信息",
																Toast.LENGTH_SHORT)
																.show();
													} else {
														final String url = Global.BASE_URL
																+ Global.EXTENSION
																+ "account/DeleteStaff/"
																+ id;
														if (Boolean
																.parseBoolean(Global.mUser.Admin)
																|| department_info.负责人 == Integer
																		.parseInt(Global.mUser.Id)) {
															Builder builder = new AlertDialog.Builder(
																	InvitationActivity.this);
															builder.setTitle("提示");
															builder.setMessage("确定删除该员工吗?");
															builder.setPositiveButton(
																	"确定",
																	new DialogInterface.OnClickListener() {
																		@Override
																		public void onClick(
																				DialogInterface dialog,
																				int which) {
																			delete(url);
																			mAdapter.getData()
																					.remove(postion);
																			mAdapter.notifyDataSetChanged();
																		}
																	});
															builder.setNegativeButton(
																	"取消", null);
															builder.show();
														} else {
															Toast.makeText(
																	InvitationActivity.this,
																	"权限不足无法删除员工",
																	Toast.LENGTH_LONG)
																	.show();
														}
													}
													break;
												case 0:
													id = mAdapter.getData()
															.get(postion).Id;
													id = mAdapter.getData()
															.get(postion).Id;
													if ((id + "")
															.equals(Global.mUser
																	.getId())) {
														Toast.makeText(
																context,
																"不允许停用自己的信息",
																Toast.LENGTH_SHORT)
																.show();
													} else {
														final String urlstop = Global.BASE_URL
																+ Global.EXTENSION
																+ "account/ExpireStaff/"
																+ id;
														Builder builder = new AlertDialog.Builder(
																InvitationActivity.this);
														builder.setTitle("提示");
														builder.setMessage("确定停用该员工吗?");
														builder.setPositiveButton(
																"确定",
																new DialogInterface.OnClickListener() {
																	@Override
																	public void onClick(
																			DialogInterface dialog,
																			int which) {
																		delete(urlstop);
																		mAdapter.getData()
																				.remove(postion);
																		mAdapter.notifyDataSetChanged();
																	}
																});
														builder.setNegativeButton(
																"取消", null);
														builder.show();
														break;
													}
												}
											}
										});
							}
						}
						builder.show();
						return true;
					}
				});

		show_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mAdapter.getData().get(position).isdepart) {
					Bundle bundle = new Bundle();
					bundle.putSerializable("depart",
							mAdapter.getData().get(position));
					skip(Invitation_infoActivity.class, bundle);
				} else if (position == mAdapter.getData().size() - 1) {
					Builder builder = new AlertDialog.Builder(
							InvitationActivity.this);
					String[] str = { "新建部门", "邀请员工" };
					builder.setItems(str,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:// 创建部门
										showCreateDialog();
										break;
									case 1:
										Bundle bundle = new Bundle();
										bundle.putSerializable("depart",
												department_info);
										skip(InviteDownloadActivity.class,
												bundle);
										break;
									}
								}
							});
					builder.show();
				}
			}
		});
	}

	/**
	 * 创建部门的线程
	 * 
	 * @param topid
	 * @param departname
	 */
	public static final int CREATE_DEPARTMENT_SUCCESS = 610;
	public static final int CREATE_DEPARTMENT_ERROR = 6101;

	public void createdepart(final int topid, final String departname) {
		ProgressDialogHelper.show(context, "创建部门中...");
		new Thread(new Runnable() {
			public void run() {
				String url = Global.BASE_URL + Global.EXTENSION
						+ "account/CreateDepartment/" + topid + "/"
						+ departname;
				String result = utils.httpGet(url);
				// 获取json对象
				// 通过键名来获取对应的类
				int status;
				try {
					JSONObject jsonObject = new JSONObject(result);
					status = jsonObject.getInt("Status");
					if (status == 1) {
						handler.sendEmptyMessage(CREATE_DEPARTMENT_SUCCESS);
					} else {
						Message message = handler.obtainMessage();
						message.obj = result;
						message.what = CREATE_DEPARTMENT_ERROR;
						handler.sendMessage(message);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 删除部门 删除员工 停用员工
	 * 
	 * @param url
	 */
	public void delete(final String url) {
		try {
			new Thread(new Runnable() {
				public void run() {
					String result = utils.httpGet(url);
					LogUtils.i("resultUrl", url);
					LogUtils.i("result", result);
					getJSON1(result, "Data");
					getNum();
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 初始化常用部门
	 * 
	 * @param editText
	 * @param mTextStr
	 */
	private void initLayout(final EditText editText, final List<String> mTextStr) {
		for (int i = 0; i < mTextStr.size(); i++) {
			final int pos = i;
			final TextView text = (TextView) LayoutInflater.from(this).inflate(
					R.layout.tag_text, mTagLayout, false);
			text.setText(mTextStr.get(i));
			text.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					text.setActivated(!text.isActivated());
					if (text.isActivated()) {
						dialog_createdepart.dismiss();
						Builder builder = new AlertDialog.Builder(
								InvitationActivity.this);
						builder.setTitle("提示");
						builder.setMessage("确定创建" + mTextStr.get(pos) + "部门");
						builder.setNegativeButton("取消", null);
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										createdepart(department_info.编号,
												mTextStr.get(pos));
									}
								});
						builder.show();
					} else {
					}
				}
			});
			mTagLayout.addView(text);
		}
	}

	/***
	 * 显示创建部门对话框
	 */
	private void showCreateDialog() {
		View view = LayoutInflater.from(InvitationActivity.this).inflate(
				R.layout.activity_createdepart, null);
		mTagLayout = (MyFlowLayout) view.findViewById(R.id.tag_layout);
		TextView textView = (TextView) view.findViewById(R.id.show_text_create);
		ImageView ivBack = (ImageView) view
				.findViewById(R.id.iv_back_createdepart);
		TextView tvSave = (TextView) view
				.findViewById(R.id.tv_save_createdepart);
		textView.setText("确定在" + department_info.名称 + "下添加部门?");
		final EditText editText = (EditText) view
				.findViewById(R.id.get_department_name);
		initLayout(editText, mDepts);
		// 全屏对话框
		dialog_createdepart = new Dialog(InvitationActivity.this,
				R.style.Dialog_Fullscreen);
		dialog_createdepart.setContentView(view);
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog_createdepart.dismiss();
			}
		});
		tvSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String depart_name = editText.getText().toString().trim();
				if (TextUtils.isEmpty(depart_name)) {
					Toast.makeText(context, "部门名称不能为空", Toast.LENGTH_SHORT)
							.show();
				} else {
					createdepart(department_info.编号, depart_name);
				}
			}
		});
		dialog_createdepart.show();
	}
}
