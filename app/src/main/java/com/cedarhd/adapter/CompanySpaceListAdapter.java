package com.cedarhd.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.cedarhd.ImagePagerActivity;
import com.cedarhd.R;
import com.cedarhd.ShareInfoActivity;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.AvartarViewHelper;
import com.cedarhd.control.BoeryunNoScrollGridView;
import com.cedarhd.control.CollapsibleTextView;
import com.cedarhd.control.SlideMenu;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Attach;
import com.cedarhd.models.公司空间;
import com.cedarhd.models.论坛回帖;
import com.cedarhd.utils.StrUtils;

import java.util.ArrayList;
import java.util.List;

public class CompanySpaceListAdapter extends BaseAdapter {
	private List<公司空间> mList;
	private Context mContext;
	/** 用于获取论坛评论列表 */
	private ZLServiceHelper zlHelper;
	private DictionaryHelper dictionaryHelper;
	/** list评论列表 */
	List<论坛回帖> listDiscuss = new ArrayList<论坛回帖>();
	private boolean mIsFling;// 是否滑动标识位

	private ArrayList<String> attchList;

	public CompanySpaceListAdapter(Context pContext, List<公司空间> mList) {
		this.mContext = pContext;
		this.mList = mList;
		zlHelper = new ZLServiceHelper();
		dictionaryHelper = new DictionaryHelper(mContext);
	}

	public List<公司空间> getData() {
		return mList;
	}

	public void bindData(List<公司空间> list) {
		mList = new ArrayList<公司空间>();
		mList.addAll(list);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public 公司空间 getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	AddImageHelper addImageHelper;

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext,
					R.layout.companyspacelist_listviewlayout, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		((SlideMenu) convertView).close(false);

		final 公司空间 item = getItem(position);

		if (!TextUtils.isEmpty(item.附件)) {
			String[] str = item.附件.split(",");

			for (int i = 0; i < str.length; i++) {
				attchList.add(str[i]);
			}
			holder.gridView.setVisibility(View.VISIBLE);
			NoScrollGridAdapter noGridAdapter = (NoScrollGridAdapter) holder.gridView
					.getTag();
			if (noGridAdapter == null) {
				noGridAdapter = new NoScrollGridAdapter(mContext,
						Global.BASE_URL, attchList);
			} else {
				noGridAdapter.setImageUrls(attchList);
			}
			holder.gridView.setAdapter(noGridAdapter);
		} else {
			holder.gridView.setVisibility(View.GONE);
		}

		String content = item.内容;
		String time = item.发帖时间;
		holder.textViewTime.setText(DateDeserializer.getFormatTime(time));
		holder.tvDept.setText(StrUtils.pareseNull(dictionaryHelper.get分公司(
				mContext, item.发帖人).get名称()));
		holder.textViewContent.setDesc(content, BufferType.NORMAL);
		holder.avartarView.setTag(position);
		holder.ivAttach.setTag(position);
		holder.layout.removeAllViews();

		if (!mIsFling) {
			// 头像
			new AvartarViewHelper(mContext, item.发帖人, holder.avartarView,
					position, 50, 50, true);

			// 设置九宫格小图片点击 查看大图
			holder.gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// 点击回帖九宫格，查看大图
					startImageBrower(position, attchList);
				}
			});

			holder.textViewContent.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
					bundle.putSerializable(ShareInfoActivity.TAG_INFO, item);
					Intent intent = new Intent(mContext,
							ShareInfoActivity.class);
					intent.putExtras(bundle);
					mContext.startActivity(intent);
				}
			});
		}
		return convertView;
	}

	public void setFling(boolean isFling) {
		this.mIsFling = isFling;
		notifyDataSetChanged();
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

	class ViewHolder {
		public AvartarView avartarView;
		public TextView textViewTime;
		public CollapsibleTextView textViewContent;
		public ImageView ivAttach;
		public RelativeLayout left_iv_attachment;
		public LinearLayout layout;
		public BoeryunNoScrollGridView gridView;
		public List<论坛回帖> listComment;
		public ListView list_fourm;
		public RelativeLayout rlAttachs;
		public TextView tvAttachCount;
		public TextView tvDelete;
		public TextView tvDept;

		public ViewHolder(View convertView) {
			avartarView = (AvartarView) convertView
					.findViewById(R.id.avatar_companyspacelist1);
			textViewTime = (TextView) convertView
					.findViewById(R.id.textViewTime);
			textViewContent = (CollapsibleTextView) convertView
					.findViewById(R.id.textViewContent);
			ivAttach = (ImageView) convertView.findViewById(R.id.iv_attachment);
			left_iv_attachment = (RelativeLayout) convertView
					.findViewById(R.id.rela_right);
			layout = (LinearLayout) convertView.findViewById(R.id.line_group);
			gridView = (BoeryunNoScrollGridView) convertView
					.findViewById(R.id.gv_company_list_item);
			list_fourm = (ListView) convertView.findViewById(R.id.list_count);
			rlAttachs = (RelativeLayout) convertView
					.findViewById(R.id.rl_attach_conpanylist);
			tvAttachCount = (TextView) convertView
					.findViewById(R.id.tv_count_attach);
			tvDelete = (TextView) convertView
					.findViewById(R.id.tv_delete_companysapacelist_item);
			tvDept = (TextView) convertView
					.findViewById(R.id.tv_dept_companyspacelist_item);
		}
	}

	// 存放非图片格式的附件信息
	List<Attach> listAttach = new ArrayList<Attach>();
	public final static int SUCESS_SHOW_ATTCHMENT = 102;
	public final static int SUCESS_DOWNLOAD_ATTACH = 103;
	public final static int FAILURE_SHOW_ATTCHMENT = 104;

}
