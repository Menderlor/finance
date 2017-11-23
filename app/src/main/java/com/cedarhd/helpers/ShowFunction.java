package com.cedarhd.helpers;

import com.cedarhd.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单展示功能
 * 
 * @author bohr
 * 
 */
public class ShowFunction {
	List<Integer> list = new ArrayList<Integer>();
	int function0 = R.layout.tag_menu_item; // 考勤模块

	// TODO
	int function1 = R.layout.tag_menu_item; // 邮件模块
	int function2 = R.layout.worklog_menu_item; // 日志模块
	int function3 = R.layout.task_menu_item; // 任务模块
	int function4 = R.layout.notice_menu_item; // 通知模块
	int function5 = R.layout.client_menu_item; // 客户模块
	int function6 = R.layout.contact_menu_item; // 联系模块
	int function7 = R.layout.salechance_menu_item; // 销售机会模块
	int function8 = R.layout.apply_menu_item; // 申请模块
	int function9 = R.layout.order_menu_item; // 订单模块
	int function10 = R.layout.advertisement_menu_item; // 广告功能模块
	int function11 = R.layout.test_menu_item;// 测试模块
	int function12 = R.layout.communication_menu_item; // 通讯录模块
	int function13 = R.layout.project_menu_item; // 项目模块
	int function14 = R.layout.product_menu_item; // 产品模块
	int function15 = R.layout.apply_in_menu_item; // 入库模块
	int function16 = R.layout.apply_out_menu_item; // 出库模块
	int function17 = R.layout.sale_summary_menu_item; // 漏斗图模块

	public ShowFunction() {
		super();
		list.add(function0);
		list.add(function1);
		list.add(function2);
		list.add(function3);
		list.add(function4);
		list.add(function5);
		list.add(function6);
		list.add(function7);
		list.add(function8);
		list.add(function9);
		list.add(function10);
		list.add(function11);
		list.add(function12);
		list.add(function13);
		list.add(function14);
		list.add(function15);
		list.add(function16);
		list.add(function17);
	}

	/**
	 * 显示需要展示的功能模块
	 * 
	 * @return
	 */
	public List<Integer> getList() {
		// 需要功能模块 编号1, 2, 3
		// 华兰集团特供版
		// int[] funcs = new int[] { 0, 2, 3, 4, 8, 10 };
		// 北京海纳联创无机纤维喷涂技术有限公司
		// 基础模块
		int[] funcs = new int[] { 0, 2, 3, 4, 8, 12, 10 };
		List<Integer> showList = new ArrayList<Integer>();
		for (int i : funcs) {
			int result = getSigalFunction(i);
			if (result != -1) {
				showList.add(result);
			}
		}
		return showList;
	}

	/**
	 * 显示需要展示的功能模块
	 * 
	 * @return
	 */
	public List<Integer> getList(int[] funcs) {
		// 需要功能模块 编号1, 2, 3
		List<Integer> showList = new ArrayList<Integer>();
		for (int i : funcs) {
			int result = getSigalFunction(i);
			if (result != -1) {
				showList.add(result);
			}
		}
		return showList;
	}

	/**
	 * 获得功能块的R.layout
	 * 
	 * @param pos
	 * @return
	 */
	private int getSigalFunction(int pos) {
		if (pos < list.size() && pos >= 0) {
			return list.get(pos);
		} else {
			return -1;
		}
	}
}
