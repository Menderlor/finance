package com.cedarhd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.AvartarViewHelper;
import com.cedarhd.models.钻石积分;
import com.cedarhd.utils.LogUtils;

import java.util.List;

public class DiamondlAdapter extends BaseAdapter {
	private List<钻石积分> list;
	private Context context;
	private LayoutInflater inflater;

	public DiamondlAdapter(List<钻石积分> list, Context context) {
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HolderView holderView;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.diamondl_item, null);
			holderView = new HolderView(convertView);
			convertView.setTag(holderView);
		}
		holderView = (HolderView) convertView.getTag();
		钻石积分 diamondl = list.get(position);

		if (position == 0) {
			holderView.diamondl_rank.setBackgroundResource(R.drawable.ico_1st);
			holderView.diamondl_rank.setText(" ");
		} else {
			holderView.diamondl_rank.setText(String.valueOf(position + 1));
			holderView.diamondl_rank
					.setBackgroundResource(R.drawable.circle_diamon);
		}
		holderView.diamondl_num.setText(String.valueOf(diamondl.钻石数量));

		holderView.name.setText(diamondl.接收人姓名);
		holderView.diamondl_zan_num.setText(String.valueOf(diamondl.赞数量));
		LogUtils.i("out", holderView.avartarView + "123");
		// new AvartarViewHelper(context, String.valueOf(diamondl.接收人),
		// holderView.avartarView, false);
		holderView.avartarView.setTag(position);
		new AvartarViewHelper(context, diamondl.接收人, holderView.avartarView,
				position, 65, 65, false);
		return convertView;
	}

	class HolderView {
		public AvartarView avartarView;
		/** 钻石数量 */
		public TextView diamondl_num;
		/** 排名 */
		public TextView diamondl_rank;
		public TextView name, diamondl_zan_num;

		public HolderView(View convertView) {
			avartarView = (AvartarView) convertView
					.findViewById(R.id.diamondl_photo);
			diamondl_num = (TextView) convertView
					.findViewById(R.id.diamondl_num);
			diamondl_rank = (TextView) convertView
					.findViewById(R.id.diamondl_rank);
			name = (TextView) convertView.findViewById(R.id.diam_name);
			diamondl_zan_num = (TextView) convertView
					.findViewById(R.id.diamondl_zan_num);
		}
	}
}
