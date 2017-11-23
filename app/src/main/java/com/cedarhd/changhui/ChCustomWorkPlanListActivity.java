package com.cedarhd.changhui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanCrmAdapter;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.control.listview.BoeryunNoScrollListView;
import com.cedarhd.control.listview.ListViewLoader;
import com.cedarhd.control.listview.PullToRefreshAndLoadMoreListView;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.DateAndTimePicker.ISelected;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictIosPickerBottomDialog;
import com.cedarhd.helpers.DictIosPickerBottomDialog.OnSelectedListener;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.DictionaryQueryDialogHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.helpers.SpeechDialogHelper.OnCompleteListener;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Client;
import com.cedarhd.models.Dict;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.User;
import com.cedarhd.models.crm.QmCustomerWorkPlan;
import com.cedarhd.models.字典;
import com.cedarhd.models.客户工作计划;
import com.cedarhd.models.客户日工作计划;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * 长汇项目工作计划(新版页面风格)
 * 
 * @author k 2015-12-21
 */
public class ChCustomWorkPlanListActivity extends BaseActivity {

	private Context mContext;
	private QmCustomerWorkPlan mQmDemand;
	private QueryDemand mQueryDemand;
	private String mUserSelectId;
	private DictionaryHelper mDictionaryHelper;
	private DictIosPickerBottomDialog mDictIosPicker;
	private CommanCrmAdapter<客户日工作计划> mAdapter;
	private ListViewLoader<客户日工作计划> mListViewLoader;
	private ZLServiceHelper mZlServiceHelper;
	private DictionaryQueryDialogHelper mDictQueryDialogHelper;
	private DisplayImageOptions mOptions;
	private List<客户日工作计划> mList;

	private 客户工作计划 mWorkPlan;

	private String mSelectClientName;// 选中客户名称

