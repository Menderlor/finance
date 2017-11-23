package com.cedarhd;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.control.MultipleAttachView;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.imp.IOnUploadMultipleFileListener;
import com.cedarhd.models.User;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MessageUtil;

import java.util.Calendar;
import java.util.List;

/**
 * 新建通知
 *
 * @author bohr
 */
public class NoticeNewActivity extends BaseActivity implements OnClickListener {
    ZLServiceHelper mDataHelper = new ZLServiceHelper();
    private Context context;
    ListView mListView;
    private ProgressBar pBar;
    EditText mEditTextReceiverName;
    EditText mEditTextTitle;
    EditText mEditTextContent;
    LinearLayout llReceiver;
    LinearLayout ll_title_notice_info;
    // private EditText mEditTextExpirationTime; //发布时间
    private HorizontalScrollViewAddImage addImg_noticeinfo;
    private MultipleAttachView attachView;
    private AddImageHelper addImageHelper;
    private List<String> photoPathList; // 要上传照片路径列表

    /**
     * 用于输入备注信息
     */
    private Button btnSpeek2; // 说话按钮
    // ImageView ivSpeek2; // 喇叭按钮，选择说话
    ImageView ivKeybord2;// 键盘输入
    String mTitle = "";
    String mContent = "";
    String mReleaseTime = "";

    public String mUserSelectId = "";
    public String mUserSelectName = "";

    /**
     * 选择部门编号
     */
    private String mSelectedDeptId = "";
    private String mSelectedDeptName = "";

    /**
     * 选择了全体员工
     */
    private boolean mIsAllUser = false;

    private static final int SHOW_DATAPICKExpirationTime = 0;
    private static final int SHOW_WriteNotice = -1;
    private static final int CODE_SELECT_USER = 11;

    /**
     * 选择部门
     */
    private static final int CODE_SELECT_DEPARTMENT = 12;

    private int mYear;
    private int mMonth;
    private int mDay;

    public static final int RESULT_CODE_SUCCESS = 0;
    public static final int RESULT_CODE_FAILED = 1;
    public static final int UPDATA_FAILED = 2;
    public static final int UPDATA_SUCCESED = 3;
    public static final int EDIT_CONTENT_CODE = 7;
    public static final int EDIT_CONTENT_CODE_TITLE = 8;
    // private ImageButton showAttchment;

