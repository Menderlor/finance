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

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanCrmAdapter;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.biz.VmFormBiz;
import com.cedarhd.constants.PreferencesConfig;
import com.cedarhd.constants.enums.Enum合格投资者认证状态;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.control.BoeryunSearchView;
import com.cedarhd.control.listview.ListViewLoader;
import com.cedarhd.control.listview.PullToRefreshAndLoadMoreListView;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.SharedPreferencesHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.Client;
import com.cedarhd.models.Dict;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.changhui.CHClinets;
import com.cedarhd.models.changhui.QmProduct;
import com.cedarhd.models.changhui.合格投资者认证申请单;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 长汇客户列表
 */
public class ChClientListActivity extends BaseActivity {

    private final int REQUEST_COE_ADD_CLIENT = 101;
    private final int REQUEST_COE_UPDATE_CLIENT = 102;

    /**
     * 选择客户
     */
    public static final String EXTRA_SELECT_CLIENT = "SELECT_CLIENT";

    /**
     * 选择客户：是否只查看自己
     */
    public static final String EXTRA_SELECT_MY_CLIENT = "SELECT_MY_CLIENT";

    /**
     * 选择客户过滤条件
     */
    public static final String EXTRA_QUERY_FILTER = "EXTRA_QUERY_FILTER";

    private Context mContext;

    private BoeryunHeaderView headerView;
    private PullToRefreshAndLoadMoreListView lv;

    private boolean mIsSelectClient;
    private boolean mIsSelectMyClient;
    private String mFilter;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private String permission = "";

    private QmProduct mQmProduct;
    private QueryDemand mQueryDemand;
    private List<CHClinets> mList;
    private CommanCrmAdapter<CHClinets> mAdapter;
    private ListViewLoader<CHClinets> mListViewLoader;

