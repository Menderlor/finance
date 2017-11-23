package com.cedarhd;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cedarhd.adapter.NoticeListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.BoeryunSearchView;
import com.cedarhd.control.BoeryunSearchView.OnSearchedListener;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ShakeListenerUtils;
import com.cedarhd.helpers.ShakeListenerUtils.OnShakeListener;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.User;
import com.cedarhd.models.通知;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 通知列表
 *
 * @author KJX
 */
public class NoticeListActivity extends BaseActivity {
    private Context context;
    PullToRefreshListView mListView;
    List<通知> m通知List;
    private NoticeListViewAdapter mListAdapter;
    private MyProgressBar mProgressBar;
    private ImageView ivNew;
    private RelativeLayout rl_choose;// 选择人员的按钮
    private BoeryunSearchView searchView;
    private RelativeLayout rl_search_root;
    // private ListViewHelper mListViewHelper = null;
    // private ListViewHelperKjx mListViewHelperKjx = null;

    private ListViewHelperNet<通知> mListViewHelperNet;
    private QueryDemand queryDemand;
    private Demand demand;

    private boolean isFling;
    public static boolean isResume; // 是否在Resume中刷新

    private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
    public String mUserSelectId = "";
    public String mUserSelectName = "";
    public static final int REQUEST_CODE_NEW_NOTICE = 0;
    public static final int REQUEST_CODE_SELECT_ID = 1;
    // 查询数据库的字段名(like包含关系)
    private String value;// 查询数据库的字段值

