package com.cedarhd.control;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.DownloadHelper;
import com.cedarhd.helpers.server.DownloadHelper.DownloadListener;
import com.cedarhd.models.Attach;
import com.cedarhd.utils.LogUtils;

import java.io.File;
import java.util.List;

/**
 * 附件列表工具类
 * 
 * @author bohr
 * 
 */
public class AttchmentListHelper {

	private Context mContext;
	private List<Attach> mList;
	private ListView mListView;
	private AttachAdapter mAdapter;
	private int height;
	private DownloadHelper mDownloadHelper;

	LinearLayout ll;

	public AttchmentListHelper(Context mContext, List<Attach> mList,
			ListView mListView, LinearLayout ll) {
		super();
		this.mContext = mContext;
		this.mList = mList;
		this.mListView = mListView;
		this.ll = ll;

		mAdapter = new AttachAdapter();
		mDownloadHelper = DownloadHelper.getInstance();
		mListView.setAdapter(mAdapter);
	}

	public void setmList(List<Attach> mList) {
		this.mList = mList;
		mAdapter.notifyDataSetChanged();
	}

	public int getHeight() {
		return 75 * mList.size();
	}

	public AttachAdapter getmAdapter() {
		return mAdapter;
	}

	public void setmAdapter(AttachAdapter mAdapter) {
		this.mAdapter = mAdapter;
	}

	/**
	 * 内容适配器
	 * 
	 * @author bohr
	 * 
	 */
	public class AttachAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Attach getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.control_attchment, null);
				height = height + view.getHeight();
				ViewHolder vh = new ViewHolder();
				vh.tvAttchmentName = (TextView) view
						.findViewById(R.id.control_attchment_name);
				vh.ivAttachIco = (ImageView) view
						.findViewById(R.id.control_attchment_ico);
				vh.tvDownloadInfo = (TextView) view
						.findViewById(R.id.tv_download_info_attach);
				vh.pBar = (ProgressBar) view
						.findViewById(R.id.pbar_attchment_item);
				view.setTag(vh);
			}
			final ViewHolder vh = (ViewHolder) view.getTag();
			Attach item = mList.get(position);
			vh.tvAttchmentName.setText(item.getName());
			String suffix = item.Suffix;
			if (new File(FilePathConfig.getCacheDirPath() + File.separator
					+ item.Name).exists()) {
				vh.tvDownloadInfo.setText("已下载");
				item.setDownloaded(true);
			} else {
				vh.tvDownloadInfo.setText("未下载");
				item.setDownloaded(false);
			}
			setIco(suffix, vh.ivAttachIco);

			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Attach attach = mList.get(position);
					if (attach.isDownloaded()) {
						// open(attach.Name);
					} else {
						Toast.makeText(mContext, "开启下载...", Toast.LENGTH_SHORT)
								.show();
						mDownloadHelper.download(Global.BASE_URL
								+ attach.Address, attach.getName(), vh.pBar);
						vh.tvDownloadInfo.setText("下载中...");
						mDownloadHelper
								.setOnDownloadListener(new DownloadListener() {
									@Override
									public void complete() {
										Looper.prepareMainLooper();
										LogUtils.i("complete", "complete:"
												+ Thread.currentThread()
														.getName());
										Looper.loop();
									}
								});
					}
				}
			});

			return view;
		}
	}

	class ViewHolder {
		ImageView ivAttachIco;
		TextView tvAttchmentName;
		TextView tvDownloadInfo;
		ProgressBar pBar;
	}

	private void setIco(String suffix, ImageView iv) {
		if (!TextUtils.isEmpty(suffix)) {
			if (suffix.equalsIgnoreCase("doc")) {
				iv.setImageResource(R.drawable.ico_doc);
			} else if (suffix.equalsIgnoreCase("txt")) {
				iv.setImageResource(R.drawable.ico_txt);
			} else if (suffix.equalsIgnoreCase("zip")
					|| suffix.equalsIgnoreCase("rar")) {
				iv.setImageResource(R.drawable.ico_zip);
			} else {
				iv.setImageResource(R.drawable.ico_other);
			}
		}
	}
}
