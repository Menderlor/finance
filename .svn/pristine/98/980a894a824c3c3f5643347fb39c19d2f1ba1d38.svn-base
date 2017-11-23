package com.cedarhd.helpers.server;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.models.Dict;

import java.util.List;

public class DictSelectDialog {

	private Context context;
	private List<Dict> mList;
	private EditText etText;
	private TextView tvText;
	private int keyPos;
	private LayoutInflater inflater;

	/**
	 * @param context
	 * @param mList
	 */
	public DictSelectDialog(Context context, List<Dict> mList, EditText etText,
			TextView tvText, int keyPos) {
		this.context = context;
		this.mList = mList;
		this.etText = etText;
		this.tvText = tvText;
		inflater = LayoutInflater.from(context);
	}

	public void showDialog() {
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
				Dict item = mList.get(position);
				tvText.setText(item.编号 + "");
				etText.setText(item.名称);
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
			Dict mDict = mList.get(position);
			viewHolder.tvName.setText(mDict.名称);
			return view;
		}
	}

	class ViewHolder {
		TextView tvName;
	}
}
