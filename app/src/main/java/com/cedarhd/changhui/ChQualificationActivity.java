package com.cedarhd.changhui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.BoeryunDateSelectView;
import com.cedarhd.control.BoeryunDictSelectView;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.control.MultipleAttachView;
import com.cedarhd.helpers.DictionaryQueryDialogHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.imp.IOnUploadMultipleFileListener;
import com.cedarhd.models.changhui.合格投资者认证申请单;
import com.cedarhd.models.changhui.理财产品;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.Timer;

/***
 * 合格投资者认证申请页面
 *
 * @author new
 */
public class ChQualificationActivity extends BaseActivity {

    /**
     * 线索实体 key
     */
    public static final String CLEW = "clew";

    /**
     * 大字典集合 key
     */
    public static final String DICTIONARYS = "dictionarys";

    private final int CODE_SELECT_PRODUCT_REQUES = 0x01;

    private int sum = 0; // 控件上传完毕的个数。

    private Context mContext;
    private int select_photo = 0;
    private boolean mIsEdit = true;
    private DictionaryQueryDialogHelper mDictionaryQueryDialogHelper;
//    private AddImageHelper imageHelper_chengnuo;
//    private AddImageHelper imageHelper_diaocha;
//    private AddImageHelper imageHelper_zhengming;
//
//    private HorizontalScrollViewAddImage addImage_chengnuo;
//    private HorizontalScrollViewAddImage addImage_diaocha;
//    private HorizontalScrollViewAddImage addImage_zhengming;


    private MultipleAttachView attachView_chengnuo;
    private MultipleAttachView attachView_diaocha;
    private MultipleAttachView attachView_zhengming;

    private Timer timer;


    /**
     * 投资期限和金额非法原因
     */
    private String mErroInfo;

    private 理财产品 mProduct;
    private 合格投资者认证申请单 mBespoke;
    private ZLServiceHelper zlServiceHelper;

    private BoeryunHeaderView headerView;
    private TextView tvSubmit;
    private TextView tvProduct;
    private TextView tvClient;
    private TextView tvClientID;//客户编号
    private TextView tvDangerLevel;//客户风险等级
    //    private TextView tvClientStyle; //客户类型
    private TextView tvPostCode; //客户邮政编码
    //    private TextView tvEmail;  //客户邮箱
    private TextView tvClientAddress; //客户地址
    private BoeryunDictSelectView tvCardType;
    private EditText etCardNo;
    private EditText etTotal;
    private EditText etPhone;
    private EditText etDeadline; // 投资期限
    private EditText etCategray;
    private BoeryunDateSelectView tvTime; // 打款时间
    private TextView tvClinetMannager; // 客户经理

