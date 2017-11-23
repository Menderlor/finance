package com.cedarhd;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.cedarhd.adapter.DiamondlAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.钻石积分;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/***
 * 钻石排行榜列表
 * 
 * @author K
 * 
 */
public class DiamondListActivity extends BaseActivity {
	private HttpUtils httpUtils;
	private String url;
	/** back按键 */
	private ImageButton button_back;
	/** 钻石榜界面的listview */
	private ListView diamondl_listview;
	/** 用于接收返回的数据 */
	private String result;
	/** 当获取成功时发送的值 */
	public static final int SUCCESS = 100;
	/** 当获取失败时发送的值 */
	/** 网络错误时发送 */
	public static final int END_INITENT_ERROR = 102;
	public static final int NO = 101;
	private List<钻石积分> diamondl_list;
	private Button record;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		httpUtils = new HttpUtils();
		setContentView(R.layout.activity_diamond);
		initView();
		getDiamondl();
	}

	/** 开启线程来获取钻石排名 */
	private void getDiamondl() {
		url = Global.BASE_URL + Global.EXTENSION
				+ "Diamond/GetDiamondScoreList" + "/" + "0" + "/" + "20";
		ProgressDialogHelper.show(DiamondListActivity.this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					result = httpUtils.httpGet(url);
					LogUtils.i("out", url + result);
					if (result.equals("网络错误")) {
						handler.sendEmptyMessage(END_INITENT_ERROR);
					} else {
						if (getStatus(result) == 1) {
							handler.sendEmptyMessage(SUCCESS);
						} else {
							handler.sendEmptyMessage(NO);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				ProgressDialogHelper.dismiss();
				diamondl_list = getStatiesDiamond(result);
				DiamondlAdapter adapter = new DiamondlAdapter(diamondl_list,
						DiamondListActivity.this);
				diamondl_listview.setAdapter(adapter);
				break;
			case NO:
				ProgressDialogHelper.dismiss();
				Toast.makeText(DiamondListActivity.this, "服务器异常",
						Toast.LENGTH_SHORT).show();
				break;
			case END_INITENT_ERROR:
				ProgressDialogHelper.dismiss();
				Toast.makeText(DiamondListActivity.this, "网络错误",
						Toast.LENGTH_SHORT).show();
				break;

			}
		}
	};

	/**
	 * 获取钻石数量统计的集合
	 * 
	 * @throws JSONException
	 */
	private List<钻石积分> getStatiesDiamond(String str) {
		List<钻石积分> list = new ArrayList<钻石积分>();
		try {
			JSONObject jsonObject = new JSONObject(str);
			JSONArray array;
			array = jsonObject.getJSONArray("Data");
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				钻石积分 diamondl = new 钻石积分();
				diamondl.接收人 = object.getInt("接收人");
				diamondl.接收人姓名 = object.getString("接收人姓名");
				diamondl.赞总计 = object.getInt("赞总计");
				diamondl.赞数量 = object.getInt("赞数量");
				diamondl.钻石数量 = object.getInt("钻石数量");
				list.add(diamondl);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	/** 获取状态信息 */
	private int getStatus(String str) throws JSONException {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(str);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject.getInt("Status");
	}

	/** 初始化控件 */
	private void initView() {
		button_back = (ImageButton) findViewById(R.id.diamondl_back);
		diamondl_listview = (ListView) findViewById(R.id.Diamondl_listView);
		record = (Button) findViewById(R.id.jilu);
		setOnclickListener();
	}

	/** 设置监听事件 */
	private void setOnclickListener() {
		diamondl_listview.setOnItemClickListener(listview_l);
		button_back.setOnClickListener(l);
		record.setOnClickListener(l);
	}

	private AdapterView.OnItemClickListener listview_l = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent(DiamondListActivity.this,
					DiamondDetailsActivity.class);
			intent.putExtra("item", diamondl_list.get(position));
			intent.putExtra("position", position);
			startActivity(intent);
		}
	};
	private View.OnClickListener l = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.diamondl_back:
				finish();
				break;
			case R.id.jilu:
				Intent intent = new Intent(DiamondListActivity.this,
						RecordActivity.class);
				startActivity(intent);
				break;

			}
		}
	};
}
