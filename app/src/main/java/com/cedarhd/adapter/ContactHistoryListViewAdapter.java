package com.cedarhd.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.AvartarViewHelper;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.models.Client;
import com.cedarhd.models.changhui.CH客户联系记录;
import com.cedarhd.utils.StrUtils;

import java.util.List;

public class ContactHistoryListViewAdapter extends BaseAdapter {

	View.OnClickListener myAdapterCBListener;
	// private List<HashMap<String, Object>> mHashMapList;
	// private List<联系拜访记录> mList;
	private List<CH客户联系记录> mList;
	private List<Client> clientLists;
	private Context mContext;
	private DictionaryHelper dictionaryHelper;
	private boolean isFling;// 是否滑动标识位
	int mlistviewlayoutId;

	public ContactHistoryListViewAdapter(Context pContext,
			int contacthistorylistListviewlayout, List<CH客户联系记录> list,
			OnClickListener listener) {
		this.mContext = pContext;
		this.mlistviewlayoutId = contacthistorylistListviewlayout;
		this.mList = list;
		this.myAdapterCBListener = listener;
		dictionaryHelper = new DictionaryHelper(mContext);
	}

	/**
	 * 从内存中查询数据库
	 * 
	 * @param pContext
	 * @param contacthistorylistListviewlayout
	 * @param list
	 * @param clientLists
	 * @param listener
	 */
	public ContactHistoryListViewAdapter(Context pContext,
			int contacthistorylistListviewlayout, List<CH客户联系记录> list,
			List<Client> clientLists, OnClickListener listener) {
		this.mContext = pContext;
		this.mlistviewlayoutId = contacthistorylistListviewlayout;
		this.mList = list;
		this.clientLists = clientLists;
		this.myAdapterCBListener = listener;
		dictionaryHelper = new DictionaryHelper(mContext);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public CH客户联系记录 getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		View view = convertView;
		if (view == null) {
			view = View.inflate(mContext, mlistviewlayoutId, null);
			ViewHolder holder = new ViewHolder();
			holder.ivIco = (ImageView) view.findViewById(R.id.iv_ico_contact);
			holder.tvClientName = (TextView) view
					.findViewById(R.id.tv_clientname_contact);
			holder.tvContactName = (TextView) view
					.findViewById(R.id.tv_contactName_contact);
			holder.tvContent = (TextView) view
					.findViewById(R.id.tv_content_contact);
			// holder.tvSaler = (TextView) view
			// .findViewById(R.id.tv_saler_contact);
			holder.avartarView = (AvartarView) view
					.findViewById(R.id.control_avatar);
			holder.tvStatus = (TextView) view
					.findViewById(R.id.tv_status_contact);
			holder.tvContactTime = (TextView) view
					.findViewById(R.id.tv_contactTime_contact);
			holder.tvCount = (TextView) view
					.findViewById(R.id.tv_disscuss_count_contact);
			holder.isRead = (ImageView) view.findViewById(R.id.iv_read_contact);
			view.setTag(holder);
		}

		ViewHolder holder = (ViewHolder) view.getTag();
		CH客户联系记录 item = getItem(position);
		holder.ivIco.setImageResource(R.drawable.notice_icon01);
		// holder.tvClientName.setText(dictionaryHelper
		// .getClientNameById(item.Customer)); // 用户名称
		// holder.tvClientName.setText(getClientName(item.Customer));
		holder.tvClientName.setText(item.客户名称);
		if (!TextUtils.isEmpty(item.联系人) && !item.联系人.equals("null")) {
			holder.tvContactName.setText("联系人:" + item.联系人);
			// holder.tvContactName.setText(dictionaryHelper.getUserNameById(item
			// .getSaler()));
		} else {
			holder.tvContactName.setText("联系人");
		}
		String time = item.时间 == null ? "" : DateDeserializer
				.getFormatTime(item.时间);
		// String time = item.PrepareTime == null ? "" : item.PrepareTime;
		holder.tvContactTime.setText(time);
		String saleChanceContent = TextUtils.isEmpty(item.内容) ? "" : item.内容;
		holder.tvContent.setText("内容：" + saleChanceContent);
		if (item.评论数 == 0) {
			holder.tvCount.setVisibility(View.GONE);
		} else {
			holder.tvCount.setVisibility(View.VISIBLE);
			holder.tvCount.setText(item.评论数 + "");
		}
		// holder.tvSaler.setText("业务员:"
		// + dictionaryHelper.getUserNameById(item.Saler));// 联系人
		if (item.业务员 != 0 && !isFling) { // 甩动状态不加载图片
			holder.avartarView.setTag(position);
			new AvartarViewHelper(mContext, item.业务员, holder.avartarView,
					position, 50, 50, true);
		}
		if (!StrUtils.isNullOrEmpty(item.联系形式名称)) {
			holder.tvStatus.setVisibility(View.VISIBLE);
			holder.tvStatus.setText("联系方式：" + item.联系形式名称);
		} else {
			holder.tvStatus.setVisibility(View.GONE);
		}

		String read = item.已读时间;
		if (!TextUtils.isEmpty(read)) {
			holder.isRead.setVisibility(View.INVISIBLE);// 已读
		} else {
			holder.isRead.setVisibility(View.VISIBLE); // 未读
		}
		return view;
	}

	final class ViewHolder {
		public ImageView ivIco;
		public TextView tvClientName;
		public TextView tvContactTime;
		public TextView tvContactName;
		// public TextView tvSaler;
		public AvartarView avartarView;
		public TextView tvStatus;
		public TextView tvContent;
		public TextView tvCount;
		public ImageView isRead;
	}

	// 设置滚动监听状态
	public void setFling(boolean isFling) {
		this.isFling = isFling;
		if (!isFling) {
			this.notifyDataSetChanged();
		}
	}

	public List<CH客户联系记录> getDataList() {
		return mList;
	}

	private String getClientName(int id) {
		String name = "";
		Client item客户 = new Client();
		if (clientLists != null) {
			for (int i = 0; i < clientLists.size(); i++) {
				item客户 = clientLists.get(i);
				if (item客户 != null && item客户.getId() == id) {
					name = item客户.CustomerName;
					break;
				}
			}
		}
		return name;
	}
}
