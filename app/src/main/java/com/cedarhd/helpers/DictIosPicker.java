package com.cedarhd.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.utils.LogUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 * 字典仿IOS风格的底部选择
 * 
 * @author K 2015/10/01 10:10
 */
public class DictIosPicker {

	private Context mContext;
	private OnSelectedListener mListener;

	/**
	 * @param mContext
	 */
	public DictIosPicker(Context mContext) {
		this.mContext = mContext;
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

		View view = View.inflate(mContext, R.layout.pop_dict_ios_picker, null);
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

		View top = (View) view.findViewById(R.id.top_dict_ios_picker);
		Button btnCancel = (Button) view
				.findViewById(R.id.btn_cancle_dict_picker);
		final ListView lv = (ListView) view
				.findViewById(R.id.lv_dict_ios_picker);
		lv.setAdapter(getAdapter(datas));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mListener != null) {
					popupWindow.dismiss();
					mListener.onSelected(position);
				}
			}
		});

		top.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					popupWindow.dismiss();
				}
			}
		});

	}

	private CommanAdapter<String> getAdapter(List<String> datas) {
		return new CommanAdapter<String>(datas, mContext,
				R.layout.item_dict_ios) {
			@Override
			public void convert(int position, String item,
					BoeryunViewHolder viewHolder) {
				TextView tvName = viewHolder.getView(R.id.tv_name_dict_ios);
				tvName.setText(item + "");
			}
		};
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

	public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
		this.mListener = onSelectedListener;
	}

	public interface OnSelectedListener {
		/** 监听选中字典集合的序号 */
		void onSelected(int index);
	}
}
