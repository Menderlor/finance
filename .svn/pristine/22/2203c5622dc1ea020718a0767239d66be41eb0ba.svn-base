package com.cedarhd.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.adapter.InfoAdapter;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.字典;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * 字典选择工具类,包含了字典下载，缓存，对话框的形式弹出，样式相对简陋 相同地功能可使用带有查询过滤功能的
 * {@link DictionaryQueryDialogHelper}
 * 
 * @author K
 * 
 */
public class DictionaryDialogHelper {
	private Context context;
	private String dictName;
	private ListView mLv;
	private InfoAdapter adapter;
	private AlertDialog alertDialog;
	private static DictionaryDialogHelper mDialogHelper;
	private List<字典> emptyList = new ArrayList<字典>();

	private HashMap<String, SoftReference<List<字典>>> mDictHashMap = new HashMap<String, SoftReference<List<字典>>>();
	/**
	 * 选中字典的id,如果为-1表示没选中
	 */
	// public int ID = -1;
	private static final int SUCCESS = 1;
	private static final int FAILURE = 2;
	ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				List<字典> mlist = (List<字典>) msg.obj;
				adapter.setList(mlist);
				adapter.notifyDataSetChanged();
				break;
			case FAILURE:
				Toast.makeText(context, "获取信息失败", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	/**
	 * 弹出对话框并显示字典列表
	 * 
	 * @param context
	 *            上下文
	 * @param tvText
	 *            要弹出字典列表的文本框
	 * @param dictName
	 *            字典名称
	 */
	private DictionaryDialogHelper(final Context context) {
		super();
		this.context = context;
		initView();
	}

	public static DictionaryDialogHelper getInstance(Context context) {
		mDialogHelper = new DictionaryDialogHelper(context);

		return mDialogHelper;
	}

	private void initView() {
		if (alertDialog == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.taskclassifydialog, null);
			View emptyView = inflater.inflate(R.layout.textview_empty, null);
			alertDialog = new AlertDialog.Builder(context).create();
			Display display = ((Activity) context).getWindowManager()
					.getDefaultDisplay();
			WindowManager.LayoutParams lp = alertDialog.getWindow()
					.getAttributes();
			lp.width = (int) (display.getWidth()); // 设置宽度
			alertDialog.setView(view, 0, 0, 0, 0);
			mLv = (ListView) view.findViewById(R.id.lv_classify);
			adapter = new InfoAdapter(context, emptyList, null);
			mLv.setEmptyView(emptyView);
			mLv.setItemsCanFocus(false);
			mLv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			mLv.setAdapter(adapter);
		}
	}

	/** 弹出对话框 */
	public void showDialog(final TextView tvText, String dictName) {
		SoftReference<List<字典>> softReference = mDictHashMap.get(dictName);
		if (softReference == null) {
			startDownload(dictName);
			adapter.setList(emptyList);
			adapter.notifyDataSetChanged();
		} else {
			List<字典> dictList = mDictHashMap.get(dictName).get();
			if (dictList == null) {
				adapter.setList(emptyList);
				adapter.notifyDataSetChanged();
				startDownload(dictName);
			} else {
				adapter.setList(dictList);
				adapter.notifyDataSetChanged();
			}
		}
		setSelectedListener(tvText);
		alertDialog.show();
	}

	/***
	 * 绑定listView的选中事件
	 * 
	 * @param tvText
	 */
	private void setSelectedListener(final TextView tvText) {
		mLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				字典 item = (字典) adapter.getItem(pos);
				tvText.setText(item.getName());
				tvText.setTag(item.getId());
				alertDialog.dismiss();
			}
		});
	};

	/** 开始下载字典项 */
	private void startDownload(final String dictName) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<字典> list = zlServiceHelper.getDictList(dictName);
				if (list != null && list.size() > 0) {
					SoftReference<List<字典>> softRef = new SoftReference<List<字典>>(
							list);
					mDictHashMap.put(dictName, softRef);

					Message msg = handler.obtainMessage();
					msg.what = SUCCESS;
					msg.obj = list;
					handler.sendMessage(msg);
				} else {
					handler.sendEmptyMessage(FAILURE);
				}
			}
		}).start();
	}
}
