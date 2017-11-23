package com.cedarhd.helpers;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.models.字典;

import java.util.List;

/**
 * 字典项选择对话框
 * 
 * @author Administrator
 * 
 */
public class DictPickedDialog {

	private Context context;
	private List<字典> mList;
	private TextView tvText;
	private LayoutInflater inflater;

	/**
	 * @param context
	 * @param mList
	 */
	public DictPickedDialog(Context context, List<字典> mList, TextView tvText) {
		this.context = context;
		this.mList = mList;
		this.tvText = tvText;
		inflater = LayoutInflater.from(context);
	}

	public void showDicDialog() {
		Builder builder = new Builder(context);
		View view = inflater.inflate(R.layout.dialog_listview, null);
		ListView lv = (ListView) view.findViewById(R.id.lv_dict_dialog);
		DictsAdapter adapter = new DictsAdapter();
		lv.setAdapter(adapter);
		builder.setView(view);
		final Dialog dialog = builder.create();
		dialog.show();

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				字典 item = mList.get(position);
				tvText.setText(item.getName() + "");
				tvText.setTag(item.getId() + "");
				dialog.dismiss();
			}
		});
	}

	class DictsAdapter extends BaseAdapter {
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

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = inflater.inflate(R.layout.item_dict_select, null);
				ViewHolder vh = new ViewHolder();
				vh.tvName = (TextView) view
						.findViewById(R.id.tv_dict_name_select_dialog);
				view.setTag(vh);
			}
			ViewHolder viewHolder = (ViewHolder) view.getTag();
			字典 mDict = mList.get(position);
			viewHolder.tvName.setText(mDict.getName());
			return view;
		}
	}

	class ViewHolder {
		TextView tvName;
	}
}
