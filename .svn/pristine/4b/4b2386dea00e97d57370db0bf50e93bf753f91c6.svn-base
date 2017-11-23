package com.cedarhd;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.AttchmentListHelper;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.control.MultipleAttachView;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.OpenFilesIntent;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Attach;
import com.cedarhd.models.User;
import com.cedarhd.models.动态;
import com.cedarhd.models.通知;
import com.cedarhd.utils.DateTimeUtil;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 通知详情
 *
 * @author KJX
 */
public class NoticeActivity extends BaseActivity {
    ZLServiceHelper mZLServiceHelper = new ZLServiceHelper();
    private Context context;
    DictionaryHelper dictionaryHelper;
    ListView mListView;
    HorizontalScrollViewAddImage addImg_noticeinfo;
    private MultipleAttachView attachView;
    private LinearLayout ll_attachImg_noticeinfo;
    通知 mNotice;
    // TextView tvContent;
    EditText mEditTextTitle;
    EditText mEditTextContent;
    // EditText mEditTextReceiverName; //不显示接收人
    EditText mEditTextPubliser;
    LinearLayout llTitle;
    LinearLayout llReceiver;
    LinearLayout llContent;
    private File file;
    private String fileName;
    private EditText mEditTextExpirationTime;

    public final static int SUCESS_SHOW_ATTCHMENT = 2;
    public final static int SUCESS_DOWNLOAD_ATTACH = 3;
    public final static int FAILURE_SHOW_ATTCHMENT = 4;
    public final int GET_DYNAMIC_SUCCESS = 7; // 点击动态，获取通知成功
    public final int GET_DYNAMIC_FAILED = 8; // 点击动态，获取通知失败
    private AddImageHelper addImageHelper;
    private LinearLayout llAttchment; // 附件内容显示区
    private ListView lvAttchment; // 附件列表
    private AttchmentListHelper attchmentListHelper;
    // 存放非图片格式的附件信息
    List<Attach> listAttach = new ArrayList<Attach>();
    private AlertDialog dialog;
    private int pos;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SUCESS_SHOW_ATTCHMENT:
                    if (listAttach != null && listAttach.size() != 0) {
                        attchmentListHelper = new AttchmentListHelper(
                                NoticeActivity.this, listAttach, lvAttchment,
                                llAttchment);
                        int height = (int) ViewHelper.dip2px(NoticeActivity.this,
                                attchmentListHelper.getHeight());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LayoutParams.MATCH_PARENT, height);
                        lvAttchment.setLayoutParams(params);
                    } else {
                        llAttchment.setVisibility(View.GONE);
                    }
                    break;
                case FAILURE_SHOW_ATTCHMENT:
                    llAttchment.setVisibility(View.GONE);
                    break;
                case SUCESS_DOWNLOAD_ATTACH:
                    Toast.makeText(NoticeActivity.this, "文件下载完毕", Toast.LENGTH_LONG)
                            .show();
                    dialog.dismiss();
                    Attach attach = listAttach.get(pos);
                    // 下载附件成功
                    int index = attach.Address.lastIndexOf("\\");
                    String picName = attach.Address.substring(index + 1,
                            attach.Address.length()); // 图片名
                    String attachaddr = FilePathConfig.getAvatarDirPath()
                            + File.separator + picName;
                    File attachfile = new File(attachaddr);
                    if (attachfile.exists()) {
                        open(attachfile);
                    }
                    break;
                case GET_DYNAMIC_SUCCESS: // 点击动态，获取日志成功
                    ProgressDialogHelper.dismiss();
                    动态 dynamic = (动态) msg.obj;
                    mNotice = dynamic.Notice;
                    if (mNotice != null) {
                        Init();
                    }
                    break;
                case GET_DYNAMIC_FAILED: // 获取通知失败
                    ProgressDialogHelper.dismiss();
                    Toast.makeText(context, "加载通知失败，请稍后重试", Toast.LENGTH_LONG)
                            .show();
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_info_new);
        context = NoticeActivity.this;
        findViews();
        setOnClickListener();

        // 获取Intent中携带的通知实体，并显示
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            Object obj = bundle.getSerializable("Notice");
            if (obj != null && obj instanceof 通知) {
                mNotice = (通知) obj;
                Init();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 监听信鸽 Notification点击打开的通知
        XGPushClickedResult clickedResult = XGPushManager
                .onActivityStarted(this);
        if (clickedResult != null) {
            // Toast.makeText(this, "来自信鸽：" + clickedResult.toString(),
            // Toast.LENGTH_LONG).show();
            LogUtils.i("clickedResult", clickedResult.toString());
            String customContent = clickedResult.getCustomContent();
            LogUtils.i("CustomContent", customContent);
            try {
                JSONObject jo = new JSONObject(customContent);
                // 获取动态类型 和 数据编号
                final String dynamicType = jo.getString("dynamicType");
                final String dataId = jo.getString("dataId");
                LogUtils.i("dynami", dynamicType + "--" + dataId);
                ProgressDialogHelper.show(NoticeActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        动态 dynamic = mZLServiceHelper.LoadDynamicById(
                                dynamicType, dataId);
                        if (dynamic != null && dynamic.Notice != null) {
                            // 设置通知为已读
                            // mZLServiceHelper.ReadLog(dynamic.Log, context);
                            mZLServiceHelper
                                    .ReadNotice(dynamic.Notice, context);

                            // 发送到handler中进行处理
                            Message msg = handler.obtainMessage();
                            msg.obj = dynamic;
                            msg.what = GET_DYNAMIC_SUCCESS;
                            handler.sendMessage(msg);
                        } else {
                            handler.sendEmptyMessage(GET_DYNAMIC_FAILED);
                        }
                    }
                }).start();
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtils.e("erro", e.toString());
                handler.sendEmptyMessage(GET_DYNAMIC_FAILED);
            }
        }
    }

    /**
     * 初始化页面
     */
    private void Init() {
        dictionaryHelper = new DictionaryHelper(getApplicationContext());
        mEditTextTitle.setText(mNotice.Title);
        mEditTextExpirationTime.setText(DateTimeUtil
                .ConvertLongDateToString(mNotice.ReleaseTime));
        mEditTextPubliser.setText(dictionaryHelper
                .getUserNameById(mNotice.Publisher));
        // String personnelName = dictionaryHelper
        // .getUserNamesById(mNotice.Personnel);
        // mEditTextReceiverName.setText(personnelName);
        mEditTextContent.setText(mNotice.Content);
        if (TextUtils.isEmpty(mNotice.Attachment)) {
//            addImg_noticeinfo.setVisibility(View.GONE);
//            ll_attachImg_noticeinfo.setVisibility(View.GONE);
//            llAttchment.setVisibility(View.GONE);
            attachView.setVisibility(View.GONE);
        } else {
            attachView.setVisibility(View.VISIBLE);
            attachView.setIsAdd(false);
            attachView.loadImageByAttachIds(mNotice.Attachment);
//            addImageHelper = new AddImageHelper(this,
//                    this.getApplicationContext(), addImg_noticeinfo,
//                    mNotice.Attachment, false);
//            addImageHelper.reload(mNotice.Attachment);
//            initAttachs();
        }
        mEditTextTitle.setTag(false);
        mEditTextContent.setTag(false);
        caculateHeight(llTitle, mEditTextTitle, mNotice.getTitle());
        // caculateHeight(llContent, mEditTextContent, mNotice.Content);

        // mEditTextReceiverName.setTag(false);
        // caculateHeight(llReceiver, mEditTextReceiverName, personnelName);
    }

    /**
     * 根据文字内容计算重绘EditText的高度
     *
     * @param linearLayout 父控件
     * @param editText     文本控件
     * @param contents     文字内容
     */
    private void caculateHeight(final LinearLayout linearLayout,
                                final EditText editText, final String contents) {
        // 监听控件绘制
        ViewTreeObserver vto = editText.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                Boolean hasMessured = (Boolean) editText.getTag();
                if (hasMessured == false) {
                    editText.setTag(true);
                    int width = editText.getWidth(); // 控件宽度
                    int height = editText.getHeight(); // 控件高度
                    if (width != 0 && height != 0) {
                        if (!TextUtils.isEmpty(contents)) {
                            // 显示文字个数字数
                            int len = contents.length();
                            // 得到字体像素
                            float px = editText.getTextSize();
                            LogUtils.i("time", "字体像素：" + px + "，控件宽度：" + width);
                            double length = Math.floor(width / px); // 能容纳字母个数
                            if (len > length) {
                                int llWidth = linearLayout.getLayoutParams().width;
                                int offset = (int) (len / length); // 计算出需要行数
                                linearLayout
                                        .setLayoutParams(new LinearLayout.LayoutParams(
                                                llWidth, (int) (height + px
                                                * offset)));
                            }

                        }
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("global", Global.mUser);
        LogUtils.i("lifeState", "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Global.mUser = (User) savedInstanceState.getSerializable("global");
        LogUtils.i("lifeState", "onRestoreInstanceState");
        LogUtils.i("lifeState", Global.mUser.Passport);
    }

    public void setOnClickListener() {
        ImageView imageViewDone = (ImageView) findViewById(R.id.imageViewDone);
        imageViewDone.setVisibility(View.GONE);
        ImageView imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
        imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lvAttchment.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                pos = position;
                Attach attach = (Attach) parent.getItemAtPosition(position);
                LogUtils.i("noticeAtta", attach.Name + "-------"
                        + attach.Address);

                int index = attach.Address.lastIndexOf("\\");
                String picName = attach.Address.substring(index + 1,
                        attach.Address.length()); // 图片名
                String attachaddr = FilePathConfig.getAvatarDirPath()
                        + File.separator + picName;
                File attachfile = new File(attachaddr);
                if (attachfile.exists()) {
                    open(attachfile);
                } else {
                    showDownLoadDialog(attach);
                }
            }
        });
    }

    public void findViews() {
        findViewById(R.id.ll_speech).setVisibility(View.GONE);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView_content_notice);
        scrollView.getParent().requestDisallowInterceptTouchEvent(true);
        // tvContent = (TextView) findViewById(R.id.tv_content_notice);
        llTitle = (LinearLayout) findViewById(R.id.ll_title_notice_info);
        llReceiver = (LinearLayout) findViewById(R.id.ll_receiver_notice_info);
        llReceiver.setVisibility(View.GONE);
        findViewById(R.id.view_personel_notic_info_new)
                .setVisibility(View.GONE);
        llContent = (LinearLayout) findViewById(R.id.ll_content_notice_info);

        mEditTextTitle = (EditText) findViewById(R.id.editTextTitle);
        mEditTextContent = (EditText) findViewById(R.id.editTextContent);
        // mEditTextReceiverName = (EditText)
        // findViewById(R.id.editTextReceiverName);
        mEditTextPubliser = (EditText) findViewById(R.id.editTextPublisher);
        mEditTextExpirationTime = (EditText) findViewById(R.id.showdateExpirationTime);
        addImg_noticeinfo = (HorizontalScrollViewAddImage) findViewById(R.id.addImg_noticeinfo);
        ll_attachImg_noticeinfo = (LinearLayout) findViewById(R.id.ll_attachImg_noticeinfo);
        attachView = (MultipleAttachView) findViewById(R.id.multipleattachview_notice_info);

        llAttchment = (LinearLayout) findViewById(R.id.ll_attchment_notice_info);
        lvAttchment = (ListView) findViewById(R.id.lv_attchment_notice_info);

        mEditTextTitle.setEnabled(false);
        mEditTextContent.setEnabled(false);
        mEditTextPubliser.setEnabled(false);
        mEditTextExpirationTime.setEnabled(false);
    }

    private void initAttachs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    String attachIds = mNotice.Attachment.replace("'", "")
                            .replace("\"", "");
                    LogUtils.i("noticeAttach", attachIds);
                    List<Attach> attachs = mZLServiceHelper.getAttachmentAddr(
                            NoticeActivity.this, attachIds);
                    String suffix = "";
                    if (attachs != null && attachs.size() > 0) {
                        for (Attach attach : attachs) {
                            if (attach != null) {
                                suffix = attach.Suffix.toLowerCase();
                                if (!TextUtils.isEmpty(suffix)
                                        && !suffix.contains("png")
                                        && !suffix.contains("jpg")
                                        && !suffix.contains("gif")) {
                                    listAttach.add(attach);
                                }
                            }
                        }
                        handler.sendEmptyMessage(SUCESS_SHOW_ATTCHMENT);
                    } else {
                        handler.sendEmptyMessage(FAILURE_SHOW_ATTCHMENT);
                    }
                } catch (Exception e) {
                    Toast.makeText(NoticeActivity.this, "初始化附件数据异常",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }).start();
    }

    // 定义用于检查要打开的附件文件的后缀是否在遍历后缀数组中
    private boolean checkEndsWithInStringArray(String checkItsEnd,
                                               String[] fileEndings) {
        for (String aEnd : fileEndings) {
            if (checkItsEnd.endsWith(aEnd))
                return true;
        }
        return false;
    }

    // 打开附件文件的方法
    private void open(File currentPath) {
        try {

            if (currentPath != null && currentPath.isFile()) {
                String fileName = currentPath.toString();
                LogUtils.i("pathname", "-->" + fileName);
                Intent intent;
                if (checkEndsWithInStringArray(fileName, getResources()
                        .getStringArray(R.array.fileEndingImage))) {
                    intent = OpenFilesIntent.getImageFileIntent(currentPath);
                    startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName, getResources()
                        .getStringArray(R.array.fileEndingWebText))) {
                    intent = OpenFilesIntent.getHtmlFileIntent(currentPath);
                    startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName, getResources()
                        .getStringArray(R.array.fileEndingPackage))) {
                    intent = OpenFilesIntent.getApkFileIntent(currentPath);
                    startActivity(intent);

                } else if (checkEndsWithInStringArray(fileName, getResources()
                        .getStringArray(R.array.fileEndingAudio))) {
                    intent = OpenFilesIntent.getAudioFileIntent(currentPath);
                    startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName, getResources()
                        .getStringArray(R.array.fileEndingVideo))) {
                    intent = OpenFilesIntent.getVideoFileIntent(currentPath);
                    startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName, getResources()
                        .getStringArray(R.array.fileEndingText))) {
                    intent = OpenFilesIntent.getTextFileIntent(currentPath);
                    startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName, getResources()
                        .getStringArray(R.array.fileEndingPdf))) {
                    intent = OpenFilesIntent.getPdfFileIntent(currentPath);
                    startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName, getResources()
                        .getStringArray(R.array.fileEndingWord))) {
                    intent = OpenFilesIntent.getWordFileIntent(currentPath);
                    startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName, getResources()
                        .getStringArray(R.array.fileEndingExcel))) {
                    intent = OpenFilesIntent.getExcelFileIntent(currentPath);
                    startActivity(intent);
                } else if (checkEndsWithInStringArray(fileName, getResources()
                        .getStringArray(R.array.fileEndingPPT))) {
                    intent = OpenFilesIntent.getPPTFileIntent(currentPath);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "无法打开，请安装相应的软件！", Toast.LENGTH_LONG)
                            .show();
                    // showMessage("无法打开，请安装相应的软件！");
                }
            } else {
                Toast.makeText(this, "对不起，这不是文件！", Toast.LENGTH_LONG).show();
                // showMessage("对不起，这不是文件！");
            }
        } catch (Exception e) {
            LogUtils.e("openFileErro", "无法打开，请安装相应的软件！");
            Toast.makeText(this, "无法打开，请安装相应的软件！", Toast.LENGTH_LONG).show();
        }
    }

    private void showDownLoadDialog(final Attach attach) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                NoticeActivity.this);

        builder.setTitle("下载");
        builder.setMessage("是否下载文件： " + attach.Name);
        builder.setPositiveButton("立即下载", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final HttpUtils httpUtils = new HttpUtils();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            httpUtils.downloadData(attach.Address, handler);
                        } catch (Exception e) {
                            // Toast.makeText(NoticeActivity.this, "下载网络数据异常",
                            // Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();
            }
        });
        builder.setNegativeButton("稍后再说", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }
}