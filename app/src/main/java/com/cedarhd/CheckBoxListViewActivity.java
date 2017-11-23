package com.cedarhd;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cedarhd.adapter.CheckBoxListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.CheckBoxListViewItem;

import java.util.ArrayList;
import java.util.List;

public class CheckBoxListViewActivity extends BaseActivity {
	ListView lv = null;
	String name[] = { "G1", "G2", "G3", "G4", "G5", "G6", "G7", "G8", "G9",
			"G10", "G11", "G12", "G13", "G14" };

	private List<CheckBoxListViewItem> mList;
	private List<String> mData;
	private CheckBoxListViewAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.checkboxlistview);

		lv = (ListView) this.findViewById(R.id.lv);

		// this.findViewById(R.id.selectall).setOnClickListener(this);
		// this.findViewById(R.id.inverseselect).setOnClickListener(this);
		// this.findViewById(R.id.cancel).setOnClickListener(this);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				CheckBoxListViewItem item = mList.get(arg2);
				item.IsChecked = !item.IsChecked;// 取反操作
				initAdapter();
			}
		});

		init();
	}

	// @Override
	// public void onClick(View v) {
	// switch (v.getId()) {
	// case R.id.selectall:
	// int size1 = mList.size();
	// for (int i = 0; i < size1; i++) {
	// mList.get(i).b = true;
	// }
	// break;
	// case R.id.inverseselect:
	// int size2 = mList.size();
	// for (int i = 0; i < size2; i++) {
	// CheckBoxListViewItem item = mList.get(i);
	// item.b = !item.b;// 取反
	// }
	// break;
	// case R.id.cancel:
	// int size3 = mList.size();
	// for (int i = 0; i < size3; i++) {
	// mList.get(i).b = false;
	// }
	// break;
	// }
	// initAdapter();
	// }

	// 数据初始化
	private void init() {
		if (mList == null)
			mList = new ArrayList<CheckBoxListViewItem>();
		else
			mList.clear();
		if (mData == null)
			mData = new ArrayList<String>();
		// for (String s : name) {
		// mList.add(new CheckBoxListViewItem(s, false));
		// }
		initAdapter();
	}

	// 刷新适配器
	public void initAdapter() {
		if (mAdapter == null) {
			mAdapter = new CheckBoxListViewAdapter(
					CheckBoxListViewActivity.this, mList, null);
			lv.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}

		// int size = mList.size();
		// mData.clear();
		// for (int i = 0; i < size; i++) {
		// if (mList.get(i).b)
		// // count++;
		// mData.add(mList.get(i).name);
		// else
		// mData.remove(mList.get(i).name);
		// }
	}

}