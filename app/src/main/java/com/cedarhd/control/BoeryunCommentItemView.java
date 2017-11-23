package com.cedarhd.control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.日志评论;
import com.cedarhd.models.论坛回帖;
import com.cedarhd.utils.StrUtils;

import java.util.ArrayList;
import java.util.List;

import static com.cedarhd.R.id.iv_support_share_info;

/***
 * 评论项
 *
 * @author Administrator
 */
public class BoeryunCommentItemView {

    private Context mContext;

    private List<日志评论> mList;

    private LinearLayout mLlRoot;

    private LayoutInflater inflater;

    public BoeryunCommentItemView(Context mContext, List<日志评论> mList,
                                  LinearLayout mLlRoot) {
        super();
        this.mContext = mContext;
        this.mList = mList;
        this.mLlRoot = mLlRoot;
        this.inflater = LayoutInflater.from(mContext);
        this.mLlRoot = mLlRoot;
    }

    public BoeryunCommentItemView(Context mContext, LinearLayout mLlRoot,
                                  List<论坛回帖> list) {
        super();
        this.mContext = mContext;
        this.mLlRoot = mLlRoot;
        this.inflater = LayoutInflater.from(mContext);
        this.mLlRoot = mLlRoot;
        this.mList = new ArrayList<日志评论>();
        for (int i = 0; i < list.size(); i++) {
            论坛回帖 item = list.get(i);
            日志评论 comment = new 日志评论(item.getId(),
                    ViewHelper.formatDateToStr(item.get回帖时间()), item.get回帖人(),
                    item.get论坛发帖() + "", item.get内容());
            mList.add(comment);
        }
    }

    public BoeryunCommentItemView(Context mContext, LinearLayout mLlRoot,
                                  List<论坛回帖> list, boolean isShowTime) {
        super();
        this.mContext = mContext;
        this.mLlRoot = mLlRoot;
        this.inflater = LayoutInflater.from(mContext);
        this.mLlRoot = mLlRoot;
        this.mList = new ArrayList<日志评论>();
        for (int i = 0; i < list.size(); i++) {
            论坛回帖 item = list.get(i);
            日志评论 comment = new 日志评论(item.编号,
                    ViewHelper.formatDateToStr(item.get回帖时间()), item.get回帖人(),
                    item.get论坛发帖() + "", item.get内容());
            mList.add(comment);
        }
    }

    public void createCommentView() {
        mLlRoot.removeAllViews();
        for (int i = 0; i < mList.size(); i++) {
            日志评论 itemComment = mList.get(i);
            addBottomComment(itemComment);
        }
    }

    /***
     * 底部添加一条评论
     *
     * @param itemComment
     */
    public void addBottomComment(日志评论 itemComment) {
        View view = inflater.inflate(R.layout.item_comment, null);
        AvartarView avartarView = (AvartarView) view
                .findViewById(R.id.avatar_conment_item);
        TextView tvContent = (TextView) view
                .findViewById(R.id.tv_content_comment_item);
        TextView tvTime = (TextView) view
                .findViewById(R.id.tv_time_comment_item);

        ImageView iv_support = (ImageView) view.findViewById(iv_support_share_info);

        new AvartarViewHelper(mContext, itemComment.发表人, avartarView, 50, 50,
                true);
        String con = itemComment.内容;

        if (con.equals("zan")) {
            con = "";
            iv_support.setVisibility(View.VISIBLE);
        } else {
            iv_support.setVisibility(View.GONE);
        }
        tvContent.setText(StrUtils.pareseNull(con));
        tvTime.setText(DateDeserializer.getFormatTime(itemComment.get发表时间())
                + "");
        mLlRoot.addView(view);
    }
}
