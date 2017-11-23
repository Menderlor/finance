package com.cedarhd;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.helpers.BitmapHelper;
import com.cedarhd.helpers.DictIosPicker;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.UploadHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Attach;
import com.cedarhd.models.User;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

/**
 * 设置头像
 */
public class SettingAvatarActivity extends BaseActivity {

    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;

    /* 用来标识请求gallery的activity */
    private static final int PICKED_PHOTO_WITH_DATA = 3021;

    private static final int CHOOSE_PHOTO = 3303;

    /* 拍照的照片存储位置 */
    private String filePath = Environment.getExternalStorageDirectory()
            + "/DCIM";
    private String thumbFilePath = filePath + File.separator + "thumb"; // 缩略图文件名
    // 头像存放路径
    private String avatarPath = FilePathConfig.getAvatarDirPath();
    private String avatarName; // 头像名称
    private Bitmap photo; // 头像照片
    private String fileName; // 文件名
    private String absoluteFileName; // 文件全路径
    ZLServiceHelper mDataHelper = new ZLServiceHelper();
    private DictionaryHelper dictionaryHelper;
    private DictIosPicker mDictIosPicker;

    private static HandlerSetting handler;

    TextView mTextViewTime;
    ImageView mImageViewCamera;
    ProgressBar mProgressBar;
    private TextView company;
    private TextView user;

    public class HandlerSetting extends Handler {
        public static final int UPLOAD_SUCCESS = 1;
        public static final int UPLOAD_FAILURE = 2;
        public static final int SHOW_IMAGE_SUCCESS = 3;
        public static final int SHOW_IMAGE_FAILUREE = 4;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what == UPLOAD_SUCCESS) {
                ProgressDialogHelper.dismiss();
                Toast.makeText(SettingAvatarActivity.this, "上传成功",
                        Toast.LENGTH_SHORT).show();
//				SettingActivity
            } else if (what == UPLOAD_FAILURE) {
                ProgressDialogHelper.dismiss();
                Toast.makeText(SettingAvatarActivity.this, "上传头像失败",
                        Toast.LENGTH_SHORT).show();
            } else if (what == SHOW_IMAGE_SUCCESS) {
                byte[] data = (byte[]) msg.obj;
                Bitmap avatar = BitmapFactory.decodeByteArray(data, 0,
                        data.length);
                LogUtils.i("leo2", "----->" + data.length);
                mImageViewCamera.setImageBitmap(avatar);
            } else if (what == SHOW_IMAGE_FAILUREE) { // 显示头像失败，默认显示本地
                mImageViewCamera.setImageResource(R.drawable.camera);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatart_settings);

