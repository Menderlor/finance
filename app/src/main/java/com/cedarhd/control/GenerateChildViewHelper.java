package com.cedarhd.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.helpers.CityPicker;
import com.cedarhd.helpers.CityPicker.OnCheckedListener;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.DictIosMultiPicker;
import com.cedarhd.helpers.DictIosMultiPicker.OnMultiSelectedListener;
import com.cedarhd.helpers.DictIosPicker;
import com.cedarhd.helpers.DictionaryQueryDialogHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.DictSelectDialog;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Dict;
import com.cedarhd.models.SelectedProvince;
import com.cedarhd.models.字段描述;
import com.cedarhd.utils.LogUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 动态生成子控件
 * 
 * @author KJX
 * 
 */
public class GenerateChildViewHelper<T> {
	private List<字段描述> mList;
	private Context mContext;
	private LayoutInflater inflater;
	private ZLServiceHelper zlServiceHelper;
	private DictionaryQueryDialogHelper mDictDialogHelper;
	private DictIosPicker mDictIosPicker;
	private DictIosMultiPicker mDictIosMultiPicker;

	/** 保存所有 字典名称 集合 */
	private List<String> mDicNameList;
	/**
	 * 显示在页面上的所有文本框控件
	 */
	public List<EditText> mEtList;
	public List<TextView> mTextList;

	/** 多个字典列表 */
	private List<List<Dict>> mDictList; //

	/** 要显示实体的键值对 */
	private HashMap<String, Object> mFieldMap;
	private String dicts = "";
	private LinearLayout llLayout;
	private DateAndTimePicker dateAndTimePicker;
	private DigitsKeyListener numericOnlyListener;

	private CityPicker mCityPicker;
	private boolean mIsShowInfo; // 默认为false代表新建，true则代表显示value

	/** 根节点 */
	private int mRootLayoutId = 0;

