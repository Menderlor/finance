package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.cedarhd.adapter.CompanySpaceListAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanCrmAdapter;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.AvartarViewHelper;
import com.cedarhd.control.BoeryunNoScrollGridView;
import com.cedarhd.control.CollapsibleTextView;
import com.cedarhd.control.MultipleAttachView;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.ListViewLoader;
import com.cedarhd.control.listview.PullToRefreshAndLoadMoreListView;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.CommanDemand;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.crm.QmBase;
import com.cedarhd.models.公司空间;
import com.cedarhd.models.论坛回帖;
import com.cedarhd.utils.StrUtils;

import java.util.ArrayList;
import java.util.List;

/***
 * 公司空间-分享
 */
public class CompanyShareSpaceActivity extends BaseActivity {

    private Context mContext;
    PullToRefreshAndLoadMoreListView mListView;
    private MyProgressBar mProgressBar;
    private ImageView iv_back;
    private ImageView ivnew;
    private TextView tv_tittle;
    private ListViewHelperNet<公司空间> mListViewHelperKjx = null;
    private QueryDemand queryDemand;
    private CommanCrmAdapter<公司空间> adapter;
    private ZLServiceHelper zeHelper;
    // private Demand demand;
    private ListViewLoader<公司空间> mListViewLoader;
    private QmBase qmBase;
    private CommanDemand commanDemand;
    private boolean isFling;
    public static boolean isResume = false; // 是否在Resume中刷新
    private String value;// 查询数据库的字段值
    private CompanySpaceListAdapter mListAdapter;
    public static List<公司空间> m公司空间List;
    private ImageView add;