	private BoeryunHeaderView headerView;
	private PullToRefreshAndLoadMoreListView lv;
	private RelativeLayout rlSelectUser;
	private RelativeLayout rlSelectDate;
	private TextView tvSelectUser;
	private TextView tvSelectDate;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ch_custom_work_plan_list);
		initViews();
		initData();
		setOnEvent();
	};

	private void initData() {
		mContext = this;
		mDictionaryHelper = new DictionaryHelper(mContext);
		mDictIosPicker = new DictIosPickerBottomDialog(mContext);
		mZlServiceHelper = new ZLServiceHelper();
		mDictQueryDialogHelper = DictionaryQueryDialogHelper
				.getInstance(mContext);
		mOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
				.showImageForEmptyUri(R.drawable.tx) //
				.showImageOnFail(R.drawable.tx) //
				.cacheOnDisk(true)//
				.bitmapConfig(Config.RGB_565)//
				.build();
		mUserSelectId = Global.mUser.getId();
		mQmDemand = new QmCustomerWorkPlan();
		mQmDemand.Offset = 0;
		mQmDemand.PageSize = 100;
		mQmDemand.Time = DateDeserializer.getTodayDate();
		mQueryDemand = new QueryDemand("员工");
		tvSelectDate.setText(mQmDemand.Time + "");
		mList = new ArrayList<客户日工作计划>();
		mAdapter = getWorkPlanAdapter();
		mListViewLoader = new ListViewLoader<客户日工作计划>(mContext,
				"CustomerWorkPlan/GetWorkPlansByDate", lv, mAdapter, mQmDemand,
				mQueryDemand, 客户日工作计划.class);
		// mListViewLoader.setIsRefresh(false);
	}

	private void initViews() {
		headerView = (BoeryunHeaderView) findViewById(R.id.header_custom_work_plan_list);
		lv = (PullToRefreshAndLoadMoreListView) findViewById(R.id.lv_ch_work_plan);
		rlSelectUser = (RelativeLayout) findViewById(R.id.rl_select_user_workplan);
		rlSelectDate = (RelativeLayout) findViewById(R.id.rl_select_date_workplan);
		tvSelectDate = (TextView) findViewById(R.id.tv_select_date_workplan);
		tvSelectUser = (TextView) findViewById(R.id.tv_select_user_workplan);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case UserBiz.SELECT_SINAL_USER_REQUEST_CODE:
			User selectUser = UserBiz.onActivityUserSelected(requestCode,
					resultCode, data);
			if (selectUser != null) {
				tvSelectUser.setText(selectUser.getUserName());
				mQmDemand.UserIds = selectUser.Id;
				reLoadData();
			}
			break;
		case ClientBiz.SELECT_CLIENT_CODE:
			Client client = ClientBiz.onActivityGetClient(mContext,
					requestCode, data);
			if (client != null && client.getId() != 0) {
				mWorkPlan.客户 = client.getId();
				mSelectClientName = client.getCustomerName() + "";
				mTvClient.setText(mSelectClientName);
			}
			break;
		default:
			break;
		}

	}

	private void setOnEvent() {
		headerView.setOnButtonClickListener(new OnButtonClickListener() {
			@Override
			public void onClickSaveOrAdd() {
				// Intent intent = new Intent(mContext,
				// ChWorkPlanInfoActivity.class);
				// if (mVmBase != null && mVmBase.Dict != null) {
				// Bundle bundle = new Bundle();
				// bundle.putSerializable(ChWorkPlanInfoActivity.TAG_DICTS,
				// mVmBase.Dict);
				// intent.putExtras(bundle);
				// }
				// startActivity(intent);

				showAddDialog();
			}

			@Override
			public void onClickFilter() {

			}

			@Override
			public void onClickBack() {
				finish();
			}
		});

		rlSelectUser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UserBiz.selectSinalUser(mContext);
			}
		});

		rlSelectDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DateAndTimePicker dateAndTimePicker = new DateAndTimePicker(
						mContext);
				dateAndTimePicker.showDateWheel(tvSelectDate, false);
				dateAndTimePicker.setOnSelectedListener(new ISelected() {
					@Override
					public void onSelected(String date) {
						mQmDemand.Time = date;
						reLoadData();
					}
				});
			}
		});
		//
		// lv.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// showShortToast("setOnItemClickListener");
		// }
		// });
	}

	private void reLoadData() {
		mQmDemand.Offset = 0;
		mListViewLoader.clearData();
		mListViewLoader.startRefresh();
	}

	private CommanCrmAdapter<客户日工作计划> getWorkPlanAdapter() {
		return new CommanCrmAdapter<客户日工作计划>(mList, mContext,
				R.layout.item_custom_work_plan) {
			@Override
			public void convert(int position, final 客户日工作计划 item,
					BoeryunViewHolder viewHolder) {
				viewHolder.setTextValue(R.id.tv_user_name_work_plan,
						this.getDictName("员工", item.员工));
				viewHolder.setTextValue(R.id.tv_date_name_work_plan,
						StrUtils.pareseNull(item.日期));
				viewHolder.setTextValue(R.id.et_summary_custom_work_plan,
						StrUtils.pareseNull(item.总结));
				final EditText etSummary = viewHolder
						.getView(R.id.et_summary_custom_work_plan);

				User user = mDictionaryHelper.getUser(item.员工 + "");
				ImageView ivUser = viewHolder
						.getView(R.id.iv_avartar_work_plan);
				ImageLoader.getInstance().displayImage(
						Global.BASE_URL + user.AvatarURI, ivUser, mOptions,
						Global.mUser.Passport);
				BoeryunNoScrollListView lv = viewHolder
						.getView(R.id.lv_custom_work_plan);
				lv.setAdapter(getWorkPlanItemAdapter(item));

				if (isMyself(item.员工)
						&& item.日期.contains(ViewHelper.getDateToday().trim())) {
					// 如果是自己当天的工作，则可以输入总结
					etSummary.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							SpeechDialogHelper speechDialogHelper = new SpeechDialogHelper(
									mContext, etSummary, false);
							speechDialogHelper
									.setOnCompleteListener(new OnCompleteListener() {
										@Override
										public void onComplete(
												final String result) {
											etSummary.setText("" + result);
											item.总结 = result;
											mAdapter.notifyDataSetChanged();
											new Thread(new Runnable() {
												@Override
												public void run() {
													mZlServiceHelper.UpdateLog(
															result, "");
												}
											}).start();
										}
									});
						}
					});
				} else {
					etSummary.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});
				}

			}
		};
	}

	private CommanCrmAdapter<客户工作计划> getWorkPlanItemAdapter(客户日工作计划 work) {
		final List<客户工作计划> list = work.工作计划列表;
		return new CommanCrmAdapter<客户工作计划>(list, mContext,
				R.layout.item_custom_work_plan_item) {
			@Override
			public void convert(int position, final 客户工作计划 item,
					BoeryunViewHolder viewHolder) {
				viewHolder.setTextValue(R.id.tv_client_name_work_plan_item,
						mAdapter.getDictName("客户", item.客户) + "");
				viewHolder.setTextValue(R.id.tv_status_work_plan_item,
						mAdapter.getDictName("客户工作计划_工作类型", item.工作类型) + "");
				viewHolder
						.setTextValue(R.id.tv_address_work_plan_item, item.地点);
				viewHolder.setTextValue(R.id.tv_content_work_plan_item,
						StrUtils.pareseNull(item.内容));
				viewHolder.setTextValue(R.id.tv_zhuizong_work_plan_item,
						StrUtils.pareseNull(item.追踪内容));
				viewHolder.setTextValue(R.id.tv_wenti_work_plan_item,
						StrUtils.pareseNull(item.困难问题));
				LinearLayout llWenti = viewHolder
						.getView(R.id.ll_wenti_work_plan_item);
				LinearLayout llZhuizong = viewHolder
						.getView(R.id.ll_zhuizong_work_plan_item);
				ImageView ivWenti = viewHolder
						.getView(R.id.iv_wenti_work_plan_item);
				ImageView ivZhuizong = viewHolder
						.getView(R.id.iv_zhuizong_work_plan_item);

				if (TextUtils.isEmpty(item.困难问题) && isMyself(item.业务员)) {
					llWenti.setVisibility(View.GONE);
					ivWenti.setVisibility(View.VISIBLE);
					ivWenti.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							showShortToast("困难问题");
							SpeechDialogHelper speechDialogHelper = new SpeechDialogHelper(
									mContext, false);
							speechDialogHelper
									.setOnCompleteListener(new OnCompleteListener() {
										@Override
										public void onComplete(String result) {
											item.困难问题 = result;
											mAdapter.notifyDataSetChanged();
											mWorkPlan = item;
											saveWorkPlan();
										}
									});
						}
					});
				} else {
					llWenti.setVisibility(View.VISIBLE);
					ivWenti.setVisibility(View.INVISIBLE);
				}

				if (isMyself(item.业务员)) {
					ivZhuizong.setVisibility(View.INVISIBLE);
					if (TextUtils.isEmpty(item.追踪内容)) {
						llZhuizong.setVisibility(View.GONE);
					} else {
						llZhuizong.setVisibility(View.VISIBLE);
					}
				} else {
					if (TextUtils.isEmpty(item.追踪内容)) {
						ivZhuizong.setVisibility(View.VISIBLE);
						llZhuizong.setVisibility(View.GONE);

						ivZhuizong.setVisibility(View.VISIBLE);
						ivZhuizong.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// showShortToast("追踪内容");
								showZhuizongDialog(item);
							}
						});
					} else {
						ivZhuizong.setVisibility(View.INVISIBLE);
						llZhuizong.setVisibility(View.VISIBLE);
					}
				}
			}
		};
	}

	/** 显示追踪对话框 */
	private void showZhuizongDialog(final 客户工作计划 item) {
		mWorkPlan = item;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_zhuizong_work_plan, null);
		Dialog dialog = new Dialog(mContext, R.style.style_dialog);
		initZhuizongDialogViews(dialog, view);
		dialog.setContentView(view);
		dialog.show();
	}

	private void initZhuizongDialogViews(final Dialog diaglog, View view) {
		final TextView tvType = (TextView) view
				.findViewById(R.id.tv_zhuizong_type_work_plan_info_dialog);
		final EditText etContent = (EditText) view
				.findViewById(R.id.et_zhuizong_content_work_plan_info_dialog);

		etContent.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_UP) {
					boolean isSave = true;
					if (isSave && mWorkPlan.追踪方式 == 0) {
						showShortToast("请选择追踪方式");
						isSave = false;
					}

					String content = etContent.getText().toString();
					if (isSave && TextUtils.isEmpty(content)) {
						showShortToast("请输入追踪内容");
						isSave = false;
					} else {
						mWorkPlan.追踪内容 = content;
					}

					if (isSave) {
						LogUtils.i("etPlanContent", "etContent_saveWorkPlan");
						diaglog.dismiss();
						saveWorkPlan();
					}
					return true;
				}
				return false;
			}
		});

		tvType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final List<Dict> dicts = mAdapter.getmDictionarys().get(
						"客户工作计划_追踪方式");
				mDictIosPicker.show(dicts, "名称");
				mDictIosPicker.setOnSelectedListener(new OnSelectedListener() {
					@Override
					public void onSelected(int index) {
						Dict dict = dicts.get(index);
						tvType.setText(mAdapter.getDictName("客户工作计划_追踪方式",
								dict.编号));
						mWorkPlan.追踪方式 = dict.编号;
					}
				});
			}
		});
	}

	private void showAddDialog() {
		mWorkPlan = new 客户工作计划();
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_add_work_plan, null);
		Dialog dialog = new Dialog(mContext, R.style.style_dialog_normal);
		initAddDialogViews(dialog, view);
		dialog.setContentView(view);
		dialog.show();
	}

	private TextView mTvClient;

	private void initAddDialogViews(final Dialog diaglog, View view) {
		final TextView tvPlanType = (TextView) view
				.findViewById(R.id.tv_plan_type_work_plan_dialog);
		final EditText etAddress = (EditText) view
				.findViewById(R.id.et_adress_work_plan_dialog);
		final EditText etPlanContent = (EditText) view
				.findViewById(R.id.et_content_work_plan_dialog);

		final TextView tvSave = (TextView) view
				.findViewById(R.id.tv_save_work_plan_dialog);

		mTvClient = (TextView) view
				.findViewById(R.id.tv_client_work_plan_dialog);

		etPlanContent.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_UP) {
					boolean isSave = isCheck(etAddress, etPlanContent);

					if (isSave) {
						isSave = false;
						diaglog.dismiss();
						saveWorkPlan();
						LogUtils.i("etPlanContent", "saveWorkPlan:" + v.getId());
					}
					return true;
				}
				return false;
			}
		});

		mTvClient.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClientBiz.selectClient_Changhui(mContext);
				// ClientBiz.selectClient(mContext);
			}
		});

		tvSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isSave = isCheck(etAddress, etPlanContent);
				if (isSave) {
					saveWorkPlan();
					diaglog.dismiss();
				}
			}
		});

		tvPlanType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final List<Dict> dicts = mAdapter.getmDictionarys().get(
						"客户工作计划_工作类型");
				if (dicts == null) {
					mDictQueryDialogHelper.show("客户工作计划_工作类型");
					mDictQueryDialogHelper
							.setOnSelectedListener(new DictionaryQueryDialogHelper.OnSelectedListener() {
								@Override
								public void onSelected(字典 dict) {
									tvPlanType.setText(dict.getName());
									mWorkPlan.工作类型 = dict.Id;
								}
							});
				} else {
					mDictIosPicker.show(dicts, "名称");
					mDictIosPicker
							.setOnSelectedListener(new OnSelectedListener() {
								@Override
								public void onSelected(int index) {
									Dict dict = dicts.get(index);
									tvPlanType.setText(mAdapter.getDictName(
											"客户工作计划_工作类型", dict.编号));
									mWorkPlan.工作类型 = dict.编号;
								}
							});
				}
			}
		});
	}

	private void saveWorkPlan() {
		LogUtils.i("etPlanContent", "saveWorkPlan()");
		ProgressDialogHelper.show(mContext);
		String url = Global.BASE_URL + "CustomerWorkPlan/UpdateWorkPlan";
		try {
			mWorkPlan.业务员 = Integer.parseInt(Global.mUser.Id);
		} catch (Exception e) {
			LogUtils.e(TAG, e + "");
		}
		StringRequest.postAsyn(url, mWorkPlan, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
				showShortToast("保存失败");
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				showShortToast("保存成功");

				if (mWorkPlan.编号 == 0) {
					// 遍历工作计划列表，添加进入当天当前员工下的计划列表
					List<客户日工作计划> list = mAdapter.getDataList();

					// HashMap<String, List<Dict>> hashMap=new HashMap<String,
					// List<Dict>>();
					//
					// mAdapter.addDict(dictMap);

					for (客户日工作计划 item : list) {
						if (isMyself(item.员工)) {
							if (item.工作计划列表 != null) {
								item.工作计划列表.add(mWorkPlan);
								mAdapter.notifyDataSetChanged();
								return;
							}
						}
					}

					// 不存在当前员工当天的计划列表，则构造一个新的
					List<客户工作计划> workplans = new ArrayList<客户工作计划>();
					workplans.add(mWorkPlan);
					客户日工作计划 todayWorkPlan = new 客户日工作计划();
					todayWorkPlan.员工 = Integer.parseInt(Global.mUser.getId());
					todayWorkPlan.日期 = ViewHelper.getDateToday();
					todayWorkPlan.工作计划列表 = workplans;
					mAdapter.getDataList().add(todayWorkPlan);

					HashMap<String, List<Dict>> hashMap = new HashMap<String, List<Dict>>();
					Dict clientDict = new Dict(mWorkPlan.客户, mSelectClientName);
					List<Dict> clientDicts = new ArrayList<Dict>();
					clientDicts.add(clientDict);

					Dict userDict = new Dict(todayWorkPlan.员工, Global.mUser
							.getUserName());
					List<Dict> userDicts = new ArrayList<Dict>();
					userDicts.add(userDict);
					hashMap.put("客户", clientDicts);
					hashMap.put("员工", userDicts);
					mAdapter.addDict(hashMap);
					mAdapter.notifyDataSetChanged();
				} else {
					mAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
				showShortToast("服务器访问异常");
			}
		});
	}

	/**
	 * 判断指定员工是否是自己
	 * 
	 * @param item
	 * @return
	 */
	private boolean isMyself(int userId) {
		return (userId + "").equals(Global.mUser.getId());
	}

	private boolean isCheck(final EditText etAddress,
			final EditText etPlanContent) {
		boolean isSave = true;
		if (isSave && mWorkPlan.工作类型 == 0) {
			showShortToast("请选择工作类型");
			isSave = false;
		}

		if (mWorkPlan.工作类型 != 2 && isSave && mWorkPlan.客户 == 0) {
			// mWorkPlan.工作类型!=2 表示意向客户不需要选择客户
			showShortToast("请选择客户");
			isSave = false;
		}

		String content = etPlanContent.getText().toString();
		if (isSave && TextUtils.isEmpty(content)) {
			showShortToast("请输入计划内容");
			isSave = false;
		} else {
			mWorkPlan.内容 = content;
		}

		String address = etAddress.getText().toString();
		mWorkPlan.地点 = address;
		return isSave;
	}
}
