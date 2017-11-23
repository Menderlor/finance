package com.cedarhd.models.changhui;

import java.io.Serializable;

public class 理财产品 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1060817195785294774L;
	public int 编号;
	public boolean 是临时产品;
	public String 产品名称;
	public String 产品简称;
	public String 发行规模;
	public String 债权到期日;
	public String 产品经理;
	public String 产品来源;
	public String 产品发行方;
	public String 产品类型;
	public String 投资领域;
	public String 投资方式;
	public String 券商;
	public String 投顾;
	public String 产品所在地;
	public String 预约方式;
	public int 预约控制;
	public int 小额合同份数;
	public int 总合同份数;
	public int 小额合同阈值;
	public String 额度控制策略;
	// todo:删除数据库字段和字段描述表数据
	// [DbDateTimeField("产品上架时间", 8)]
	// public String 产品上架时间 ;
	public String 成立日期;
	public String 清算日期;
	public int 收益分配周期;
	public String 募集账户;
	public String 账号;
	public String 开户行信息;
	public String 融资公司介绍;
	public String 产品优势;
	public String 还款来源;
	public String 风控措施;
	public String 资金运作方式;
	public String 开放赎回规则;
	public String 清算分配规则;
	public String 收益分配规则;
	public String 备注;
	public String 附件;
	public int 上线状态;
	public double 募集额度;

	// / <summary>
	// / 勾选此项时，客户经理将无法在产品信息中看到募集账户字段中的内容
	// / </summary>
	public boolean 隐藏募集账户;

	public int 项目期限;
	
	public String 可买期限;
    public String 产品期限描述;

	// / <summary>
	// / ZL.Common.Enum.理财产品计息方式
	// / </summary>
	public int 计息方式;

	public boolean 是否清算;
	public boolean Deleted;

	public int 产品类别;
	public int 期次;
	public String 产品状态;
	public double 剩余额度;

	// / <summary>
	// / 认购费比例
	// / </summary>
	public double SubscribeRate;
}