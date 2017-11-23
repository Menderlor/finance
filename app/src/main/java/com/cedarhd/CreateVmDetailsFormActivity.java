package com.cedarhd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.ParseVmFormCreateUI;
import com.cedarhd.models.VmFormDef;
import com.cedarhd.models.流程;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态生成明细表单
 *
 * @author KJX 2015/03/13 10:35
 */
@SuppressLint("NewApi")
public class CreateVmDetailsFormActivity extends BaseActivity {
	public static final String TAG = "CreateVmDetailsFormActivity";

	/** 保存表单明细 请求码 */
	public static final int REQUEST_CODE_UPDATE_DETAILS = 21;

	/** 流程表单实体，如果有数据的话，还包含相关的值[用于生成UI控件] */
	private VmFormDef mVmFormDef;

	/** 页面动态生成文本框控件，Tag中包含一个FieldInfo */
	private List<EditText> mEditList;

	private Context context;
	private ImageView iv_cancel;
	private ImageView iv_save;
	private LinearLayout ll_root_create_details;
	private TextView tvTitle;
	/** 添加表单明细按钮表单明细 */
	private LinearLayout ll_addDetails_creatform;

	/** 删除表单明细按钮 */
	private LinearLayout ll_deleteDetails_creatform;

	private ParseVmFormCreateUI parseVmFormCreateUI;

	/** 表单明细value */
	private List<List<String>> mDetailsValueResult;

	/** 服务器的表单编号,每个item的表单编号 */
	int typeId; // 表的类型id号

	/** 表单号,0表示新建 */
	String dataId = "0";

	/** 服务器返回的表单编号，用于提交表单 */
	int mFormId = 0;

	/** 流程表名 */
	String typeName;//

	/** 只能接受数字的字段 列名 */
	String mustBeIntFieldName = "";//

	/** 内容为空的字段名 */
	String nullContentFieldName = "";

	/** 判断是否保存，true代表已保存，false代表未保存 */
	boolean isFormSaved = false;

	boolean isInit = false;// 判断是否初始化数据了
	流程 mFlow;
	private String opinion; // 审核意见

	boolean isAudit = false;// 是否审核

	/** 保存后是否提交(周工作总结不提交),默認提交 */
	boolean isNotSubmit = false;

	/** 标记动态生成EditText控件是否可编辑 **/
	boolean isEdit = true; //

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_details_form);
		context = CreateVmDetailsFormActivity.this;
		findViews();
		setOnClickListener();
		try {
			LogUtils.i("oncreate", "");
			Intent intent = getIntent();
			Bundle bundle = intent.getExtras();

			mVmFormDef = (VmFormDef) bundle.getSerializable(TAG);
			typeName = bundle.getString("typeName", "表单");
			isEdit = bundle.getBoolean("isEdit");
			if (mVmFormDef != null) {
				createForm();
			}

			if (isEdit) {
				showAddTableRow();
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "进入浏览器模式:\n" + e);
		}
	}

	/**
	 * 动态生成表单
	 */
	private void createForm() {
		tvTitle.setText(typeName + "明细");
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
				CreateVmDetailsFormActivity.this, context, mVmFormDef, isEdit);
		mDetailsValueResult = parseVmFormCreateUI.createDetailsUI();
	}

	@SuppressLint("NewApi")
	public void findViews() {
		// photoSerialNo = UUID.randomUUID().toString();
		iv_cancel = (ImageView) findViewById(R.id.iv_cancel_create_details);
		iv_save = (ImageView) findViewById(R.id.iv_save_create_details);
		tvTitle = (TextView) findViewById(R.id.tv_title_create_details);
		ll_root_create_details = (LinearLayout) findViewById(R.id.ll_root_create_details);
		ll_addDetails_creatform = (LinearLayout) findViewById(R.id.ll_addDetails_creat_details);
		ll_deleteDetails_creatform = (LinearLayout) findViewById(R.id.ll_deleteDetails_creat_details);
	}

	public void setOnClickListener() {
		iv_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 提交和保存按钮监听
		iv_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//
				// if (isEmpty()) {
				// Toast.makeText(context, nullContentFieldName + "不能为空",
				// Toast.LENGTH_SHORT).show();
				// } else {
				// if (!isParse()) { // 如果数字校验不通过
				// Toast.makeText(context, mustBeIntFieldName + "必须为数字类型",
				// Toast.LENGTH_SHORT).show();
				// } else {
				//
				// // List转为ArrayList
				// ArrayList<ArrayList<String>> detailsValues = new
				// ArrayList<ArrayList<String>>();
				// for (int i = 0; i < mDetailsValueResult.size(); i++) {
				// List<String> list = mDetailsValueResult.get(i);
				// ArrayList<String> arrayList = new ArrayList<String>();
				// for (int j = 0; j < list.size(); j++) {
				// arrayList.add(list.get(j));
				// }
				// detailsValues.add(arrayList);
				// }
				// }
				// }

				/** 提交项空校验 */
				boolean isResult = true;
				if (mDetailsValueResult != null) {
					for (int i = 0; i < mDetailsValueResult.size(); i++) {
						List<String> item = mDetailsValueResult.get(i);
						for (int j = 0; j < item.size(); j++) {
							if (TextUtils.isEmpty(item.get(j))) {
								isResult = false;
							}
						}
					}
				}

				if (isResult) {
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					mVmFormDef.DetailValues = mDetailsValueResult;
					bundle.putSerializable("mVmFormDef_Details", mVmFormDef);
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					Toast.makeText(context, "明细项不允许为空,请检查表单填写是否完整！",
							Toast.LENGTH_SHORT).show();
				}
			}

		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_UPDATE_DETAILS) {
				LogUtils.i(TAG, "onActivity--->REQUEST_CODE_UPDATE_DETAILS");
				Bundle bundle = data.getExtras();
				int listAtPos = bundle.getInt("listAtPos");
				List<String> rowValueList = bundle
						.getStringArrayList("rowValueList");

				for (int i = 0; i < mVmFormDef.DetailValues.get(listAtPos)
						.size(); i++) {
					LogUtils.d(TAG,
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
						ll_root_create_details,
						CreateVmDetailsFormActivity.this, context, mVmFormDef,
						isEdit);
				mDetailsValueResult = parseVmFormCreateUI.createDetailsUI();
			}
		}
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
						ll_root_create_details,
						CreateVmDetailsFormActivity.this, context, mVmFormDef,
						isEdit);
				mDetailsValueResult = parseVmFormCreateUI.createDetailsUI();
			}
		});

		ll_deleteDetails_creatform.setVisibility(View.VISIBLE);
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
							ll_root_create_details,
							CreateVmDetailsFormActivity.this, context,
							mVmFormDef, isEdit);
					mDetailsValueResult = parseVmFormCreateUI.createDetailsUI();
				} else {
					Toast.makeText(context, "明细表不能全部删空，至少保留一条记录",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

}