package com.cedarhd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.models.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * 属性列表内容适配器
 * 
 * @author Administrator
 * 
 */
public class ExpandAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	// private Node mNode;
	private List<Node> showNodes = new ArrayList<Node>();

	public ExpandAdapter(Context context, Node mNode, List<Node> showNodes) {
		super();
		this.context = context;
		// this.mNode = mNode;
		this.showNodes = showNodes;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return showNodes.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.item_categray_lv, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_node_item);
		if (showNodes != null) {
			Node node = showNodes.get(position);
			if (node != null) {
				tv.setPadding(node.leftPadding, tv.getPaddingTop(),
						tv.getPaddingRight(), tv.getPaddingBottom());
				tv.setText(node.getName());
			}

		}
		return view;
	}

}
