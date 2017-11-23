package com.cedarhd.changhui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.biz.DeptBiz;
import com.cedarhd.biz.DictionaryBiz;
import com.cedarhd.control.BoeryunDateSelectView;
import com.cedarhd.control.BoeryunDictSelectView;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.control.MultipleAttachView;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.DateAndTimePicker.ISelected;
import com.cedarhd.helpers.DictionaryQueryDialogHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.imp.IOnUploadMultipleFileListener;
import com.cedarhd.models.Client;
import com.cedarhd.models.Dict;
import com.cedarhd.models.changhui.VmContractCustomerInfo;
import com.cedarhd.models.changhui.理财产品;
import com.cedarhd.models.changhui.理财产品预约;
import com.cedarhd.models.changhui.预期年化收益率;
import com.cedarhd.utils.DoubleUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MoneyUtils;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import static com.cedarhd.utils.JsonUtils.pareseData;

/***
 * 追加预约界面
 *
 * @author new
 */
public class ChAddBespokeInfoActivity extends BaseActivity {

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
    private 理财产品预约 mBespoke;
    private HashMap<String, List<Dict>> mDictionarys;
    private ZLServiceHelper zlServiceHelper;

    private String reservationId = "";

    private BoeryunHeaderView headerView;
    private TextView tvSubmit;
    private TextView tvProduct;
    private TextView tvClient; //客户名称
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
    private TextView tv_bespoke_money_add_ch_bespoke_info;//追加金额
    private TextView tv_total_money_ch_bespoke_info;//预约总金额
    private TextView tv_total_money_big_ch_bespoke_info;//预约总金额大写


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (sum == 3) {
                        ProgressDialogHelper.dismiss();
                        if (!showUpdateClientInfo() && isChecked()) {
                            if (TextUtils.isEmpty(mErroInfo)) {
                                saveBespoke();
                            } else {
                                showShortToast(mErroInfo);
                            }
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
        setContentView(R.layout.activity_ch_add_bespoke_info);
        initData();
        initViews();
        showData();
        setOnEvent();

        setAllViewEnable();

        if (!mIsEdit) { // 如果是详情设置页面不可编辑
            enabled();
        }

//        imageHelper_chengnuo = new AddImageHelper(ChBespokeInfoActivity.this, mContext, addImage_chengnuo, StrUtils.pareseNull(mBespoke.投资者承诺函), mIsEdit);
//        imageHelper_diaocha = new AddImageHelper(ChBespokeInfoActivity.this, mContext, addImage_diaocha, StrUtils.pareseNull(mBespoke.投资者风险调查), mIsEdit);
//        imageHelper_zhengming = new AddImageHelper(ChBespokeInfoActivity.this, mContext, addImage_zhengming, StrUtils.pareseNull(mBespoke.合格投资者资产证明), mIsEdit);


        attachView_chengnuo.setIsAdd(mIsEdit);
        attachView_diaocha.setIsAdd(mIsEdit);
        attachView_zhengming.setIsAdd(mIsEdit);

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


//        imageHelper_chengnuo.ibAdd.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                select_photo = 1;
//                imageHelper_chengnuo.addImage(select_photo);
//            }
//        });
//
//        imageHelper_diaocha.ibAdd.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                select_photo = 2;
//                imageHelper_diaocha.addImage(select_photo);
//            }
//        });
//
//
//        imageHelper_zhengming.ibAdd.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                select_photo = 3;
//                imageHelper_zhengming.addImage(select_photo);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ClientBiz.SELECT_CLIENT_CODE:
                    Client client = ClientBiz.onActivityGetClient(
                            ClientBiz.SELECT_CLIENT_CODE, data);
                    if (client != null) {
                        tvClient.setText(client.getCustomerName() + "");
                        mBespoke.客户 = client.Id;

                        getContractCustomerInfoById(mBespoke.客户);
                    }
                    break;
                case CODE_SELECT_PRODUCT_REQUES:
                    if (data != null) {
                        理财产品 product = (理财产品) data.getExtras().getSerializable(
                                ChProductListActivity.EXTRAS_SELECT_PRODUCT);
                        if (product != null) {
                            mBespoke.理财产品 = product.编号;
                            tvProduct.setText(product.产品名称);
                        }
                    }
                    break;
                case 101:
                case 3021:
//                    if (select_photo == 1) {
//                        imageHelper_chengnuo.refresh(requestCode, data);
//                    } else if (select_photo == 2) {
//                        imageHelper_diaocha.refresh(requestCode, data);
//                    } else if (select_photo == 3) {
//                        imageHelper_zhengming.refresh(requestCode, data);
//                    }
                    break;

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

    private void enabled() {
        et_zuoji_ch_bespoke_info.setEnabled(false);
        et_email_ch_bespoke_info.setEnabled(false);
        etCategray.setEnabled(false);
        et_nianhuashouyi_ch_bespoke_info.setEnabled(false);
        et_kehujinglishouji_ch_bespoke_info.setEnabled(false);
        et_kehujingli_email_ch_bespoke_info.setEnabled(false);
    }

    private void initData() {
        mContext = this;
        mBespoke = new 理财产品预约();
        zlServiceHelper = new ZLServiceHelper();
        mDictionarys = new HashMap<String, List<Dict>>();
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
//        tvClientStyle = (TextView) findViewById(R.id.tv_client_style_bespoke_info);
        tvPostCode = (TextView) findViewById(R.id.tv_client_postcode_bespoke_info);
//        tvEmail = (TextView) findViewById(R.id.tv_client_email__bespoke_info);
        tvClientAddress = (TextView) findViewById(R.id.tv_client_address_denger_bespoke_info);
//        addImage_chengnuo = (HorizontalScrollViewAddImage) findViewById(R.id.addImageView_chengnuo_info);
//        addImage_diaocha = (HorizontalScrollViewAddImage) findViewById(R.id.addImageView_diaocha_info);
//        addImage_zhengming = (HorizontalScrollViewAddImage) findViewById(R.id.addImageView_zhengming_info);

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
        tv_bespoke_money_add_ch_bespoke_info = (TextView) findViewById(R.id.tv_bespoke_money_add_ch_bespoke_info);
        tv_total_money_ch_bespoke_info = (TextView) findViewById(R.id.tv_total_money_ch_bespoke_info);
        tv_total_money_big_ch_bespoke_info = (TextView) findViewById(R.id.tv_total_money_big_ch_bespoke_info);

        // tvCardType.setEnabled(false);
        // etCardNo.setEnabled(false);
        // etPhone.setEnabled(false);
        // tvCardType.setEnabled(false);
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
        int userId = 0;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mBespoke = (理财产品预约) bundle.getSerializable(CLEW);
            mDictionarys = (HashMap<String, List<Dict>>) bundle
                    .getSerializable(DICTIONARYS);

            mProduct = (理财产品) bundle
                    .getSerializable(ChProductListActivity.EXTRAS_SELECT_PRODUCT);
        }

        if (mDictionarys == null) {
            mDictionarys = new HashMap<String, List<Dict>>();
        }

        if (mBespoke != null) {

//            if (Enum理财产品预约状态.getStatusNameById(mBespoke.状态).contains("未提交")
//                    && (mBespoke.制单人 + "").equals(Global.mUser.Id)) {
//                headerView.ivSave.setVisibility(View.GONE);
//                tvSubmit.setVisibility(View.VISIBLE);
//            }
            reservationId = mBespoke.编号 + "";
            if (Integer.parseInt(reservationId) > 0) { //已经保存过，同时显示保存和提交按钮
                headerView.ivSave.setVisibility(View.VISIBLE);
                tvSubmit.setVisibility(View.VISIBLE);
            } else {
                headerView.ivSave.setVisibility(View.VISIBLE);
                tvSubmit.setVisibility(View.GONE);
            }
            mIsEdit = false;
            // 查看
            tvProduct.setText(DictionaryBiz.getDictName(mDictionarys, "理财产品",
                    mBespoke.理财产品));
            tvClient.setText(DictionaryBiz.getDictName(mDictionarys, "客户",
                    mBespoke.客户));
            tvCardType.setText(DictionaryBiz.getDictName(mDictionarys,
                    "客户_证件类别", mBespoke.证件类别));
            etCardNo.setText(StrUtils.pareseNull(mBespoke.身份证号));
            etPhone.setText(StrUtils.pareseNull(mBespoke.手机));
            etTotal.setText("" + DoubleUtils.formatFloatNumber(mBespoke.追加前金额));
            etDeadline.setText(mBespoke.投资期限 + "");
            tvTime.setText(""
                    + ViewHelper.convertStrToFormatDateStr(mBespoke.预约打款日期,
                    "yyyy-MM-dd"));
            tvClinetMannager.setText(DictionaryBiz.getDictName(mDictionarys,
                    "客户经理,理财师", mBespoke.客户经理));
            etCategray.setText(StrUtils.pareseNull(mBespoke.预期年化收益率分类));

            tv_rengou_ch_bespoke_info.setText(DictionaryBiz.getDictName(
                    mDictionarys, "客户_客户性质", mBespoke.认购类型));// 认购类型
            et_zuoji_ch_bespoke_info.setText(mBespoke.座机);// 座机
            et_email_ch_bespoke_info.setText(mBespoke.邮箱);//
            tv_total_big_ch_bespoke_info.setText(mBespoke.追加前金额大写);// 预约金额大写
            if (mBespoke.编号 > 0) {
                tv_bespoke_money_add_ch_bespoke_info.setText((int) (mBespoke.本次追加金额) + ""); //本次追加金额
                tv_total_money_ch_bespoke_info.setText("" + DoubleUtils.formatFloatNumber(mBespoke.预约金额));
                tv_total_money_big_ch_bespoke_info.setText("" + MoneyUtils.change(mBespoke.预约金额));
            }
            // tv_outtime_ch_bespoke_info
            // .setText(vmContractCustomerInfo.预约到期日期);//
            et_nianhuashouyi_ch_bespoke_info.setText(mBespoke.预期年化收益率 + "");//
            et_kehujinglishouji_ch_bespoke_info.setText(mBespoke.客户经理手机);// 客户经理收益
            et_kehujingli_email_ch_bespoke_info.setText(mBespoke.客户经理邮箱);//
            tv_dept_ch_bespoke_info.setText(DeptBiz.getDeptById(mContext,
                    mBespoke.所属部门).get名称());
            tv_company_bespoke_info.setText(DeptBiz.getDeptById(mContext,
                    mBespoke.所属分公司).get名称());
            tvClientID.setText(mBespoke.客户编号);
            tvDangerLevel.setText(DictionaryBiz.getDictName(mDictionarys, "客户_风险测评类型",
                    mBespoke.风险测评类型));
            tvClientAddress.setText(mBespoke.联系地址);
            tvPostCode.setText(mBespoke.邮政编码);
            if (!TextUtils.isEmpty(mBespoke.预约到期日期)) {
                tv_outtime_ch_bespoke_info.setText(mBespoke.预约到期日期.substring(0,
                        10));
            }

            if (mBespoke.客户 > 0) {
                getContractCustomerInfoById(mBespoke.客户);
            }

        } else if (mBespoke == null) {// 新建预约
            headerView.setTitle("新建预约");
            mIsEdit = true;
            // findViewById(R.id.ll_saler_ch_bespoke_info)
            // .setVisibility(View.GONE);
            mBespoke = new 理财产品预约();
            tvTime.setText(ViewHelper.getDateToday());
            try {
                userId = Integer.parseInt(Global.mUser.Id);
                mBespoke.制单人 = userId;
                // 客户经理默认为当前客户
                mBespoke.客户经理 = userId;
                tvClinetMannager.setText(Global.mUser.getUserName());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
        }

        if (mProduct == null) {
            mProduct = new 理财产品();
        } else {
            mBespoke.理财产品 = mProduct.编号;
            mBespoke.期次 = mProduct.期次;
            tvProduct.setText(mProduct.产品名称);
        }

        // if (mBespoke.工作流 == 0 && mBespoke.制单人 == userId) {
        // tvSubmit.setVisibility(View.VISIBLE);
        // }
    }

    private void setAllViewEnable() {
        // 如果不可编辑，则隐藏保存按钮
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

    private boolean isEditable() {
        return ((mBespoke.制单人 + "").equals(Global.mUser.Id) && mBespoke.工作流 == 0)
                || mBespoke.编号 == 0;
    }

    private void setOnEvent() {

        setSelectSalerEvent();

        headerView.setOnButtonClickListener(new OnButtonClickListener() {
            @Override
            public void onClickSaveOrAdd() {
                if (isCanSave()) {
                    validateAddBescoke();
                }
            }

            @Override
            public void onClickFilter() {

            }

            @Override
            public void onClickBack() {
                finish();
            }
        });


        tvProduct.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,
                        ChProductListActivity.class);
                intent.putExtra(ChProductListActivity.EXTRAS_IS_SELECT, true);
                startActivityForResult(intent, CODE_SELECT_PRODUCT_REQUES);
            }
        });

