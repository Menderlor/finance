package com.cedarhd.widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.cedarhd.R;

/***
 * 自定义符合系统风格的对话框 包括取消，确定
 *
 * @author K
 *
 */
public class BoeryunDialog {

    private Context mContext;
    private boolean isCanceable;

    /**
     * 提示文字标题
     */
    private String mTitle;

    /**
     * 提示文字内容
     */
    private String mMeassage;

    /**
     * 确定标题的文字，默认为‘取消’
     */
    private String mNegTitle = "取消";

    /**
     * 确定标题的文字，默认为‘确认’
     */
    private String mPosTitle = "确认";

    /**
     * @param mContext
     */
    public BoeryunDialog(Context mContext) {
        this.mContext = mContext;
        this.isCanceable = true;
    }

    /**
     * @param mContext
     * @param isCanceable
     */
    public BoeryunDialog(Context mContext, boolean isCanceable) {
        this.mContext = mContext;
        this.isCanceable = isCanceable;
    }

    /**
     * @param mContext    当前上下文
     * @param isCanceable 点击返回是否取消对话框
     * @param mTitle      提示标题
     * @param mMeassage   提示内容
     * @param mNegTitle   取消按钮的文字
     * @param mPosTitle   确定按钮的文字
     */
    public BoeryunDialog(Context mContext, boolean isCanceable, String mTitle,
                         String mMeassage, String mNegTitle, String mPosTitle) {
        this.mContext = mContext;
        this.isCanceable = isCanceable;
        this.mTitle = mTitle;
        this.mMeassage = mMeassage;
        this.mNegTitle = TextUtils.isEmpty(mNegTitle) ? "取消" : mNegTitle;
        this.mPosTitle = TextUtils.isEmpty(mPosTitle) ? "确定" : mPosTitle;
    }

    public boolean isCanceable() {
        return isCanceable;
    }

    public void setCanceable(boolean isCanceable) {
        this.isCanceable = isCanceable;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmMeassage() {
        return mMeassage;
    }

    public void setmMeassage(String mMeassage) {
        this.mMeassage = mMeassage;
    }

    public String getmNegTitle() {
        return mNegTitle;
    }

    public void setmNegTitle(String mNegTitle) {
        this.mNegTitle = mNegTitle;
    }

    public String getmPosTitle() {
        return mPosTitle;
    }

    public void setmPosTitle(String mPosTitle) {
        this.mPosTitle = mPosTitle;
    }

    AlertDialog dialog;

    @SuppressLint("NewApi")
    public AlertDialog show() {
        dialog = new AlertDialog.Builder(mContext).create();
        View dialogView = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_boeryun, null);
        initView(dialogView);
        dialog.setView(dialogView, 0, 0, 0, 0);
        dialog.setCancelable(isCanceable);
        dialog.show();
        return dialog;
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void initView(View view) {
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title_dialog);
        TextView tvMsg = (TextView) view.findViewById(R.id.tv_message_dialog);
        TextView tvPos = (TextView) view.findViewById(R.id.tv_postive_dialog);
        TextView tvNeg = (TextView) view.findViewById(R.id.tv_negetive_dialog);

        tvTitle.setText(mTitle);
        tvMsg.setText(mMeassage);
        tvPos.setText(mPosTitle);

        if (TextUtils.isEmpty(mNegTitle)) {
            tvNeg.setVisibility(View.GONE);
        } else {
            tvNeg.setText(mNegTitle);
        }
        tvPos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDialogPostiveButtonClickListner != null) {
                    mOnDialogPostiveButtonClickListner.onClick();
                }
            }
        });

        tvNeg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDialogNegtiveButtonClickListner != null) {
                    mOnDialogNegtiveButtonClickListner.onClick();
                }
            }
        });
    }

    /***
     * 绑定按钮监听事件
     *
     * @param onDialogNegtiveButtonClickListner
     *            取消按钮监听
     * @param onDialogPostiveButtonClickListner
     *            确定按钮监听
     */
    public void setBoeryunDialogClickListener(
            OnBoeryunDialogClickListner onDialogNegtiveButtonClickListner,
            OnBoeryunDialogClickListner onDialogPostiveButtonClickListner) {
        this.mOnDialogPostiveButtonClickListner = onDialogPostiveButtonClickListner;
        this.mOnDialogNegtiveButtonClickListner = onDialogNegtiveButtonClickListner;
    }

    private OnBoeryunDialogClickListner mOnDialogPostiveButtonClickListner;
    private OnBoeryunDialogClickListner mOnDialogNegtiveButtonClickListner;

    public interface OnBoeryunDialogClickListner {
        public abstract void onClick();
    }
}
