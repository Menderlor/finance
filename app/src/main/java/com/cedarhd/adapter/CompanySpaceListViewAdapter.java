package com.cedarhd.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.cedarhd.AttachListActivity;
import com.cedarhd.ImagePagerActivity;
import com.cedarhd.R;
import com.cedarhd.ShareInfoActivity;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.AvartarViewHelper;
import com.cedarhd.control.BoeryunNoScrollGridView;
import com.cedarhd.control.CollapsibleTextView;
import com.cedarhd.control.MultipleAttachView;
import com.cedarhd.control.SlideMenu;
import com.cedarhd.control.TitlePopup;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.helpers.SpeechDialogHelper.OnCompleteListener;
import com.cedarhd.helpers.Util;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Attach;
import com.cedarhd.models.帖子;
import com.cedarhd.models.论坛回帖;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;

import java.util.ArrayList;
import java.util.List;

public class CompanySpaceListViewAdapter extends BaseAdapter {
    private List<帖子> mList;
    private Context mContext;
    /**
     * 用于获取论坛评论列表
     */
    private ZLServiceHelper zlHelper;
    private DictionaryHelper dictionaryHelper;
    /**
     * list评论列表
     */
    List<论坛回帖> listDiscuss = new ArrayList<论坛回帖>();
    private boolean mIsFling;// 是否滑动标识位

    public CompanySpaceListViewAdapter(Context pContext, List<帖子> mList) {
        this.mContext = pContext;
        this.mList = mList;
        zlHelper = new ZLServiceHelper();
        dictionaryHelper = new DictionaryHelper(mContext);
    }

    public List<帖子> getData() {
        return mList;
    }

