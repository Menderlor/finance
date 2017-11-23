package com.cedarhd.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.ClientListActivity;
import com.cedarhd.CreateVmDetailsFormActivity;
import com.cedarhd.CreateVmDetailsFormAddRowActivity;
import com.cedarhd.CreateVmFormActivity;
import com.cedarhd.ProductSelectListActivity;
import com.cedarhd.R;
import com.cedarhd.User_SelectActivityNew;
import com.cedarhd.User_SelectActivityNew_zmy;
import com.cedarhd.control.MultipleAttachView;
import com.cedarhd.control.MultipleAttachView.OnAddImageListener;
import com.cedarhd.helpers.DictionaryQueryDialogHelper.OnSelectedListener;
import com.cedarhd.models.FieldInfo;
import com.cedarhd.models.VmFormDef;
import com.cedarhd.models.字典;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MoneyUtils;
import com.cedarhd.utils.RegexUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import parsii.eval.Expression;
import parsii.eval.Parser;

/**
 * 解析XML文件生产表单界面
 *
 * 2015/03/10
 */
@SuppressLint("ResourceAsColor")
public class ParseVmFormCreateUI {
	private static final String TAG = "ParseVmFormCreateUI";
	/** 可编辑状态：有可编辑单元格 */
	public static final int EditableByCurrentNodeEditableCells = 101;

	/** 可编辑状态：有可编辑单元格 ,并且已经保存过 */
	public static final int EditableByCurrentNodeEditableCells_Saved = 102;

	/** 可编辑状态：有可编辑单元格 ,直接审核 */
	public static final int EditableByCurrentNodeEditableCells_DirectAduit = 103;

	/** 默认可编辑 */
	public static final int EditableByDefault = 104;

	private int mEditStuts;

	private List<EditText> mEditList = new ArrayList<EditText>();

	/***
	 * 多图片上传控件 集合
	 */
	private List<MultipleAttachView> mAttachViews = new ArrayList<MultipleAttachView>();

	private VmFormDef mVmFormDef;

	/** 字典项集合 */
	private HashMap<String, HashMap<Integer, String>> mDictionaries;

	/** 是否选择 */
	private final String[] checkStrs = { "否", "是" };

	/** 主表字段定义，如果有数据的话，还包含相关的值[用于生成UI控件] */
	private List<FieldInfo> mFields;

	/** 明细表字段定义，用于表头的显示 */
	private List<FieldInfo> mDetailsFields;

	/** 明细表值定义，用于表内容行的显示 */
	private List<List<String>> mDetailValues;

	private Activity mActivity;
	private LinearLayout mRoot;
	private Context mContext;

	/** 是否可编辑 */
	private boolean isEdit;
	private DateAndTimePicker dateAndTimePicker;
	private DictIosPickerBottomDialog mDictIosPicker;
	private DictionaryQueryDialogHelper dictionaryQueryDialogHelper;
	private String mFormName; // 表名
	private String mFromRealName;// 表单名称
	private DictionaryHelper dictionaryHelper;

	private int mWindowHeightPixels;
	private int mWindowWidthPixels;

	/***
	 * 记录选择员工的字段名称
	 */
	public static String mUserFieldName = "";

	/***
	 * 记录选择附件的字段名称
	 */
	public static String mMultipleAttachFieldName = "";

	/**
	 * 通过流文件生成
	 *
	 * @param root
	 *            布局的根节点
	 * @param context
	 *            上下文对象
	 * @param vmFormDef
	 *            表单实体
	 * @param isEdit
	 *            是否可编辑，默认为true
	 */
	public ParseVmFormCreateUI(LinearLayout root, Activity activity,
							   Context context, VmFormDef vmFormDef, boolean isEdit) {
		this.mRoot = root;
		this.mActivity = activity;
		this.mContext = context;
		this.mVmFormDef = vmFormDef;
		this.isEdit = isEdit;
		dateAndTimePicker = new DateAndTimePicker(context);
		dictionaryHelper = new DictionaryHelper(context);
		mDictIosPicker = new DictIosPickerBottomDialog(context);
		dictionaryQueryDialogHelper = DictionaryQueryDialogHelper
				.getInstance(context);
		mWindowHeightPixels = mContext.getResources().getDisplayMetrics().heightPixels;
		mWindowWidthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
		parserStream(vmFormDef);
	}

	/**
	 * 解析Xml的流文件
	 */
	public void parserStream(VmFormDef vmFormDef) {

		mDictionaries = vmFormDef.Dictionaries;
		mFields = vmFormDef.Fields;
		mDetailsFields = vmFormDef.DetailFields;
		mDetailValues = vmFormDef.DetailValues;
	}

	/**
	 * 生成主表相应的UI界面,并返回FieldInfo对象的字段名,字段值(字段值是EditText类型),字段类型的集合
	 */
	public List<EditText> createUI() {
		// 将dp转化为px
		final float scale = mContext.getResources().getDisplayMetrics().density;
		int width = (int) (100 * scale + 0.5f);
		int height = (int) (50 * scale + 0.5f);
		int leftPadding = (int) (10 * scale + 0.5f);
		mRoot.setVisibility(View.VISIBLE);
		if (mFields == null || mFields.size() < 0) {
			return mEditList;
		}
		// 生成相应的UI界面
		for (int i = 0; i < mFields.size(); i++) {
			String fieldStyle = mFields.get(i).fieldStyle;
			String fieldName = mFields.get(i).fieldName;
			if (!TextUtils.isEmpty(fieldStyle) && !TextUtils.isEmpty(fieldName)
					&& !fieldName.contains("附件")) {
				String dict = mFields.get(i).fieldDict; // 绑定字典项
				LogUtils.d(TAG, "Dict=" + dict);
				FieldInfo fieldInfo = mFields.get(i);
				LinearLayout linearLayout = new LinearLayout(mContext);
				linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				linearLayout.setLayoutParams(params);
				linearLayout.setGravity(Gravity.CENTER_VERTICAL);

				// 添加左边提示文字
				addTextView(width, height, leftPadding, fieldInfo.fieldName,
						linearLayout);

				if ("textbox".equalsIgnoreCase(fieldStyle)
						&& TextUtils.isEmpty(dict)) {
					addEditTextView(fieldInfo, linearLayout, params);
				} else if ("combobox".equalsIgnoreCase(fieldStyle)
						|| "dropdownlist".equalsIgnoreCase(fieldStyle)) {
					addComboxView(fieldInfo, linearLayout, params);
				} else if ("datepicker".equalsIgnoreCase(fieldStyle)) {
					addDateTimeEditView(fieldInfo, linearLayout, params);
				} else if ("checkbox".equalsIgnoreCase(fieldStyle)) {
					addCheckedBox(fieldInfo, linearLayout, params);
				} else if ("image".equalsIgnoreCase(fieldStyle)) {
					addMultiImageView(fieldInfo, linearLayout, params);
				} else {
					addOtherEditView(fieldInfo, linearLayout, params);
				}
				mRoot.addView(linearLayout);
				// 加分割线
				if (i != mFields.size() - 1) {
					addHorionzalLine();
				}
			}
		}
		return mEditList;
	}

