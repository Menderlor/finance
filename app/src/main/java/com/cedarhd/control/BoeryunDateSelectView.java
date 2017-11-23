package com.cedarhd.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.ViewHelper;

/***
 * 日期选择控件,文本控件的形式显示内容，点击弹出字典选择页面，选择完毕将时间值显示TextView
 *
 * @author K
 *
 *         2015/09/29 15:12
 */
public class BoeryunDateSelectView extends TextView {

    private DateAndTimePicker mDateAndTimePicker;
    /**
     * 是否显示默认值
     */
    private boolean mIsDefaultValue;

    /**
     * 是否显示时分秒
     */
    private boolean mIsShowTime;

    /**
     * 是否年月日
     */
    private boolean mIsShowDate = true;

    public BoeryunDateSelectView(Context context) {
        this(context, null, 0);
    }

    public BoeryunDateSelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BoeryunDateSelectView(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
        // 初始化属性
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.BoeryunDateSelectView, defStyle, defStyle);
        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.BoeryunDateSelectView_defaultValue:
                    // 获取属性中 字典名称
                    mIsDefaultValue = typedArray.getBoolean(attr, false);
                    break;
                case R.styleable.BoeryunDateSelectView_isShowDate:
                    // 获取属性中 字典名称
                    mIsShowDate = typedArray.getBoolean(attr, false);
                    break;
                case R.styleable.BoeryunDateSelectView_isShowTime:
                    // 获取属性中 字典名称
                    mIsShowTime = typedArray.getBoolean(attr, false);
                    break;
                default:
                    break;
            }
        }

        initData(context);
        setOnClick();

        if (mIsDefaultValue) {
            if (mIsShowTime) {
                setText(ViewHelper.getDateString());
            } else {
                setText(ViewHelper.getDateToday());
            }
        }
    }

    private void initData(Context context) {
        mDateAndTimePicker = new DateAndTimePicker(context);
    }

    private void setOnClick() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateAndTimePicker.showDateWheel("选取时间",
                        BoeryunDateSelectView.this, mIsShowTime, mIsShowDate);
            }
        });
    }

}
