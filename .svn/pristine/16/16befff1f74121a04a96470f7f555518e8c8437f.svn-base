package com.cedarhd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.流程;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MessageUtil;

import java.util.HashMap;

/**
 * WebView显示表单内容
 * 
 * @author Administrator
 * 
 */
@SuppressLint("NewApi")
public class AuditFormActivity extends BaseActivity {
	public static final int APPROVAL_OPINION_CODE = 8;// 审批意见返回码
	public final int REQUEST_CODE_ASKFOR_ME = 12;// 待我审批
	ZLServiceHelper mZLServiceHelper = new ZLServiceHelper();
	ImageView imageViewCancel;
	ImageView imageViewSave;
	Button buttonTag_createfrom_commit;
	Button buttonTag_createfrom_approval;
	Button buttonTag_createfrom_votedown;
	EditText editText_approval_opinion;
	TextView textViewTitle;
	private LinearLayout ll_audit;
	private WebView webview_root;
	private MyProgressBar pBar;
	private String rusult;

	int id; // 服务器的表单编号,每个item的表单编号
	int typeId; // 表的类型id号
	String dataId; // 表单号,0表示新建
	int formId = 0; // 服务器返回的表单编号，用于提交表单
	String typeNname;// 表名
	String mustBeInt = "";// 只能接受数字的字段
	String nullContentfield = "";// 内容为空的字段
	boolean isFormSave = false;// 判断是否保存
	boolean isInit = false;// 判断是否初始化数据了
	流程 mFlow;
	private String opinion; // 审核意见
	boolean isAudit = false;
	Bundle bundle;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 3:
				rusult = (String) msg.obj;
				MessageUtil
						.ToastMessage(AuditFormActivity.this, rusult + "成功！");
				finish();
				break;
			case 4:
				rusult = (String) msg.obj;
				MessageUtil
						.ToastMessage(AuditFormActivity.this, rusult + "失败！");
				break;
			default:
				break;
			}
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audit_form);
		LogUtils.i("oncreate", "服务器传来的表单编号,表示该条数据在服务器数据库的位置");
		Intent intent = getIntent();
		bundle = intent.getExtras();
		id = bundle.getInt("id");// 服务器传来的表单编号,表示该条数据在服务器数据库的位置
		dataId = bundle.getString("dataId");// 表单号,0表示新建
		typeId = bundle.getInt("typeId");// 获取表的类型id号,110表示请假,94表示报销
		typeNname = bundle.getString("typeName");
		String isPhoneForm = bundle.getString("isPhoneForm");
		isAudit = bundle.getBoolean("isAudit");
		LogUtils.i("oncreate", "==id==" + id + "==typeId==" + typeId
				+ "==dataId==" + dataId + "==name==" + typeNname
				+ "==isAudit==" + isAudit);
		findViews();
		initViews(bundle);
		setOnClickListener();
		LogUtils.i("isPhoneForm", "isPhoneForm---" + isPhoneForm);
		loadWebView();
	}

	private void loadWebView() {
		requestFocusForForm();
		textViewTitle.setText(typeNname);
		// 加载webview
		webview_root.setVisibility(View.VISIBLE);
		// 相当于打开浏览器对象
		webview_root = (WebView) findViewById(R.id.webview_root);
		// 为25%，最小缩放等级，将页面所有内容显示在手机屏幕上
		webview_root.setInitialScale(25);
		String url = "http://www.boeryun.com/流程表单/VSheet/Form?name="
				+ typeNname + "&id=" + dataId;
		LogUtils.i("url_webview", url);
		// 得到浏览器设置
		WebSettings webSettings = webview_root.getSettings();
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
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("passport", Global.mUser.Passport);
		webview_root.loadUrl(url, headers);

		webview_root.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				pBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				pBar.setVisibility(View.GONE);
			}
		});
	}

	public void findViews() {
		imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		imageViewSave = (ImageView) findViewById(R.id.imageViewSave);
		buttonTag_createfrom_commit = (Button) findViewById(R.id.buttonTag_createfrom_commit);
		buttonTag_createfrom_approval = (Button) findViewById(R.id.buttonTag_createfrom_approval);
		buttonTag_createfrom_votedown = (Button) findViewById(R.id.buttonTag_createfrom_votedown);
		editText_approval_opinion = (EditText) findViewById(R.id.editText_approval_opinion);
		editText_approval_opinion.clearFocus();
		ll_audit = (LinearLayout) findViewById(R.id.ll_audit);
		pBar = (MyProgressBar) findViewById(R.id.pb_audit_form);
		webview_root = (WebView) findViewById(R.id.webview_root);
	}

	public void setOnClickListener() {
		imageViewCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		imageViewSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imageViewSave.setVisibility(View.GONE);
				submitForm();
			}
		});

		// 意见点击监听
		editText_approval_opinion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String opinion = editText_approval_opinion.getText().toString()
						.trim();
				Intent intent = new Intent(AuditFormActivity.this,
						Approval_Opinion.class);
				intent.putExtra(Approval_Opinion.FORMID, id);
				intent.putExtra(Approval_Opinion.OPINION, opinion);
				intent.putExtra(Approval_Opinion.TITLE, "审批意见");
			}
		});

		// 意见焦点改变监听
		editText_approval_opinion
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							String opinion = editText_approval_opinion
									.getText().toString().trim();
							Intent intent = new Intent(AuditFormActivity.this,
									Approval_Opinion.class);
							intent.putExtra(Approval_Opinion.FORMID, id);
							intent.putExtra(Approval_Opinion.OPINION, opinion);
							intent.putExtra(Approval_Opinion.TITLE, "审批意见");
							startActivityForResult(intent,
									APPROVAL_OPINION_CODE);
						}
					}
				});

		// 审核通过按钮
		buttonTag_createfrom_approval.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				opinion = editText_approval_opinion.getText().toString().trim();
				if (TextUtils.isEmpty(opinion)) {
					opinion = "无意见";
				}

				new Thread(new Runnable() {
					@Override
					public void run() {
						mZLServiceHelper.submitApprovalNew(opinion, "", id,
								true, handler);
					}
				}).start();

			}
		});

		// 否决按钮监听
		buttonTag_createfrom_votedown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				opinion = editText_approval_opinion.getText().toString().trim();
				if (TextUtils.isEmpty(opinion)) {
					opinion = "无意见";
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							mZLServiceHelper.submitApprovalNew(opinion, "", id,
									false, handler);
						} catch (Exception e) {
							Toast.makeText(AuditFormActivity.this, "否决异常",
									Toast.LENGTH_SHORT).show();
						}
					}
				}).start();
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 审核
		if (requestCode == APPROVAL_OPINION_CODE) {
			if (resultCode == Approval_Opinion.APPROVAL_OPINION_RESULT) {
				String opinion = data.getStringExtra(Approval_Opinion.OPINION);
				editText_approval_opinion.setText(opinion);
			}
			if (resultCode == Approval_Opinion.APPROVAL_OPINION_RESULT_SUCCEED) {
				// 如果审批成功，返回上级一个标志信息，APPROVAL_RESULT_SUCCEED
				// setResult(AskForLeaveListActivity.APPROVAL_RESULT_SUCCEED);
				setResult(ApplyListFragmentActivity.APPROVAL_RESULT_SUCCEED);
				finish();
			}
		}
	}

	/**
	 * 初始化数据
	 */
	private void initViews(Bundle bundle) {
		mFlow = (流程) bundle.getSerializable("flow");
		if (mFlow != null && mFlow.getCurrentState() != null) {
			LogUtils.i("mFlow", mFlow.getCurrentState());
			if (mFlow.getCurrentState().equals("未提交")) {
				imageViewSave.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 为表单获取焦点
	 */
	public void requestFocusForForm() {
		if (isAudit) {
			ll_audit.setVisibility(View.VISIBLE);
		} else {
			ll_audit.setVisibility(View.GONE);
		}
	}

	private void submitForm() {
		// 调用js代码：提交表单
		webview_root.loadUrl("javascript:submitForm()");
		Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
	}
}