package com.cedarhd.control;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.论坛回帖;

import java.util.List;
/**
 * 论坛回贴列表工具类
 * 
 * @author py 2014.8.21
 * 
 */
public class LunTanDiscussListHelper {
	
	private Context mContext;
	private List<论坛回帖> mList;
	private ListView mListView;
	private DiscussAdapter mAdapter;
	private DictionaryHelper dictionaryHelper;
	LinearLayout ll;
	
	public LunTanDiscussListHelper(Context mContext, List<论坛回帖> mList,
			ListView mListView, LinearLayout ll) {
		super();
		this.mContext = mContext;
		this.mList = mList;
		this.mListView = mListView;
		this.ll = ll;

		mAdapter = new DiscussAdapter();
		dictionaryHelper = new DictionaryHelper(mContext);
		mListView.setAdapter(mAdapter);
	}
	
	public void setmList(List<论坛回帖> mList) {
		this.mList = mList;
		mAdapter.notifyDataSetChanged();
	}
	
	public int getHeight() {
		int height = 20 + (int) ViewHelper.dip2px(mContext, 35); // header高度 +30
		for (int i = 0; i < mList.size(); i++) {
			int num = 1;
			String content = mList.get(i).get内容();
			if (!TextUtils.isEmpty(content)) {
				num += content.length() / 15; // 每行13 字
			}
			height += 35 * num;
		}
		return (int) ViewHelper.dip2px(mContext, height);
	}
	
	/**
	 * 内容适配器
	 * 
	 * @author bohr
	 * 
	 */
	class DiscussAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.control_discuss, null);
				ViewHolder vh = new ViewHolder();
				vh.tvUserName = (TextView) view
						.findViewById(R.id.control_discuss_user_name);
				vh.tvContent = (TextView) view
						.findViewById(R.id.control_discuss_content);
				view.setTag(vh);
			}

			ViewHolder vh = (ViewHolder) view.getTag();
			论坛回帖 item = mList.get(position);
			vh.tvUserName.setText(dictionaryHelper.getUserNameById(item.回帖人)
					+ ":");
			vh.tvContent.setText(item.内容);
			return view;
		}
	}

	class ViewHolder {
		TextView tvUserName;
		TextView tvContent;
	}

}
