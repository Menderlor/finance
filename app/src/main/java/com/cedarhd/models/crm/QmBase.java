package com.cedarhd.models.crm;

public class QmBase {
    /**
     * 只查询当前用户
     */
    public boolean OnlyCurrentUser;

    /**
     * 查询所有用户
     */
    public boolean QueryAllUser;

    /**
     * 页码
     */
    public int Offset;

    /**
     * 偏移量
     */
    public int PageIndex;

    /**
     * 每页数量
     */
    public int PageSize;

    /**
     * 不分页
     */
    public boolean NoPager;

    /**
     * 过滤条件
     */
    public String Filter;

    /**
     * 0:个人空间，1：部门空间，2：公司空间
     */
    public int Type;

    /**
     * 附加过滤条件 ：本地添加
     */
    public String moreFilter = "";

    public String Keyword = "";

    @Override
    public String toString() {
        return "QmBase [OnlyCurrentUser=" + OnlyCurrentUser + ", QueryAllUser="
                + QueryAllUser + ", Offset=" + Offset + ", PageIndex="
                + PageIndex + ", PageSize=" + PageSize + ", NoPager=" + NoPager
                + ", Filter=" + Filter + ", Type=" + Type + ", moreFilter="
                + moreFilter + "]";
    }

}