    public final static int SUCESS_READ_NOTICE = 10;
    public final static int SUCESS_GET_PERMISSION = 3;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == SUCESS_READ_NOTICE) {
                mListAdapter.notifyDataSetChanged();
            } else if (msg.what == SUCESS_GET_PERMISSION) {
                String permissions = (String) msg.obj;
                if (permissions.contains("389")) {
                    // 具备新建通知权限
                    ivNew.setVisibility(View.VISIBLE);
                }
            } else if (msg.what == UPDATE_READ) {
                reload();
                Toast.makeText(NoticeListActivity.this, "所有通知设置为已读",
                        Toast.LENGTH_SHORT).show();
            }
        }

        ;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.noticelist);
        value = Global.mUser.Id;
        context = NoticeListActivity.this;
        findViews();
        setOnClickListener();
        getPermission();
        createDialog();
        m通知List = new ArrayList<通知>();
        queryDemand = new QueryDemand();
        demand = new Demand();
        demand.用户编号 = value;
        demand.表名 = "通知";
        // demand.方法名 = "查询_分页";
        demand.方法名 = "Notice/GetNoticeList";
        demand.条件 = "";
        demand.附加条件 = "";
        demand.每页数量 = 20;
        demand.偏移量 = 0;
        queryDemand.fildName = "最后更新";
        queryDemand.sortFildName = "UpdateTime";
        mListAdapter = new NoticeListViewAdapter(NoticeListActivity.this,
                R.layout.noticelist_listviewlayout, m通知List, null);
        mListView.setAdapter(mListAdapter);
        mListViewHelperNet = new ListViewHelperNet<通知>(this, 通知.class, demand,
                mListView, m通知List, mListAdapter, mProgressBar, queryDemand);
        reload();
    }

    private void reload() {
        boolean isConnectedInternet = HttpUtils
                .IsHaveInternet(NoticeListActivity.this);
        if (!isConnectedInternet) {
            Toast.makeText(NoticeListActivity.this,
                    "需要连接到3G或者wifi因特网才能获取最新信息！", Toast.LENGTH_LONG).show();
            // mListViewHelperKjx.loadLocalData();
        } else {
            m通知List.clear();
            mListViewHelperNet.setNotifyDataSetChanged();
            // mListViewHelperKjx.loadServerData(true);
            mListViewHelperNet.loadServerData(true);
        }
    }

    /**
     * 摇一摇监听类
     */
    ShakeListenerUtils mShakeListener = null;

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i("noticeList", "noticeList onResume");
        if (isResume) {
            isResume = false;
            reload();
        }
        // 摇一摇
        mShakeListener = new ShakeListenerUtils(NoticeListActivity.this);
        mShakeListener.setOnShakeListener(new shakeLitener());

    }

    @Override
    protected void onPause() {
        super.onPause();
        mShakeListener.stop();
    }

    AlertDialog dialog;
    private String statusStr;
    HttpUtils httpUtils;
    String path = Global.BASE_URL + Global.EXTENSION + "ReadStatus/SetAllRead/"
            + 1;
    public static final int UPDATE_READ = 1021;

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                NoticeListActivity.this);
        builder.setTitle("提示");
        builder.setMessage("是否将数据设置为已读");
        httpUtils = new HttpUtils();
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        statusStr = httpUtils.httpGet(path);
                        LogUtils.i("out", statusStr + path);
                        JSONObject object;
                        try {
                            object = new JSONObject(statusStr);
                            if (object.getInt("Status") == 1) {
                                handler.sendEmptyMessage(UPDATE_READ);

                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).start();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", null);
        dialog = builder.create();
    }

    private class shakeLitener implements OnShakeListener {

        public void onShake() {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        }

    }

    public void setOnClickListener() {
        ImageView ImageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
        ImageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rl_choose = (RelativeLayout) findViewById(R.id.rl_choose_noticelist);
        rl_choose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到选择员工的Activity
                Intent intent = new Intent(NoticeListActivity.this,
                        User_SelectActivityNew_zmy.class);
                // 单选标志位
                intent.putExtra(User_SelectActivityNew_zmy.SELECT_EMPLOYEE,
                        true);
                startActivityForResult(intent, REQUEST_CODE_SELECT_ID);
            }
        });

        ivNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotice();
            }
        });

        // 监听搜索框文字变化
        searchView.setOnSearchedListener(new OnSearchedListener() {
            @Override
            public void OnSearched(String str) {
                LogUtils.i("noticeList", "获取到文字变化了：" + str);
                search(str);
            }
        });

        mListView
                .setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mListViewHelperNet.mListViewLoadType = ListViewLoadType.顶部视图;
                        try {
                            // 下拉刷新 导入数据
                            mListViewHelperNet.loadServerData(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        // listview滚动监听
        mListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 获得是否Fling标志
                isFling = (scrollState == OnScrollListener.SCROLL_STATE_FLING);
                mListAdapter.setFling(isFling);
                LogUtils.i("scroll", "onScrollStateChanged--->isFling="
                        + isFling);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                LogUtils.i("onScroll", "第一个可见项：" + firstVisibleItem);
                if (firstVisibleItem == 1) {
                    rl_search_root.setVisibility(View.VISIBLE);
                } else if (firstVisibleItem > 1) {
                    rl_search_root.setVisibility(View.GONE);
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // postion 从Listview中Header起始
                ListView listView = (ListView) parent;
                final 通知 map = (通知) listView.getItemAtPosition(position);
                if (TextUtils.isEmpty(map.ReadTime)) {
                    mListAdapter.getDataList().get(position - 1).ReadTime = ViewHelper
                            .getDateString();
                    mListAdapter.notifyDataSetChanged();
                    // 如果通知是未读，设置为已读
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // zlServiceHelper.ReadNotice(map,
                                // NoticeListActivity.this, handler);
                                zlServiceHelper.ReadDynamic(map.Id, 1);
                            } catch (Exception e) {
                                LogUtils.e("erro", "" + e);
                            }
                        }
                    }).start();
                }

                Intent intent = new Intent(NoticeListActivity.this,
                        NoticeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Notice", map);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void findViews() {
        // mbuttonNew = (Button) findViewById(R.id.buttonNew);
        ivNew = (ImageView) findViewById(R.id.imageViewNew);
        mListView = (PullToRefreshListView) findViewById(R.id.listView1);
        searchView = (BoeryunSearchView) findViewById(R.id.searchview_noticelist);
        rl_search_root = (RelativeLayout) findViewById(R.id.rl_search_root_noticelist);
        mProgressBar = (MyProgressBar) findViewById(R.id.progress_noticelist);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * 新建通知
     */
    private void createNotice() {
        Intent intent = new Intent();
        intent.setClass(context, NoticeNewActivity.class);
        startActivityForResult(intent, REQUEST_CODE_NEW_NOTICE);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_NEW_NOTICE) {
            if (resultCode == NoticeNewActivity.RESULT_CODE_SUCCESS) {
                LogUtils.e("guojianwen",
                        "NoticeListActivity onActivityResult RESULT_CODE_SUCCESS");
                // reload(); //导致插入多条数据
            } else if (resultCode == NoticeNewActivity.RESULT_CODE_FAILED) {
                LogUtils.e("guojianwen",
                        "NoticeListActivity onActivityResult RESULT_CODE_FAILED");
            }
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SELECT_ID) {
            // 取出字符串
            Bundle bundle = data.getExtras();
            mUserSelectId = bundle.getString("UserSelectId");
            mUserSelectName = bundle.getString("UserSelectName");
            LogUtils.i("mUserSelectId", mUserSelectId);
            if (mUserSelectName != null && !mUserSelectName.isEmpty()) {
                String publisher = mUserSelectId.replaceAll("'", "")
                        .replaceAll(";", "");
                value = publisher;// 只能取得一个用户的id
                // columnLikeName.add("Publisher");
                queryDemand.eqDemand.clear();
                queryDemand.eqDemand.put("Publisher", value);
                // demand.用户编号 = value;
                demand.附加条件 = " 发布人=" + value;
                reload();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("global", Global.mUser);
        LogUtils.i("lifeState", "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Global.mUser = (User) savedInstanceState.getSerializable("global");
        LogUtils.i("lifeState", "onRestoreInstanceState");
        LogUtils.i("lifeState", Global.mUser.Passport);
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    ;

    private void search(String str) {
        demand.Keyword = str;
        mListViewHelperNet.setmDemand(demand);
        reload();
    }

    /**
     * 根据权限加载模块
     */
    private void getPermission() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String data = zlServiceHelper.GetPermissions();
                    Message msg = handler.obtainMessage();
                    msg.obj = data;
                    msg.what = SUCESS_GET_PERMISSION;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    LogUtils.e("NoticeList", "" + e);
                }
            }
        }).start();
    }
}