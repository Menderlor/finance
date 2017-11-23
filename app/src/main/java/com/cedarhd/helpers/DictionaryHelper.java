package com.cedarhd.helpers;

import android.annotation.SuppressLint;
import android.content.Context;

import com.cedarhd.R;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.models.Client;
import com.cedarhd.models.User;
import com.cedarhd.models.部门;
import com.cedarhd.utils.LogUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * 字典查询帮助类
 * 
 * @author bohr
 * 
 */
@SuppressLint("NewApi")
public class DictionaryHelper {
	private Context context;
	private ORMDataHelper helper;

	public DictionaryHelper(Context context) {
		this.context = context;
		helper = ORMDataHelper.getInstance(context);
	}

	/**
	 * 根据id查询员工姓名
	 * 
	 * @param id
	 * @return
	 */
	public String getUserNameById(String id) {
		String name = "";
		if (id != null) {
			ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
			Dao<User, Integer> dao = ormDataHelper.getUserDao();
			User user = new User();
			user.setId(id);
			user.setCorpId(Global.mUser.CorpId);
			try {
				List<User> result = dao.queryForMatching(user);
				if (result.size() > 0) {
					name = result.get(0).getUserName();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				// ormDataHelper.close();
			}
		}
		return name;
	}

	/**
	 * 根据id查询员工对象
	 * 
	 * @param id
	 * @return
	 */
	public User getUser(String id) {
		User user = new User();
		user.setId(id);
		user.setCorpId(Global.mUser.CorpId);
		if (id != null) {
			ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
			Dao<User, Integer> dao = ormDataHelper.getUserDao();
			try {
				List<User> result = dao.queryForMatching(user);
				if (result.size() > 0) {
					user = result.get(0);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				// ormDataHelper.close();
			}
		}
		return user;
	}

	/**
	 * 根据id查询员工姓名
	 * 
	 * @param id
	 * @return
	 */
	public String getUserNameById(int Id) {
		String id = String.valueOf(Id);
		return getUserNameById(id);
	}

	/**
	 * 根据id查询多个员工姓名
	 * 
	 * @param ids
	 *            例如：‘12’;‘14’;‘13’;
	 * @return 员工姓名 例如：张三,李四,王五
	 */
	public String getUserNamesById(String ids) {
		String names = "";
		if (ids == null || ids.equals("")) {
			return names;
		}
		if (ids.contains("'")) {
			ids = ids.replace("'", "");
		}
		if (ids.contains(";")) {
			if (ids.startsWith(";")) { // 针对数据表 ;12;13;
				ids = ids.substring(1, ids.length());
			}
			if (ids.endsWith(";")) { // 针对数据表 ;12;13;
				ids = ids.substring(0, ids.length() - 1);
			}
			names = queryNames(ids, ";");
		}

		if (ids.contains("|")) {
			if (ids.startsWith("|")) { // 针对数据表 |12|13|
				ids = ids.substring(1, ids.length());
			}
			if (ids.endsWith("|")) { // 针对数据表 ;12;13;
				ids = ids.substring(0, ids.length() - 1);
			}
			names = queryNames(ids, "|");
		}
		if (ids.contains(",")) {
			if (ids.startsWith(",")) { // 针对数据表 |12|13|
				ids = ids.substring(1, ids.length());
			}
			if (ids.endsWith(",")) { // 针对数据表 ;12;13;
				ids = ids.substring(0, ids.length() - 1);
			}
			names = queryNames(ids, ",");
		}

		if (ids.length() <= 2) {
			try { // 单个ID
				int id = Integer.parseInt(ids);
				names = getUserNameById(id);
			} catch (Exception e) {
			}
		}
		return names;
	}

	/**
	 * 将ids转为id数组
	 * 
	 * @param ids
	 *            例如：‘12’;‘14’;‘13’;
	 * @return
	 */
	public String[] getUserIdArray(String ids) {
		if (ids == null || ids.equals("")) {
			return new String[0];
		}
		if (ids.contains("'")) {
			ids = ids.replace("'", "");
		}
		if (ids.contains("|")) {
			ids = ids.replace("|", ";");
		}
		if (ids.contains(",")) {
			ids = ids.replace(",", ";");
		}
		if (ids.contains(";")) {
			if (ids.startsWith(";")) { // 针对数据表 ;12;13;
				ids = ids.substring(1, ids.length());
			}
			if (ids.endsWith(";")) { // 针对数据表 ;12;13;
				ids = ids.substring(0, ids.length() - 1);
			}

			if (ids.contains(";")) {
				String[] idArray = ids.split(";");
				return idArray;
			}
		}

		if (ids.length() <= 2) {
			try { // 单个ID
				String[] idArray = new String[] { ids };
				return idArray;
			} catch (Exception e) {
			}
		}
		return new String[0];
	}

	/**
	 * 根据id查询客户名称
	 * 
	 * @param id
	 * @return
	 */
	public String getClientNameById(int id) {
		return getClientById(id).getCustomerName();
	}

	/**
	 * 根据id查询客户
	 * 
	 * @param id
	 * @return
	 */
	public Client getClientById(int id) {
		Client client = new Client();
		ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
		try {
			Dao<Client, Integer> dao = ormDataHelper.getDao(Client.class);
			Client user = new Client();
			user.setId(id);
			// user.setCorpId(Global.mUser.CorpId);
			List<Client> result = dao.queryForMatching(user);
			if (result.size() > 0) {
				client = result.get(0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// ormDataHelper.close();
		}
		return client;
	}

	private String queryNames(String ids, String reg) {
		String names = "";
		String[] idArr = ids.split(reg);
		ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
		Dao<User, Integer> dao = ormDataHelper.getUserDao();
		User user = new User();
		for (int i = 0; i < idArr.length; i++) {
			user.setId(idArr[i]);
			try {
				List<User> result = dao.queryForMatching(user);
				if (result.size() > 0) {
					names += result.get(0).getUserName() + ";";
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				// ormDataHelper.close();
			}
		}
		return names;
	}

	/**
	 * 根据id查询员工头像
	 * 
	 * @param id
	 * @return 员工头像url
	 */
	public String getUserPhoto(String id) {
		if (id != null) {
			ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
			Dao<User, Integer> dao = ormDataHelper.getUserDao();
			User user = new User();
			user.setId(id);
			user.setCorpId(Global.mUser.CorpId);
			try {
				List<User> result = dao.queryForMatching(user);
				if (result.size() > 0) {
					String photo = result.get(0).getAvatarURI(); // 获得头像路径
					LogUtils.i("userInfo", result.get(0).toString());
					return photo == null ? "" : photo;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// finally {
			// //ormDataHelper.close();
			// }
		}
		return "";
	}

	/**
	 * 获得状态名
	 * 
	 * @param stateId
	 *            状态Id
	 * @return
	 */
	public String getStateName(int stateId) {
		String[] arrs = context.getResources()
				.getStringArray(R.array.statelist);
		String stateName = "";
		if (stateId >= 1 && stateId <= 6) {
			stateName = arrs[stateId - 1];
		} else {
			stateName = "状态异常";
		}
		return stateName;
	}

	/**
	 * 根据id查询客户联系人
	 * 
	 * @author py 2014.8.7
	 * @param id
	 * @return
	 */
	public String getContactsById(int id) {
		String contacts = "";
		ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
		try {
			Dao<Client, Integer> dao = ormDataHelper.getDao(Client.class);
			Client user = new Client();
			user.setId(id);
			// user.setCorpId(Global.mUser.CorpId);
			List<Client> result = dao.queryForMatching(user);
			if (result.size() > 0) {
				contacts = result.get(0).getContacts();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// ormDataHelper.close();
		}
		return contacts;
	}

	/**
	 * 根据id查询部门名称姓名
	 * 
	 * @param id
	 * @return
	 */
	public String getDepartNameById(String id) {
		String name = "";
		if (id != null) {
			ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
			try {
				Dao<部门, Integer> dao = ormDataHelper.getDao(部门.class);
				List<部门> result = dao.queryForEq("编号", id);
				if (result.size() > 0) {
					name = result.get(0).get名称();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
			}
		}
		return name;
	}

	/***
	 * 获取当前员工所在分公司
	 * 
	 * @return 分公司的部门实体
	 */
	public 部门 get分公司(Context context, int userId) {
		User user = getUser(userId + "");
		部门 dept = null;
		ORMDataHelper helper = ORMDataHelper.getInstance(context);
		try {
			QueryBuilder<部门, ?> queryBuilder = helper.getDao(部门.class)
					.queryBuilder();

			部门 childDept = queryBuilder.where().eq("编号", user.Department)
					.queryForFirst();
			dept = childDept;
			while (dept != null && dept.get上级() != 1) {
				dept = queryBuilder.where().eq("编号", dept.get上级())
						.queryForFirst();
			}

			//TODO && dept != null 我新加的判断
			if (childDept != null && dept != null) {
				LogUtils.i("depteId", childDept.get名称() + "--" + dept.get名称());
			}

			if (dept != null && dept.get编号() == 37) {
				LogUtils.d("depteId", "总部公司:" + dept.get名称());
				dept = childDept;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dept == null ? new 部门() : dept;
	}
}