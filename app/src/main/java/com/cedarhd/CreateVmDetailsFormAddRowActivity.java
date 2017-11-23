package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.ParseVmFormCreateUI;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.FieldInfo;
import com.cedarhd.models.VmFormDef;
import com.cedarhd.models.产品型号;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/***
 * 生成明细表中的一行数据[编辑/查看]
 * 
 * @author KJX 2015-03-14
 */
public class CreateVmDetailsFormAddRowActivity extends BaseActivity {
	public static String TAG = "CreateVmDetailsFormAddRowActivity";

	private Context mContext;

	/** 流程表单实体，如果有数据的话，还包含相关的值[用于生成UI控件] */
	private VmFormDef mVmFormDef;

	/** 明细表一行的数据值 集合 */
	private ArrayList<String> mRowValue;

	/** 在上一个页面List中的位置 */
	private int listAtPos;

	private ParseVmFormCreateUI parseVmFormCreateUI;
	private ImageView iv_cancel;
	private ImageView iv_save;
	private LinearLayout ll_root;

	/** 页面动态生成文本框控件，Tag中包含一个FieldInfo */
	private List<EditText> mEditList;

	/** 标记动态生成EditText控件是否可编辑 **/
	boolean isEdit = true; //
	/** 只能接受数字的字段 列名 */
	String mustBeIntFieldName = "";//

	/** 内容为空的字段名 */
	String nullContentFieldName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_vm_details_form_add_row);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		mContext = CreateVmDetailsFormAddRowActivity.this;
		findViews();
		setOnClickListener();
		try {
			LogUtils.i("oncreate", "");
			Intent intent = getIntent();
			Bundle bundle = intent.getExtras();
			mVmFormDef = (VmFormDef) bundle.getSerializable(TAG);
			isEdit = bundle.getBoolean("isEdit");
			listAtPos = bundle.getInt("listAtPos");
			mRowValue = bundle.getStringArrayList("rowValueList");
			if (mVmFormDef != null) {
				createForm();
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "进入浏览器模式:\n" + e);
		}
	}

	public void findViews() {
		// photoSerialNo = UUID.randomUUID().toString();
		iv_cancel = (ImageView) findViewById(R.id.iv_cancel_add_row_details);
		iv_save = (ImageView) findViewById(R.id.iv_save_add_row_details);
		ll_root = (LinearLayout) findViewById(R.id.ll_root_add_row_details);
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
				if (isEmpty()) {
					Toast.makeText(mContext, nullContentFieldName + "不能为空",
							Toast.LENGTH_SHORT).show();
				} else {
					if (!isParse()) { // 如果数字校验不通过
						Toast.makeText(mContext,
								mustBeIntFieldName + "必须为数字类型",
								Toast.LENGTH_SHORT).show();
					} else {
						mRowValue = getDetailsValue();
						for (int i = 0; i < mRowValue.size(); i++) {
							LogUtils.i(TAG, "---->" + mRowValue.get(i));
						}
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putInt("listAtPos", listAtPos);
						bundle.putStringArrayList("rowValueList", mRowValue);
						intent.putExtras(bundle);
						setResult(RESULT_OK, intent);
						finish();
					}
				}
			}
		});
	}

	/**
	 * 动态生成表单
	 */
	private void createForm() {
		parseVmFormCreateUI = new ParseVmFormCreateUI(ll_root,
				CreateVmDetailsFormAddRowActivity.this, mContext, mVmFormDef,
				isEdit);
		mEditList = parseVmFormCreateUI.createDetailsAddRowUI(mRowValue,
				listAtPos + 1);
		parseVmFormCreateUI.setExpression();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == CreateVmFormActivity.SELECT_PRODUCT_CODE) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					产品型号 product = (产品型号) bundle
							.getSerializable(CreateVmFormActivity.PRODUCT_SELECTED);
					Toast.makeText(mContext, "选择产品:" + product.名称,
							Toast.LENGTH_SHORT).show();

					parseVmFormCreateUI.setProductControl(mEditList,
							product.名称, product.编号 + "");
				}
			}
		}
	}

	/**
	 * 判断字段是否为空
	 * 
	 * @return 如果为空则返回
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
				} else {// 判断文本项
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
			String dataType = fieldInfo.dataType;
			String fieldValue = fieldInfo.fieldValue;
			String dict = fieldInfo.fieldDict;

			if ("int".equalsIgnoreCase(dataType)) {
				String content = eText.getText().toString().trim();
				if (!TextUtils.isEmpty(dict)) {
					content = fieldValue;
				}
				try {
					content = TextUtils.isEmpty(content) ? "0" : content;
					Integer.parseInt(content);
					fieldInfo.fieldValue = content;
				} catch (Exception e) {
					mustBeIntFieldName = fieldName;
					return false;
				}
			}
			if ("double".equalsIgnoreCase(dataType)) {
				String content = eText.getText().toString().trim();
				if (!TextUtils.isEmpty(dict)) {
					content = fieldValue;
				}
				try {
					content = TextUtils.isEmpty(content) ? "0" : content;
					Double.parseDouble(content);
					fieldInfo.fieldValue = content;
				} catch (Exception e) {
					mustBeIntFieldName = fieldName;
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 获取编辑后明细表的值
	 * 
	 * @return
	 */
	private ArrayList<String> getDetailsValue() {
		mRowValue.clear();
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < mEditList.size(); i++) {
			FieldInfo fInfo = (FieldInfo) mEditList.get(i).getTag();
			if (TextUtils.isEmpty(fInfo.fieldDict)
					&& !fInfo.fieldStyle.equalsIgnoreCase("combobox")
					&& !fInfo.fieldStyle.equalsIgnoreCase("dropdownlist")
					&& !fInfo.fieldStyle.equalsIgnoreCase("product")
					&& !fInfo.fieldStyle.equalsIgnoreCase("checkbox")) {
				if (!TextUtils.isEmpty(mEditList.get(i).getText().toString())) {
					// 可编辑方式需要取文本框当前显示内容
					list.add(mEditList.get(i).getText().toString());
				} else {
					list.add(fInfo.fieldValue);
				}
			} else {
				list.add(fInfo.fieldValue);
			}
		}
		return list;
	}
}
