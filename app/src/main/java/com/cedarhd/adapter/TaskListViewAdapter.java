package com.cedarhd.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.TaskInfoActivity;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.AvartarViewHelper;
import com.cedarhd.control.SlideMenu;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.任务;
import com.cedarhd.utils.LogUtils;

import java.util.List;

/**
 * 任务列表内容适配器
 */
public class TaskListViewAdapter extends BaseAdapter {
	private List<任务> mList;
	private Context mContext;
	int mlistviewlayoutId;
	DictionaryHelper dictionaryHelper;
	private ZLServiceHelper zlServiceHelper;
	String[] arrs; // 状态数组

	/**
	 * 请求任务详情
	 */
	public static final int REQUEST_CODE_TASK_INFO = 101;
	private static final int SUCCEESS_DELETE_TASK = 7;
	private static final int FAILURE_DELETE_TASK = 8;
	/** 状态色块 */
	private int[] stateBgs = new int[] { R.drawable.ico_state_qidong,
			R.drawable.ico_state_zanting, R.drawable.ico_state_wancheng,
			R.drawable.ico_state_gezhi, R.drawable.ico_state_tijiao,
			R.drawable.ico_state_chongqi };

	public TaskListViewAdapter(Context pContext, int listviewlayoutId,
			List<任务> pList) {
		this.mContext = pContext;
		this.mlistviewlayoutId = listviewlayoutId;
		this.mList = pList;

		zlServiceHelper = new ZLServiceHelper();
		dictionaryHelper = new DictionaryHelper(mContext);
		arrs = mContext.getResources().getStringArray(R.array.statelist);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public 任务 getItem(int pos) {
		return mList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder holder;
		if (view == null || (holder = (ViewHolder) view.getTag()) == null) {
			view = View.inflate(mContext, mlistviewlayoutId, null);
			holder = new ViewHolder();
			holder.slideMenu = (SlideMenu) view;
			holder.llItem = (LinearLayout) view
					.findViewById(R.id.ll_item_tasklist);
			holder.tvState = (TextView) view
					.findViewById(R.id.tv_state_tasklist);
			// holder.tvState1 = (TextView) view
			// .findViewById(R.id.tv_state_tasklist1);
			holder.ivReadDot = (ImageView) view
					.findViewById(R.id.iv_dot_read_tasklist);
			holder.tvCommentCount = (TextView) view
					.findViewById(R.id.tv_comment_count_tasklist);
			holder.textViewPublisherName = (TextView) view
					.findViewById(R.id.textViewPublishName_tasklist);
			holder.textViewPartcipant = (TextView) view
					.findViewById(R.id.textViewParticipant_tasklist);
			holder.textViewTitle = (TextView) view
					.findViewById(R.id.textViewTitle_tasklist);
			holder.textViewState = (TextView) view
					.findViewById(R.id.textViewState_tasklist);
			holder.avartarView = (AvartarView) view
					.findViewById(R.id.control_avatar_tasklist);
			holder.textViewTime = (TextView) view
					.findViewById(R.id.textViewTime_tasklist);
			holder.textViewContent = (TextView) view
					.findViewById(R.id.textViewContent_tasklist);
			holder.tvDelete = (TextView) view
					.findViewById(R.id.tv_delete_task_item);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.slideMenu.close(false);
		holder.slideMenu.setPrimaryShadowWidth(0);
		final 任务 item = mList.get(position);
		holder.textViewTitle.setText(item.Content == null ? "" : item.Content);
		// 黄色启动，蓝色提交，绿色完成，搁置灰色，重启红色
		if (item.Status >= 1 && item.Status <= 6) {
			holder.textViewState.setText(arrs[item.Status - 1]);
			holder.tvState.setBackground(mContext.getResources().getDrawable(
					stateBgs[item.Status - 1]));
		} else {
			holder.tvState.setBackgroundColor(0x0000000); // 状态异常透明
			holder.textViewState.setText("状态异常");
		}
		holder.textViewPartcipant.setText("参与人："
				+ dictionaryHelper.getUserNamesById(item.Participant));
		holder.textViewPublisherName.setText(dictionaryHelper
				.getUserNameById(item.Executor)); // 执行人
		String time = item.AssignTime == null ? "" : DateDeserializer
				.getFormatTime(item.AssignTime); // 开始时间
		holder.textViewTime.setText(time);
		holder.textViewContent.setText(item.Content);
		holder.avartarView.setTag(position);
		AvartarViewHelper avartarViewHelper = new AvartarViewHelper(mContext,
				item.Publisher, holder.avartarView, position, 65, 65, true);

		if (item.CommentCount != 0) {
			holder.tvCommentCount.setVisibility(View.VISIBLE);
			holder.tvCommentCount.setText(item.CommentCount + "评");
		} else {
			holder.tvCommentCount.setVisibility(View.GONE);
		}
		String read = item.ReadTime;
		if (!TextUtils.isEmpty(read)) {
			holder.ivReadDot.setVisibility(View.INVISIBLE);
			avartarViewHelper.setRead(true);
		} else {
			// holder.ivReadDot.setVisibility(View.VISIBLE);
			holder.ivReadDot.setVisibility(View.INVISIBLE);
			avartarViewHelper.setRead(false);
		}

		holder.llItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LogUtils.i("listview2", "ListView下拉刷新");

				LogUtils.i("taskInfo", item.Title + ":" + item.Content + "-->"
						+ item.Attachment);
				mList.get(position).setReadTime(ViewHelper.getDateString());
				notifyDataSetChanged();

				Intent intent = new Intent(mContext, TaskInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(TaskInfoActivity.TAG, item);
				intent.putExtras(bundle);
				((Activity) mContext).startActivityForResult(intent,
						REQUEST_CODE_TASK_INFO);

				// 读任务
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							zlServiceHelper.ReadDynamic(item.Id, 3);
						} catch (Exception e) {
							LogUtils.e("Erro", "" + e);
						}
					}
				}).start();
			}
		});

		holder.tvDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Global.mUser.Id.equals(item.Publisher + "")) {
					mList.remove(item);
					notifyDataSetChanged();
					new Thread(new Runnable() {
						@Override
						public void run() {
							boolean isSuccess = zlServiceHelper.deleteTask(item
									.getId());
							Message msg = handler.obtainMessage();
							msg.obj = item;
							if (isSuccess) {
								msg.what = SUCCEESS_DELETE_TASK;
								handler.sendMessage(msg);
							} else {
								msg.what = FAILURE_DELETE_TASK;
								handler.sendMessage(msg);
							}
						}
					}).start();
				} else {
					Toast.makeText(mContext, "只能删除自己发布的任务", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
		return view;
	}

	public List<任务> getDataList() {
		return mList;
	}

	final class ViewHolder {
		public SlideMenu slideMenu;
		public LinearLayout llItem;
		public TextView tvState; // 左侧状态色块
		public ImageView ivReadDot; // 状态色块1
		public TextView textViewState;
		public TextView textViewPartcipant;
		public TextView textViewTitle;
		public TextView textViewPublisherName;
		public AvartarView avartarView;
		public TextView textViewTime;
		public TextView textViewContent;
		public TextView tvCommentCount; // 评价数量
		public TextView tvDelete;// 删除
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCEESS_DELETE_TASK:
				break;
			case FAILURE_DELETE_TASK:
				任务 item = (任务) msg.obj;
				mList.add(0, item);
				notifyDataSetChanged();
				Toast.makeText(mContext, "删除任务失败异常!", Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				break;
			}
		};
	};

}
