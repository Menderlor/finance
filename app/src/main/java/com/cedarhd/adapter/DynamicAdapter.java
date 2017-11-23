package com.cedarhd.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.AvartarViewHelper;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.动态;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

public class DynamicAdapter extends BaseAdapter {

	private List<动态> list;
	private Context mContext;
	private LayoutInflater inflater;
	private DictionaryHelper dictionaryHelper;

	public DynamicAdapter(List<动态> list, Context context) {
		super();
		this.list = list;
		mContext = context;
		inflater = LayoutInflater.from(context);
		dictionaryHelper = new DictionaryHelper(mContext);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public 动态 getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public List<动态> getList() {
		return list;
	}

	public void setList(List<动态> list) {
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.dynamic_listviewitem, parent,
					false);
			ViewHolder holder = new ViewHolder();
			holder.avartarView = (AvartarView) view
					.findViewById(R.id.avatar_dynamic);
			holder.tvUserName = (TextView) view
					.findViewById(R.id.tv_username_dynamic);
			holder.tvContent = (TextView) view
					.findViewById(R.id.tv_content_dynamic);
			holder.tvTime = (TextView) view.findViewById(R.id.tv_time_dynamic);
			view.setTag(holder);
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		动态 item = list.get(position);

		String time = item.Time == null ? "" : DateDeserializer
				.getFormatTime(ViewHelper.formatDateToStr(item.Time));
		int userId = Integer.parseInt(item.UserId);
		if (TextUtils.isEmpty(item.UserName)) {
			holder.tvUserName.setText("");
		} else {
			String userInfo = item.UserName + "("
					+ dictionaryHelper.get分公司(mContext, userId).get名称() + ")";
			holder.tvUserName.setText(userInfo);
		}
		holder.tvTime.setText(time);
		holder.tvContent.setText(item.Content);
		// holder.tvContent.setText(paresHtmlContent(item.HtmlContent));
		holder.avartarView.setTag(position);

		String read = item.Read;
		AvartarViewHelper avartarViewHelper = new AvartarViewHelper(mContext,
				userId, holder.avartarView, position, 50, 50, false);
		if (!TextUtils.isEmpty(read)) {
			avartarViewHelper.setRead(true);
		} else {
			// holder.ivDotRead.setVisibility(View.VISIBLE);
			avartarViewHelper.setRead(false);
		}
		return view;
	}

	class ViewHolder {
		AvartarView avartarView;
		TextView tvUserName;
		TextView tvContent;
		TextView tvTime;
	}

	private String paresHtmlContent(String htmlStr) {
		Document doc = Jsoup.parseBodyFragment(htmlStr);
		Element body = doc.body();
		return body.text();
	}
}