    private Handler upDataHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATA_FAILED:
                    ProgressDialogHelper.dismiss();
                    // 提示信息显示
                    pBar.setVisibility(View.GONE);
                    MessageUtil.ToastMessage(NoticeNewActivity.this, "发布失败！");
                    break;
                case UPDATA_SUCCESED:
                    ProgressDialogHelper.dismiss();
                    // 提示信息显示
                    MessageUtil.ToastMessage(NoticeNewActivity.this, "发布成功！");
                    setResult(RESULT_CODE_SUCCESS);
                    NoticeListActivity.isResume = true;
                    finish();
                    break;
            }
        }

        ;
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_info_new);
        findViews();
        setOnClickListener();
        Init();
    }

    void Init() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        setDateTime();

        registerForContextMenu(mEditTextReceiverName);
    }

    public void setOnClickListener() {
        ImageView imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
        imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageView imageViewDone = (ImageView) findViewById(R.id.imageViewDone);
        imageViewDone.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                if (!Verification_E()) {
                    return;
                }
                showDialog(SHOW_WriteNotice);
            }
        });

        // mEditTextTitle.setOnClickListener(this);
        // mEditTextTitle
        // .setOnFocusChangeListener(new View.OnFocusChangeListener() {
        // @Override
        // public void onFocusChange(View v, boolean hasFocus) {
        // if (hasFocus) {
        // new SpeechDialogHelper(context,
        // NoticeNewActivity.this, mEditTextTitle,
        // false);
        // }
        // }
        // });

        // mEditTextReceiverName.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // Intent intent = new Intent(NoticeNewActivity.this,
        // User_SelectActivityNew_zmy.class);
        // Bundle bundle = new Bundle();
        // bundle.putString("UserSelectId", mUserSelectId);
        // intent.putExtras(bundle);
        // startActivityForResult(intent, CODE_SELECT_USER);
        // }
        // });
        mEditTextContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(NoticeNewActivity.this,
                        TaskContentActivity.class);
                intent.putExtra(TaskContentActivity.EDITECONTENT, true);
                Bundle bundle = new Bundle();
                bundle.putString(TaskContentActivity.Content, mEditTextContent
                        .getText().toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, EDIT_CONTENT_CODE);
            }
        });
        mEditTextTitle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoticeNewActivity.this,
                        TaskContentActivity.class);
                intent.putExtra(TaskContentActivity.EDITECONTENT, true);
                Bundle bundle = new Bundle();
                bundle.putString(TaskContentActivity.Content, mEditTextTitle
                        .getText().toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, EDIT_CONTENT_CODE_TITLE);
            }
        });
        mEditTextTitle.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(NoticeNewActivity.this,
                        TaskContentActivity.class);
                intent.putExtra(TaskContentActivity.EDITECONTENT, true);
                startActivityForResult(intent, EDIT_CONTENT_CODE_TITLE);
            }
        });
        ll_title_notice_info.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(NoticeNewActivity.this,
                        TaskContentActivity.class);
                intent.putExtra(TaskContentActivity.EDITECONTENT, true);
                startActivityForResult(intent, EDIT_CONTENT_CODE_TITLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        setResult(RESULT_CODE_FAILED);
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("globalTag", Global.mUser);
        LogUtils.i("lifeState", "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (Global.mUser == null) {
            Global.mUser = (User) savedInstanceState
                    .getSerializable("globalTag");
        }
        LogUtils.i("lifeState", "onRestoreInstanceState");
        LogUtils.i("lifeState", Global.mUser.Passport);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CODE_SELECT_USER) {
                // 取出字符串
                Bundle bundle = data.getExtras();
                mUserSelectId = bundle.getString("UserSelectId");
                mUserSelectName = bundle.getString("UserSelectName");
                // 选择接收人
                mEditTextReceiverName.setText(mUserSelectName);

                mEditTextReceiverName.setTag(false);
                caculateHeight(llReceiver, mEditTextReceiverName,
                        mUserSelectName);
                LogUtils.i("testKeno", mUserSelectId + "======"
                        + mUserSelectName);
            } else {
                if (requestCode == addImageHelper.CAMERA_TAKE_HELPER
                        || requestCode == addImageHelper.PICKED_PHOTO_WITH_DATA) {
                    addImageHelper.refresh(requestCode, data);
                }
            }
            if (requestCode == EDIT_CONTENT_CODE) {
                // 取出通知内容
                Bundle bundle = data.getExtras();
                // etContent = bundle.getInt(ClientListActivity.ClientId);
                mEditTextContent.setText(bundle
                        .getString(TaskContentActivity.Content));

            }
            if (requestCode == EDIT_CONTENT_CODE_TITLE) {
                // 取出通知标题
                Bundle bundle = data.getExtras();
                mEditTextTitle.setText(bundle
                        .getString(TaskContentActivity.Content));

            }

            if (requestCode == CODE_SELECT_DEPARTMENT) {
                if (data == null) {
                    mSelectedDeptId = "";
                    mSelectedDeptName = "";
                } else {
                    Bundle bundle = data.getExtras();
                    mSelectedDeptId = bundle.getString("selectDepts");
                    mSelectedDeptName = bundle.getString("selectDeptsName");
                }
                // 选择接收人
                mEditTextReceiverName.setText(mSelectedDeptName + "");
            }

        }

        attachView.onActivityiForResultImage(requestCode,
                resultCode, data);

    }

    boolean Verification_E() {
        if (mEditTextTitle.getText() == null
                || mEditTextTitle.getText().toString().replaceAll(" ", "")
                .length() <= 0) {
            MessageUtil.AlertMessage(NoticeNewActivity.this, "保存失败", "标题不能为空！");
            return false;
        } else if (mEditTextTitle.getText().toString().trim().length() > 50) {
            MessageUtil.AlertMessage(NoticeNewActivity.this, "保存失败",
                    "标题不能多于50个字！");
            return false;
        } else {
            mTitle = mEditTextTitle.getText().toString().replaceAll(" ", "");
        }

        if (mEditTextContent.getText() == null
                || mEditTextContent.getText().toString().replaceAll(" ", "")
                .length() <= 0) {
            MessageUtil.AlertMessage(NoticeNewActivity.this, "保存失败",
                    "通知内容不能为空！");
            return false;
        } else {
            mContent = mEditTextContent.getText().toString()
                    .replaceAll(" ", "");
        }

        if (mUserSelectId.replaceAll(" ", "").length() <= 0
                && mIsAllUser == false && TextUtils.isEmpty(mSelectedDeptId)) {
            MessageUtil
                    .AlertMessage(NoticeNewActivity.this, "保存失败", "接收人不能为空！");
            return false;
        }

        return true;
    }

    public void findViews() {
        context = NoticeNewActivity.this;
        LinearLayout ll_publisher = (LinearLayout) findViewById(R.id.ll_publiser);
        findViewById(R.id.view_line_id).setVisibility(View.GONE);
        findViewById(R.id.ll_attchment_notice_info).setVisibility(View.GONE); // 隐藏附件列表
        findViewById(R.id.ll_publish_time_notic_info_new).setVisibility(
                View.GONE);
        findViewById(R.id.view_publish_time_notic_info_new).setVisibility(
                View.GONE);
        llReceiver = (LinearLayout) findViewById(R.id.ll_receiver_notice_info);

        ll_publisher.setVisibility(View.GONE); // 默认发布人为当前用户，隐藏
        ((TextView) findViewById(R.id.tv_notice_info_title)).setText("新建通知");
        pBar = (ProgressBar) findViewById(R.id.progressbar_addnotice);
        mEditTextTitle = (EditText) findViewById(R.id.editTextTitle);
        mEditTextTitle.setOnFocusChangeListener(onFocusAutoClearListener);
        mEditTextContent = (EditText) findViewById(R.id.editTextContent);
        mEditTextContent.setFocusable(false);
        mEditTextContent.setOnFocusChangeListener(onFocusAutoClearListener);
        mEditTextReceiverName = (EditText) findViewById(R.id.editTextReceiverName);
        mEditTextReceiverName
                .setOnFocusChangeListener(onFocusAutoClearListener);
        addImg_noticeinfo = (HorizontalScrollViewAddImage) findViewById(R.id.addImg_noticeinfo);
        attachView = (MultipleAttachView) findViewById(R.id.multipleattachview_notice_info);
        addImageHelper = new AddImageHelper(this, NoticeNewActivity.this,
                addImg_noticeinfo, null, true);

        // btnSpeek = (Button) findViewById(R.id.btn_speek_noticeinfo); // 语音 说话
        // ivSpeek = (ImageView) findViewById(R.id.iv_speek_noticeinfo); // 喇叭
        // ivKeybord = (ImageView) findViewById(R.id.iv_keybord_noticeinfo);//
        // 键盘
        btnSpeek2 = (Button) findViewById(R.id.btn_speek2_noticeinfo); // 语音 说话
        btnSpeek2.setVisibility(View.GONE);
        // ivSpeek2 = (ImageView) findViewById(R.id.iv_speek2_noticeinfo); // 喇叭
        ivKeybord2 = (ImageView) findViewById(R.id.iv_keybord2_noticeinfo);// 键盘
        ivKeybord2.setVisibility(View.GONE);
        // btnSpeek.setOnClickListener(this);
        // ivSpeek.setOnClickListener(this);
        // ivKeybord.setOnClickListener(this);
        btnSpeek2.setOnClickListener(this);
        // ivSpeek2.setOnClickListener(this);
        ivKeybord2.setOnClickListener(this);

        mEditTextContent.setHeight(100);
        // mEditTextTitle.setHint("请输入标题..");
        // mEditTextReceiverName.setHint("请选择接收人..");
        mEditTextContent.setHint("请输入通知内容..");

        mEditTextReceiverName.setCursorVisible(false);
        mEditTextReceiverName.setFocusable(false);
        mEditTextReceiverName.setFocusableInTouchMode(false);
        // ivAddParticipant = (ImageView)
        // findViewById(R.id.ivAddParticipant_addNotice);
        // showAttchment = (ImageButton) findViewById(R.id.show_attchment);
        // showAttchment.setVisibility(View.GONE);
        ll_title_notice_info = (LinearLayout) findViewById(R.id.ll_title_notice_info);
        attachView.loadImageByAttachIds("");
        attachView.setIsAdd(true);
        attachView.setIsDelete(true);
    }

    private void setDateTime() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case SHOW_WriteNotice:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("确认发布通知吗?")
                        .setCancelable(false)
                        .setPositiveButton("是",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
//                                        LogUtils.i("attach", "--->"
//                                                + addImageHelper.sbAttachIds);
//                                        photoPathList = addImageHelper
//                                                .getPhotoList();
//                                        for (String path : photoPathList) {
//                                            LogUtils.i("attachPath", path);
//                                        }
//                                        if (photoPathList.size() > 0) {
//                                            pBar.setVisibility(View.VISIBLE);
//                                            pBar.setMax(photoPathList.size());
//                                       }
                                        attachView.uploadImage(new IOnUploadMultipleFileListener() {
                                            @Override
                                            public void onStartUpload(int sum) {

                                            }

                                            @Override
                                            public void onProgressUpdate(int completeCount) {

                                            }

                                            @Override
                                            public void onComplete(final String attachIds) {
                                                ProgressDialogHelper
                                                        .show(context, "保存中...");
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            mDataHelper.WriteNotice(
                                                                    mUserSelectId, mTitle,
                                                                    mContent, mReleaseTime,
                                                                    mSelectedDeptId,
                                                                    mIsAllUser,
                                                                    attachIds,
                                                                    upDataHandler, pBar);
                                                        } catch (Exception e) {
                                                            upDataHandler
                                                                    .sendEmptyMessage(UPDATA_FAILED);
                                                        }
                                                    }

                                                }).start();
                                            }
                                        });


                                    }
                                })
                        .setNegativeButton("否",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                return alert;
        }

        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case SHOW_DATAPICKExpirationTime:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
                break;
        }
    }

    // 添加上下文菜单
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // 选择员工，三种模式（员工，部门，全体）
        // 添加菜单项
        menu.add(0, 0, 0, "选择员工");
        menu.add(0, 1, 0, "选择部门");
        menu.add(0, 2, 0, "全体员工");
    }

    // 选择上下文菜单内容
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = item.getItemId();

        // // 获得选中上下文菜单选中项的编号
        switch (position) {
            case 0: // 选择员工
                selectUser();
                mIsAllUser = false;
                break;
            case 1: // 选择部门
                mIsAllUser = false;
                selectDepartment();
                break;
            case 2:// 选择全体
                mIsAllUser = true;
                mEditTextReceiverName.setText("全体员工");
                break;
        }
        // statusIndex = position;
        // etContactStatus.setText(statusList[statusIndex]);
        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.editTextTitle:
                // new SpeechDialogHelper(context, this, mEditTextTitle, false);
                break;
            case R.id.btn_speek2_noticeinfo:
                new SpeechDialogHelper(context, this, mEditTextContent, true);
                break;
            case R.id.iv_keybord2_noticeinfo:
                // ivKeybord2.setVisibility(View.GONE);
                // btnSpeek2.setVisibility(View.GONE);
                // mEditTextContent.setVisibility(View.VISIBLE);
                // ivSpeek2.setVisibility(View.VISIBLE);
                mEditTextContent.requestFocus();
                // 弹出软键盘
                InputMethodManager m2 = (InputMethodManager) mEditTextTitle
                        .getContext().getSystemService(INPUT_METHOD_SERVICE);
                m2.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            default:
                break;
        }

    }

    public static OnFocusChangeListener onFocusAutoClearListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            EditText textView = (EditText) v;
            String hint;
            if (hasFocus) {
                hint = textView.getHint().toString();
                textView.setTag(hint);
                textView.setHint("");
            } else {
                hint = textView.getTag().toString();
                textView.setHint(hint);
            }
        }
    };

    /***
     * 选择员工
     */
    private void selectUser() {
        Intent intent = new Intent(NoticeNewActivity.this,
                User_SelectActivityNew_zmy.class);
        Bundle bundle = new Bundle();
        bundle.putString("UserSelectId", mUserSelectId);
        intent.putExtras(bundle);
        startActivityForResult(intent, CODE_SELECT_USER);
    }

    /***
     * 选择部门
     */
    private void selectDepartment() {
        Intent intent = new Intent(NoticeNewActivity.this,
                SelectDepartmnetActivity.class);
        startActivityForResult(intent, CODE_SELECT_DEPARTMENT);
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
}