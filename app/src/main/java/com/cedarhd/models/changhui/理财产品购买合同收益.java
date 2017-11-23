package com.cedarhd.models.changhui;

import java.io.Serializable;
import java.util.List;

public class 理财产品购买合同收益 implements Serializable {
	public int 理财产品购买合同 ;
    public int 理财产品 ;
    public String 理财产品名称 ;
    public double 投资金额 ;
    public int 投资期限 ;
    public String 打款日期 ;
    public String 收益起息日 ;
    public String 投资期开始 ;
    public String 投资期结束 ;
    public double 收益率 ;
    public String 收益率分类;
    public List<理财产品购买合同收益明细> 收益明细 ;
}
