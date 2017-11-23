package com.cedarhd;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.helpers.DictIosPickerBottomDialog;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.utils.LogUtils;

import java.util.HashMap;

/***
 * 嵌入网页页面
 * 
 * @author K
 * 
 */
public class WebviewNormalActivity extends BaseActivity {

	public static final String EXTRA_URL = "extral_url_webview_normal";
	public static final String EXTRA_TITLE = "extral_title_webview_normal";
	private Context mContext;
	private WebView webView;
	private BoeryunHeaderView headerView;
	private DictIosPickerBottomDialog mDictIosPickerBottomDialog;
	private String mUrl;
	private String mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_activity);
		intData();
		initView();
		loadWebView();
		setOnEvent();
	}

	private void intData() {
		mContext = this;
		mDictIosPickerBottomDialog = new DictIosPickerBottomDialog(mContext);
		mUrl = getIntent().getStringExtra(EXTRA_URL);
		mTitle = getIntent().getStringExtra(EXTRA_TITLE);
		LogUtils.i("url", mUrl);
	}

	private void setOnEvent() {
		headerView.setOnButtonClickListener(new OnButtonClickListener() {

			@Override
			public void onClickSaveOrAdd() {

			}

			@Override
			public void onClickFilter() {

			}

			@Override
			public void onClickBack() {
				/*mDictIosPickerBottomDialog.show("返回上级页面");
				mDictIosPickerBottomDialog
						.setOnSelectedListener(new OnSelectedListener() {
							@Override
							public void onSelected(int index) {
								if (index == 0) {
									finish();
								}
							}
						});*/
				finish();
			}
		});
	}

	private void initView() {
		webView = (WebView) findViewById(R.id.webview_webview);
		headerView = (BoeryunHeaderView) findViewById(R.id.header_webview);

		if (!TextUtils.isEmpty(mTitle)) {
			headerView.setTitle(mTitle);
		}
	}

	@Override
	// 设置回退
	// 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack(); // goBack()表示返回WebView的上一页面
			return true;
		}
		return false;
	}

	/**
	 * 浏览器模式加载表单
	 */
	private void loadWebView() {
		// requestFocusForForm();
		// textViewTitle.setText(typeName);
		// 相当于打开浏览器对象
		// 为25%，最小缩放等级，将页面所有内容显示在手机屏幕上
		webView.setInitialScale(25);
		// 得到浏览器设置
		WebSettings webSettings = webView.getSettings();
		// 设置浏览器中javascript可执行
		webSettings.setJavaScriptEnabled(true);
		// 设置webview推荐使用的窗口
		webSettings.setUseWideViewPort(true);
		// 置webview加载的页面的
		webSettings.setLoadWithOverviewMode(true);
		// 设置缩放控制
		webSettings.setBuiltInZoomControls(true);
		// 支持缩放
		webSettings.setSupportZoom(true);
		// 缩放按钮
		webSettings.setBuiltInZoomControls(true);

		LogUtils.i("url_webview", mUrl);
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("passport", Global.mUser.Passport);
		webView.loadUrl(mUrl, headers);

		webView.setWebViewClient(new WebViewClient() {
			// 点击网页中按钮时，在原页面打开
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				ProgressDialogHelper.show(mContext, true);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				// pBar.setVisibility(View.GONE);
				ProgressDialogHelper.dismiss();
			}
		});
	}
}
