package com.cedarhd.helpers;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.BoeryunApp;
import com.cedarhd.R;
import com.cedarhd.adapter.InfoAdapter;
import com.cedarhd.control.MyFlowLayout;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.LatestSelectedDict;
import com.cedarhd.models.字典;
import com.cedarhd.utils.LogUtils;
import com.j256.ormlite.dao.Dao;

import java.lang.ref.SoftReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 字典选择项全屏对话框，带有仿QQ搜索效果,自带内存缓存效果，保存最近搜索历史记录以流式布局的形式显示
 * 
 * @author K
 * @since 2015-7-27
 */
@SuppressLint("NewApi")
public class DictionaryQueryDialogHelper {
	/** 一个字典最大缓存数量 */
	private final int MAX_LATEST_VALUE = 6;
	private Context mContext;
	private String mDictName;

	private String mOriginalColumnName;
	private String mFilter;

	private List<字典> mDictList;
	private ListView mLv;
	private MyProgressBar mPbar;
	private InfoAdapter adapter;
	private Dialog alertDialog;
	private static DictionaryQueryDialogHelper mDialogHelper;
	private Dao<LatestSelectedDict, Integer> mDao;
	private List<字典> emptyList = new ArrayList<字典>();

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
				mPbar.setVisibility(View.GONE);
				List<字典> list = (List<字典>) msg.obj;
				adapter.setList(list);
				adapter.notifyDataSetChanged();
				break;
			case FAILURE:
				mPbar.setVisibility(View.GONE);
				Toast.makeText(mContext, "获取信息失败", Toast.LENGTH_SHORT).show();
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
	private DictionaryQueryDialogHelper(final Context context) {
		super();
		this.mContext = context;
		ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
		try {
			mDao = ormDataHelper.getDao(LatestSelectedDict.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		initView();
	}

	public static DictionaryQueryDialogHelper getInstance(Context context) {
		if (mDialogHelper != null && mDialogHelper.mContext.equals(context)) {
			return mDialogHelper;
		} else {
			return new DictionaryQueryDialogHelper(context);
		}
	}

	private void initView() {
		if (alertDialog == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View view = inflater.inflate(R.layout.dialog_select_search_dict,
					null);
			View emptyView = inflater.inflate(R.layout.textview_empty, null);
			alertDialog = new Dialog(mContext, R.style.Dialog_Fullscreen);
			// alertDialog.setContentView(R.layout.dialog_select_search_dict);
			alertDialog.setContentView(view);
			initEvent(view);
			mLv = (ListView) view.findViewById(R.id.lv_dict_select);
			mPbar = (MyProgressBar) view.findViewById(R.id.pbar_dict_select);
			adapter = new InfoAdapter(mContext, emptyList, null);
			mLv.setEmptyView(emptyView);
			mLv.setItemsCanFocus(false);
			mLv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			mLv.setAdapter(adapter);
		}
	}

	private MyFlowLayout mFlowLayout;

	private void initEvent(View view) {
		ImageView ivCancle = (ImageView) view
				.findViewById(R.id.imageViewCancel_client);
		EditText etInput = (EditText) view.findViewById(R.id.et_input_dict);
		EditText etSearch = (EditText) view.findViewById(R.id.et_search_dict);
		TextView tvCancel = (TextView) view
				.findViewById(R.id.tv_cancle_select_dict);

		mFlowLayout = (MyFlowLayout) view
				.findViewById(R.id.flow_layout_select_dict);
		ivCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		tvCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		etInput.setInputType(InputType.TYPE_NULL); // 获取焦点，并且不让弹出软键盘
		etSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				// 监听文本变化
				LogUtils.i("onText", s.toString());
				search(s.toString());
			}
		});
	}

	/**
	 * 弹出字典选择对话框,不做任何绑定操作，选中字典后需要通过 {@link setOnSelectedListener}回调自行处理
	 * 
	 * @param dictName
	 *            字典名称
	 */
	public void show(String dictName) {
		show(null, dictName);
	}

	/**
	 * 弹出字典选择对话框,不做任何绑定操作，选中字典后需要通过 {@link setOnSelectedListener}回调自行处理
	 * 
	 * @param dictName
	 *            字典名称
	 * @param originalColumnName
	 *            原始数据库列表 AS 字典.名称
	 * @param filter
	 *            附加条件
	 */
	public void show(String dictName, String originalColumnName, String filter) {
		this.mOriginalColumnName = originalColumnName;
		this.mFilter = filter;
		show(null, dictName);
	}

	/***
	 * 弹出对话框,选中后将字典项实体绑定tvText的tag,字典名称在tvText上显示
	 * 
	 * @param tvText
	 *            选中绑定字典项的文本框，选中后将字典项实体绑定TextView的tag,字典名称在TextView上显示
	 * @param dictName
	 *            字典表名称
	 */
	public void show(final TextView tvText, String dictName) {
		this.mDictName = dictName;
		final List<LatestSelectedDict> localDicts = getLocalLatestList();
		mPbar.setVisibility(View.VISIBLE);
		mFlowLayout.removeAllViews();
		for (int i = 0; i < localDicts.size(); i++) {
			final LatestSelectedDict item = localDicts.get(i);
			// 流式布局的形式 显示本地最近选择
			final TextView tvName = (TextView) LayoutInflater.from(mContext)
					.inflate(R.layout.tag_text, mFlowLayout, false);
			tvName.setText(localDicts.get(i).getName() + "");
			tvName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setFlowSelectedListener(tvText,
							new 字典(item.getId(), item.getName()));
				}
			});
			mFlowLayout.addView(tvName);
		}

		if (!TextUtils.isEmpty(mFilter)) {
			// 如果过滤条件不为空，则先清空该字典原有缓存
			BoeryunApp.getDictHashMap().remove(dictName);
		}

		SoftReference<List<字典>> softReference = BoeryunApp.getDictHashMap()
				.get(dictName);
		if (softReference == null) {
			startDownload(dictName);
			adapter.setList(emptyList);
			adapter.notifyDataSetChanged();
		} else {
			mDictList = BoeryunApp.getDictHashMap().get(dictName).get();
			if (mDictList == null) {
				adapter.setList(emptyList);
				adapter.notifyDataSetChanged();
				startDownload(dictName);
			} else {
				adapter.setList(mDictList);
				adapter.notifyDataSetChanged();
				mPbar.setVisibility(View.GONE);
			}
		}
		setSelectedListener(tvText);

		alertDialog.show();
		// WindowManager.LayoutParams params = alertDialog.getWindow()
		// .getAttributes();
		// params.height -= ViewHelper.getStatusBarHeight(mContext); // 状态栏高度
		// alertDialog.getWindow().setAttributes(params);
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

				setFlowSelectedListener(tvText, item);
			}
		});
	};

	/***
	 * 绑定最近按钮选中监听事件
	 * 
	 * @param tvText
	 */
	private void setFlowSelectedListener(final TextView tvText, 字典 item) {
		if (tvText != null) {
			tvText.setText(item.getName());
			tvText.setTag(item);
		}

		insertDbIfNoExist(item);

		if (mOnSelectedListener != null) {
			mOnSelectedListener.onSelected(item);
		}

		alertDialog.dismiss();
	};

	/** 开始下载字典项 */
	private void startDownload(final String dictName) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// mDictList = zlServiceHelper.getDictList(dictName);
				mDictList = zlServiceHelper.getCustomDicts(dictName,
						mOriginalColumnName, mFilter);
				if (mDictList != null && mDictList.size() > 0) {
					SoftReference<List<字典>> softRef = new SoftReference<List<字典>>(
							mDictList);
					BoeryunApp.getDictHashMap().put(dictName, softRef);
					Message msg = handler.obtainMessage();
					msg.what = SUCCESS;
					msg.obj = mDictList;
					handler.sendMessage(msg);
				} else {
					handler.sendEmptyMessage(FAILURE);
				}
			}
		}).start();
	}

	/***
	 * 搜索
	 * 
	 * @param filter
	 */
	private void search(String filter) {
		if (mDictList != null && mDictList.size() > 0) {
			List<字典> list = new ArrayList<字典>();
			for (int i = 0; i < mDictList.size(); i++) {
				if (mDictList.get(i).getName().contains(filter)) {
					list.add(mDictList.get(i));
				}
			}
			adapter.setList(list);
			adapter.notifyDataSetChanged();
		}
	}

	/***
	 * 选中字典项，添加到最近选中数据库
	 * 
	 * @param dict
	 */
	private void insertDbIfNoExist(字典 dict) {
		try {
			int count = mDao.queryBuilder().where().eq("DictName", mDictName)
					.query().size();
			if (count >= MAX_LATEST_VALUE) {
				// 如果超出最大数量，先删除最后更新时间小的，间隔远的
				long deleteCount = count / 2;
				mDao.delete(mDao.queryBuilder().orderBy("updateTime", true)
						.limit(deleteCount).query());
			}

			// 查询相同字典项
			LatestSelectedDict updateDict = mDao.queryBuilder().where()
					.eq("DictName", mDictName).and().eq("Id", dict.getId())
					.queryForFirst();
			mDao.delete(updateDict);

			updateDict = new LatestSelectedDict(dict.getId(), dict.getName(),
					mDictName);
			mDao.create(updateDict);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/***
	 * 获取本地最近选择列表
	 * 
	 * @return
	 */
	private List<LatestSelectedDict> getLocalLatestList() {
		try {
			return mDao.queryBuilder().orderBy("updateTime", false).where()
					.eq("DictName", mDictName).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<LatestSelectedDict>();
	}

	private OnSelectedListener mOnSelectedListener;

	public interface OnSelectedListener {
		public void onSelected(字典 dict);
	}

	/*** 字典选中事件监听 */
	public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
		this.mOnSelectedListener = onSelectedListener;
	}
}
