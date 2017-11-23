package com.cedarhd.biz;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.helpers.CityPicker;
import com.cedarhd.helpers.CityPicker.OnCheckedListener;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.DateAndTimePicker.ISelected;
import com.cedarhd.helpers.DictionaryQueryDialogHelper;
import com.cedarhd.helpers.DictionaryQueryDialogHelper.OnSelectedListener;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.SelectedProvince;
import com.cedarhd.models.changhui.表单字段;
import com.cedarhd.models.字典;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.RegexUtils;
import com.cedarhd.utils.StrUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 长汇客户逻辑处理 create 2016-1-27 参考 GenerateChildViewHelper
 */
public class ChClientBiz {

    private final String TAG = "ChClientBiz";
    private Context mContext;
    /**
     * 相关联的fragment
     */
    private Fragment mRelateFragment;

    private DictionaryQueryDialogHelper mDictDialogHelper;
    private DateAndTimePicker mDateAndTimePicker;
    private List<表单字段> mFormList;
    // 右侧内容输入框 使用Tag保存内容
    private List<EditText> mEditTexts;
    private LinearLayout mRootLayout;// 根布局
    private LayoutInflater mInflater;
    private CityPicker mCityPicker;
    private boolean isNewClient;

    public ChClientBiz(Context context, List<表单字段> mFormList,
                       LinearLayout mRootLayout, boolean isNewClient) {
        super();
        this.mContext = context;
        this.mFormList = mFormList;
        this.mRootLayout = mRootLayout;
        mInflater = LayoutInflater.from(mContext);
        mDictDialogHelper = DictionaryQueryDialogHelper.getInstance(mContext);
        mDateAndTimePicker = new DateAndTimePicker(mContext);
        mEditTexts = new ArrayList<EditText>();
        mCityPicker = CityPicker.getInstance(mContext);
        this.isNewClient = isNewClient;
    }

    /**
     * 生成控件
     */
    public void generateViews() {
        for (表单字段 form : mFormList) {
            LinearLayout childView = (LinearLayout) mInflater.inflate(
                    R.layout.item_control_ch_client, null);
            TextView tvRequired = (TextView) childView
                    .findViewById(R.id.tv_required_control_ch_client);
            TextView tvStatus = (TextView) childView
                    .findViewById(R.id.tv_statuts_control_ch_client);
            TextView tvTitle = (TextView) childView
                    .findViewById(R.id.tv_title_control_ch_client);
            EditText etValue = (EditText) childView
                    .findViewById(R.id.et_value_control_ch_client);
            // 保存字典类型编号
            TextView tvId = (TextView) childView
                    .findViewById(R.id.tv_id_control_ch_client);
            etValue.setTag(form); // 绑定数据
            if (!isNewClient) {
                etValue.setEnabled(false);
            }
            if ("编号".equals(form.DisplayName)) {  //隐藏编号单元格
                childView.setVisibility(View.GONE);
            }
            setTitleText(form, tvTitle);
            initRequired(form, tvRequired);
            initColorStatus(form, tvStatus);
            setReadOnlyStyle(childView, etValue);
            setEditTextValue(etValue);
            setOnEvent(etValue);
            mEditTexts.add(etValue);
            mRootLayout.addView(childView);
        }
    }

    public void setRelateFragment(Fragment fragment) {
        this.mRelateFragment = fragment;
    }

    /***
     * 设置左侧标题显示
     *
     * @param form
     * @param tvTitle
     */
    private void setTitleText(表单字段 form, TextView tvTitle) {
        tvTitle.setText(StrUtils.pareseNull(form.DisplayName));
    }

    private void setEditTextValue(final EditText etValue) {
        final 表单字段 form = (表单字段) etValue.getTag();
        etValue.setText(StrUtils.pareseNull(form.DicText));
        String dataType = form.DataType;

        if (isTextType(dataType) || isNumberType(dataType)) {
            // 如果是文本类型或数值类型 直接显示
            etValue.setText(StrUtils.pareseNull(form.Value));
        } else if (isDateTimeSelectType(dataType)) {
            if (!TextUtils.isEmpty(form.Format)
                    && !TextUtils.isEmpty(form.Value)) {
                try {
                    Date date = ViewHelper.formatStrToDate(form.Value);
                    String value = ViewHelper.getDateString(date, form.Format);
                    etValue.setText(value);
                } catch (Exception e) {
                    e.printStackTrace();
                    etValue.setText(form.Value);
                }
            }
        } else if (isDictSelectType(dataType)) {
            etValue.setText(StrUtils.pareseNull(form.DicText));
        }
    }

