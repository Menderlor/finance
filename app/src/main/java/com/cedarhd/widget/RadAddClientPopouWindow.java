package com.cedarhd.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.models.rad.Rad客户;

/** 新建客户 */
public class RadAddClientPopouWindow {

	private Context mContext;

	public RadAddClientPopouWindow(Context mContext) {
		super();
		this.mContext = mContext;
	}

	/**
	 * 弹出IOS风格的底部字典选择
	 * 
	 * @param mainLayoutId
	 *            layout文件的根节点id
	 * @param datas
	 *            数据源
	 */
	public void show(int mainLayoutId) {
		View parentView = ((Activity) mContext).findViewById(mainLayoutId);
		View view = View
				.inflate(mContext, R.layout.dialog_rad_add_client, null);
		final PopupWindow popupWindow = new PopupWindow(view,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		initViews(view, popupWindow);
		initPopupWindow(parentView, popupWindow);
	}

	private void initViews(View view, PopupWindow popupWindow) {
		final Rad客户 client = new Rad客户();
		final EditText etName = (EditText) view
				.findViewById(R.id.et_name_rad_add_client_dialog);
		final EditText etAddress = (EditText) view
				.findViewById(R.id.et_address_rad_add_client_dialog);
		final EditText etPhone = (EditText) view
				.findViewById(R.id.et_phone_rad_add_client_dialog);
		TextView tvCancel = (TextView) view
				.findViewById(R.id.tv_cancle_rad_add_client_dialog);
		TextView tvSave = (TextView) view
				.findViewById(R.id.tv_save_rad_add_client_dialog);

		tvCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});

		tvSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				client.名称 = etName.getText().toString();
				client.地址 = etAddress.getText().toString();
				client.电话 = etPhone.getText().toString();

				if (TextUtils.isEmpty(client.名称)) {
					Toast.makeText(mContext, "请填写客户名称", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (TextUtils.isEmpty(client.地址)) {
					Toast.makeText(mContext, "请填写客户地址", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				if (TextUtils.isEmpty(client.电话)) {
					Toast.makeText(mContext, "请填写客户电话", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				// saveClient(client);
				if (mOnSaveSuccessedListener != null) {
					mOnSaveSuccessedListener.onSaved(client);
				}
			}
		});
	}

	private void initPopupWindow(View parentView, final PopupWindow popupWindow) {
		popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				setBackgroundAlpha(1);
			}
		});

		popupWindow.setAnimationStyle(R.style.AnimationFadeBottom);
		setBackgroundAlpha(0.5f);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		popupWindow.showAtLocation(parentView, Gravity.BOTTOM | Gravity.LEFT,
				0, 0);

	}

	/**
	 * 设置添加屏幕的背景透明度
	 * 
	 * @param bgAlpha
	 */
	public void setBackgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = ((Activity) mContext).getWindow()
				.getAttributes();
		lp.alpha = bgAlpha;// 0.0-1.0
		((Activity) mContext).getWindow().setAttributes(lp);
	}

	private OnSaveSuccessedListener mOnSaveSuccessedListener;

	public interface OnSaveSuccessedListener {
		void onSaved(Rad客户 client);

		void onErro();
	}

	public void setOnSaveSuccessedListener(
			OnSaveSuccessedListener onSaveSuccessedListener) {
		this.mOnSaveSuccessedListener = onSaveSuccessedListener;
	}
}