    public static boolean isResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ch_client_list);
        initViews();
        initData();
        setOnEvent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_COE_ADD_CLIENT:
                    reLoadData();
                    break;
                case REQUEST_COE_UPDATE_CLIENT:
                    reLoadData();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isResume) {
            reLoadData();
            isResume = false;
        }
    }

    private void initViews() {
        headerView = (BoeryunHeaderView) findViewById(R.id.header_ch_client_list);
        lv = (PullToRefreshAndLoadMoreListView) findViewById(R.id.lv_pull_to_refresh);
        lv.setHintText("请输入客户名称");
    }

    private void initData() {
        mContext = this;
        sharedPreferencesHelper = new SharedPreferencesHelper(mContext, PreferencesConfig.APP_USER_INFO);
        permission = sharedPreferencesHelper.getValue(
                PreferencesConfig.POINT_PERMISSION);
        mIsSelectClient = getIntent().getBooleanExtra(EXTRA_SELECT_CLIENT,
                false);
        mIsSelectMyClient = getIntent().getBooleanExtra(EXTRA_SELECT_MY_CLIENT,
                false);
        mFilter = getIntent().getStringExtra(EXTRA_QUERY_FILTER);
        mList = new ArrayList<CHClinets>();
        mAdapter = getAdapter();
        mQmProduct = new QmProduct();
        mQmProduct.PageSize = 20;
        mQmProduct.Offset = 0;
        mQmProduct.NoPager = false;
        mQueryDemand = new QueryDemand("最后更新");
//        if (TextUtils.isEmpty(mFilter)) {
//            mFilter = "1=1";
//        }
//
//        if (mIsSelectMyClient) {
//            mFilter += " AND 业务员=" + com.cedarhd.helpers.Global.mUser.getId();
//        }
//        mQmProduct.moreFilter = mFilter;

//		String url = Global.BASE_URL + "Customer/GetCustomers";
//		
//		StringRequest.postAsyn(url, mQmProduct, new StringResponseCallBack() {
//			
//			@Override
//			public void onResponseCodeErro(String result) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onResponse(String response) {
//				LogUtils.i(TAG, response);
//				mList = JsonUtils.ConvertJsonToList(response, 长汇客户.class);
//				mAdapter = getAdapter();
//				lv.setAdapter(mAdapter);
//			}
//			
//			@Override
//			public void onFailure(Request request, Exception ex) {
//				// TODO Auto-generated method stub
//				
//			}
//		});

        mListViewLoader = new ListViewLoader<CHClinets>(mContext,
                "Customer/GetClients", lv, mAdapter, mQmProduct, mQueryDemand,
                CHClinets.class);

        if (!permission.contains(",840,")) {
            headerView.ivSave.setVisibility(View.GONE);
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

                CHClinets chClient = mList.get(pos);
                Client client = new Client();
                client.Id = chClient.编号;
                client.CustomerName = chClient.名称;
                if (mIsSelectClient) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putInt(ClientBiz.ClientId, chClient.编号);
                    bundle.putString(ClientBiz.ClientName, chClient.名称);
                    bundle.putSerializable(ClientBiz.EXTRA_CLIENT_OBJECT,
                            client);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {

                    Intent intent = new Intent(mContext,
                            ChClientInfoActivity.class);
                    Bundle bundle = new Bundle();
                    if (permission.contains(",840,")) {
                        bundle.putBoolean("isNewClient", true);
                    }
                    bundle.putInt(ChClientInfoActivity.EXTRA_CLIENT_ID,
                            mList.get(pos).编号);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUEST_COE_UPDATE_CLIENT);
                }

            }
        });

        headerView.setOnButtonClickListener(new OnButtonClickListener() {
            @Override
            public void onClickSaveOrAdd() {

                Intent intent = new Intent(mContext, ChClientInfoActivity.class);
                intent.putExtra("isNewClient", true);
                startActivityForResult(intent, REQUEST_COE_ADD_CLIENT);
            }

            @Override
            public void onClickFilter() {

            }

            @Override
            public void onClickBack() {
                finish();
            }
        });

        lv.mSearchView
                .setOnButtonClickListener(new BoeryunSearchView.OnButtonClickListener() {
                    @Override
                    public void OnSearch(String str) {
//                        String filter = "(名称 like '%" + str
//                                + "%' or 手机 like '%" + str + "%')";
//
//                        mQmProduct.moreFilter = getQueryFilter(filter);
                        mQmProduct.Keyword = str;
                        reLoadData();
                    }

                    @Override
                    public void OnCancle() {
//                        mQmProduct.moreFilter = getQueryFilter("");
                        mQmProduct.Keyword = "";
                        reLoadData();
                    }
                });
    }

    private void reLoadData() {
        mQmProduct.Offset = 0;
        mListViewLoader.clearData();
        mListViewLoader.startRefresh();
    }

    private String getQueryFilter(String filter) {
        if (!TextUtils.isEmpty(filter)) {
            return mFilter + " and " + filter;
        }
        return mFilter;
    }

    private CommanCrmAdapter<CHClinets> getAdapter() {
        return new CommanCrmAdapter<CHClinets>(mList, mContext,
                R.layout.item_ch_client) {
            @Override
            public void convert(int position, final CHClinets item,
                                BoeryunViewHolder viewHolder) {
                TextView tv_renzheng = viewHolder.getView(R.id.tv_zige_renzheng_ch_client_item);

                viewHolder.setTextValue(R.id.tv_renzheng_status_ch_client_item, Enum合格投资者认证状态.getStatusNameById(item.资格认证状态));

                if (item.资格认证状态 == Enum合格投资者认证状态.已认证.getValue() ||
                        item.资格认证状态 == Enum合格投资者认证状态.认证中.getValue()) {
                    tv_renzheng.setVisibility(View.GONE);
                } else {
                    tv_renzheng.setVisibility(View.VISIBLE);
                }


                tv_renzheng.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getQualificationByID(item.编号);
                    }
                });

                if (!TextUtils.isEmpty(item.名称)) {
                    String name = item.名称.substring(0, 1);
                    for (int i = 0; i < item.名称.length() - 1; i++) {
                        name += "*";
                    }
                    viewHolder.setTextValue(R.id.tv_name_ch_client_item,
                            StrUtils.pareseNull(name));
                }
                viewHolder.setTextValue(R.id.tv_user_ch_client_item,
                        StrUtils.pareseNull(this.getDictName("员工", item.业务员)));
                viewHolder.setTextValue(R.id.id_client_item, item.客户编号);
                if (!TextUtils.isEmpty(item.生日)) {
                    viewHolder.setTextValue(R.id.birth_client_item, ViewHelper.convertStrToFormatDateStr(item.生日, "yyyy-MM-dd"));
                    viewHolder.getView(R.id.birth_name_client_item).setVisibility(View.VISIBLE);
                    viewHolder.getView(R.id.birth_client_item).setVisibility(View.VISIBLE);
                } else {
                    viewHolder.getView(R.id.birth_name_client_item).setVisibility(View.GONE);
                    viewHolder.getView(R.id.birth_client_item).setVisibility(View.GONE);
                }
