package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class Client implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -9199154210409081455L;
    @DatabaseField(generatedId = true, unique = true)
    public int _Id;
    @DatabaseField
    public int Id;
    @DatabaseField
    public String CustomerName; // 客户名称
    @DatabaseField
    public String Contacts; // 联系人
    @DatabaseField
    public String RegisterTime; // 登记时间
    @DatabaseField
    public int Salesman;
    @DatabaseField
    public int CorpId; // 单位Id,客户所属单位
    @DatabaseField
    public int UserId;
    @DatabaseField
    public int Classification;
    @DatabaseField
    public String ClassificationName; // 分类名称
    @DatabaseField
    public int Trade; // 行业
    @DatabaseField
    public String TradeName;// 行业名称
    @DatabaseField
    public String SalesmanName; // 业务员姓名
    @DatabaseField
    public String LastContactDate; // 最后联系时间
    @DatabaseField
    public String PlanContactTime; //
    @DatabaseField
    public String Attachment; // 附件
    @DatabaseField
    public int ToContact;
    @DatabaseField
    public String ContactState; // 联系状态

    @DatabaseField
    public int 省;

    @DatabaseField
    public String 省名; // 省名

    @DatabaseField
    public int 市;

    @DatabaseField
    public String 市名; // 市

    @DatabaseField
    public int 县;

    @DatabaseField
    public String 县名;

    @DatabaseField
    private String Phone; // 电话号码
    @DatabaseField
    public String Address; // 地址
    @DatabaseField
    public Date UpdateTime; // 最后更新

    @DatabaseField
    public String Readed;

    @DatabaseField
    public int ParentNode; // 上级

    /**
     * 已读时间
     */
    @DatabaseField
    public String ReadTime;
    // ****************
    // 以下字段为后续添加，文本1,文本2等字段用于动态分配生成的
    @DatabaseField
    public String 生日;

    @DatabaseField
    public int 来源;

    @DatabaseField
    public int 销售经理;

    @DatabaseField
    public double 余额;

    @DatabaseField
    public int 价格组;

    @DatabaseField
    public String 文本1;
    @DatabaseField
    public String 文本2;
    @DatabaseField
    public String 文本3;
    @DatabaseField
    public String 文本4;
    @DatabaseField
    public String 文本5;
    @DatabaseField
    public String 文本6;
    @DatabaseField
    public String 文本7;
    @DatabaseField
    public String 文本8;
    @DatabaseField
    public String 文本9;
    @DatabaseField
    public String 文本10;

    @DatabaseField
    public String 文本11;
    @DatabaseField
    public String 文本12;
    @DatabaseField
    public String 文本13;
    @DatabaseField
    public String 文本14;
    @DatabaseField
    public String 文本15;
    @DatabaseField
    public String 文本16;
    @DatabaseField
    public String 文本17;
    @DatabaseField
    public String 文本18;
    @DatabaseField
    public String 文本19;
    @DatabaseField
    public String 文本20;

    @DatabaseField
    public String 文本21;
    @DatabaseField
    public String 文本22;
    @DatabaseField
    public String 文本23;
    @DatabaseField
    public String 文本24;
    @DatabaseField
    public String 文本25;
    @DatabaseField
    public String 文本26;
    @DatabaseField
    public String 文本27;
    @DatabaseField
    public String 文本28;
    @DatabaseField
    public String 文本29;
    @DatabaseField
    public String 文本30;

    @DatabaseField
    public double 数值1;
    @DatabaseField
    public double 数值2;
    @DatabaseField
    public double 数值3;
    @DatabaseField
    public double 数值4;
    @DatabaseField
    public double 数值5;

    @DatabaseField
    public String 日期1;
    @DatabaseField
    public String 日期2;
    @DatabaseField
    public String 日期3;
    @DatabaseField
    public String 日期4;
    @DatabaseField
    public String 日期5;

    @DatabaseField
    public String 时间1;
    @DatabaseField
    public String 时间2;

    @DatabaseField
    public int 单选1;
    @DatabaseField
    public int 单选2;
    @DatabaseField
    public int 单选3;
    @DatabaseField
    public int 单选4;
    @DatabaseField
    public int 单选5;

    @DatabaseField
    public int 单选6;

    @DatabaseField
    public int 单选7;

    @DatabaseField
    public int 单选8;

    @DatabaseField
    public int 单选9;

    @DatabaseField
    public int 单选10;

    @DatabaseField
    public int 自动完成1;
    @DatabaseField
    public int 自动完成2;
    @DatabaseField
    public int 自动完成3;
    @DatabaseField
    public int 自动完成4;
    @DatabaseField
    public int 自动完成5;

    @DatabaseField
    public String 多选1;
    @DatabaseField
    public String 多选2;
    @DatabaseField
    public String 多选3;
    @DatabaseField
    public String 多选4;
    @DatabaseField
    public String 多选5;

    @DatabaseField
    public int SQL客户关联;

    @DatabaseField
    public int 创建人;

    @DatabaseField
    public String 手机;

    @DatabaseField
    public int 状态;

    @DatabaseField
    public int 客户状态;

    @DatabaseField
    public int 二级来源;

    @DatabaseField
    public int 市场专员;

    @DatabaseField
    public String 关键字;

    @DatabaseField
    public String 微信;

    @DatabaseField
    public String QQ;

    @DatabaseField
    public String 邮箱;

    @DatabaseField
    public String 网址;

    @DatabaseField
    public String 旺旺;

    @DatabaseField
    public String 提示;

    public String 客户编号;

    public String 全国信息代码号;

    public String 风险测评时间;

    public int get_Id() {
        return _Id;
    }

    public void set_Id(int _Id) {
        this._Id = _Id;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getContacts() {
        return Contacts;
    }

    public void setContacts(String contacts) {
        Contacts = contacts;
    }

    public String getRegisterTime() {
        return RegisterTime;
    }

    public void setRegisterTime(String registerTime) {
        RegisterTime = registerTime;
    }

    public int getSalesman() {
        return Salesman;
    }

    public void setSalesman(int salesman) {
        Salesman = salesman;
    }

    public int getCorpId() {
        return CorpId;
    }

    public void setCorpId(int corpId) {
        CorpId = corpId;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public int getClassification() {
        return Classification;
    }

    public void setClassification(int classification) {
        Classification = classification;
    }

    public String getClassificationName() {
        return ClassificationName;
    }

    public void setClassificationName(String classificationName) {
        ClassificationName = classificationName;
    }

    public int getTrade() {
        return Trade;
    }

    public void setTrade(int trade) {
        Trade = trade;
    }

    public String getTradeName() {
        return TradeName;
    }

    public void setTradeName(String tradeName) {
        TradeName = tradeName;
    }

    public String getSalesmanName() {
        return SalesmanName;
    }

    public void setSalesmanName(String salesmanName) {
        SalesmanName = salesmanName;
    }

    public String getLastContactDate() {
        return LastContactDate;
    }

    public void setLastContactDate(String lastContactDate) {
        LastContactDate = lastContactDate;
    }

    public String getPlanContactTime() {
        return PlanContactTime;
    }

    public void setPlanContactTime(String planContactTime) {
        PlanContactTime = planContactTime;
    }

    public String getAttachment() {
        return Attachment;
    }

    public void setAttachment(String attachment) {
        Attachment = attachment;
    }

    public int getToContact() {
        return ToContact;
    }

    public void setToContact(int toContact) {
        ToContact = toContact;
    }

    public String getContactState() {
        return ContactState;
    }

    public void setContactState(String contactState) {
        ContactState = contactState;
    }

    public int getProvince() {
        return 省;
    }

    public void setProvince(int province) {
        省 = province;
    }

    public int getCity() {
        return 市;
    }

    public void setCity(int city) {
        市 = city;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public Date getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(Date updateTime) {
        UpdateTime = updateTime;
    }

    public String getRead() {
        return Readed;
    }

    public void setRead(String read) {
        Readed = read;
    }

    public int getParentNode() {
        return ParentNode;
    }

    public void setParentNode(int parentNode) {
        ParentNode = parentNode;
    }

    public class ClientComparator implements Comparator<Client> {
        @Override
        public int compare(Client lhs, Client rhs) {
            if (lhs.Classification == rhs.ParentNode) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public int get县() {
        return 县;
    }

    public void set县(int 县) {
        this.县 = 县;
    }

    public String get省名() {
        return 省名;
    }

    public void set省名(String 省名) {
        this.省名 = 省名;
    }

    public String get市名() {
        return 市名;
    }

    public void set市名(String 市名) {
        this.市名 = 市名;
    }



}
