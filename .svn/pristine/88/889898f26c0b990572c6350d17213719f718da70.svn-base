package com.cedarhd.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.models.任务分类;

import java.util.List;

public class TaskClassifyListViewAdapter extends BaseAdapter {

	View.OnClickListener myAdapterCBListener;
	private List<任务分类> mHashMapList;
	private Context mContext;

	public TaskClassifyListViewAdapter(Context pContext, List<任务分类> pList,
			OnClickListener listener) {
		this.mContext = pContext;
		this.mHashMapList = pList;
		this.myAdapterCBListener = listener;
	}

	@Override
	public int getCount() {
		return mHashMapList.size();
	}

	@Override
	public 任务分类 getItem(int arg0) {
		return mHashMapList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder holder;
//		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
//				LinearLayout.LayoutParams.FILL_PARENT, 70);
//		view.setLayoutParams(lp);
		if (view == null || (holder = (ViewHolder) view.getTag()) == null) {
			view = View.inflate(mContext, R.layout.taskclassify, null);
			holder = new ViewHolder();
			holder.Classify = (TextView) view
					.findViewById(R.id.tv_classifyname);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		// //
		// if (myAdapterCBListener != null) {
		// holder.viewBtn1.setOnClickListener(myAdapterCBListener);
		// holder.viewBtn2.setOnClickListener(myAdapterCBListener);
		// }

		任务分类 item = getItem(position);
		holder.Classify.setText(item.getName());
		// holder.viewBtn1.setTag(item);
		// holder.viewBtn2.setTag(item);
		// holder.viewBtn.setTag(item);
		// holder.viewBtn.setText(item.Name);
		// holder.viewBtn.setChecked(item.b);
		return view;
	}

	final class ViewHolder {
		// public ImageView img;
		public TextView Classify;

	}
}
