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
 * 勾选树形效果，遍历vector可以获取哪些Item被勾选
 * 
 * @author xuzl@stelect.softserv.com.cn 252386922@qq.com
 * 
 */
public class CheckTree extends Activity {

	private Vector vector = new Vector();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		InitData();// 初始化树节点数据

		LinearLayout layout = new LinearLayout(this); // 线性布局方式
		layout.setOrientation(LinearLayout.VERTICAL); // 控件对其方式为垂直排列
		Vector tmpVector = GetChildsTreeNode(0);
		createTree(tmpVector, layout, 1);// 递归建立树
		setContentView(layout);
	}

	private void createTree(Vector tmpVector, LinearLayout layout, int lay) {
		if (tmpVector.size() > 0) {
			for (int i = 0; i < tmpVector.size(); i++) {
				// -------------相关数据--------------
				TreeNode item = (TreeNode) tmpVector.elementAt(i);
				Vector tmpVector22 = GetChildsTreeNode(Integer.parseInt(item
						.getId()));
				// --------------层信息------------------
				LinearLayout layouttmp = new LinearLayout(this);
				layouttmp.setId(lay);// 保存所在的层级别 id=level
				// --------------导航接点-----------------
				TextView tvnavagate = new TextView(this);
				tvnavagate.setId(Integer.parseInt(item.getId()));
				tvnavagate.setTextColor(Color.rgb(255, 0, 0));
				tvnavagate.setClickable(true);
				tvnavagate.setFocusable(true);
				if (!item.Getisextends()) {
					if (tmpVector22.size() > 0) {
						tvnavagate.setText("+");
					} else {
						tvnavagate.setText("-");
					}
				} else {
					tvnavagate.setText("-");
				}
				tvnavagate.setPadding(lay * 20, 0, 0, 0);
				tvnavagate.setOnClickListener(new View.OnClickListener() {
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
								((TextView) v).setText("+");

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
								((TextView) v).setText("-");

								// 展开
								item.Setisextends(true);
								int index = vector.indexOf(item);
								vector.setElementAt(item, index);
							}
						}
					}
				});
				layouttmp.addView(tvnavagate);
				// --------------check图片----------------
				ImageView btn = new ImageView(this);
				btn.setId(Integer.parseInt(item.getId()));
				btn.setPadding(2, 4, 0, 0);
				if (item.GetIsChecked()) {
					btn.setImageResource(R.drawable.checkbox_on);
				} else {
					btn.setImageResource(R.drawable.checkbox_off);
				}

				btn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						TreeNode item = getByID(v.getId());
						if (item.GetIsChecked()) {
							item.SetIsChecked(false);
							((ImageView) v)
									.setImageResource(R.drawable.checkbox_off);
						} else {
							item.SetIsChecked(true);
							((ImageView) v)
									.setImageResource(R.drawable.checkbox_on);
						}
						int index = vector.indexOf(item);
						vector.setElementAt(item, index);
					}
				});

				layouttmp.addView(btn, new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
				// --------------显示文本---------------
				TextView tv = new TextView(this);
				tv.setId(Integer.parseInt(item.getId()));
				tv.setText(item.getName());
				tv.setTextColor(Color.rgb(255, 0, 0));
				tv.setPadding(5, 0, 0, 0);
				tv.setClickable(true);
				tv.setFocusable(true);
				if (tmpVector22.size() == 0) {
					tv.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							Toast.makeText(CheckTree.this,
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
				// -----------------
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
			// -------------相关数据--------------
			TreeNode item = (TreeNode) aa.elementAt(i);
			Vector tmpVector22 = GetChildsTreeNode(Integer.parseInt(item
					.getId()));
			// --------------层信息------------------
			LinearLayout layouttmp = new LinearLayout(this);
			layouttmp.setId(lay);
			// --------------导航接点-----------------
			TextView tvnavagate = new TextView(this);
			tvnavagate.setId(Integer.parseInt(item.getId()));
			tvnavagate.setTextColor(Color.rgb(255, 0, 0));
			tvnavagate.setClickable(true);
			tvnavagate.setFocusable(true);
			if (!item.Getisextends()) {
				if (tmpVector22.size() > 0) {
					tvnavagate.setText("+");
				} else {
					tvnavagate.setText("-");
				}
			} else {
				tvnavagate.setText("-");
			}
			tvnavagate.setPadding(lay * 20, 0, 0, 0);
			tvnavagate.setOnClickListener(new View.OnClickListener() {
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
							((TextView) v).setText("+");

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
							((TextView) v).setText("-");

							// 展开
							item.Setisextends(true);
							int index = vector.indexOf(item);
							vector.setElementAt(item, index);
						}
					}
				}
			});
			layouttmp.addView(tvnavagate);
			// --------------check图片----------------
			ImageView btn = new ImageView(this);
			btn.setId(Integer.parseInt(item.getId()));
			btn.setPadding(2, 4, 0, 0);
			if (item.GetIsChecked()) {
				btn.setImageResource(R.drawable.checkbox_on);
			} else {
				btn.setImageResource(R.drawable.checkbox_off);
			}

			btn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					TreeNode item = getByID(v.getId());
					if (item.GetIsChecked()) {
						item.SetIsChecked(false);
						((ImageView) v)
								.setImageResource(R.drawable.checkbox_off);
					} else {
						item.SetIsChecked(true);
						((ImageView) v)
								.setImageResource(R.drawable.checkbox_on);
					}
					int index = vector.indexOf(item);
					vector.setElementAt(item, index);
				}
			});
			layouttmp.addView(btn, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			// --------------显示文本---------------
			TextView tv = new TextView(this);
			tv.setId(Integer.parseInt(item.getId()));
			tv.setText(item.getName());
			tv.setTextColor(Color.rgb(255, 0, 0));
			tv.setPadding(5, 0, 0, 0);
			tv.setClickable(true);
			tv.setFocusable(true);
			tv.setClickable(true);
			tv.setFocusable(true);
			if (tmpVector22.size() == 0) {
				tv.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Toast.makeText(CheckTree.this,
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
			// --------------------------------------
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
		node.SetIsChecked(false);
		vector.addElement(node);

		TreeNode node2 = new TreeNode("2", "0101接点", "1", true, false);
		vector.addElement(node2);

		TreeNode node3 = new TreeNode("3", "0102接点", "1", false, false);
		vector.addElement(node3);

		TreeNode node4 = new TreeNode("4", "010201接点", "3", true, true);
		vector.addElement(node4);

		TreeNode node5 = new TreeNode("5", "010202接点", "3", false, true);
		vector.addElement(node5);

		TreeNode node6 = new TreeNode("6", "01020201接点", "5", true, true);
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
