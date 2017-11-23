package com.cedarhd.changhui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanCrmAdapter;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.constants.enums.Enum理财产品预约状态;
import com.cedarhd.constants.enums.Enum理财产品预约类型;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.control.BoeryunSearchView.OnButtonSearchClickListener;
import com.cedarhd.control.listview.ListViewLoader;
import com.cedarhd.control.listview.PullToRefreshAndLoadMoreListView;
import com.cedarhd.helpers.Global;
import com.cedarhd.models.Client;
import com.cedarhd.models.Dict;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.changhui.QmOrder;
import com.cedarhd.models.changhui.理财产品购买追加合同;
import com.cedarhd.models.changhui.理财产品预约;
import com.cedarhd.models.changhui.理财产品额度调整;
import com.cedarhd.utils.ActivityUtils;
import com.cedarhd.utils.DoubleUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 长汇理财产品预约列表
 */
public class ChBespokeListActivity extends BaseActivity {

    private Context mContext;
    private int mClientIdPop;// 选中客户编号

    private BoeryunHeaderView headerView;
    private PullToRefreshAndLoadMoreListView lv;
    private TextView tvClientPop;
    public static ChBespokeListActivity activity;

    private QmOrder mQmProduct;
    private QueryDemand mQueryDemand;
    private List<理财产品预约> mList;
    private CommanCrmAdapter<理财产品预约> mAdapter;
    private ListViewLoader<理财产品预约> mListViewLoader;

    private View popupView;
    private PopupWindow popupWindowFilter;

