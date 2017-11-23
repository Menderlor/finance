package com.cedarhd.biz;

import com.cedarhd.models.Dict;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DictionaryBiz {
	/***
	 * 根据字典名称 和字典Id获取对应 字典值
	 * 
	 * @param dictionaryKey
	 *            大字典的键值：省、市、县、业务员．．
	 * @param dictId
	 *            　字符型的 字典的编号 Dict.编号
	 * @return
	 */
	public static String getDictName(HashMap<String, List<Dict>> dictionarys,
			String dictionaryKey, String dictIdStr) {
		int dictId = -1;
		try {
			dictId = Integer.parseInt(dictIdStr);
		} catch (Exception e) {
			dictId = -1;
		}

		if (dictId == -1) {
			return "";
		} else {
			return getDictName(dictionarys, dictionaryKey, dictId);
		}
	}

	/***
	 * 根据大字典名称 和字典Id获取对应 字典值
	 * 
	 * @param dictionarys
	 *            字典集合
	 * 
	 * @param dictionaryContainKey
	 *            键值,省、市、县;如hashmap键值为多个字典的键值组合：‘客户经理,部门经理,副总经理,理财师,分公司总经理
	 *            ’，输入‘理财师’即可查询对应‘理财师’的字典
	 * @param dictId
	 *            　字典的编号 Dict.编号
	 * @return
	 */
	public static String getDictName(HashMap<String, List<Dict>> dictionarys,
			String dictionaryContainKey, int dictId) {
		String dictName = "";
		if (dictionarys == null || dictionaryContainKey == null) {
			return dictName;
		}

		Iterator<Entry<String, List<Dict>>> iterator = dictionarys.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, List<Dict>> entry = (Map.Entry<String, List<Dict>>) iterator
					.next();
			String dictKey = "," + entry.getKey() + ",";
			if (dictKey.contains("," + dictionaryContainKey + ",")) {
				List<Dict> dictList = dictionarys.get(entry.getKey());
				if (dictList != null && dictList.size() > 0) {
					for (Dict dict : dictList) {
						if (dict.编号 == dictId) {
							dictName = dict.名称;
							break;
						}
					}
				}
			}
		}
		return dictName;
	}
}