	/***
	 * 绑定公式计算
	 *
	 * @param detailEditTexts
	 *            明细表集合
	 */
	public void setExpression() {
		if (mEditList != null && mEditList.size() > 0) {
			for (int i = 0; i < mEditList.size(); i++) {
				// 遍历带有公式的EditText
				final EditText etExpression = mEditList.get(i);
				FieldInfo fieldInfoExpression = (FieldInfo) etExpression
						.getTag();
				String expression = fieldInfoExpression.expression;
				if (!TextUtils.isEmpty(expression)) {
					expression = expression.toLowerCase();
					if (expression.contains("rmb(")) {
						// 计算人民币大小写转换
						setMoneyConvert(etExpression, expression);
					} else if (expression.contains("count(")) {
						// 统计个数
					} else if (expression.contains("sum(")) {
						// 计算总和
					} else if (expression.contains("avg(")) {
						// 计算平均值
					} else if (expression.contains("sum(")) {

					} else if (expression.contains("*")
							|| expression.contains("-")
							|| expression.contains("+")
							|| expression.contains("/")) {
						setOperatorConvert(etExpression, expression);
					}
				}
			}
		}
	}

	/***
	 * 绑定计算明细表公式
	 *
	 * @param detailValues
	 *            明细表值集合
	 */
	public void setDetailExpression(List<EditText> editList,
									List<List<String>> detailValues) {
		mEditList = editList;
		if (mEditList != null && mEditList.size() > 0) {
			for (int i = 0; i < mEditList.size(); i++) {
				// 遍历带有公式的EditText
				final EditText etExpression = mEditList.get(i);
				FieldInfo fieldInfoExpression = (FieldInfo) etExpression
						.getTag();
				String expression = fieldInfoExpression.expression;
				if (!TextUtils.isEmpty(expression)) {
					expression = expression.toLowerCase();
					if (expression.contains("sum(")) {
						// 统计总和
						setSumResult(detailValues, etExpression, expression);
					} else if (expression.contains("count(")) {
						// 统计个数
						setCountResult(detailValues, etExpression, expression);
					} else if (expression.contains("avg(")) {
						// 计算平均值
						setAvgResult(detailValues, etExpression, expression);
					}
				}
			}
		}
	}

	/**
	 * 生成明细表相应的UI界面(横向排列，带有表头的明细表),并返回表单明细值的集合
	 */
	public List<List<String>> createDetailsUI() {
		if (mDetailsFields == null || mDetailsFields.size() < 0) {
			return mDetailValues;
		}
		mRoot.setVisibility(View.VISIBLE);
		int width = (int) (mWindowHeightPixels / mDetailsFields.size());
		int height = (int) (ViewHelper.dip2px(mContext, 50) + 0.5f);

		// 生成明细表表头
		createHeaderLinearLayout(width, height);
		// 生成明细表
		addDetailsTable(width, height);

		return mDetailValues;
	}

	/**
	 * 生成明细表相应的UI界面,纵向无表头的明细表,并返回表单明细值的集合
	 */
	public List<List<String>> createVeticalDetailsUI() {
		if (mDetailsFields == null || mDetailsFields.size() < 0) {
			// 如果没有明细字段，则不生成页面
			return mDetailValues;
		}
		mRoot.setVisibility(View.VISIBLE);
		int width = (int) (mWindowHeightPixels / mDetailsFields.size());
		int height = (int) (ViewHelper.dip2px(mContext, 50) + 0.5f);
		// 生成纵向排列的明细表
		addVeticalDetailsTable(width, height);
		return mDetailValues;
	}

	/***
	 * 获取生成表单的所有多附件上传控件，每个控件通过tag的形式持有对应的FieldInfo对象
	 *
	 * 2016-5-19
	 *
	 * @return
	 */
	public List<MultipleAttachView> getMultipleAttachViews() {
		return mAttachViews;
	}

	/**
	 * 生成明细表每一条明细相应的UI界面,并返回..
	 *
	 * @param rowValue
	 * @return
	 */
	public List<EditText> createDetailsAddRowUI(List<String> rowValue, int pos) {
		if (mDetailsFields == null || mDetailsFields.size() < 0) {
			return mEditList;
		}
		// 将dp转化为px
		final float scale = mContext.getResources().getDisplayMetrics().density;
		int width = (int) (80 * scale + 0.5f);
		int height = (int) (50 * scale + 0.5f);
		int leftPadding = (int) (10 * scale + 0.5f);

		if (rowValue.size() == mDetailsFields.size()) {
			// 生成相应的UI界面
			for (int i = 0; i < rowValue.size(); i++) {
				String fieldStyle = mDetailsFields.get(i).fieldStyle;
				String fieldName = mDetailsFields.get(i).fieldName;
				if (!TextUtils.isEmpty(fieldStyle) && !fieldName.contains("附件")) {
					String dict = mDetailsFields.get(i).fieldDict; // 绑定字典项
					LogUtils.d(TAG, "Dict=" + dict);
					LogUtils.d("fieldStyle", "fieldStyle=" + fieldStyle);
					FieldInfo fieldInfo = mDetailsFields.get(i);
					fieldInfo.fieldValue = rowValue.get(i);
					LinearLayout linearLayout = new LinearLayout(mContext);
					linearLayout.setOrientation(LinearLayout.HORIZONTAL);
					LayoutParams params = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);

					// 添加左边提示文字
					addTextView(width, height, leftPadding,
							fieldInfo.fieldName, linearLayout);

					if ("textbox".equalsIgnoreCase(fieldStyle)
							&& TextUtils.isEmpty(dict)) {
						addEditTextView(fieldInfo, linearLayout, params);
					} else if ("combobox".equalsIgnoreCase(fieldStyle)
							|| "dropdownlist".equalsIgnoreCase(fieldStyle)) {
						addComboxView(fieldInfo, linearLayout, params);
					} else if ("datepicker".equalsIgnoreCase(fieldStyle)) {
						addDateTimeEditView(fieldInfo, linearLayout, params);
					} else if ("checkbox".equalsIgnoreCase(fieldStyle)) {
						addCheckedBox(fieldInfo, linearLayout, params);
					} else if ("index".equalsIgnoreCase(fieldStyle)
							|| "序号".equals(fieldName)) {
						fieldInfo.fieldValue = pos + ""; // 序号，默认显示行号
						addEditTextView(fieldInfo, linearLayout, params);
					} else {
						addOtherEditView(fieldInfo, linearLayout, params);
					}
					mRoot.addView(linearLayout);
					// 加分割线
					if (i != mFields.size() - 1) {
						addHorionzalLine();
					}
				}
			}
		}

