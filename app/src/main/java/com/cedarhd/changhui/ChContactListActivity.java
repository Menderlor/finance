package com.cedarhd.changhui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.cedarhd.ProfitInfoNewActivity;
import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanCrmAdapter;
import com.cedarhd.constants.enums.Enum理财产品购买合同状态;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.control.BoeryunSearchView;
import com.cedarhd.control.listview.ListViewLoader;
import com.cedarhd.control.listview.PullToRefreshAndLoadMoreListView;
import com.cedarhd.models.Dict;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.changhui.QmProduct;
import com.cedarhd.models.changhui.理财产品购买追加合同;
import com.cedarhd.utils.DoubleUtils;
import com.cedarhd.utils.StrUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 长汇合同列表
 */
public class ChContactListActivity extends BaseActivity {

    private Context mContext;

    private BoeryunHeaderView headerView;
    private PullToRefreshAndLoadMoreListView lv;

    private QmProduct mQmProduct;
    private QueryDemand mQueryDemand;
    private List<理财产品购买追加合同> mList;
    private CommanCrmAdapter<理财产品购买追加合同> mAdapter;
    private ListViewLoader<理财产品购买追加合同> mListViewLoader;
    private HashMap<String, List<Dict>> dictionarys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ch_contact_list);
        initViews();
        initData();
        setOnEvent();
    }

    private void initViews() {
        headerView = (BoeryunHeaderView) findViewById(R.id.header_ch_contact_list);
        lv = (PullToRefreshAndLoadMoreListView) findViewById(R.id.lv_pull_to_refresh);
        lv.setHintText("请输入客户名称或合同编号");
    }

    private void initData() {
        mContext = this;
        mList = new ArrayList<理财产品购买追加合同>();
        mAdapter = getAdapter();
        mQmProduct = new QmProduct();
        mQmProduct.PageSize = 20;
        mQmProduct.Offset = 0;
        mQmProduct.NoPager = false;
        mQueryDemand = new QueryDemand("编号");
        mListViewLoader = new ListViewLoader<理财产品购买追加合同>(mContext,
                "Wealth/GetContractList", lv, mAdapter, mQmProduct,
                mQueryDemand, 理财产品购买追加合同.class);
    }

    private void reLoadData() {
        mQmProduct.Offset = 0;
        mListViewLoader.clearData();
        mListViewLoader.startRefresh();
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
                dictionarys = mAdapter
                        .getmDictionarys();
                Intent intent = null;
                if (mList.get(pos).Type == 6) { // 追加合同
                    intent = new Intent(mContext,
                            ChAddContactActivity.class);
                } else {
                    intent = new Intent(mContext,
                            ChContactInfoActivity.class);
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable(ChContactInfoActivity.EXTRA_CONTACT,
                        mList.get(pos));
                bundle.putSerializable(ChContactInfoActivity.EXTRA_DICTIONARYS,
                        dictionarys);
                intent.putExtras(bundle);
                ChContactInfoActivity.mIsBespoke = false;
                startActivity(intent);
            }
        });

        lv.mSearchView
                .setOnButtonClickListener(new BoeryunSearchView.OnButtonClickListener() {
                    @Override
                    public void OnSearch(String str) {
//                        mQmProduct.moreFilter = "(户名 like '%" + str
//                                + "%' or 合同编号  like '%" + str + "%')";
                        mQmProduct.Keyword = str;
                        reLoadData();
                    }

                    @Override
                    public void OnCancle() {
//                        mQmProduct.moreFilter = "";
                        mQmProduct.Keyword = "";
                        reLoadData();
                    }
                });

        headerView.setOnButtonClickListener(new OnButtonClickListener() {
            @Override
            public void onClickSaveOrAdd() {
                startActivity(new Intent(mContext, ChContactInfoActivity.class));
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

    private CommanCrmAdapter<理财产品购买追加合同> getAdapter() {
        return new CommanCrmAdapter<理财产品购买追加合同>(mList, mContext,
                R.layout.item_ch_contact) {
            @Override
            public void convert(int position, final 理财产品购买追加合同 item,
                                BoeryunViewHolder viewHolder) {
                TextView tvClient = viewHolder
                        .getView(R.id.tv_client_ch_contactlist_item);
                TextView tvNo = viewHolder
                        .getView(R.id.tv_no_ch_contactlist_item);
                TextView tvShouYi = viewHolder
                        .getView(R.id.tv_shouyi_ch_contactlist_item);
                TextView tvState = viewHolder
                        .getView(R.id.tv_type_ch_contactlist_item);
                // TextView tvCount = viewHolder
                // .getView(R.id.tv_count_item_ch_productlist);
                tvClient.setText(StrUtils.pareseNull(mAdapter.getDictName("客户",
                        item.客户)));
                if (TextUtils.isEmpty(item.合同编号)) {
                    tvNo.setText("#");
                } else {
                    tvNo.setText("#" + item.合同编号);
                }
                viewHolder.setTextValue(R.id.tv_user_ch_contactlist_item,
                        mAdapter.getDictName("客户经理,部门经理,副总经理,分公司总经理", item.客户经理));
                viewHolder.setTextValue(R.id.tv_total_ch_contactlist_item,
                        DoubleUtils.formatFloatNumber(item.认购金额小写));
                viewHolder.setTextValue(R.id.tv_qixian_ch_contactlist_item,
                        item.投资期限 + "");
                viewHolder.setTextValue(R.id.tv_jizhun_ch_contactlist_item,
                        item.预期年化收益率 + "%");
                viewHolder.setTextValue(R.id.tv_project_ch_contactlist_item,
                        mAdapter.getDictName("理财产品", item.理财产品) + "");
                viewHolder.setTextValue(R.id.tv_type_ch_contactlist_item,
                        Enum理财产品购买合同状态.getStatusNameById(item.状态) + "");
                // tvCount.setText(StrUtils.pareseNull(item.发行规模));
                // tvContact.setText(StrUtils.pareseNull(item.联系人));
                // tvPhone.setText(StrUtils.pareseNull(item.联系电话));
                // tvTime.setText(DateDeserializer.getFormatTime(item.创建时间));

                String StrState = tvState.getText().toString().trim();
                if (TextUtils.isEmpty(StrState)) {
                    tvState.setVisibility(View.GONE);
                }

                if (item.状态 == Enum理财产品购买合同状态.已通过.getValue() || item.状态 == Enum理财产品购买合同状态.已追加.getValue()) {
                    tvShouYi.setBackgroundResource(R.drawable.selector_button_blue);
                    tvShouYi.setClickable(true);
                    tvShouYi.setEnabled(true);
                    tvShouYi.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            /*String url = "http://www.boeryun.com:8035/金融理财/清算/PaymentDetail?contractId="
                                    + item.编号;
							Intent intent = new Intent(mContext,
									WebviewNormalActivity.class);
							intent.putExtra(WebviewNormalActivity.EXTRA_TITLE,
									"查看收益");
							intent.putExtra(WebviewNormalActivity.EXTRA_URL,
									url);*/
//							Intent intent = new Intent(mContext, ProfitInfoActivity.class);
                            Intent intent = new Intent(mContext, ProfitInfoNewActivity.class);
                            dictionarys = getmDictionarys();
                            intent.putExtra("dictionarys", dictionarys);
                            intent.putExtra("profitInfo", item);
                            startActivity(intent);
                        }
                    });
                } else {
                    tvShouYi.setBackgroundResource(R.drawable.selector_button_gray);
                    tvShouYi.setFocusable(false);
                    tvShouYi.setClickable(false);
                    tvShouYi.setEnabled(false);
                }
            }
        };
    }
}
