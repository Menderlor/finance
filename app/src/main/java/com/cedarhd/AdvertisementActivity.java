package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.CheckVersionHelper;
import com.cedarhd.helpers.DictWheelPicker;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.utils.BrowserUtils;

/**
 * 联系我们（关于波尔云）
 * 
 * @author kjx
 * 
 */
public class AdvertisementActivity extends BaseActivity implements
		OnClickListener {
	private Context context;
	private CheckVersionHelper checkVersionHelper;
	private DictWheelPicker mDictWheelPicker;
	private LinearLayout llTel;
	private LinearLayout llUrl;
	private TextView tvVersion;
	private TextView tvTel;
	private TextView tvUrl;
	private ImageView ivCancel;
	private Button btnUpdate;// 升级

	// private View main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advertisement);
		findviews();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ll_tel_ad: // 拨打电话
		case R.id.tv_tel_ad:
			String telNo = tvTel.getText().toString();
			call(telNo);
			break;
		case R.id.ll_url_ad: // 打开浏览器
		case R.id.tv_url_ad:
			String url = tvUrl.getText().toString();
			BrowserUtils.openBrowser(context, url);
			break;
		case R.id.imageViewCancel_ad:
			finish();
			break;
		case R.id.btn_update_ad:
			// try {
			// // 版本更新
			// new Thread(checkVersionHelper.new CheckVersionTask()).start();
			// } catch (Exception e) {
			// Toast.makeText(context, "更新异常", Toast.LENGTH_SHORT).show();
			// }
			String[] cities = new String[] { "New York", "Washington",
					"Chicago", "Atlanta", "Orlando", "Washington", "Chicago",
					"Atlanta", "Washington", "Chicago", "Atlanta", "Orlando" };
			// mDictWheelPicker.showDateWheel(R.id.main_ad, cities);
			break;
		default:
			break;
		}
	}

	private void findviews() {
		context = AdvertisementActivity.this;
		checkVersionHelper = new CheckVersionHelper(context, false);
		mDictWheelPicker = new DictWheelPicker(context);
		llTel = (LinearLayout) findViewById(R.id.ll_tel_ad);
		llUrl = (LinearLayout) findViewById(R.id.ll_url_ad);
		tvVersion = (TextView) findViewById(R.id.tv_version_ad);
		tvTel = (TextView) findViewById(R.id.tv_tel_ad);
		tvUrl = (TextView) findViewById(R.id.tv_url_ad);
		ivCancel = (ImageView) findViewById(R.id.imageViewCancel_ad);
		btnUpdate = (Button) findViewById(R.id.btn_update_ad);

		// main = findViewById(R.id.main_ad);

		tvVersion.setText("版本号：" + ViewHelper.getVersionName(context));
		tvTel.setOnClickListener(this);
		llTel.setOnClickListener(this);
		tvUrl.setOnClickListener(this);
		llUrl.setOnClickListener(this);
		ivCancel.setOnClickListener(this);
		llUrl.setOnClickListener(this);
		btnUpdate.setOnClickListener(this);

	}

	/**
	 * 跳转拨号页面
	 * 
	 * @param number
	 *            电话号码
	 */
	private void call(String number) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + number));
		startActivity(intent);
	}

}