		return mEditList;
	}

	/** 生成明细表 */
	private void addDetailsTable(int width, int height) {
		if (mDetailValues == null || mDetailValues.size() <= 0) {
			return;
		} else {
			LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			int padding = (int) ViewHelper.dip2px(mContext, 8);
			for (int i = 0; i < mDetailValues.size(); i++) {
				// 生成横向分割线
				addHorionzalLine();
				LinearLayout rowLayout = new LinearLayout(mContext);
				rowLayout.setPadding(0, padding, 0, padding);
				rowLayout.setOrientation(LinearLayout.HORIZONTAL);
				rowLayout.setLayoutParams(params);
				final ArrayList<String> valueList = new ArrayList<String>();
				for (int k = 0; k < mDetailValues.get(i).size(); k++) {
					// 初始化，将List存为ArrayList中
					valueList.add(mDetailValues.get(i).get(k));
				}
				for (int j = 0; j < valueList.size(); j++) {// 生成表中的每一个item
					String showValue = valueList.get(j).toString();
					if (mDetailsFields.size() == valueList.size()) {
						String dict = mDetailsFields.get(j).fieldDict;
						String fieldStyle = mDetailsFields.get(j).fieldStyle;
						String fieldName = mDetailsFields.get(j).fieldName;

						if (!TextUtils.isEmpty(dict)
								&& "combobox".equalsIgnoreCase(fieldStyle)
								&& !TextUtils.isEmpty(showValue)) {
							int key = Integer.parseInt(showValue);
							showValue = getDictInfo(dict, key);
						}

						if ("index".equalsIgnoreCase(fieldStyle)
								|| "序号".equals(fieldName)) {
							showValue = (i + 1) + "";
						}
					}
					addRowTextView(width, height, showValue, rowLayout);
				}

				if (isEdit) { // 如果可编辑，为每一个行添加点击事件
					final int pos = i;
					rowLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									CreateVmDetailsFormAddRowActivity.class);
							Bundle bundle = new Bundle();
							bundle.putBoolean("isEdit", isEdit);
							bundle.putInt("listAtPos", pos);
							bundle.putStringArrayList("rowValueList", valueList);
							bundle.putSerializable(
									CreateVmDetailsFormAddRowActivity.TAG,
									mVmFormDef);
							intent.putExtras(bundle);
							mActivity
									.startActivityForResult(
											intent,
											CreateVmDetailsFormActivity.REQUEST_CODE_UPDATE_DETAILS);
						}
					});
				}
				mRoot.addView(rowLayout);
			}
		}
	}

	int startX = 0;// 手指按下位置
	int endX = 0;// 手指按下位置

	/** 生成纵向排列的明细表 */
	private void addVeticalDetailsTable(int width, int height) {
		if (mDetailValues == null || mDetailValues.size() <= 0) {
			return;
		} else {
			LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			int padding = (int) ViewHelper.dip2px(mContext, 8);
			for (int i = 0; i < mDetailValues.size(); i++) {// 遍历明细行
				// 包括行和底部分割线
				final LinearLayout itemLayout = new LinearLayout(mContext);
				itemLayout.setOrientation(LinearLayout.VERTICAL);
				// 明细行
				final LinearLayout rowLayout = new LinearLayout(mContext);
				rowLayout.setPadding(padding, padding, padding, padding);
				rowLayout.setOrientation(LinearLayout.HORIZONTAL);
				rowLayout.setGravity(Gravity.CENTER_VERTICAL);
				rowLayout.setLayoutParams(params);
				final ArrayList<String> valueList = new ArrayList<String>();
				for (int k = 0; k < mDetailValues.get(i).size(); k++) {
					// 初始化，将List存为ArrayList中
					valueList.add(mDetailValues.get(i).get(k));
				}

				// 显示值区域
				LinearLayout valueLayout = new LinearLayout(mContext);
				valueLayout.setLayoutParams(new LinearLayout.LayoutParams(0,
						LayoutParams.WRAP_CONTENT, 6));
				valueLayout.setPadding(padding, 0, padding, 0);
				valueLayout.setOrientation(LinearLayout.VERTICAL);
				valueLayout.setGravity(Gravity.LEFT);
				// TODO 如果mobileX 和 mobileY全为0则导致不显示
				// 生成表中的每一个item
				int itemCount = 0;// 记录明细表显示个数
				for (int j = 0; j < valueList.size(); j++) {
					int mobileX = mDetailsFields.get(j).MobileX;
					int mobileY = mDetailsFields.get(j).MobileY;
					String dict = mDetailsFields.get(j).fieldDict;
					String fieldStyle = mDetailsFields.get(j).fieldStyle;
					if (mobileX != 0 && mobileY != 0) {
						itemCount++; // 记录第一次添加个数
						String showValue = valueList.get(j).toString();
						String fieldName = mDetailsFields.get(j).fieldName;
						if (mDetailsFields.size() == valueList.size()) {// 保证不发生数组越界
							if (!TextUtils.isEmpty(dict)
									&& !TextUtils.isEmpty(showValue)
									&& ("combobox".equalsIgnoreCase(fieldStyle) || "dropdownlist"
									.equalsIgnoreCase(fieldStyle))) {
								int key = Integer.parseInt(showValue);
								showValue = getDictInfo(dict, key);
							}
						}

						showValue = fieldName + ": " + showValue;
						addRowTextView(LayoutParams.MATCH_PARENT, height,
								showValue, valueLayout);
					}
				}

				// 如果表单配置中没有显示，则默认显示3个
				if (itemCount == 0 && valueList != null) {
					itemCount = valueList.size() > 3 ? 3 : valueList.size();
					for (int j = 0; j < itemCount; j++) {
						String showValue = valueList.get(j).toString();
						String fieldName = mDetailsFields.get(j).fieldName;
						if (mDetailsFields.size() == valueList.size()) {
							String dict = mDetailsFields.get(j).fieldDict;
							String fieldStyle = mDetailsFields.get(j).fieldStyle;
							if (!TextUtils.isEmpty(dict)
									&& ("combobox".equalsIgnoreCase(fieldStyle) || "dropdownlist"
									.equalsIgnoreCase(fieldStyle))
									&& !TextUtils.isEmpty(showValue)) {
								int key = Integer.parseInt(showValue);
								showValue = getDictInfo(dict, key);
							}
						}
						showValue = fieldName + ": " + showValue;
						addRowTextView(LayoutParams.MATCH_PARENT, height,
								showValue, valueLayout);
					}
				}

				rowLayout.addView(valueLayout);

				// 添加删除按钮
				LinearLayout.LayoutParams deleteButtonLayoutParams = new LinearLayout.LayoutParams(
						0, LayoutParams.WRAP_CONTENT, 1);
				// 右箭头
				ImageView iv_arrow = new ImageView(mContext);
				iv_arrow.setLayoutParams(deleteButtonLayoutParams);
				iv_arrow.setImageResource(R.drawable.arrow_right_blue);
				rowLayout.addView(iv_arrow);

				// 如果可编辑，为每一个行添加点击事件
				final int pos = i;
				if (isEdit) {
					final ImageView iv_delete = new ImageView(mContext);
					iv_delete.setVisibility(View.GONE);
					iv_delete.setLayoutParams(deleteButtonLayoutParams);
					iv_delete.setImageResource(R.drawable.ico_remove);
					rowLayout.addView(iv_delete);
					rowLayout.setOnTouchListener(new OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								startX = (int) event.getX();
								LogUtils.i(TAG, "手指抬起:" + startX);
							} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
								endX = (int) event.getX();
								// LogUtils.i(TAG, "滑动" + endX);
							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								endX = (int) event.getX();
								LogUtils.i(TAG, "手指抬起:" + endX);
								int distance = endX - startX;
								LogUtils.i(TAG, "distance=" + distance);
								// if (endX < startX && distance > 100) {
								if (distance < -100) {
									iv_delete.setVisibility(View.VISIBLE);
									return true;
								} else if (distance > 100) {
									iv_delete.setVisibility(View.GONE);
									return true;
								}
							}
							return false;
						}
					});
					iv_delete.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							int childCount = mRoot.getChildCount();
							if (childCount == 1) {
								Toast.makeText(mContext, "明细表不能全部删空，至少保留一条记录",
										Toast.LENGTH_SHORT).show();
							} else if (childCount > 1) {
								Toast.makeText(mContext, "删除第" + pos + "行明细",
										Toast.LENGTH_SHORT).show();
								// mRoot.removeViewAt(pos);
								mDetailValues.remove(pos);
								mRoot.removeAllViews();
								createVeticalDetailsUI();// 重新生成界面
							}
						}
					});
				}

				rowLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext,
								CreateVmDetailsFormAddRowActivity.class);
						Bundle bundle = new Bundle();
						bundle.putBoolean("isEdit", isEdit);
						bundle.putInt("listAtPos", pos);
						bundle.putStringArrayList("rowValueList", valueList);
						bundle.putSerializable(
								CreateVmDetailsFormAddRowActivity.TAG,
								mVmFormDef);
						intent.putExtras(bundle);
						mActivity
								.startActivityForResult(
										intent,
										CreateVmDetailsFormActivity.REQUEST_CODE_UPDATE_DETAILS);
					}
				});

				itemLayout.addView(rowLayout);
				if (i < mDetailValues.size() - 1) {
					// 添加分割线
					addHorionzalLine(itemLayout);
				}
				mRoot.addView(itemLayout);

			}
		}
	}

	/**
	 * 绘制横向分割线
	 */
	private void addHorionzalLine() {
		View view = new View(mContext);
		view.setMinimumHeight(1);
		view.setMinimumWidth(LayoutParams.MATCH_PARENT);
		view.setBackgroundColor(0xFFdddddd);
		mRoot.addView(view);
	}

	/**
	 * 指定控件绘制横向分割线
	 */
	private void addHorionzalLine(LinearLayout layout) {
		View view = new View(mContext);
		view.setMinimumHeight(1);
		view.setMinimumWidth(LayoutParams.MATCH_PARENT);
		view.setBackgroundColor(0xFFdddddd);
		layout.addView(view);
	}

	/***
	 * 生成表头布局
	 *
	 * @param width
	 *            列宽
	 * @param height
	 *            列高
	 */
	private void createHeaderLinearLayout(int width, int height) {
		LinearLayout headerLinearLayout = new LinearLayout(mContext);
		headerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		headerLinearLayout.setLayoutParams(params);
		for (int i = 0; i < mDetailsFields.size(); i++) {
			String fieldName = mDetailsFields.get(i).fieldName;
			if (!TextUtils.isEmpty(fieldName)) {
				addHeaderTextView(width, height, fieldName, headerLinearLayout);
			}
		}
		mRoot.addView(headerLinearLayout);
	}

	private void addOtherEditView(FieldInfo fieldInfo,
								  LinearLayout linearLayout, LayoutParams params) {
		EditText editText = new EditText(mContext);
		setEditEnable(fieldInfo, editText);
		setHiddenFields(fieldInfo, editText);

		// editText.setHint("点击填写");
		editText.setTextColor(mContext.getResources().getColor(
				R.color.text_info));
		editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		editText.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		editText.setBackgroundColor(Color.TRANSPARENT);
		editText.setLayoutParams(params);

		String readOnly = fieldInfo.readOnly;
		if (!TextUtils.isEmpty(readOnly) && "true".equalsIgnoreCase(readOnly)) {
			editText.setEnabled(false);
		}
		editText.setTag(fieldInfo);
		mEditList.add(editText);
		linearLayout.addView(editText);
	}

	/**
	 * 多图片类型
	 *
	 * @param fieldInfo
	 *            一个配置单元<FieldInfo>的信息,包含了一行表单的信息
	 * @param linearLayout
	 *            用于生成表单的父布局
	 * @param params
	 */
	private void addMultiImageView(final FieldInfo fieldInfo,
								   LinearLayout linearLayout, LayoutParams params) {
		String fieldValue = fieldInfo.fieldValue;
		// new一个线性布局
		final EditText editText = new EditText(mContext);
		editText.setEnabled(false);
		editText.setFocusable(false);
		editText.setVisibility(View.GONE);
		editText.setTextColor(mContext.getResources().getColor(
				R.color.text_info));
		editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		editText.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		// editText.setHint("点击选择");
		editText.setBackgroundColor(Color.TRANSPARENT);
		editText.setLayoutParams(params);

		LinearLayout llOther = new LinearLayout(mContext);
		LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);

		llOther.setLayoutParams(llParams);
		llOther.setPadding(0, 5, 5, 5);
		llOther.setGravity(Gravity.RIGHT);

		final MultipleAttachView gView = MultipleAttachView.getInstance(
				mContext, 3);
		gView.setIsAdd(isEdit);
		gView.setTag(fieldInfo);
		gView.loadImageByAttachIds(fieldValue);
		gView.setOnAddImageListener(new OnAddImageListener() {
			@Override
			public void onAddImageListener() {
				mMultipleAttachFieldName = fieldInfo.fieldName;
			}
		});

		gView.setLayoutParams(llParams);
		llOther.addView(gView);

		editText.setTag(fieldInfo);
		mEditList.add(editText);
		mAttachViews.add(gView);
		linearLayout.addView(llOther);
		linearLayout.addView(editText);
	}

	/** 设置文本框是否可编辑 */
	private void setEditEnable(FieldInfo fieldInfo, EditText editText) {
		if (!isEdit) {
			if (TextUtils.isEmpty(mVmFormDef.CurrentNodeEditableCells)) {
				// 可编辑单元格为空
				editText.setEnabled(isEdit);
			} else if (!TextUtils.isEmpty(mVmFormDef.CurrentNodeEditableCells)
					&& !mVmFormDef.CurrentNodeEditableCells
					.contains(fieldInfo.fieldName)) {
				// 可编辑单元格 不包括当前
				editText.setEnabled(isEdit);
				mEditStuts = EditableByCurrentNodeEditableCells;
			}
		} else
			mEditStuts = EditableByDefault;
	}

	/** 根据HiddenFields设置对应隐藏文本框 */
	private void setHiddenFields(FieldInfo fieldInfo, EditText editText) {
		if (!TextUtils.isEmpty(mVmFormDef.HiddenFields)
				&& mVmFormDef.HiddenFields.contains(fieldInfo.fieldName)) {
			// 可编辑单元格 不包括当前
			editText.setVisibility(View.INVISIBLE);
		}
	}

	private void addDateTimeEditView(FieldInfo fieldInfo,
									 LinearLayout linearLayout, LayoutParams params) {
		final EditText editText = new EditText(mContext);
		editText.setFocusable(false);
		setEditEnable(fieldInfo, editText);
		setHiddenFields(fieldInfo, editText);
		editText.setTextColor(mContext.getResources().getColor(
				R.color.text_info));
		editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		editText.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		// editText.setHint("点击选择");
		editText.setBackgroundColor(Color.TRANSPARENT);
		editText.setLayoutParams(params);

		String readOnly = fieldInfo.readOnly;
		if (!TextUtils.isEmpty(readOnly) && "true".equals(readOnly)) {
			editText.setEnabled(false);
		}

		if (!TextUtils.isEmpty(fieldInfo.fieldValue)) {
			//
			editText.setText(fieldInfo.fieldValue);
		} else if (!TextUtils.isEmpty(fieldInfo.defaultValue)) {
			// 没有值则 设置默认值
			if (fieldInfo.defaultValue.toLowerCase().contains("now")) {
				String dateStr = ViewHelper.getDateString();
				editText.setText(dateStr);
				fieldInfo.fieldValue = dateStr;
			}
		}

		if (isEdit
				|| (!TextUtils.isEmpty(mVmFormDef.CurrentNodeEditableCells) && mVmFormDef.CurrentNodeEditableCells
				.contains(fieldInfo.fieldName))) { // 如果控件可编辑，为其绑定监听事件
			editText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					dateAndTimePicker.showDateWheel(editText);
				}
			});
		}

		editText.setTag(fieldInfo);
		mEditList.add(editText);
		linearLayout.addView(editText);
	}

	/** 选择是否 */
	private void addCheckedBox(final FieldInfo fieldInfo,
							   LinearLayout linearLayout, LayoutParams params) {
		final EditText editText = new EditText(mContext);
		editText.setFocusable(false);
		setEditEnable(fieldInfo, editText);
		setHiddenFields(fieldInfo, editText);
		editText.setTextColor(mContext.getResources().getColor(
				R.color.text_info));
		editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		editText.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		// editText.setHint("点击选择");
		editText.setBackgroundColor(Color.TRANSPARENT);
		editText.setLayoutParams(params);

		String readOnly = fieldInfo.readOnly;
		if (!TextUtils.isEmpty(readOnly) && "true".equals(readOnly)) {
			editText.setEnabled(false);
		}

		if (TextUtils.isEmpty(fieldInfo.defaultValue)) {
			fieldInfo.fieldValue = "0";
		}

		if (!TextUtils.isEmpty(fieldInfo.fieldValue)) {
			try {
				int index = Integer.parseInt(fieldInfo.fieldValue);
				if (index < checkStrs.length) {
					editText.setText(checkStrs[index]);
				}
			} catch (Exception e) {
				Log.e(TAG, e + "");
			}
		}

		if (isEdit
				|| (!TextUtils.isEmpty(mVmFormDef.CurrentNodeEditableCells) && mVmFormDef.CurrentNodeEditableCells
				.contains(fieldInfo.fieldName))) { // 如果控件可编辑，为其绑定监听事件
			editText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mDictIosPicker.show(checkStrs);
					mDictIosPicker
							.setOnSelectedListener(new DictIosPickerBottomDialog.OnSelectedListener() {
								@Override
								public void onSelected(int index) {
									editText.setText(checkStrs[index]);
									fieldInfo.fieldValue = "" + index;
								}
							});
				}
			});
		}

		editText.setTag(fieldInfo);
		mEditList.add(editText);
		linearLayout.addView(editText);
	}

	/** 添加TextView控件 */
	private void addTextView(int width, int height, int leftPadding,
							 String fildName, LinearLayout linearLayout) {
		TextView textView = new TextView(mContext);
		textView.setText(fildName);
		textView.setWidth(width);
		textView.setHeight(LayoutParams.WRAP_CONTENT);
		textView.setMinHeight(height);
		textView.setPadding(leftPadding, 0, 0, 0);
		textView.setGravity(Gravity.LEFT | Gravity.CENTER);
		textView.setTextColor(mContext.getResources().getColor(R.color.text_info));
		textView.setTextColor(mContext.getResources().getColor(
				R.color.text_info));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		linearLayout.addView(textView);
	}

	/** 添加表头TextView控件 */
	private void addHeaderTextView(int width, int height, String fildName,
								   LinearLayout linearLayout) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,
				height, 1);
		TextView textView = new TextView(mContext);
		textView.setText(fildName);
		// textView.setWidth(0);
		// textView.setHeight(0);
		textView.setLayoutParams(params);
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(Color.BLACK);
		textView.setLayoutParams(params);
		TextPaint tp = textView.getPaint();
		tp.setFakeBoldText(true); // 字体加粗
		textView.setTextSize(18);
		linearLayout.addView(textView);
	}

	/** 添加明细表 行TextView控件 */
	private void addRowTextView(int width, int height, String fildName,
								LinearLayout linearLayout) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,
				LayoutParams.MATCH_PARENT, 1);
		TextView textView = new TextView(mContext);
		textView.setText(fildName);
		// textView.setWidth(0);
		// textView.setHeight(0);
		textView.setLayoutParams(params);

		textView.setTextColor(mContext.getResources().getColor(
				R.color.text_info));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		textView.setLayoutParams(params);
		// textView.setHint("请选择");
		linearLayout.addView(textView);
	}

	/**
	 *
	 * @param fieldInfo
	 *            一个配置单元<FieldInfo>的信息,包含了一行表单的信息
	 * @param linearLayout
	 *            用于生成表单的父布局
	 * @param params
	 */
	private void addEditTextView(FieldInfo fieldInfo,
								 LinearLayout linearLayout, LayoutParams params) {
		String fieldValue = fieldInfo.fieldValue;
		String defalutValue = fieldInfo.defaultValue;

		final EditText editText = new EditText(mContext);
		setEditEnable(fieldInfo, editText);
		setHiddenFields(fieldInfo, editText);

		// editText.setHint("点击填写");
		editText.setTextColor(mContext.getResources().getColor(
				R.color.text_info));
		editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		editText.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		editText.setBackgroundColor(Color.TRANSPARENT);
		editText.setLayoutParams(params);

		if (!TextUtils.isEmpty(fieldValue)) {// 如果携带数据,则显示
			editText.setText(fieldValue);
		} else if (!TextUtils.isEmpty(fieldInfo.dataType)
				&& (fieldInfo.dataType.equalsIgnoreCase("int") || fieldInfo.dataType
				.equalsIgnoreCase("double"))) {
			// 既没有值fieldValue类型的整数，设置默认为0
			fieldInfo.fieldValue = "0";
			// editText.setText(fieldInfo.fieldValue);
		} else if (!TextUtils.isEmpty(defalutValue)) {
			editText.setText(defalutValue);

			if (defalutValue.contains("user")) {
				// 设置用户默认值
				editText.setText(Global.mUser.UserName);
				// editText.setTag(Global.mUser.Id);
				fieldInfo.fieldValue = Global.mUser.Id + "";
			}
		}

		String readOnly = fieldInfo.readOnly;
		if (!TextUtils.isEmpty(readOnly) && "true".equals(readOnly)) {
			editText.setEnabled(false);
			editText.setHint("");
		}
		editText.setTag(fieldInfo);
		mEditList.add(editText);
		linearLayout.addView(editText);
	}

	/**
	 *
	 * @param fieldInfo
	 *            一个配置单元<FieldInfo>的信息,包含了一行表单的信息
	 * @param linearLayout
	 *            用于生成表单的父布局
	 * @param params
	 */
	private void addComboxView(final FieldInfo fieldInfo,
							   LinearLayout linearLayout, LayoutParams params) {
		String fieldValue = fieldInfo.fieldValue;
		// new一个线性布局
		final EditText editText = new EditText(mContext);
		editText.setFocusable(false);
		setEditEnable(fieldInfo, editText);
		setHiddenFields(fieldInfo, editText);
		editText.setTextColor(mContext.getResources().getColor(
				R.color.text_info));
		editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		editText.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		// editText.setHint("点击选择");
		editText.setBackgroundColor(Color.TRANSPARENT);
		editText.setLayoutParams(params);

		String defaultValue = fieldInfo.defaultValue;
		final String dict = fieldInfo.fieldDict;

		String readOnly = fieldInfo.readOnly;
		if (!TextUtils.isEmpty(readOnly) && "true".equalsIgnoreCase(readOnly)) {
			editText.setEnabled(false);
		}

		// 设置默认值
		if (!TextUtils.isEmpty(dict)) {
			if (!TextUtils.isEmpty(fieldValue)) {// 字段有值有字典则显示
				if ("员工".equals(dict)) {
					// 根据员工编号，查询员工姓名
					String userName = dictionaryHelper
							.getUserNameById(fieldValue);
					editText.setText(userName + "");
				} else {// 除了客户和员工以外的 选择项，根据字典编号设置字典名称
					int value = Integer.parseInt(fieldValue);
					HashMap<Integer, String> dictHashMap = mDictionaries
							.get(dict);
					if (dictHashMap != null) {
						String defaultStrValue = dictHashMap.get(value);
						editText.setText(defaultStrValue);
					}
				}
			} else if (!TextUtils.isEmpty(defaultValue)) {// 判断是否有DefaultValue字段
				defaultValue = defaultValue.toLowerCase();
				if (defaultValue.contains("user")) {
					// 设置用户默认值
					editText.setText(Global.mUser.UserName);
					// editText.setTag(Global.mUser.Id);
					fieldInfo.fieldValue = Global.mUser.Id + "";
				} else if (defaultValue.contains("department")) {
					HashMap<Integer, String> dictHashMap = mDictionaries
							.get(dict);
					String defaultDepartment = dictHashMap
							.get(Global.mUser.Department);
					editText.setText(defaultDepartment);
					fieldInfo.fieldValue = Global.mUser.Department + "";
					LogUtils.i(TAG, Global.mUser.CorpId + ":"
							+ Global.mUser.Department);
				} else if (defaultValue.contains("职务")) {
					HashMap<Integer, String> dictHashMap = mDictionaries
							.get(dict);
					String defaultPosition = dictHashMap
							.get(Global.mUser.Position);
					editText.setText(defaultPosition);
					fieldInfo.fieldValue = Global.mUser.Position + "";
				} else {
					// 如果有默认值 根据字典项查询该默认值key对应的value
					try {
						int value = Integer.parseInt(defaultValue);
						HashMap<Integer, String> dictHashMap = mDictionaries
								.get(dict);
						String defaultStrValue = dictHashMap.get(value);
						editText.setText(defaultStrValue);
					} catch (Exception e) {
						LogUtils.e(TAG, "" + e);
					}
				}
			}
		}

		if ((isEdit && !TextUtils.isEmpty(dict) && !"true"
				.equalsIgnoreCase(readOnly))
				|| (!TextUtils.isEmpty(mVmFormDef.CurrentNodeEditableCells) && mVmFormDef.CurrentNodeEditableCells
				.contains(fieldInfo.fieldName))) {
			editText.setEnabled(true); //
			// 如果是字段为客户，就跳转到客户列表
			if ("客户".equals(dict)) {
				editText.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						((InputMethodManager) mContext
								.getSystemService(Context.INPUT_METHOD_SERVICE))
								.hideSoftInputFromWindow(
										editText.getWindowToken(), 0);
						Intent intent = new Intent(mContext,
								ClientListActivity.class);
						intent.putExtra(ClientListActivity.SELECT_CLIENT, true);
						mActivity.startActivityForResult(intent,
								CreateVmFormActivity.SELECT_CLIENT_CODE);
					}
				});
			} else if ("员工".equals(dict)) {
				// if (binding.equals("客户")) {
				editText.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO 取消软键盘
						InputSoftHelper.hiddenSoftInput(mContext, editText);
						// 记录当前选择员工的字段名
						mUserFieldName = fieldInfo.fieldName;
						// 跳转到选择员工的Activity
						Intent intent = new Intent(mActivity,
								User_SelectActivityNew_zmy.class);
						intent.putExtra(User_SelectActivityNew.SELECT_EMPLOYEE,
								true);
						mActivity.startActivityForResult(intent,
								CreateVmFormActivity.SELECT_USER_CODE);
					}
				});
			} else if ("product".equalsIgnoreCase(dict)) {
				editText.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// 取消软键盘
						InputSoftHelper.hiddenSoftInput(mContext, editText);
						// 跳转到选择產品的Activity
						Intent intent = new Intent(mActivity,
								ProductSelectListActivity.class);
						mActivity.startActivityForResult(intent,
								CreateVmFormActivity.SELECT_PRODUCT_CODE);
					}
				});
			} else {
				editText.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// 取消软键盘
						InputSoftHelper.hiddenSoftInput(mContext, editText);

						dictionaryQueryDialogHelper.show(dict);
						dictionaryQueryDialogHelper
								.setOnSelectedListener(new OnSelectedListener() {
									@Override
									public void onSelected(字典 dict) {
										fieldInfo.fieldValue = dict.getId()
												+ "";
										editText.setText(dict.getName());
									}
								});
					}
				});
			}
		}

		editText.setTag(fieldInfo);
		mEditList.add(editText);
		linearLayout.addView(editText);
	}

	/**
	 * 设置用户的下拉选项
	 *
	 * @param editText
	 *            接受输入的编辑框
	 * @param dict
	 *            字典的字段
	 */
	private void setUserComboBox(final EditText editText) {
		final FieldInfo fieldInfo = (FieldInfo) editText.getTag();
		String dict = fieldInfo.fieldDict;
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("请选择" + dict);
		// HashMap<String, String> mapString = hashMap.get(dict);
		if (mDictionaries == null || TextUtils.isEmpty(dict)) {
			return;
		}

		final ArrayList<Integer> keyList = new ArrayList<Integer>();
		ArrayList<String> valueList = new ArrayList<String>();
		if (mDictionaries == null) {
			return;
		}

		// 获取指定名称的字典项集合：如部门列表
		HashMap<Integer, String> mapString = mDictionaries.get(dict);
		for (Entry<Integer, String> e : mapString.entrySet()) {
			keyList.add(e.getKey());
			valueList.add(e.getValue());
		}

		final String[] arr = new String[valueList.size()];
		for (int i = 0; i < valueList.size(); i++) {
			arr[i] = valueList.get(i);
		}

		// 选中选项事件监听
		builder.setItems(arr, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				editText.setText(arr[which]);
				if (which < keyList.size()) {
					// 将选中字典号保存在默认值
					int selectedDict = keyList.get(which);
					// fieldInfo.defaultValue = selectedDict + "";
					fieldInfo.fieldValue = selectedDict + "";
				}
			}
		});
		builder.create().show();
	}

	/***
	 * 获取字典项内容
	 *
	 * @param dict
	 *            字典表名称如[部门]/[员工]
	 * @param key
	 *            字典键值
	 * @return 如果无则返回 “”
	 */
	private String getDictInfo(String dict, int key) {
		if (mDictionaries == null) {
			return "";
		}
		HashMap<Integer, String> dictMap = mDictionaries.get(dict);
		if (dictMap != null && dictMap.size() > 0) {
			return dictMap.get(key);
		}
		return "";
	}

	public String getmFormName() {
		return mFormName;
	}

	public void setmFormName(String mFormName) {
		this.mFormName = mFormName;
	}

	/**
	 * 获得流程分类表中
	 *
	 * @return
	 */
	public String getmFromRealName() {
		if (TextUtils.isEmpty(mFromRealName)) {
			mFromRealName = getmFormName();
		}
		return mFromRealName;
	}

	public void setmFromRealName(String mFromRealName) {
		this.mFromRealName = mFromRealName;
	}

	/** 获取附件的字段名称 */
	public String getMultipleFieldName() {
		return mMultipleAttachFieldName;
	}

	/***
	 * 设置大小写公式绑定
	 *
	 * @param etExpression
	 *            计算公式的文本框
	 * @param expression
	 *            公式
	 */
	private void setMoneyConvert(final EditText etExpression, String expression) {
		// 包含公式数字大小写转换
		LogUtils.i(TAG, expression);
		for (int j = 0; j < mEditList.size(); j++) {
			final EditText eText = mEditList.get(j);// 绑定公式计算的文本框监听
			FieldInfo fieldInfo = (FieldInfo) eText.getTag();
			String fieldName = fieldInfo.fieldName;
			if (!TextUtils.isEmpty(fieldName)
					&& expression.contains("(" + fieldName + ")")) {
				LogUtils.i(TAG, "涉及字段：" + fieldName);
				eText.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start,
											  int before, int count) {
						try {
							int money = Integer.parseInt(s.toString());
							String moneyUp = MoneyUtils.change(money);
							LogUtils.i(TAG, "文字发生变化：" + moneyUp);
							etExpression.setText(moneyUp + "");
						} catch (Exception e) {
							LogUtils.e(TAG, e + "");
						}
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
												  int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});
			}
		}
	}

	/***
	 * 设置运算符公式绑定，设置监听
	 *
	 * @param etExpression
	 *            计算公式的文本框
	 * @param expression
	 *            公式
	 */
	private void setOperatorConvert(final EditText etExpression,
									String expression) {
		final HashMap<String, String> hashMap = new HashMap<String, String>();
		// 包含公式数字大小写转换
		final String operatorStr = expression;
		LogUtils.i(TAG, "setOperatorConvert--" + expression);
		for (int j = 0; j < mEditList.size(); j++) {
			final EditText eText = mEditList.get(j);// 绑定公式计算的文本框监听
			FieldInfo fieldInfo = (FieldInfo) eText.getTag();
			final String fieldName = fieldInfo.fieldName;
			final String fieldValue = fieldInfo.fieldValue;

			if (!TextUtils.isEmpty(fieldName) && expression.contains(fieldName)
					&& RegexUtils.isTrimChinese(expression, fieldName)) {
				LogUtils.i(TAG, "涉及字段：" + fieldName);
				if (!TextUtils.isEmpty(fieldValue)) {
					// 如果有值则
					hashMap.put(fieldName, fieldValue);
				}
				eText.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start,
											  int before, int count) {
						try {
							// etExpression.setText(moneyUp + "");
							// 以键值对的形式把 涉及公式字段和值保存到hashmap
							hashMap.put(fieldName, s.toString());
							LogUtils.i(TAG, "文字发生变化：onTextChanged");
						} catch (Exception e) {
							LogUtils.e(TAG, e + "");
						}
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
												  int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {
						LogUtils.i(TAG, "afterTextChanged：");
						String str = operatorStr;
						Iterator<Entry<String, String>> iter = hashMap
								.entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry<String, String> entry = (Map.Entry<String, String>) iter
									.next();
							String key = entry.getKey();
							String value = entry.getValue();
							LogUtils.i(TAG, key + "--" + value);
							str = str.replace(key, value);
						}
						try {
							// 表达式转为运算符
							Expression parsiiExpression = Parser.parse(str);
							double result = parsiiExpression.evaluate();
							LogUtils.i("out", "result =" + result);
							LogUtils.i(TAG, "運算結果：" + result);
							etExpression.setText(result + "");
						} catch (Exception e) {
							LogUtils.e(TAG, str);
						}
						LogUtils.i(TAG, str);
					}
				});
			}
		}
	}

	/***
	 * 明细表累加汇总运算符公式绑定，设置监听
	 *
	 * @param detailValues
	 *            明细value集合
	 * @param etExpression
	 *            计算公式的文本框
	 * @param expression
	 *            公式
	 * @return 累加和
	 */
	private double setSumResult(List<List<String>> detailValues,
								final EditText etExpression, String expression) {
		double result = 0;
		// 包含公式数字大小写转换
		final String operatorStr = expression;
		LogUtils.i(TAG, "setSumConvert--" + expression);
		int index = 0; // 获取求和公式在一条明细记录值的位置
		for (int i = 0; i < mDetailsFields.size(); i++) {
			FieldInfo fieldInfo = mDetailsFields.get(i);// 绑定公式计算的文本框监听
			final String fieldName = fieldInfo.fieldName.toLowerCase();
			if (!TextUtils.isEmpty(fieldName)
					&& expression.contains("(" + fieldName + ")")) {
				index = i;
			}
		}
		LogUtils.i(TAG, "INDEX=" + index);

		// 累加
		String operator = "0";
		for (int j = 0; j < detailValues.size(); j++) {
			operator += "+" + detailValues.get(j).get(index);
		}
		try {
			// 表达式转为运算符
			Expression parsiiExpression = Parser.parse(operator);
			result = parsiiExpression.evaluate();
			LogUtils.i("out", "result =" + result);
			LogUtils.i(TAG, "運算結果：" + result);
			etExpression.setText(result + "");
		} catch (Exception e) {
			LogUtils.e(TAG, e + "");
		}
		return result;
	}

	/***
	 * 明细表平均值 运算符公式绑定，设置监听
	 *
	 * @param detailValues
	 *            明细value集合
	 * @param etExpression
	 *            计算公式的文本框
	 * @param expression
	 *            公式
	 * @return 平均值
	 */
	private double setAvgResult(List<List<String>> detailValues,
								final EditText etExpression, String expression) {
		double result = 0;
		// 包含公式数字大小写转换
		final String operatorStr = expression;
		LogUtils.i(TAG, "setSumConvert--" + expression);
		int count = 0;
		int index = 0; // 获取求和公式在一条明细记录值的位置
		for (int i = 0; i < mDetailsFields.size(); i++) {
			FieldInfo fieldInfo = mDetailsFields.get(i);// 绑定公式计算的文本框监听
			final String fieldName = fieldInfo.fieldName.toLowerCase();
			if (!TextUtils.isEmpty(fieldName)
					&& expression.contains("(" + fieldName + ")")) {
				index = i;
				count++;
			}
		}
		LogUtils.i(TAG, "INDEX=" + index);

		// 累加
		String operator = "0";
		for (int j = 0; j < detailValues.size(); j++) {
			operator += "+" + detailValues.get(j).get(index);
		}
		try {
			// 表达式转为运算符
			Expression parsiiExpression = Parser.parse(operator);
			result = parsiiExpression.evaluate() / count;
			LogUtils.i("out", "result =" + result);
			LogUtils.i(TAG, "運算結果：" + result);
			etExpression.setText(result + "");
		} catch (Exception e) {
			LogUtils.e(TAG, e + "");
		}
		return result;
	}

	/***
	 * 明细表统计个数 运算符公式绑定，设置监听
	 *
	 * @param detailValues
	 *            明细value集合
	 * @param etExpression
	 *            计算公式的文本框
	 * @param expression
	 *            公式
	 * @return 平均值
	 */
	private double setCountResult(List<List<String>> detailValues,
								  final EditText etExpression, String expression) {
		// 包含公式数字大小写转换
		LogUtils.i(TAG, "setSumConvert--" + expression);
		int count = 0;
		for (int i = 0; i < mDetailsFields.size(); i++) {
			FieldInfo fieldInfo = mDetailsFields.get(i);// 绑定公式计算的文本框监听
			final String fieldName = fieldInfo.fieldName.toLowerCase();
			if (!TextUtils.isEmpty(fieldName)
					&& expression.contains("(" + fieldName + ")")) {
				count++;
			}
		}
		etExpression.setText("" + count);
		return count;
	}

	/**
	 * 给员工的控件赋值
	 *
	 */
	public void updateUserOnActivityForResult(List<EditText> editTexts,
											  String userName, String userId) {
		mEditList = editTexts; // 7.30号添加
		EditText eText = null;
		FieldInfo fieldInfo = null;
		for (int i = 0; i < mEditList.size(); i++) {
			eText = mEditList.get(i);
			fieldInfo = (FieldInfo) eText.getTag();
			String fieldDict = fieldInfo.fieldDict;
			if ("员工".equals(fieldDict)
					&& mUserFieldName.equals(fieldInfo.fieldName)) {
				eText.setText(userName);
				fieldInfo.fieldValue = userId + "";
				eText.setTag(fieldInfo);
			}
		}
	}

	/**
	 * 给员工的控件赋值
	 *
	 */
	public void updateMultipeAttachViewOnActivityForResult(int requestCode,
														   int resultCode, Intent data) {
		FieldInfo fieldInfo = null;
		for (MultipleAttachView attachView : mAttachViews) {
			fieldInfo = (FieldInfo) attachView.getTag();
			String fieldName = fieldInfo.fieldName;
			if (mMultipleAttachFieldName.equals(fieldName)) {
				mMultipleAttachFieldName = "";
				attachView.onActivityiForResultImage(requestCode, resultCode,
						data);
				break;
			}
		}
	}

	/**
	 * 设置product
	 *
	 */
	public void setProductControl(List<EditText> editTexts, String name,
								  String id) {
		mEditList = editTexts; // 7.30号添加
		EditText eText = null;
		FieldInfo fieldInfo = null;
		for (int i = 0; i < mEditList.size(); i++) {
			eText = mEditList.get(i);
			fieldInfo = (FieldInfo) eText.getTag();
			String fieldDict = fieldInfo.fieldDict;
			if ("Product".equalsIgnoreCase(fieldDict)) {
				eText.setText(name);
				fieldInfo.fieldValue = id + "";
				eText.setTag(fieldInfo);
			}
		}
	}

	/** 获取当前页面可编辑状态 {@link EditableByCurrentNodeEditableCells} */
	public int getmEditStuts() {
		return mEditStuts;
	}

	/**
	 * 设置当前页面可编辑状态
	 *
	 * @param editStuts
	 *            可编辑状态{@link EditableByCurrentNodeEditableCells}
	 * */
	public void setmEditStuts(int editStuts) {
		this.mEditStuts = editStuts;
	}

}
