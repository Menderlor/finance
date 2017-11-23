package com.cedarhd.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommanAdapter<T> extends BaseAdapter {
	private List<T> mList;
	private Context context;
	private int mLayoutId;

	/**
	 * 通用内容适配器
	 * 
	 * @param mList
	 *            数据源
	 * @param context
	 *            当前上下文
	 * @param mLayoutId
	 *            item布局资源id
	 */
	public CommanAdapter(List<T> mList, Context context, int mLayoutId) {
		super();
		this.mList = mList;
		this.context = context;
		this.mLayoutId = mLayoutId;
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public T getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BoeryunViewHolder vh = BoeryunViewHolder.getInstance(position,
				convertView, parent, context, mLayoutId);
		convert(position, mList.get(position), vh);
		return vh.getConvertView();
	}

	/** 获取当前数据源 */
	public List<T> getDataList() {
		return mList;
	}

	/***
	 * 向底部添加一条
	 * 
	 * @param t
	 * @param isClearOld
	 */
	public void addBottom(T t, boolean isClearOld) {
		if (isClearOld) {
			mList.clear();
		}
		mList.add(t);
		notifyDataSetChanged();
	}

	/***
	 * 向底部添加一个新的集合
	 * 
	 * @param data
	 * @param isClearOld
	 *            是否清空旧数据
	 */
	public void addBottom(List<T> list, boolean isClearOld) {
		addBottom(mList.size(), list, isClearOld);
	}

	/***
	 * 向底部添加一个新的集合
	 * 
	 * @param isClearOld
	 *            是否清空旧数据
	 */
	public void addBottom(int pos, List<T> list, boolean isClearOld) {
		if (isClearOld) {
			mList.clear();
			pos = 0;
		}
		mList.addAll(pos, list);
		notifyDataSetChanged();
	}

	/***
	 * 向顶部添加一条新数据
	 * 
	 * @param data
	 * @param isClearOld
	 *            是否清空旧数据
	 */
	public void addTop(T t, boolean isClearOld) {
		if (isClearOld) {
			mList.clear();
		}
		mList.add(0, t);
		notifyDataSetChanged();
	}

	// 向对顶部添加多条
	public void addTop(List<T> data, boolean isClearOld) {
		if (isClearOld) {
			mList.clear();
		}
		mList.addAll(0, data);
		notifyDataSetChanged();
	}

	/***
	 * 根据序号移除其中某一条
	 * 
	 * @param pos
	 *            序号
	 */
	public void removeAtPos(int pos) {
		if (pos < mList.size()) {
			mList.remove(pos);
			notifyDataSetChanged();
		}
	}

	/***
	 * 移除其中某一条
	 * 
	 * @param pos
	 *            序号
	 */
	public void remove(T t) {
		mList.remove(t);
		notifyDataSetChanged();
	}

	/***
	 * 移除部分集合的数据源
	 * 
	 * @param pos
	 *            序号
	 */
	public void removeList(List<T> list) {
		mList.removeAll(list);
		notifyDataSetChanged();
	}

	/**
	 * 填充新的适配数据，清空原有数据
	 * 
	 * @param data
	 */
	public void changeData(List<T> data) {
		mList.clear();
		mList.addAll(0, data);
		notifyDataSetChanged();
	}

	/**
	 * 填充新的适配数据，清空原有数据
	 * 
	 */
	public void clearData() {
		mList.clear();
		notifyDataSetChanged();
	}

	public abstract void convert(int position, T item,
			BoeryunViewHolder viewHolder);
}