    private void setOnEvent(final EditText etValue) {
        final 表单字段 form = (表单字段) etValue.getTag();
        String dataType = form.DataType;
        boolean readOnly = form.ReadOnly;

        if (readOnly) {
            // 只读类型 EditText不可编辑
            etValue.setEnabled(false);
            return;
        }

        if (isTextType(dataType) || isNumberType(dataType)) {
            // 输入类型
            if (isNumberType(dataType)) {
                // 设置输入类型为 Number
                etValue.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        } else {// 不可输入
            etValue.setFocusable(false);
            if ("combobox".equalsIgnoreCase(form.DataType)) {
                if (isPrivinceType(form.Name)) {
                    etValue.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCityPicker.show();
                            mCityPicker
                                    .setOnCheckedListener(new OnCheckedListener() {
                                        @Override
                                        public void onChecked(
                                                SelectedProvince selectedCity) {
                                            if (selectedCity != null) {
                                                updateCity(selectedCity);
                                            }
                                        }
                                    });
                        }
                    });
                } else if (isProductType(form.Name)) {
                    // 选择产品
                    etValue.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mRelateFragment != null) {
                                ChProductBiz.selectProduct(mRelateFragment);
                            }
                        }
                    });
                } else {
                    etValue.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDictDialogHelper.show(form.DicTableName);
                            mDictDialogHelper
                                    .setOnSelectedListener(new OnSelectedListener() {
                                        @Override
                                        public void onSelected(字典 dict) {
                                            form.Value = dict.Id + "";
                                            form.DicText = dict.Name;
                                            setEditTextValue(etValue);
                                        }
                                    });
                        }
                    });
                }
            } else if (isDateTimeSelectType(dataType)) {
                etValue.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("yyyy-MM-dd".equals(form.Format)) {
                            mDateAndTimePicker.showDateWheel("选择"
                                    + form.DisplayName, etValue, false);
                            mDateAndTimePicker
                                    .setOnSelectedListener(new ISelected() {
                                        @Override
                                        public void onSelected(String dateStr) {
                                            try {
                                                Date date = ViewHelper
                                                        .formatStrToDate(dateStr);
                                                String value = ViewHelper
                                                        .getDateString(date,
                                                                form.Format);
                                                etValue.setText(value);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                etValue.setText(form.Value);
                                            }
                                        }
                                    });
                        } else {
                            mDateAndTimePicker.showDateWheel("选择"
                                    + form.DisplayName, etValue);
                        }
                    }
                });
            } else if (isBooleanType(dataType)) {
                etValue.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDictDialogHelper.show(form.DicTableName);
                        mDictDialogHelper
                                .setOnSelectedListener(new OnSelectedListener() {
                                    @Override
                                    public void onSelected(字典 dict) {
                                        form.Value = dict.Id + "";
                                        form.DicText = dict.Name;
                                        setEditTextValue(etValue);
                                    }
                                });
                    }
                });
            }
        }

    }

    /**
     * 设置只读类型的样式
     *
     * @param llLayout
     * @param etValue
     */
    private void setReadOnlyStyle(final LinearLayout llLayout,
                                  final EditText etValue) {
        final 表单字段 form = (表单字段) etValue.getTag();
        boolean readOnly = form.ReadOnly;
        if (readOnly) {
            llLayout.setBackgroundColor(mContext.getResources().getColor(
                    R.color.bg_readonly));
        }

    }

    /**
     * 初始化状态色块
     *
     * @param form     表单实体
     * @param tvStatus 色块
     */
    private void initColorStatus(表单字段 form, TextView tvStatus) {
        if (TextUtils.isEmpty(form.Color)) {
            tvStatus.setVisibility(View.GONE);
        } else {
            tvStatus.setVisibility(View.VISIBLE);
            int bgColor = Color.parseColor(form.Color);
            tvStatus.setBackgroundColor(bgColor);
        }
    }

    /**
     * 设置初始化 必填项红色※是否显示
     */
    private void initRequired(表单字段 form, TextView tvRequired) {
        // 必填红色※
        if (form.Required) {
            tvRequired.setVisibility(View.VISIBLE);
        } else {
            tvRequired.setVisibility(View.GONE);
        }
    }

    /***
     * 判断类型是否是文本类型
     *
     * @param dataType
     * @return
     */
    private boolean isTextType(String dataType) {
        return "string".equalsIgnoreCase(dataType);
    }

    /***
     * 判断类型是否是日期选择类型本类型
     *
     * @param dataType
     * @return
     */
    private boolean isDateTimeSelectType(String dataType) {
        return "datetime".equalsIgnoreCase(dataType);
    }

    /***
     * 判断类型是否是数值类型
     *
     * @param dataType
     * @return
     */
    private boolean isNumberType(String dataType) {
        return "int32".equalsIgnoreCase(dataType)
                || "double".equalsIgnoreCase(dataType);
    }

    /***
     * 判断类型是否是字典选择类型
     *
     * @param dataType
     * @return
     */
    private boolean isDictSelectType(String dataType) {
        return "multiselect".equalsIgnoreCase(dataType)
                || "combobox".equalsIgnoreCase(dataType);
    }

    /***
     * 判断类型是否是bool类型
     *
     * @param dataType
     * @return
     */
    private boolean isBooleanType(String dataType) {
        return "boolean".equalsIgnoreCase(dataType);
    }

    /***
     * 判断类型是否是省市县选择类型
     *
     * @param 表单名称
     * @return
     */
    private boolean isPrivinceType(String formName) {
        return "省".equals(formName) || "市".equals(formName)
                || "县".equals(formName);
    }

    /***
     * 判断类型是否选择产品
     *
     * @param 表单名称
     * @return
     */
    public boolean isProductType(String formName) {
        return "意向产品".equals(formName);
    }

    public ArrayList<表单字段> getFormList() {
        ArrayList<表单字段> formList = new ArrayList<表单字段>();

        for (EditText etValue : mEditTexts) {
            表单字段 form = (表单字段) etValue.getTag();
            if ("string".equalsIgnoreCase(form.DataType)
                    || "datetime".equalsIgnoreCase(form.DataType)) {
                form.Value = etValue.getText().toString();
            }
            formList.add(form);
        }
        return formList;
    }

    /***
     * 返回系统控件
     *
     * @return
     */
    public List<EditText> getEditList() {
        return mEditTexts;
    }

    /**
     * 空校验
     */
    public static String checkNull(List<表单字段> formList) {
        for (表单字段 form : formList) {
            if (form.Required) {
                if (TextUtils.isEmpty(form.Value)) {
                    return form.TypeName + "分类中的 " + form.DisplayName + "不能为空";
                }
            }
        }
        return "";
    }

    /**
     * 正则公式校验
     */
    public static String checkRegEx(List<表单字段> formList) {
        for (表单字段 form : formList) {
            if (!TextUtils.isEmpty(form.Value)
                    && !TextUtils.isEmpty(form.RegEx)) {
                String regStr = form.RegEx;
                String[] regArr = {form.RegEx};
                if (form.RegEx.contains("!errorMsg!")) {
                    regArr = form.RegEx.split("!errorMsg!");
                }

                if (regArr != null && regArr.length > 0) {
                    regStr = regArr[0];
                }
                LogUtils.i("REGX", form.Value + "-----" + regStr);
                if (!RegexUtils.regex(form.Value, regStr)) {
                    if (regArr.length > 1) {
                        return form.DisplayName + "格式非法," + regArr[1];
                    }
                    return form.DisplayName + "格式非法";
                }
            }
        }
        return "";
    }

    /**
     * 如果证件类别是身份证，需要正则校验身份证号
     */
    public static String checkCardRegEx(List<表单字段> formList) {
        String cardType = "";
        String cardNo = "";
        for (表单字段 form : formList) {
            if (!TextUtils.isEmpty(form.Value)) {
                if ("证件类别".equals(form.Name)) {
                    cardType = form.Value;
                }

                if ("证件号".equals(form.Name)) {
                    cardNo = form.Value;
                }
            }
        }

        if (!TextUtils.isEmpty(cardNo) && !TextUtils.isEmpty(cardType)) {
            try {
                int type = Integer.parseInt(cardType);
                if (type == 1) {
                    if (!RegexUtils.isIdCard(cardNo)) {
                        return "非法的查身份证格式，请修改后再次提交";
                    }
                }
            } catch (Exception e) {
            }

        }
        return "";
    }

    /***
     * 更新省市县
     *
     * @param selectedCity
     */
    private void updateCity(SelectedProvince selectedCity) {
        for (EditText etValue : mEditTexts) {
            表单字段 form = (表单字段) etValue.getTag();
            if (form != null && isPrivinceType(form.Name)) {
                if ("省".equals(form.Name) && selectedCity.省 != null) {
                    form.Value = selectedCity.省.编号 + "";
                    form.DicText = selectedCity.省.名称;
                    etValue.setText(selectedCity.省.名称);
                } else if ("市".equals(form.Name) && selectedCity.市 != null) {
                    form.Value = selectedCity.市.编号 + "";
                    form.DicText = selectedCity.市.名称;
                    etValue.setText(selectedCity.市.名称);
                } else if ("县".equals(form.Name) && selectedCity.县 != null) {
                    form.Value = selectedCity.县.编号 + "";
                    form.DicText = selectedCity.县.名称;
                    etValue.setText(selectedCity.县.名称);
                }
            }
        }
    }
}
