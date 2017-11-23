package com.cedarhd;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.control.MultipleAttachView;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.helpers.DictIosPickerBottomDialog;
import com.cedarhd.helpers.DictIosPickerBottomDialog.OnSelectedListener;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ParseVmFormCreateUI;
import com.cedarhd.helpers.PictureUtils;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.ShakeListenerUtils;
import com.cedarhd.helpers.ShakeListenerUtils.OnShakeListener;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.imp.IOnUploadMultipleFileListener;
import com.cedarhd.models.Demand;
import com.cedarhd.models.Dict;
import com.cedarhd.models.FieldInfo;
import com.cedarhd.models.FormInfo;
import com.cedarhd.models.User;
import com.cedarhd.models.VmFormDef;
import com.cedarhd.models.changhui.长汇客户;
import com.cedarhd.models.产品型号;
import com.cedarhd.models.动态;
import com.cedarhd.models.字段信息;
import com.cedarhd.models.流程;
import com.cedarhd.models.流程过程表;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MessageUtil;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 表单生成和查看页面(解析Json方式)
 *
 *
 * 保存表单：1、返回表单数据编号 dataId，即具体某张表的id，如 请假单127中的编号
 *
 * 提交表单 ：需要dataId和流程表单名称
 *
 * @author kjx 2015/03/10 10:40
 */
@SuppressLint("NewApi")
public class CreateVmFormActivity extends BaseActivity {
	private final String TAG = "CreateVmFormActivity";

	/** 页面间传递 HashMap 保存表单的默认值 */
	public final static String PROPERTY_MAPS = "property_maps";

	/** 页面间传递 HashMap 保存表单的默认值 */
	public final static String PROPERTY_CLIENT = "property_client";

	public static final int RESULT_CODE_NEW_FORM = -1;
	public static final int LOAD_SERVER_DATA_SUCCEEDED = 0;// 下载服务器数据成功
	public static final int LOAD_SERVER_DATA_FAILED = 1;
	public static final int UPLOAD_DATA_SUCCEEDED = 2;// 上传数据成功
	public static final int UPLOAD_DATA_FAILED = 3;
	public static final int UPLOAD_DATA_START = 4;// 开始上传提交数据
	// public static final int UPLOAD_LEAVEDATA_SUCCEEDED=5;//上传提交数据成功
	public static final int UPLOAD_DATA_END = 6;// 上传提交数据结束
	public static final int LOAD_SERVER_DATA_NOTFOUND = 7;// 表单未找到
	public static final int APPROVAL_OPINION_CODE = 8;// 审批意见返回码
	public static final int APPROVAL_RESULT_SUCCEED = 11; // 审核通过,返回给上级页面

	public static final int UPLOAD_APPROVAL_SUCCEEDED = 12;// 申请成功
	public static final int UPLOAD_APPROVAL_FAILED = 13;// 申请失败

	public final int REQUEST_CODE_ASKFOR_ME = 14;// 待我审批
	public final int GET_DYNAMIC_SUCCESS = 17; // 点击动态，获取流程表单成功
	public final int GET_DYNAMIC_FAILED = 18; // 点击动态，获取流程表单失败
	public static final int SELECT_CLIENT_CODE = 15;
	public static final int SELECT_USER_CODE = 16;

	/** 保存表单明细 请求码 */
	public final int REQUEST_CODE_DETAILS = 21;

	/** 选择产品 */
	public static final int SELECT_PRODUCT_CODE = 26;

	/*** 添加产品明细 */
	public static final int SUCCEED_ADD_PRODUCT_DETAIL = 27;

	/*** 添加产品明细失败 */
	public static final int FAILURE_ADD_PRODUCT_DETAIL = 28;
	/** 请求扫描 */
	private final int CODE_REQUEST_SCAN = 41;

	/*** 选择产品 */
	public static final String PRODUCT_SELECTED = "select_project_list";

	/** 流程表单实体，如果有数据的话，还包含相关的值[用于生成UI控件] */
	private VmFormDef mVmFormDef;

	/** 其他页面带过来的默认显示的键值 */
	private HashMap<String, Object> mDefalutValueMap;

	/** 其他页面带过来的关联显示的客户信息 */
	private 长汇客户 mClientInfo;

	/** 页面动态生成文本框控件，Tag中包含一个FieldInfo */
	private List<EditText> mEditList;

	private Context mContext;
	ZLServiceHelper mZLServiceHelper = new ZLServiceHelper();
	private DictIosPickerBottomDialog mDictIosPicker;
	DictionaryHelper dictionaryHelper;
	PictureUtils pictureUtils;
	ImageView imageViewCancel;
	ImageView imageViewSave;
	LinearLayout create_root;
	/** 上传图片控件 */
	LinearLayout ll_upload_photo;

	/** 填写审核意见布局 */
	LinearLayout ll_approval;
	// HorizontalScrollViewAddImage addImage_attachFile;

	MultipleAttachView multipleAttachView;
	Button buttonTag_createfrom_commit;
	Button buttonTag_createfrom_approval;
	Button buttonTag_createfrom_votedown;
	EditText editText_approval_opinion;
	TextView textViewTitle;
	ProgressBar progressBar_showupload;
	MyProgressBar pbar_web;
	private WebView webview_root;
	private ProgressBar pBar;

	String avatarName = "";// 返回图片的ID之后，获得到的服务器上的图片名字
	String avatarPath = "";// 返回图片的ID之后，获得到的服务器上的图片名字之后，拼接起来的本地缓存地址
	String result = "";// 上传图片的图片ID
	String mPictureFile = "";// 拍照之后图片的名字
	public String URL;

	/** 服务器的表单编号,每个item的表单编号 */

	/** 流程分类编号 */
	int typeId;

	/** 表单数据编号,0表示新建(如：请假单127表中的编号) */
	String mDataId = "0";

	/** 流程表编号，服务器返回的编号，用于提交表单 */
	int mFlowId = 0;

	/** 流程表名 */
	String typeName;//

	String formName;

	/** 顶部标题 内容，默认 */
	String titleName;

	/** 只能接受数字的字段 列名 */
	String mustBeIntFieldName = "";//

	/** 内容为空的字段名 */
	String nullContentFieldName = "";

	/** 判断是否保存，true代表已保存，false代表未保存 */
	boolean isFormSaved = false;

	boolean isInit = false;// 判断是否初始化数据了
	private 流程 mFlow;
	private String opinion; // 审核意见

	/** 是否是审核 */
	boolean isAudit = false;

	/** 保存后是否提交(周工作总结不提交),默認提交 */
	boolean isNotSubmit = false;

	/** 标记动态生成EditText控件是否可编辑 **/
	boolean isEdit = true; //

	/** 标记动态生成EditText控件是否不可用，动态页面打开不可编辑 **/
	boolean isDisable = true; //

	/** 是否通过，false表示否决 **/
	boolean isPass = true; //

	/** 是否使用手写签名，默认false不使用，配置信息从网络获取 **/
	boolean isSignByHand = false;

	/*** 是否已经签名 ,如果没有签名则不允许保存 */
	boolean isSigned = false;

	private TextView tv_web_mode;
	private List<String> photoPathList; // 要上传照片路径列表
	// private AddImageHelper addImageHelper;
	private LinearLayout ll_leader_signure;// 领导签字

	/** 表单明细查看按钮 */
	private LinearLayout ll_details_creatform;

	/** 表单明细显示区域 */
	private LinearLayout ll_root_details_form;
	/** 生成明细表布局显示布局 */
	private LinearLayout ll_root_create_details;
	/** 表单明细value */
	private List<List<String>> mDetailsValueResult;
	/** 添加表单明细按钮表单明细 */
	private LinearLayout ll_addDetails_creatform;

	/** 添加表单明细按钮表单明细 */
	private LinearLayout ll_scanDetails_creatform;

	/** 删除表单明细按钮 */
	private LinearLayout ll_deleteDetails_creatform;

	private ParseVmFormCreateUI parseVmFormCreateUI;

