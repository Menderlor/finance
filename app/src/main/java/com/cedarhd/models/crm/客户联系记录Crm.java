package com.cedarhd.models.crm;

public class 客户联系记录Crm {
	// TextField("业务员", 40)]
	public int 业务员;

	// TextField("客户", 40)]
	public int 客户;

	// TextField("销售机会", 40)]
	public int 销售机会;

	// TextField("合同", 40)]
	public int 合同;

	// TextField("项目", 40)]
	public int 项目;

	// StringField("时间", 8)]
	public String 时间;

	// TextField("联系状态", 40)]
	public int 联系状态;

	// TextField("联系人", 80)]
	public String 联系人;

	// TextField("内容", 2000)]
	public String 内容;
	// TextField("客户反馈", 2000)]
	public String 客户反馈;
	// TextField("联系形式", 40)]
	public int 联系形式;
	// TextField("意向程度", 40)]
	public int 意向程度;
	// TextField("备注", 80)]
	public String 备注;

	// StringField("最后处理时间", 8)]
	public String 最后处理时间;

	// TextField("制单人", 40)]
	public int 制单人;

	// StringField("制单时间", 8)]
	public String 制单时间;

	// TextField("附件", 200)]
	public String 附件;

	// 联系状态比率(乘以100后)
	public double ContactStatusRate;

	public int 赞数量;

	public int 钻石数;

	public boolean 已读;

	public int 评论数;

	public double 经度;

	public double 纬度;

	public String 地址;
}