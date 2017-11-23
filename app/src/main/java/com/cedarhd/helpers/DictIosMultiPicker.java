package com.cedarhd.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.adapter.TagAdapter;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.view.FlowLayout;
import com.cedarhd.view.TagFlowLayout;
import com.cedarhd.view.TagFlowLayout.OnSelectListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/***
 * 字典仿IOS风格的底部选择 流式布局多选
 * 
 * @author K 2015/11/05 10:13
 */
public class DictIosMultiPicker {

	private Context mContext;
	private OnMultiSelectedListener mListener;
	private LayoutInflater mInflater;

	private Set<Integer> mSelectPosSet = new HashSet<Integer>();

	/**
	 * @param mContext
	 */
	public DictIosMultiPicker(Context mContext) {
		this.mContext = mContext;
		this.mInflater = LayoutInflater.from(mContext);
	}

	/**
	 * 弹出IOS风格的底部字典选择
	 * 
	 * @param mainLayoutId
	 *            layout文件的根节点id
	 * @param datas
	 *            数据源
	 */
	public void show(int mainLayoutId, String[] datas) {
		show(mainLayoutId, Arrays.asList(datas));
	}

	/**
	 * 弹出IOS风格的底部字典选择
	 * 
	 * @param mainLayoutId
	 *            layout文件的根节点id
	 * @param datas
	 *            数据源
	 */
	public void show(int mainLayoutId, List<String> datas) {
		View parentView = ((Activity) mContext).findViewById(mainLayoutId);
		View view = View.inflate(mContext, R.layout.pop_dict_ios_multi_picker,
				null);
		final PopupWindow popupWindow = new PopupWindow(view,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		initViews(datas, view, popupWindow);
		initPopupWindow(parentView, popupWindow);
	}

	/**
	 * 弹出IOS风格的底部字典选择
	 * 
	 * @param mainLayoutId
	 *            layout文件的根节点id
	 * 
	 * @param datas
	 *            泛型类型的集合数据源
	 * @param fieldName
	 *            泛型中作为显示名称的字段名称
	 */
	public <T> void show(int mainLayoutId, List<T> datas, String fieldName) {
		List<String> list = new ArrayList<String>();
		for (T item : datas) {
			Class cs = item.getClass();
			try {
				Field field = cs.getField(fieldName);
				String value = (String) field.get(item);
				LogUtils.i("field", "--" + value);
				list.add(value);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		show(mainLayoutId, list);
	}

	private void initPopupWindow(View parentView, final PopupWindow popupWindow) {
		popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				setBackgroundAlpha(1);
			}
		});

		popupWindow.setAnimationStyle(R.style.AnimationFadeBottom);
		setBackgroundAlpha(0.5f);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		popupWindow.showAtLocation(parentView, Gravity.BOTTOM | Gravity.LEFT,
				0, 0);
	}

	private void initViews(List<String> datas, View view,
			final PopupWindow popupWindow) {

		View top = (View) view.findViewById(R.id.top_dict_ios_multi_picker);
		Button btnSure = (Button) view
				.findViewById(R.id.btn_sure_dict_multi_picker);
		final TagFlowLayout tagFlowLayout = (TagFlowLayout) view
				.findViewById(R.id.tgflowlayout_multi_picker);

		tagFlowLayout.setAdapter(new TagAdapter<String>(datas) {
			@Override
			public View getView(FlowLayout parent, int position, String t) {
				LogUtils.i("tagA", position + "--" + t);
				TextView tv = (TextView) mInflater.inflate(
						R.layout.item_tag_flowlayout, tagFlowLayout, false);
				tv.setText(t);
				return tv;
			}
		});

		tagFlowLayout.setOnSelectListener(new OnSelectListener() {
			@Override
			public void onSelected(Set<Integer> selectPosSet) {
				mSelectPosSet = selectPosSet;
			}
		});

		top.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});

		btnSure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					if (mListener != null) {
						mListener.onSelected(mSelectPosSet);
					}
					popupWindow.dismiss();
				}
			}
		});
	}

	/**
	 * 设置添加屏幕的背景透明度
	 * 
	 * @param bgAlpha
	 */
	public void setBackgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = ((Activity) mContext).getWindow()
				.getAttributes();
		lp.alpha = bgAlpha;// 0.0-1.0
		((Activity) mContext).getWindow().setAttributes(lp);
	}

	public void setOnMultiSelectedListener(
			OnMultiSelectedListener onSelectedListener) {
		this.mListener = onSelectedListener;
	}

	public interface OnMultiSelectedListener {
		/** 监听选中字典集合的序号集合 */
		void onSelected(Set<Integer> selectPosSet);
	}
}
