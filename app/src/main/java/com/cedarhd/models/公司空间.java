package com.cedarhd.models;

import java.io.Serializable;

public class 公司空间 implements Serializable {
	public String 编号 ;
    public String 标题 ;
    public String 内容 ;
    public int 发帖人 ;
    public String 发帖人姓名 ;
    /// <summary>
    /// 论坛版块
    /// </summary>
    public int 分类 ;
    public String 发帖时间 ;
    public int 回复次数 ;
    public String 更新时间 ;
    public String 附件 ;
    /// <summary>
    /// 0:个人空间，1：部门空间，2：公司空间
    /// </summary>
    public int 类型 ;
}
