package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cedarhd.adapter.ExpandAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Client;
import com.cedarhd.models.Node;
import com.cedarhd.models.部门;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择部门， 进来之后先从本地数据库中读取数据显示，然后在有网络的情况下访问服务器，看是否更新本地数据
 * 
 * @author kjx
 * @since 2014/9/18
 */
public class DepartmentActivity extends BaseActivity {
	private Context context;
	private ListView lv;
	private ZLServiceHelper zlServiceHelper;

	private List<Node> allNodes = new ArrayList<Node>();
	private List<Node> showNodes = new ArrayList<Node>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.departmentactivity);
		findViews();
	}

	private void findViews() {
		context = DepartmentActivity.this;
		zlServiceHelper = new ZLServiceHelper();
		lv = (ListView) findViewById(R.id.lv_select_dept);
		new QueryDeptTask().execute("");

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LogUtils.i("DeptList",
						position + "----" + showNodes.get(position).getId()
								+ "--" + showNodes.get(position).getName());

				Intent intent = new Intent();
				Bundle b = new Bundle();
				b.putString("DeptSelectId", showNodes.get(position).getId()
						+ "");
				b.putString("DeptSelectName", showNodes.get(position).getName());
				intent.putExtras(b);
				setResult(1001, intent);
				finish();
			}
		});
	}

	/**
	 * 递归函数（添加子节点）初始化显示的list
	 * 
	 * @param mNode
	 */
	private void initShowList(Node mNode) {
		if (mNode != null) {
			int leftPadding = mNode.getLeftPadding();
			if (mNode.getChildList() != null && mNode.getChildList().size() > 0) {
				List<Node> childList = mNode.getChildList();
				for (int i = 0; i < childList.size(); i++) {
					Node node = childList.get(i);
					node.setLeftPadding((int) ViewHelper.dip2px(context, 25)
							+ leftPadding);
					showNodes.add(node);
					initShowList(node);
				}
			}
		}
	}

	/**
	 * 递归函数（设置节点）
	 */
	private void setShowList(Node mNode) {
		if (mNode != null) {
			// mNode.setLeftPadding(leftPadding);
			int nodeId = mNode.getId(); // 取得当前节点的分类编号
			if (allNodes != null && allNodes.size() > 0) {
				List<Node> childList = new ArrayList<Node>();
				// leftPadding += 20;
				for (int i = 0; i < allNodes.size(); i++) {
					Node itemNode = allNodes.get(i);
					if (itemNode.getParentId() == nodeId) {
						setShowList(itemNode);
						childList.add(itemNode);
					}
				}
				mNode.setChildList(childList);
			}
		}
	}

	/**
	 * 根据数据库初始化部门信息
	 * 
	 */
	private class QueryDeptTask extends AsyncTask<String, Void, List<Client>> {
		@Override
		protected List<Client> doInBackground(String... params) {
			List<部门> listDept = zlServiceHelper.getDepartmentList(context);
			部门 dept = null;
			Node firstNode = null;
			if (listDept != null && listDept.size() > 0) {
				for (int i = 0; i < listDept.size(); i++) {
					dept = listDept.get(i);
					Node itemNode = new Node(dept.get编号(), dept.get名称(),
							dept.get上级());
					if (dept.get编号() == 1) { // 最高节点
						firstNode = itemNode;
						firstNode.setLeftPadding(5);
					}
					allNodes.add(itemNode);
				}
			}
			showNodes.add(firstNode);
			setShowList(firstNode);
			initShowList(firstNode);
			return null;
		}

		@Override
		protected void onPostExecute(List<Client> result) {
			super.onPostExecute(result);
			ExpandAdapter expandAdapter = new ExpandAdapter(context, null,
					showNodes);
			lv.setAdapter(expandAdapter);
		}
	}

}
