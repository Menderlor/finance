package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.AvartarViewHelper;
import com.cedarhd.control.BoeryunCommentItemView;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.control.BoeryunNoScrollGridView;
import com.cedarhd.control.MultipleAttachView;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.InputSoftHelper;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.公司空间;
import com.cedarhd.models.动态;
import com.cedarhd.models.论坛回帖;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/***
 * 公司空间详情页面
 * 
 * @author k 2015-11-30
 */
public class CompanySpaceInfoActivity extends BaseActivity {

	private final int GET_DYNAMIC_SUCCESS = 101;
	private final int GET_DYNAMIC_FAILED = 102;
	public final static String TAG_INFO = "share_info";
	private Context mContext;
	private 公司空间 mLt公司空间;

	private ArrayList<String> attchList;

	private DictionaryHelper mDictionaryHelper;
	private ZLServiceHelper zlServiceHelper;

	private BoeryunHeaderView headerView;
	private AvartarView ivAvartar;
	private TextView tvName;
	private TextView tvTime;
	private TextView tvContent;
	private BoeryunNoScrollGridView gvImgs;
	private LinearLayout ll_comment_root;
	private ImageView ivTalking;
	private EditText etDiscuss;
	private TextView tvPublishDiscuss;
	private TextView tvTittle;
	private MultipleAttachView attachView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_info);

		initViews();
		initData();

		setonEvent();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 监听信鸽 Notification点击打开的通知
		XGPushClickedResult clickedResult = XGPushManager
				.onActivityStarted(this);
		if (clickedResult != null) {
			LogUtils.i("clickedResult", clickedResult.toString());
			String customContent = clickedResult.getCustomContent();
			LogUtils.i("CustomContent", customContent);
			try {
				JSONObject jo = new JSONObject(customContent);
				final String dynamicType = jo.getString("dynamicType");
				final String dataId = jo.getString("dataId");
				LogUtils.i("dynami", dynamicType + "--" + dataId);
				ProgressDialogHelper.show(mContext);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 加载动态对象
						动态 dynamic = zlServiceHelper.LoadDynamicById(
								dynamicType, dataId);
						if (dynamic != null && dynamic.Tiezi != null) {
							Message msg = mHandler.obtainMessage();
							msg.obj = dynamic;
							msg.what = GET_DYNAMIC_SUCCESS;
							mHandler.sendMessage(msg);
						} else {
							mHandler.sendEmptyMessage(GET_DYNAMIC_FAILED);
						}
					}
				}).start();
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("erro", e.toString());
				// mHandler.sendEmptyMessage(mHandler.GET_DISCUSS_FAILED);
			}

		}
	}

	public void initData() {
		mContext = CompanySpaceInfoActivity.this;
		mDictionaryHelper = new DictionaryHelper(mContext);
		zlServiceHelper = new ZLServiceHelper();

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mLt公司空间 = (公司空间) bundle.getSerializable(TAG_INFO);
		}

		if (mLt公司空间 != null) {
			loadData();
			loadCommentList(Integer.valueOf(mLt公司空间.编号));
		} else {
			mLt公司空间 = new 公司空间();
		}
	}

	private void initViews() {
		headerView = (BoeryunHeaderView) findViewById(R.id.header_share_info);
		ivAvartar = (AvartarView) findViewById(R.id.vatar_share_info);
		tvName = (TextView) findViewById(R.id.tv_user_share_info);
		tvTime = (TextView) findViewById(R.id.tv_time_share_info);
		tvContent = (TextView) findViewById(R.id.tv_content_share_info);
		gvImgs = (BoeryunNoScrollGridView) findViewById(R.id.gv_imgs_share_info);
		attachView = (MultipleAttachView) findViewById(R.id.attch_company);
		ll_comment_root = (LinearLayout) findViewById(R.id.ll_root_comment_share_info);
		ivTalking = (ImageView) findViewById(R.id.iv_talking_merge_bottom_discuss);
		etDiscuss = (EditText) findViewById(R.id.et_content_merge_bottom_discuss);
		tvPublishDiscuss = (TextView) findViewById(R.id.tv_publish_merge_bottom_discuss);
		tvTittle = (TextView) findViewById(R.id.tv_title_share_info);
		gvImgs.setVisibility(View.GONE);
		tvTittle.setVisibility(View.VISIBLE);
	}

	private void loadData() {
		tvName.setText(mDictionaryHelper.getUserNameById(mLt公司空间.发帖人) + "");
		tvContent.setText(mLt公司空间.内容 + "");
		tvTittle.setText(mLt公司空间.标题);
		tvTime.setText(DateDeserializer.getFormatTime(mLt公司空间.发帖时间));
		attachView.loadImageByAttachIds(mLt公司空间.附件); 

		int windowWidth = (int) (ViewHelper.getScreenWidth(mContext) - ViewHelper
				.dip2px(mContext, 16));

		// ImageLoader.getInstance().displayImage(imageUrls.get(position),
		// ivAvartar);

		// 头像
		new AvartarViewHelper(mContext, mLt公司空间.发帖人, ivAvartar, 60, 60, false);

//		if (!TextUtils.isEmpty(mLt公司空间.附件)) {
//			attchList = new ArrayList<String>();
//			String[] arr = mLt公司空间.附件.split(",");
//			for (int i = 0; i < arr.length; i++) {
//				attchList.add(arr[i]);
//			}
//			gvImgs.setAdapter(new NoScrollGridAdapter(mContext, 75,
//					Global.BASE_URL, attchList));
//		}

	}

	private void setonEvent() {
		// 设置九宫格小图片点击 查看大图
//		gvImgs.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// 点击回帖九宫格，查看大图
//				startImageBrower(position, attchList);
//			}
//		});

		headerView.setOnButtonClickListener(new OnButtonClickListener() {
			@Override
			public void onClickSaveOrAdd() {
				// finish();
			}

			@Override
			public void onClickFilter() {

			}

			@Override
			public void onClickBack() {
				finish();
			}
		});

		ivTalking.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new SpeechDialogHelper(mContext, etDiscuss, true);
			}
		});

		tvPublishDiscuss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = etDiscuss.getText().toString();
				if (TextUtils.isEmpty(content)) {
					showShortToast("还没有输入评论内容哦");
				} else {
					publishDiscuss(content);
				}
			}

		});
	}

	/***
	 * 获取评论列表
	 * 
	 * @param orderId
	 *            公司空间编号
	 */
	private void loadCommentList(int orderId) {
		String url = Global.BASE_URL + "CompanySpace/GetTieziDiscussList/"
				+ orderId;
		StringRequest.getAsyn(url, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {

			}

			@Override
			public void onResponse(String response) {
				List<论坛回帖> commanList = (List<论坛回帖>) JsonUtils
						.ConvertJsonToList(response, 论坛回帖.class);
				if (commanList != null && commanList.size() > 0) {
					ll_comment_root.setVisibility(View.VISIBLE);
					new BoeryunCommentItemView(mContext, ll_comment_root,
							commanList, true).createCommentView();
				}
			}

			@Override
			public void onFailure(Request request, Exception ex) {

			}
		});

	}

	private void publishDiscuss(String content) {
		ProgressDialogHelper.show(mContext, "评论中..");
		InputSoftHelper.hiddenSoftInput(mContext, etDiscuss);
		String url = Global.BASE_URL + "CompanySpace/AddTieziDiscuss";
		论坛回帖 lt = new 论坛回帖();
		lt.内容 = content;
		lt.论坛发帖 = Integer.valueOf(mLt公司空间.编号);
		lt.回帖人 = UserBiz.getGlobalUserIntegerId();
		Date date = new Date(System.currentTimeMillis());
		lt.回帖时间 = date;
		StringRequest.postAsyn(url, lt, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				response = StrUtils.removeRex(JsonUtils.pareseData(response));
//				showShortToast(response);
				if (response.equals("true")) {
					showShortToast("评论成功");
					etDiscuss.setText("");
					loadCommentList(Integer.valueOf(mLt公司空间.编号));
				} else {
					showShortToast("评论失败");
				}
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
			}
		});
	}

	/**
	 * 打开可滑动的图片查看器
	 * 
	 * @param position
	 * @param urls2
	 */
	protected void startImageBrower(int position, ArrayList<String> urls2) {
		Intent intent = new Intent(mContext, ImagePagerActivity.class);
		ArrayList<String> urlList = new ArrayList<String>();
		for (int i = 0; i < urls2.size(); i++) {
			urlList.add(Global.BASE_URL + urls2.get(i));
		}
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urlList);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		mContext.startActivity(intent);
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_DYNAMIC_SUCCESS:
				// ProgressDialogHelper.dismiss();
				// 动态 dynamic = (动态) msg.obj;
				// mLt公司空间 = (公司空间) dynamic.Tiezi;
				// loadData();
				break;
			case GET_DYNAMIC_FAILED:
				ProgressDialogHelper.dismiss();
				showShortToast("加载公司空间失败，请稍后重试");
				break;
			default:
				break;
			}
		};
	};

}
