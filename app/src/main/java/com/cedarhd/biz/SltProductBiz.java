package com.cedarhd.biz;

import android.content.Context;
import android.text.TextUtils;

import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.models.slt.Slt产品字典;
import com.cedarhd.models.字典;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 森拉特定制版产品 逻辑处理相关
 * 
 * @author kjx
 * 
 */
public class SltProductBiz {
	private final static String TAG = "SltProductBiz";

	/** 下载产品字典，保存到数据库 */
	public static void downloadProductDicts(final Context context) {
		final String url = Global.BASE_URL
				+ "slt/getMultiDictByNames/型号_颜色,型号_系列,型号_接口口径,型号_进水方式";
		new Thread(new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				HttpUtils httpUtils = new HttpUtils();
				String response = httpUtils.httpGet(url);
				HashMap<String, List<字典>> dictionarysMap = new HashMap<String, List<字典>>();
				Type type = new TypeToken<HashMap<String, List<字典>>>() {
				}.getType();
				dictionarysMap = JsonUtils.ConvertJsonObject(response, type);
				try {
					if (dictionarysMap != null) {
						Dao<Slt产品字典, Integer> productDao = ORMDataHelper
								.getInstance(context).getDao(Slt产品字典.class);
						Iterator<Entry<String, List<字典>>> iter = dictionarysMap
								.entrySet().iterator();
						while (iter.hasNext()) {
							Entry<String, List<字典>> entry = iter.next();
							String key = entry.getKey();
							List<字典> dicts = entry.getValue();
							if (!TextUtils.isEmpty(key) && dicts != null) {
								List<Slt产品字典> deleteDicts = productDao
										.queryBuilder().where()
										.eq("DictTableName", key).query();
								productDao.delete(deleteDicts);
								for (int i = 0; i < dicts.size(); i++) {
									字典 dict = dicts.get(i);
									productDao.create(new Slt产品字典(dict.getId(),
											dict.getName(), key));
								}

								// SoftReference<List<字典>> softRef = new
								// SoftReference<List<字典>>(
								// dicts);
								// BoeryunApp.getDictHashMap().put(key,
								// softRef);
							}
						}
					}
				} catch (Exception ex) {
					LogUtils.e(TAG, ex.getMessage() + "");
				}
			}
		}).start();
	}

	/**
	 * 获取产品字典值
	 * 
	 * @param context
	 * @param dictTableName
	 *            字典表名
	 * @return 字典集合
	 * @throws SQLException
	 */
	public static List<Slt产品字典> getDictListByDictTableName(Context context,
			String dictTableName) throws SQLException {
		Dao<Slt产品字典, Integer> dao = ORMDataHelper.getInstance(context).getDao(
				Slt产品字典.class);
		List<Slt产品字典> dicts = dao.queryBuilder().where()
				.eq("DictTableName", dictTableName).query();
		return dicts;
	}
}
