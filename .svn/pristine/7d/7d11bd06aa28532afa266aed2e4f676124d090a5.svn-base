package com.cedarhd.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.helpers.BoeryunDownloadManager;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.OpenFilesIntent;
import com.cedarhd.models.Attach;
import com.cedarhd.models.DownloadFile;
import com.cedarhd.utils.LogUtils;

import java.io.File;
import java.util.List;

public class DownloadAdapter extends BaseAdapter {

	private Context mContext;
	private ListView mLv;
	private List<Attach> mList;
	private BoeryunDownloadManager mDownloadManager;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			DownloadFile downloadFile = (DownloadFile) msg.obj;

			int pos = getUpdatePos(downloadFile.atttachId);

			if (pos != -1) {
				Attach attach = mList.get(pos);
				attach.totalSize = downloadFile.totalSize;
				attach.downloadSize = downloadFile.downloadSize;
				attach.downloadState = downloadFile.downloadState;

				// notifyDataSetChanged会执行getView函数，更新所有可视item的数据
				// notifyDataSetChanged();
				// 只更新指定item的数据，提高了性能
				updateView(pos, attach);
			}
		};
	};

	// 更新指定item的数据
	private void updateView(int offset, Attach attach) {

		View view = mLv.getChildAt(offset);
		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder.pBar.getProgress() != 0) {
			holder.pBar.setMax(attach.totalSize);
		}
		holder.pBar.setProgress(attach.downloadSize);
		holder.tvAttchmentName.setText(attach.Name);
		String downloadSize = String.format("%.2f",
				(attach.downloadSize * 1.0f / 1024 / 1024));
		String totoalSize = String.format("%.2f",
				(attach.totalSize * 1.0f / 1024 / 1024));
		holder.tvSize.setText(downloadSize + "/");
		holder.tvTotal.setText(totoalSize + "MB");
		// Drawable drawable = mContext.mContext.getResources().getDrawable(
		// R.drawable.app_icon);
		// holder.icon.setImageDrawable(drawable);

		switch (attach.downloadState) {
		case BoeryunDownloadManager.DOWNLOAD_STATE_DOWNLOADING:
			holder.btnDown.setText("下载中");
			holder.tvStatus.setText("下载中");
			// this.changeBtnStyle(holder.btn, false);
			break;
		case BoeryunDownloadManager.DOWNLOAD_STATE_FINISH:
			holder.btnDown.setText("打开");
			holder.tvStatus.setText("已下载");

			holder.tvSize.setText("");
			holder.tvTotal.setText("");
			holder.pBar.setVisibility(View.INVISIBLE);
			// this.changeBtnStyle(holder.btn, false);
			break;
		}
	}

	/**
	 * @param mContext
	 * @param mList
	 */
	public DownloadAdapter(Context mContext, List<Attach> mList, ListView lv) {
		this.mContext = mContext;
		this.mList = mList;
		this.mLv = lv;
		mDownloadManager = BoeryunDownloadManager.getInstance();
		mDownloadManager.setHandler(mHandler);
	}

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
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_attchment_download, null);
			ViewHolder vh = new ViewHolder();
			vh.tvAttchmentName = (TextView) view
					.findViewById(R.id.download_attchment_name);
			vh.ivAttachIco = (ImageView) view
					.findViewById(R.id.download_attchment_ico);
			vh.tvStatus = (TextView) view.findViewById(R.id.tv_status_download);
			vh.tvSize = (TextView) view.findViewById(R.id.tv_size_download);
			vh.tvTotal = (TextView) view
					.findViewById(R.id.tv_filetotal_download);
			vh.pBar = (ProgressBar) view
					.findViewById(R.id.pbar_download_attach);
			vh.btnDown = (Button) view.findViewById(R.id.btn_download_attach);
			view.setTag(vh);
		}

		final ViewHolder vHolder = (ViewHolder) view.getTag();

		Attach item = mList.get(position);
		String suffix = item.Suffix;
		vHolder.tvAttchmentName.setText(item.getName());
		vHolder.tvAttchmentName.setText(item.Name + "");
		// vHolder.tvSize.setText("0/");
		// vHolder.tvTotal.setText("10.02MB");
		if (new File(FilePathConfig.getCacheDirPath() + File.separator
				+ item.Name).exists()) {
			vHolder.tvStatus.setText("已下载");
			vHolder.btnDown.setText("打开");
			vHolder.tvSize.setText("");
			vHolder.tvTotal.setText("");
			vHolder.pBar.setVisibility(View.INVISIBLE);
			item.downloadState = BoeryunDownloadManager.DOWNLOAD_STATE_FINISH;
		} else {
			vHolder.tvStatus.setText("未下载");
			vHolder.btnDown.setText("下载");
			item.downloadState = BoeryunDownloadManager.DOWNLOAD_STATE_NORMAL;
		}
		setIco(suffix, vHolder.ivAttachIco);

		vHolder.btnDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Attach attach = mList.get(position);
				/** 已下载完成直接打开 */
				if (attach.downloadState == BoeryunDownloadManager.DOWNLOAD_STATE_FINISH) {
					open(attach.Name);
				} else {
					DownloadFile downloadFile = new DownloadFile();
					downloadFile.atttachId = attach.Id;
					downloadFile.attachName = attach.Name;
					downloadFile.totalSize = attach.totalSize;
					downloadFile.downloadSize = attach.downloadSize;
					downloadFile.url = Global.BASE_URL + attach.Address;
					mDownloadManager.download(downloadFile);
					downloadFile.downloadState = BoeryunDownloadManager.DOWNLOAD_STATE_WAITING;
					vHolder.btnDown.setText("排队中");
					vHolder.pBar.setVisibility(View.VISIBLE);
					// mDownloadManager.download(url, fileName, pBar)
				}
			}
		});
		return view;
	}

	private class ViewHolder {
		TextView tvAttchmentName;
		TextView tvStatus;
		ImageView ivAttachIco;
		TextView tvSize;
		TextView tvTotal;
		ProgressBar pBar;
		Button btnDown;
	}

	/**
	 * 获取下载完成需要修改了pos 获取失败返回-1
	 */
	private int getUpdatePos(int attachId) {
		for (int i = 0; i < mList.size(); i++) {
			if (mList.get(i).Id == attachId) {
				return i;
			}
		}
		return -1;
	}

	private void setIco(String suffix, ImageView iv) {
		if (!TextUtils.isEmpty(suffix)) {
			suffix = suffix.replace(".", "");
			if (suffix.equalsIgnoreCase("doc")
					|| suffix.equalsIgnoreCase("docx")) {
				iv.setImageResource(R.drawable.ico_doc);
			} else if (suffix.equalsIgnoreCase("xls")
					|| suffix.equalsIgnoreCase("xlsx")) {
				iv.setImageResource(R.drawable.ico_excel);
			} else if (suffix.equalsIgnoreCase("ppt")
					|| suffix.equalsIgnoreCase("pptx")) {
				iv.setImageResource(R.drawable.ico_ppt);
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

	// 定义用于检查要打开的附件文件的后缀是否在遍历后缀数组中
	private boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.toLowerCase().endsWith(aEnd.toLowerCase()))
				return true;
		}
		return false;
	}

	/***
	 * 打开附件文件的方法
	 * 
	 * @param name
	 *            后缀名
	 */
	private void open(String name) {
		String fileName = FilePathConfig.getCacheDirPath() + File.separator
				+ name;
		File currentPath = new File(fileName);
		if (currentPath != null && currentPath.isFile()) {
			LogUtils.i("pathname", "-->" + fileName);
			Intent intent = null;
			if (checkEndsWithInStringArray(fileName, mContext.getResources()
					.getStringArray(R.array.fileEndingImage))) {
				intent = OpenFilesIntent.getImageFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, mContext
					.getResources().getStringArray(R.array.fileEndingWebText))) {
				intent = OpenFilesIntent.getHtmlFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, mContext
					.getResources().getStringArray(R.array.fileEndingPackage))) {
				intent = OpenFilesIntent.getApkFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, mContext
					.getResources().getStringArray(R.array.fileEndingAudio))) {
				intent = OpenFilesIntent.getAudioFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, mContext
					.getResources().getStringArray(R.array.fileEndingVideo))) {
				intent = OpenFilesIntent.getVideoFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, mContext
					.getResources().getStringArray(R.array.fileEndingText))) {
				intent = OpenFilesIntent.getTextFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, mContext
					.getResources().getStringArray(R.array.fileEndingPdf))) {
				intent = OpenFilesIntent.getPdfFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, mContext
					.getResources().getStringArray(R.array.fileEndingWord))) {
				intent = OpenFilesIntent.getWordFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, mContext
					.getResources().getStringArray(R.array.fileEndingExcel))) {
				intent = OpenFilesIntent.getExcelFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, mContext
					.getResources().getStringArray(R.array.fileEndingPPT))) {
				intent = OpenFilesIntent.getPPTFileIntent(currentPath);
			} else {
				intent = OpenFilesIntent.getOtherFileIntent(currentPath);
			}

			if (intent != null) {
				try {
					mContext.startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
					LogUtils.e("Open", e.getMessage() + "");
					Toast.makeText(mContext, "系统未检测到打开文件的程序，请选择",
							Toast.LENGTH_LONG).show();
					intent = OpenFilesIntent.getOtherFileIntent(currentPath);
					try {
						mContext.startActivity(intent);
					} catch (Exception e1) {
						e1.printStackTrace();
						LogUtils.e("Open2", e.getMessage() + "");
					}
				}
			}
		} else {
			Toast.makeText(mContext, "抱歉,这不是一个合法文件！", Toast.LENGTH_LONG).show();
		}
	}
}
