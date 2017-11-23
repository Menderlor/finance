package com.cedarhd.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.rad.Rad客户;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

/** 新建客户 */
public class RadAddClientDialog {

	private Context mContext;
	private Dialog dialog;

	public RadAddClientDialog(Context mContext) {
		super();
		this.mContext = mContext;

		dialog = new Dialog(mContext, R.style.styleNoFrameDialog);
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_rad_add_client, null);
		initViews(view);
		dialog.setContentView(view);
		dialog.setCancelable(false);
	}

	public void show() {
		dialog.show();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int) (ViewHelper.getScreenWidth(mContext)); // 设置宽度
		lp.gravity = Gravity.BOTTOM;
		dialog.getWindow().setAttributes(lp);
	}

	private void initViews(View view) {
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
				dialog.dismiss();
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
					dialog.dismiss();
				}
			}
		});
	}

	private void saveClient(final Rad客户 client) {
		ProgressDialogHelper.show(mContext);
		String url = Global.BASE_URL + "SltRad/saveClient";
		StringRequest.postAsyn(url, client, new StringResponseCallBack() {

			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				if (mOnSaveSuccessedListener != null) {
					try {
						String result = JsonUtils.getStringValue(response,
								"Data");
						result = StrUtils.removeRex(result);
						int clientId = Integer.parseInt(result);
						client.编号 = clientId;
						mOnSaveSuccessedListener.onSaved(client);

						dialog.dismiss();
					} catch (Exception e) {
						LogUtils.e("erro", e + "");
						mOnSaveSuccessedListener.onErro();
					}
				}
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
			}
		});
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