        tvClient.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // ClientBiz.selectClient_Changhui(mContext, true,
                // "会员状态 = 2 and 无效客户 = 0");
                ClientBiz.selectClient_Changhui(mContext, true, "无效客户 = 0");
            }
        });

        tvCardType.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                showUpdateClientInfo();
            }
        });

        etCardNo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                showUpdateClientInfo();
            }
        });

        tvTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                DateAndTimePicker dateAndTimePicker = new DateAndTimePicker(
                        mContext);
                dateAndTimePicker.showDateWheel("选择时间", tvTime, false, true);
                dateAndTimePicker.setOnSelectedListener(new ISelected() {
                    @Override
                    public void onSelected(String date) {
                        mBespoke.预约打款日期 = date;
                        tvTime.setText(ViewHelper.convertStrToFormatDateStr(
                                date, "yyyy-MM-dd"));

                        // 预约到期日期 等于预约打款时间往后加7天

                        mBespoke.预约到期日期 = get预约到期日期(mBespoke.预约打款日期);
                        tv_outtime_ch_bespoke_info.setText(mBespoke.预约到期日期);
                    }
                });

            }
        });

        etPhone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                showUpdateClientInfo();
            }
        });

        tvSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                tvSubmit.setClickable(false);
//                validateOrder(mBespoke.编号);
                submitBescoke();
            }
        });

        tv_bespoke_money_add_ch_bespoke_info.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    mBespoke.本次追加金额 = Double.parseDouble(s.toString());
                    double i = mBespoke.追加前金额 + Integer.parseInt(s.toString());
                    mBespoke.预约金额 = i;
                    mBespoke.预约金额大写 = MoneyUtils.change(i);
                    tv_total_money_ch_bespoke_info.setText("" + DoubleUtils.formatFloatNumber(i));
                    tv_total_money_big_ch_bespoke_info.setText(MoneyUtils.change(i));
                    getContractYield();
                }
            }
        });


