package com.cedarhd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.models.Client;

import java.util.List;

/**
 * 客户分类列表的内容适配器
 * 
 * @author BOHR
 * @since 2014/08/12 14:21
 */
public class ClientCategrayAdapter extends BaseAdapter {

	private List<Client> list;
	private LayoutInflater inflater;

	public ClientCategrayAdapter(List<Client> list, Context context) {
		super();
		this.list = list;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Client getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return list.get(position).get_Id();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(
                    com.cedarhd.R.layout.item_lv_drawer_client_categray, null);
        }
		Client item = list.get(position);
		TextView tv = (TextView) view.findViewById(R.id.tv_categray_item);
		tv.setText(item.getClassificationName());
		return view;
	}
}