	private final int SUCCESS_GET_DICTS = 101;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS_GET_DICTS:
				if (!((Activity) mContext).isFinishing()) {
					// 下载字典成功后，绑定监听事件
					// setOnDictClick();
					// 下载字典完成后，设置字典选择事件
					setOnItemDictClick();
				}
				break;
			default:
				break;
			}
		};
	};

	/**
	 * @param mList
	 *            根据字段描述表加载控件
	 * @param context
	 *            上下文
	 */
	public GenerateChildViewHelper(List<字段描述> mList, Context context) {
		this.mList = mList;
		this.mContext = context;
		inflater = LayoutInflater.from(context);
		dateAndTimePicker = new DateAndTimePicker(context);
		zlServiceHelper = new ZLServiceHelper();
		numericOnlyListener = new DigitsKeyListener(false, true);
		mDicNameList = new ArrayList<String>();
		mEtList = new ArrayList<EditText>();
		mTextList = new ArrayList<TextView>();
		mFieldMap = new HashMap<String, Object>();
		mDictDialogHelper = DictionaryQueryDialogHelper.getInstance(context);
		mDictIosPicker = new DictIosPicker(context);
		mDictIosMultiPicker = new DictIosMultiPicker(context);

		mCityPicker = CityPicker.getInstance(mContext);
	}

	/**
	 * 
	 * @param mList
	 * @param t
	 *            要显示的内容实体
	 * @param context
	 */
	public GenerateChildViewHelper(List<字段描述> mList, T t, Context context) {
		this(mList, context);
		mIsShowInfo = true;
		initObjProperties(t);
	}

	/** 设置要显示的根布局,如果设置了，字典选择为ios底部风格 */
	public void setmRootLayoutId(int mRootLayoutId) {
		this.mRootLayoutId = mRootLayoutId;
	}

	/**
	 * 初始化对象实体，保存到HashMap
	 * 
	 * @param t
	 */
	private void initObjProperties(T t) {
		Class c = t.getClass();
		Field[] fields = c.getDeclaredFields();
		LogUtils.i("fieldKeyValue", c.getName() + "--->" + fields.length);
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String name = field.getName(); // 获取属性名
			Object value;
			try {
				value = field.get(t);
				LogUtils.i("fieldKeyValue", name + "--" + value);
				if (value != null) {
					mFieldMap.put(name, value);
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				LogUtils.i("fieldKey", "" + e);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				LogUtils.i("fieldKey", "" + e);
			}
		}
	}

	/** 下载字典 */
	private void downLoadDict() {
		if (dicts.endsWith(",")) {
			LogUtils.i("dicts", dicts);
			dicts = dicts.substring(0, dicts.length() - 1);
			new Thread(new Runnable() {
				@Override
				public void run() {
					mDictList = zlServiceHelper.GetDicts(dicts);
					if (mDictList != null && mDictList.size() > 0) {
						handler.sendEmptyMessage(SUCCESS_GET_DICTS);
					}
				}
			}).start();
		}
	}

	/**
	 * 动态加载 生成页面
	 */
	public void addChildViews(LinearLayout llLayout) {
		LogUtils.i("addChildViews", "xxxxxxxxx:" + mList.size());
		this.llLayout = llLayout;

		initDictNames();
		showFieldInfo();
		downLoadDict();
	}

	/**
	 * 显示字段表内容
	 */
	private void showFieldInfo() {
		for (int i = 0; i < mList.size(); i++) {
			final 字段描述 item字段描述 = mList.get(i);
			View chiView = inflater.inflate(R.layout.item_ll_control, null);
			TextView tvName = (TextView) chiView
					.findViewById(R.id.tv_name_control);
			tvName.setVisibility(View.VISIBLE);
			EditText etValue = (EditText) chiView
					.findViewById(R.id.et_value_control);
			TextView tvId = (TextView) chiView.findViewById(R.id.tv_id_control);
			ImageView ivCall = (ImageView) chiView
					.findViewById(R.id.iv_call_control);
			ImageView ivMsg = (ImageView) chiView
					.findViewById(R.id.iv_message_control);
			etValue.setTag(item字段描述); // 保存对应的字段描述
			final String 字段名 = (TextUtils.isEmpty(item字段描述.字段显示名) ? item字段描述.字段名
					: item字段描述.字段显示名)
					+ "";
			mEtList.add(etValue);
			mTextList.add(tvId);
			tvName.setText(字段名 + "");
			if (mIsShowInfo) {
				if (字段名.contains("手机") || 字段名.contains("电话")) {
					ivCall.setVisibility(View.VISIBLE);
					ivMsg.setVisibility(View.VISIBLE);
					ivCall.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String phone = (String) getPropertyValue(item字段描述.字段名);
							Intent intent = new Intent();
							intent.setAction(Intent.ACTION_DIAL);
							intent.setData(Uri.parse("tel:" + phone));
							mContext.startActivity(intent);
						}
					});

					ivMsg.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String phone = (String) getPropertyValue(item字段描述.字段名);
							Intent intent = new Intent(Intent.ACTION_SENDTO,
									Uri.parse("smsto:" + phone));
							intent.putExtra("sms_body", "");
							mContext.startActivity(intent);

						}
					});
				} else if (字段名.contains("座机")) {
					ivCall.setVisibility(View.VISIBLE);
					ivCall.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String phone = (String) getPropertyValue(item字段描述.字段名);
							Intent intent = new Intent();
							intent.setAction(Intent.ACTION_DIAL);
							intent.setData(Uri.parse("tel:" + phone));
							mContext.startActivity(intent);
						}
					});
				}
			}
			setTextValue(etValue, tvId, item字段描述, false); // 设置显示内容
			llLayout.addView(chiView);
		}
	}

	/** 初始化字典名称集合 */
	private void initDictNames() {
		for (int i = 0; i < mList.size(); i++) {
			字段描述 item = mList.get(i);
			int inputType = item.输入类型;
			switch (inputType) {
			case 6:
			case 7:
			case 8: // 字典类型
				if (isCityDict(item.字典)) {
					// 省市县不做处理
				} else {
					dicts += item.字典 + ",";
					mDicNameList.add(item.字典); // 保存字典名称
				}
				break;
			}
		}
	}

	/** 字典名称是否包括省市县 */
	private boolean isCityDict(String dictName) {
		return "省".equals(dictName) || "市".equals(dictName)
				|| "县".equals(dictName);
	}

	private void setOnItemDictClick() {
		for (int i = 0; i < mList.size(); i++) {
			字段描述 item = mList.get(i);
			EditText etValue = mEtList.get(i);
			TextView tvId = mTextList.get(i);
			setOnClick(etValue, tvId, item);// 绑定监听
			setTextValue(etValue, tvId, item, true); // 设置显示内容
		}
	}

	private void setOnClick(final EditText etValue, final TextView tvText,
			final 字段描述 item) {
		int inputType = item.输入类型;

		switch (inputType) {
		case 6:
		case 7:
			setDictOnClick(etValue, tvText, item);
			break;
		case 8: // 字典类型(多选)
			setMultiDictOnClick(etValue, tvText, item);
			break;
		case 5:
		case 4:// 时间类型
			etValue.setFocusable(false);
			etValue.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!TextUtils.isEmpty(item.Format)
							&& item.Format.equals("yyyy-MM-dd")) {
						dateAndTimePicker.showDateWheel("选择日期", etValue, false);
					} else {
						dateAndTimePicker.showDateWheel("选择日期", etValue);
					}
				}
			});
			break;
		case 3: // bool
			// etValue.setInputType(EditorIn);
			break;
		case 2: // 数字类型
			etValue.setKeyListener(numericOnlyListener);
			break;
		default:
			break;
		}
	}

	private void setDictOnClick(final EditText etValue, final TextView tvText,
			final 字段描述 item) {
		etValue.setFocusable(false);
		final int pos = mDicNameList.indexOf(item.字典);
		if (isCityDict(item.字典) && item.只读 != 1) {
			etValue.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 城市选择
					// 字典项监听事件另作处理
					mCityPicker.show();
					mCityPicker.setOnCheckedListener(new OnCheckedListener() {
						@Override
						public void onChecked(SelectedProvince selectedCity) {
							if (selectedCity != null) {
								updateCity(selectedCity);
							}
						}
					});
				}
			});
		} else {// 普通字典选择项
			etValue.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mRootLayoutId == 0) {
						new DictSelectDialog(mContext, mDictList.get(pos),
								etValue, tvText, pos).showDialog();
					} else {
						final List<Dict> dicts = mDictList.get(pos);
						mDictIosPicker.show(mRootLayoutId, dicts, "名称");
						mDictIosPicker
								.setOnSelectedListener(new DictIosPicker.OnSelectedListener() {
									@Override
									public void onSelected(int index) {
										if (index >= 0 && index < dicts.size()) {
											Dict dict = dicts.get(index);
											tvText.setText(dict.编号 + "");
											etValue.setText(dict.名称);
										}
									}
								});
					}
				}
			});
		}
	}

	private void setMultiDictOnClick(final EditText etValue,
			final TextView tvText, final 字段描述 item) {
		etValue.setFocusable(false);
		final int pos = mDicNameList.indexOf(item.字典);
		if (isCityDict(item.字典) && item.只读 != 1) {
		} else {// 普通字典选择项
			etValue.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final List<Dict> dicts = mDictList.get(pos);
					mDictIosMultiPicker.show(mRootLayoutId, dicts, "名称");
					mDictIosMultiPicker
							.setOnMultiSelectedListener(new OnMultiSelectedListener() {
								@Override
								public void onSelected(Set<Integer> selectPosSet) {
									List<Dict> selectDicts = new ArrayList<Dict>();
									String selectedIds = "";
									String selectedNames = "";
									for (int selectedPos : selectPosSet) {
										if (selectedPos >= 0
												&& selectedPos < dicts.size()) {
											Dict selectedDict = dicts
													.get(selectedPos);
											selectDicts.add(selectedDict);
											selectedIds += selectedDict.编号
													+ ",";
											selectedNames += selectedDict.名称
													+ ",";
										}
									}

									if (selectedIds.endsWith(",")
											&& selectedIds.endsWith(",")) {
										selectedIds = selectedIds.substring(0,
												selectedIds.length() - 1);
										selectedNames = selectedNames.substring(
												0, selectedNames.length() - 1);
									}
									tvText.setText(selectedIds + "");
									etValue.setText(selectedNames);
								}
							});
				}
			});
		}
	}

	/**
	 * 设置每一项Edittext显示的值
	 * 
	 * @param etValue
	 * @param item
	 * @param isShowDictType
	 *            是否显示字典类型
	 */
	private void setTextValue(final EditText etValue, final TextView tvText,
			字段描述 item, boolean isShowDictType) {
		int inputType = item.输入类型;
		String fieldName = item.字段名;
		Object val = getPropertyValue(fieldName);

		if (val == null) {
			LogUtils.i("ziduan", inputType + "---" + fieldName + "---"
					+ item.字段显示名);
			if (inputType == 8 || inputType == 7 || inputType == 6) {// 字典类型
				if ("创建人".equals(fieldName) || "业务员".equals(fieldName)) {
					tvText.setText(Global.mUser.Id + "");
					etValue.setText(Global.mUser.UserName + "");
				}
			} else {
				if ("创建时间".equals(fieldName) || "登记时间".equals(fieldName)) {
					etValue.setText(ViewHelper.getDateString());
				}
			}
			return;
		}

		LogUtils.i("TextVal", fieldName + "--" + val.toString());
		// LogUtils.i("TextVal", val.toString());
		switch (inputType) {
		case 6:
		case 7:
		case 8:
			if (isShowDictType && mDictList != null & mDictList.size() > 0) {// 字典类型
				if (isCityDict(item.字典)) {
					if (val != null) {
						tvText.setText(val + "");
						String fieldValue = getPropertyValue(fieldName + "名")
								+ "";
						fieldValue = TextUtils.isEmpty(fieldValue) ? ""
								: fieldValue;
						etValue.setText(fieldValue);
					}
				} else {
					final int pos = mDicNameList.indexOf(item.字典);
					List<Dict> mDicts = mDictList.get(pos);// 取得指定字典名的字典表
					int dictId = 0;
					try {
						String dictValue = val.toString();
						LogUtils.i("dictValue9", dictValue);
						dictId = Integer.parseInt(dictValue);
						LogUtils.i("valDict", val.toString() + "---" + dictId);
						Dict dict = getDict(dictId, mDicts);
						if (dict != null) {
							tvText.setText(dict.编号 + "");
							etValue.setText(dict.名称 + "");
						}
					} catch (NumberFormatException ex) {
						if (inputType == 8) {// 多选类型
							//
							String[] dictValueArr = val.toString().split(",");
							StringBuilder sbIds = new StringBuilder();
							StringBuilder sbValues = new StringBuilder();
							for (int i = 0; i < dictValueArr.length; i++) {
								int dictKey = Integer.parseInt(dictValueArr[i]);
								Dict dict = getDict(dictKey, mDicts);
								sbIds.append(dict.编号 + ",");
								sbValues.append(dict.名称 + ",");

							}

							if (sbIds.length() > 0 && sbValues.length() > 0) {
								tvText.setText(sbIds.toString().substring(0,
										sbIds.length() - 1));
								etValue.setText(sbValues.toString().substring(
										0, sbValues.length() - 1));
							}
						}
					} catch (Exception e) {
						LogUtils.i("erro", "" + e);

					}
				}

			}
			break;
		case 5:
		case 4:// 时间类型
				// break;
		case 3: // bool

			// break;
		case 2: // 数字类型
			// LogUtils.i("valValue", val + "");
			// break;
		default:
			LogUtils.i("valValue", val + "");
			if (val != null) {
				etValue.setText(val.toString());
			}
			break;
		}
	}

	/**
	 * 根据实体的属性名获得实体属性的value
	 * 
	 * 
	 * @param fieldName
	 *            根据字段名称，如客户实体的 [名称]属性
	 * @return 字段值 客户实体的 [名称]属性的值
	 */
	private Object getPropertyValue(String fieldName) {
		Object val = null;
		Iterator iter = mFieldMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			if (key.equals(fieldName)) {
				val = entry.getValue();
				break;
			}
		}
		return val;
	}

	/**
	 * 查询指定字典项
	 * 
	 * @param id
	 *            字典项编号
	 * @param mDicts
	 *            字典表
	 * @return
	 */
	private Dict getDict(int id, List<Dict> mDicts) {
		Dict dict = null;
		for (int i = 0; i < mDicts.size(); i++) {
			if (id == mDicts.get(i).编号) {
				dict = mDicts.get(i);
				LogUtils.i("dictInfo", "取到：" + dict.名称 + "---" + dict.编号);
				return dict;
			}
		}
		return dict;
	}

	/**
	 * 获得所有EditText控件
	 * 
	 * @return
	 */
	public List<EditText> getAllEtList() {
		return mEtList;
	}

	/**
	 * 获得所有EditText控件
	 * 
	 * @return
	 */
	public List<TextView> getAllTextList() {
		return mTextList;
	}

	/***
	 * 更新省市县
	 * 
	 * @param selectedCity
	 */
	private void updateCity(SelectedProvince selectedCity) {
		for (int i = 0; i < mEtList.size(); i++) {
			EditText etValue = mEtList.get(i);
			TextView tvId = mTextList.get(i);
			字段描述 form = (字段描述) etValue.getTag();
			if (form != null && isCityDict(form.字典)) {
				if ("省".equals(form.字段名) && selectedCity.省 != null) {
					etValue.setText(selectedCity.省.名称);
					tvId.setText(selectedCity.省.编号 + "");
				} else if ("市".equals(form.字段名) && selectedCity.市 != null) {
					etValue.setText(selectedCity.市.名称);
					tvId.setText(selectedCity.市.编号 + "");
				} else if ("县".equals(form.字段名) && selectedCity.县 != null) {
					etValue.setText(selectedCity.县.名称);
					tvId.setText(selectedCity.县.编号 + "");
				}
			}
		}
	}
}
