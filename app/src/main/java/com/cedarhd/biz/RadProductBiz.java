package com.cedarhd.biz;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.models.rad.Rad产品字典;
import com.cedarhd.models.rad.Rad产品选项;
import com.cedarhd.models.rad.Rad商品;
import com.cedarhd.models.字典;
import com.cedarhd.models.字段描述;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class RadProductBiz {
	private static final String TAG = "RadProductBiz";

	/** 下载产品字典，保存到数据库 */
	public static void downloadProductDicts(final Context context) {
		final String url = Global.BASE_URL + "SltRad/getRelateForProductDicts";
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
						Dao<Rad产品字典, Integer> productDao = ORMDataHelper
								.getInstance(context).getDao(Rad产品字典.class);
						Iterator<Entry<String, List<字典>>> iter = dictionarysMap
								.entrySet().iterator();
						while (iter.hasNext()) {
							Entry<String, List<字典>> entry = iter.next();
							String key = entry.getKey();
							List<字典> dicts = entry.getValue();
							if (!TextUtils.isEmpty(key) && dicts != null) {
								List<Rad产品字典> deleteDicts = productDao
										.queryBuilder().where()
										.eq("DictTableName", key).query();
								productDao.delete(deleteDicts);
								for (int i = 0; i < dicts.size(); i++) {
									字典 dict = dicts.get(i);
									productDao.create(new Rad产品字典(dict.getId(),
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

	public static void downloadProductFieldDescrip(final Context context,
			final String tableName) {
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String url = Global.BASE_URL + "account/Get表字段描述s/"
							+ tableName;
					LogUtils.i(TAG, url);
					HttpUtils httpUtils = new HttpUtils();
					String result = httpUtils.httpGet(url);

					LogUtils.i(TAG, result);
					if (!TextUtils.isEmpty(result)) {
						try {
							String status = JsonUtils.parseStatus(result);
							if ("1".equals(status)) {
								String data = JsonUtils.getStringValue(result,
										"Data");
								new File(FilePathConfig.getCacheDirPath()
										+ File.separator + "Get表字段描述s").mkdir();

								File file = new File(FilePathConfig
										.getCacheDirPath()
										+ File.separator
										+ "Get表字段描述s", tableName);
								if (!file.exists()) {
									file.createNewFile();
								}
								try {
									FileWriter fileWriter = new FileWriter(file);
									fileWriter.write(data);
									fileWriter.flush();
									fileWriter.close();
								} catch (IOException e) {
									e.printStackTrace();
									LogUtils.e(TAG, "IOException:" + e);
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
							LogUtils.e(TAG, "IOException:" + e);
						}

					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取产品字典值
	 * 
	 * @param context
	 * @param dictTableName
	 *            字典表名
	 * @param dictId
	 *            Dict.Id
	 * @return Dict.Name
	 * @throws SQLException
	 */
	public static String getDictValue(Context context, String dictTableName,
			int dictId) throws SQLException {
		String dictValue = "";
		Dao<Rad产品字典, Integer> dao = ORMDataHelper.getInstance(context).getDao(
				Rad产品字典.class);
		Rad产品字典 dict = dao.queryBuilder().where()
				.eq("DictTableName", dictTableName).and().eq("Id", dictId)
				.queryForFirst();
		if (dict != null) {
			dictValue = dict.getName();
		}
		return dictValue;
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
	public static List<Rad产品字典> getDictListByDictTableName(Context context,
			String dictTableName, Iterable<Integer> iterableIn)
			throws SQLException {
		Dao<Rad产品字典, Integer> dao = ORMDataHelper.getInstance(context).getDao(
				Rad产品字典.class);
		List<Rad产品字典> dicts = dao.queryBuilder().where()
				.eq("DictTableName", dictTableName).and().in("Id", iterableIn)
				.query();
		return dicts;
	}

	/**
	 * 获取产品字典值
	 * 
	 * @param context
	 * @param dictTableName
	 *            字典表名
	 * @param dictId
	 *            Dict.Id
	 * @return Dict.Name
	 * @throws SQLException
	 * @throws IOException
	 */
	public static String getProductDetailInfo(Context context, Rad商品 product)
			throws SQLException, IOException {
		String returnStr = "";

		List<字段描述> dictFieldList = getTypeFieldDescribList();

		Class cl = product.getClass();
		Field fields[] = cl.getFields();

		if (dictFieldList != null && dictFieldList.size() > 0) {
			for (字段描述 field字段描述 : dictFieldList) {
				for (Field field : fields) {
					if (field字段描述.字段名.equals(field.getName())) {
						int fieldValue = 0;
						try {
							fieldValue = field.getInt(product);
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						returnStr += field字段描述.字段显示名
								+ ":"
								+ getDictValue(context, field字段描述.字典,
										fieldValue) + ";";
					}
				}
			}
		}
		return returnStr;
	}

	/** 获取产品类型选项字段 的字段描述集合 */
	public static List<字段描述> getTypeFieldDescribList()
			throws FileNotFoundException, IOException {
		String fieldsJson = getFieldDecribeJson();
		List<字段描述> fieldList = JsonUtils.pareseJsonToList(fieldsJson,
				字段描述.class);

		List<字段描述> dictFieldList = new ArrayList<字段描述>();
		for (字段描述 field : fieldList) {
			if ("规格".equals(field.分类) && !TextUtils.isEmpty(field.字典)) {
				dictFieldList.add(field);
			}
		}
		return dictFieldList;
	}

	/** 获取字典描述表数据 */
	public static String getFieldDecribeJson() throws FileNotFoundException,
			IOException {
		FileReader fileReader = new FileReader(FilePathConfig.getCacheDirPath()
				+ File.separator + "Get表字段描述s" + File.separator + "商品");
		StringBuffer sBuffer = new StringBuffer();
		char buffers[] = new char[1024];
		int len = 0;
		while ((len = fileReader.read(buffers, 0, 1024)) != -1) {
			sBuffer.append(buffers);
		}
		fileReader.close();
		String fieldsJson = sBuffer.toString();
		return fieldsJson;
	}

	/**
	 * 根据商品列表 获得商品选项列表
	 * 
	 * @param context
	 * @param productList
	 *            商品列表
	 * @param field字段描述s
	 *            商品的字段描述表
	 * @return 商品选项列表
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static HashMap<String, Rad产品选项> getProductSelectHashmap(
			Context context, List<Rad商品> productList, List<字段描述> field字段描述s)
			throws FileNotFoundException, IOException, SQLException,
			IllegalAccessException, IllegalArgumentException {
		HashMap<String, Rad产品选项> hashMap = new HashMap<String, Rad产品选项>();

		// 保存每个选项 值的集合
		HashMap<String, HashSet<Integer>> dictSetHashMap = new HashMap<String, HashSet<Integer>>();

		if (field字段描述s != null && field字段描述s.size() > 0) {
			for (字段描述 field字段描述 : field字段描述s) {
				Log.i("field", field字段描述.字段名 + "--" + field字段描述.字段显示名);
				for (Rad商品 product : productList) {
					Log.i("field_product", product.编号 + "");
					Class cl = product.getClass();
					Field reflectFields[] = cl.getFields();
					for (Field reflectField : reflectFields) {
						Log.i("field_reflectField", product.编号 + "");

						// 保存产品 选项
						if (field字段描述.字段名.equals(reflectField.getName())) {
							if (dictSetHashMap.get(field字段描述.字段名) == null) {
								dictSetHashMap.put(field字段描述.字段名,
										new HashSet<Integer>());
							}

							Object obj = reflectField.get(product);

							// 记录选择项的id集合
							dictSetHashMap.get(field字段描述.字段名).add(
									Integer.parseInt(obj.toString()));

							Rad产品选项 selectItem = new Rad产品选项();
							selectItem.slt商品 = product;
							selectItem.fieldDescribe = field字段描述;
							// selectItem.dictionaries = RadProductBiz
							// .getDictListByDictTableName(mContext,
							// field字段描述.字典);
							hashMap.put(field字段描述.字段名, selectItem);
							// break;
						}

					}
				}
			}
			LogUtils.i(TAG, hashMap.size() + "--");
		}

		Iterator<Entry<String, Rad产品选项>> iter = hashMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Rad产品选项> entry = iter.next();
			String key = entry.getKey();
			Rad产品选项 slt产品选项 = entry.getValue();
			if (dictSetHashMap.get(key) != null) {
				slt产品选项.dictionaries = getDictListByDictTableName(context,
						slt产品选项.fieldDescribe.字典, dictSetHashMap.get(key));
			}
		}
		return hashMap;
	}

	/** 获取根据几个选项 关联相关产品,如果未找到则产品为空 */
	public static Rad商品 getRelateProductInfo(
			HashMap<String, HashSet<Integer>> productSetHashMap,
			List<Rad商品> productList) {
		// 保存商品号的二维数组
		List<List<Integer>> productLists = new ArrayList<List<Integer>>();
		Iterator<Entry<String, HashSet<Integer>>> iterator = productSetHashMap
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, HashSet<Integer>> entry = iterator.next();
			String fieldName = entry.getKey();
			HashSet<Integer> productSet = entry.getValue();

			Iterator<Integer> productIdIterator = productSet.iterator();
			List<Integer> products = new ArrayList<Integer>();
			while (productIdIterator.hasNext()) {
				int productId = productIdIterator.next();
				products.add(productId);
			}
			productLists.add(products);
		}

		// 记录遍历交集
		List<List<Integer>> conflicg2s = new ArrayList<List<Integer>>();
		for (int i = 0; i < productLists.size() - 1; i++) {
			List<Integer> lines = new ArrayList<Integer>();
			List<Integer> conflicgs = new ArrayList<Integer>();
			if (i == 0) {
				lines = productLists.get(i);
			} else {
				lines = conflicg2s.get(conflicg2s.size() - 1);
			}

			for (int j = 0; j < productLists.get(i + 1).size(); j++) {
				int productId = productLists.get(i + 1).get(j);
				int index = lines.indexOf(productId);
				if (index != -1) {
					conflicgs.add(productId);
				}
			}
			conflicg2s.add(conflicgs);
		}

		for (List<Integer> list : conflicg2s) {
			for (Integer id : list) {
				LogUtils.i("product_Id", "" + id);
			}
			LogUtils.i("product_Id", "---------------");
		}

		int selectProductId = -1;
		List<Integer> list = conflicg2s.get(conflicg2s.size() - 1);
		if (list != null && list.size() > 0) {
			selectProductId = list.get(0);
		}

		if (selectProductId == -1) {
			return null;
		}

		for (Rad商品 item商品 : productList) {
			if (item商品.编号 == selectProductId) {
				return item商品;
			}
		}
		return null;
	}
}
