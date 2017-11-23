package com.cedarhd.biz;

import com.cedarhd.R;
import com.cedarhd.constants.enums.EnumFunctionPoint;
import com.cedarhd.models.MenuChildItem;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 */
public class MenuFunctionBiz {
    private final EnumFunctionPoint[] defultPoints = new EnumFunctionPoint[]{
//			EnumFunctionPoint.LOG, EnumFunctionPoint.TASK,
//			EnumFunctionPoint.NOTICE, EnumFunctionPoint.APPLY
    };

    private ArrayList<MenuChildItem> mGridItems;

    public MenuFunctionBiz() {
        super();
        mGridItems = new ArrayList<MenuChildItem>();

        // 雪松要求去除此处4个按钮
        // mGridItems.add(new MenuChildItem(R.drawable.a_icon4, "考勤",
        // EnumFunctionPoint.ATTANCE));
        // mGridItems.add(new MenuChildItem(R.drawable.a_icon3, "日志",
        // EnumFunctionPoint.LOG));
        // mGridItems.add(new MenuChildItem(R.drawable.a_icon7, "任务",
        // EnumFunctionPoint.TASK));
        // mGridItems.add(new MenuChildItem(R.drawable.a_icon8, "申请",
        // EnumFunctionPoint.APPLY));

        mGridItems.add(new MenuChildItem(R.drawable.a_icon1, "通知",
                EnumFunctionPoint.NOTICE));
        mGridItems.add(new MenuChildItem(R.drawable.a_icon5, "客户",
                EnumFunctionPoint.CLIENT));

        /** CRM模块 */
        mGridItems.add(new MenuChildItem(R.drawable.ico_loudou, "漏斗图",
                EnumFunctionPoint.SALESUMARY));
        mGridItems.add(new MenuChildItem(R.drawable.ico_paihang, "排行榜",
                EnumFunctionPoint.RANKING));
        mGridItems.add(new MenuChildItem(R.drawable.ico_xiansuo, "线索",
                EnumFunctionPoint.CLEW));
        mGridItems.add(new MenuChildItem(R.drawable.ico_hetong, "合同",
                EnumFunctionPoint.CONPACT));
        mGridItems.add(new MenuChildItem(R.drawable.ico_shoukuan, "收款单",
                EnumFunctionPoint.RECEIPET));
        mGridItems.add(new MenuChildItem(R.drawable.ico_baoxiao, "报销单",
                EnumFunctionPoint.EXPENSE));

        mGridItems.add(new MenuChildItem(R.drawable.a_icon9, "销售机会",
                EnumFunctionPoint.SALECHANCE));
        mGridItems.add(new MenuChildItem(R.drawable.a_icon6, "联系记录",
                EnumFunctionPoint.CONTACTS));
        mGridItems.add(new MenuChildItem(R.drawable.icon_companyspace, "公司空间",
                EnumFunctionPoint.COMPANY_SPACE));
        mGridItems.add(new MenuChildItem(R.drawable.icon_calculator, "收益计算器",
                EnumFunctionPoint.PROFIT_CALCULATOR));
        mGridItems.add(new MenuChildItem(R.drawable.a_icon5, "客户",
                EnumFunctionPoint.CHANGHUI_CLIENT_LIST));
        mGridItems.add(new MenuChildItem(R.drawable.a_icon11, "项目",
                EnumFunctionPoint.PROJECT));
        mGridItems.add(new MenuChildItem(R.drawable.contacts_icon_menu_item,
                "通讯录", EnumFunctionPoint.COMMUNICATION));
        mGridItems.add(new MenuChildItem(R.drawable.ico_product, "产品",
                EnumFunctionPoint.PRODUCT));
        mGridItems.add(new MenuChildItem(R.drawable.a_icon14, "入库",
                EnumFunctionPoint.APPLY_INBOX));
        mGridItems.add(new MenuChildItem(R.drawable.a_icon15, "出库",
                EnumFunctionPoint.APPLY_OUTBOX));

        mGridItems.add(new MenuChildItem(R.drawable.a_icon9, "橱柜订单",
                EnumFunctionPoint.ORDER));

        /** 订单模块 */
        mGridItems.add(new MenuChildItem(R.drawable.ico_product, "产品",
                EnumFunctionPoint.RAD_PRODUCTLIST));
        mGridItems.add(new MenuChildItem(R.drawable.ico_rad_order, "订单",
                EnumFunctionPoint.RAD_ORDER));
        mGridItems.add(new MenuChildItem(R.drawable.ico_xiansuo, "订单审批",
                EnumFunctionPoint.SLT_APPROVE_ORDER));
        mGridItems.add(new MenuChildItem(R.drawable.ico_rad_caculate, "预算",
                EnumFunctionPoint.RAD_CACULATE));
        mGridItems.add(new MenuChildItem(R.drawable.ico_product, "购物车",
                EnumFunctionPoint.SLT_SHOPCAR_LIST));
        mGridItems.add(new MenuChildItem(R.drawable.ico_rad_code, "扫码到货",
                EnumFunctionPoint.RAD_SCAN_CODE));
        mGridItems.add(new MenuChildItem(R.drawable.ico_upload, "上报",
                EnumFunctionPoint.RAD_REPORT));
        mGridItems.add(new MenuChildItem(R.drawable.ico_xiaoshoumubiao, "销售目标",
                EnumFunctionPoint.SLT_SALE_TARGET));

        mGridItems.add(new MenuChildItem(R.drawable.ico_work_plan, "工作计划",
                EnumFunctionPoint.CHANGHUI_WORK_PLAN));
        mGridItems.add(new MenuChildItem(R.drawable.ico_product, "产品",
                EnumFunctionPoint.CHANGHUI_PRODUCT_LIST));
        mGridItems.add(new MenuChildItem(R.drawable.a_icon_yuyue, "预约",
                EnumFunctionPoint.CHANGHUI_BESPOKE_LIST));
        mGridItems.add(new MenuChildItem(R.drawable.a_icon_hetong, "合同",
                EnumFunctionPoint.CHANGHUI_CONTACT_LIST));

        // mGridItems.add(new MenuChildItem(R.drawable.a_icon_more, "更多功能",
        // EnumFunctionPoint.ADVERTISEMENT));
    }

    /***/
    public ArrayList<MenuChildItem> getFunctions(List<EnumFunctionPoint> points) {
        ArrayList<MenuChildItem> menuChildItems = new ArrayList<MenuChildItem>();
        for (MenuChildItem item : mGridItems) {
            for (EnumFunctionPoint point : points) {
                if (item.ponit.getValue() == point.getValue()) {
                    menuChildItems.add(item);
                    LogUtils.i("menuchild", item.title + "--");
                    break;
                }
            }
        }

        return menuChildItems;
    }

    /**
     * 获得默认显示模块
     */
    public List<EnumFunctionPoint> getDefaultFunctions() {
        List<EnumFunctionPoint> list = new ArrayList<EnumFunctionPoint>();
        for (EnumFunctionPoint point : defultPoints) {
            list.add(point);
        }
        return list;
    }

}
