package com.cedarhd.biz;

import android.content.Context;

import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.models.部门;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

public class DeptBiz {

	/***
	 * 获取当前员工所在分公司
	 * 
	 * @return 分公司的部门实体
	 */
	public static 部门 get分公司(Context context, int userId) {
		部门 dept = null;
		ORMDataHelper helper = ORMDataHelper.getInstance(context);
		try {
			QueryBuilder<部门, ?> queryBuilder = helper.getDao(部门.class)
					.queryBuilder();
			dept = queryBuilder.where().eq("编号", Global.mUser.Department)
					.queryForFirst();

			while (dept != null && dept.get上级() != 1) {
				dept = queryBuilder.where().eq("编号", dept.get上级())
						.queryForFirst();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dept;
	}

	/***
	 * 获取当前员工所在分公司
	 * 
	 * @return 分公司的部门实体
	 */
	public static 部门 getDeptById(Context context, int deptId) {
		部门 dept = null;
		ORMDataHelper helper = ORMDataHelper.getInstance(context);
		try {
			QueryBuilder<部门, ?> queryBuilder = helper.getDao(部门.class)
					.queryBuilder();
			dept = queryBuilder.where().eq("编号", deptId).queryForFirst();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dept == null ? new 部门() : dept;
	}
}
