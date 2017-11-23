package com.cedarhd.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.NewFormWebviewActivity;
import com.cedarhd.R;
import com.cedarhd.models.流程分类表;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 申请与审批 更多 表单内容适配器
 * 
 * @author BOHR
 * 
 */
public class FormAdapter extends BaseAdapter {

	private List<流程分类表> list = new ArrayList<流程分类表>();
	private List<流程分类表> listCategray = new ArrayList<流程分类表>();
	private LayoutInflater inflater;
	private Context context;

	public FormAdapter(List<流程分类表> list, Context context) {
		super();
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
		List<流程分类表> listTemp = new ArrayList<流程分类表>();
		for (int i = 0; i < list.size(); i++) {
			// 保存分类信息
			流程分类表 item = list.get(i);
			LogUtils.i("all", item.编号 + "--" + item.名称 + "--" + item.上级);
			if (TextUtils.isEmpty(item.表单配置文件)) {
				item.上级 = 0;
				listTemp.add(item);
			}
			// if (item.上级 == 0 ) {
			// listTemp.add(item);
			// }
		}
		list.removeAll(listTemp);
		LogUtils.i("formAdapter", "list-->" + list.size());
		LogUtils.i("formAdapter", "listTemp-->" + listTemp.size());
		for (int i = 0; i < listTemp.size(); i++) {
			// listCategray 相同分类表单存放在同一个段
			int id = listTemp.get(i).编号;
			// boolean isFirst = true; // 分类只插入一次
			boolean isHasChild = false; // 是否有子节点
			LogUtils.e("formAdapterE", "listTemp-->ID=" + id);

			// 插入分类
			listCategray.add(listTemp.get(i));
			for (int j = 0; j < list.size(); j++) {
				流程分类表 item = list.get(j);
				LogUtils.d("formAdapterE", "编号=" + item.编号 + "--上级=" + item.上级);
				if (item.上级 == id) {
					listCategray.add(item);
					if (!isHasChild) {
						isHasChild = true;
					}
				}
			}

			// 如果没有子节点，移除该分类
			if (!isHasChild) {
				listCategray.remove(listCategray.size() - 1);
			}
		}
		LogUtils.i("formAdapter", "listCategray-->" + listCategray.size());
	}

	@Override
	public int getCount() {
		return listCategray.size();
	}

	@Override
	public 流程分类表 getItem(int position) {
		return listCategray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final 流程分类表 item = listCategray.get(position);
		View view = inflater.inflate(R.layout.item_ask_more_form, null);
		TextView tvName = (TextView) view.findViewById(R.id.tv_form_name);
		ImageView ivWeb = (ImageView) view.findViewById(R.id.iv_form_web);
		if (TextUtils.isEmpty(item.表单配置文件) && TextUtils.isEmpty(item.工作流配置文件)
				&& item.上级 == 0) {
			view.setBackgroundColor(context.getResources().getColor(
					R.color.bg_list));
			tvName.setText(item.表单名称);
			tvName.setTextColor(context.getResources().getColor(R.color.gray));
			view.setEnabled(false);
			ivWeb.setVisibility(View.GONE);
		} else {
			tvName.setText("    " + item.表单名称);
			ivWeb.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "点击了浏览器" + item.表单名称,
							Toast.LENGTH_SHORT).show();
					// webview打开表单页面
					Intent intent = new Intent(context,
							NewFormWebviewActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("lcfl", item);
					intent.putExtras(bundle);
					context.startActivity(intent);
				}
			});
		}
		return view;
	}

	public void setList(List<流程分类表> list) {
		this.list = list;
	}
}
