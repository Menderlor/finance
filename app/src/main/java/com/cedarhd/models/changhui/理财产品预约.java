package com.cedarhd.models.changhui;

import java.io.Serializable;

public class 理财产品预约 implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -4035651213134541467L;
    public int 编号;
    public int 制单人;
    public int 工作流;
    public String 制单时间;
    public String 附件;
    public int 理财产品;
    public int 客户;
    public double 预约金额;
    public int 所属分公司;
    public String 预约打款日期;
    public String 备注;
    public String 客户名称;

    // / <summary>
    // / ZL.Common.Enum.金融理财.理财产品预约状态
    // / </summary>
    public int 状态;
    // / <summary>
    // / 按期预约
    // / </summary>
    public int 期次;
    public int 投资期限;
    public String 身份证号;
    public int 客户经理;
    public String 手机;
    public int 证件类别;
    public String 通过时间;

    public String 预期年化收益率分类;

    public int 理财师;
    public String 理财师名称;

    public int 认购类型;
    public String 座机;
    public String 邮箱;
    public String 预约金额大写;
    public String 预约到期日期;
    public double 预期年化收益率;
    public String 客户经理手机;
    public String 客户经理邮箱;
    public int 所属部门;
    public String 预约结果;

    public String 客户编号;
    public int 风险测评类型;
    public String 联系地址;
    public String 邮政编码;
    public String 投资者承诺函;
    public String 投资者风险调查;
    public String 合格投资者资产证明;

    public int 类型;
    public double 追加前金额;
    public String 追加前金额大写;
    public double 本次追加金额;
    public int OriginalReservation;
    public int PreviousReservation;
    public int 理财产品购买合同;
    public boolean 是否已追加;


    /**
     * 预约列表字段  用来判断预约状态
     */
    public boolean 允许追加投资;
    public int 下个步骤编号;
    public String 下个步骤;
    public int 合同状态;
    public String 合同编号;
    public int 合同类型;


}