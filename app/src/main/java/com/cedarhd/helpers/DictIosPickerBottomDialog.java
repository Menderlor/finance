package com.cedarhd.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
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
public class DictIosPickerBottomDialog {
	private Dialog mDialog;
	private Context mContext;

	/** 是否是确定删除类型的对话框 */
	private boolean isSureAndCancel;
	private OnSelectedListener mListener;

	/**
	 * @param mContext
	 */
	public DictIosPickerBottomDialog(Context mContext) {
		this.mContext = mContext;
		mDialog = new Dialog(mContext, R.style.styleNoFrameDialog_bottom);
		mDialog.setCancelable(true);
	}

	/**
	 * 弹出IOS风格的确定选择
	 * 
	 * @param info
	 *            提示信息，如是否确定
	 */
	public void show(String info) {
		isSureAndCancel = true;
		ArrayList<String> list = new ArrayList<String>();
		list.add(info);
		show(list);
	}

	/**
	 * 弹出IOS风格的底部字典选择
	 * 
	 * @param datas
	 *            数据源
	 */
	public void show(String[] datas) {
		show(Arrays.asList(datas));
	}

	/**
	 * 弹出IOS风格的底部字典选择
	 * 
	 * @param mainLayoutId
	 *            layout文件的根节点id
	 * @param datas
	 *            数据源
	 */
	public void show(List<String> datas) {
		View view = View.inflate(mContext, R.layout.pop_dict_ios_picker, null);
		initViews(datas, view);
		mDialog.setContentView(view);
		show();
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
	public <T> void show(List<T> datas, String fieldName) {
		if (datas == null) {
			return;
		}

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
		show(list);
	}

	private void initViews(List<String> datas, View view) {

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
					isSureAndCancel = false; // 恢复默认值
					mDialog.dismiss();
					mListener.onSelected(position);
				}
			}
		});

		top.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mDialog.dismiss();
				}
			}
		});

	}

	private void show() {
		mDialog.show();
		WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
		lp.width = (int) (ViewHelper.getScreenWidth(mContext)); // 设置宽度
		lp.gravity = Gravity.BOTTOM;
		mDialog.getWindow().setAttributes(lp);
	}

	private CommanAdapter<String> getAdapter(List<String> datas) {
		return new CommanAdapter<String>(datas, mContext,
				R.layout.item_dict_ios) {
			@Override
			public void convert(int position, String item,
					BoeryunViewHolder viewHolder) {
				TextView tvName = viewHolder.getView(R.id.tv_name_dict_ios);
				tvName.setText(item + "");
				if (isSureAndCancel) {
					// 选择功能则 字体颜色为红色
					tvName.setTextColor(mContext.getResources().getColor(
							R.color.red));
				}
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
