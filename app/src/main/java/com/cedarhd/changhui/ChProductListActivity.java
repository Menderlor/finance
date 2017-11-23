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

import com.cedarhd.ProductInfoActivity;
import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanCrmAdapter;
import com.cedarhd.constants.enums.Enum理财产品产品状态;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickRightListener;
import com.cedarhd.control.BoeryunSearchView;
import com.cedarhd.control.listview.ListViewLoader;
import com.cedarhd.control.listview.PullToRefreshAndLoadMoreListView;
import com.cedarhd.helpers.DictIosPickerBottomDialog;
import com.cedarhd.helpers.DictIosPickerBottomDialog.OnSelectedListener;
import com.cedarhd.models.Dict;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.changhui.QmProduct;
import com.cedarhd.models.changhui.理财产品;
import com.cedarhd.utils.DoubleUtils;
import com.cedarhd.utils.StrUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 长汇产品列表
 */
public class ChProductListActivity extends BaseActivity {

    public static final String EXTRAS_IS_SELECT = "extra_select";
    public static final String EXTRAS_SELECT_PRODUCT = "extra_select_product";

    private Context mContext;
    private BoeryunHeaderView headerView;
    private PullToRefreshAndLoadMoreListView lv;

    private QmProduct mQmProduct;
    private QueryDemand mQueryDemand;
    private List<理财产品> mList;
    private CommanCrmAdapter<理财产品> mAdapter;
    private ListViewLoader<理财产品> mListViewLoader;
    private DecimalFormat mdDecimalFormat;
    private DictIosPickerBottomDialog mDictIosPickerBottomDialog;

    /**
     * 是否选择产品
     */
    private boolean mIsSelect;

    private String mSearchKey;
    private int mType = -1;
    private String mTypeName = "";

