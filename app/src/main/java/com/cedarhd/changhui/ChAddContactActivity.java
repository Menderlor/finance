package com.cedarhd.changhui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cedarhd.AttachListActivity;
import com.cedarhd.R;
import com.cedarhd.SelectPhotoActivity;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.biz.CameraBiz;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.biz.DeptBiz;
import com.cedarhd.biz.DictionaryBiz;
import com.cedarhd.constants.PreferencesConfig;
import com.cedarhd.constants.enums.Enum理财产品购买合同状态;
import com.cedarhd.constants.enums.Enum理财产品预约类型;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.AddImageHelper.OnDeleteListener;
import com.cedarhd.control.BoeryunDateSelectView;
import com.cedarhd.control.BoeryunDictSelectView;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.control.listview.BoeryunNoScrollListView;
import com.cedarhd.helpers.DictIosPickerBottomDialog;
import com.cedarhd.helpers.DictIosPickerBottomDialog.OnSelectedListener;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.SharedPreferencesHelper;
import com.cedarhd.helpers.UploadHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Attach;
import com.cedarhd.models.Client;
import com.cedarhd.models.Dict;
import com.cedarhd.models.changhui.VmContractCustomerInfo;
import com.cedarhd.models.changhui.合同上传附件;
import com.cedarhd.models.changhui.理财产品;
import com.cedarhd.models.changhui.理财产品购买追加合同;
import com.cedarhd.models.changhui.理财产品预约;
import com.cedarhd.models.changhui.预期年化收益率;
import com.cedarhd.models.字典;
import com.cedarhd.utils.ActivityUtils;
import com.cedarhd.utils.DoubleUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MoneyUtils;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.hanvon.utils.StringUtil;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 长汇追加合同详情
 */
public class ChAddContactActivity extends BaseActivity {

    private final String[] mPhotoArrs = {"拍照", "从相册选择"};

    /**
     * 合同实体 key
     */
    public static final String EXTRA_CONTACT = "extra_contact";

    /**
     * 预约实体 key
     */
    public static final String EXTRA_BEPOKE = "extra_bepoke";

    /**
     * 大字典集合 key
     */
    public static final String EXTRA_DICTIONARYS = "dictionarys";

    private final String X_SCROLLVIEW = "x_scrollview";
    private final String Y_SCROLLVIEW = "y_scrollview";

    /**
     * 选择图片请求码
     **/
    private final int REQUEST_CODE_SELECT_PHOTO = 21;

    private final int CODE_UPLOAD_IMAGE = 211;

    /**
     * 记录选择图片的pos
     */
    private int mSelectPos;

    private Context mContext;
    private SharedPreferencesHelper spHelper;
    private ZLServiceHelper zlServiceHelper;
    private CameraBiz mCameraBiz;
    private DictIosPickerBottomDialog mIosPickerBottomDialog;

    private BoeryunHeaderView headerView;
    private ScrollView scrollView;

    private 理财产品购买追加合同 mContract = new 理财产品购买追加合同();
    private 理财产品预约 mBespoke;
    private HashMap<String, List<Dict>> mDictionarys;

    private TextView tvSubmit;

    private String reservationId = "";

    /**
     * 是否是从预约页面跳转过来的
     */
    public static boolean mIsBespoke;

    /**
     * 是否是从合同页面跳转过来的
     */
    private boolean mIsContract;

    private List<合同上传附件> mAttachList;
    private CommanAdapter<合同上传附件> mAttachAdapter;

    /**
     * 合同编号
     */
    private EditText etNo;
    private TextView tvClientName;
    private TextView tvCardType;
    private TextView tvCardNo;
    private TextView tvPhone;
    private EditText etAddress;
    private TextView tvClientMg;
    private TextView tvChildCompany;
    private TextView tvDepartMg;
    private String contractId;
    /**
     * 副总经理
     */
    private TextView tvAssistanMg;
    private TextView tvChildCompanyMg;
    private TextView tvSaler;

    /**
     * 认购金额
     */
    private EditText etTotal;
    private TextView tvTotalBig;
    /**
     * 认购费用
     */
    private EditText etTotal2;
    private TextView tvTotal2Big;

    /**
     * 预期年化收益率
     */
    private TextView tvProduct;
    private EditText etDeadline;// 投资期限
    private TextView tvRengou; // 认购规模
    private TextView tvShouyi; // 认购规模

    /**
     * 银行账户信息
     */
    private EditText tvUserName;
    private EditText etBank;
    private EditText etOtherBank;
    private EditText etChildBank;
    private EditText etBankAcount;
    private EditText etBankNo;

    /**
     * 附加信息
     */
    private BoeryunDictSelectView tvPayType; // 支付方式
    private BoeryunDateSelectView tvPayTime; // 打款时间
    private EditText etPosNo; // pos单号

    /**
     * 上传
     */
    private TextView tvUploadIdCard;// 身份证上传
    private TextView tvUploadBankCard;// 身份证上传
    private TextView tvUploadContactShouye;// 身份证上传
    private TextView tvUploadContactQianshu;//
    private TextView tvUploadContactShourang;// 合同受让方信息扫描
    private TextView tvUploadPayOrder;// 付款凭证扫描
    private EditText etCategray;
    private TextView tvFudong;

    private BoeryunNoScrollListView lvAttach;
    private LinearLayout llAttach;

