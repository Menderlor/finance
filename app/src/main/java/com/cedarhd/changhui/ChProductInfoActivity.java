package com.cedarhd.changhui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.LinearLayout;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.utils.BrowserUtils;
import com.cedarhd.utils.LogUtils;

import java.util.HashMap;

/***
 * 长汇产品详情
 * 
 * @author K
 * 
 */
public class ChProductInfoActivity extends BaseActivity {

	public static final String EXTRA_PRODUCT_ID = "product_id";
	private Context mContext;

	private BoeryunHeaderView headerView;
	private WebView webview_root;
	private LinearLayout llRoot;
	private HashMap<String, String> mHeaderMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ch_product_info);
		initViews();
	}

	@Override
	protected void onStart() {
		super.onStart();
		initData();
		setOnEvent();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		webview_root.destroy();
	}

	private void initViews() {
		headerView = (BoeryunHeaderView) findViewById(R.id.header_ch_product_info);

		llRoot = (LinearLayout) findViewById(R.id.ll_root_ch_product_info);
		webview_root = new WebView(getApplicationContext());
		llRoot.addView(webview_root, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
	}

	private void initData() {
		mContext = ChProductInfoActivity.this;
		final String passport = UserBiz.getLocalSerializableUser().Passport;
		mHeaderMap = new HashMap<String, String>();
		mHeaderMap.put("passport", passport);
		int productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, 0);
		loadWebView(productId);
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
				finish();
			}
		});
	}

	/**
	 * 浏览器模式加载表单
	 */
	private void loadWebView(int productId) {
		// 加载webview
		// webview_root.setVisibility(View.VISIBLE);

		CookieManager.getInstance().setAcceptCookie(true);
		// CookieManager.getInstance().setAcceptThirdPartyCookies(webview_root,
		// true);
		// 为50%，最小缩放等级，将页面所有内容显示在手机屏幕上
		webview_root.setInitialScale(220);
		// 得到浏览器设置
		WebSettings webSettings = webview_root.getSettings();
		// 设置浏览器中javascript可执行
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		// 设置允许访问文件数据
		webSettings.setAllowFileAccess(true);
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
		String loadUrl = "http://183.62.246.245/金融理财/产品管理/MobileView?id="
				+ productId;
		LogUtils.i("webview_sh", loadUrl + "\n" + loadUrl);
		CookieManager.getInstance().removeAllCookie();
		webview_root.setWebViewClient(new MyWebViewClient());
		webview_root.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				// 用系统浏览器打开下载
				BrowserUtils.openBrowser(mContext, url);
			}
		});
		webview_root.loadUrl(loadUrl, mHeaderMap);
	}

	private class MyWebViewClient extends WebViewClient {

		// 点击网页中按钮时，在原页面打开
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url, mHeaderMap);
			LogUtils.i("webview_shouldOverrideUrlLoading", url);
			// ProgressDialogHelper.show(mContext, true);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			LogUtils.d(TAG, "URL地址:" + url);
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			LogUtils.i(TAG, "onPageFinished");
			super.onPageFinished(view, url);
		}
	}

}