    public static final int REQUEST_CODE_NEW_COMPANYSPACE = 0;
    public final static int SUCESS_READ_COMPANYSPACE = 10;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == SUCESS_READ_COMPANYSPACE) {
                mListAdapter.notifyDataSetChanged();
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        value = UserBiz.getGlobalUserId();
        setContentView(R.layout.activity_companyspace_list);
        findviews();

        zeHelper = new ZLServiceHelper();
        queryDemand = new QueryDemand("更新时间");
        mContext = CompanyShareSpaceActivity.this;
        m公司空间List = new ArrayList<公司空间>();
        adapter = getAdapter();
        qmBase = new QmBase();
        qmBase.Offset = 0;
        qmBase.PageSize = 5;
        qmBase.Type = 2;
        qmBase.NoPager = false;
//		queryDemand.fildName = "更新时间";
//		queryDemand.sortFildName = "UpdateTime";
        mListViewLoader = new ListViewLoader<公司空间>(mContext,
                "CompanySpace/GetTieziList", mListView, adapter, qmBase,
                queryDemand, 公司空间.class);

        mListView.setSerarchViewVisible(false);


        setonclicklistener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isResume) {
            isResume = false;
            reLoadData();
        }
    }

    private void reLoadData() {
        qmBase.Offset = 0;
        mListViewLoader.clearData();
        mListViewLoader.startRefresh();
    }

    private void findviews() {
        // 初始化控件
        mListView = (PullToRefreshAndLoadMoreListView) findViewById(R.id.listView12);
        mProgressBar = (MyProgressBar) findViewById(R.id.progress_companyspacelist);
        iv_back = (ImageView) findViewById(R.id.imageViewCancela);
        tv_tittle = (TextView) findViewById(R.id.spacetitle);
        ivnew = (ImageView) findViewById(R.id.imageViewNews);
        add = (ImageView) findViewById(R.id.imageViewNews);
        iv_back.setVisibility(View.VISIBLE);

        tv_tittle.setText("公司空间");
    }

    private void setonclicklistener() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(CompanyShareSpaceActivity.this,
                        CompanySpaceNewActivity.class);
                startActivityForResult(intent, REQUEST_CODE_NEW_COMPANYSPACE);
            }
        });

        iv_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        // 添加监听事件
        ivnew.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(CompanyShareSpaceActivity.this,
                        CompanySpaceNewActivity.class);
                startActivityForResult(intent, REQUEST_CODE_NEW_COMPANYSPACE);
            }
        });

		/*
         * mListView.setOnScrollListener(new OnScrollListener() {
		 * 
		 * @Override public void onScroll(AbsListView view, int
		 * firstVisibleItem, int visibleItemCount, int totalItemCount) {
		 * 
		 * }
		 * 
		 * @Override public void onScrollStateChanged(AbsListView view, int
		 * scrollState) { // 获得是否Fling标志 isFling = (scrollState ==
		 * OnScrollListener.SCROLL_STATE_FLING); LogUtils.i("MyISFling",
		 * String.valueOf(isFling)); mListAdapter.setFling(isFling); } });
		 */

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int pos = position = mListView.getHeaderViewsCount();
                公司空间 item = m公司空间List.get(pos);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ShareInfoActivity.TAG_INFO, item);
                Intent intent = new Intent(mContext, ShareInfoActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private CommanCrmAdapter<公司空间> getAdapter() {
        return new CommanCrmAdapter<公司空间>(m公司空间List, mContext,
                R.layout.companyspacelist_listviewlayout) {

            public AvartarView avartarView;
            public TextView textViewTime;
            public CollapsibleTextView textViewContent;
            public ImageView ivAttach;
            public RelativeLayout left_iv_attachment;
            public LinearLayout layout;
            public BoeryunNoScrollGridView gridView;
            public List<论坛回帖> listComment;
            public ListView list_fourm;
            private MultipleAttachView attachView;
            public RelativeLayout rlAttachs;
            public TextView tvAttachCount;
            public TextView tvDelete;
            public TextView tvDept;
            public LinearLayout llDelete;
            private TextView tv_comment_count;
            private TextView tvTittle;

            private ArrayList<String> attchList;
            private DictionaryHelper dictionaryHelper;

            @Override
            public void convert(int position, final 公司空间 item,
                                BoeryunViewHolder viewHolder) {
                attchList = new ArrayList<String>();
                dictionaryHelper = new DictionaryHelper(mContext);
                avartarView = (AvartarView) viewHolder
                        .getView(R.id.avatar_companyspacelist1);
                textViewTime = (TextView) viewHolder.getView(R.id.textViewTime);
                textViewContent = (CollapsibleTextView) viewHolder
                        .getView(R.id.textViewContent);
                ivAttach = (ImageView) viewHolder.getView(R.id.iv_attachment);
                left_iv_attachment = (RelativeLayout) viewHolder
                        .getView(R.id.rela_right);
                layout = (LinearLayout) viewHolder.getView(R.id.line_group);
                gridView = (BoeryunNoScrollGridView) viewHolder
                        .getView(R.id.gv_company_list_item);
                attachView = viewHolder.getView(R.id.attch_company);
                list_fourm = (ListView) viewHolder.getView(R.id.list_count);
                rlAttachs = (RelativeLayout) viewHolder
                        .getView(R.id.rl_attach_conpanylist);
                tvAttachCount = (TextView) viewHolder
                        .getView(R.id.tv_count_attach);
                tvDelete = (TextView) viewHolder
                        .getView(R.id.tv_delete_companysapacelist_item);
                tvDept = viewHolder.getView(R.id.tv_dept_companyspacelist_item);
                llDelete = viewHolder.getView(R.id.ll_space_delete);
                tv_comment_count = viewHolder.getView(R.id.tv_companyspace_comment_count);
                tvTittle = viewHolder.getView(R.id.tv_title_share_info);
                tvTittle.setVisibility(View.VISIBLE);
                llDelete.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
//				if (!TextUtils.isEmpty(item.附件)) {
//					String[] str = item.附件.split(",");
//					for (int i = 0; i < str.length; i++) {
//						attchList.add(zeHelper.getPhotoAddr(mContext, str[i]));
//					}
//					gridView.setVisibility(View.VISIBLE);
//					NoScrollGridAdapter noGridAdapter = (NoScrollGridAdapter) gridView
//							.getTag();
//					if (noGridAdapter == null) {
//						noGridAdapter = new NoScrollGridAdapter(mContext,
//								Global.BASE_URL, attchList);
//					} else {
//						noGridAdapter.setImageUrls(attchList);
//					}
//					gridView.setAdapter(noGridAdapter);
//				} else {
//					gridView.setVisibility(View.GONE);
//				}
                attachView.loadImageByAttachIds(item.附件);

                String content = item.内容;
                String time = item.发帖时间;
                tvTittle.setText(item.标题);
                tv_comment_count.setText(item.回复次数 + "");
                textViewTime.setText(DateDeserializer.getFormatTime(time));
                tvDept.setText(StrUtils.pareseNull(dictionaryHelper.get分公司(
                        mContext, item.发帖人).get名称()));
                textViewContent.setDesc(content, BufferType.NORMAL);
                avartarView.setTag(position);
                ivAttach.setTag(position);
                layout.removeAllViews();
                // 头像
                new AvartarViewHelper(mContext, item.发帖人, avartarView,
                        position, 50, 50, true);

                // 设置九宫格小图片点击 查看大图
                gridView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // 点击回帖九宫格，查看大图
                        startImageBrower(position, attchList);
                    }
                });

                textViewContent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(CompanySpaceInfoActivity.TAG_INFO, item);
                        Intent intent = new Intent(mContext,
                                CompanySpaceInfoActivity.class);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                });
            }

            /**
             * 打开可滑动的图片查看器
             *
             * @param position
             * @param urls2
             */
            protected void startImageBrower(int position,
                                            ArrayList<String> urls2) {
                Intent intent = new Intent(mContext, ImagePagerActivity.class);
                ArrayList<String> urlList = new ArrayList<String>();
                for (int i = 0; i < urls2.size(); i++) {
                    urlList.add(Global.BASE_URL + urls2.get(i));
                }
                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urlList);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                mContext.startActivity(intent);
            }
        };
    }
}
