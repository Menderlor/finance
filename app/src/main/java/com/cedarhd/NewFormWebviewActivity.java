package com.cedarhd;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.helpers.Global;
import com.cedarhd.models.流程分类表;
import com.cedarhd.utils.LogUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * 新建表单 通过WebView显示OA上的表单内容
 * 
 * @author kjx
 * @since 2014/07/24 14:13
 */
@SuppressLint("NewApi")
public class NewFormWebviewActivity extends BaseActivity {
	public static final String PROCESS_URL_HEADER = "http://www.boeryun.com/流程表单/VSheet/sheet/Form?name=";
	private String baseUrl = "";
	private WebView webView;
	private ImageView ivCancel;
	private ImageView ivSubmit;
	private TextView tvTitle;
	private MyProgressBar pBar;
	private String title;
	private boolean isSave = true; // 标志位：用于区分用于保存还是提交；保存成功，会显示提交页面，提交成功则不会跳转到新页面
	private String newUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form_new_webview);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			流程分类表 item = (流程分类表) bundle.getSerializable("lcfl");
			title = item.表单名称;
		}
		initWebView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initView();
	}

	private void initWebView() {
		// 相当于打开浏览器对象
		webView = (WebView) findViewById(R.id.webView_form_new);
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
		// 支持缩放
		webSettings.setSupportZoom(true);
		// 缩放按钮
		webSettings.setBuiltInZoomControls(true);
		baseUrl = "http://www.boeryun.com/流程表单/VSheet/Form?name=" + title
				+ "&id=0";
		// url = "http://www.bohrsoft.com:8676/流程表单/VSheet/Form?name=" + title
		// + "&id=0";
		// url = PROCESS_URL_HEADER + "订货单"
		// + "&id=0&uname=阚健雄&coid=127&pwd=123456";
		LogUtils.i("url_webview", baseUrl);
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("passport", Global.mUser.Passport);
		webView.loadUrl(baseUrl, headers);

		// 如果页面中链接，如果希望点击链接继续在当前browser中响应，
		// 而不是新开Android的系统browser中响应该链接，必须覆盖webview的WebViewClient对象
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			// 开启新页面监听跳转到新页面url
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				pBar.setVisibility(View.VISIBLE);
				try {
					newUrl = URLDecoder.decode(url, "UTF-8");
					if (!isSave && baseUrl.equals(newUrl)) {
						// 提交成功后，不会跳转到新页面，隐藏提交按钮
						ivSubmit.setVisibility(View.GONE);
					}

					if (!newUrl.trim().endsWith("id=0")) {
						isSave = false;
						baseUrl = newUrl;
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				LogUtils.i("url_webviewStart", url);
				LogUtils.i("url_webviewStartNew", newUrl);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				LogUtils.i("url_webviewfinish", url);
				pBar.setVisibility(View.GONE);
			}
		});
	}

	private void saveForm() {
		// 调用js代码:保存表单
		webView.loadUrl("javascript:saveForm()");
		ivSubmit.setImageResource(R.drawable.up_load);
		Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
	}

	private void submitForm() {
		// 调用js代码：提交表单
		webView.loadUrl("javascript:submitForm()");
		Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
	}

	private void initView() {
		ivCancel = (ImageView) findViewById(R.id.iv_cancle_form_new);
		ivSubmit = (ImageView) findViewById(R.id.iv_sumit_form_new);
		tvTitle = (TextView) findViewById(R.id.tv_title_form_new);
		pBar = (MyProgressBar) findViewById(R.id.pb_form_new);
		if (!TextUtils.isEmpty(title)) {
			tvTitle.setText(title);
		}
		ivCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ivSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 提交表单
				if (isSave) {
					saveForm();
				} else {
					submitForm();
				}
			}
		});
	}
}
