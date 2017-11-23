package com.cedarhd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.models.钻石收发记录;
import com.cedarhd.utils.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiamondDetailAdapter extends BaseAdapter {
	private Context context;
	private List<钻石收发记录> mlist;
	LayoutInflater inflater;
	int mlistviewlayoutId;
	private boolean isFling;// 是否滑动标识位

	public void setFling(boolean isFling) {
		this.isFling = isFling;
	}

	public DiamondDetailAdapter(Context context, int mlistviewlayoutId,
			List<钻石收发记录> list) {
		super();
		this.context = context;
		this.mlist = list;
		this.mlistviewlayoutId = mlistviewlayoutId;
		inflater = LayoutInflater.from(context);
	}

	public void bindData(List<钻石收发记录> list) {
		mlist = new ArrayList<钻石收发记录>();
		mlist.addAll(list);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public List<钻石收发记录> getDataList() {
		return mlist;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HolderView holderView;
		if (convertView == null) {
			convertView = inflater.inflate(mlistviewlayoutId, null);
			holderView = new HolderView(convertView);
			convertView.setTag(holderView);
		}
		holderView = (HolderView) convertView.getTag();
		holderView.shou_name.setText(mlist.get(position).接收人姓名);
		holderView.fa_name.setText(mlist.get(position).发放人姓名);
		holderView.time.setText(gettime(mlist.get(position).时间));
		LogUtils.i("out", mlist.get(position).类型 + "");
		if (mlist.get(position).类型 == 1) {
			holderView.icon.setImageResource(R.drawable.diamondl);
		} else if (mlist.get(position).类型 == 2) {
			holderView.icon.setImageResource(R.drawable.ico_support2);
		}
		return convertView;
	}

	class HolderView {
		public TextView shou_name;
		public TextView fa_name;
		public TextView time;
		public ImageView icon;

		public HolderView(View convertView) {
			shou_name = (TextView) convertView.findViewById(R.id.shou_name);
			fa_name = (TextView) convertView.findViewById(R.id.fa_name);
			time = (TextView) convertView.findViewById(R.id.record_time);
			icon = (ImageView) convertView.findViewById(R.id.dimand_isdima);
		}
	}

	private String gettime(String str) {
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date d;
		String string = null;
		try {
			d = sim.parse(str);
			SimpleDateFormat sims = new SimpleDateFormat("MM-dd hh:mm");
			string = sims.format(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return string;
	}

}
