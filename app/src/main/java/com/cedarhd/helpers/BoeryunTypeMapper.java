package com.cedarhd.helpers;

import com.cedarhd.models.Client;
import com.cedarhd.models.客户;
import com.cedarhd.models.日志评论;
import com.cedarhd.models.评论;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据类型映射类转换，处理相同实体中英文属性的的相互转换
 * 
 * @author Administrator
 * 
 */
public class BoeryunTypeMapper {
	public static 客户 MapperTo客户(Client oldClient) {
		客户 client = new 客户();
		client.编号 = oldClient.getId();
		client.名称 = oldClient.CustomerName;
		client.业务员 = oldClient.getSalesman();
		client.联系人 = oldClient.getContacts();
		client.分类 = oldClient.getClassification();
		client.最后更新 = oldClient.getUpdateTime();
		client.最后联系时间 = oldClient.getLastContactDate();
		client.电话 = oldClient.getPhone();
		client.地址 = oldClient.getAddress();
		client.登记时间 = oldClient.getRegisterTime();
		client.省 = oldClient.省;
		client.省名 = oldClient.省名;
		client.市 = oldClient.市;
		client.市名 = oldClient.市名;
		client.县 = oldClient.县;
		client.县名 = oldClient.县名;
		client.状态 = oldClient.状态;
		client.客户状态 = oldClient.客户状态;
		client.行业 = oldClient.getTrade();
		client.附件 = oldClient.getAttachment();
		client.销售经理 = oldClient.销售经理;
		client.生日 = oldClient.生日;
		client.二级来源 = oldClient.二级来源;
		client.价格组 = oldClient.价格组;
		client.余额 = oldClient.余额;
		client.创建人 = oldClient.创建人;
		client.单选1 = oldClient.单选1;
		client.单选2 = oldClient.单选2;
		client.单选3 = oldClient.单选3;
		client.单选4 = oldClient.单选4;
		client.单选5 = oldClient.单选5;
		client.单选6 = oldClient.单选6;
		client.单选7 = oldClient.单选7;
		client.单选8 = oldClient.单选8;
		client.单选9 = oldClient.单选9;
		client.单选10 = oldClient.单选10;
		client.多选1 = oldClient.多选1;
		client.多选2 = oldClient.多选2;
		client.多选3 = oldClient.多选3;
		client.多选4 = oldClient.多选4;
		client.多选5 = oldClient.多选5;
		client.市场专员 = oldClient.市场专员;
		client.关键字 = oldClient.关键字;
		client.手机 = oldClient.手机;
		client.数值1 = oldClient.数值1;
		client.数值2 = oldClient.数值2;
		client.数值1 = oldClient.数值1;
		client.数值3 = oldClient.数值3;
		client.数值4 = oldClient.数值4;
		client.数值5 = oldClient.数值5;
		client.文本1 = oldClient.文本1;
		client.文本2 = oldClient.文本2;
		client.文本3 = oldClient.文本3;
		client.文本4 = oldClient.文本4;
		client.文本5 = oldClient.文本5;
		client.文本6 = oldClient.文本6;
		client.文本7 = oldClient.文本7;
		client.文本8 = oldClient.文本8;
		client.文本9 = oldClient.文本9;
		client.文本10 = oldClient.文本10;
		client.文本11 = oldClient.文本11;
		client.文本12 = oldClient.文本12;
		client.文本13 = oldClient.文本13;
		client.文本14 = oldClient.文本14;
		client.文本15 = oldClient.文本15;
		client.文本16 = oldClient.文本16;
		client.文本17 = oldClient.文本17;
		client.文本18 = oldClient.文本18;
		client.文本19 = oldClient.文本19;
		client.文本20 = oldClient.文本20;
		client.文本21 = oldClient.文本21;
		client.文本22 = oldClient.文本22;
		client.文本23 = oldClient.文本23;
		client.文本24 = oldClient.文本24;
		client.文本25 = oldClient.文本25;
		client.文本26 = oldClient.文本26;
		client.文本27 = oldClient.文本27;
		client.文本28 = oldClient.文本28;
		client.文本29 = oldClient.文本29;
		client.文本30 = oldClient.文本30;
		client.日期1 = oldClient.日期1;
		client.日期2 = oldClient.日期2;
		client.日期3 = oldClient.日期3;
		client.日期4 = oldClient.日期4;
		client.日期5 = oldClient.日期5;
		client.时间1 = oldClient.时间1;
		client.时间2 = oldClient.时间2;
		client.自动完成1 = oldClient.自动完成1;
		client.自动完成2 = oldClient.自动完成2;
		client.自动完成3 = oldClient.自动完成3;
		client.自动完成3 = oldClient.自动完成3;

		client.微信 = oldClient.微信;
		client.QQ = oldClient.QQ;
		client.邮箱 = oldClient.邮箱;
		client.网址 = oldClient.网址;
		client.旺旺 = oldClient.旺旺;
		client.提示 = oldClient.提示;
		return client;

	}

	/** 把英文属性的评论转为中文属性 */
	public static 日志评论 MapperTo评论中文(评论 item) {
		日志评论 comment = new 日志评论();
		comment.Id = item.getId();
		comment.内容 = item.Content;
		comment.发表人 = item.userId;
		comment.发表时间 = item.PublishDate;
		comment.日志编号 = item.OrderNo;
		return comment;
	}

	/** 把英文属性的评论列表转为中文属性列表 */
	public static List<日志评论> MapperTo评论中文(List<评论> commentList) {
		List<日志评论> list = new ArrayList<日志评论>();
		for (评论 item : commentList) {
			日志评论 comment = new 日志评论();
			comment.Id = item.getId();
			comment.内容 = item.Content;
			comment.发表人 = item.userId;
			comment.发表时间 = item.PublishDate;
			comment.日志编号 = item.OrderNo;
			list.add(comment);
		}
		return list;
	}
}