	private Handler loadDataHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case LOAD_SERVER_DATA_SUCCEEDED: // 加载表单数据成功
					ProgressDialogHelper.dismiss();
					// 动态控件不可编辑：流程状态不等于未提交 or 标志为不用提交的 未提交状态
					if ((mFlow != null && !mFlow.CurrentState.equals("未提交") && !mFlow.CurrentState
							.startsWith("已退回"))
							|| (isNotSubmit && mFlow != null && mFlow.CurrentState
							.equals("未提交"))) {
						imageViewSave.setVisibility(View.GONE);
						isEdit = false;
					}
					String jsonData = (String) msg.obj;
					LogUtils.i("xmlPullData", jsonData);
					// JsonUtils.ConvertJsonToDetailValue(jsonData);
					List<VmFormDef> formList = JsonUtils.ConvertJsonToList(
							jsonData, VmFormDef.class);
					if (formList == null || formList.size() == 0) {
						return;
					} else {
						if (mVmFormDef == null) {
							mVmFormDef = formList.get(0);
							titleName = !TextUtils.isEmpty(mVmFormDef.TableName) ? mVmFormDef.TableName
									: mVmFormDef.FormName;
							textViewTitle.setText(titleName + "");
							// mFlow.Id = mVmFormDef.FlowId;
							if (mVmFormDef.Workflow != null) {
								mFlowId = mVmFormDef.Workflow.Id;
							}
							initDefaultValue();
						}

						if (mVmFormDef.Dictionaries != null
								&& mVmFormDef.Dictionaries.containsKey("Product")) {
							showScanCode();
						}

						if (isAudit
								&& !isEdit
								&& !TextUtils
								.isEmpty(mVmFormDef.CurrentNodeEditableCells)) {
							// 如果页面整体不可编辑，可以审核,但是[可编辑单元格存在字段]，则该页面可保存
							imageViewSave.setVisibility(View.VISIBLE);
							isNotSubmit = true;
						}
						parseVmFormCreateUI = new ParseVmFormCreateUI(create_root,
								CreateVmFormActivity.this, mContext, mVmFormDef,
								isEdit);
						mEditList = parseVmFormCreateUI.createUI();// 设置表头
						parseVmFormCreateUI.setExpression();

						if (mVmFormDef.Fields != null
								&& mVmFormDef.Fields.size() > 0) {
							// 上传图片附件
							showAddImage(mVmFormDef.Fields);
						}

						if (mVmFormDef.Results != null
								&& mVmFormDef.Results.size() > 0) {
							// 显示审批意见模块
							showAuditOpinion(mVmFormDef.Results);
						}

						if (mVmFormDef.DetailFields != null
								&& mVmFormDef.DetailFields.size() > 0) {
							showDetailsForm(); // 显示表单明细
						}

						if (isAudit) {
							showAudit();
							getSettingOfSignByHand();
						}

					}
					break;
				case LOAD_SERVER_DATA_FAILED:
					ProgressDialogHelper.dismiss();
					Toast.makeText(CreateVmFormActivity.this, "下载网络数据失败，请检查网络连接",
							Toast.LENGTH_SHORT).show();
					break;
				case LOAD_SERVER_DATA_NOTFOUND:
					ProgressDialogHelper.dismiss();
					String jsonFailedData = (String) msg.obj;
					Toast.makeText(CreateVmFormActivity.this,
							"未找到编号" + jsonFailedData + "对应的表单", Toast.LENGTH_SHORT)
							.show();
					break;
				case UPLOAD_DATA_SUCCEEDED:
					ProgressDialogHelper.dismiss();
					pBar.setVisibility(View.GONE);
					imageViewSave.setImageDrawable(getResources().getDrawable(
							R.drawable.upsubmit));
					// 表单保存成功
					setResult(ApplyListFragmentActivity.REQUEST_CODE_NEW_FORM);
					if (isNotSubmit) {
						// 只保存不提交,保存完隐藏提交按钮
						imageViewSave.setVisibility(View.GONE);
						Toast.makeText(CreateVmFormActivity.this, "表单保存成功！",
								Toast.LENGTH_SHORT).show();

						if (parseVmFormCreateUI.getmEditStuts() == ParseVmFormCreateUI.EditableByCurrentNodeEditableCells) {
							// 用于记录可编辑单元格 记录已保存
							parseVmFormCreateUI
									.setmEditStuts(ParseVmFormCreateUI.EditableByCurrentNodeEditableCells_Saved);
						} else if (parseVmFormCreateUI.getmEditStuts() == ParseVmFormCreateUI.EditableByCurrentNodeEditableCells_DirectAduit) {
							submitApproval();
						}
					} else {
						// 保存成功，弹出框显示 已保存，是否立刻提交，
						submitFormInfo();
					}
					break;
				case UPLOAD_DATA_FAILED:
					ProgressDialogHelper.dismiss();
					pBar.setVisibility(View.GONE);
					Toast.makeText(CreateVmFormActivity.this, "表单保存失败！",
							Toast.LENGTH_SHORT).show();
					break;
				case UPLOAD_DATA_START:
					// if (photoPathList.size() > 0) {
					// pBar.setVisibility(View.VISIBLE);
					// pBar.setMax(photoPathList.size());
					// }
					progressBar_showupload.setVisibility(View.VISIBLE);
					break;
				case UPLOAD_DATA_END:
					progressBar_showupload.setVisibility(View.INVISIBLE);
					break;

