package com.cedarhd.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.models.部门;

import java.util.List;

public class Common_Sort_SelectListViewAdapter extends BaseAdapter {

	View.OnClickListener myAdapterCBListener;
	private List<部门> mList;
	private Context mContext;
	int mlistviewlayoutId;

	public Common_Sort_SelectListViewAdapter(Context pContext,
			int listviewlayoutId, List<部门> list, OnClickListener listener) {
		this.mContext = pContext;
		this.mlistviewlayoutId = listviewlayoutId;
		this.mList = list;
		this.myAdapterCBListener = listener;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public 部门 getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder holder;
		if (view == null || (holder = (ViewHolder) view.getTag()) == null) {
			view = View.inflate(mContext, mlistviewlayoutId, null);
			holder = new ViewHolder();
			holder.title = (TextView) view.findViewById(R.id.textView1);
			holder.viewBtn1 = (Button) view.findViewById(R.id.button1);
			holder.viewBtn2 = (Button) view.findViewById(R.id.button2);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		//
		if (myAdapterCBListener != null) {
			holder.viewBtn1.setOnClickListener(myAdapterCBListener);
			holder.viewBtn2.setOnClickListener(myAdapterCBListener);
		}

		部门 item = getItem(position);
		holder.title.setText(item.get名称());
		holder.viewBtn1.setTag(item);
		holder.viewBtn2.setTag(item);
		// holder.viewBtn.setTag(item);
		// holder.viewBtn.setText(item.Name);
		// holder.viewBtn.setChecked(item.b);
		return view;
	}

	final class ViewHolder {
		// public ImageView img;
		public TextView title;
		// public TextView info;
		public Button viewBtn1;
		public Button viewBtn2;
	}
}
