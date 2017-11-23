package com.cedarhd.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.cedarhd.R;
import com.cedarhd.biz.ChClientBiz;
import com.cedarhd.biz.ChProductBiz;
import com.cedarhd.models.changhui.理财产品;
import com.cedarhd.models.changhui.表单字段;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/***
 * 长汇客户Tab分类信息 （基本信息,会员信息的一个Tab），动态生成
 *
 * @author new
 */
public class ChClientTabFragment extends Fragment {
    private static final String EXTRA_FORM_LIST = "extra_form_list";
    private ArrayList<表单字段> mFormList;

    private ChClientBiz mClientBiz;

    private LinearLayout mRootLayout;

    private boolean isNewClient;

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mFormList = (ArrayList<表单字段>) bundle
                    .getSerializable(EXTRA_FORM_LIST);
        }
        isNewClient = bundle.getBoolean("isNewClient");

        View view = inflater.inflate(R.layout.fragment_tab_ch_client, null);
        mRootLayout = (LinearLayout) view
                .findViewById(R.id.ll_root_ch_client_tab);
        if (mFormList != null && mFormList.size() > 0) {
            mClientBiz = new ChClientBiz(getActivity(), mFormList, mRootLayout, isNewClient);
            mClientBiz.setRelateFragment(this);
            mClientBiz.generateViews();
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i("ONA", "onActivityResult");
        理财产品 product = ChProductBiz.onActivityGetClient(requestCode, data);

        if (product != null) {
            List<EditText> formList = mClientBiz.getEditList();
            for (int i = 0; i < formList.size(); i++) {
                EditText etValue = formList.get(i);
                表单字段 form = (表单字段) etValue.getTag();
                LogUtils.i("ChClient", form.Name);
                if (mClientBiz.isProductType(form.Name)) {
                    form.Value = product.编号 + "";
                    form.DicText = product.产品名称;
                    etValue.setText(product.产品名称);
                }
            }
        }
    }

    public ArrayList<表单字段> getFormList() {
        return mClientBiz.getFormList();
    }

    public static ChClientTabFragment newInstance(ArrayList<表单字段> formList, boolean isNewClient) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_FORM_LIST, formList);
        bundle.putBoolean("isNewClient", isNewClient);
        ChClientTabFragment fragment = new ChClientTabFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

}
