package com.cedarhd.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.cedarhd.adapter.UploadAttachAdapter;
import com.cedarhd.adapter.UploadAttachAdapter.OnAdapterItemClickListener;
import com.cedarhd.biz.AttachBiz;
import com.cedarhd.biz.AttachInfoCache;
import com.cedarhd.biz.SelectPhotoBiz;
import com.cedarhd.constants.enums.EnumAttachType;
import com.cedarhd.helpers.BitmapHelper;
import com.cedarhd.helpers.DictIosPickerBottomDialog;
import com.cedarhd.helpers.DictIosPickerBottomDialog.OnSelectedListener;
import com.cedarhd.helpers.UploadHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.imp.IOnUploadMultipleFileListener;
import com.cedarhd.models.Attach;
import com.cedarhd.models.AttachInfo;
import com.cedarhd.utils.ImageUtils;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/***
 * 多附件显示上传控件
 *
 * @author K
 *         2016-01-01
 */
public class MultipleAttachView extends GridView {

    private final String TAG = "MultipleAttachView";

    private final static int CODE_DOWNLOAD_ATTACH_TYPE = 3;
    private Context mContext;
    private Handler mHandler;
    private DictIosPickerBottomDialog mDictIosPicker;
    private ZLServiceHelper mZlServiceHelper;
    private UploadAttachAdapter mAdapter;

    private String mAttachIds;
    List<AttachInfo> mAttachInfos;

    /**
     * 是否新建
     */
    private boolean mIsAdd;

    private static int mNumColumns = -1;

    public MultipleAttachView(Context context) {
        this(context, null);
    }

