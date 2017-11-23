package com.cedarhd.base;

import android.content.Context;

import com.cedarhd.biz.DictionaryBiz;
import com.cedarhd.models.Dict;
import com.cedarhd.utils.LogUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/***
 * CRM通用版带字典项的通用内容适配器
 * 
 * @author K
 * 
 * @param <T>
 */
public abstract class CommanCrmAdapter<T> extends CommanAdapter<T> {
	private final String TAG = "CommanCrmAdapter";
	private final HashMap<String, List<Dict>> mDictionarys = new HashMap<String, List<Dict>>();

	public CommanCrmAdapter(List<T> mList, Context context, int mLayoutId) {
		super(mList, context, mLayoutId);
	}

	/** 添加字典项 */
	public void addDict(HashMap<String, List<Dict>> dictMap) {
		Iterator<Entry<String, List<Dict>>> iterator = dictMap.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, List<Dict>> entry = (Map.Entry<String, List<Dict>>) iterator
					.next();
			String dictName = entry.getKey();

			/** 新添加的字典集合 */
			List<Dict> newDictList = entry.getValue();

			if (mDictionarys.get(dictName) == null) {
				// 如果指定字典名称的集合不存在，添加到字典中
				mDictionarys.put(dictName, newDictList);
			} else {
				// 存在则添加队列末尾
				List<Dict> oldDictList = mDictionarys.get(dictName);
				LogUtils.d(TAG, dictName);
				for (Dict newDict : newDictList) {
					LogUtils.i(TAG, newDict.名称 + "");
					boolean isAdd = true;

					for (Dict oldDict : oldDictList) {
						if (newDict.编号 == oldDict.编号) {
							// 和已有字典对比，添加不重复的元素
							isAdd = false;
							break;
						}
					}
					if (isAdd) {
						oldDictList.add(newDict);
					}
				}
			}
		}
	}

	/***
	 * 根据字典名称 和字典Id获取对应 字典值
	 * 
	 * @param dictionaryKey
	 *            大字典的键值：省、市、县、业务员．．
	 * @param dictId
	 *            　字符型的 字典的编号 Dict.编号
	 * @return
	 */
	public String getDictName(String dictionaryKey, String dictIdStr) {
		int dictId = -1;
		try {
			dictId = Integer.parseInt(dictIdStr);
		} catch (Exception e) {
			dictId = -1;
		}

		if (dictId == -1) {
			return "";
		} else {
			return getDictName(dictionaryKey, dictId);
		}

	}

	/***
	 * 根据大字典名称 和字典Id获取对应 字典值
	 * 
	 * @param dictionaryKey
	 *            大字典的键值：省、市、县、业务员．．
	 * @param dictId
	 *            　字典的编号 Dict.编号
	 * @return
	 */
	public String getDictName(String dictionaryKey, int dictId) {
		// String dictName = "";
		// Iterator<Entry<String, List<Dict>>> iterator =
		// mDictionarys.entrySet()
		// .iterator();
		// while (iterator.hasNext()) {
		// Map.Entry<String, List<Dict>> entry = (Map.Entry<String, List<Dict>>)
		// iterator
		// .next();
		// String dictKey = "," + entry.getKey() + ",";
		// if (dictKey.contains("," + dictionaryKey + ",")) {
		// List<Dict> dictList = mDictionarys.get(entry.getKey());
		// if (dictList != null && dictList.size() > 0) {
		// for (Dict dict : dictList) {
		// if (dict.编号 == dictId) {
		// dictName = dict.名称;
		// break;
		// }
		// }
		// }
		// }
		// }
		return DictionaryBiz.getDictName(mDictionarys, dictionaryKey, dictId);
	}

	/***
	 * 获取列表字典
	 * 
	 * @return
	 */
	public HashMap<String, List<Dict>> getmDictionarys() {
		return mDictionarys == null ? new HashMap<String, List<Dict>>()
				: mDictionarys;
	}

}