    public static boolean resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ch_bespoke_list);
        initViews();
        initData();
        setOnEvent();
        initPopupWindow();
        activity = ChBespokeListActivity.this;
        ActivityUtils.addActivity(ChBespokeListActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (resume) {
            reLoadData();
            resume = false;
        }
    }

    private void initViews() {
        headerView = (BoeryunHeaderView) findViewById(R.id.header_ch_bespoke_list);
        lv = (PullToRefreshAndLoadMoreListView) findViewById(R.id.lv_pull_to_refresh);
        lv.setHintText("点击选择客户");
    }

    private void initData() {
        mContext = this;
        mList = new ArrayList<理财产品预约>();
        mAdapter = getAdapter();
        mQmProduct = new QmOrder();
        mQmProduct.PageSize = 20;
        mQmProduct.Offset = 0;
        mQmProduct.NoPager = false;
        mQueryDemand = new QueryDemand("编号");
        mListViewLoader = new ListViewLoader<理财产品预约>(mContext,
                "Wealth/GetOrderList", lv, mAdapter, mQmProduct, mQueryDemand,
                理财产品预约.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ClientBiz.SELECT_CLIENT_CODE:
                    Client client = ClientBiz
                            .onActivityGetClient(requestCode, data);
                    if (client != null && client.Id != 0) {
                        tvClientPop.setText(client.getCustomerName());
                        mClientIdPop = client.Id;
                    }
                    break;
            }

        }
    }

    private void setOnEvent() {

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int pos = position - lv.getHeaderViewsCount();
                if (pos < 0 || pos >= mList.size()) {
                    return;
                }
                HashMap<String, List<Dict>> dictionarys = mAdapter
                        .getmDictionarys();
                Intent intent = new Intent(mContext,
                        ChBespokeInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ChBespokeInfoActivity.CLEW,
                        mList.get(pos));
                bundle.putSerializable(ChBespokeInfoActivity.DICTIONARYS,
                        dictionarys);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        lv.mSearchView
                .setmOnButtonSearchClickListener(new OnButtonSearchClickListener() {
                    @Override
                    public void OnClick() {
                        showFilterPop(lv.mSearchView);
                    }
                });

        headerView.setOnButtonClickListener(new OnButtonClickListener() {
            @Override
            public void onClickSaveOrAdd() {
                showFilterPop(headerView.ivSave);
            }

            @Override
            public void onClickFilter() {

            }

            @Override
            public void onClickBack() {
                finish();
            }
        });
    }

    private void reLoadData() {
        mQmProduct.Offset = 0;
        mListViewLoader.clearData();
        mListViewLoader.startRefresh();
    }

    private void showFilterPop(View v) {
        int pos[] = new int[2];
        // 获取在当前窗口内的绝对坐标
        v.getLocationOnScreen(pos);
        int height = v.getHeight() + pos[1];
        popupWindowFilter.showAtLocation(v, Gravity.TOP, 0, height);
    }

    private void initPopupWindow() {
        popupView = getLayoutInflater().inflate(
                R.layout.pop_filter_bespokelist, null);
        popupWindowFilter = new PopupWindow(popupView,
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);

        popupWindowFilter.setAnimationStyle(R.style.AnimationFade);
        popupWindowFilter.update();
        // 点击空白处 对话框消失
        popupWindowFilter.setTouchable(true);
        popupWindowFilter.setOutsideTouchable(true);
        popupWindowFilter.setBackgroundDrawable(new BitmapDrawable(
                getResources(), (Bitmap) null));
        tvClientPop = (TextView) popupView
                .findViewById(R.id.tv_select_project_pop_filter_tab);
        Button btnSure = (Button) popupView
                .findViewById(R.id.btn_done_pop_filter_bespokelist);
        Button btnCancel = (Button) popupView
                .findViewById(R.id.btn_cancel_pop_filter_bespokelist);

        tvClientPop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				ClientBiz.selectClient(mContext);
                Intent intent = new Intent(mContext, ChClientListActivity.class);
                intent.putExtra(ChClientListActivity.EXTRA_SELECT_CLIENT, true);
                startActivityForResult(intent, ClientBiz.SELECT_CLIENT_CODE);
            }
        });

        btnSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowFilter.dismiss();
                mQmProduct.ClientId = mClientIdPop;
                reLoadData();
            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowFilter.dismiss();
                mClientIdPop = 0;
                tvClientPop.setText("");
                mQmProduct.ClientId = mClientIdPop;
                reLoadData();
            }
        });
    }

    private CommanCrmAdapter<理财产品预约> getAdapter() {
        return new CommanCrmAdapter<理财产品预约>(mList, mContext,
                R.layout.item_ch_bespoke) {

            @Override
            public void convert(int position, final 理财产品预约 item,
                                BoeryunViewHolder viewHolder) {
                LogUtils.i("position",
                        position + "----" + lv.getHeaderViewsCount());
                final int pos = position;
                if (pos < 0 || pos >= mList.size()) {
                    return;
                }
                viewHolder.setTextValue(R.id.tv_user_ch_bespoke_item,
                        mAdapter.getDictName("客户经理,理财师", item.客户经理));
                viewHolder.setTextValue(R.id.tv_total_ch_bespoke_item,
                        DoubleUtils.formatFloatNumber(item.预约金额));
                viewHolder.setTextValue(R.id.tv_client_ch_bespoke_item,
                        mAdapter.getDictName("客户", item.客户) + "");
                viewHolder.setTextValue(R.id.tv_product_ch_bespoke_item,
                        mAdapter.getDictName("理财产品", item.理财产品) + "");
                if (!TextUtils.isEmpty(item.预约结果)) {
                    viewHolder.setTextValue(R.id.tv_status_client_ch_bespoke_item, item.预约结果);
                } else {
                    viewHolder.setTextValue(R.id.tv_status_client_ch_bespoke_item,
                            Enum理财产品预约状态.getStatusNameById(item.状态) + "");
                }

                viewHolder.getView(R.id.tv_type_client_ch_bespoke_item).setVisibility(View.VISIBLE);
                if (item.类型 == Enum理财产品预约类型.新增.getValue()) {
                    viewHolder.setTextValue(R.id.tv_type_client_ch_bespoke_item, "新增");
                } else if (item.类型 == Enum理财产品预约类型.追加.getValue()) {
                    viewHolder.setTextValue(R.id.tv_type_client_ch_bespoke_item, "追加");
                } else {
                    viewHolder.getView(R.id.tv_type_client_ch_bespoke_item).setVisibility(View.GONE);
                }


                TextView tvAddContract = viewHolder.getView(R.id.tv_add_contract_ch_bespoke_item);
                TextView tvAddBespoke = viewHolder.getView(R.id.tv_add_bespoke_ch_bespoke_item);
                TextView tvAddMoney = viewHolder.getView(R.id.tv_add_money_ch_bespoke_item);

                tvAddContract.setBackgroundResource(R.drawable.selector_button_gray);
                tvAddContract.setEnabled(false);

                if (item.状态 == Enum理财产品预约状态.预约成功.getValue() && ((!item.是否已追加 && item.理财产品购买合同 == 0) || item.合同状态 == 4)) {
                    if (item.类型 == Enum理财产品预约类型.新增.getValue()) {
                        tvAddContract.setText("录入合同");
                    } else if (item.类型 == Enum理财产品预约类型.追加.getValue()) {
                        tvAddContract.setText("录入追加合同");
                    }
//                    tvAddContract.setVisibility(View.VISIBLE);
                    tvAddContract.setBackgroundResource(R.drawable.selector_button_blue);
                    tvAddContract.setEnabled(true);
                    tvAddContract.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getContactInfo(mList.get(pos).编号);
                        }
                    });
                } else {
//                    tvAddContract.setVisibility(View.GONE);
                    tvAddContract.setBackgroundResource(R.drawable.selector_button_gray);
                    tvAddContract.setEnabled(false);
                }


                tvAddMoney.setBackgroundResource(R.drawable.selector_button_gray);
                tvAddMoney.setEnabled(false);
                if (item.状态 == Enum理财产品预约状态.预约成功.getValue() && !item.是否已追加 &&
                        (item.理财产品购买合同 == 0 || item.合同状态 == Enum理财产品预约状态.合同作废.getValue())) {
                    if (item.下个步骤编号 == -1) {
//                        tvAddMoney.setVisibility(View.VISIBLE);
                        tvAddMoney.setBackgroundResource(R.drawable.selector_button_blue);
                        tvAddMoney.setEnabled(true);

                        tvAddMoney.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getBescokeInfoBYid(mList.get(pos).编号);
                            }
                        });
                    } else {
//                        tvAddMoney.setVisibility(View.GONE);
                        tvAddMoney.setBackgroundResource(R.drawable.selector_button_gray);
                        tvAddMoney.setEnabled(false);
                    }
                } else {
//                    tvAddMoney.setVisibility(View.GONE);
                    tvAddMoney.setBackgroundResource(R.drawable.selector_button_gray);
                    tvAddMoney.setEnabled(false);
                }


                tvAddBespoke.setBackgroundResource(R.drawable.selector_button_gray);
                tvAddBespoke.setEnabled(false);
                if (item.状态 == Enum理财产品预约状态.预约成功.getValue() && !item.是否已追加 && item.理财产品购买合同 > 0) {
                    if (item.允许追加投资 && item.下个步骤编号 == -1) {
//                        tvAddBespoke.setVisibility(View.VISIBLE);
                        tvAddBespoke.setBackgroundResource(R.drawable.selector_button_blue);
                        tvAddBespoke.setEnabled(true);
                        tvAddBespoke.setOnClickListener(new OnClickListener() {  //追加预约
                            @Override
                            public void onClick(View v) {
                                getBescokeInfo(mList.get(pos).编号);
                            }
                        });
                    } else {
//                        tvAddBespoke.setVisibility(View.GONE);
                        tvAddBespoke.setBackgroundResource(R.drawable.selector_button_gray);
                        tvAddBespoke.setEnabled(false);
                    }
                } else {
//                    tvAddBespoke.setVisibility(View.GONE);
                    tvAddBespoke.setBackgroundResource(R.drawable.selector_button_gray);
                    tvAddBespoke.setEnabled(false);
                }


                // tvCount.setText(StrUtils.pareseNull(item.发行规模));
                // tvContact.setText(StrUtils.pareseNull(item.联系人));
                // tvPhone.setText(StrUtils.pareseNull(item.联系电话));
                // tvTime.setText(DateDeserializer.getFormatTime(item.创建时间));
            }
        };
    }


    /**
     * 获取合同信息
     *
     * @param reservationId
     */
    private void getContactInfo(int reservationId) {
        String url = Global.BASE_URL + "Wealth/GetNewContractByReservationId/" + reservationId;

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                理财产品购买追加合同 contact = JsonUtils.ConvertJsonObject(response, 理财产品购买追加合同.class);
                if (contact != null) {
                    if (contact.Type == 6) { // 追加合同
                        HashMap<String, List<Dict>> dictionarys = mAdapter
                                .getmDictionarys();
                        Intent intent = new Intent(mContext,
                                ChAddContactActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(
                                ChAddContactActivity.EXTRA_CONTACT,
                                contact);
                        bundle.putSerializable(
                                ChAddContactActivity.EXTRA_DICTIONARYS,
                                dictionarys);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else { //录入新合同
                        HashMap<String, List<Dict>> dictionarys = mAdapter
                                .getmDictionarys();
                        Intent intent = new Intent(mContext,
                                ChContactInfoActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(
                                ChContactInfoActivity.EXTRA_CONTACT,
                                contact);
                        bundle.putSerializable(
                                ChContactInfoActivity.EXTRA_DICTIONARYS,
                                dictionarys);
                        intent.putExtras(bundle);
                        ChContactInfoActivity.mIsBespoke = true;
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                LogUtils.i(TAG, request.toString());
            }

            @Override
            public void onResponseCodeErro(String result) {
                String message = JsonUtils.pareseMessage(result);
                showShortToast(message);
            }
        });
    }


    /**
     * 获取预约信息
     *
     * @param reservationId
     */
    private void getBescokeInfo(int reservationId) {
        String url = Global.BASE_URL + "Reservation/GetAppendReservationByReservationId/" + reservationId;

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                理财产品预约 bescoke = JsonUtils.ConvertJsonObject(response, 理财产品预约.class);
                if (bescoke != null) {
                    HashMap<String, List<Dict>> dictionarys = mAdapter
                            .getmDictionarys();
                    Intent intent = new Intent(mContext,
                            ChAddBespokeInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(
                            ChAddBespokeInfoActivity.CLEW,
                            bescoke);
                    bundle.putSerializable(ChAddBespokeInfoActivity
                                    .DICTIONARYS,
                            dictionarys);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                LogUtils.i(TAG, request.toString());
            }

            @Override
            public void onResponseCodeErro(String result) {
                if ("405".equals(JsonUtils.parseStatus(result))) { //返回数据正常
                    理财产品预约 bescoke = JsonUtils.ConvertJsonObject(result, 理财产品预约.class);

                    if (bescoke != null) {
                        HashMap<String, List<Dict>> dictionarys = mAdapter
                                .getmDictionarys();
                        Intent intent = new Intent(mContext,
                                ChAddBespokeInfoActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(
                                ChAddBespokeInfoActivity.CLEW,
                                bescoke);
                        bundle.putSerializable(ChAddBespokeInfoActivity
                                        .DICTIONARYS,
                                dictionarys);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } else {
                    String message = JsonUtils.pareseMessage(result);
                    showShortToast(message);
                }
            }
        });
    }


    /**
     * 获取预约信息
     *
     * @param reservationId
     */
    private void getBescokeInfoBYid(int reservationId) {
        String url = Global.BASE_URL + "Reservation/GetChangeAmountMoneyOrder/" + reservationId;

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                理财产品额度调整 bescoke = JsonUtils.ConvertJsonObject(response, 理财产品额度调整.class);
                if (bescoke != null) {
                    HashMap<String, List<Dict>> dictionarys = mAdapter
                            .getmDictionarys();
                    Intent intent = new Intent(mContext,
                            ChAddBespokeMoneyActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(
                            ChAddBespokeMoneyActivity.CLEW,
                            bescoke);
                    bundle.putSerializable(
                            ChAddBespokeMoneyActivity.DICTIONARYS,
                            dictionarys);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                LogUtils.i(TAG, request.toString());
            }

            @Override
            public void onResponseCodeErro(String result) {
                if ("405".equals(JsonUtils.parseStatus(result))) { //返回数据正常
                    理财产品额度调整 bescoke = JsonUtils.ConvertJsonObject(result, 理财产品额度调整.class);
                    if (bescoke != null) {
                        HashMap<String, List<Dict>> dictionarys = mAdapter
                                .getmDictionarys();
                        Intent intent = new Intent(mContext,
                                ChAddBespokeMoneyActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(
                                ChAddBespokeMoneyActivity.CLEW,
                                bescoke);
                        bundle.putSerializable(
                                ChAddBespokeMoneyActivity.DICTIONARYS,
                                dictionarys);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } else {
                    String message = JsonUtils.pareseMessage(result);
                    showShortToast(message);
                }
            }
        });
    }

}
