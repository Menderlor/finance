package com.cedarhd.changhui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.cedarhd.ClientConstactListActivity;
import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.biz.ChClientBiz;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.fragment.ChClientTabFragment;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.changhui.动态表单ViewModel;
import com.cedarhd.models.changhui.动态表单分类;
import com.cedarhd.models.changhui.表单字段;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.cedarhd.view.Indicator;
import com.cedarhd.view.Indicator.OnTitleClickListener;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 长汇客户详情 2016/01/27 10:47
 * <p/>
 * 长汇项目动态生成客户: 加载数据 解析，分类（基本字段,附加字段...），生成控件，绑定点击事件，提交校验
 */
public class ChClientInfoActivity extends BaseActivity {
    public static final String EXTRA_CLIENT_ID = "extra_client_id";
    private Context mContext;
    private ChClientBiz mClientBiz;

    private BoeryunHeaderView mHeaderView;
    private Indicator mIndicator;
    private ViewPager mViewPager;

    private HashMap<String, ArrayList<表单字段>> mFormDataMap;
    private List<ChClientTabFragment> mFragments;

    private boolean isNewClient = false;  //是否是新建客户

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ch_client_info);
        initViews();
        initData();
        setOnEvent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initViews() {
        mIndicator = (Indicator) findViewById(R.id.indicator_ch_client_info);
        mViewPager = (ViewPager) findViewById(R.id.vp_ch_client_info);
        mHeaderView = (BoeryunHeaderView) findViewById(R.id.header_ch_client_info);
    }

    private void initData() {
        mContext = this;
        mFragments = new ArrayList<ChClientTabFragment>();
        mFormDataMap = new HashMap<String, ArrayList<表单字段>>();
        Bundle bundle = getIntent().getExtras();
        int clientId = bundle.getInt(EXTRA_CLIENT_ID, 0);

        if (getIntent().getExtras() != null) {
            isNewClient = bundle.getBoolean("isNewClient", false);
        }
        getCustomerFormById(clientId);
    }

    private void setOnEvent() {
        mIndicator.setRelateViewPager(mViewPager);
        mHeaderView.setOnButtonClickListener(new OnButtonClickListener() {
            @Override
            public void onClickSaveOrAdd() {
                ArrayList<表单字段> list = getAllFormList();
                String result = ChClientBiz.checkNull(list);
                if (!TextUtils.isEmpty(result)) {
                    showShortToast(result);
                } else {
                    String idCardReg = ChClientBiz.checkCardRegEx(list);
                    if (!TextUtils.isEmpty(idCardReg)) {
                        // 先校验身份证号
                        showShortToast(idCardReg);
                    } else {
                        result = ChClientBiz.checkRegEx(list);
                        if (!TextUtils.isEmpty(result)) {
                            showShortToast(result);
                        } else {
                            saveCustomerForm(list);
                        }
                    }
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

        if (isNewClient) {
            mHeaderView.ivSave.setVisibility(View.VISIBLE);
        } else {
            mHeaderView.ivSave.setVisibility(View.GONE);
        }

    }

    /***
     * 根据客户编号获取客户表单
     *
     * @param clientId 客户编号，0代表新建
     */
    private void getCustomerFormById(final int clientId) {
        ProgressDialogHelper.show(mContext);
        String url = Global.BASE_URL + "Customer/GetCustomerFormById/"
                + clientId;
        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponseCodeErro(String result) {
                ProgressDialogHelper.dismiss();
            }

            @Override
            public void onResponse(String response) {
                LogUtils.i(TAG, response);
                ProgressDialogHelper.dismiss();
                List<动态表单ViewModel> list = JsonUtils.ConvertJsonToList(
                        response, 动态表单ViewModel.class);

                loadUiByData(list, clientId);
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                LogUtils.e(TAG, ex + "");
                ProgressDialogHelper.dismiss();
            }
        });
    }

    private void saveCustomerForm(ArrayList<表单字段> formList) {
        ProgressDialogHelper.show(mContext);
        String url = Global.BASE_URL + "Customer/SaveCustomerForm/";
        StringRequest.postAsyn(url, formList, new StringResponseCallBack() {
            @Override
            public void onResponseCodeErro(String result) {
                ProgressDialogHelper.dismiss();
                LogUtils.d(TAG, result + "");
                String erroInfo = StrUtils.removeRex(JsonUtils
                        .parseLoginMessage(result));
                showShortToast("保存失败:" + erroInfo);
            }

            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
                showShortToast("保存成功");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                ProgressDialogHelper.dismiss();
                LogUtils.e(TAG, ex + "");
                showShortToast("保存失败");
            }
        });
    }

    private ArrayList<表单字段> getAllFormList() {
        ArrayList<表单字段> list = new ArrayList<表单字段>();
        for (ChClientTabFragment fragment : mFragments) {
            list.addAll(fragment.getFormList());
        }
        return list;
    }

    private void loadUiByData(List<动态表单ViewModel> list, final int clientId) {
        if (list != null && list.size() > 0) {
            动态表单ViewModel formViewModel = list.get(0);
            final List<String> titles = new ArrayList<String>();
            for (动态表单分类 categray : formViewModel.动态表单分类s) {
                if (!TextUtils.isEmpty(categray.分类名称)) {
                    LogUtils.i(TAG, categray.分类名称 + "");
                    if (categray.分类名称.equals("基本信息")
                            || categray.分类名称.equals("会员信息")
                            || categray.分类名称.equals("联系信息")
                            || categray.分类名称.equals("背景信息")
                            || categray.分类名称.equals("联系记录")) {
                        titles.add(categray.分类名称);
                        mFormDataMap.put(categray.分类名称, new ArrayList<表单字段>());
                    }
                }
            }

            if (formViewModel.表单字段s != null && formViewModel.表单字段s.size() > 0) {
                // 根据分类Tab
                for (表单字段 form : list.get(0).表单字段s) {
                    if (mFormDataMap.containsKey(form.TypeName)) {
                        mFormDataMap.get(form.TypeName).add(form);
                    } else { // useless
                        mFormDataMap.put(form.TypeName, new ArrayList<表单字段>());
                        mFormDataMap.get(form.TypeName).add(form);
                    }
                }

                for (String title : titles) {
                    Iterator<Entry<String, ArrayList<表单字段>>> it = mFormDataMap
                            .entrySet().iterator();
                    while (it.hasNext()) {
                        Entry<String, ArrayList<表单字段>> entry = it.next();
                        String keyStr = entry.getKey();
                        LogUtils.i("EQU", keyStr + "---" + title);
                        if (keyStr.equals(title)) {
                            ArrayList<表单字段> formList = entry.getValue();
                            ChClientTabFragment fragment = ChClientTabFragment
                                    .newInstance(formList, isNewClient);
                            mFragments.add(fragment);
                        }
                    }
                }

                mViewPager.setOffscreenPageLimit(mFragments.size());
                mViewPager.setAdapter(new FragmentPagerAdapter(
                        getSupportFragmentManager()) {

                    @Override
                    public int getCount() {
                        return mFragments.size();
                    }

                    @Override
                    public Fragment getItem(int position) {
                        return mFragments.get(position);
                    }
                });

//				titles.add("联系记录");
                mIndicator.setTabItemTitles(titles);
                mIndicator.setOnTitleClickListener(new OnTitleClickListener() {
                    @Override
                    public void onTitleClick(int pos) {
                        if ("联系记录".equals(titles.get(pos))) {
                            Intent intent = new Intent(mContext,
                                    ClientConstactListActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("ClientInfoActivity_clientId",
                                    clientId);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                });
            }

        }
    }
}