//                viewHolder.setTextValue(R.id.tv_client_type_ch_client_item,
//                        StrUtils.pareseNull(this.getDictName("联系状态", item.状态)));
                TextView tvApply = viewHolder
                        .getView(R.id.tv_apply_ch_client_item);
                TextView tvType = viewHolder
                        .getView(R.id.tv_type_ch_client_item);


                String clientType = this.getDictName("联系状态", item.状态);
                TextView tvClientType = viewHolder
                        .getView(R.id.tv_client_type_ch_client_item);
                tvClientType.setText(clientType);
                if (!TextUtils.isEmpty(clientType)) {
                    tvClientType.setVisibility(View.VISIBLE);
                } else {
                    tvClientType.setVisibility(View.GONE);
                }


                String typeName = this.getDictName("客户_客户性质", item.客户性质);
                tvType.setText(StrUtils.pareseNull(typeName));
                if (!TextUtils.isEmpty(typeName)) {
                    tvType.setVisibility(View.VISIBLE);
                } else {
                    tvType.setVisibility(View.GONE);
                }
                tvApply.setVisibility(View.GONE);
//				if (item.会员状态 <= 0) {
//					tvApply.setVisibility(View.VISIBLE);
//				} else {
//					// 申请中 或 已入会 则不显示状态
//					tvApply.setVisibility(View.GONE);
//				}
                tvApply.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // HashMap<String, Object> properties = new
                        // HashMap<String, Object>();
                        // Dict dict = new Dict();
                        // dict.编号 = item.编号;
                        // dict.名称 = item.名称;
                        // properties.put("客户", dict);
                        // VmFormBiz.startNewVmFromActivity(mContext, 10090,
                        // "俱乐部入会申请", properties);

                        HashMap<String, Object> properties = new HashMap<String, Object>();

                        HashMap<String, List<Dict>> dictMap = mAdapter
                                .getmDictionarys();
                        Iterator<Entry<String, List<Dict>>> iterator = dictMap
                                .entrySet().iterator();

                        // TODO 有点小问题，需要处理下
                        // while (iterator.hasNext()) {
                        // // 处理客户携带字典转为vmForm可识别的参数
                        // Entry<String, List<Dict>> entry = iterator.next();
                        // String dictName = entry.getKey();
                        // try {
                        // Class<长汇客户> classType = 长汇客户.class;
                        // Field field = classType
                        // .getDeclaredField(dictName);
                        // // 获取指定字典编号
                        // Object objValue = field.get(item);
                        // if (objValue != null
                        // && objValue instanceof Integer) {
                        // int dictId = (Integer) objValue;
                        // Dict dict = new Dict(dictId, mAdapter
                        // .getDictName(dictName, dictId));
                        // properties.put(dictName, dict);
                        // }
                        // } catch (NoSuchFieldException e) {
                        // // TODO Auto-generated catch block
                        // e.printStackTrace();
                        // } catch (IllegalAccessException e) {
                        // // TODO Auto-generated catch block
                        // e.printStackTrace();
                        // } catch (IllegalArgumentException e) {
                        // // TODO Auto-generated catch block
                        // e.printStackTrace();
                        // }
                        // }

                        properties.put("客户", item);

                        VmFormBiz.startNewVmFromActivity(mContext, 10090,
                                "俱乐部入会申请", properties);
                    }
                });
            }
        };
    }


    /**
     * 根据客户id获取资格认证申请单
     */
    private void getQualificationByID(int id) {
        String url = Global.BASE_URL + "Customer/GetNewCertificationByCustomerId/" + id;

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                合格投资者认证申请单 shenqing = JsonUtils.ConvertJsonObject(response, 合格投资者认证申请单.class);
                HashMap<String, List<Dict>> dictionarys = mAdapter
                        .getmDictionarys();
                Intent intent = new Intent(mContext,
                        ChQualificationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ChQualificationActivity.CLEW,
                        shenqing);
                bundle.putSerializable(ChQualificationActivity.DICTIONARYS,
                        dictionarys);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {
                if ("405".equals(JsonUtils.parseStatus(result))) {
                    合格投资者认证申请单 shenqing = JsonUtils.ConvertJsonObject(result, 合格投资者认证申请单.class);
                    HashMap<String, List<Dict>> dictionarys = mAdapter
                            .getmDictionarys();
                    Intent intent = new Intent(mContext,
                            ChQualificationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ChQualificationActivity.CLEW,
                            shenqing);
                    bundle.putSerializable(ChQualificationActivity.DICTIONARYS,
                            dictionarys);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    showShortToast(JsonUtils.pareseMessage(result));
                }
            }
        });
    }
}