    private String[] statuNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ch_productlist);
        initViews();
        initData();
        setOnEvent();
    }

    private void initViews() {
        headerView = (BoeryunHeaderView) findViewById(R.id.header_ch_product_list);
        lv = (PullToRefreshAndLoadMoreListView) findViewById(R.id.lv_pull_to_refresh);
        lv.setHintText("请输入产品名称关键字");
    }

    private void initData() {
        mContext = this;
        statuNames = new String[]{"所有", "在售", "待售"};
        mList = new ArrayList<理财产品>();
        mdDecimalFormat = new DecimalFormat("#####0.00");
        mAdapter = getAdapter();
        mQmProduct = new QmProduct();
        mQmProduct.PageSize = 20;
        mQmProduct.Offset = 0;
        mQueryDemand = new QueryDemand("编号");
        mListViewLoader = new ListViewLoader<理财产品>(mContext,
                "Wealth/GetProductList", lv, mAdapter, mQmProduct,
                mQueryDemand, 理财产品.class);
        mDictIosPickerBottomDialog = new DictIosPickerBottomDialog(mContext);
        mIsSelect = getIntent().getBooleanExtra(EXTRAS_IS_SELECT, false);
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
                理财产品 product = mList.get(pos);
                if (mIsSelect) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(EXTRAS_SELECT_PRODUCT, product);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    /*
                     * Intent intent = new Intent(mContext,
					 * ChProductInfoActivity.class);
					 * intent.putExtra(ChProductInfoActivity.EXTRA_PRODUCT_ID,
					 * product.编号); startActivity(intent);
					 */
                    Intent intent = new Intent(mContext,
                            ProductInfoActivity.class);
                    intent.putExtra("productInfo", product.编号);
                    startActivity(intent);
                }
                // else {
                // HashMap<String, List<Dict>> dictionarys = mAdapter
                // .getmDictionarys();
                // Intent intent = new Intent(mContext,
                // ChBespokeInfoActivity.class);
                // Bundle bundle = new Bundle();
                // bundle.putSerializable(ChBespokeInfoActivity.CLEW, product);
                // bundle.putSerializable(ChBespokeInfoActivity.DICTIONARYS,
                // dictionarys);
                // intent.putExtras(bundle);
                // startActivity(intent);
                // }
            }
        });

        lv.mSearchView
                .setOnButtonClickListener(new BoeryunSearchView.OnButtonClickListener() {
                    @Override
                    public void OnSearch(String searchStr) {
                        mSearchKey = searchStr;
//                        initMoreFilter();
                        mQmProduct.Name = searchStr;
                        reLoadData();
                    }

                    @Override
                    public void OnCancle() {
                        mSearchKey = "";
//                        initMoreFilter();
                        mQmProduct.Name = "";
                        reLoadData();
                    }
                });

        headerView
                .setmButtonClickRightListener(new OnButtonClickRightListener() {
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

                    @Override
                    public void onRightTextClick() {
                        final List<Dict> list = Enum理财产品产品状态.getAllDicts();
//                        mDictIosPickerBottomDialog.show(
//                                Enum理财产品产品状态.getAllDicts(), "名称");
                        mDictIosPickerBottomDialog.show(statuNames);
                        mDictIosPickerBottomDialog
                                .setOnSelectedListener(new OnSelectedListener() {
                                    @Override
                                    public void onSelected(int index) {
//                                        if (index >= 0 && index < list.size()) {
//                                            Dict dict = list.get(index);
//                                            headerView.setRightTitle(StrUtils
//                                                    .pareseNull(dict.名称));
//                                            mType = dict.编号;
//                                            mTypeName = dict.名称;
////                                            initMoreFilter();
//                                            mQmProduct.Status = dict.编号 + "";
//                                            reLoadData();
//                                        }
                                        mQmProduct.Status = index + "";
                                        reLoadData();
                                    }
                                });
                    }
                });
    }

    private void initMoreFilter() {
        if (!TextUtils.isEmpty(mSearchKey)) {
            mQmProduct.moreFilter = "产品名称 like '%" + mSearchKey + "%'";
        } else {
            mQmProduct.moreFilter = "";
        }

        // if (mType != -1) {
        // String filter = "上线状态=" + mType;
        // if (!TextUtils.isEmpty(mQmProduct.moreFilter)) {
        // mQmProduct.moreFilter += " and " + filter;
        // } else {
        // mQmProduct.moreFilter = filter;
        // }
        // }

        if (!TextUtils.isEmpty(mTypeName)) {
            String filter = "产品状态='" + mTypeName + "'";
            if (!TextUtils.isEmpty(mQmProduct.moreFilter)) {
                mQmProduct.moreFilter += " and " + filter;
            } else {
                mQmProduct.moreFilter = filter;
            }
        }
    }

    private void reLoadData() {
        mQmProduct.Offset = 0;
        mListViewLoader.clearData();
        mListViewLoader.startRefresh();
    }

    private CommanCrmAdapter<理财产品> getAdapter() {
        return new CommanCrmAdapter<理财产品>(mList, mContext,
                R.layout.item_ch_product) {
            @Override
            public void convert(int position, final 理财产品 item,
                                BoeryunViewHolder viewHolder) {
                TextView tvClient = viewHolder
                        .getView(R.id.tv_client_ch_productlist_item);
                TextView tvSimple = viewHolder
                        .getView(R.id.tv_simple_ch_productlist_item);
                TextView tvCount = viewHolder
                        .getView(R.id.tv_count_item_ch_productlist);
                TextView tvBay = viewHolder
                        .getView(R.id.tv_bay_ch_productlist_item); // 购买
                TextView tvSy = viewHolder
                        .getView(R.id.tv_shengyuedu_item_ch_productlist); // 剩余额度
                TextView tvStatus = viewHolder
                        .getView(R.id.tv_status_item_ch_productlist); // 剩余额度
                TextView tvQici = viewHolder
                        .getView(R.id.tv_qici_item_ch_productlist); // 期次

                tvClient.setText(StrUtils.pareseNull(item.产品名称));
                tvSimple.setText(StrUtils.pareseNull(item.产品简称));
                tvCount.setText(StrUtils.pareseNull(DoubleUtils
                        .formatFloatNumber(item.募集额度)));
                tvStatus.setText(StrUtils.pareseNull(item.产品状态));
                if (TextUtils.isEmpty(item.可买期限)) {
                    tvQici.setText("无");
                } else {
                    tvQici.setText(StrUtils.pareseNull(item.可买期限 + ""));
                }
                double total = item.剩余额度 <= 0 ? 0 : item.剩余额度;
                tvSy.setText(mdDecimalFormat.format(total) + "");
                // tvContact.setText(StrUtils.pareseNull(item.联系人));
                // tvPhone.setText(StrUtils.pareseNull(item.联系电话));
                // tvTime.setText(DateDeserializer.getFormatTime(item.创建时间));

                if ("在售".equals(item.产品状态)) {
                    tvBay.setVisibility(View.VISIBLE);
                    if (item.预约控制 == 1) {
                        // 购买 -产品购买合同
//                        tvBay.setText("购买");
//                        tvBay.setOnClickListener(new OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(mContext,
//                                        ChContactInfoActivity.class);
//                                Bundle bundle = new Bundle();
//                                bundle.putSerializable(EXTRAS_SELECT_PRODUCT,
//                                        item);
//                                intent.putExtras(bundle);
//                                startActivity(intent);
//                            }
//                        });
                        tvBay.setText("预约");
                        tvBay.setBackgroundResource(R.drawable.selector_button_gray);
                        tvBay.setFocusable(false);
                        tvBay.setClickable(false);
                        tvBay.setEnabled(false);
                    } else if (item.剩余额度 != 0) {
                        // 预约 - 理财产品预约
                        tvBay.setBackgroundResource(R.drawable.selector_button_blue);
                        tvBay.setClickable(true);
                        tvBay.setEnabled(true);


                        tvBay.setText("预约");
                        tvBay.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext,
                                        ChBespokeInfoActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(EXTRAS_SELECT_PRODUCT,
                                        item);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                    } else {
                        // tvBay.setVisibility(View.GONE);
                        tvBay.setBackgroundResource(R.drawable.selector_button_gray);
                        tvBay.setFocusable(false);
                        tvBay.setClickable(false);
                        tvBay.setEnabled(false);
                    }
                } else {
                    tvBay.setBackgroundResource(R.drawable.selector_button_gray);
                    tvBay.setFocusable(false);
                    tvBay.setClickable(false);
                    tvBay.setEnabled(false);
                }
            }
        };
    }
}