    public void bindData(List<帖子> list) {
        mList = new ArrayList<帖子>();
        mList.addAll(list);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public 帖子 getItem(int arg0) {
        return mList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    AddImageHelper addImageHelper;
    com.cedarhd.models.论坛回帖 论坛回帖;

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext,
                    R.layout.companyspacelist_listviewlayout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ((SlideMenu) convertView).close(false);

        final 帖子 item = getItem(position);
        if (mList.get(position).getReplyList().size() <= 0) {
            holder.layout.setVisibility(View.GONE);
        } else {
            holder.layout.setVisibility(View.VISIBLE);
        }
        if (item.AttachImageList != null && item.AttachImageList.size() > 0) {
            holder.gridView.setVisibility(View.VISIBLE);
            NoScrollGridAdapter noGridAdapter = (NoScrollGridAdapter) holder.gridView
                    .getTag();
            if (noGridAdapter == null) {
                noGridAdapter = new NoScrollGridAdapter(mContext,
                        Global.BASE_URL, item.AttachImageList);
            } else {
                noGridAdapter.setImageUrls(item.AttachImageList);
            }
            holder.gridView.setAdapter(noGridAdapter);
        } else {
            holder.gridView.setVisibility(View.GONE);
        }
        final ArrayList<String> imageUrls = item.AttachImageList;
        String content = item.Content;
        String time = item.PostTime;
        holder.textViewTime.setText(DateDeserializer.getFormatTime(time));
        holder.tvDept.setText(StrUtils.pareseNull(dictionaryHelper.get分公司(
                mContext, item.Poster).get名称()));
        holder.textViewContent.setDesc(content, BufferType.NORMAL);
        holder.avartarView.setTag(position);
        holder.ivAttach.setTag(position);
        holder.layout.removeAllViews();

        if (!TextUtils.isEmpty(item.otherAttachIds)) {
            int attachCount = 1;
            if (item.otherAttachIds.contains(",")) {
                attachCount = item.otherAttachIds.split(",").length;
            }
            holder.tvAttachCount.setText(attachCount + "");
            holder.rlAttachs.setVisibility(View.VISIBLE);
            holder.rlAttachs.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,
                            AttachListActivity.class);
                    intent.putExtra(AttachListActivity.ATTACH_IDS,
                            item.otherAttachIds);
                    mContext.startActivity(intent);
                }
            });
        } else {
            holder.tvAttachCount.setText("0");
            holder.rlAttachs.setVisibility(View.GONE);
        }

        if (!mIsFling) {
            for (int i = 0; i < mList.get(position).ReplyList.size(); i++) {
                View view = View.inflate(mContext, R.layout.item_publish, null);
                TextView textView = (TextView) view.findViewById(R.id.tv_item_publish);
                ImageView iv = (ImageView) view.findViewById(R.id.iv_item_support);
//                TextView textView = new TextView(mContext);
                String content1 = mList.get(position).ReplyList.get(i).内容;

                if (content1.equals("zan")) {
                    iv.setVisibility(View.VISIBLE);
                }
                if (content1.equals("zan")) {
                    content1 = "";
                }
                textView.setText(mList.get(position).ReplyList.get(i).回帖人名称
                        + ":" + content1);
                holder.layout.addView(view);
            }

            // 头像
            new AvartarViewHelper(mContext, item.Poster, holder.avartarView,
                    position, 50, 50, true);

            // // 点击弹出popwindow
            holder.ivAttach.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    showComment(position, item, view);
                }
            });

            // 设置九宫格小图片点击 查看大图
            holder.gridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // 点击回帖九宫格，查看大图
                    startImageBrower(position, imageUrls);
                }
            });

            holder.textViewContent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ShareInfoActivity.TAG_INFO, item);
                    Intent intent = new Intent(mContext,
                            ShareInfoActivity.class);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
        }

        holder.tvDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((item.Poster + "").endsWith(Global.mUser.Id)) {
                    mList.remove(item);
                    notifyDataSetChanged();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            zlHelper.deleteTiezi(item.Id);
                        }
                    }).start();
                } else {
                    Toast.makeText(mContext, "不能删除其他人分享", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        return convertView;
    }

    public void setFling(boolean isFling) {
        this.mIsFling = isFling;
        notifyDataSetChanged();
    }

    /**
     * 打开可滑动的图片查看器
     *
     * @param position
     * @param urls2
     */
    protected void startImageBrower(int position, ArrayList<String> urls2) {
        Intent intent = new Intent(mContext, ImagePagerActivity.class);
        ArrayList<String> urlList = new ArrayList<String>();
        for (int i = 0; i < urls2.size(); i++) {
            urlList.add(Global.BASE_URL + urls2.get(i));
        }
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urlList);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        mContext.startActivity(intent);
    }

    private void showComment(final int position, final 帖子 item, View view) {
        final TitlePopup titlePopup = new TitlePopup(mContext, Util.dip2px(
                mContext, 180), Util.dip2px(mContext, 38));
        titlePopup.setAnimationStyle(R.anim.in_lefttoright);
        titlePopup.show(view);
        LinearLayout popu_praise = (LinearLayout) titlePopup.getContentView()
                .findViewById(R.id.popu_praise);
        LinearLayout popu_comment = (LinearLayout) titlePopup.getContentView()
                .findViewById(R.id.popu_comment);
        // 点赞
        popu_praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                LogUtils.i("out", mList.get(position).Content);
                论坛回帖 = new 论坛回帖("觉得很赞", Global.mUser.UserName);
                mList.get(position).ReplyList.add(论坛回帖);
                notifyDataSetChanged();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.i("pycontent", "觉得很赞");
                            LogUtils.i("pyid", item.Id + "");
                            zlHelper.publishRepaly(item.Id, "觉得很赞");
                        } catch (Exception e) {
                        }
                    }
                }).start();

                titlePopup.dismiss();
            }
        });
        // 评论
        popu_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                SpeechDialogHelper speechDialogHelper = new SpeechDialogHelper(
                        mContext, false);
                speechDialogHelper
                        .setOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(final String result) {
                                if (!TextUtils.isEmpty(result)) {
                                    论坛回帖 = new 论坛回帖(result,
                                            Global.mUser.UserName);
                                    mList.get(position).ReplyList.add(论坛回帖);
                                    notifyDataSetChanged();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                LogUtils.i("pycontent", "觉得很赞");
                                                LogUtils.i("pyid", item.Id + "");
                                                zlHelper.publishRepaly(item.Id,
                                                        result);
                                            } catch (Exception e) {
                                            }
                                        }
                                    }).start();
                                    titlePopup.dismiss();
                                } else {
                                    Toast.makeText(mContext, "评论内容不能为空",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    class ViewHolder {
        public AvartarView avartarView;
        public TextView textViewTime;
        public CollapsibleTextView textViewContent;
        public ImageView ivAttach;
        public RelativeLayout left_iv_attachment;
        public LinearLayout layout;
        public BoeryunNoScrollGridView gridView;
        public List<论坛回帖> listComment;
        public ListView list_fourm;
        public RelativeLayout rlAttachs;
        public TextView tvAttachCount;
        public TextView tvDelete;
        public TextView tvDept;
        public MultipleAttachView attchview;

        public ViewHolder(View convertView) {
            avartarView = (AvartarView) convertView
                    .findViewById(R.id.avatar_companyspacelist1);
            textViewTime = (TextView) convertView
                    .findViewById(R.id.textViewTime);
            textViewContent = (CollapsibleTextView) convertView
                    .findViewById(R.id.textViewContent);
            ivAttach = (ImageView) convertView.findViewById(R.id.iv_attachment);
            left_iv_attachment = (RelativeLayout) convertView
                    .findViewById(R.id.rela_right);
            layout = (LinearLayout) convertView.findViewById(R.id.line_group);
            gridView = (BoeryunNoScrollGridView) convertView
                    .findViewById(R.id.gv_company_list_item);
            list_fourm = (ListView) convertView.findViewById(R.id.list_count);
            rlAttachs = (RelativeLayout) convertView
                    .findViewById(R.id.rl_attach_conpanylist);
            tvAttachCount = (TextView) convertView
                    .findViewById(R.id.tv_count_attach);
            tvDelete = (TextView) convertView
                    .findViewById(R.id.tv_delete_companysapacelist_item);

            attchview = (MultipleAttachView) convertView.findViewById(R.id.attch_company);
            tvDept = (TextView) convertView
                    .findViewById(R.id.tv_dept_companyspacelist_item);

            attchview.setVisibility(View.GONE);
        }
    }

    // 存放非图片格式的附件信息
    List<Attach> listAttach = new ArrayList<Attach>();
    public final static int SUCESS_SHOW_ATTCHMENT = 102;
    public final static int SUCESS_DOWNLOAD_ATTACH = 103;
    public final static int FAILURE_SHOW_ATTCHMENT = 104;

}
