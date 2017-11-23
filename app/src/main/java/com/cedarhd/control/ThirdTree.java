package com.cedarhd.control;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;

import java.util.Vector;

/**
 * 
 * @author xuzl@stelect.softserv.com.cn 252386922@qq.com
 * 
 */
public class ThirdTree extends Activity {

	private Vector vector = new Vector();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		InitData();// 初始化数据

		LinearLayout layout = new LinearLayout(this); // 线性布局方式
		layout.setOrientation(LinearLayout.VERTICAL); // 控件对其方式为垂直排列
		Vector tmpVector = GetChildsTreeNode(0);
		createTree(tmpVector, layout, 1);
		setContentView(layout);
	}

	private void createTree(Vector tmpVector, LinearLayout layout, int lay) {
		if (tmpVector.size() > 0) {
			for (int i = 0; i < tmpVector.size(); i++) {
				LinearLayout layouttmp = new LinearLayout(this);
				layouttmp.setId(lay);// 保存层
				TreeNode item = (TreeNode) tmpVector.elementAt(i);

				Vector tmpVector22 = GetChildsTreeNode(Integer.parseInt(item
						.getId()));
				ImageView btn = new ImageView(this);
				btn.setPadding(lay * 20, 4, 0, 0);
				btn.setId(Integer.parseInt(item.getId()));

				// ---------效果3----------
				if (tmpVector22.size() > 0) {
					if (!item.Getisextends()) {
						btn.setImageResource(R.drawable.jia);
					} else {
						btn.setImageResource(R.drawable.jian);
					}
				}
				// -------------------------
				btn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						TreeNode item = getByID(v.getId());

						if (item.Getisextends())// 如果当前已经展开下级，递归收缩下级，并改变当前的isextends
						{
							// 递归
							LinearLayout cur = (LinearLayout) v.getParent();
							LinearLayout z = (LinearLayout) cur.getParent();
							int Layoutindex = z.indexOfChild(cur);

							int childsize = dgss(v.getId(), Layoutindex, z);
							if (childsize > 0) {
								((ImageView) v)
										.setImageResource(R.drawable.jia);

								// 收缩
								item.Setisextends(false);
								int index = vector.indexOf(item);
								vector.setElementAt(item, index);
							}
						} else// 如果当前没有展开下级，递归展开下级，并改变当前的isextends
						{
							// 递归
							LinearLayout cur = (LinearLayout) v.getParent();
							LinearLayout z = (LinearLayout) cur.getParent();
							int Layoutindex = z.indexOfChild(cur);

							int childsize = dgzk(v.getId(), Layoutindex, z,
									cur.getId() + 1);
							if (childsize > 0) {
								((ImageView) v)
										.setImageResource(R.drawable.jian);

								// 展开
								item.Setisextends(true);
								int index = vector.indexOf(item);
								vector.setElementAt(item, index);
							}

						}
					}
				});

				layouttmp.addView(btn, new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));

				TextView tv = new TextView(this);
				tv.setId(Integer.parseInt(item.getId()));
				tv.setText(item.getName());
				tv.setTextColor(Color.rgb(255, 0, 0));
				tv.setPadding(0, 0, 0, 0);
				tv.setClickable(true);
				tv.setFocusable(true);
				if (tmpVector22.size() == 0) {
					tv.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							Toast.makeText(ThirdTree.this,
									String.valueOf(v.getId()),
									Toast.LENGTH_LONG).show();
						}
					});
				}
				tv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					public void onFocusChange(View v, boolean isfocus) {
						TextView tv = (TextView) v;
						if (isfocus) {
							tv.setTextColor(Color.rgb(0, 0, 255));
						} else {
							tv.setTextColor(Color.rgb(255, 255, 255));
						}
					}
				});
				layouttmp.addView(tv);
				layout.addView(layouttmp);
				if (item.Getisextends()) {
					createTree(tmpVector22, layout, lay + 1);
				}
			}
		}
	}

	private int dgzk(int itemid, int Layoutindex, LinearLayout z, int lay)// 递归展开
	{
		Vector aa = GetChildsTreeNode(itemid);
		int tmpid = 0;
		for (int i = 0; i < aa.size(); i++) {
			TreeNode item = (TreeNode) aa.elementAt(i);
			LinearLayout layouttmp = new LinearLayout(this);
			layouttmp.setId(lay);
			ImageView btn = new ImageView(this);
			btn.setPadding(lay * 20, 4, 0, 0);
			btn.setId(Integer.parseInt(item.getId()));
			Vector tmpVector22 = GetChildsTreeNode(Integer.parseInt(item
					.getId()));

			if (tmpVector22.size() > 0) {
				if (!item.Getisextends()) {
					btn.setImageResource(R.drawable.jia);
				} else {
					btn.setImageResource(R.drawable.jian);
				}
			}

			btn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					TreeNode item = getByID(v.getId());

					if (item.Getisextends())// 如果当前已经展开下级，递归收缩下级，并改变当前的isextends
					{
						// 递归
						LinearLayout cur = (LinearLayout) v.getParent();
						LinearLayout z = (LinearLayout) cur.getParent();
						int Layoutindex = z.indexOfChild(cur);

						int childsize = dgss(v.getId(), Layoutindex, z);
						if (childsize > 0) {
							((ImageView) v).setImageResource(R.drawable.jia);

							// 收缩
							item.Setisextends(false);
							int index = vector.indexOf(item);
							vector.setElementAt(item, index);
						}
					} else// 如果当前没有展开下级，递归展开下级，并改变当前的isextends
					{
						// 递归
						LinearLayout cur = (LinearLayout) v.getParent();
						LinearLayout z = (LinearLayout) cur.getParent();
						int Layoutindex = z.indexOfChild(cur);

						int childsize = dgzk(v.getId(), Layoutindex, z,
								cur.getId() + 1);
						if (childsize > 0) {
							((ImageView) v).setImageResource(R.drawable.jian);

							// 展开
							item.Setisextends(true);
							int index = vector.indexOf(item);
							vector.setElementAt(item, index);
						}
					}
				}
			});

			layouttmp.addView(btn, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));

			TextView tv = new TextView(this);
			tv.setId(Integer.parseInt(item.getId()));
			tv.setText(item.getName());
			tv.setTextColor(Color.rgb(255, 0, 0));
			tv.setPadding(0, 0, 0, 0);
			tv.setClickable(true);
			tv.setFocusable(true);
			tv.setClickable(true);
			tv.setFocusable(true);
			if (tmpVector22.size() == 0) {
				tv.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Toast.makeText(ThirdTree.this,
								String.valueOf(v.getId()), Toast.LENGTH_LONG)
								.show();
					}
				});
			}
			tv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				public void onFocusChange(View v, boolean isfocus) {
					TextView tv = (TextView) v;
					if (isfocus) {
						tv.setTextColor(Color.rgb(0, 0, 255));
					} else {
						tv.setTextColor(Color.rgb(255, 255, 255));
					}
				}
			});
			layouttmp.addView(tv);

			z.addView(layouttmp, Layoutindex + 1 + i + tmpid);
			if (item.Getisextends()) {
				tmpid = dgzk(Integer.parseInt(item.getId()), Layoutindex + 1
						+ i, z, lay + 1);
			}
		}
		return aa.size();
	}

	private int dgss(int itemid, int Layoutindex, LinearLayout z)// 递归收缩
	{
		Vector aa = GetChildsTreeNode(itemid);
		for (int i = 0; i < aa.size(); i++) {
			TreeNode item = (TreeNode) aa.elementAt(i);
			z.removeViewAt(Layoutindex + 1);
			if (item.Getisextends()) {
				dgss(Integer.parseInt(item.getId()), Layoutindex, z);
			}
		}
		return aa.size();
	}

	private TreeNode getByID(int id) {
		for (int i = 0; i < vector.size(); i++) {
			TreeNode item = (TreeNode) vector.elementAt(i);
			if (Integer.parseInt(item.getId()) == id)
				return item;
		}
		return null;
	}

	/**
	 * 初始化数据
	 */
	private void InitData() {
		TreeNode node = new TreeNode();
		node.setId("1");
		node.setName("01接点");
		node.Setisextends(true);
		node.setParent("0");
		vector.addElement(node);

		TreeNode node2 = new TreeNode("2", "0101接点", "1", true);
		vector.addElement(node2);

		TreeNode node3 = new TreeNode("3", "0102接点", "1", false);
		vector.addElement(node3);

		TreeNode node4 = new TreeNode("4", "010201接点", "3", true);
		vector.addElement(node4);

		TreeNode node5 = new TreeNode("5", "010202接点", "3", false);
		vector.addElement(node5);

		TreeNode node6 = new TreeNode("6", "01020201接点", "5", true);
		vector.addElement(node6);
	}

	/**
	 * 选择子接点
	 * 
	 * @param Pid
	 * @return
	 */
	private Vector GetChildsTreeNode(int Pid) {
		Vector tmpVector = new Vector();
		for (int i = 0; i < vector.size(); i++) {
			TreeNode item = (TreeNode) vector.elementAt(i);
			if (Integer.parseInt(item.getParent()) == Pid) {
				tmpVector.add(item);
			}
		}
		return tmpVector;
	}
}
