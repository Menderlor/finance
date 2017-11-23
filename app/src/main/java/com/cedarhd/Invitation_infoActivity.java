package com.cedarhd;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.cedarhd.control.MyBaseActivity;
import com.cedarhd.control.MyFlowLayout;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.LoadImage;
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

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class Invitation_infoActivity extends MyBaseActivity {
	private GridView show_info;
	private ImageView add;
	private ImageButton in_back;
	private TextView view_num, departMent_new_name, user_name_invia;
	private PorterShapeImageView user_phito;
	String result;
	public HttpUtils utils = new HttpUtils();
	public static final int GET_END = 1;
	private InviationAdapter adapter;
	private LayoutInflater inflater;
	private Button button_ok;
	private String numpeople;
	// 图片加载；类
	private LoadImage loadImage;
	Bitmap bitmap = null;
	public static final int END_BITMAP = 3;
	public static final int END_DELEDT = 2;
	ProgressDialog dialog;
	private ImageView invi_back_new;
	/**
	 * 将员工与部门结合起来的实体类包含二者中的部分用到的属性
	 */
	GroupModel groupModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invitation);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		groupModel = (GroupModel) bundle.get("depart");
		loadImage = new LoadImage(this, null);
		inflater = LayoutInflater.from(this);
		button_ok = (Button) findViewById(R.id.buton_ok);
		view_num = (TextView) findViewById(R.id.user_num);
		init();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (HttpUtils.IsHaveInternet(Invitation_infoActivity.this)) {
			// getBitmap(Global.mUser.AvatarURI);
			getAllInfo();
		} else {
			Toast.makeText(Invitation_infoActivity.this,
					"暂无网络连接请检查网络后重新返回此界面...", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 获取当前人所在的部门信息
	 */
	String resultDepartment;
	public static final int GETDEPARTMENT = 609;

	private void getDepartment(final int id) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String url = Global.BASE_URL + Global.EXTENSION
						+ "account/GetDepartments/" + "编号=" + id;
				resultDepartment = utils.httpGet(url);
				try {
					if (resultDepartment != null) {
						// 获取json对象
						JSONObject jsonObject = new JSONObject(resultDepartment);
						// 通过键名来获取对应的类
						int status = jsonObject.getInt("Status");
						if (status == 1) {
							handler.sendEmptyMessage(GETDEPARTMENT);
						} else {
							handler.sendEmptyMessage(ERROR);
						}
					} else {
						handler.sendEmptyMessage(ERROR);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
	}

	private static final int ERROR = 1201;

	/** 获取员工列表 */
	private void getAllEmp(final int departMent) {
		dialog = ProgressDialog.show(this, "提示", "加载中...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				String url = Global.BASE_URL + Global.EXTENSION
						+ "account/GetEmployeeListByDeptId/" + departMent;
				LogUtils.i("out", url);
				LogUtils.i("out", Global.mUser.Id);
				try {
					result = utils.httpGet(url);
					LogUtils.i("out", "员工列表:" + result);
					getNum();
					if (result != null) {
						// 获取json对象
						JSONObject jsonObject = new JSONObject(result);
						// 通过键名来获取对应的类
						int status = jsonObject.getInt("Status");
						if (status == 1) {
							handler.sendEmptyMessage(GET_END);
						} else {
							handler.sendEmptyMessage(ERROR);
						}
					} else {
						handler.sendEmptyMessage(ERROR);
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

	private void getDepartmentList(final int departMent) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String url = Global.BASE_URL + Global.EXTENSION
						+ "account/GetDepartments/" + "上级=" + departMent;
				resultDepartment = utils.httpGet(url);
				LogUtils.i("out", url + resultDepartment);
				try {
					if (resultDepartment != null) {
						// 获取json对象
						JSONObject jsonObject = new JSONObject(resultDepartment);
						// 通过键名来获取对应的类
						int status = jsonObject.getInt("Status");
						if (status == 1) {
							handler.sendEmptyMessage(GETDEPARTLIST);
						} else {
							handler.sendEmptyMessage(ERROR);
						}
					} else {
						handler.sendEmptyMessage(ERROR);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
	}

	private void getNum() {
		String str = utils.httpGet(Global.BASE_URL + Global.EXTENSION
				+ "account/GetEmployeeCount");
		numpeople = getJSON1(str, "Data").substring(1,
				getJSON1(str, "Data").length() - 1);
		handler.sendEmptyMessage(11);
	}

	private List<User> list_user;
	String res;

	/** 获取所有员工的信息 */
	private void getAllInfo() {
		try {
			new Thread(new Runnable() {
				String url = Global.BASE_URL
						+ "Department/GetUserListByLastDate?lastDate=0";

				@Override
				public void run() {
					res = utils.httpGet(url);
					LogUtils.i("out", "所有员工信息" + res);
					handler.sendEmptyMessage(10);
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
			case GET_END:
				dialog.dismiss();
				list_staff = JsonUtils
						.ConvertJsonToList(result, NewStaff.class);
				isstaff = true;
				getGroupMode();
				LogUtils.i("out", list_staff.size() + "liststaff");
				// adapter = new InviationAdapter(InvitationActivity.this,
				// show_info);
				// adapter.addBottom(list, true);
				// show_info.setAdapter(adapter);
				// adapter.notifyDataSetChanged();
				break;
			case END_BITMAP:
				LogUtils.i("out", bitmap + "11123");
				if (bitmap != null) {
					user_phito.setImageBitmap(bitmap);
				} else {
					user_phito.setImageDrawable(getResources().getDrawable(
							R.drawable.camera));
				}
				break;
			case 10:
				list_user = JsonUtils.ConvertJsonToList(res, User.class);
				LogUtils.i("out", "zhixing" + list_user.size());
				getDepartment(groupModel.编号);
				break;
			case 11:
				view_num.setText(numpeople);
				break;
			case ERROR:
				Toast.makeText(Invitation_infoActivity.this, "服务器异常请联系本公司",
						Toast.LENGTH_LONG).show();
			case GETDEPARTMENT:
				// list_user = getAlluser(getJSON(res, "Data"));
				// LogUtils.i("out","zhixing"+list_user.size());
				// NewDepartMent
				// department=JsonUtils.ConvertJsonObject(resultDepartment.trim(),NewDepartMent.class);
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
					getAllEmp(department_info.编号);
					/**
					 * 获取部门下属列表
					 */
					getDepartmentList(department_info.编号);
					for (int i = 0; i < list_user.size(); i++) {
						LogUtils.i("out", "执行getbitmap" + department_info.负责人);
						LogUtils.i("out",
								department_info.负责人 + "+" + list_user.get(i).Id);
						if (Integer.parseInt(list_user.get(i).Id) == department_info.负责人) {
							user_name_invia.setText(list_user.get(i).UserName);
							getBitmap(list_user.get(i).AvatarURI);
							break;
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case GETDEPARTLIST:
				list_depart = JsonUtils.ConvertJsonToList(resultDepartment,
						NewDepartMent.class);
				isdepart = true;
				getGroupMode();
				LogUtils.i("out", (list_depart.size() + "list_depart"));
				break;
			case CREATE_DEPARTMENT_SUCCESS:
				dialog.dismiss();
				Toast.makeText(Invitation_infoActivity.this, "创建成功",
						Toast.LENGTH_LONG).show();
				/**
				 * 先获取部门信息 在获取部门下属员工部门下属列表
				 */
				getAllEmp(department_info.编号);
				/**
				 * 获取部门下属列表
				 */
				getDepartmentList(department_info.编号);
				break;
			case CREATE_DEPARTMENT_ERROR:
				dialog.dismiss();
				Toast.makeText(Invitation_infoActivity.this, "创建失败" + msg.obj,
						Toast.LENGTH_LONG).show();
				break;
			case SETADMIN:
				dialog.dismiss();
				Toast.makeText(Invitation_infoActivity.this, "设置成功",
						Toast.LENGTH_LONG).show();
				getDepartment(groupModel.编号);
				break;
			case SETADMINERROR:
				dialog.dismiss();
				Toast.makeText(Invitation_infoActivity.this, "设置失败" + msg.obj,
						Toast.LENGTH_LONG).show();
				break;
			}
		};
	};

	/**
	 * 当部门与员工全部解析完成的时候就要设置适配器
	 */
	private void getGroupMode() {
		List<GroupModel> groupModels = new ArrayList<GroupModel>();
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
		adapter = new InviationAdapter(Invitation_infoActivity.this, show_info);
		adapter.addBottom(groupModels, true);
		show_info.setAdapter(adapter);
		adapter.notifyDataSetChanged();
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
				Toast.makeText(Invitation_infoActivity.this,
						"数据异常，可能是网络原因，请确定络正常连接后在试！！！", Toast.LENGTH_LONG)
						.show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}

	private JSONArray getJSON(String data, String TAG) {
		JSONArray array = null;
		try {
			// 获取json对象
			JSONObject jsonObject = new JSONObject(data);
			// 通过键名来获取对应的类
			int status = jsonObject.getInt("Status");
			if (status == 1) {
				array = jsonObject.getJSONArray(TAG);
			} else {
				dialog.dismiss();
				Toast.makeText(Invitation_infoActivity.this,
						"数据异常，可能是网络原因，请确定络正常连接后在试！！！", Toast.LENGTH_LONG)
						.show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return array;
	}

	// 记录id
	public int id;
	// 记录编号
	public int postion;

	private void init() {
		in_back = (ImageButton) findViewById(R.id.in_back);
		show_info = (GridView) findViewById(R.id.in_gridView);
		add = (ImageView) findViewById(R.id.in_add);
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
	AlertDialog alertDialog_createdepart;

	private void startactivity() {
		Intent intent = new Intent(Invitation_infoActivity.this,
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
		// add.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// int[] mLocation = new int[2];
		// add.getLocationOnScreen(mLocation);
		// if (popupWindow == null) {
		// view = inflater.inflate(R.layout.popupwindows, null);
		// popupWindow = new PopupWindow(view, 200, 120);
		// popupWindow.setFocusable(true);// 设置点击任意位置收回
		// // popupWindow.setBackgroundDrawable(getResources()
		// // .getDrawable(R.drawable.tag2));
		// popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// department = (Button) view
		// .findViewById(R.id.department_pop);
		// clerk = (Button) view.findViewById(R.id.clerk_pop);
		// // popupWindow.setAnimationStyle(R.style.popwin_anim_style1);
		// }
		// department.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Builder builder = new AlertDialog.Builder(
		// Invitation_infoActivity.this);
		// View view = LayoutInflater.from(
		// Invitation_infoActivity.this).inflate(
		// R.layout.activity_createdepart, null);
		// TextView textView = (TextView) view
		// .findViewById(R.id.show_text_create);
		// textView.setText("确定在" + department_info.名称 + "下添加部门?");
		// final EditText editText = (EditText) view
		// .findViewById(R.id.get_department_name);
		// builder.setView(view);
		// builder.setPositiveButton("确定",
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// // TODO Auto-generated method stub
		// String depart_name = editText.getText()
		// .toString().trim();
		// if (depart_name != "") {
		// createdepart(department_info.编号,
		// depart_name);
		// }
		// }
		// });
		// builder.setNegativeButton("取消", null);
		// builder.show();
		// }
		// });
		// clerk.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Bundle bundle = new Bundle();
		// bundle.putSerializable("depart", department_info);
		// skip(InviteDownloadActivity.class, bundle);
		// }
		// });
		// // popupWindow.showAsDropDown(arg0);
		// popupWindow.showAtLocation(add, Gravity.NO_GRAVITY,
		// mLocation[0]- popupWindow.getWidth()/2+add.getWidth()/2, mLocation[1]
		// - popupWindow.getHeight());
		// // popupWindow.showAtLocation(add, Gravity.NO_GRAVITY,
		// // mLocation[0]+add.getWidth(), mLocation[1]);
		// // popupWindow.showAtLocation(add, Gravity.NO_GRAVITY,
		// // mLocation[0]-popupWindow.getWidth()-10,
		// // mLocation[1]-((popupWindow.getHeight() - add.getHeight()) /
		// // 2));// 距离屏幕下方多少显示
		// }
		// });

		show_info
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						postion = arg2;
						Builder builder = new AlertDialog.Builder(
								Invitation_infoActivity.this);
						String[] str_depart = { "删除部门" };
						String[] str = { "删除员工", "停用员工", "设为部门负责人" };
						// adapter = (InviationAdapter)
						// ((MyBaseAdapter<GroupModel>) show_info
						// .getAdapter());
						if (postion < adapter.getData().size() - 1) {
							if (adapter.getData().get(postion).isdepart) {
								builder.setItems(str_depart,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												switch (which) {
												case 0:
													id = adapter.getData().get(
															postion).编号;
													final String url = Global.BASE_URL
															+ Global.EXTENSION
															+ "account/DeleteDepartment/"
															+ id;
													if (Boolean
															.parseBoolean(Global.mUser.Admin)
															|| department_info.负责人 == Integer
																	.parseInt(Global.mUser.Id)) {
														Builder builder = new AlertDialog.Builder(
																Invitation_infoActivity.this);
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
																		adapter.getData()
																				.remove(postion);
																		adapter.notifyDataSetChanged();
																	}
																});
														builder.setNegativeButton(
																"取消", null);
														builder.show();
													} else {
														Toast.makeText(
																Invitation_infoActivity.this,
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
												case 0:
													id = adapter.getData().get(
															postion).Id;
													final String url = Global.BASE_URL
															+ Global.EXTENSION
															+ "account/DeleteStaff/"
															+ id;
													if (Boolean
															.parseBoolean(Global.mUser.Admin)
															|| department_info.负责人 == Integer
																	.parseInt(Global.mUser.Id)) {
														Builder builder = new AlertDialog.Builder(
																Invitation_infoActivity.this);
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
																		adapter.getData()
																				.remove(postion);
																		adapter.notifyDataSetChanged();
																	}
																});
														builder.setNegativeButton(
																"取消", null);
														builder.show();
													} else {
														Toast.makeText(
																Invitation_infoActivity.this,
																"权限不足无法删除员工",
																Toast.LENGTH_LONG)
																.show();
													}
													break;
												case 1:
													id = adapter.getData().get(
															postion).Id;
													final String urlstop = Global.BASE_URL
															+ Global.EXTENSION
															+ "account/ExpireStaff/"
															+ id;
													Builder builder = new AlertDialog.Builder(
															Invitation_infoActivity.this);
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
																	adapter.getData()
																			.remove(postion);
																	adapter.notifyDataSetChanged();
																}
															});
													builder.setNegativeButton(
															"取消", null);
													builder.show();
													break;
												case 2:
													id = adapter.getData().get(
															postion).Id;
													final String urladmin = Global.BASE_URL
															+ Global.EXTENSION
															+ "account/SetDepartmentLead/"
															+ department_info.编号
															+ "/" + id;
													Builder builder1 = new AlertDialog.Builder(
															Invitation_infoActivity.this);
													builder1.setTitle("提示");
													builder1.setMessage("确定设置为部门负责人吗?");
													builder1.setPositiveButton(
															"确定",
															new DialogInterface.OnClickListener() {
																@Override
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	SetAdmin(urladmin);
																}
															});
													builder1.setNegativeButton(
															"取消", null);
													builder1.show();
													break;
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
				if (adapter.getData().get(position).isdepart) {
					Bundle bundle = new Bundle();
					bundle.putSerializable("depart",
							adapter.getData().get(position));
					skip(Invitation_infoActivity.class, bundle);
				} else if (position == adapter.getData().size() - 1) {
					Builder builder = new AlertDialog.Builder(
							Invitation_infoActivity.this);
					String[] str = { "新建部门", "邀请员工" };
					builder.setItems(str,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										Builder builder = new AlertDialog.Builder(
												Invitation_infoActivity.this);
										View view = LayoutInflater
												.from(Invitation_infoActivity.this)
												.inflate(
														R.layout.activity_createdepart,
														null);
										mTagLayout = (MyFlowLayout) view
												.findViewById(R.id.tag_layout);
										TextView textView = (TextView) view
												.findViewById(R.id.show_text_create);
										textView.setText("确定在"
												+ department_info.名称 + "下添加部门?");
										final EditText editText = (EditText) view
												.findViewById(R.id.get_department_name);
										initLayout(editText, mTextStr);
										builder.setView(view);
										builder.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {

													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														// TODO Auto-generated
														// method stub
														String depart_name = editText
																.getText()
																.toString()
																.trim();
														if (depart_name != "") {
															createdepart(
																	department_info.编号,
																	depart_name);
														}
													}
												});
										builder.setNegativeButton("取消", null);
										alertDialog_createdepart = builder
												.create();
										alertDialog_createdepart.show();
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
		dialog = ProgressDialog.show(this, "提示", "创建部门中...");
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
	 * 设置为部门负责人
	 */
	public static final int SETADMIN = 6102;
	public static final int SETADMINERROR = 6103;

	public void SetAdmin(final String url) {
		dialog = ProgressDialog.show(Invitation_infoActivity.this, "提示",
				"正在设置请稍后...");
		try {
			new Thread(new Runnable() {
				public void run() {
					String result = utils.httpGet(url);
					// 获取json对象
					// 通过键名来获取对应的类
					int status;
					try {
						JSONObject jsonObject = new JSONObject(result);
						status = jsonObject.getInt("Status");
						if (status == 1) {
							handler.sendEmptyMessage(SETADMIN);
						} else {
							Message message = handler.obtainMessage();
							message.obj = result;
							message.what = SETADMINERROR;
							handler.sendMessage(message);
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
			// TODO Auto-generated catch
			// block
			e.printStackTrace();
		}
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
					LogUtils.i("out", "DELETE返回数据：" + result);
					getJSON1(result, "Data");
					LogUtils.i("out", "delete" + url);
				}
			}).start();
		} catch (Exception e) {
			// TODO Auto-generated catch
			// block
			e.printStackTrace();
		}
	}

	String[] mTextStr = { "销售部", "产品部", "技术部", "测试", "组织部", "宣传部", "外交部",
			"研发部", "网络部", "维修部" };

	private void initLayout(final EditText editText, final String[] mTextStr) {
		for (int i = 0; i < mTextStr.length; i++) {
			final int pos = i;
			final TextView text = (TextView) LayoutInflater.from(this).inflate(
					R.layout.tag_text, mTagLayout, false);
			text.setText(mTextStr[i]);
			text.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					text.setActivated(!text.isActivated());
					if (text.isActivated()) {
						alertDialog_createdepart.dismiss();
						Builder builder = new AlertDialog.Builder(
								Invitation_infoActivity.this);
						builder.setTitle("提示");
						builder.setMessage("确定创建" + mTextStr[pos] + "部门");
						builder.setNegativeButton("取消", null);
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										createdepart(department_info.编号,
												mTextStr[pos]);
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
}