    public MultipleAttachView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultipleAttachView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        mDictIosPicker = new DictIosPickerBottomDialog(mContext);
        mZlServiceHelper = new ZLServiceHelper();
        initHandler();
        if (mNumColumns >= 1) {
            setNumColumns(mNumColumns);
        }
        LogUtils.i("mNumColumns", mNumColumns + "==" + getNumColumns());
        LogUtils.i(TAG, "MultipleAttachView=setAdapter");
        mAdapter = new UploadAttachAdapter(mContext, "", mIsAdd);
        setAdapter(mAdapter);
        setOnEvent();
    }

    public static MultipleAttachView getInstance(Context context, int numColumns) {
        mNumColumns = numColumns;
        return new MultipleAttachView(context);
    }

    private void setOnEvent() {
        mAdapter.setOnAdapterItemClickListener(new OnAdapterItemClickListener() {
            @Override
            public void onOpenUrl(int pos, List<AttachInfo> attachInfos) {
                if (pos < attachInfos.size()) {
                    AttachInfo attachInfo = attachInfos.get(pos);
                    Attach attach = attachInfo.getAttach();
                    if (attach != null) {
                        if (ImageUtils.isImage(attach.Suffix)) {
                            ImageUtils.startSingleImageBrower(mContext,
                                    attach.getAddress());
                        } else {
                            AttachBiz.startAttachActivity(mContext, attach);
                        }
                    } else if (attachInfo.getType() == EnumAttachType.本地路径 && !TextUtils.isEmpty(attachInfo.getLocalPath())) {
                        AttachBiz.startLocalImageActivity(mContext, attachInfo.getLocalPath());
                    }
                }
            }

            @Override
            public void onOpenLocal(int pos, List<AttachInfo> attachInfos) {

            }

            @Override
            public void onAdd() {
                if (mOnAddImageListener != null) {
                    mOnAddImageListener.onAddImageListener();
                }
                doPickPhoto();
                mAdapter.notifySetChangeIsDelete(false);
            }
        });

        /*setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                LogUtils.i(TAG, "长按...");
                mAdapter.notifySetChangeIsDelete(true);
                return true;
            }
        });*/

        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mAdapter.notifySetChangeIsDelete(false);
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        LogUtils.i(TAG, "onMeasure=" + width);

        mAdapter.setPicWidth(width / getNumColumns());
        // mAdapter.notifyDataSetChanged();
    }

    public boolean isAdd() {
        return mIsAdd;
    }

    public void setIsAdd(boolean isAdd) {
        mAdapter.notifySetChangeIsAdd(isAdd);
    }

    public void setIsDelete(boolean isDelete) {
        mAdapter.setIsDelete(isDelete);
    }

    /***
     * 加载指定附件编号的图片
     *
     * @param attachIds
     */
    public void loadImageByAttachIds(final String attachIds) {
        if (TextUtils.isEmpty(attachIds)) {
            //附件为空
            this.mAttachIds = attachIds;
            mAttachInfos = new ArrayList<AttachInfo>();
            mAdapter.addItems(mAttachInfos, true);
            return;
        }

        mAttachInfos = AttachInfoCache.getInstance().get(attachIds);
        if (mAttachInfos != null && mAttachInfos.size() > 0) {
            mAdapter.addItems(mAttachInfos, true);
        } else {
            if (!attachIds.equals(mAttachIds)) {
                mAttachInfos = getAttachInfos(attachIds);
                mAdapter.addItems(mAttachInfos, true);
            } else {
                this.mAttachIds = attachIds;
            }
            loadAttachsFromServer(attachIds, mAttachInfos);
        }
    }

    /***
     * 预加载指定附件，先占位不访问网络
     *
     * @param attachIds
     */
    public void preLoadImageByAttachIds(final String attachIds) {
        this.mAttachIds = attachIds;
        final List<AttachInfo> attachInfos = getAttachInfos(attachIds);
        mAdapter.addItems(attachInfos, true);
    }

    private List<AttachInfo> getAttachInfos(String attachIds) {
        mAttachInfos = new ArrayList<AttachInfo>();
        if (!TextUtils.isEmpty(attachIds)) {
            String[] mAttachArr = attachIds.split(",");
            for (String attachId : mAttachArr) {
                AttachInfo attachInfo = new AttachInfo();
                if (!TextUtils.isEmpty(attachId)) {
                    attachInfo.setIdAndUpdateType(attachId);
                    mAttachInfos.add(attachInfo);
                }
            }
        }
        return mAttachInfos;
    }

    private void loadAttachsFromServer(final String attachIds,
                                       final List<AttachInfo> attachInfos) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Attach> attachs = mZlServiceHelper.getAttachmentAddr(
                        mContext, attachIds);
                if (attachs != null) {
                    for (Attach attach : attachs) {
                        for (AttachInfo attachInfo : attachInfos) {
                            if (attachInfo.getType() == EnumAttachType.附件号
                                    && attachInfo.getId() == attach.getId()) {
                                attachInfo.setAttach(attach);
                                break;
                            }
                        }
                    }
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addItems(attachInfos, true);
                        AttachInfoCache.getInstance().put(attachIds, attachInfos);
                    }
                });
            }
        }).start();
    }

    /**
     * 按下拍照按钮
     */
    private void doPickPhoto() {
        String[] choices = new String[]{"拍照", "从相册中选择"};
        mDictIosPicker.show(choices);
        mDictIosPicker.setOnSelectedListener(new OnSelectedListener() {
            @Override
            public void onSelected(int index) {
                onSelectPicture(index);
            }
        });
    }

    private void onSelectPicture(int which) {
        switch (which) {
            case 0:
                SelectPhotoBiz.doTakePhoto(mContext);
                break;
            case 1:
                SelectPhotoBiz.selectPhoto(mContext);
                break;
        }
    }

    /***
     * 获取本地待上传路径
     *
     * @return
     */
    private List<String> getLocalPathList() {
        return mAdapter.getLocalPathList();
    }

    /**
     * 获取不包括新建 数据源
     */
    public List<AttachInfo> getAttachDataList() {
        return mAdapter.getAttachDataList();
    }

    public void uploadImage(final IOnUploadMultipleFileListener onUploadMultipleFileListener) {
        final UploadHelper uploadHelper = UploadHelper.getInstance();
        new Thread(new Runnable() {
            @Override
            public void run() {
                uploadHelper.uploadMultipleFiles(getLocalPathList(),
                        new IOnUploadMultipleFileListener() {
                            @Override
                            public void onProgressUpdate(int completeCount) {
                                if (onUploadMultipleFileListener != null) {
                                    onUploadMultipleFileListener
                                            .onProgressUpdate(completeCount);
                                }
                            }

                            @Override
                            public void onComplete(String AttachIds) {
                                String existAttachIds = mAdapter.getAttachIds();
                                if (TextUtils.isEmpty(existAttachIds)) {
                                    existAttachIds = AttachIds;
                                } else if (!TextUtils.isEmpty(AttachIds)) {
                                    existAttachIds += "," + AttachIds;
                                }

                                if (onUploadMultipleFileListener != null) {
                                    onUploadMultipleFileListener
                                            .onComplete(existAttachIds);
                                }
                            }

                            @Override
                            public void onStartUpload(int sum) {
                                if (onUploadMultipleFileListener != null) {
                                    onUploadMultipleFileListener
                                            .onStartUpload(sum);
                                }
                            }
                        });
            }
        }).start();
    }

    public void onActivityiForResultImage(int requestCode, int resultCode,
                                          Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            if (requestCode == SelectPhotoBiz.REQUESTCODE_TAKE_PHOTO) {
                String photoPath = SelectPhotoBiz.getPhotoPath(mContext);
                if (!TextUtils.isEmpty(photoPath)) {
                    Bitmap photoBitmap = BitmapHelper.zoomBitmap(photoPath,
                            100, 100);
                    if (photoBitmap != null) {
                        AttachInfo attachInfo = new AttachInfo();
                        attachInfo.setLocalPathUpdateType(photoPath);
                        mAdapter.addItem(attachInfo);
                    }
                }
            } else if (requestCode == SelectPhotoBiz.REQUESTCODE_SELECT_PHOTO) {
                List<String> pathList = SelectPhotoBiz
                        .getSelectPathListOnActivityForResult(data);
                if (pathList != null && pathList.size() != 0) {
                    List<AttachInfo> selectInfoList = new ArrayList<AttachInfo>();
                    for (String path : pathList) {
                        AttachInfo attachInfo = new AttachInfo();
                        attachInfo.setLocalPathUpdateType(path);
                        selectInfoList.add(attachInfo);
                    }
                    mAdapter.addItems(selectInfoList, false);
                }
            }
        }
    }

    private void initHandler() {
        mHandler = new Handler(mContext.getMainLooper()) {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case CODE_DOWNLOAD_ATTACH_TYPE:

                        break;

                    default:
                        break;
                }
            }

            ;
        };
    }

    private OnAddImageListener mOnAddImageListener;

    public void setOnAddImageListener(OnAddImageListener onAddImageListener) {
        this.mOnAddImageListener = onAddImageListener;
    }

    public interface OnAddImageListener {
        void onAddImageListener();
    }
}