				case UPLOAD_APPROVAL_FAILED:
					String resultInfo = (String) msg.obj;
					if (TextUtils.isEmpty(resultInfo)) {
						resultInfo = "申请失败";
					}
					Toast.makeText(mContext, resultInfo, Toast.LENGTH_LONG).show();
					break;
				case UPLOAD_APPROVAL_SUCCEEDED: // 提交表单成功
					List<User> aduitorList = (List<User>) msg.obj;
					if (aduitorList != null && aduitorList.size() > 0) {
						// 选择下个步骤审核人，再次提交
						selectAudiotor(aduitorList, "选择审核人", false);
					} else {
						MessageUtil
								.ToastMessage(CreateVmFormActivity.this, "已提交申请！");
						finish();
					}
					break;
				default:
					break;
			}
		}
	};

	String rusult = "";
	private final int SUCCEED_SUBMIT_AUDIT = 21;
	private final int FAILURE_SUBMIT_AUDIT = 22;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 3:
					// 审核通过 选择审核人
					ProgressDialogHelper.dismiss();
					// rusult = (String) msg.obj;
					List<User> aduitorList = (List<User>) msg.obj;
					if (aduitorList != null && aduitorList.size() > 0) {
						selectAudiotor(aduitorList, "选择审核人", true);
					} else {
						MessageUtil.ToastMessage(CreateVmFormActivity.this, "成功！");
						finish();
					}
					break;
				case 4:
					ProgressDialogHelper.dismiss();
					result = (String) msg.obj;
					MessageUtil.ToastMessage(CreateVmFormActivity.this, rusult
							+ "失败！");
					break;
				case GET_DYNAMIC_SUCCESS: // 点击动态，获取日志成功
					ProgressDialogHelper.dismiss();
					动态 dynamic = (动态) msg.obj;
					mFlow = dynamic.WorkFlow;
					if (mFlow != null) {
						// Init();
						// initViews(bundle);
						// initFlows();
						// dataId = mFlow.FormDataId + "";
						// typeId = mFlow.ClassTypeId;
						createForm();
					}
					break;
				case GET_DYNAMIC_FAILED: // 获取日志失败
					ProgressDialogHelper.dismiss();
					Toast.makeText(mContext, "加载表单失败，请稍后重试", Toast.LENGTH_LONG)
							.show();
					break;
				case SUCCEED_SUBMIT_AUDIT: // 选择审核人提交成功
					ProgressDialogHelper.dismiss();
					Toast.makeText(mContext, "提交成功", Toast.LENGTH_SHORT).show();
					finish();
					break;
				case FAILURE_SUBMIT_AUDIT: // 选择审核人提交失败
					ProgressDialogHelper.dismiss();
					String info = (String) msg.obj;
					Toast.makeText(mContext, "" + info, Toast.LENGTH_LONG).show();
					break;
				case SUCCEED_ADD_PRODUCT_DETAIL: // 添加产品明细
					ProgressDialogHelper.dismiss();
					产品型号 product = (产品型号) msg.obj;
					addProductDetails(product);
					break;
				case FAILURE_ADD_PRODUCT_DETAIL:
					ProgressDialogHelper.dismiss();
					break;
				default:
					break;
			}
		};
	};

	final HttpUtils httpUtils = new HttpUtils();

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createform);
		mContext = CreateVmFormActivity.this;
		findViews();
		setOnClickListener();
		try {
			LogUtils.i("oncreate", "服务器传来的表单编号,表示该条数据在服务器数据库的位置");
			Intent intent = getIntent();
			Bundle bundle = intent.getExtras();

			mDefalutValueMap = (HashMap<String, Object>) bundle
					.getSerializable(PROPERTY_MAPS);

			mFlow = (流程) bundle.getSerializable("flow");
			isAudit = bundle.getBoolean("isAudit", false);
			isDisable = bundle.getBoolean("isDisable");
			if (isDisable) {
				isEdit = false;
			}
			if (mFlow == null) {
				mFlowId = bundle.getInt("id");// 服务器传来的表单流程编号,表示该条数据在服务器数据库的位置
				mDataId = bundle.getString("dataId", "0");// 表单号,0表示新建
				typeId = bundle.getInt("typeId");// 获取表的类型id号,110表示请假,94表示报销
				typeName = bundle.getString("typeName", "表单名称");
				formName = bundle.getString("formName", "表单名称");
				isNotSubmit = bundle.getBoolean("isNotSubmit", false);
				titleName = typeName;
			} else {
				mFlowId = mFlow.Id;
				mDataId = mFlow.getFormDataId() + "";
				typeId = mFlow.getClassTypeId();
				typeName = mFlow.getName();
				formName = mFlow.getFormName();
				LogUtils.i(TAG, "状态：" + mFlow.CurrentState);
				titleName = mFlow.getClassTypeName();
			}
			// 周工作总结报告，只保存不用提交
			if ("周工作总结报告".equals(typeName)) {
				isNotSubmit = true;
			}

			LogUtils.i("testkeno", mFlowId + "");
			LogUtils.i("oncreate", "==id==" + mFlowId + "==typeId==" + typeId
					+ "==dataId==" + mDataId + "==name==" + typeName
					+ "==isAudit==" + isAudit + "==isNotSubmit=" + isNotSubmit);
			createForm();
			initFlows();
		} catch (Exception e) {
			LogUtils.e(TAG, "进入浏览器模式:\n" + e);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 监听信鸽 Notification点击打开的通知
		XGPushClickedResult clickedResult = XGPushManager
				.onActivityStarted(this);
		if (clickedResult != null) {
			// Toast.makeText(this, "来自信鸽：" + clickedResult.toString(),
			// Toast.LENGTH_LONG).show();
			LogUtils.i("clickedResult", clickedResult.toString());
			String customContent = clickedResult.getCustomContent();
			LogUtils.i("CustomContent", customContent);
			try {
				JSONObject jo = new JSONObject(customContent);
				// 获取动态类型 和 数据编号
				final String dynamicType = jo.getString("dynamicType");
				final String dataId = jo.getString("dataId");
				LogUtils.i("dynami", dynamicType + "--" + dataId);
				ProgressDialogHelper.show(mContext, "日志加载中");
				new Thread(new Runnable() {
					@Override
					public void run() {
						动态 dynamic = mZLServiceHelper.LoadDynamicById(
								dynamicType, dataId);
						if (dynamic != null && dynamic.WorkFlow != null) {
							// 设置日志为已读
							// mDataHelper.ReadLog(dynamic.Log, context);
							// 发送到handler中进行处理
							Message msg = handler.obtainMessage();
							msg.obj = dynamic;
							msg.what = GET_DYNAMIC_SUCCESS;
							handler.sendMessage(msg);
						} else {
							handler.sendEmptyMessage(GET_DYNAMIC_FAILED);
						}
					}
				}).start();
			} catch (JSONException e) {
				e.printStackTrace();
				// LogUtils.e("erro", e.toString());
				handler.sendEmptyMessage(GET_DYNAMIC_FAILED);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// List<FieldInfo> fieldInfos = getFieldInfoList();
		// String str = "";
		// for (int i = 0; i < mVmFormDef.Fields.size(); i++) {
		// str += mVmFormDef.Fields.get(i).fieldName + "\t";
		// }
		//
		// Toast.makeText(context, "onSaveInstanceState\n" + str,
		// Toast.LENGTH_LONG).show();
		List<FieldInfo> tempFields = getFieldInfoList();

		// for (int i = 0; i < mVmFormDef.Fields.size(); i++) {
		// FieldInfo info = mVmFormDef.Fields.get(i);
		// if (info != null && tempFields != null) {
		// for (int j = 0; j < tempFields.size(); j++) {
		// }
		// }
		// }

		mVmFormDef.Fields = tempFields;
		outState.putSerializable("VmFormDef", mVmFormDef);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mVmFormDef = (VmFormDef) savedInstanceState.get("VmFormDef");
		// LogUtils.i(TAG, "onRestoreInstanceState--" +
		// mVmFormDef.DetailFields.size());
		// Toast.makeText(context,
		// "onRestoreInstanceState---" + mVmFormDef.DetailFields.size(),
		// Toast.LENGTH_SHORT).show();
	}

	/**
	 * 动态生成表单
	 */
	private void createForm() {
		URL = Global.BASE_URL_PROCESS + Global.EXTENSION
				+ "Flow/GetXmlContent?id=" + typeId + "&dataid=" + mDataId;
		// URL = Global.BASE_URL_PROCESS + Global.EXTENSION
		// + "Flow/GetFormDefAndData/62/323";
		LogUtils.i("oncreate", URL);
		ProgressDialogHelper.show(mContext);
		// 启动线程下载数据
		new Thread() {
			public void run() {
				try {
					// 访问网络 获得表单xml文件
					String data = httpUtils.httpGet(URL);
					// 执行完毕后给handler发送一个消息
					LogUtils.i("createForm", data);
					Message msg = new Message();
					if (!TextUtils.isEmpty(data) && !data.equals("网络错误")
							&& !data.equals("网络异常")) {
						// typeId = mFlow.ClassTypeId;
						if (typeId == 0) {
							msg.what = LOAD_SERVER_DATA_NOTFOUND;
							msg.obj = "" + typeId;
						} else {
							msg.what = LOAD_SERVER_DATA_SUCCEEDED;
							msg.obj = data;
						}
					} else if (data.equals("网络错误")) {
						msg.what = LOAD_SERVER_DATA_FAILED;
					} else {
						msg.what = LOAD_SERVER_DATA_NOTFOUND;
						msg.obj = "" + typeId;
					}
					loadDataHandler.sendMessage(msg);
				} catch (Exception ex) {
					LogUtils.e("erro", ex + "");
					Message msg = new Message();
					msg.obj = ex;
					msg.what = LOAD_SERVER_DATA_FAILED;
					loadDataHandler.sendMessage(msg);
				}
			};
		}.start();
	}

	/**
	 * 动态生成表单明细
	 */
	private void createDetailsForm() {
		if (mVmFormDef.DetailValues == null
				|| mVmFormDef.DetailValues.size() == 0) {
			mVmFormDef.DetailValues = new ArrayList<List<String>>();
			// 添加一行
			int size = mVmFormDef.DetailFields.size();
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < size; i++) {
				list.add("");
			}
			mVmFormDef.DetailValues.add(list);
		}
		parseVmFormCreateUI = new ParseVmFormCreateUI(ll_root_create_details,
				CreateVmFormActivity.this, mContext, mVmFormDef, isEdit);
		// mDetailsValueResult = parseVmFormCreateUI.createDetailsUI();
		mDetailsValueResult = parseVmFormCreateUI.createVeticalDetailsUI();
	};

	/**
	 * 动态生成纵向表单明细
	 */
	private void createVeticalDetailsForm() {
		if (mVmFormDef.DetailValues == null
				|| mVmFormDef.DetailValues.size() == 0) {
			mVmFormDef.DetailValues = new ArrayList<List<String>>();
			// 添加一默认行
			int size = mVmFormDef.DetailFields.size();
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < size; i++) {
				list.add("");
			}
			mVmFormDef.DetailValues.add(list);
		}
		parseVmFormCreateUI = new ParseVmFormCreateUI(ll_root_create_details,
				CreateVmFormActivity.this, mContext, mVmFormDef, isEdit);
		mDetailsValueResult = parseVmFormCreateUI.createVeticalDetailsUI();
	}

	/**
	 * 浏览器模式加载表单
	 */
	private void loadWebView() {
		// requestFocusForForm();
		// textViewTitle.setText(typeName);
		textViewTitle.setText(titleName + "");
		// 加载webview
		webview_root.setVisibility(View.VISIBLE);
		ll_root_details_form.setVisibility(View.GONE);

		// 相当于打开浏览器对象
		webview_root = (WebView) findViewById(R.id.webview_root);
		// 为25%，最小缩放等级，将页面所有内容显示在手机屏幕上
		webview_root.setInitialScale(50);
		// 得到浏览器设置
		WebSettings webSettings = webview_root.getSettings();
		// 设置浏览器中javascript可执行
		webSettings.setJavaScriptEnabled(true);
		// 设置webview推荐使用的窗口
		webSettings.setUseWideViewPort(true);
		// 置webview加载的页面的
		webSettings.setLoadWithOverviewMode(true);
		// 设置缩放控制
		webSettings.setBuiltInZoomControls(true);
		// 支持缩放
		webSettings.setSupportZoom(true);
		// 缩放按钮
		webSettings.setBuiltInZoomControls(true);

		String url = Global.PROCESS_URL_HEADER + formName + "&id=" + mDataId;
		LogUtils.i("url_webview", url);
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("passport", Global.mUser.Passport);
		webview_root.loadUrl(url, headers);

		webview_root.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				webview_root.getParent().getParent()
						.requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
	}

	@SuppressLint("NewApi")
	public void findViews() {
		dictionaryHelper = new DictionaryHelper(CreateVmFormActivity.this);
		mDictIosPicker = new DictIosPickerBottomDialog(mContext);
		// photoSerialNo = UUID.randomUUID().toString();
		imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		tv_web_mode = (TextView) findViewById(R.id.tv_web_mode);
		progressBar_showupload = (ProgressBar) findViewById(R.id.progressBar_showupload);
		pbar_web = (MyProgressBar) findViewById(R.id.pbar_createform);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		imageViewSave = (ImageView) findViewById(R.id.imageViewSave);
		create_root = (LinearLayout) findViewById(R.id.create_root);
		ll_upload_photo = (LinearLayout) findViewById(R.id.create_photoroot);
		ll_approval = (LinearLayout) findViewById(R.id.create_approval);
		ll_details_creatform = (LinearLayout) findViewById(R.id.ll_details_creatform);
		// addImage_attachFile = (HorizontalScrollViewAddImage)
		// findViewById(R.id.AddImage_createfrom_attachfilename);
		multipleAttachView = (MultipleAttachView) findViewById(R.id.multipleAttachView_createform);

		buttonTag_createfrom_commit = (Button) findViewById(R.id.buttonTag_createfrom_commit);
		buttonTag_createfrom_approval = (Button) findViewById(R.id.buttonTag_createfrom_approval);
		buttonTag_createfrom_votedown = (Button) findViewById(R.id.buttonTag_createfrom_votedown);
		editText_approval_opinion = (EditText) findViewById(R.id.editText_approval_opinion);
		editText_approval_opinion.clearFocus();
		pBar = (ProgressBar) findViewById(R.id.progress_add);
		ll_leader_signure = (LinearLayout) findViewById(R.id.ll_leader_signure_createform);
		// addImageHelper = new AddImageHelper(this, CreateVmFormActivity.this,
		// addImage_attachFile, null, true);
		pictureUtils = new PictureUtils(CreateVmFormActivity.this);
		webview_root = (WebView) findViewById(R.id.webview_root);

		ll_root_details_form = (LinearLayout) findViewById(R.id.ll_root_details_form);
		ll_root_create_details = (LinearLayout) findViewById(R.id.ll_root_create_details_form);
		ll_addDetails_creatform = (LinearLayout) findViewById(R.id.ll_addDetails_creat_details_form);
		ll_deleteDetails_creatform = (LinearLayout) findViewById(R.id.ll_deleteDetails_creat_details_form);
		ll_scanDetails_creatform = (LinearLayout) findViewById(R.id.ll_scan_creat_details_form);
	}

	public void setOnClickListener() {
		imageViewCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 提交和保存按钮监听
		imageViewSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isInit) {
					isInit = false;
					imageViewSave.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_check));
				} else {
					if (isEmpty() || isEmptyDetails()) {
						Toast.makeText(CreateVmFormActivity.this,
								nullContentFieldName + "不能为空",
								Toast.LENGTH_SHORT).show();
					} else {
						if (isParse()) {
							uploadMulipleFile();
						} else {
							Toast.makeText(CreateVmFormActivity.this,
									mustBeIntFieldName + " 只能输入数字哦",
									Toast.LENGTH_SHORT).show();
						}
					}

				}
			}
		});
		tv_web_mode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (create_root != null) {
					create_root.setVisibility(View.GONE);
				}
				// if (addImage_attachFile != null) {
				// addImage_attachFile.setVisibility(View.GONE);
				// }
				if (multipleAttachView != null) {
					multipleAttachView.setVisibility(View.GONE);
				}
				if (ll_leader_signure != null) {
					ll_leader_signure.setVisibility(View.GONE);
				}

				if (ll_root_create_details != null) {
					ll_leader_signure.setVisibility(View.GONE);
				}

				tv_web_mode.setVisibility(View.GONE);
				ll_approval.setVisibility(View.GONE); // 隐藏审核意见填写框
				ll_details_creatform.setVisibility(View.GONE); // 隐藏明细按钮
				loadWebView();
			}
		});

	}

	/**
	 * 判断字段是否为空
	 *
	 * @return 如果为空则返回 true
	 */
	private boolean isEmpty() {
		for (int i = 0; i < mEditList.size(); i++) {
			EditText editText = mEditList.get(i);
			FieldInfo fieldInfo = (FieldInfo) editText.getTag();
			String fieldName = fieldInfo.fieldName;
			// String fieldType = fieldInfo.fieldType;
			String fieldDict = fieldInfo.fieldDict;
			String required = fieldInfo.required;
			String fieldValue = fieldInfo.fieldValue;
			String fieldStyle = fieldInfo.fieldStyle;
			if (!TextUtils.isEmpty(required)) {
				// 仅仅对必填项校验
				// 判断用于选择的项，如果不是文本类型并且字典项不为空
				if (!"textbox".equalsIgnoreCase(fieldStyle)
						&& !TextUtils.isEmpty(fieldDict)) {
					if (TextUtils.isEmpty(fieldValue)) {
						nullContentFieldName = fieldName;
						return true;
					}
				} else {
					if (!"image".equals(fieldStyle)) {
						// 判断文本项
						String content = editText.getText().toString().trim();
						if (TextUtils.isEmpty(content)) {
							nullContentFieldName = fieldName;
							return true;
						} else {
							// 文本项赋值
							fieldInfo.fieldValue = content;
							editText.setTag(fieldInfo);
						}
					}
				}
			} else {
				if ("datepicker".equalsIgnoreCase(fieldStyle)) {
					String content = editText.getText().toString().trim();
					// 时间若为空，则设置为当前时间
					content = TextUtils.isEmpty(content) ? ViewHelper
							.getDateString() : content;
					// 文本项赋值
					fieldInfo.fieldValue = content;
					editText.setTag(fieldInfo);
				}
			}
		}

		List<MultipleAttachView> multipleAttachViews = parseVmFormCreateUI
				.getMultipleAttachViews();
		for (MultipleAttachView attachView : multipleAttachViews) {
			FieldInfo fieldData = (FieldInfo) attachView.getTag();
			if (!TextUtils.isEmpty(fieldData.required)
					&& attachView.getAttachDataList().size() == 0) {
				nullContentFieldName = fieldData.fieldName;
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断要求Int和Double类型字段是否为输入为数字
	 *
	 * @return
	 */
	private boolean isParse() {
		for (int i = 0; i < mEditList.size(); i++) {
			EditText eText = mEditList.get(i);
			FieldInfo fieldInfo = (FieldInfo) eText.getTag();
			String fieldName = fieldInfo.fieldName;
			String dict = fieldInfo.fieldDict;
			String fieldValue = fieldInfo.fieldValue;
			String dataType = fieldInfo.dataType;
			String required = fieldInfo.required;

			if ("int".equalsIgnoreCase(dataType)) {
				try {
					String content = eText.getText().toString().trim();
					if (!TextUtils.isEmpty(dict)) {
						content = fieldValue;
					}
					content = TextUtils.isEmpty(content) ? "0" : content;

					Integer.parseInt(content);
				} catch (Exception e) {
					mustBeIntFieldName = fieldName;
					return false;
				}
			}

			if ("double".equalsIgnoreCase(dataType)) {
				try {
					String content = eText.getText().toString().trim();
					content = TextUtils.isEmpty(content) ? "0" : content;
					Double.parseDouble(content);
				} catch (Exception e) {
					mustBeIntFieldName = fieldName;
					return false;
				}
			}
		}
		return true;
	}

	/***
	 * 校验表单明细是否为空
	 *
	 * @return 为空则返回ture
	 */
	private boolean isEmptyDetails() {
		// 判断明细表内容是否为空
		if (mVmFormDef.DetailFields != null
				&& mVmFormDef.DetailFields.size() > 0) {
			if (mDetailsValueResult == null || mDetailsValueResult.size() <= 0) {
				nullContentFieldName = mVmFormDef.FormName + "明细表";
				return true;
			} else {
				for (int k = 0; k < mDetailsValueResult.size(); k++) { // 遍历行
					List<String> rowValue = mDetailsValueResult.get(k);
					for (int i = 0; i < mVmFormDef.DetailFields.size(); i++) {// 遍历列
						FieldInfo fieldInfo = mVmFormDef.DetailFields.get(i);
						String fieldName = fieldInfo.fieldName;
						String fieldType = fieldInfo.fieldStyle;
						String fieldDict = fieldInfo.fieldDict;
						String required = fieldInfo.required;
						String fieldValue = fieldInfo.fieldValue;
						String content = rowValue.get(i);
						int rows = k + 1;
						if (!TextUtils.isEmpty(required)
								&& TextUtils.isEmpty(content)) {
							nullContentFieldName = "明细表第" + rows + "行"
									+ fieldName;
							return true;
						}
					}
				}
			}
		} else {
			return false;
		}
		return false;
	}

	/**
	 * 给客户的控件赋值
	 *
	 */
	private void clientControl(String userName, int userId) {
		EditText eText = null;
		FieldInfo fieldInfo = null;
		for (int i = 0; i < mEditList.size(); i++) {
			eText = mEditList.get(i);
			fieldInfo = (FieldInfo) eText.getTag();
			String fieldDict = fieldInfo.fieldDict;
			if ("客户".equals(fieldDict)) {
				eText.setText(userName);
				fieldInfo.fieldValue = userId + "";
				eText.setTag(fieldInfo);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			// if (mOnActivityForResultListener != null) {
			// mOnActivityForResultListener.onActivityForResult(requestCode,
			// resultCode, data);
			// }

			// if (requestCode == addImageHelper.CAMERA_TAKE_HELPER
			// || requestCode == addImageHelper.PICKED_PHOTO_WITH_DATA) {
			// addImageHelper.refresh(requestCode, data);
			// parseVmFormCreateUI.attachControl(mEditList, requestCode, data);
			// }

			if (requestCode == SELECT_CLIENT_CODE) {
				// 取出客户名称字符串
				Bundle bundle = data.getExtras();
				int clientId = bundle.getInt(ClientListActivity.ClientId);
				LogUtils.i("clinetId", "clientId:" + clientId);
				if (clientId != 0) {
					DictionaryHelper dictionaryHelper = new DictionaryHelper(
							getApplicationContext());
					String clientName = dictionaryHelper
							.getClientNameById(clientId);
					LogUtils.i("kjxi", "clientName" + clientName);
					clientControl(clientName, clientId);
				}
			}

			if (requestCode == SELECT_USER_CODE) {
				int count = mEditList.size();
				// 取出字符串
				Bundle bundle = data.getExtras();
				String mUserSelectId = bundle.getString("UserSelectId")
						.replace("'", "").replace(";", "");
				String mUserSelectName = bundle.getString("UserSelectName");
				LogUtils.i("selectUser", mUserSelectId + "--" + mUserSelectName);
				if (!TextUtils.isEmpty(mUserSelectName)) {
					parseVmFormCreateUI.updateUserOnActivityForResult(
							mEditList, mUserSelectName, mUserSelectId);
				}
			}

			/***
			 * 选择产品
			 */
			if (requestCode == SELECT_PRODUCT_CODE) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					产品型号 product = (产品型号) bundle
							.getSerializable(PRODUCT_SELECTED);
					Toast.makeText(mContext, "选择产品:" + product.名称,
							Toast.LENGTH_SHORT).show();
					parseVmFormCreateUI.setProductControl(mEditList,
							product.名称, product.编号 + "");
				}
			}

			if (requestCode == CODE_REQUEST_SCAN) {
				String scanCode = data
						.getStringExtra(CaptureActivity.RESULT_SCAN_CODE);
				Toast.makeText(mContext, "扫描条码成功：" + scanCode,
						Toast.LENGTH_SHORT).show();
				if (!TextUtils.isEmpty(scanCode)) {
					// etSearch.setText(scanCode);
					ProgressDialogHelper.show(mContext);
					getProductByCode(scanCode);
				}
			}

			// 修改明细表
			if (requestCode == REQUEST_CODE_DETAILS) {
				LogUtils.i(TAG, "onActivity--->REQUEST_CODE_UPDATE_DETAILS");
				Bundle bundle = data.getExtras();
				int listAtPos = bundle.getInt("listAtPos");
				List<String> rowValueList = bundle
						.getStringArrayList("rowValueList");
				for (int i = 0; i < mVmFormDef.DetailValues.get(listAtPos)
						.size(); i++) {
					LogUtils.i(TAG,
							"---->"
									+ mVmFormDef.DetailValues.get(listAtPos)
									.get(i));
				}
				// 获取到编辑过的item,移除原有替换为新的
				mVmFormDef.DetailValues.remove(listAtPos);
				mVmFormDef.DetailValues.add(listAtPos, rowValueList);
				for (int i = 0; i < mVmFormDef.DetailValues.get(listAtPos)
						.size(); i++) {
					LogUtils.e(TAG,
							"---->"
									+ mVmFormDef.DetailValues.get(listAtPos)
									.get(i));
				}
				ll_root_create_details.removeAllViews();
				parseVmFormCreateUI = new ParseVmFormCreateUI(
						ll_root_create_details, CreateVmFormActivity.this,
						mContext, mVmFormDef, isEdit);
				// mDetailsValueResult = parseVmFormCreateUI.createDetailsUI();
				mDetailsValueResult = parseVmFormCreateUI
						.createVeticalDetailsUI();
				parseVmFormCreateUI.setDetailExpression(mEditList,
						mDetailsValueResult);
			}

			if (!TextUtils.isEmpty(parseVmFormCreateUI.getMultipleFieldName())) {
				parseVmFormCreateUI.updateMultipeAttachViewOnActivityForResult(
						requestCode, resultCode, data);
			} else {
				multipleAttachView.onActivityiForResultImage(requestCode,
						resultCode, data);
			}
		}

		// 审核
		if (requestCode == APPROVAL_OPINION_CODE) {
			if (resultCode == Approval_Opinion.APPROVAL_OPINION_RESULT) {
				String opinion = data.getStringExtra(Approval_Opinion.OPINION);
				editText_approval_opinion.setText(opinion);
			}
			if (resultCode == Approval_Opinion.APPROVAL_OPINION_RESULT_SUCCEED) {
				// 如果审批成功，返回上级一个标志信息，APPROVAL_RESULT_SUCCEED
				// setResult(AskForLeaveListActivity.APPROVAL_RESULT_SUCCEED);
				setResult(ApplyListFragmentActivity.APPROVAL_RESULT_SUCCEED);
				finish();
			}
		}
	}

	/**
	 * 获取上传表单 的字段数据
	 *
	 * @return
	 */
	private List<FieldInfo> getFieldInfoList() {
		List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();
		for (int i = 0; i < mEditList.size(); i++) {
			FieldInfo fInfo = (FieldInfo) mEditList.get(i).getTag();
			if (TextUtils.isEmpty(fInfo.fieldDict)
					&& !"Product".equalsIgnoreCase(fInfo.fieldStyle)
					&& !"checkbox".equalsIgnoreCase(fInfo.fieldStyle)) {
				if (!TextUtils.isEmpty(mEditList.get(i).getText().toString())) {
					// 可编辑方式需要取文本框当前显示内容
					fInfo.fieldValue = mEditList.get(i).getText().toString();
				}
			}
			fieldInfos.add(fInfo);
		}
		return fieldInfos;
	}

	/**
	 * 获取明细表上传表单 的字段数据
	 *
	 * @return
	 */
	private List<FormInfo> getDetailsFormInfos() {
		// 明细表头为空，则表说明没有明细表
		if (mVmFormDef.DetailFields == null
				|| mVmFormDef.DetailFields.size() == 0) {
			return null;
		}

		// 明细表无值说明未填写
		if (mVmFormDef.DetailValues == null
				|| mVmFormDef.DetailValues.size() == 0) {
			return null;
		}

		List<FormInfo> formList = new ArrayList<FormInfo>();
		// 根据明细值表（二维数组）生成字段信息
		for (int i = 0; i < mVmFormDef.DetailValues.size(); i++) {
			/** 明细表 */
			FormInfo detailFormInfo = new FormInfo();
			detailFormInfo.表名 = mVmFormDef.DetailTableName;

			// 明细表 一行中内容，列数有表头DetailFields数量控制
			List<String> detailValue = mVmFormDef.DetailValues.get(i);
			List<字段信息> 字段信息s = new ArrayList<字段信息>();

			for (int j = 0; j < mVmFormDef.DetailFields.size(); j++) {
				字段信息 info = new 字段信息();
				String dataType = mVmFormDef.DetailFields.get(j).dataType;
				info.字段类型 = mVmFormDef.DetailFields.get(j).fieldStyle;
				info.字段名 = mVmFormDef.DetailFields.get(j).fieldName;
				info.字段值 = detailValue.get(j);
				if (TextUtils.isEmpty(info.字段值)) {
					// 设置默认值
					if ("datepicker".equalsIgnoreCase(info.字段类型)) {
						info.字段值 = "0001-01-01 00:00:00";
					} else if ("int".equalsIgnoreCase(dataType)
							|| "double".equalsIgnoreCase(dataType)
							|| "bit".equalsIgnoreCase(dataType)) {
						info.字段值 = "0";
					}
				}
				字段信息s.add(info);
			}
			detailFormInfo.字段s = 字段信息s;
			formList.add(detailFormInfo);
		}
		return formList;
	}

	/**
	 * 将fieldInfo转化为中文属性的FieldInfo的List集合
	 *
	 * @param fieldInfo
	 *            返回的字段名，字段类型，字段值的集合(字段值是EditText类型)
	 * @param photoId
	 *            图片附件号
	 * @return FieldInfo的List集合
	 */
	private List<字段信息> convertListToFieldInfo(List<FieldInfo> fieldInfoList,
											  String photoId) {
		List<字段信息> fieldInfos_result = new ArrayList<字段信息>();
		for (int i = 0; i < fieldInfoList.size(); i++) {
			字段信息 fieldInfo = new 字段信息();
			fieldInfo.字段类型 = fieldInfoList.get(i).fieldStyle;

			if (!"image".equals(fieldInfo.字段类型)) {
				fieldInfo.字段名 = fieldInfoList.get(i).fieldName;
				// 把字段值存于 fieldValue中
				fieldInfo.字段值 = fieldInfoList.get(i).fieldValue;
				// 如果选择类型的字段未选择，设置默认值0
				if (TextUtils.isEmpty(fieldInfo.字段值)
						&& ("combobox".equalsIgnoreCase(fieldInfo.字段类型)
						|| "dropdownlist"
						.equalsIgnoreCase(fieldInfo.字段类型) || "checkbox"
						.equalsIgnoreCase(fieldInfo.字段类型))) {
					fieldInfo.字段值 = 0 + "";
				}
				fieldInfos_result.add(fieldInfo);
			}
		}

		List<MultipleAttachView> multipleAttachViews = parseVmFormCreateUI
				.getMultipleAttachViews();
		if (multipleAttachViews != null && multipleAttachViews.size() > 0) {
			for (MultipleAttachView attachView : multipleAttachViews) {
				FieldInfo fieldData = (FieldInfo) attachView.getTag();
				字段信息 fieldInfo = new 字段信息(fieldData.fieldName,
						fieldData.fieldValue, fieldData.fieldStyle);
				fieldInfos_result.add(fieldInfo);
			}
		}

		字段信息 fieldInfo = new 字段信息();
		fieldInfo.字段类型 = "String";
		fieldInfo.字段名 = "附件";
		fieldInfo.字段值 = photoId;
		fieldInfos_result.add(fieldInfo);

		// 是否有制单时间
		boolean isExisitTime = false;

		// 是否已有制单人
		boolean isExisitUser = false;
		for (int i = 0; i < fieldInfos_result.size(); i++) {
			字段信息 fInfo = fieldInfos_result.get(i);
			if (!TextUtils.isEmpty(fInfo.字段名)) {
				if (fInfo.字段名.equals("制单人") && !TextUtils.isEmpty(fInfo.字段值)) {
					isExisitUser = true;
				} else if (fInfo.字段名.equals("制单时间")
						&& !TextUtils.isEmpty(fInfo.字段值)) {
					isExisitTime = true;
				}
			}
		}

		if (!isExisitTime) {
			// 每张表默认添加制单时间和制单人
			字段信息 fieldInfo2 = new 字段信息();
			fieldInfo2.字段类型 = "String";
			fieldInfo2.字段名 = "制单时间";
			fieldInfo2.字段值 = ViewHelper.getDateString();
			fieldInfos_result.add(fieldInfo2);
		}

		if (!isExisitUser) {
			字段信息 fieldInfo3 = new 字段信息();
			fieldInfo3.字段类型 = "String";
			fieldInfo3.字段名 = "制单人";
			fieldInfo3.字段值 = Global.mUser.Id;
			fieldInfos_result.add(fieldInfo3);
		}
		return fieldInfos_result;
	}

	/**
	 * 初始化 流程表单数据，显示到页面
	 */
	private void initFlows() {
		// 审批表单 流程表编号
		// formId = mFlow.Id;
		// textViewTitle.setText(typeName + "");
		textViewTitle.setText(titleName + "");
	}

	/***
	 * 选择下个步骤审核人
	 *
	 * @param userList
	 *
	 * @param title
	 *            对话框标题
	 * @param isAudit
	 *            是否是审核，ｆａｌｓｅ表示提交
	 */
	private void selectAudiotor(final List<User> userList, String title,
								final boolean isAudit) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_lv,
				null);
		TextView tvTitle = (TextView) view.findViewById(R.id.tv_title_dialog);
		tvTitle.setText("" + title);
		ListView lvSelect = (ListView) view.findViewById(R.id.lv_dialog);
		CommanAdapter<User> adapter = new CommanAdapter<User>(userList,
				mContext, R.layout.item_select_user_dialog) {
			@Override
			public void convert(int position, User item,
								BoeryunViewHolder viewHolder) {
				TextView tvName = viewHolder
						.getView(R.id.tv_name_select_dialog);
				tvName.setText("" + item.getUserName());
			}
		};
		lvSelect.setAdapter(adapter);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		final AlertDialog dialog = builder.create();
		lvSelect.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				String userName = userList.get(position).getUserName();
				final String aduitorId = userList.get(position).getId() + "";
				// Toast.makeText(context, userName, Toast.LENGTH_SHORT).show();
				dialog.dismiss();
				ProgressDialogHelper.show(mContext);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							if (isAudit) {
								// 审核
								mZLServiceHelper.submitApprovalNew(opinion,
										attachId, mFlowId, aduitorId, isPass,
										handler);
							} else {
								// 提交
								String result = mZLServiceHelper
										.submitFormInfoNew(mDataId + "",
												mVmFormDef.TableName, aduitorId);
								Message msg = handler.obtainMessage();
								String status = JsonUtils.parseStatus(result);
								String data = JsonUtils.pareseData(result);
								if ("0".equals(status)) {
									msg.obj = data;
									msg.what = FAILURE_SUBMIT_AUDIT;
								} else {
									msg.what = SUCCEED_SUBMIT_AUDIT;
								}
								handler.sendMessage(msg);
							}
						} catch (Exception e) {
							Message msg = handler.obtainMessage();
							msg.what = FAILURE_SUBMIT_AUDIT;
							msg.obj = "审批失败";
							handler.sendMessage(msg);
							LogUtils.e(TAG, "审批：" + e);
						}
					}
				}).start();
			}
		});
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}

	/**
	 * 保存表单数据到服务器
	 */
	private void saveFormInfo() {
		ProgressDialogHelper.show(mContext);
		Message msgStart = new Message();
		msgStart.what = UPLOAD_DATA_START;
		loadDataHandler.sendMessage(msgStart);
		multipleAttachView.uploadImage(new IOnUploadMultipleFileListener() {
			@Override
			public void onStartUpload(int sum) {
				pBar.setVisibility(View.VISIBLE);
				pBar.setMax(sum);
			}

			@Override
			public void onProgressUpdate(int completeCount) {
				pBar.setProgress(completeCount);
			}

			@Override
			public void onComplete(String attachIds) {
				pBar.setVisibility(View.GONE);
				final String attachMent = attachIds;// 附件
				Message msgEnd = new Message();
				msgEnd.what = UPLOAD_DATA_END;
				loadDataHandler.sendMessage(msgEnd);
				new Thread(new Runnable() {
					@Override
					public void run() {
						uploadPhotoIdToNetwork(attachMent);
					}
				}).start();
			}
		});

	}

	/**
	 * 提交表单数据到服务器
	 */
	private void submitFormInfo() {
		AlertDialog.Builder builderWrite = new AlertDialog.Builder(
				CreateVmFormActivity.this);
		builderWrite
				.setMessage("表单保存成功，是否立刻提交?")
				.setCancelable(false)
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (isFormSaved) {
							if (isEmpty() || isEmptyDetails()) {
								Toast.makeText(CreateVmFormActivity.this,
										nullContentFieldName + "不能为空",
										Toast.LENGTH_SHORT).show();
							} else {
								if (isParse()) {
									// 启动线程上传提交数据
									new Thread() {
										public void run() {
											try {
												// 开启线程时,发一条消息给handler,使progress显示
												Message msgStart = new Message();
												msgStart.what = UPLOAD_DATA_START;
												loadDataHandler
														.sendMessage(msgStart);
												Looper.prepare();
												Message msgUp = new Message();
												try {
													String result = mZLServiceHelper
															.submitFormInfoNew(
																	mDataId,
																	mVmFormDef.TableName,
																	null);
													String returnMsg = JsonUtils
															.parseLoginMessage(result);
													String status = JsonUtils
															.parseStatus(result);
													List<User> aduitList = JsonUtils
															.pareseJsonToList(
																	returnMsg,
																	User.class);
													LogUtils.i("testkeno3",
															"==result=="
																	+ result);
													if ("1".equals(status)) {
														msgUp.what = UPLOAD_APPROVAL_SUCCEEDED;
														msgUp.obj = aduitList;
														setResult(RESULT_CODE_NEW_FORM);
													} else {
														// 过去借助Service中的handler，已修改
														msgUp.obj = JsonUtils
																.pareseData(result);
														msgUp.what = UPLOAD_APPROVAL_FAILED;
													}
													// 图片上传结束后，发一条消息给服务，显示上传成功与否
													loadDataHandler
															.sendMessage(msgUp);
												} catch (Exception ex) {
													msgUp.what = UPLOAD_APPROVAL_FAILED;
													loadDataHandler
															.sendMessage(msgUp);
													LogUtils.e(TAG, ex + "");
												}
												// 线程结束时,发一条消息给handler,使progress不显示
												Message msgEnd = new Message();
												msgEnd.what = UPLOAD_DATA_END;
												loadDataHandler
														.sendMessage(msgEnd);
												Looper.loop();
											} catch (Exception e) {
												LogUtils.e(TAG, e + "");
											}
										};
									}.start();
								} else {
									Toast.makeText(CreateVmFormActivity.this,
											mustBeIntFieldName + "只能为数字",
											Toast.LENGTH_SHORT).show();
								}
							}
						} else {
							Toast.makeText(CreateVmFormActivity.this,
									"请先保存,再提交", Toast.LENGTH_SHORT).show();
						}
					}
				})
				.setNegativeButton("否", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builderWrite.create().show();
	}

	/**
	 * 上传图片的ID给服务器
	 *
	 * @param attachId图片的编号
	 * @return 返回提交的表单编号
	 */
	public String uploadPhotoIdToNetwork(String attachId) {
		FormInfo formInfo = new FormInfo();
		// 表名 对应流程分类表中的 名称
		// 表单 对应流程分类表中的 表单名称
		mDataId = TextUtils.isEmpty(mDataId) ? "0" : mDataId;
		formInfo.编号 = Integer.valueOf(mDataId);
		// formInfo.编号 = 0;
		// formInfo.字段 = convertListToFieldInfo(listResult, hashMap, attachId);
		formInfo.字段s = convertListToFieldInfo(getFieldInfoList(), attachId);
		// 流程分类表中表名可能重复，表单名称不同，所以用来区分具体表单xml
		formInfo.表单 = mVmFormDef.TableName;
		formInfo.表名 = typeName;
		LogUtils.i("testKeno2", "formName=" + formInfo.表名 + ",表单名称:"
				+ formInfo.表单);
		formInfo.明细表信息s = getDetailsFormInfos();
		if (formInfo.明细表信息s == null || formInfo.明细表信息s.size() == 0) {
		} else {
			formInfo.明细表名 = mVmFormDef.DetailTableName;
		}
		int dataId = 0;
		Message msg = new Message();
		try {
			// 保存后返回流程分类编号
			mDataId = mZLServiceHelper.WriteFormInfoNew(formInfo);
			try {
				dataId = Integer.valueOf(mDataId);
				if (mDataId.equals("0")) {
					// 新建表单
					mFlowId = dataId;
				}
				LogUtils.i("testKeno2", "===mFlowId=" + mFlowId);
			} catch (Exception e) {
				LogUtils.e(TAG, "" + e);
			}
			LogUtils.i("testKeno2", "===formId=" + mDataId);
			if (dataId != 0) {
				// 显示提示信息
				isFormSaved = true;
				msg.what = UPLOAD_DATA_SUCCEEDED;
			} else {
				isFormSaved = false;
				msg.what = UPLOAD_DATA_FAILED;
			}
			loadDataHandler.sendMessage(msg);
		} catch (Exception ex) {
			msg.what = UPLOAD_DATA_FAILED;
			loadDataHandler.sendMessage(msg);
		}
		return mDataId;
	}

	/**
	 * 显示上传图片模块
	 */
	private void showAddImage(List<FieldInfo> fieldInfos) {
		// 如果不是新建，则加载网络获取的初始化数据
		for (int i = 0; i < fieldInfos.size(); i++) {
			if ("附件".equals(fieldInfos.get(i).fieldName)) {
				if (!TextUtils.isEmpty(fieldInfos.get(i).fieldValue)) {
					attachId = fieldInfos.get(i).fieldValue;
					break;
				}
			}
		}

		if (TextUtils.isEmpty(attachId) && !isEdit) {
			// 如果附件为空,并且页面是不可编辑，隐藏图片控件
			ll_upload_photo.setVisibility(View.GONE);
		} else {
			ll_upload_photo.setVisibility(View.VISIBLE);
			// 设置AddImageView的显示数据和状态
			multipleAttachView.setIsAdd(isEdit);
			multipleAttachView.loadImageByAttachIds(attachId);
		}
	}

	/**
	 * 显示审批意见模块
	 */
	private void showAuditOpinion(List<流程过程表> resutList) {
		// 流程过程：包括审核意见，时间，领导签字
		LogUtils.i("createkjx", "size:" + resutList.size());
		ll_leader_signure.setVisibility(View.VISIBLE);
		for (int pos = 0; pos < resutList.size(); pos++) {
			流程过程表 item = resutList.get(pos);
			int count = pos + 1;
			String name = dictionaryHelper.getUserNameById(item.userId);
			TextView tvText = new TextView(CreateVmFormActivity.this);
			tvText.setTextColor(0xFF000000);
			String opinionNew = TextUtils.isEmpty(item.opinion) ? ""
					: item.opinion;
			tvText.setText(count + "." + name + "审核:" + item.result + ";\t "
					+ item.UpdateTime + ";\n 意见:" + opinionNew);
			ll_leader_signure.addView(tvText);

			// 如果签名不为空，显示签名图片
			if (!TextUtils.isEmpty(item.signure)) {
				ImageView iv_leader_signure = new ImageView(
						CreateVmFormActivity.this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						(int) ViewHelper.dip2px(CreateVmFormActivity.this, 200),
						(int) ViewHelper.dip2px(CreateVmFormActivity.this, 200));
				iv_leader_signure.setScaleType(ScaleType.CENTER_CROP);
				iv_leader_signure.setLayoutParams(params);
				ll_leader_signure.addView(iv_leader_signure);
				pictureUtils.showPicture(CreateVmFormActivity.this,
						item.signure, iv_leader_signure);
			}
		}

		// 初始化页面
		isInit = true;
		imageViewSave.setImageDrawable(getResources().getDrawable(
				R.drawable.memo_mini));
		// imageViewSave.setVisibility(View.GONE);
		buttonTag_createfrom_commit.setVisibility(View.GONE);
	}

	/**
	 * 显示填写审批意见模块，通过否决
	 */
	private void showAudit() {
		// 显示，并绑定监听事件
		ll_approval.setVisibility(View.VISIBLE);
		setOnAuditClickListener();
	}

	/**
	 * 显示填写审批意见模块，通过否决
	 */
	private void showScanCode() {
		// 显示，并绑定监听事件
		ll_scanDetails_creatform.setVisibility(View.VISIBLE);
	}

	/***
	 * 获取手写签名配置信息
	 */
	private void getSettingOfSignByHand() {
		final String url_signByHand = Global.BASE_URL
				+ "account/GetShallSignByHand";
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String json = httpUtils.httpGet(url_signByHand);
					String result = JsonUtils.pareseData(json);

					if (!TextUtils.isEmpty(result) && result.contains("true")) {
						isSignByHand = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 显示表单明细页面
	 */
	private void showDetailsForm() {
		mDetailsValueResult = mVmFormDef.DetailValues;
		// 查看表单明细按钮 显示，并绑定监听事件
		// ll_details_creatform.setVisibility(View.VISIBLE);
		// setOnDetailsClickLisener();
		ll_root_details_form.setVisibility(View.VISIBLE);
		// createDetailsForm(); //横向的明细表
		createVeticalDetailsForm(); // 纵向的明细表
		if (isEdit) {
			showAddTableRow();
		}
	}

	/***
	 * 查看明细表 监听事件绑定
	 */
	private void setOnDetailsClickLisener() {
		ll_details_creatform.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 打开表单明细
				Intent intent = new Intent(mContext,
						CreateVmDetailsFormActivity.class);
				Bundle bundle = new Bundle();
				bundle.putBoolean("isEdit", isEdit);
				bundle.putString("typeName", typeName);
				bundle.putSerializable(CreateVmDetailsFormActivity.TAG,
						mVmFormDef);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUEST_CODE_DETAILS);
			}
		});
	}

	/**************** 审核部分代码 ********************/
	/**
	 * 为审核“通过”、“否决”按钮绑定监听事件
	 */
	private void setOnAuditClickListener() {
		// 意见点击监听
		editText_approval_opinion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String opinion = editText_approval_opinion.getText().toString()
						.trim();
				Intent intent = new Intent(CreateVmFormActivity.this,
						Approval_Opinion.class);
				intent.putExtra(Approval_Opinion.FORMID, mFlowId);
				intent.putExtra(Approval_Opinion.OPINION, opinion);
				intent.putExtra(Approval_Opinion.TITLE, "审批意见");
				startActivityForResult(intent, APPROVAL_OPINION_CODE);
			}
		});

		// 意见焦点改变监听
		editText_approval_opinion
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							String opinion = editText_approval_opinion
									.getText().toString().trim();

							Intent intent = new Intent(
									CreateVmFormActivity.this,
									Approval_Opinion.class);
							intent.putExtra(Approval_Opinion.FORMID, mFlowId);
							intent.putExtra(Approval_Opinion.OPINION, opinion);
							intent.putExtra(Approval_Opinion.TITLE, "审批意见");
							startActivityForResult(intent,
									APPROVAL_OPINION_CODE);
						}
					}
				});
		// 审核通过按钮
		buttonTag_createfrom_approval.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isPass = true;
				opinion = editText_approval_opinion.getText().toString().trim();
				if (TextUtils.isEmpty(opinion)) {
					opinion = "无意见";
				}

				if (isSignByHand) {
					ShowDialogIsSignure(v);
				} else {
					// 如果审核通过时发现 有可编辑单元格，则先保存表单，在提交,状态记录为直接审核
					if (parseVmFormCreateUI.getmEditStuts() == ParseVmFormCreateUI.EditableByCurrentNodeEditableCells) {
						parseVmFormCreateUI
								.setmEditStuts(ParseVmFormCreateUI.EditableByCurrentNodeEditableCells_DirectAduit);
						saveFormInfo();
					} else {
						submitApproval();
					}
				}
			}
		});

		// 否决按钮监听
		buttonTag_createfrom_votedown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				isPass = false;
				ProgressDialogHelper.show(mContext);
				opinion = editText_approval_opinion.getText().toString().trim();
				if (TextUtils.isEmpty(opinion)) {
					opinion = "无意见";
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							mZLServiceHelper.submitApprovalNew(opinion, "",
									mFlowId, isPass, handler);
						} catch (Exception e) {
							Toast.makeText(CreateVmFormActivity.this, "否决异常",
									Toast.LENGTH_SHORT).show();
						}
					}
				}).start();
			}
		});
	}

	/**
	 * 显示添加明细表行按钮
	 */
	private void showAddTableRow() {
		ll_addDetails_creatform.setVisibility(View.VISIBLE);
		ll_addDetails_creatform.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LogUtils.i(TAG, "显示添加明细表行按钮");
				// 添加一行
				if (mDetailsValueResult != null
						&& mDetailsValueResult.size() > 0) {
					int size = mDetailsValueResult.get(0).size();
					List<String> list = new ArrayList<String>();
					for (int i = 0; i < size; i++) {
						list.add("");
					}
					mDetailsValueResult.add(list);
				}

				ll_root_create_details.removeAllViews();
				parseVmFormCreateUI = new ParseVmFormCreateUI(
						ll_root_create_details, CreateVmFormActivity.this,
						mContext, mVmFormDef, isEdit);
				// mDetailsValueResult = parseVmFormCreateUI.createDetailsUI();
				mDetailsValueResult = parseVmFormCreateUI
						.createVeticalDetailsUI();
			}
		});

		ll_scanDetails_creatform.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LogUtils.i(TAG, "扫描");
				Intent intent = new Intent(mContext, CaptureActivity.class);
				startActivityForResult(intent, CODE_REQUEST_SCAN);
			}
		});

		// 默认不显示原有删除行按钮
		// ll_deleteDetails_creatform.setVisibility(View.GONE);
		ll_deleteDetails_creatform.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LogUtils.i(TAG, "显示删除明细表行按钮");
				// 删除最末一行
				if (mDetailsValueResult != null
						&& mDetailsValueResult.size() > 1) {
					int size = mDetailsValueResult.get(0).size();
					List<String> list = new ArrayList<String>();
					mDetailsValueResult.remove(mDetailsValueResult.size() - 1);

					ll_root_create_details.removeAllViews();
					parseVmFormCreateUI = new ParseVmFormCreateUI(
							ll_root_create_details, CreateVmFormActivity.this,
							mContext, mVmFormDef, isEdit);
					// mDetailsValueResult =
					// parseVmFormCreateUI.createDetailsUI();
					mDetailsValueResult = parseVmFormCreateUI.createDetailsUI();
				} else {
					Toast.makeText(mContext, "明细表不能全部删空，至少保留一条记录",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && isEdit) {
			mDictIosPicker.show("未保存,离开当前页面");
			mDictIosPicker.setOnSelectedListener(new OnSelectedListener() {
				@Override
				public void onSelected(int index) {
					if (index == 0) {
						finish();
					}
				}
			});
			return false; // 禁用回退键
		}
		return super.onKeyDown(keyCode, event);
	}

	private PopupWindow popupWindow; // 领导签名窗体
	private boolean isClosed;// 签名是否已关闭
	private String signurePath;// 签名图片路径
	private String attachId; // 签名上传图片附件号
	private final int defaultPaintWeight = 6; // 默认画笔粗细
	private ImageView btnResume;
	private ImageView ivSave;
	private ImageView iv_canvas;
	private Bitmap baseBitmap;
	private Canvas canvas;
	private Paint paint;

	/**
	 * 显示签名框
	 */
	private void showSignureWindow() {
		View popupView = getLayoutInflater().inflate(R.layout.dialog_signure,
				null);
		View v = popupView.findViewById(R.id.view_top);

		int mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
		Rect rect = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		int statusBarHeight = rect.top; // 状态栏高度
		popupWindow = new PopupWindow(popupView, mScreenWidth, mScreenHeight
				- statusBarHeight);
		// popupWindow.setWidth(mScreenWidth);
		// popupWindow.setHeight(mScreenHeight);
		// 点击空白处 对话框消失
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
				(Bitmap) null));
		initPopupWindow(popupView);

		popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				isClosed = true;
			}
		});
		// int[] location = new int[2];
		// v.getLocationOnScreen(location);
		isClosed = false;
		popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
		// popupWindow.showAsDropDown(v);
	}

	private void initPopupWindow(View view) {
		// 初始化一个画笔，笔触宽度为5
		paint = new Paint();
		paint.setStrokeWidth(defaultPaintWeight);
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);// 画笔加上抗锯齿效果
		iv_canvas = (ImageView) view.findViewById(R.id.iv_canvas);
		iv_canvas.setOnTouchListener(touch);
		btnResume = (ImageView) view.findViewById(R.id.iv_clear_signure);
		ivSave = (ImageView) view.findViewById(R.id.iv_save_signure);
		btnResume.setOnClickListener(clickListener);
		ivSave.setOnClickListener(clickListener);

		/** 摇一摇清空画板 */
		ShakeListenerUtils shakeListenerUtils = new ShakeListenerUtils(mContext);
		shakeListenerUtils.setOnShakeListener(new OnShakeListener() {
			@Override
			public void onShake() {
				if (!isClosed) {
					resumeCanvas();
				}
			}
		});
	}

	private View.OnTouchListener touch = new OnTouchListener() {
		// 定义手指开始触摸的坐标
		float startX;
		float startY;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
				// 用户按下动作
				case MotionEvent.ACTION_DOWN:
					// 第一次绘图初始化内存图片，指定背景为白色
					if (baseBitmap == null) {
						baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(),
								iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
						canvas = new Canvas(baseBitmap);
						// // 画布上设置抗锯齿属性，效果比paint加锯齿效果更好
						// canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
						// Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
						canvas.drawColor(Color.WHITE);
					}
					// 记录开始触摸的点的坐标
					startX = event.getX();
					startY = event.getY();
					break;
				// 用户手指在屏幕上移动的动作
				case MotionEvent.ACTION_MOVE:
					// 记录移动位置的点的坐标
					float stopX = event.getX();
					float stopY = event.getY();
					// 根据两点坐标，绘制连线
					canvas.drawLine(startX, startY, stopX, stopY, paint);
					if (!isSigned) {
						isSigned = true;
					}
					// 绘制点
					// canvas.drawPoint(stopX, stopY, paint);
					// 更新开始点的位置
					startX = event.getX();
					startY = event.getY();
					// 把图片展示到ImageView中
					iv_canvas.setImageBitmap(baseBitmap);
					break;
				case MotionEvent.ACTION_UP:

					break;
				default:
					break;
			}
			return true;
		}
	};

	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.iv_clear_signure:
					// 擦除画板
					resumeCanvas();
					// 关闭签名
					popupWindow.dismiss();
					break;
				case R.id.iv_save_signure:
					if (isSigned) {
						saveBitmap();
					} else {
						Toast.makeText(mContext, "还没在画板上签名哦", Toast.LENGTH_SHORT)
								.show();
					}
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 清除画板
	 */
	protected void resumeCanvas() {
		isSigned = false;
		signurePath = ""; // 清空图片原有路径
		// 手动清除画板的绘图，重新创建一个画板
		if (baseBitmap != null) {
			baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(),
					iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
			canvas = new Canvas(baseBitmap);
			canvas.drawColor(Color.WHITE);
			iv_canvas.setImageBitmap(baseBitmap);
		}
	}

	/**
	 * 保存图片到SD卡上
	 */
	protected void saveBitmap() {
		try {
			// 保存图片到SD卡上
			File file = new File(Environment.getExternalStorageDirectory(),
					System.currentTimeMillis() + ".png");
			FileOutputStream stream = new FileOutputStream(file);
			final boolean isSave = baseBitmap.compress(CompressFormat.PNG, 100,
					stream);
			Toast.makeText(CreateVmFormActivity.this, "保存签名成功", 0).show();
			// Android设备Gallery应用只会在启动的时候扫描系统文件夹
			// 这里模拟一个媒体装载的广播，用于使保存的图片可以在Gallery中查看
			// Intent intent = new Intent();
			// intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
			// intent.setData(Uri.fromFile(Environment
			// .getExternalStorageDirectory()));
			// sendBroadcast(intent);
			signurePath = file.getAbsolutePath();
			popupWindow.dismiss(); // 保存签名成功并关闭画板,提交
			// findViewById(R.id.ll_upload_signure).setVisibility(View.GONE);
			LogUtils.i("testkenoAudit", "isSave=" + isSave);
			if (isSave) {
				attachId = mZLServiceHelper
						.uploadAttachPhoto(signurePath, null);
				LogUtils.i("testkenoAudit", "attachId=" + attachId);
			}

			ProgressDialogHelper.show(mContext);
			new Thread(new Runnable() {
				public void run() {
					try {
						LogUtils.e("testkenoAudit", "attachId2=" + attachId);
						mZLServiceHelper.submitApprovalNew(opinion, attachId,
								mFlowId, isPass, handler);
					} catch (Exception e) {
						LogUtils.e(TAG, "审核异常：" + e.getMessage());
					}
				}
			}).start();
		} catch (Exception e) {
			Toast.makeText(CreateVmFormActivity.this, "保存图片失败", 0).show();
			LogUtils.e("savePng", "savePng" + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * 是否使用签名
	 */
	private void ShowDialogIsSignure(final View view) {
		AlertDialog.Builder builderWrite = new AlertDialog.Builder(
				CreateVmFormActivity.this);
		builderWrite.setMessage("是否手写签字").setCancelable(false)
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// TODO 手写签字
						showSignureWindow();
						dialog.cancel();
					}
				})
				.setNegativeButton("否", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						submitApproval();
						dialog.cancel();
					}
				});
		builderWrite.create().show();
	}

	/***
	 * 提交审核意见
	 */
	private void submitApproval() {
		ProgressDialogHelper.show(mContext);
		new Thread(new Runnable() {
			public void run() {
				try {
					// 不上传电子签名附件l
					mZLServiceHelper.submitApprovalNew(opinion, "", mFlowId,
							isPass, handler);
				} catch (Exception e) {
					LogUtils.e(TAG, "审核异常：" + e.getMessage());
				}
			}
		}).start();
	}

	/***
	 * 根据产品条码号获取 产品信息
	 *
	 * @param code
	 */
	private void getProductByCode(String code) {
		final Demand demand = new Demand();
		demand.表名 = "";
		demand.方法名 = "SaleStore/getProductSimpleList";
		demand.条件 = "条码 like '%" + code + "%'";
		final String url = Global.BASE_URL + demand.方法名;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String json = httpUtils.postSubmit(url,
							JsonUtils.initJsonObj(demand));
					LogUtils.i(TAG, json);
					List<产品型号> list = JsonUtils.ConvertJsonToList(json,
							产品型号.class);
					Message msg = handler.obtainMessage();
					if (list != null && list.size() > 0) {
						LogUtils.i(TAG, list.get(0).名称 + "" + list.get(0).编号);
						msg.obj = list.get(0);
						msg.what = SUCCEED_ADD_PRODUCT_DETAIL;
					} else {
						LogUtils.i(TAG, list.get(0).名称 + "" + list.get(0).编号);
					}
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(FAILURE_ADD_PRODUCT_DETAIL);
				}
			}
		}).start();
	}

	/***
	 * 添加一行产品明细
	 *
	 * @param product
	 */
	private void addProductDetails(产品型号 product) {
		// 明细表字段的定义
		List<FieldInfo> detailsFieldList = mVmFormDef.DetailFields;

		int rowIndex = -1; // 记录已存在产品的行号
		int coutnIndex = -1; // 记录数量 所在列编号
		int totalIndex = -1;
		for (int i = 0; i < detailsFieldList.size(); i++) {
			FieldInfo detailsFieldInfo = detailsFieldList.get(i);
			if ("产品".equals(detailsFieldInfo.fieldName)) {
				if (mDetailsValueResult != null
						&& mDetailsValueResult.size() > 0) {
					for (int j = 0; j < mDetailsValueResult.size(); j++) {
						// 添加新产品时如果产品编号为空，则移除该行
						String productIdStr = mDetailsValueResult.get(j).get(i);
						if (TextUtils.isEmpty(productIdStr)) {
							mDetailsValueResult.remove(j);
						} else if ((product.编号 + "").equals(productIdStr)) {
							// 如果改产品已存在则数量加+1
							rowIndex = j;
						}
					}
				}
			} else if ("数量".equals(detailsFieldInfo.fieldName)) {
				coutnIndex = i;
			} else if ("小计".equals(detailsFieldInfo.fieldName)) {
				totalIndex = i;
			}
		}

		if (rowIndex != -1) {
			// 该产品已存在，则添加数量
			int count = 1;
			String countStr = mDetailsValueResult.get(rowIndex).get(coutnIndex);
			try {
				count = Integer.parseInt(countStr) + 1;
			} catch (Exception e) {
				count = 1;
			}
			double total = product.单价 * count;
			mDetailsValueResult.get(rowIndex).set(coutnIndex, count + "");
			mDetailsValueResult.get(rowIndex).set(totalIndex, total + "");
		} else if (mDetailsValueResult != null) {
			// && mDetailsValueResult.size() > 0
			// // 添加一行
			// int size = mDetailsValueResult.get(0).size();
			int fieldSize = detailsFieldList.size();
			// if (size == fieldSize) {
			try {
				Class c = Class.forName("com.zlcloud.models.产品型号");
				List<String> list = new ArrayList<String>();
				for (int i = 0; i < fieldSize; i++) {
					List<Field> fields = Arrays.asList(c.getFields());
					String fieldName = detailsFieldList.get(i).fieldName;
					fieldName = fieldName.equals("产品") ? "编号" : fieldName;
					boolean isExistFiled = false;
					for (int x = 0; x < fields.size(); x++) {
						if (fields.get(x).getName().contains(fieldName)) {
							// 明细表包含
							isExistFiled = true;
						}
					}
					if ("数量".equals(fieldName)) {
						list.add("1");
					} else if ("单价".equals(fieldName)) {
						list.add(product.单价 + "");
					} else if ("小计".equals(fieldName)) {
						list.add(product.单价 + "");
					} else if (isExistFiled) {
						// 当前field是 private
						Field field = c.getDeclaredField(fieldName);
						if (field != null) {
							// 设置private变量可以访问的
							field.setAccessible(true);
							Object value = field.get(product) + "";
							list.add(value.toString());
						} else {
							list.add("");
						}
					} else {
						list.add("");
					}
				}
				mDetailsValueResult.add(list);
			} catch (Exception e) {
				e.printStackTrace();
				LogUtils.e(TAG, e.getMessage() + "");
			}
		}
		// }
		ll_root_create_details.removeAllViews();
		parseVmFormCreateUI = new ParseVmFormCreateUI(ll_root_create_details,
				CreateVmFormActivity.this, mContext, mVmFormDef, isEdit);
		mDetailsValueResult = parseVmFormCreateUI.createVeticalDetailsUI();
		// 计算合计等公式绑定
		parseVmFormCreateUI.setDetailExpression(mEditList, mDetailsValueResult);
	}

	/***
	 * 初始化其他页面带有默认值
	 */
	private void initDefaultValue() {
		if (mDefalutValueMap != null && mVmFormDef != null) {
			if (mVmFormDef.Dictionaries == null) {
				// 构建空字典
				mVmFormDef.Dictionaries = new HashMap<String, HashMap<Integer, String>>();
			}

			Iterator<Entry<String, Object>> iterator = mDefalutValueMap
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				String key = entry.getKey(); //
				Object value = entry.getValue();

				for (FieldInfo fInfo : mVmFormDef.Fields) {
					if (key.equals(fInfo.fieldName)) {
						if (value instanceof String) {
							fInfo.fieldValue = value.toString();
						} else if (value instanceof Dict) {
							// 把携带的 key-value 构建成字典存入mVmFormDef字典对象中
							Dict dict = (Dict) value;
							HashMap<Integer, String> dictMap = mVmFormDef.Dictionaries
									.get(key);
							if (dictMap == null) {
								dictMap = new HashMap<Integer, String>();
							}
							dictMap.put(dict.编号, dict.名称);
							LogUtils.i(TAG, key + ":" + dict.编号 + "--"
									+ dict.名称);
							mVmFormDef.Dictionaries.put(key, dictMap);

							fInfo.fieldValue = dict.编号 + "";
						} else if (value instanceof 长汇客户) {
							// 携带客户信息自动填充
							mClientInfo = (长汇客户) value;

							Dict dict = new Dict(mClientInfo.编号, mClientInfo.名称);
							HashMap<Integer, String> dictMap = mVmFormDef.Dictionaries
									.get(key);
							if (dictMap == null) {
								dictMap = new HashMap<Integer, String>();
							}
							dictMap.put(dict.编号, dict.名称);
							LogUtils.i(TAG, key + ":" + dict.编号 + "--"
									+ dict.名称);
							mVmFormDef.Dictionaries.put(key, dictMap);

							fInfo.fieldValue = dict.编号 + "";
						}
					}
				}

				if (value instanceof 长汇客户) {
					// 携带客户信息自动填充
					mClientInfo = (长汇客户) value;
					Class<长汇客户> classType = 长汇客户.class;
					for (FieldInfo fInfo : mVmFormDef.Fields) {
						try {
							Field field = classType
									.getDeclaredField(fInfo.fieldName);
							Object objValue = field.get(mClientInfo);
							if (objValue != null && objValue instanceof String) {
								fInfo.fieldValue = (String) objValue;
							}
						} catch (NoSuchFieldException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}

			}
		}
	}

	private int uploadCount = 0;

	private void uploadMulipleFile() {
		List<MultipleAttachView> multipleAttachViews = parseVmFormCreateUI
				.getMultipleAttachViews();
		if (multipleAttachViews == null || multipleAttachViews.size() == 0) {
			saveOrSubmit();
			return;
		}

		final int size = multipleAttachViews.size();
		ProgressDialogHelper.show(mContext, "上传中");
		for (MultipleAttachView attachView : multipleAttachViews) {
			final FieldInfo fieldInfo = (FieldInfo) attachView.getTag();
			attachView.uploadImage(new IOnUploadMultipleFileListener() {
				@Override
				public void onStartUpload(int sum) {
					LogUtils.i("upload_sum", fieldInfo.fieldName + "--sum="
							+ sum);
				}

				@Override
				public void onProgressUpdate(int completeCount) {
					LogUtils.i("upload_progress", "progress=" + completeCount);
				}

				@Override
				public void onComplete(String attachIds) {
					uploadCount++;
					LogUtils.i("upload_com", uploadCount + "\tonComplete:"
							+ attachIds);
					fieldInfo.fieldValue = attachIds;
					if (uploadCount == size) {
						LogUtils.i("upload_All", "所有文件上传完毕");
						saveOrSubmit();
					}
				}
			});
		}
	}

	private void saveOrSubmit() {
		if (isFormSaved) {
			// 提交
			submitFormInfo();
		} else {
			// 保存
			saveFormInfo();
		}
	}
}