    // 2016-12-22新添加字段
    private TextView tv_rengou_ch_bespoke_info;// 认购类型
    private TextView et_zuoji_ch_bespoke_info;// 座机
    private TextView et_email_ch_bespoke_info;//
    private TextView tv_total_big_ch_bespoke_info;// 金额大写
    private TextView tv_outtime_ch_bespoke_info;//
    private TextView et_nianhuashouyi_ch_bespoke_info;//
    private TextView et_kehujinglishouji_ch_bespoke_info;// 客户经理收益
    private TextView et_kehujingli_email_ch_bespoke_info;//
    private TextView tv_dept_ch_bespoke_info;//
    private TextView tv_company_bespoke_info;//

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (sum == 3) {
                        ProgressDialogHelper.dismiss();
                        if (isChecked()) {
                            submitContact();
                        }
                    }
                    break;
            }
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ch_qualification_qualificate_info);
        initData();
        initViews();
        showData();
        setOnEvent();

        setAllViewEnable();

        attachView_chengnuo.setIsAdd(true);
        attachView_chengnuo.setIsDelete(true);
        attachView_diaocha.setIsAdd(true);
        attachView_diaocha.setIsDelete(true);
        attachView_zhengming.setIsAdd(true);
        attachView_zhengming.setIsDelete(true);

        attachView_chengnuo.loadImageByAttachIds(mBespoke.投资者承诺函);
        attachView_diaocha.loadImageByAttachIds(mBespoke.投资者风险调查);
        attachView_zhengming.loadImageByAttachIds(mBespoke.合格投资者资产证明);


        attachView_chengnuo.setOnAddImageListener(new MultipleAttachView.OnAddImageListener() {
            @Override
            public void onAddImageListener() {
                select_photo = 1;
            }
        });

        attachView_diaocha.setOnAddImageListener(new MultipleAttachView.OnAddImageListener() {
            @Override
            public void onAddImageListener() {
                select_photo = 2;
            }
        });

        attachView_zhengming.setOnAddImageListener(new MultipleAttachView.OnAddImageListener() {
            @Override
            public void onAddImageListener() {
                select_photo = 3;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                default:
                    if (select_photo == 1) {
                        attachView_chengnuo.onActivityiForResultImage(requestCode,
                                resultCode, data);
                    }
                    if (select_photo == 2) {
                        attachView_diaocha.onActivityiForResultImage(requestCode,
                                resultCode, data);
                    }
                    if (select_photo == 3) {
                        attachView_zhengming.onActivityiForResultImage(requestCode,
                                resultCode, data);
                    }
                    break;
            }

        }

    }

    private void initData() {
        mContext = this;
//        mBespoke = new 理财产品预约();
        mBespoke = new 合格投资者认证申请单();
        zlServiceHelper = new ZLServiceHelper();
        mDictionaryQueryDialogHelper = DictionaryQueryDialogHelper
                .getInstance(mContext);
    }

    private void initViews() {
        headerView = (BoeryunHeaderView) findViewById(R.id.header_ch_bespoke_info);
        tvSubmit = (TextView) findViewById(R.id.tv_submit_ch_bespoke_info);
        tvProduct = (TextView) findViewById(R.id.tv_product_ch_bespoke_info);
        tvClient = (TextView) findViewById(R.id.tv_client_ch_bespoke_info);
        tvCardType = (BoeryunDictSelectView) findViewById(R.id.tv_card_type_ch_bespoke_info);
        etCardNo = (EditText) findViewById(R.id.et_card_no_ch_bespoke_info);
        etTotal = (EditText) findViewById(R.id.tv_bespoke_total_ch_bespoke_info);
        etPhone = (EditText) findViewById(R.id.tv_phone_ch_bespoke_info);
        etDeadline = (EditText) findViewById(R.id.tv_deadline_ch_bespoke_info);
        tvTime = (BoeryunDateSelectView) findViewById(R.id.tv_pay_time_ch_bespoke_info);
        tvClinetMannager = (TextView) findViewById(R.id.tv_client_mannager_ch_bespoke_info);
        etCategray = (EditText) findViewById(R.id.et_category_ch_bespoke_info);
        tvClientID = (TextView) findViewById(R.id.tv_client_id_bespoke_info);
        tvDangerLevel = (TextView) findViewById(R.id.tv_client_level_denger_bespoke_info);
        tvPostCode = (TextView) findViewById(R.id.tv_client_postcode_bespoke_info);
        tvClientAddress = (TextView) findViewById(R.id.tv_client_address_denger_bespoke_info);

        attachView_chengnuo = (MultipleAttachView) findViewById(R.id.multipleattachview_chengnuo_info);
        attachView_diaocha = (MultipleAttachView) findViewById(R.id.multipleattachview_diaocha_info);
        attachView_zhengming = (MultipleAttachView) findViewById(R.id.multipleattachview_zhengming_info);

        tv_rengou_ch_bespoke_info = (TextView) findViewById(R.id.tv_rengou_ch_bespoke_info);// 认购类型
        et_zuoji_ch_bespoke_info = (EditText) findViewById(R.id.et_zuoji_ch_bespoke_info);// 座机
        et_email_ch_bespoke_info = (TextView) findViewById(R.id.et_email_ch_bespoke_info);//
        tv_total_big_ch_bespoke_info = (TextView) findViewById(R.id.tv_total_big_ch_bespoke_info);// 金额大写
        tv_outtime_ch_bespoke_info = (TextView) findViewById(R.id.tv_outtime_ch_bespoke_info);//
        et_nianhuashouyi_ch_bespoke_info = (EditText) findViewById(R.id.et_nianhuashouyi_ch_bespoke_info);//
        et_kehujinglishouji_ch_bespoke_info = (EditText) findViewById(R.id.et_kehujinglishouji_ch_bespoke_info);// 客户经理收益
        et_kehujingli_email_ch_bespoke_info = (EditText) findViewById(R.id.et_kehujingli_email_ch_bespoke_info);//
        tv_dept_ch_bespoke_info = (TextView) findViewById(R.id.tv_dept_ch_bespoke_info);//
        tv_company_bespoke_info = (TextView) findViewById(R.id.tv_company_bespoke_info);//

        etCardNo.setFocusable(false);
        etPhone.setEnabled(false);
        etCategray.setEnabled(false);
        et_zuoji_ch_bespoke_info.setEnabled(false);
        et_email_ch_bespoke_info.setEnabled(false);
        et_kehujinglishouji_ch_bespoke_info.setEnabled(false);
        et_kehujingli_email_ch_bespoke_info.setEnabled(false);


    }

    /***
     * 显示客户详情
     */
    @SuppressWarnings("unchecked")
    private void showData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mBespoke = (合格投资者认证申请单) bundle.getSerializable(CLEW);
        }

        if (mBespoke != null) {
            tvSubmit.setVisibility(View.VISIBLE);
            mIsEdit = false;
            tvClient.setText(mBespoke.客户名称);
            tvCardType.setText(mBespoke.证件类别名称);
            etCardNo.setText(StrUtils.pareseNull(mBespoke.证件号));
            etPhone.setText(StrUtils.pareseNull(mBespoke.手机));
            tvClinetMannager.setText(mBespoke.客户经理名称);
            et_zuoji_ch_bespoke_info.setText(mBespoke.座机);// 座机
            et_email_ch_bespoke_info.setText(mBespoke.邮箱);//
            tv_dept_ch_bespoke_info.setText(mBespoke.所属部门名称);
            tv_company_bespoke_info.setText(mBespoke.所属分公司名称);
            tv_rengou_ch_bespoke_info.setText(mBespoke.客户类型名称);
            tvClientID.setText(mBespoke.客户编号);
            tvDangerLevel.setText(mBespoke.风险测评类型名称);
            tvClientAddress.setText(mBespoke.联系地址);
            tvPostCode.setText(mBespoke.邮政编码);
        }
    }

    private void setAllViewEnable() {
        // 如果不可编辑，则隐藏保存按钮
        headerView.ivSave.setVisibility(View.GONE);

        tvProduct.setEnabled(false);
        tvClient.setEnabled(false);
        tvCardType.setEnabled(false);
        etCardNo.setEnabled(false);
        etTotal.setEnabled(false);
        etPhone.setEnabled(false);
        etDeadline.setEnabled(false);
        tvTime.setEnabled(false);
        tvClinetMannager.setEnabled(false);
    }

    private void setOnEvent() {

        headerView.setOnButtonClickListener(new OnButtonClickListener() {
            @Override
            public void onClickSaveOrAdd() {

            }

            @Override
            public void onClickFilter() {

            }

            @Override
            public void onClickBack() {
                finish();
            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAttch();
            }
        });
    }


    /**
     * 上传附件
     */
    private void uploadAttch() {
        ProgressDialogHelper.show(mContext, "保存中...");
        sum = 0;  //重新调用上传附件的方法，上传和置零；

        attachView_chengnuo.uploadImage(new IOnUploadMultipleFileListener() {
            @Override
            public void onStartUpload(int sum) {

            }

            @Override
            public void onProgressUpdate(int completeCount) {

            }

            @Override
            public void onComplete(String attachIds) {
                LogUtils.i(TAG, "投资者承诺函附件：" + attachIds);
                sum = sum + 1;
                mBespoke.投资者承诺函 = attachIds;
                handler.sendEmptyMessage(1);
            }
        });

        attachView_diaocha.uploadImage(new IOnUploadMultipleFileListener() {
            @Override
            public void onStartUpload(int sum) {

            }

            @Override
            public void onProgressUpdate(int completeCount) {

            }

            @Override
            public void onComplete(String attachIds) {
                LogUtils.i(TAG, "投资者风险调查：" + attachIds);
                sum = sum + 1;
                mBespoke.投资者风险调查 = attachIds;
                handler.sendEmptyMessage(1);
            }
        });

        attachView_zhengming.uploadImage(new IOnUploadMultipleFileListener() {
            @Override
            public void onStartUpload(int sum) {

            }

            @Override
            public void onProgressUpdate(int completeCount) {

            }

            @Override
            public void onComplete(String attachIds) {
                LogUtils.i(TAG, "合格投资者资产证明：" + attachIds);
                sum = sum + 1;
                mBespoke.合格投资者资产证明 = attachIds;
                handler.sendEmptyMessage(1);
            }
        });

    }

    private boolean isChecked() {

        if (mBespoke != null) {

            if (TextUtils.isEmpty(mBespoke.投资者承诺函)) {
                showShortToast("请添加投资者承诺函");
                return false;
            }

            if (TextUtils.isEmpty(mBespoke.投资者风险调查)) {
                showShortToast("请添加投资者风险调查");
                return false;
            }

            if (TextUtils.isEmpty(mBespoke.合格投资者资产证明)) {
                showShortToast("请添加合格投资者资产证明");
                return false;
            }

        }
        return true;
    }

    private void submitContact() {
        ProgressDialogHelper.show(mContext);
        String url = Global.BASE_URL + "Customer/SaveAndSubmitCertificationOrder";
        StringRequest.postAsyn(url, mBespoke, new StringResponseCallBack() {
            @Override
            public void onResponseCodeErro(String result) {
                tvSubmit.setClickable(true);
                ProgressDialogHelper.dismiss();
                String error = JsonUtils.pareseMessage(result);
                showShortToast(error);
            }

            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
                try {
                    String msg = JsonUtils.getStringValue(response, JsonUtils.KEY_MESSAGE);
                    if (msg.contains("成功")) {
                        ChClientListActivity.isResume = true;
                        showShortToast("提交成功");
                        finish();
                    } else {
                        showShortToast(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                tvSubmit.setClickable(true);
                ProgressDialogHelper.dismiss();
                showShortToast(INFO_ERRO_SERVER);
            }
        });
    }

    /**
     * 判断客户信息是否完整
     *
     * @return
     */
    private boolean showUpdateClientInfo() {
        if (TextUtils.isEmpty(mBespoke.风险测评类型 + "")
                || TextUtils.isEmpty(mBespoke.证件类别 + "")
                || TextUtils.isEmpty(mBespoke.证件号)
                || TextUtils.isEmpty(mBespoke.手机)
                || TextUtils.isEmpty(mBespoke.联系地址)
                ) {
            showShortToast("客户信息不完整，请回到客户页面完善");
            return true;
        } else {
            return false;
        }
    }
}
