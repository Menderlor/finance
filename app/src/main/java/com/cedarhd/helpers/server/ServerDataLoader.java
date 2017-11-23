package com.cedarhd.helpers.server;

import com.cedarhd.helpers.Global;
import com.cedarhd.models.Demand;
import com.cedarhd.models.任务;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class ServerDataLoader {
	private static ServerCall serverCall = null;

	/***
	 * 查询并分页
	 * 
	 * @param type
	 *            实体类型
	 * @param tableName
	 *            表名
	 * @param methodName
	 *            方法名
	 * @param pageNumber
	 *            页码
	 * @param pageSize
	 *            每页数量
	 * @param index
	 *            偏移量
	 * @param filter
	 *            过滤条件
	 * @return
	 */
	public static List<Object> getServerData(Class type, String tableName,
			String methodName, int pageNumber, int pageSize, int index,
			String filter) {
		// String md5 = ByteUtil.md5One();
		String url;
		if (tableName.equals("流程")) {
			url = Global.BASE_URL_PROCESS + Global.EXTENSION + methodName;
		} else {
			url = Global.BASE_URL + Global.EXTENSION + methodName;
		}
		List<Object> list = new ArrayList<Object>();

		Demand demand = new Demand();
		demand.方法名 = methodName;
		demand.企业编号 = Global.mUser.CorpId + "";
		demand.用户编号 = Global.mUser.Id + "";
		demand.表名 = tableName + "";
		demand.条件 = filter + "";
		demand.页码 = pageNumber;
		demand.每页数量 = pageSize;
		demand.偏移量 = index;
		LogUtils.i("keno2Resul", demand.toString());
		if (serverCall == null) {
			serverCall = new ServerCall();
		}
		String result = serverCall.makeServerCalll_Post(demand);
		// list = JsonUtils.parseNoticeFromJson(strResp);
		LogUtils.i("keno2Result", result);
		if (result != null) {
			list = JsonUtils.ConvertJsonToList(result, type);
		}
		LogUtils.i("keno2Result-->url:", url + "\n" + list.size());
		return list;
	}

	/** 查询并分页 */
	public static List<Object> getServerData(Class type, String tableName,
			String methodName, int pageNumber, int pageSize, int index,
			String filter, String value) {
		// String md5 = ByteUtil.md5One();
		String url;
		if (tableName.equals("流程")) {
			url = Global.BASE_URL_PROCESS + Global.EXTENSION + methodName;
		} else {
			url = Global.BASE_URL + Global.EXTENSION + methodName;
		}
		List<Object> list = new ArrayList<Object>();
		LogUtils.i("url_getServerData", url);
		Demand demand = new Demand();
		demand.方法名 = methodName;
		demand.企业编号 = Global.mUser.CorpId + "";
		demand.用户编号 = value;
		demand.表名 = tableName + "";
		demand.条件 = filter + "";
		demand.页码 = pageNumber;
		demand.每页数量 = pageSize;
		demand.偏移量 = index;
		if (serverCall == null)
			serverCall = new ServerCall();
		String result = serverCall.makeServerCalll_Post(demand, value);
		LogUtils.i("url_getServerData", demand.toString());
		LogUtils.i("url_getServerData", result);
		// list = JsonUtils.parseNoticeFromJson(strResp);
		if (result != null) {
			list = JsonUtils.ConvertJsonToList(result, type);
		}
		LogUtils.i("kjxtest-->url:", url + "\n" + list.size());
		return list;
	}

	/***
	 * 查询并分页
	 * 
	 * @param type
	 *            实体类类型
	 * @param demand
	 *            查询条件
	 * @author kjx
	 * @since 2014-07-19 17:21
	 * @return
	 */
	public static List<Object> getServerData(Class type, Demand demand,
			String filter) {
		String url = Global.BASE_URL + Global.EXTENSION + demand.方法名;
		List<Object> list = new ArrayList<Object>();
		LogUtils.i("url_getServerData", url);
		if (serverCall == null) {
			serverCall = new ServerCall();
		}
		String result = serverCall.makeServer_Post(demand, filter);
		LogUtils.i("demand_server", result);
		// list = JsonUtils.parseNoticeFromJson(strResp);
		if (result != null) {
			list = JsonUtils.ConvertJsonToList(result, type);
		}
		LogUtils.i("kjxtest-->url:", url + "\n" + list.size());
		return list;
	}

	/***
	 * 查询并分页(泛型方法)
	 * 
	 * @param type
	 *            实体类类型
	 * @param demand
	 *            查询条件
	 * @author kjx
	 * @param <T>
	 * @since 2014/10/08 16:31
	 * @return
	 */
	public static <T> List<T> getServerData(Demand demand, Class<T> type) {
		String url = Global.BASE_URL + Global.EXTENSION + demand.方法名;
		List<T> list = new ArrayList<T>();
		LogUtils.i("url_getServerData", url);
		if (serverCall == null) {
			serverCall = new ServerCall();
		}
		// 访问网络，下载数据
		String result = serverCall.makeServer_Post(demand, "");
		LogUtils.i("demand_server", result);
		// json解析
		if (result != null) {
			list = JsonUtils.ConvertJsonToList(result, type);
		}
		return list;
	}

	/***
	 * 查询并分页
	 * 
	 * @param type
	 *            实体类类型
	 * @param demand
	 *            查询条件
	 * @author kjx
	 * @since 2014-07-19 17:21
	 * @return
	 */
	public static List<任务> getServerTaskData(Demand demand, String filter) {
		String url = Global.BASE_URL + Global.EXTENSION + demand.方法名;
		List<任务> list = new ArrayList<任务>();
		LogUtils.i("url_getServerData", url);
		if (serverCall == null) {
			serverCall = new ServerCall();
		}
		String result = serverCall.makeServer_Post(demand, filter);
		LogUtils.i("demand_server", result + "");
		// list = JsonUtils.parseNoticeFromJson(strResp);
		if (result != null) {
			list = JsonUtils.ConvertJsonToList(result, 任务.class);
		}
		return list;
	}
}
