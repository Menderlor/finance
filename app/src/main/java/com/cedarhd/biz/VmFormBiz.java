package com.cedarhd.biz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cedarhd.CreateVmFormActivity;

import java.util.HashMap;

public class VmFormBiz {

	/***
	 * 打开新建表单页面
	 * 
	 * @param context
	 *            当前上下文
	 * @param typeId
	 *            表单编号
	 * @param typeName
	 *            表单名称
	 */
	public static void startNewVmFromActivity(Context context, int typeId,
			String typeName) {
		startNewVmFromActivity(context, typeId, typeName, null);
	}

	/***
	 * 打开新建表单页面
	 * 
	 * @param context
	 *            当前上下文
	 * @param typeId
	 *            表单编号
	 * @param typeName
	 *            表单名称
	 * @param propertieValue
	 *            需要传递的属性值 键值对
	 */
	public static void startNewVmFromActivity(Context context, int typeId,
			String typeName, HashMap<String, Object> propertieValue) {
		Intent intent = new Intent(context, CreateVmFormActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("typeId", typeId);// 流程分类表中的编号，对应唯一的表单
		bundle.putString("typeName", typeName);
		bundle.putSerializable(CreateVmFormActivity.PROPERTY_MAPS,
				propertieValue);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/***
	 * 打开新建表单页面
	 * 
	 * @param context
	 *            当前上下文
	 * @param typeId
	 *            表单编号
	 * @param typeName
	 *            表单名称
	 * @param dataId
	 *            流程编号
	 */
	public static void startVmFromActivity(Context context, int typeId,
			int dataId, String typeName) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("typeId", typeId);// 流程分类表中的编号，对应唯一的表单
		bundle.putString("dataId", dataId + "");// 流程编号，对应唯一的表单
		bundle.putString("typeName", typeName);
		intent.putExtras(bundle);
		intent.setClass(context, CreateVmFormActivity.class);
		context.startActivity(intent);
	}
}