    /**
     * 金融新添加字段
     */
    private BoeryunDictSelectView dict_contacttype_ch_contact_info;
    private EditText et_zuoji_ch_contact_info;
    private EditText et_email_ch_contact_info;
    private TextView tv_licaijinglishouji_ch_contact_info;
    private TextView tv_licaijingli_email_ch_contact_info;
    private TextView tv_qixi_ch_contact_info;
    private TextView tv_daozhang_ch_contact_info;
    private TextView tv_touzidaoqiqisuan_ch_contact_info;
    private TextView tv_touzidaoqidaoqi_ch_contact_info;
    private TextView tv_hetongshouhui_ch_contact_info;
    private TextView tv_dept_ch_contact_info;
    private EditText tv_fukuan_user_name_ch_contact_info;
    private EditText et_fukuan_bank_ch_contact_info;
    private EditText et_fukuan_bank_acount_ch_contact_info;
    private TextView tv_client_id_ch_contact_info;
    private TextView tv_client_danger_level_ch_contact_info;
    private TextView tv_client_postcode_ch_contact_info;
    private TextView tv_this_add_money_ch_contact_info; //本次追加金额
    private TextView tv_touzi_total_money_ch_contact_info; //投资总金额
    private TextView tv_big_touzi_total_money_ch_contact_info; //投资总金额大写
    private TextView contact_type_ch_contact_info; //合同类型给

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ch_add_contact_info);
        initViews();
        initData();
        setOnEvent();
        initAttachData();
        setEnabled();
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ClientBiz.SELECT_CLIENT_CODE:
                    Client client = ClientBiz
                            .onActivityGetClient(requestCode, data);
                    if (client != null && client.Id != 0) {
                        mContract.客户 = client.Id;
                        mContract.户名 = client.getCustomerName();

                        tvClientName.setText(StrUtils.pareseNull(client
                                .getCustomerName()));
                        tvUserName.setText(StrUtils.pareseNull(client
                                .getCustomerName()));
                        getContractCustomerInfoById(client.Id);
                    }
                    break;
                case 101: // 身份证扫描
                case 102:
                case 103:
                case 104:
                case 105:
                case 106:
                    if (data != null) {
                        final ArrayList<String> list = data.getExtras()
                                .getStringArrayList(SelectPhotoActivity.PHOTO_LIST);
                        if (list != null && list.size() > 0) {
                            String path = list.get(0);
                            File file = new File(path);
                            if (file != null && file.exists()) {
                                // showShortToast(list.size() + "-----" +
                                // list.get(0));
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Attach attach = UploadHelper
                                                .uploadFileByHttpGetAttach(new File(
                                                        list.get(0)));
                                        Message msg = handler.obtainMessage();
                                        msg.what = CODE_UPLOAD_IMAGE;
                                        msg.obj = attach;
                                        handler.sendMessage(msg);
                                    }
                                }).start();
                            } else {
                                showShortToast("图片异常，请重新上传!");
                            }
                        }
                    }
                    break;
                case CameraBiz.CAMERA_TAKE_PHOTO:
                    if (resultCode == RESULT_OK) {
                        String path = mCameraBiz.getFilePath(requestCode, data);
                        if (!TextUtils.isEmpty(path)) {
                            ArrayList<String> pathList = new ArrayList<String>();
                            pathList.add(path);
                            updateFileList(pathList);
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_PHOTO: // 选择图片
                    if (data != null) {
                        final ArrayList<String> pathList = data.getExtras()
                                .getStringArrayList(SelectPhotoActivity.PHOTO_LIST);
                        updateFileList(pathList);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void updateFileList(final ArrayList<String> pathList) {
        if (pathList != null && pathList.size() > 0) {
            ProgressDialogHelper.show(mContext, "图片上传..");
            new Thread(new Runnable() {
                /*
                 * (non-Javadoc)
                 *
                 * @see java.lang.Runnable#run()
                 */
                @Override
                public void run() {
                    List<Attach> attachList = zlServiceHelper
                            .uploadAttachPhotos(pathList);
                    Message msg = handler.obtainMessage();
                    msg.obj = attachList;
                    msg.what = CODE_UPLOAD_IMAGE;
                    handler.sendMessage(msg);
                }
            }).start();
        }
    }

    private void initAttachIds(合同上传附件 uploadAttach, String attachIds) {
        Class cl = mContract.getClass();
        try {
            Field field = cl.getDeclaredField(uploadAttach.getInfo());
            field.set(mContract, attachIds);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat(X_SCROLLVIEW, scrollView.getScrollX());
        outState.putFloat(Y_SCROLLVIEW, scrollView.getScrollY());

        spHelper.putIntValue(X_SCROLLVIEW, scrollView.getScrollX());
        spHelper.putIntValue(Y_SCROLLVIEW, scrollView.getScrollY());
        LogUtils.i(TAG, "onSaveInstanceState:" + scrollView.getScrollY());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        float x = savedInstanceState.getFloat(X_SCROLLVIEW);
        float y = savedInstanceState.getFloat(Y_SCROLLVIEW);
        LogUtils.d(TAG, "onRestoreInstanceState:" + y);
        // scrollView.scrollTo((int) x, (int) y);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final int x = spHelper.getIntValue(X_SCROLLVIEW);
        final int y = spHelper.getIntValue(Y_SCROLLVIEW);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                // scrollView.scrollTo((int) x, (int) y + 1);
                scrollView.smoothScrollTo((int) x, (int) y + 1);
                LogUtils.d(TAG, "onResume:" + "x=" + x + "--" + y);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        spHelper.putIntValue(X_SCROLLVIEW, 0);
        spHelper.putIntValue(Y_SCROLLVIEW, 0);
    }

    private void setEnabled() {
        et_zuoji_ch_contact_info.setEnabled(false);
        et_email_ch_contact_info.setEnabled(false);
        tvUserName.setEnabled(false);
        etAddress.setEnabled(false);
        //TODO 11月21日，建伟突然要传这个字段
        tvPayTime.setEnabled(true);
        tvClientName.setEnabled(false);
        etTotal.setEnabled(false);
        etBank.setEnabled(false);
        etDeadline.setEnabled(false);
        etBankAcount.setEnabled(false);
        et_fukuan_bank_ch_contact_info.setEnabled(false);
        et_fukuan_bank_acount_ch_contact_info.setEnabled(false);
        tv_fukuan_user_name_ch_contact_info.setEnabled(false);
    }

    private void initViews() {
        headerView = (BoeryunHeaderView) findViewById(R.id.header_ch_contact_info);
        scrollView = (ScrollView) findViewById(R.id.scrollView_ch_contact_info);
        tvSubmit = (TextView) findViewById(R.id.tv_submit_order_ch_contact_info);
        /** 客户信息模块 */
        etNo = (EditText) findViewById(R.id.et_hetongbianhao_ch_contact_info);
        tvClientName = (TextView) findViewById(R.id.tv_client_name_ch_contact_info);
        tvCardType = (TextView) findViewById(R.id.tv_card_type_ch_contact_info);
        tvCardNo = (TextView) findViewById(R.id.tv_card_no_ch_contact_info);
        tvPhone = (TextView) findViewById(R.id.tv_phone_ch_contact_info);
        etAddress = (EditText) findViewById(R.id.et_address_ch_contact_info);
        tvClientMg = (TextView) findViewById(R.id.tv_client_mg_ch_contact_info);
        tvChildCompany = (TextView) findViewById(R.id.tv_child_company_ch_contact_info);
        tvDepartMg = (TextView) findViewById(R.id.tv_depart_mg_ch_contact_info);
        tvAssistanMg = (TextView) findViewById(R.id.tv_company_assistant_mg_ch_contact_info);
        tvChildCompanyMg = (TextView) findViewById(R.id.tv_child_company_mg_ch_contact_info);
        tvSaler = (TextView) findViewById(R.id.tv_saler_ch_contact_info);

        /** 认购金额 */
        etTotal = (EditText) findViewById(R.id.et_total_small_ch_contact_info);
        tvTotalBig = (TextView) findViewById(R.id.tv_total_big_ch_contact_info);

        /** 认购费用 */
        etTotal2 = (EditText) findViewById(R.id.et_total2_small_ch_contact_info);
        tvTotal2Big = (TextView) findViewById(R.id.tv_total2_big_ch_contact_info);

        /** 预期年化收益率 */
        tvProduct = (TextView) findViewById(R.id.tv_product_ch_contact_info);
        etDeadline = (EditText) findViewById(R.id.et_deadline_ch_contact_info);// 投资期限
        tvRengou = (TextView) findViewById(R.id.tv_rengouguimo_ch_contact_info); // 认购规模
        tvShouyi = (TextView) findViewById(R.id.tv_shouyilv_ch_contact_info); // 收益率

        /** 银行账户信息 */
        tvUserName = (EditText) findViewById(R.id.tv_user_name_ch_contact_info);
        etBank = (EditText) findViewById(R.id.et_bank_ch_contact_info);
        etOtherBank = (EditText) findViewById(R.id.et_other_bank_ch_contact_info);
        etChildBank = (EditText) findViewById(R.id.et_child_bank_ch_contact_info);
        etBankAcount = (EditText) findViewById(R.id.et_bank_acount_ch_contact_info);
        etBankNo = (EditText) findViewById(R.id.et_bank_no_ch_contact_info);
        tv_fukuan_user_name_ch_contact_info = (EditText) findViewById(R.id.tv_fukuan_user_name_ch_contact_info);
        et_fukuan_bank_ch_contact_info = (EditText) findViewById(R.id.et_fukuan_bank_ch_contact_info);
        et_fukuan_bank_acount_ch_contact_info = (EditText) findViewById(R.id.et_fukuan_bank_acount_ch_contact_info);
        tv_client_id_ch_contact_info = (TextView) findViewById(R.id.tv_client_id_ch_contact_info);
        tv_client_danger_level_ch_contact_info = (TextView) findViewById(R.id.tv_client_danger_level_ch_contact_info);
        tv_client_postcode_ch_contact_info = (TextView) findViewById(R.id.tv_client_postcode_ch_contact_info);
        tv_this_add_money_ch_contact_info = (TextView) findViewById(R.id.tv_this_add_money_ch_contact_info);
        tv_touzi_total_money_ch_contact_info = (TextView) findViewById(R.id.tv_touzi_total_money_ch_contact_info);
        tv_big_touzi_total_money_ch_contact_info = (TextView) findViewById(R.id.tv_big_touzi_total_money_ch_contact_info);
        contact_type_ch_contact_info = (TextView) findViewById(R.id.contact_type_ch_contact_info);

        /** 附加信息 */
        tvPayType = (BoeryunDictSelectView) findViewById(R.id.tv_pay_type_ch_contact_info); // 支付方式
        tvPayTime = (BoeryunDateSelectView) findViewById(R.id.tv_pay_time_ch_contact_info); // 选择时间
        etPosNo = (EditText) findViewById(R.id.et_pos_no_ch_contact_info);
        /** 上传 */
        tvUploadIdCard = (TextView) findViewById(R.id.tv_upload_id_card_main_ch_contact_info);// 身份证上传
        tvUploadBankCard = (TextView) findViewById(R.id.tv_upload_bank_card_main_ch_contact_info);// 银行卡上传
        tvUploadContactShouye = (TextView) findViewById(R.id.tv_upload_contact_shouye_ch_contact_info);// 身份证上传
        tvUploadContactQianshu = (TextView) findViewById(R.id.tv_upload_contact_qianshuye_ch_contact_info);// 身份证上传
        tvUploadContactShourang = (TextView) findViewById(R.id.tv_upload_contact_shourangfang_ch_contact_info);// 身份证上传
        tvUploadPayOrder = (TextView) findViewById(R.id.tv_upload_pay_order_ch_contact_info);// 身份证上传
        etCategray = (EditText) findViewById(R.id.et_category_ch_contact_info);// 分类
        tvFudong = (TextView) findViewById(R.id.tv_fudong_ch_contact_info);

        lvAttach = (BoeryunNoScrollListView) findViewById(R.id.lv_upload_ch_contact_info);
        llAttach = (LinearLayout) findViewById(R.id.ll_upload_ch_contact_info);

        /** 新添加字段 */
        dict_contacttype_ch_contact_info = (BoeryunDictSelectView) findViewById(R.id.dict_contacttype_ch_contact_info);
        et_zuoji_ch_contact_info = (EditText) findViewById(R.id.et_zuoji_ch_contact_info);
        et_email_ch_contact_info = (EditText) findViewById(R.id.et_email_ch_contact_info);
        tv_licaijinglishouji_ch_contact_info = (TextView) findViewById(R.id.tv_licaijinglishouji_ch_contact_info);
        tv_licaijingli_email_ch_contact_info = (TextView) findViewById(R.id.tv_licaijingli_email_ch_contact_info);
        tv_qixi_ch_contact_info = (TextView) findViewById(R.id.tv_qixi_ch_contact_info);
        tv_touzidaoqiqisuan_ch_contact_info = (TextView) findViewById(R.id.tv_touzidaoqiqisuan_ch_contact_info);
        tv_touzidaoqidaoqi_ch_contact_info = (TextView) findViewById(R.id.tv_touzidaoqidaoqi_ch_contact_info);
        tv_daozhang_ch_contact_info = (TextView) findViewById(R.id.tv_daozhang_ch_contact_info);
        tv_hetongshouhui_ch_contact_info = (TextView) findViewById(R.id.tv_hetongshouhui_ch_contact_info);
        tv_dept_ch_contact_info = (TextView) findViewById(R.id.tv_dept_ch_contact_info);
    }

    private void initData() {
        mContext = this;
        spHelper = new SharedPreferencesHelper(mContext,
                PreferencesConfig.APP_USER_INFO);
        mIosPickerBottomDialog = new DictIosPickerBottomDialog(mContext);
        zlServiceHelper = new ZLServiceHelper();
        mCameraBiz = new CameraBiz();
        if (mContract == null) {
            mContract = new 理财产品购买追加合同();
            mContract.认购人类型 = 6; // 自然人
            mContract.状态 = 1;
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            理财产品 product = (理财产品) bundle
                    .getSerializable(ChProductListActivity.EXTRAS_SELECT_PRODUCT);

            mContract = (理财产品购买追加合同) bundle.getSerializable(EXTRA_CONTACT);
            mDictionarys = (HashMap<String, List<Dict>>) bundle
                    .getSerializable(EXTRA_DICTIONARYS);
            if (mContract == null) {
                mContract = new 理财产品购买追加合同();
                mContract.认购人类型 = 6; // 自然人
            }
            if (mIsBespoke) {
                mIsContract = false;
            } else {
                mIsContract = true;
            }

            if (product != null) {
                mContract.理财产品 = product.编号;
                tvProduct.setText(StrUtils.pareseNull(product.产品名称));
            }

            mBespoke = (理财产品预约) bundle.getSerializable(EXTRA_BEPOKE);
            if (mBespoke != null) {
//                mIsBespoke = true;
                mContract.理财产品预约 = mBespoke.编号;
                mContract.期次 = mBespoke.期次;
                mContract.理财产品 = mBespoke.理财产品;
                mContract.认购金额小写 = mBespoke.预约金额;
                mContract.投资期限 = mBespoke.投资期限;
                mContract.客户 = mBespoke.客户;
                mContract.制单人 = mBespoke.制单人;
                mContract.理财师 = mBespoke.理财师;
                mContract.预期年化收益率分类 = mBespoke.预期年化收益率分类;

                mContract.座机 = mBespoke.座机;
                mContract.邮箱 = mBespoke.邮箱;
                mContract.客户经理手机 = mBespoke.客户经理手机;
                mContract.客户经理邮箱 = mBespoke.客户经理邮箱;
                // mContract.合同收回日期 = mBespoke.合同收回日期;
                // mContract.投资起算日 = mBespoke.投资起算日;
                // mContract.投资到期日 = mBespoke.座机;
                // mContract.投资起息日 = mBespoke.投资起息日;
                mContract.所属部门 = mBespoke.所属部门;
                // mContract.合同类型 = mBespoke.合同类型;
                mContract.认购类型 = mBespoke.认购类型;

                getContractYield();
            }
        }

        if (mContract != null && mContract.客户 != 0) {
            showData();
        }

//        if (!isEditable()) {
//            // 不可编辑，隐藏保存按钮
//            headerView.ivSave.setVisibility(View.GONE);
//        }
        if (!isEditable()) {
            // 不可编辑，隐藏保存按钮
            headerView.ivSave.setVisibility(View.GONE);
        }
        if (isEditable() && mContract.编号 > 0) {
            tvSubmit.setVisibility(View.VISIBLE);
        }
    }

    /***
     * 展示数据
     */
    private void showData() {
        if (mIsBespoke) {
            // 预约
            getContractCustomerInfoById(mContract.客户);
            getContractYield();
            tvClientName.setEnabled(false);
            etTotal.setEnabled(false);
            etDeadline.setEnabled(false);
            et_email_ch_contact_info.setEnabled(false);
            etAddress.setEnabled(false);

            mContract.认购金额大写 = MoneyUtils.change(mContract.认购金额小写);
            mContract.认购规模 = getInvestmentScale(mContract.认购金额小写);

            // 从预约录合同时，客户不应可以修改
//            tvClientName.setBackgroundColor(getResources().getColor(
//                    R.color.bg_list));
            tvPhone.setText(StrUtils.pareseNull(mContract.联系电话));
            // 预约过来的合同，产品，期限，金额不能修改
//            etTotal.setBackgroundColor(getResources().getColor(R.color.bg_list));
//            etDeadline.setBackgroundColor(getResources().getColor(
//                    R.color.bg_list));
            tv_client_danger_level_ch_contact_info.setText(mContract.风险测评类型名称);
            etAddress.setText(mContract.通讯地址);

            tvProduct.setText(DictionaryBiz.getDictName(mDictionarys, "理财产品",
                    mContract.理财产品));
            etCategray.setText(StrUtils.pareseNull(mContract.预期年化收益率分类));

            if (mContract.理财师 != 0) {
                String 理财师名称 = new DictionaryHelper(mContext)
                        .getUserNameById(mContract.理财师);
                tvSaler.setText(StrUtils.pareseNull(理财师名称));
            }
        } else {
            LogUtils.i(TAG, mContract.toString());
            // 展示
            tvCardType.setText(DictionaryBiz.getDictName(mDictionarys,
                    "客户_证件类别", mContract.证件类别));
            tvCardNo.setText(StrUtils.pareseNull(mContract.营业执照注册号或身份证号));
            tvPhone.setText(StrUtils.pareseNull(mContract.联系电话));
            etAddress.setText(StrUtils.pareseNull(mContract.通讯地址));
            // tvChildCompany.setText(DictionaryBiz.getDictName(mDictionarys,
            // "部门", mContract.所属分公司));
            tvClientMg.setText(DictionaryBiz.getDictName(mDictionarys,
                    "客户经理,理财师", mContract.客户经理));
            tvDepartMg.setText(DictionaryBiz.getDictName(mDictionarys,
                    "客户经理,部门经理,副总经理,分公司总经理", mContract.部门经理));
            tvAssistanMg.setText(DictionaryBiz.getDictName(mDictionarys,
                    "客户经理,部门经理,副总经理,分公司总经理", mContract.副总经理));
            tvChildCompanyMg.setText(DictionaryBiz.getDictName(mDictionarys,
                    "客户经理,部门经理,副总经理,分公司总经理", mContract.分公司总经理));
            tvSaler.setText(DictionaryBiz.getDictName(mDictionarys,
                    "客户经理,部门经理,副总经理,分公司总经理,理财师", mContract.理财师));
            tvRengou.setText(get认购规模(mContract.认购规模));
            contact_type_ch_contact_info.setText(Enum理财产品预约类型.getStatusNameById(mContract.Type));
            tv_client_danger_level_ch_contact_info.setText(DictionaryBiz.getDictName(mDictionarys, "客户_风险测评类型",
                    mContract.风险测评类型));

            tvProduct.setText(DictionaryBiz.getDictName(mDictionarys, "理财产品",
                    mContract.理财产品));
            tvShouyi.setText("" + mContract.预期年化收益率);
            etCategray.setText(StrUtils.pareseNull(mContract.预期年化收益率分类));

            tv_fukuan_user_name_ch_contact_info.setText(mContract.付款账户名);
            et_fukuan_bank_ch_contact_info.setText(mContract.付款账户开户行);
            et_fukuan_bank_acount_ch_contact_info.setText(mContract.付款账号);
            tvRengou.setText(DictionaryBiz.getDictName(mDictionarys, "客户_客户性质",
                    mContract.认购类型));// 认购类型
            dict_contacttype_ch_contact_info.setText(DictionaryBiz.getDictName(
                    mDictionarys, "理财产品购买合同Type", mContract.Type));
            et_zuoji_ch_contact_info.setText(mContract.座机);// 座机
            et_email_ch_contact_info.setText(mContract.邮箱);//
            tv_licaijinglishouji_ch_contact_info.setText(mContract.客户经理手机);// 客户经理收益
            tv_licaijingli_email_ch_contact_info.setText(mContract.客户经理邮箱);//
            tv_client_id_ch_contact_info.setText(mContract.客户编号);
            tv_client_postcode_ch_contact_info.setText(mContract.邮政编码);

            tv_this_add_money_ch_contact_info.setText(DoubleUtils.formatFloatNumber(mContract.本次追加金额) + "");
            tv_touzi_total_money_ch_contact_info.setText(DoubleUtils.formatFloatNumber(mContract.认购金额小写));
            tv_big_touzi_total_money_ch_contact_info.setText(mContract.认购金额大写);

            if (!TextUtils.isEmpty(mContract.到账时间)) {
                tv_daozhang_ch_contact_info.setText(ViewHelper.convertStrToFormatDateStr(mContract.到账时间, "yyyy-MM-dd"));
            }
            if (!TextUtils.isEmpty(mContract.投资到期日)) {
                tv_touzidaoqidaoqi_ch_contact_info.setText(ViewHelper.convertStrToFormatDateStr(mContract.投资到期日, "yyyy-MM-dd"));
            }
            if (!TextUtils.isEmpty(mContract.投资起算日)) {
                tv_touzidaoqiqisuan_ch_contact_info.setText(ViewHelper.convertStrToFormatDateStr(mContract.投资起算日, "yyyy-MM-dd"));
            }
            tv_dept_ch_contact_info.setText(DeptBiz.getDeptById(mContext,
                    mContract.所属部门).get名称());
            if (!TextUtils.isEmpty(mContract.合同收回日期)) {
                tv_hetongshouhui_ch_contact_info.setText(ViewHelper.convertStrToFormatDateStr(mContract.追加文件回收日期, "yyyy-MM-dd"));
            }
            if (!TextUtils.isEmpty(mContract.投资起息日)) {
                tv_qixi_ch_contact_info.setText(ViewHelper.convertStrToFormatDateStr(mContract.投资起息日, "yyyy-MM-dd"));
            }
            tvChildCompany.setText(DeptBiz.getDeptById(mContext,
                    mContract.所属分公司).get名称());
        }

        // 查看合同详情
        etNo.setText(StrUtils.pareseNull(mContract.合同编号));

        String clientName = DictionaryBiz.getDictName(mDictionarys, "客户",
                mContract.客户);
        tvClientName.setText(clientName);
        etTotal.setText(DoubleUtils.formatFloatNumber(mContract.追加前投资金额) + "");
        etDeadline.setText(mContract.投资期限 + "");
        tvUserName.setText(mContract.户名);
        tvTotalBig.setText(mContract.追加前投资金额大写);

        tvTotal2Big.setText(StrUtils.pareseNull(mContract.认购费用大写) + "");
        etTotal2.setText(mContract.认购费用小写 + "");
        String result = get认购规模(mContract.认购规模);
        tvRengou.setText(StrUtils.pareseNull(result));
        etBank.setText(mContract.银行);
        etOtherBank.setText(mContract.分行);
        etChildBank.setText(mContract.支行);
        etBankAcount.setText(mContract.账号);
        etBankNo.setText(mContract.银行卡号);
        tvPayType.setText(DictionaryBiz.getDictName(mDictionarys, "支付方式",
                mContract.支付方式));
        etPosNo.setText(mContract.ReferNo);

        //TODO 这段代码为什么被注释了？现在建伟要求显示
        //TODO 11月21日，建伟突然要传这个字段
        if (!TextUtils.isEmpty(mContract.打款时间)) {
//            tvPayTime.setText(StrUtils.pareseNull(mContract.打款时间).substring(0, 10));
            //TODO 要求默认值空
            tvPayTime.setText("");
        }

    }

    private void setOnEvent() {

        headerView.setOnButtonClickListener(new OnButtonClickListener() {
            @Override
            public void onClickSaveOrAdd() {
                if (isCanSave()) {
                    saveContract();
                }
//                String checkResult = isChecked();
//                if (StringUtil.isEmpty(checkResult)) {
//                    if (mBespoke != null && mBespoke.编号 != 0) {
//                        validate理财产品剩余额度();
//                    } else {
//                        // validate理财产品购买合同保存(); // 先通过服务端进行校验
//                        saveContact();
//                    }
//                } else {
//                    showShortToast(checkResult);
//                }

            }

            @Override
            public void onClickFilter() {

            }

            @Override
            public void onClickBack() {
                finish();
            }
        });

        tvSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mContract.编号 != 0) {
//                    validate理财产品购买合同();
//                } else {
//                    showShortToast("请先保存！");
//                }
                submitContract();

            }
        });

        tvClientName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 选择客户 */
                ClientBiz.selectClient_Changhui(mContext, true);
            }
        });

        etTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String totalStr = s.toString();
                try {
                    double total = Double.parseDouble(totalStr);
                    String totalBigStr = MoneyUtils.change(total);
                    tvTotalBig.setText(totalBigStr);
                    mContract.认购费用小写 = total;

                    getContractYield();
                } catch (Exception e) {
                    LogUtils.e(TAG, e + "");
                }
            }
        });

        etTotal2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String totalStr = s.toString();
                try {
                    double total = Double.parseDouble(totalStr);
                    String totalBigStr = MoneyUtils.change(total);
                    tvTotal2Big.setText(totalBigStr);
                } catch (Exception e) {
                    LogUtils.e(TAG, e + "");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etDeadline.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String totalStr = s.toString();
                try {
                    int total = Integer.parseInt(totalStr);
                    mContract.投资期限 = total;
                    getContractYield();
                } catch (Exception e) {
                    LogUtils.e(TAG, e + "");
                }

            }
        });

        tvUploadIdCard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SelectPhotoActivity.class);
                intent.putExtra(SelectPhotoActivity.MAX_PHOTO_COUNT, 1);
                startActivityForResult(intent, 101);
            }
        });

        tvUploadBankCard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SelectPhotoActivity.class);
                intent.putExtra(SelectPhotoActivity.MAX_PHOTO_COUNT, 1);
                startActivityForResult(intent, 102);
            }
        });

        tvUploadContactShouye.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SelectPhotoActivity.class);
                intent.putExtra(SelectPhotoActivity.MAX_PHOTO_COUNT, 1);
                startActivityForResult(intent, 103);
            }
        });
        tvUploadContactQianshu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SelectPhotoActivity.class);
                intent.putExtra(SelectPhotoActivity.MAX_PHOTO_COUNT, 1);
                startActivityForResult(intent, 104);
            }
        });

        tvUploadContactShourang.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SelectPhotoActivity.class);
                intent.putExtra(SelectPhotoActivity.MAX_PHOTO_COUNT, 1);
                startActivityForResult(intent, 105);
            }
        });

        tvUploadPayOrder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SelectPhotoActivity.class);
                intent.putExtra(SelectPhotoActivity.MAX_PHOTO_COUNT, 1);
                startActivityForResult(intent, 106);
            }
        });

        etTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !StringUtil.isEmpty(s.toString())) {
                    try {
                        mContract.认购金额小写 = Integer.parseInt(s.toString());
                        mContract.认购规模 = getInvestmentScale(mContract.认购金额小写);

                        String result = get认购规模(mContract.认购规模);
                        tvRengou.setText(StrUtils.pareseNull(result));
                    } catch (Exception e) {
                    }
                }

                getContractYield();
            }
        });

        etCategray.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mContract.预期年化收益率分类 = s.toString();
                LogUtils.i(TAG, s.toString());
                getContractYield();

            }
        });

    }

    private void initAttachData() {
        mAttachList = new ArrayList<合同上传附件>();
//        mAttachList.add(new 合同上传附件("投资者承诺函", mContract.投资者承诺函));
//        mAttachList.add(new 合同上传附件("投资者风险调查", mContract.投资者风险调查));
//        mAttachList.add(new 合同上传附件("合格投资者资产证明", mContract.合格投资者资产证明));
        mAttachList.add(new 合同上传附件("身份证正反面", mContract.身份证正反面复印件扫描件));
        mAttachList.add(new 合同上传附件("银行卡", mContract.银行卡正反面复印件扫描件));
        mAttachList.add(new 合同上传附件("追加投资付款凭证", mContract.追加投资付款凭证));
        mAttachList.add(new 合同上传附件("追加协议", mContract.追加协议));
//        mAttachList.add(new 合同上传附件("合同签署页扫描件", mContract.合同签署页扫描件));
        // mAttachList.add(new 合同上传附件("合同受让方信息页扫描件", mContract.合同受让方信息页扫描件));
//        mAttachList.add(new 合同上传附件("投资者问卷调查", mContract.投资风险测评卡));
        // mAttachList.add(new 合同上传附件("俱乐部入会申请表", mContract.身份证正反面复印件扫描件));

//        initUploadAdapter();
        // lvAttach.setAdapter(mAttachAdapter);

        // 遍历填入数据
        for (int i = 0; i < mAttachList.size(); i++) {
            final int position = i;
            final 合同上传附件 item = mAttachList.get(i);
            View childView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_ch_upload_attach, null);
            TextView tvInfo = (TextView) childView
                    .findViewById(R.id.tv_info_ch_item);
            TextView tvUpload = (TextView) childView
                    .findViewById(R.id.tv_upload_ch_item);
            TextView tvXing = (TextView) childView
                    .findViewById(R.id.tv_item_contact_info_xing);
            tvInfo.setText(item.getInfo());
            HorizontalScrollViewAddImage addImage = (HorizontalScrollViewAddImage) childView
                    .findViewById(R.id.horizontalScrollView_control_ch_item);
            AddImageHelper addImageHelper = null;
            if (addImage.getTag() != null) {
                addImageHelper = (AddImageHelper) addImage.getTag();
            } else {
//                if (item.getInfo().equals("投资者承诺函")
//                        || item.getInfo().equals("投资者风险调查")
//                        || item.getInfo().equals("合格投资者资产证明")) { //这三个附件在
//                    tvXing.setVisibility(View.GONE);
//                    addImageHelper = new AddImageHelper(ChAddContactActivity.this,
//                            mContext, addImage, item.getAttachIds(), false,
//                            false);
//                } else {
                if (item.getInfo().equals("追加协议")) {
                    tvXing.setVisibility(View.GONE);
                } else {
                    tvXing.setVisibility(View.VISIBLE);
                }
                addImageHelper = new AddImageHelper(ChAddContactActivity.this,
                        mContext, addImage, item.getAttachIds(), false,
                        isEditable());
//                }
                addImage.setTag(addImageHelper);
            }
            LogUtils.i(TAG, "合同详情附件:" + position + "----" + item.getAttachIds());
//            addImageHelper.reload(item.getAttachIds());
            addImageHelper.setOnDeleteListener(new OnDeleteListener() {
                @Override
                public void onDelete(int pos) {
//                    showShortToast("---" + pos);
                    String[] idArr = item.getAttachIds().split(",");
                    String removeId = idArr[pos];
                    LogUtils.i("remove",
                            idArr.length + "==" + item.getAttachIds()
                                    + "---id=" + removeId);
                    String newAttachIds = item.getAttachIds().replace(removeId,
                            "");
                    newAttachIds = newAttachIds.replace(",,", ",");
                    newAttachIds = StrUtils.removeRex(newAttachIds, ",");
                    item.setAttachIds(newAttachIds);
                    if (position == 0) {
                        mContract.身份证正反面复印件扫描件 = newAttachIds;
                    } else if (position == 1) {
                        mContract.银行卡正反面复印件扫描件 = newAttachIds;
                    } else if (position == 2) {
                        mContract.追加投资付款凭证 = newAttachIds;
                    } else if (position == 3) {
                        mContract.追加协议 = newAttachIds;
                    }

                    initAttachIds(item, newAttachIds);
                }
            });

            final boolean isEditable = isEditable();

            if (isEditable) {
                tvUpload.setVisibility(View.VISIBLE);
            } else {
                tvUpload.setText("下载");
            }
            tvUpload.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEditable) {
                        addImage(position);
                    } else {
                        // 下载附件
                        Intent intent = new Intent(mContext,
                                AttachListActivity.class);
                        intent.putExtra(AttachListActivity.ATTACH_IDS,
                                item.getAttachIds());
                        mContext.startActivity(intent);
                    }
                }
            });

            ViewHolder holder = new ViewHolder();
            holder.addImage = addImage;
            childView.setTag(holder);
            llAttach.addView(childView);

        }
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
                        mContract.证件类别 = vmContractCustomerInfo.证件类别;
                        mContract.客户编号 = vmContractCustomerInfo.客户编号;
                        mContract.营业执照注册号或身份证号 = vmContractCustomerInfo.证件号;
                        mContract.联系电话 = vmContractCustomerInfo.联系电话;
                        mContract.通讯地址 = vmContractCustomerInfo.联系地址;
                        mContract.客户经理 = vmContractCustomerInfo.客户经理;
                        mContract.邮箱 = vmContractCustomerInfo.邮箱;
                        mContract.邮政编码 = vmContractCustomerInfo.邮政编码;
                        mContract.联系电话 = vmContractCustomerInfo.手机;
                        mContract.所属分公司 = vmContractCustomerInfo.所属分公司;
                        mContract.所属部门 = vmContractCustomerInfo.所属部门;
                        mContract.部门经理 = vmContractCustomerInfo.部门经理;
                        mContract.副总经理 = vmContractCustomerInfo.副总经理;
                        mContract.分公司总经理 = vmContractCustomerInfo.分公司总经理;
                        mContract.风险测评类型名称 = vmContractCustomerInfo.风险测评类型名称;
                        mContract.风险测评类型 = vmContractCustomerInfo.风险测评类型;
                        // mContract.理财师 = vmContractCustomerInfo.理财师;

                        mContract.认购人类型 = vmContractCustomerInfo.认购人类型;

                        // 展示
                        tvCardType.setText(vmContractCustomerInfo.证件类别名称);
                        tvCardNo.setText(StrUtils
                                .pareseNull(vmContractCustomerInfo.证件号));
                        tvPhone.setText(StrUtils
                                .pareseNull(mContract.联系电话));
                        etAddress.setText(StrUtils
                                .pareseNull(mContract.通讯地址));
                        tvClientMg.setText(StrUtils
                                .pareseNull(vmContractCustomerInfo.客户经理名称));
                        tvChildCompany.setText(StrUtils
                                .pareseNull(vmContractCustomerInfo.所属分公司名称));
                        tvDepartMg.setText(StrUtils
                                .pareseNull(vmContractCustomerInfo.部门经理名称));
                        tvAssistanMg.setText(StrUtils
                                .pareseNull(vmContractCustomerInfo.副总经理名称));
                        tvChildCompanyMg.setText(StrUtils
                                .pareseNull(vmContractCustomerInfo.分公司总经理名称));
                        tv_client_danger_level_ch_contact_info.setText(StrUtils.pareseNull(vmContractCustomerInfo.风险测评类型名称));
                        tv_client_id_ch_contact_info.setText(StrUtils.pareseNull(vmContractCustomerInfo.客户编号));
                        // tvSaler.setText(StrUtils
                        // .pareseNull(vmContractCustomerInfo.理财师名称));

                        tvRengou.setText(DictionaryBiz.getDictName(
                                mDictionarys, "客户_客户性质",
                                vmContractCustomerInfo.认购类型));// 认购类型
                        et_zuoji_ch_contact_info
                                .setText(vmContractCustomerInfo.座机);// 座机
                        et_email_ch_contact_info
                                .setText(vmContractCustomerInfo.邮箱);//
                        tv_licaijinglishouji_ch_contact_info
                                .setText(vmContractCustomerInfo.客户经理手机);// 客户经理收益
                        tv_licaijingli_email_ch_contact_info
                                .setText(vmContractCustomerInfo.客户经理邮箱);//
                        tv_dept_ch_contact_info
                                .setText(vmContractCustomerInfo.所属部门名称);
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

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Attach attach = new Attach();
            if (msg.what >= 101 && msg.what <= 106) {
                attach = (Attach) msg.obj;
            }

            switch (msg.what) {
                case 101:
                    showShortToast("上传完成！");
                    tvUploadIdCard.setText("已上传");
                    mContract.身份证正反面复印件扫描件 = attach.Id + "";
                    break;
                case 102:
                    showShortToast("上传完成！");
                    tvUploadBankCard.setText("已上传");
                    mContract.银行卡正反面复印件扫描件 = attach.Id + "";
                    break;
                case 103:
                    showShortToast("上传完成！");
                    tvUploadContactShouye.setText("已上传");
                    mContract.合同首页扫描件 = attach.Id + "";
                    break;
                case 104:
                    showShortToast("上传完成！");
                    tvUploadContactQianshu.setText("已上传");
                    mContract.合同签署页扫描件 = attach.Id + "";
                    break;
                case 105:
                    showShortToast("上传完成！");
                    tvUploadContactShourang.setText("已上传");
                    mContract.合同受让方信息页扫描件 = attach.Id + "";
                    break;
                case 106:
                    showShortToast("上传完成！");
                    tvUploadPayOrder.setText("已上传");
                    mContract.付款凭证扫描件 = attach.Id + "";
                    break;
                case CODE_UPLOAD_IMAGE:
                    ProgressDialogHelper.dismiss();
                    List<Attach> attachList = (List<Attach>) msg.obj;
                    updateAttachImg(attachList);

                    // mAttachAdapter.get
                    break;
                default:
                    break;
            }
        }

        ;
    };


    private String isChecked() {
        // 合同类型默认为新增
        mContract.Type = 0;
        String contractNo = etNo.getText().toString();
        if (StringUtil.isEmpty(contractNo)) {
            // return "合同编号不能为空";
        } else {
            mContract.合同编号 = contractNo;
        }

        // if(mContract.Type == 0) {
        // return "请选择合同分类";
        // }

        字典 dict = dict_contacttype_ch_contact_info.getSelectDict();
        if (dict != null) {
            mContract.Type = dict.Id;
        }

        if (mContract.客户 == 0) {
            return "请先选择客户";
        } else {
            String address = etAddress.getText().toString();
            if (TextUtils.isEmpty(mContract.通讯地址) && TextUtils.isEmpty(address)) {
                return "客户的通讯地址为空，请先补全该客户信息";
            }
            mContract.通讯地址 = address;
            if (TextUtils.isEmpty(mContract.联系电话)) {
                return "客户的联系电话为空，请先补全该客户信息";
            }
        }

        String totol = etTotal.getText().toString();
        if (StringUtil.isEmpty(totol)) {
            return "认购金额不能为空";
        } else {
            try {
                totol = DoubleUtils.formatDoubleString(totol);
                mContract.认购金额小写 = Double.parseDouble(totol);
                mContract.认购金额大写 = tvTotalBig.getText().toString();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                LogUtils.e(TAG, e + "");
                return "认购金额格式非法";
            }
        }

        if (mContract.预期年化收益率 == 0 && tvShouyi.getHint() != null
                && !TextUtils.isEmpty(tvShouyi.getHint().toString())) {
            // 非法的 预期年化收益率
            return tvShouyi.getHint().toString();
        }

        String totol2 = etTotal2.getText().toString();
        if (StringUtil.isEmpty(totol2)) {
            // return "认购费用不能为空";
        } else {
            try {
                mContract.认购费用小写 = Double.parseDouble(totol2);
                mContract.认购费用大写 = tvTotal2Big.getText().toString();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                LogUtils.e(TAG, e + "");
                return "认购费用格式非法";
            }
        }

        String deadLine = etDeadline.getText().toString();
        if (StringUtil.isEmpty(deadLine)) {
            return "投资期限不能为空";
        } else {
            try {
                mContract.投资期限 = Integer.parseInt(deadLine);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                LogUtils.e(TAG, e + "");
                return "投资期限格式非法";
            }
        }

        mContract.户名 = tvUserName.getText().toString();
        mContract.银行 = etBank.getText().toString();

        mContract.分行 = etOtherBank.getText().toString();
        mContract.支行 = etChildBank.getText().toString();
        mContract.账号 = etBankAcount.getText().toString();
        mContract.银行卡号 = etBankNo.getText().toString();

        if (StringUtil.isEmpty(mContract.银行)
                || StringUtil.isEmpty(mContract.账号)
                || StringUtil.isEmpty(mContract.户名)) {
            // 新版去掉不显示，此处校验也没必要
            // || StringUtil.isEmpty(mContract.支行)
            // || StringUtil.isEmpty(mContract.分行)

            return "银行账户信息不允许为空";
        }

        // if (mContract.银行卡号.length() != 12) {
        // return "银行卡号长度必须为 12位";
        // }

        // if (mContract.银行卡号.length() != 10) {
        // // return "银行行号长度必须为 10位";
        // }

        // 字典 dict = tvPayType.getSelectDict();
        // if (dict == null) {
        // return "请选择支付方式";
        // } else {
        // mContract.支付方式 = dict.Id;
        // if (mContract.支付方式 == 1) {// pos支付，需要校验pos单号
        // mContract.ReferNo = etPosNo.getText().toString();
        // if (StringUtil.isEmpty(mContract.ReferNo)) {
        // return "请填写Pos单号";
        // }
        // }
        // }

        // mContract.
        mContract.打款时间 = tvPayTime.getText().toString();
        if (StringUtil.isEmpty(mContract.打款时间)) {
            return "请选择付款时间";
        }

        if (StringUtil.isEmpty(mContract.预期年化收益率分类)) {
            // 不校验分类
            // return "请输入分类";
        }

        // // 附件上传检测
        if (StringUtil.isEmpty(mContract.身份证正反面复印件扫描件)) {
            return "请上传身份证正反面复印件扫描件";
        }
        if (StringUtil.isEmpty(mContract.银行卡正反面复印件扫描件)) {
            return "请上传银行卡正反面复印件扫描件";
        }
        // if (StringUtil.isEmpty(mContract.合同首页扫描件)) {
        // return "请上传合同首页扫描件";
        // }
        // if (StringUtil.isEmpty(mContract.合同签署页扫描件)) {
        // return "请上传合同签署页扫描件";
        // }
        // if (StringUtil.isEmpty(mContract.合同受让方信息页扫描件)) {
        // return "请上传合同受让方信息页扫描件";
        // }
        if (StringUtil.isEmpty(mContract.付款凭证扫描件)) {
            return "请上传付款凭证扫描件";
        }
        // if (StringUtil.isEmpty(mContract.投资风险测评卡)) {
        // return "请上传投资风险测评卡";
        // }
        // if (StringUtil.isEmpty(mContract.俱乐部入会申请表)) {
        // return "请上传俱乐部入会申请表";
        // }
        // if (StringUtil.isEmpty(mContract.合格投资者资产证明)) {
        // return "请上传合格投资者资产证明";
        // }

        return "";
    }


    private boolean isCanSave() {
        //TODO 11月21日，建伟突然要传这个字段
        mContract.打款时间 = tvPayTime.getText().toString();
        if (StringUtil.isEmpty(mContract.打款时间)) {
            showShortToast("请选择付款时间！");
            return false;
        }
        if (TextUtils.isEmpty(mContract.身份证正反面复印件扫描件)) {
            showShortToast("身份证正反面复印件扫描件不能为空！");
            return false;
        }
        if (TextUtils.isEmpty(mContract.银行卡正反面复印件扫描件)) {
            showShortToast("银行卡正反面复印件扫描件不能为空！");
            return false;
        }
        if (TextUtils.isEmpty(mContract.追加投资付款凭证)) {
            showShortToast("追加投资付款凭证不能为空！");
            return false;
        }
        return true;
    }

    /**
     * 是否可以编辑
     */
    private boolean isEditable() {
        return (mContract.状态 != Enum理财产品购买合同状态.已作废.getValue()
                && ((mContract.制单人 + "").equals(Global.mUser.Id) || (mContract.客户经理 + "").equals(Global.mUser.Id))
                && mContract.工作流 == 0);
//        return mIsBespoke;
    }

    /**
     * 保存时校验,如果是从预约页面跳转过来的则进行校验
     */
    private void validate理财产品剩余额度() {
        ProgressDialogHelper.show(mContext);
        String url = Global.BASE_URL + "/wealth/Validate理财产品剩余额度/"
                + mBespoke.编号 + "/" + mContract.编号 + "";
        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponseCodeErro(String result) {
                ProgressDialogHelper.dismiss();
                if (!TextUtils.isEmpty(result)) {
                    try {
                        String msg = JsonUtils.getStringValue(result,
                                JsonUtils.KEY_MESSAGE);
                        showShortToast(" " + msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
                LogUtils.i(TAG, response + "理财产品剩余额度");
                // validate理财产品购买合同保存();
                saveContact();
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                ProgressDialogHelper.dismiss();
                showShortToast(INFO_ERRO_SERVER);
            }
        });
    }

    /**
     * 保存时校验
     */
    private void validate理财产品购买合同保存() {
        ProgressDialogHelper.show(mContext);
        try {
            mContract.合同编号 = new String(mContract.合同编号.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String url = Global.BASE_URL + "/wealth/Validate理财产品购买合同保存/"
                + mContract.编号 + "/" + mContract.合同编号 + "";
        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponseCodeErro(String result) {
                ProgressDialogHelper.dismiss();
                if (!TextUtils.isEmpty(result)) {
                    try {
                        String msg = JsonUtils.getStringValue(result,
                                JsonUtils.KEY_MESSAGE);
                        showShortToast(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
                LogUtils.i(TAG, response + "合同号校验成功");
                saveContact();
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                ProgressDialogHelper.dismiss();
                showShortToast(INFO_ERRO_SERVER);
            }
        });
    }

    /**
     * 提交时校验
     */
    private void validate理财产品购买合同() {
        ProgressDialogHelper.show(mContext);
        String url = Global.BASE_URL + "/wealth/Validate理财产品购买合同/"
                + mContract.编号;
        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponseCodeErro(String result) {
                ProgressDialogHelper.dismiss();
                if (!TextUtils.isEmpty(result)) {
                    try {
                        String msg = JsonUtils.getStringValue(result,
                                JsonUtils.KEY_MESSAGE);
                        showShortToast(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
                LogUtils.i(TAG, response + "合同号校验成功");
                saveContact();
                submitContact(mContract.编号);
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                ProgressDialogHelper.dismiss();
                showShortToast(INFO_ERRO_SERVER);
            }
        });
    }

    private void saveContact() {
        ProgressDialogHelper.show(mContext);
        mContract.付款账户名 = tv_fukuan_user_name_ch_contact_info.getText().toString();
        mContract.付款账户开户行 = et_fukuan_bank_ch_contact_info.getText().toString();
        mContract.付款账号 = et_fukuan_bank_acount_ch_contact_info.getText().toString();
        Log.i(TAG, mContract.toString());
        String url = Global.BASE_URL + "Wealth/SaveContract";

        StringRequest.postAsyn(url, mContract, new StringResponseCallBack() {
            @Override
            public void onResponseCodeErro(String result) {
                ProgressDialogHelper.dismiss();
                // showShortToast(INFO_ERRO_SERVER);
                showShortToast(JsonUtils.pareseMessage(result));
            }

            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
                LogUtils.i(TAG, response + "");
                showShortToast("保存成功");

                String msg = JsonUtils.pareseData(response);
                msg = StrUtils.removeRex(msg);
                try {
                    int contractId = Integer.parseInt(msg);
                    mContract.编号 = contractId;
                    tvSubmit.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    LogUtils.e(TAG, e + "");
                }
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                ProgressDialogHelper.dismiss();
                // showShortToast(INFO_ERRO_SERVER);
                showShortToast("访问服务器失败");
            }
        });
    }

    private void submitContact(int contractId) {
        ProgressDialogHelper.show(mContext);
        String url = Global.BASE_URL + "Wealth/submitcontract/" + contractId;
        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponseCodeErro(String result) {
                ProgressDialogHelper.dismiss();
                showShortToast(JsonUtils.pareseMessage(result));
            }

            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
                ActivityUtils.finishSingleActivity(ChBespokeListActivity.activity);
                showShortToast("提交成功");
                startActivity(new Intent(ChAddContactActivity.this, ChContactListActivity.class));
                finish();
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                ProgressDialogHelper.dismiss();
                showShortToast(INFO_ERRO_SERVER);
            }
        });
    }

    /**
     * 获取收益率
     */
    private void getContractYield() {
        if (mContract.理财产品 != 0 && mContract.认购金额小写 > 0 && mContract.投资期限 > 0) {
            // && !StringUtil.isEmpty(mContract.预期年化收益率分类)
            mContract.预期年化收益率 = 0;
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");// 格式化设置
            String url;
            if (TextUtils.isEmpty(mContract.预期年化收益率分类)) {
                url = Global.BASE_URL + "Wealth/GetContractYield/"
                        + decimalFormat.format(mContract.认购金额小写) + "/"
                        + mContract.投资期限 + "/" + mContract.理财产品;
            } else {
                url = Global.BASE_URL + "Wealth/GetContractYield/"
                        + decimalFormat.format(mContract.认购金额小写) + "/"
                        + mContract.投资期限 + "/" + mContract.理财产品 + "/"
                        + mContract.预期年化收益率分类;
            }

            LogUtils.i("Shouyi", url);
            StringRequest.getAsyn(url, new StringResponseCallBack() {
                @Override
                public void onResponseCodeErro(String result) {
                    try {
                        List<预期年化收益率> list = JsonUtils.ConvertJsonToList(
                                result, 预期年化收益率.class);
                        if (list != null && list.size() > 0) {
                            预期年化收益率 yq = list.get(0);
                            mContract.预期年化收益率 = yq.收益率;
                            mContract.预期年化收益率浮动 = yq.浮动;
                            String fd = yq.浮动 ? "是" : "否";
                            tvFudong.setText(fd);
                            tvShouyi.setText("" + yq.收益率);
                        }

                        String msg = JsonUtils.getStringValue(result,
                                JsonUtils.KEY_MESSAGE);
                        tvShouyi.setHint(msg);
                        tvShouyi.setText(msg);
                        LogUtils.i("Shouyi", msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponse(String response) {
                    // showShortToast(response);
                    List<预期年化收益率> list = JsonUtils.ConvertJsonToList(response,
                            预期年化收益率.class);
                    if (list != null && list.size() > 0) {
                        预期年化收益率 yq = list.get(0);
                        mContract.预期年化收益率 = yq.收益率;
                        mContract.预期年化收益率浮动 = yq.浮动;
                        String fd = yq.浮动 ? "是" : "否";
                        tvFudong.setText(fd);
                        tvShouyi.setText("" + yq.收益率);
                    }
                }

                @Override
                public void onFailure(Request request, Exception ex) {

                }
            });
        }
    }

    /***
     * 获取
     *
     * @param amount
     * @return
     */
    public int getInvestmentScale(double amount) {
        if (amount >= 50000 && amount < 200000) {
            return 6;
        }
        if (amount >= 200000 && amount < 500000) {
            return 7;
        }
        if (amount >= 500000 && amount < 1000000) {
            return 8;
        }
        if (amount >= 1000000 && amount < 3000000) {
            return 9;
        }
        if (amount >= 3000000 && amount < 5000000) {
            return 10;
        }
        return amount >= 5000000 ? 11 : 0;
    }

    /***
     * 获取认购规模的文字描述
     *
     * @param codeNo
     * @return
     */
    public String get认购规模(int codeNo) {
        String result = "";
        switch (codeNo) {
            case 6:
                result = "5万-20万以下";
                break;
            case 7:
                result = "20万-50万以下";
                break;
            case 8:
                result = "50万-100万以下";
                break;
            case 9:
                result = "100万-300万以下";
                break;
            case 10:
                result = "300万-500万以下";
                break;
            case 11:
                result = "500万及以上";
                break;
            default:
                break;
        }
        return result;
    }

    private void initUploadAdapter() {
        if (mAttachAdapter == null) {

            mAttachAdapter = new CommanAdapter<合同上传附件>(mAttachList, mContext,
                    R.layout.item_ch_upload_attach) {

                @Override
                public void convert(final int position, final 合同上传附件 item,
                                    BoeryunViewHolder viewHolder) {
                    viewHolder.setTextValue(R.id.tv_info_ch_item,
                            item.getInfo());
                    TextView tvUpload = viewHolder
                            .getView(R.id.tv_upload_ch_item);
                    HorizontalScrollViewAddImage addImage = viewHolder
                            .getView(R.id.horizontalScrollView_control_ch_item);

                    AddImageHelper addImageHelper = null;
                    if (addImage.getTag() != null) {
                        addImageHelper = (AddImageHelper) addImage.getTag();
                    } else {
                        addImageHelper = new AddImageHelper(
                                ChAddContactActivity.this, mContext, addImage,
                                item.getAttachIds(), false, isEditable());
                        addImage.setTag(addImageHelper);
                    }
                    addImageHelper.reload(item.getAttachIds());
                    addImageHelper.setOnDeleteListener(new OnDeleteListener() {
                        @Override
                        public void onDelete(int pos) {
                            showShortToast("---" + pos);
                            String[] idArr = item.getAttachIds().split(",");
                            String removeId = idArr[pos];

                            LogUtils.i("remove",
                                    idArr.length + "==" + item.getAttachIds()
                                            + "---id=" + removeId);
                            String newAttachIds = item.getAttachIds().replace(
                                    removeId, "");
                            newAttachIds = newAttachIds.replace(",,", ",");
                            newAttachIds = StrUtils
                                    .removeRex(newAttachIds, ",");
                            item.setAttachIds(newAttachIds);
                            // addImageHelper.reload(newAttachIds);

                            initAttachIds(item, newAttachIds);
                        }
                    });
                    if (item.getPathList() != null
                            && item.getPathList().size() > 0) {
                        // BoeryunNoScrollGridView gv =
                        // (BoeryunNoScrollGridView) viewHolder
                        // .getView(R.id.gv_img_ch_item);
                        // gv.setAdapter(new NoScrollGridAdapter(mContext,
                        // Global.BASE_URL, item.getPathList()));
                    }

                    if (isEditable()) {
                        tvUpload.setVisibility(View.VISIBLE);
                    } else {
                        tvUpload.setVisibility(View.GONE);
                    }

                    tvUpload.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSelectPos = position;
                            Intent intent = new Intent(mContext,
                                    SelectPhotoActivity.class);
                            intent.putExtra(
                                    SelectPhotoActivity.MAX_PHOTO_COUNT, 16);
                            startActivityForResult(intent,
                                    REQUEST_CODE_SELECT_PHOTO);
                        }
                    });

                }
            };
        }
    }

    /**
     * 更新附件列表页面
     *
     * @param
     * @param attachList 上传成功返回附件集合
     */
    private void updateAttachImg(List<Attach> attachList) {
        合同上传附件 uploadAttach = mAttachList.get(mSelectPos);
        LogUtils.i("Assert:", "Assert:" + "--" + attachList.size());
        String attachIds = "";
        for (int i = 0; i < attachList.size(); i++) {
            uploadAttach.getPathList().add(attachList.get(i).Address);
            attachIds += "," + attachList.get(i).Id;
        }

        attachIds = uploadAttach.getAttachIds() + attachIds;
        if (attachIds.startsWith(",")) {
            attachIds = attachIds.substring(1, attachIds.length());
        }

        LogUtils.i("Assert:", "attachIds:" + "--" + attachIds);
        initAttachIds(uploadAttach, attachIds);

        uploadAttach.setAttachIds(attachIds);

        if (mSelectPos == 0) {
            mContract.身份证正反面复印件扫描件 = attachIds;
        } else if (mSelectPos == 1) {
            mContract.银行卡正反面复印件扫描件 = attachIds;
        } else if (mSelectPos == 2) {
            mContract.追加投资付款凭证 = attachIds;
        } else if (mSelectPos == 3) {
            mContract.追加协议 = attachIds;
        }
        // View view = lvAttach.getChildAt(mSelectPos);
        // updateItemView(view, uploadAttach);

        View view = llAttach.getChildAt(mSelectPos);
        updateItemView(view, uploadAttach);

        // mAttachList.get(mSelectPos).setPathList(attachList);
        // mAttachAdapter.notifyDataSetChanged();
    }

    private void updateItemView(View view, 合同上传附件 uploadAttach) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        HorizontalScrollViewAddImage addImage = viewHolder.addImage;
        AddImageHelper addImageHelper = null;
        if (addImage.getTag() != null) {
            addImageHelper = (AddImageHelper) addImage.getTag();
        }
        addImageHelper.reload(uploadAttach.getAttachIds());
    }

    private void addImage(final int position) {
        mIosPickerBottomDialog.show(mPhotoArrs);
        mIosPickerBottomDialog.setOnSelectedListener(new OnSelectedListener() {
            @Override
            public void onSelected(int index) {
                switch (index) {
                    case 0:
                        mSelectPos = position;
                        mCameraBiz.takePhoto(mContext);
                        break;
                    case 1:
                        Intent intent = new Intent(mContext,
                                SelectPhotoActivity.class);
                        intent.putExtra(SelectPhotoActivity.MAX_PHOTO_COUNT, 16);
                        startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTO);
                        mSelectPos = position;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private class ViewHolder {
        private HorizontalScrollViewAddImage addImage;
    }


    /**
     * 保存合同
     */
    private void saveContract() {
        String url = Global.BASE_URL + "Contract/SaveAppendContract";

        StringRequest.postAsyn(url, mContract, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                reservationId = JsonUtils.pareseData(response);
                tvSubmit.setVisibility(View.VISIBLE);
                showShortToast("保存成功");
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
     * 提交合同
     */
    private void submitContract() {
        String url = Global.BASE_URL + "Contract/SubmitAppendContract/" + reservationId;
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
