package com.cedarhd.models.changhui;

import java.io.Serializable;

public class 理财产品购买合同 implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2279231916110875945L;
    public int 编号;
    public int 制单人;
    public int 工作流;
    public String 制单时间;
    public String 附件;
    public int 客户;
    public String 营业执照注册号或身份证号;
    public String 联系电话;
    public String 通讯地址;
    public double 认购金额小写;
    public String 认购金额大写;
    public double 认购费用小写;
    public String 认购费用大写;
    public int 理财产品;
    public double 预期年化收益率;
    public int 认购规模;
    public String 户名;
    public String 银行;
    public String 分行;
    public String 支行;
    public String 账号;
    public String 付款账户名;
    public String 付款账户开户行;
    public String 付款账号;

    // / <summary>
    // / 银行行号，不是卡号
    // / </summary>
    public String 银行卡号;

    // / <summary>
    // / dict 客户_客户性质
    // / </summary>
    public int 认购人类型;

    // / <summary>
    // / zlcloud.理财产品预约.编号
    // / </summary>
    public int 理财产品预约;

    // / <summary>
    // / 同预约，如有本合同有预约,从预约中复制期次的值
    // / zlcloud.理财产品期次.期次,不是编号
    // / </summary>
    public int 期次;

    // / <summary>
    // / 预期年化收益率.编号
    // / </summary>
    public int 投资期限;

    public String 打款时间;

    public String ReferNo;

    // / <summary>
    // / ZL.Common.Enum.理财产品购买合同状态
    // / </summary>
    public int 状态;

    // / <summary>
    // / "支付方式"表或ZL.Common.Enum.理财产品购买合同支付方式
    // / </summary>
    public int 支付方式;

    // / <summary>
    // / 营销部确认到账的时间，不是王府井接口中的到账时间
    // / </summary>
    public String 到账时间;

    public String 合同编号;
    public int 客户经理;
    public int 所属分公司;
    public int 部门经理;
    public int 副总经理;
    public int 分公司总经理;

    public int 理财师;

    public String 身份证正反面复印件扫描件;
    public String 银行卡正反面复印件扫描件;
    public String 合同首页扫描件;
    public String 合同签署页扫描件;
    public String 合同受让方信息页扫描件;
    public String 付款凭证扫描件;
    public String 合格投资者资产证明;

    // / <summary>
    // / 根据产品类别的业务考核折算比率而来，默认是1
    // / </summary>
    public double 业绩考核折算比率;
    // / <summary>
    // / 上一份理财产品购买合同编号
    // / 如果大于0，表示此合同是续投或转投
    // / </summary>
    public int OriginalContract;

    // / <summary>
    // / 在合同审核通过后跟清算信息一起生成
    // / </summary>
    public String ExpireDate;

    // / <summary>
    // / Enum 理财产品购买合同Type
    // / </summary>
    public int Type;
    public String 分账日;
    public int 证件类别;

    // / <summary>
    // / 转投申请通过后保存申请书编号进来
    // / </summary>
    public int 转投申请书;

    // / <summary>
    // / 续投申请通过后保存申请书编号进来
    // / </summary>
    public int 续投申请书;

    // / <summary>
    // / 目前只记录提前赎回时间
    // / </summary>
    public String 赎回时间;

    public boolean IsCleared;
    public boolean 是否导入;

    public String 俱乐部入会申请表;
    public String 投资风险测评卡;

    public String 预期年化收益率分类;

    public boolean 预期年化收益率浮动;

    public String 座机;
    public String 邮箱;
    public String 客户经理手机;
    public String 客户经理邮箱;
    public String 合同收回日期;
    public String 投资起算日;
    public String 投资到期日;
    public String 投资起息日;
    public int 所属部门;

    public int 认购类型;

    public String 客户编号;
    public String 邮政编码;
    public String 投资者承诺函;
    public String 投资者风险调查;
    public int 风险测评类型;
    public String 风险测评类型名称;

    @Override
    public String toString() {
        return "理财产品购买合同 [编号=" + 编号 + ", 制单人=" + 制单人 + ", 工作流=" + 工作流
                + ", 制单时间=" + 制单时间 + ", 附件=" + 附件 + ", 客户=" + 客户
                + ", 营业执照注册号或身份证号=" + 营业执照注册号或身份证号 + ", 联系电话=" + 联系电话
                + ", 通讯地址=" + 通讯地址 + ", 认购金额小写=" + 认购金额小写 + ", 认购金额大写="
                + 认购金额大写 + ", 认购费用小写=" + 认购费用小写 + ", 认购费用大写=" + 认购费用大写
                + ", 理财产品=" + 理财产品 + ", 预期年化收益率=" + 预期年化收益率 + ", 认购规模=" + 认购规模
                + ", 户名=" + 户名 + ", 银行=" + 银行 + ", 分行=" + 分行 + ", 支行=" + 支行
                + ", 账号=" + 账号 + ", 银行卡号=" + 银行卡号 + ", 认购人类型=" + 认购人类型
                + ", 理财产品预约=" + 理财产品预约 + ", 期次=" + 期次 + ", 投资期限=" + 投资期限
                + ", 打款时间=" + 打款时间 + ", ReferNo=" + ReferNo + ", 状态=" + 状态
                + ", 支付方式=" + 支付方式 + ", 到账时间=" + 到账时间 + ", 合同编号=" + 合同编号
                + ", 客户经理=" + 客户经理 + ", 所属分公司=" + 所属分公司 + ", 部门经理=" + 部门经理
                + ", 副总经理=" + 副总经理 + ", 分公司总经理=" + 分公司总经理 + ", 理财师=" + 理财师
                + ", 身份证正反面复印件扫描件=" + 身份证正反面复印件扫描件 + ", 银行卡正反面复印件扫描件="
                + 银行卡正反面复印件扫描件 + ", 合同首页扫描件=" + 合同首页扫描件 + ", 合同签署页扫描件="
                + 合同签署页扫描件 + ", 合同受让方信息页扫描件=" + 合同受让方信息页扫描件 + ", 付款凭证扫描件="
                + 付款凭证扫描件 + ", 合格投资者资产证明=" + 合格投资者资产证明 + ", 业绩考核折算比率="
                + 业绩考核折算比率 + ", OriginalContract=" + OriginalContract
                + ", ExpireDate=" + ExpireDate + ", Type=" + Type + ", 分账日="
                + 分账日 + ", 证件类别=" + 证件类别 + ", 转投申请书=" + 转投申请书 + ", 续投申请书="
                + 续投申请书 + ", 赎回时间=" + 赎回时间 + ", IsCleared=" + IsCleared
                + ", 是否导入=" + 是否导入 + ", 俱乐部入会申请表=" + 俱乐部入会申请表 + ", 投资风险测评卡="
                + 投资风险测评卡 + ", 预期年化收益率分类=" + 预期年化收益率分类 + ", 预期年化收益率浮动="
                + 预期年化收益率浮动 + ", 座机=" + 座机 + ", 邮箱=" + 邮箱 + ", 客户经理手机="
                + 客户经理手机 + ", 客户经理邮箱=" + 客户经理邮箱 + ", 合同收回日期=" + 合同收回日期
                + ", 投资起算日=" + 投资起算日 + ", 投资到期日=" + 投资到期日 + ", 投资起息日=" + 投资起息日
                + ", 所属部门=" + 所属部门 + ", 认购类型=" + 认购类型 + "]";
    }


}