//        etTotal.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before,
//                                      int count) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                double total = 0;
//                if (!TextUtils.isEmpty(s.toString())) {
//                    try {
//                        total = Double.parseDouble(s.toString());
//
//                        mBespoke.预约金额大写 = MoneyUtils.change(total);
//                        tv_total_big_ch_bespoke_info.setText(mBespoke.预约金额大写);
//                    } catch (Exception e) {
//                        // TODO: handle exception
//                    }
//                }
//                mBespoke.预约金额 = total;
//
//                getContractYield();
//            }
//        });

//        etDeadline.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before,
//                                      int count) {
//
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                int deadline = 0;
//                if (!TextUtils.isEmpty(s.toString())) {
//                    try {
//                        deadline = Integer.parseInt(s.toString());
//                    } catch (Exception e) {
//                    }
//                }
//                mBespoke.投资期限 = deadline;
//
//                getContractYield();
//            }
//        });

        // etCategray.addTextChangedListener(new TextWatcher() {
        //
        // @Override
        // public void onTextChanged(CharSequence s, int start, int before,
        // int count) {
        // // TODO Auto-generated method stub
        //
        // }
        //
        // @Override
        // public void beforeTextChanged(CharSequence s, int start, int count,
        // int after) {
        // // TODO Auto-generated method stub
        //
        // }
        //
        // @Override
        // public void afterTextChanged(Editable s) {
        // mBespoke.预期年化收益率分类 = s.toString();
        // LogUtils.i(TAG, s.toString());
        // getContractYield();
        // }
        // });
    }

    /***
     * 选择理财师
     */
    private void setSelectSalerEvent() {
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

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String attch_zhengming = zlServiceHelper.uploadAttachPhotos(imageHelper_zhengming.getPhotoList(), new ProgressBar(mContext));
//                String attch_chengnuo = zlServiceHelper.uploadAttachPhotos(imageHelper_chengnuo.getPhotoList(), new ProgressBar(mContext));
//                String attch_diaocha = zlServiceHelper.uploadAttachPhotos(imageHelper_diaocha.getPhotoList(), new ProgressBar(mContext));
//                if (!TextUtils.isEmpty(mBespoke.合格投资者资产证明)) {  //判断是否为空，如果不为空吧上传的id拼在一起。
//                    if (!TextUtils.isEmpty(attch_zhengming)) {
//                        mBespoke.合格投资者资产证明 = mBespoke.合格投资者资产证明 + "," + attch_zhengming;
//                    }
//                } else {
//                    mBespoke.合格投资者资产证明 = attch_zhengming;
//                }
//                if (!TextUtils.isEmpty(mBespoke.投资者承诺函)) {
//                    if (!TextUtils.isEmpty(attch_chengnuo)) {
//                        mBespoke.投资者承诺函 = mBespoke.投资者承诺函 + "," + attch_chengnuo;
//                    }
//                } else {
//                    mBespoke.投资者承诺函 = attch_chengnuo;
//                }
//
//                if (!TextUtils.isEmpty(mBespoke.投资者风险调查)) {
//                    if (!TextUtils.isEmpty(attch_diaocha)) {
//                        mBespoke.投资者风险调查 = mBespoke.投资者风险调查 + "," + attch_diaocha;
//                    }
//                } else {
//                    mBespoke.投资者风险调查 = attch_diaocha;
//                }
//                handler.sendEmptyMessage(1);
//            }
//        }).start();
    }


    private boolean isCanSave() {
        if (TextUtils.isEmpty(mBespoke.本次追加金额 + "")) {
            showShortToast("本次追加金额不能为空!");
            return false;
        }
        return true;
    }

    private boolean isChecked() {

        if (mBespoke != null) {
            getBespokeInfo();

            if (mBespoke.客户 == 0) {
                showShortToast("请选择客户");
                return false;
            }

            if (mBespoke.理财产品 == 0) {
                showShortToast("请选择理财产品");
                return false;
            }

            if (mBespoke.证件类别 == 0) {
                showShortToast("请选择证件类别");
                return false;
            }

            if (TextUtils.isEmpty(mBespoke.身份证号)) {
                showShortToast("请填写证件号");
                return false;
            }

            // if (TextUtils.isEmpty(mBespoke.手机)) {
            // showShortToast("请填写手机号");
            // return false;
            // }

            if (mBespoke.预约金额 == 0) {
                showShortToast("请填写预约金额");
                return false;
            }

            if (mBespoke.预约金额 == 0) {
                showShortToast("请填写预约金额");
                return false;
            }

            if (mBespoke.投资期限 == 0) {
                showShortToast("请填写投资期限");
                return false;
            }

            // if (TextUtils.isEmpty(mBespoke.预期年化收益率分类)) {
            // showShortToast("请输入分类");
            // return false;
            // }

//            if (TextUtils.isEmpty(mBespoke.预约打款日期)) {
//                showShortToast("请选择预约打款日期");
//                return false;
//            }

//            if (TextUtils.isEmpty(mBespoke.投资者承诺函)) {
//                showShortToast("请添加投资者承诺函");
//                return false;
//            }
//
//            if (TextUtils.isEmpty(mBespoke.投资者风险调查)) {
//                showShortToast("请添加投资者风险调查");
//                return false;
//            }
//
//            if (TextUtils.isEmpty(mBespoke.合格投资者资产证明)) {
//                showShortToast("请添加合格投资者资产证明");
//                return false;
//            }

            if (TextUtils.isEmpty(mBespoke.预约到期日期)) {
                mBespoke.预约到期日期 = get预约到期日期(mBespoke.预约打款日期);
            }

            if (TextUtils.isEmpty(mBespoke.预约金额大写)) {
                mBespoke.预约金额大写 = MoneyUtils.change(mBespoke.预约金额);
            }


        }
        return true;
    }

    private String get预约到期日期(String date) {
        Date selectDate = ViewHelper.formatStrToDate(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectDate);
        calendar.add(Calendar.DAY_OF_MONTH, 6);
        selectDate = calendar.getTime();
        // 预约到期日期 等于预约打款时间往后加7天
        return ViewHelper.formatDateToStr(selectDate);
    }

    private void getBespokeInfo() {
        mBespoke.手机 = etPhone.getText().toString();
        mBespoke.身份证号 = etCardNo.getText().toString();
        mBespoke.预约打款日期 = tvTime.getText().toString();
        try {
            int months = Integer.parseInt(etDeadline.getText().toString());
            mBespoke.投资期限 = months;
            double total = Double.parseDouble(etTotal.getText().toString());
            mBespoke.预约金额 = total;
        } catch (Exception e) {
            LogUtils.e(TAG, e + "");
        }

        if (tvCardType.getSelectDict() != null) {
            mBespoke.证件类别 = tvCardType.getSelectDict().Id;
        }

        mBespoke.预期年化收益率分类 = etCategray.getText().toString();
        mBespoke.邮箱 = et_email_ch_bespoke_info.getText().toString();
        mBespoke.座机 = et_zuoji_ch_bespoke_info.getText().toString();
        mBespoke.客户经理手机 = et_kehujinglishouji_ch_bespoke_info.getText()
                .toString();
        mBespoke.客户经理邮箱 = et_kehujingli_email_ch_bespoke_info.getText()
                .toString();
    }

    /***
     * 保存
     */
    private void saveBespoke() {
        ProgressDialogHelper.show(mContext);
        String url = Global.BASE_URL + "Wealth/SaveOrder";
        StringRequest.postAsyn(url, mBespoke, new StringResponseCallBack() {
            @Override
            public void onResponseCodeErro(String result) {
                ProgressDialogHelper.dismiss();
                showShortToast(INFO_ERRO_SERVER);
            }

            @Override
            public void onResponse(String response) {
                headerView.ivSave.setVisibility(View.GONE);
                ProgressDialogHelper.dismiss();
                showShortToast("保存成功");
                String msg = pareseData(response);
                msg = StrUtils.removeRex(msg);
                try {
                    int id = Integer.parseInt(msg);
                    mBespoke.编号 = id;
                    tvSubmit.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    LogUtils.e(TAG, e + "");
                }
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                headerView.ivSave.setClickable(true);
                ProgressDialogHelper.dismiss();
            }
        });
    }

    /**
     * 加载客户信息
     */
    private void getContractCustomerInfoById(int clientId) {
        ProgressDialogHelper.show(mContext, "检索客户..");
        String url = Global.BASE_URL + "Wealth/GetContractCustomerInfo/"
                + clientId;
        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponseCodeErro(String result) {
                ProgressDialogHelper.dismiss();
                showShortToast(INFO_ERRO_SERVER);
            }

            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
                try {
                    VmContractCustomerInfo vmContractCustomerInfo = JsonUtils
                            .ConvertJsonToList(response,
                                    VmContractCustomerInfo.class).get(0);
                    if (vmContractCustomerInfo != null) {
                        // 赋值
                        mBespoke.证件类别 = vmContractCustomerInfo.证件类别;
                        mBespoke.身份证号 = vmContractCustomerInfo.证件号;
                        mBespoke.手机 = vmContractCustomerInfo.手机;
                        mBespoke.客户经理 = vmContractCustomerInfo.客户经理;
                        mBespoke.所属分公司 = vmContractCustomerInfo.所属分公司;
                        mBespoke.理财师 = vmContractCustomerInfo.理财师;
                        mBespoke.客户名称 = vmContractCustomerInfo.客户名称;

                        mBespoke.认购类型 = vmContractCustomerInfo.客户性质;
                        mBespoke.座机 = vmContractCustomerInfo.座机;
                        mBespoke.邮箱 = vmContractCustomerInfo.邮箱;
                        mBespoke.预约金额大写 = vmContractCustomerInfo.预约金额大写;
                        mBespoke.预期年化收益率 = vmContractCustomerInfo.预期年化收益率;
                        mBespoke.客户经理 = vmContractCustomerInfo.客户经理;
                        mBespoke.客户经理手机 = vmContractCustomerInfo.客户经理手机;
                        mBespoke.客户经理邮箱 = vmContractCustomerInfo.客户经理邮箱;
                        mBespoke.所属部门 = vmContractCustomerInfo.所属部门;
                        mBespoke.所属分公司 = vmContractCustomerInfo.所属分公司;


                        mBespoke.客户编号 = vmContractCustomerInfo.客户编号;
                        mBespoke.风险测评类型 = vmContractCustomerInfo.风险测评类型;
                        mBespoke.联系地址 = vmContractCustomerInfo.联系地址;
                        mBespoke.邮政编码 = vmContractCustomerInfo.邮政编码;
                        mBespoke.投资者承诺函 = vmContractCustomerInfo.投资者承诺函;
                        mBespoke.投资者风险调查 = vmContractCustomerInfo.投资者风险调查;
                        mBespoke.合格投资者资产证明 = vmContractCustomerInfo.合格投资者资产证明;

                        // 预约打款时间往后加7天
                        // mBespoke.预约到期日期 = vmContractCustomerInfo.;

                        // 展示
                        tvCardType.setText(vmContractCustomerInfo.证件类别名称);
                        tvClient.setText(vmContractCustomerInfo.客户名称);
                        etCardNo.setText(StrUtils
                                .pareseNull(vmContractCustomerInfo.证件号));
                        etPhone.setText(StrUtils
                                .pareseNull(vmContractCustomerInfo.手机));

                        tv_rengou_ch_bespoke_info
                                .setText(vmContractCustomerInfo.客户性质名称);// 认购类型
                        et_zuoji_ch_bespoke_info
                                .setText(vmContractCustomerInfo.座机);// 座机
                        et_email_ch_bespoke_info
                                .setText(vmContractCustomerInfo.邮箱);//
//                        tv_total_big_ch_bespoke_info
//                                .setText(vmContractCustomerInfo.预约金额大写);// 预约金额大写
                        // tv_outtime_ch_bespoke_info
                        // .setText(vmContractCustomerInfo.预约到期日期);//
                        et_nianhuashouyi_ch_bespoke_info
                                .setText(vmContractCustomerInfo.预期年化收益率 + "");//
                        et_kehujinglishouji_ch_bespoke_info
                                .setText(vmContractCustomerInfo.客户经理手机);// 客户经理收益
                        et_kehujingli_email_ch_bespoke_info
                                .setText(vmContractCustomerInfo.客户经理邮箱);//
                        tv_dept_ch_bespoke_info
                                .setText(vmContractCustomerInfo.所属部门名称);
                        tv_company_bespoke_info
                                .setText(vmContractCustomerInfo.所属分公司名称);
                        tvClinetMannager.setText(StrUtils
                                .pareseNull(vmContractCustomerInfo.客户经理名称));

                        tvClientID.setText(vmContractCustomerInfo.客户编号);
                        tvDangerLevel.setText(vmContractCustomerInfo.风险测评类型名称);
                        tvClientAddress.setText(vmContractCustomerInfo.联系地址);
                        tvPostCode.setText(vmContractCustomerInfo.邮政编码);

//                        imageHelper_chengnuo.reload(vmContractCustomerInfo.投资者承诺函);
//                        imageHelper_diaocha.reload(vmContractCustomerInfo.投资者风险调查);
//                        imageHelper_zhengming.reload(vmContractCustomerInfo.合格投资者资产证明);

                        attachView_chengnuo.loadImageByAttachIds(mBespoke.投资者承诺函);
                        attachView_diaocha.loadImageByAttachIds(mBespoke.投资者风险调查);
                        attachView_zhengming.loadImageByAttachIds(mBespoke.合格投资者资产证明);

                        showUpdateClientInfo();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e(TAG, e + "");
                }
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                ProgressDialogHelper.dismiss();
                showShortToast(INFO_ERRO_SERVER);
            }
        });
    }

    /**
     * 获取收益率,根据产品和金额、期限来校验
     */
    private void getContractYield() {
        mErroInfo = "不允许投资期限和金额";
        if (mBespoke.理财产品 != 0 && mBespoke.预约金额 > 0 && mBespoke.投资期限 > 0) {
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");// 格式化设置
            String url = Global.BASE_URL + "Wealth/GetContractYield/"
                    + decimalFormat.format(mBespoke.预约金额) + "/" + mBespoke.投资期限
                    + "/" + mBespoke.理财产品;
            // if (!TextUtils.isEmpty(mBespoke.预期年化收益率分类)) {
            // url += "/" + mBespoke.预期年化收益率分类;
            // }

            StringRequest.getAsyn(url, new StringResponseCallBack() {
                @Override
                public void onResponseCodeErro(String result) {
                    try {
                        String msg = JsonUtils.getStringValue(result,
                                JsonUtils.KEY_MESSAGE);
                        mErroInfo = msg;
                        // showShortToast(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponse(String response) {
                    mErroInfo = "";
                    List<预期年化收益率> list = JsonUtils.ConvertJsonToList(response,
                            预期年化收益率.class);
                    if (list != null && list.size() > 0) {
                        预期年化收益率 yq = list.get(0);
                        // etTotal.setTextColor(Color.RED);
                        mBespoke.预期年化收益率 = yq.收益率;
                        mBespoke.预期年化收益率分类 = yq.分类;
                        et_nianhuashouyi_ch_bespoke_info.setText(""
                                + mBespoke.预期年化收益率);
                        etCategray.setText(mBespoke.预期年化收益率分类);
                    }
                }

                @Override
                public void onFailure(Request request, Exception ex) {

                }
            });
        }
    }

    /***
     * 提交前校验预约
     *
     * @param id
     */
    private void validateOrder(int id) {
        ProgressDialogHelper.show(mContext);
        String url = Global.BASE_URL + "Wealth/ValidateOrder/" + id;
        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponseCodeErro(String result) {
                tvSubmit.setClickable(true);
                ProgressDialogHelper.dismiss();
                try {
                    String erroInfo = JsonUtils.getStringValue(result,
                            JsonUtils.KEY_MESSAGE);
                    showShortToast(erroInfo);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
                submitContact(mBespoke.编号);
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                tvSubmit.setClickable(true);
                ProgressDialogHelper.dismiss();
                showShortToast(INFO_ERRO_SERVER);
            }
        });
    }

    private void submitContact(int id) {
        ProgressDialogHelper.show(mContext);
        String url = Global.BASE_URL + "Wealth/SubmitOrder/" + id;
        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponseCodeErro(String result) {
                tvSubmit.setClickable(true);
                ProgressDialogHelper.dismiss();
                showShortToast(INFO_ERRO_SERVER);
            }

            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
                try {
                    String msg = JsonUtils.getStringValue(response, JsonUtils.KEY_MESSAGE);
                    if (msg.contains("成功")) {
                        ChBespokeListActivity.resume = true;
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
     * 获取图片的序列号
     *
     * @param attchList
     * @return
     */
    private String getAttchSerio(List<String> attchList) {
        String attch = "";
        for (String s : attchList) {
            attch += s + ",";
        }
        if (attch.length() > 0) {
            attch = attch.substring(0, attch.length() - 1);
        }
        return attch;
    }

    /**
     * 判断客户信息是否完整
     *
     * @return
     */
    private boolean showUpdateClientInfo() {
        if (TextUtils.isEmpty(mBespoke.风险测评类型 + "")
                || TextUtils.isEmpty(mBespoke.证件类别 + "")
                || TextUtils.isEmpty(mBespoke.身份证号)
                || TextUtils.isEmpty(mBespoke.手机)
                || TextUtils.isEmpty(mBespoke.联系地址)
                ) {
            showShortToast("客户信息不完整，请回到客户页面完善");
            return true;
        } else {
            return false;
        }
    }


    /**
     * 追加预约验证金额
     */
    private void validateAddBescoke() {
        String url = Global.BASE_URL + "Reservation/SaveAppendReservation";

        StringRequest.postAsyn(url, mBespoke, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                showShortToast("保存成功！");
                reservationId = JsonUtils.pareseData(response);
//                headerView.ivSave.setVisibility(View.GONE);
                tvSubmit.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {
                showShortToast(JsonUtils.pareseMessage(result));
            }
        });
    }


    /**
     * 提交预约单
     */
    private void submitBescoke() {
        String url = Global.BASE_URL + "Reservation/SubmitAppendReservation/" + reservationId;

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                showShortToast("提交成功");
                ChBespokeListActivity.resume = true;
                finish();
            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {
                showShortToast(JsonUtils.pareseMessage(result));
            }
        });
    }
}
