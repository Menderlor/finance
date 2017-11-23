package com.cedarhd.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cedarhd.R;
import com.cedarhd.biz.AttachBiz;
import com.cedarhd.constants.enums.EnumAttachType;
import com.cedarhd.helpers.BitmapHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.models.Attach;
import com.cedarhd.models.AttachInfo;
import com.cedarhd.utils.ImageUtils;
import com.cedarhd.utils.StrUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class UploadAttachAdapter extends BaseAdapter {
    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 是否新建
     */
    private boolean mIsAdd = true;

    private boolean mIsDelete;
    private boolean canDelete;

    private List<AttachInfo> mAttachInfos = new ArrayList<AttachInfo>();

    /*** 默认图片宽度 85dp */
    private static int mPicWidth = 85;

    private final String ATTACH_METHOD = Global.BASE_URL
            + "FileUpDownLoad/downloadAttach/";

    /***
     *
     *
     * @param ctx
     *            当前上下文
     * @param urls
     *            图片下载url地址
     */
    public UploadAttachAdapter(Context ctx, List<String> urls, boolean isAdd) {
        this.mContext = ctx;
        this.mIsAdd = isAdd;
        mAttachInfos.clear();
        for (String url : urls) {
            AttachInfo attachInfo = new AttachInfo();
            mAttachInfos.add(attachInfo);
        }

        if (mIsAdd) {
            initAddView();
        }
    }

    /***
     *
     *
     * @param ctx
     *            当前上下文
     * @param urls
     *            图片下载url
     */
    public UploadAttachAdapter(Context ctx, String attachIds, boolean isAdd) {
        this.mContext = ctx;
        this.mIsAdd = isAdd;
        mAttachInfos.clear();
        String[] mAttachArr = attachIds.split(",");
        for (String attachId : mAttachArr) {
            AttachInfo attachInfo = new AttachInfo();
            attachInfo.setIdAndUpdateType(attachId);
            mAttachInfos.add(attachInfo);
        }
        initAddView();
    }

    private void initAddView() {
        AttachInfo attachInfo = getAddAttachInfo();
        if (mIsAdd) {
            mAttachInfos.add(attachInfo);
        } else {
            mAttachInfos.remove(attachInfo);
        }
    }

    private AttachInfo getAddAttachInfo() {
        AttachInfo attachInfo = new AttachInfo();
        attachInfo.setType(EnumAttachType.新建);
        return attachInfo;
    }

    public void notifySetChangeIsAdd(boolean isAdd) {
        if (mIsAdd != isAdd) {
            mIsAdd = isAdd;
            // 如果原来不可新建，则刷新页面显示新建图标
            initAddView();
            notifyDataSetChanged();
        }
    }

    public void setIsDelete(boolean isDelete) {
        canDelete = isDelete;
        notifyDataSetChanged();
    }

    public void notifySetChangeIsDelete(boolean isDelete) {
        mIsDelete = isDelete;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mAttachInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mAttachInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.item_attachinfo_grid, null);
        ImageView iv = (ImageView) view.findViewById(R.id.iv_attachinfo_item);

        ImageView ivDelete = (ImageView) view
                .findViewById(R.id.iv_delte_attachinfo_item);

        // RelativeLayout.LayoutParams params = new LayoutParams(mPicWidth,
        // mPicWidth);
        // iv.setLayoutParams(params);

        AttachInfo attachInfo = mAttachInfos.get(position);
        Attach attach = attachInfo.getAttach();

        String url = "";
        switch (attachInfo.getType()) {
            case 附件号:
                if (attach != null) {
                    if (ImageUtils.isImage(attach.Suffix)) {
                        url = ImageUtils.getDownloadUrlByAddress(attach
                                .getAddress());
                        ImageLoader.getInstance().displayImage(url, iv,
                                Global.mUser.Passport);
                    } else {
                        String suffix = StrUtils.pareseNull(attach.Suffix).replace(
                                ".", "");
                        iv.setImageResource(AttachBiz
                                .getImageResoureIdBySuffix(suffix));
                    }
                }

                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onOpenUrl(position, mAttachInfos);
                        }
                    }
                });
                break;
            case 本地路径:
                Bitmap bitmap = BitmapHelper.zoomBitmap(attachInfo.getLocalPath(),
                        mPicWidth, mPicWidth);
                iv.setImageBitmap(bitmap);
                break;
            case 新建:
                iv.setImageResource(R.drawable.ico_add_img);
                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onAdd();
                        }
                    }
                });
                break;
            default:
                break;
        }

        /**
         * 删除图片
         */
        ivDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mAttachInfos.remove(position);
                notifyDataSetChanged();
            }
        });

        iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (canDelete) {
                    notifySetChangeIsDelete(true);
                    return true;
                }
                return false;
            }
        });

        if (mIsDelete && attachInfo.getType() != EnumAttachType.新建) {
            ivDelete.setVisibility(View.VISIBLE);
        } else {
            ivDelete.setVisibility(View.GONE);
        }
        return view;
    }

    public int getPicWidth() {
        return mPicWidth;
    }

    public void setPicWidth(int mPicWidth) {
        this.mPicWidth = mPicWidth;
    }

    /**
     * 添加一个附件实体
     *
     * @param attachInfo
     */
    public void addItem(AttachInfo attachInfo) {
        if (mIsAdd) {
            if (mAttachInfos.size() >= 1) {
                mAttachInfos.add(mAttachInfos.size() - 1, attachInfo);
            }
        } else {
            mAttachInfos.add(attachInfo);
        }
        notifyDataSetChanged();
    }

    /**
     * 添加一个附件实体
     *
     * @param attachInfo
     */
    public void addItems(List<AttachInfo> addAttachInfos, boolean isClearOldData) {
        if (isClearOldData) {
            mAttachInfos.clear();
            initAddView();
        }

        if (mIsAdd && mAttachInfos != null && mAttachInfos.size() > 0) {
            mAttachInfos.addAll(mAttachInfos.size() - 1, addAttachInfos);
        } else {
            mAttachInfos.addAll(addAttachInfos);
        }
        notifyDataSetChanged();
    }

    /**
     * 获取指定类型的附件集合
     */
    private List<AttachInfo> getAttachInfoListByType(EnumAttachType type) {
        List<AttachInfo> pathList = new ArrayList<AttachInfo>();
        for (AttachInfo attachInfo : mAttachInfos) {
            if (attachInfo != null && attachInfo.getType() == type) {
                pathList.add(attachInfo);
            }
        }
        return pathList;
    }

    /**
     * 获取当地文件路径
     */
    public List<String> getLocalPathList() {
        List<String> pathList = new ArrayList<String>();
        List<AttachInfo> attachInfos = getAttachInfoListByType(EnumAttachType.本地路径);
        for (AttachInfo attachInfo : attachInfos) {
            if (attachInfo != null) {
                pathList.add(attachInfo.getLocalPath());
            }
        }
        return pathList;
    }

    /**
     * 获取不包括新建 数据源
     */
    public List<AttachInfo> getAttachDataList() {
        List<AttachInfo> pathList = new ArrayList<AttachInfo>();
        for (AttachInfo attachInfo : mAttachInfos) {
            if (attachInfo != null && attachInfo.getType() != EnumAttachType.新建) {
                pathList.add(attachInfo);
            }
        }
        return pathList;
    }

    /**
     * 获取已有附件号编号 1,2,3
     */
    public String getAttachIds() {
        StringBuilder sBuilder = new StringBuilder();
        List<AttachInfo> attachInfos = getAttachInfoListByType(EnumAttachType.附件号);
        for (AttachInfo attachInfo : attachInfos) {
            if (attachInfo != null && attachInfo.getId() != 0) {
                sBuilder.append(attachInfo.getId() + ",");
            }
        }
        String attachIds = sBuilder.toString();
        if (attachIds.endsWith(",")) {
            attachIds = attachIds.substring(0, attachIds.length() - 1);
        }
        return attachIds;
    }

    private OnAdapterItemClickListener mOnItemClickListener;

    public void setOnAdapterItemClickListener(
            OnAdapterItemClickListener onAdapterItemClickListener) {
        this.mOnItemClickListener = onAdapterItemClickListener;
    }

    public interface OnAdapterItemClickListener {
        void onAdd();

        void onOpenLocal(int pos, List<AttachInfo> attachInfos);

        void onOpenUrl(int pos, List<AttachInfo> attachInfos);
    }
}
