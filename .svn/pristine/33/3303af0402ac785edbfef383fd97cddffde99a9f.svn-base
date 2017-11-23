package com.cedarhd.biz;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.cedarhd.AttachListActivity;
import com.cedarhd.R;
import com.cedarhd.models.Attach;
import com.cedarhd.utils.StrUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * 处理附件的相关业务逻辑
 */
public class AttachBiz {
    /**
     * 打开附件下载页面
     */
    public static void startAttachActivity(Context context,
                                           ArrayList<Attach> attachs) {
        Intent intent = new Intent(context, AttachListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AttachListActivity.ATTACH_LIST, attachs);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 打开附件下载页面
     */
    public static void startAttachActivity(Context context, Attach attach) {
        ArrayList<Attach> attachs = new ArrayList<Attach>();
        attachs.add(attach);
        startAttachActivity(context, attachs);
    }

    /**
     * 打开本地图片地址下载页面
     */
    public static void startLocalImageActivity(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);// 调用系统的图片查看器
        Uri mUri = Uri.fromFile(new File(path));// 图片的路径
        intent.setDataAndType(mUri, "image/*");// 设置数据和格式
        context.startActivity(intent);
    }

    /***
     * 根据文件后缀返回对应图片resId
     *
     * @param suffix
     * @return
     */
    public static int getImageResoureIdBySuffix(String suffix) {
        int resId = R.drawable.ico_other;
        suffix = StrUtils.pareseNull(suffix).replace(".", "");
        if (suffix.equalsIgnoreCase("doc") || suffix.equalsIgnoreCase("docx")) {
            resId = R.drawable.ico_doc;
        } else if (suffix.equalsIgnoreCase("xls")
                || suffix.equalsIgnoreCase("xlsx")) {
            resId = R.drawable.ico_excel;
        } else if (suffix.equalsIgnoreCase("ppt")
                || suffix.equalsIgnoreCase("pptx")) {
            resId = R.drawable.ico_ppt;
        } else if (suffix.equalsIgnoreCase("txt")) {
            resId = R.drawable.ico_txt;
        } else if (suffix.equalsIgnoreCase("zip")
                || suffix.equalsIgnoreCase("rar")) {
            resId = R.drawable.ico_zip;
        } else {
            resId = R.drawable.ico_other;
        }
        return resId;
    }

}
