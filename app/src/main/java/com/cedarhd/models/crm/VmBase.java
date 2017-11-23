package com.cedarhd.models.crm;

import com.cedarhd.models.Dict;

import java.util.HashMap;
import java.util.List;

/***
 * Crm返回数据基类
 * 
 * @author K
 * 
 * @param <T>
 */
public class VmBase<T> {
	public List<T> Data;
	public HashMap<String, List<Dict>> Dict;
}