        findViews();
        setOnClickListener();
    }

    public void setOnClickListener() {
        ImageView imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel_settings);
        imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView imageViewDone = (ImageView) findViewById(R.id.imageViewDone);
        imageViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存头像
                LogUtils.i("kjxTest", "absoluteFileName--->" + absoluteFileName);
                uploadImg(); // 上传图片
            }
        });

        ImageView imageViewCamera = (ImageView) findViewById(R.id.imageViewCamera);
        imageViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPickPhotoAction();
            }
        });
    }

    public void findViews() {
        new File(avatarPath).mkdirs();
        new File(thumbFilePath).mkdirs(); // 如果该路径不存在，创建

        mTextViewTime = (TextView) findViewById(R.id.textViewTime);
        String today = (new java.util.Date()).toLocaleString();
        mTextViewTime.setText(today);
        mImageViewCamera = (ImageView) findViewById(R.id.imageViewCamera);
        handler = new HandlerSetting();
        dictionaryHelper = new DictionaryHelper(getApplicationContext());
        mDictIosPicker = new DictIosPicker(this);
        final String avataUrl = dictionaryHelper.getUserPhoto(Global.mUser.Id); // 头像地址
        company = (TextView) findViewById(R.id.tv_userinfo_setting_company);
        user = (TextView) findViewById(R.id.tv_useinfo_setting_user);
        user.setText(Global.mUser.UserName);
        company.setText(Global.mUser.CorpName);
        LogUtils.i("avataUrl", avataUrl);
        // 如果本地数据库中存有用户头像信息
        if (!TextUtils.isEmpty(avataUrl) && avataUrl.contains("\\")) {
            LogUtils.i("avataUrl", avataUrl);
            int index = avataUrl.lastIndexOf("\\");
            avatarName = avataUrl.substring(index + 1, avataUrl.length());
            // 本地图片文件
            if (!TextUtils.isEmpty(avatarName)) {
                File file = new File(new File(avatarPath), avatarName);
                if (file.exists()) { // 本地有图直接使用
                    Bitmap avatar = BitmapFactory.decodeFile(file.toString());
                    mImageViewCamera.setImageBitmap(getCroppedBitmap(avatar,
                            avatar.getWidth()));
                } else {
                    // 开启线程从网络下载
                    final HttpUtils httpUtils = new HttpUtils();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                httpUtils.downloadData(avataUrl, handler);
                            } catch (Exception e) {
                                Toast.makeText(SettingAvatarActivity.this,
                                        "下载数据异常", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).start();
                }
            } else {
                // 开启线程从网络下载
                final HttpUtils httpUtils = new HttpUtils();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            httpUtils.downloadData(avataUrl, handler);
                        } catch (Exception e) {
                            Toast.makeText(SettingAvatarActivity.this,
                                    "下载网数据异常", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();
            }
        }
    }

    /*
     * 对Bitmap裁剪，使其变成圆形，这步最关键
     */
    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;

        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
                Bitmap.Config.ARGB_8888);
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(Color.parseColor("#BAB399"));

        Canvas c = new Canvas(output);
        c.drawARGB(0, 0, 0, 0);
        c.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f,
                sbmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        c.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

    /**
     * 按下拍照按钮
     */
    private void doPickPhotoAction() {
        String[] choices = new String[]{"拍照", "相册中选择"};
        mDictIosPicker.show(R.id.root_avatar, choices);
        mDictIosPicker.setOnSelectedListener(new DictIosPicker.OnSelectedListener() {
            @Override
            public void onSelected(int index) {
                switch (index) {
                    case 0: {
                        String status = Environment.getExternalStorageState();
                        if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
                            doTakePhoto();// 用户点击了从照相机获取
                        } else {
                            Toast.makeText(SettingAvatarActivity.this, "没有SD卡", Toast.LENGTH_LONG)
                                    .show();
                        }
                        break;
                    }
                    case 1:
//                        doPickPhotoFromGallery();// 从相册中去获取
                        pickPhoto();
                        break;
                }
            }
        });

    }


    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//选择图片格式
        intent.setType("image/*");
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    /**
     * 拍照获取图片
     */
    protected void doTakePhoto() {
        Intent intent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        fileName = getPhotoFileName();
        // 文件全路径
        absoluteFileName = filePath + File.separator + fileName;
        LogUtils.i("KJX", absoluteFileName);
        File file = new File(absoluteFileName);
        Uri uri = Uri.fromFile(file);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, CAMERA_WITH_DATA);
    }

    /***
     * 请求Gallery程序,打开相册
     */
    protected void doPickPhotoFromGallery() {
        try {
            final Intent intent = getPhotoPickIntent(null);
            startActivityForResult(intent, PICKED_PHOTO_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "失败", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 用当前时间给取得的图片命名
     */
    private String getPhotoFileName() {
        return "IMG"
                + DateFormat.format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA)) + ".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case CHOOSE_PHOTO: {
                Intent intent = getCropImageIntent(data.getData());
                startActivityForResult(intent, PICKED_PHOTO_WITH_DATA);
                break;
            }
            case PICKED_PHOTO_WITH_DATA: {// 调用Gallery返回的
                fileName = getPhotoFileName();
                if (data == null) {
                    Toast.makeText(SettingAvatarActivity.this, "图片系统异常..",
                            Toast.LENGTH_SHORT).show();
                } else {
                    photo = data.getParcelableExtra("data");
                    if (photo == null) {
                        Toast.makeText(SettingAvatarActivity.this,
                                "获取图片异常,请尝试选择相机", Toast.LENGTH_SHORT).show();
                    } else {
                        mImageViewCamera.setImageBitmap(photo);
                        // 下面就是显示照片了
                        absoluteFileName = thumbFilePath + File.separator
                                + fileName;
                        // 保存头像缩略图
                        BitmapHelper.createThumBitmap(absoluteFileName, photo);
                        LogUtils.i("kjxTest", "-------->" + absoluteFileName);
                    }
                }
                break;
            }
            case CAMERA_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                doCropPhoto(absoluteFileName);
                break;
            }
        }
    }

    protected void doCropPhoto(String fileName) {
        try {
            File file = new File(fileName);
            Uri uri = Uri.fromFile(file);
            // 启动gallery去剪辑这个照片
            final Intent intent = getCropImageIntent(uri);
            startActivityForResult(intent, PICKED_PHOTO_WITH_DATA);
        } catch (Exception e) {
            Toast.makeText(this, "failure", Toast.LENGTH_LONG).show();
        }
    }

    // 封装请求Gallery的intent
    public static Intent getPhotoPickIntent(Uri uri) {
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//        Intent intent = new Intent(Intent.ACTION_PICK);
        Intent intent = new Intent("com.android.camera.action.CROP", uri);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 138);
        intent.putExtra("outputY", 138);
        intent.putExtra("return-data", true);
        return intent;
    }

    /**
     * Constructs an intent for image cropping. 调用图片剪辑程序
     */
    public static Intent getCropImageIntent(Uri photoUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 138); // 宽
        intent.putExtra("outputY", 138); // 高
        intent.putExtra("return-data", true);
        return intent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("globalUser", Global.mUser);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (Global.mUser == null) {
            Global.mUser = (User) savedInstanceState
                    .getSerializable("globalUser");
        }
    }

    /**
     * 上传头像
     */
    private void uploadImg() {
        if (absoluteFileName == null) {
            return;
        }
        final File file = new File(absoluteFileName);
        if (!file.exists()) {
            Toast.makeText(getApplicationContext(), "请先设置头像再保存",
                    Toast.LENGTH_SHORT).show();
        } else {
            ProgressDialogHelper.show(this, "保存中..");
            // 开启线程上传头像
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 上传头像完成 获得头像再服务器中的地址，存入用户表
                        Attach attach = UploadHelper
                                .uploadFileByHttpGetAttach(file);
                        mDataHelper.updateAvatar(attach,
                                getApplicationContext(), handler);
                    } catch (Exception e) {
                        Toast.makeText(SettingAvatarActivity.this, "上传头像异常", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }).start();
        }
    }
}