package com.cedarhd.models.changhui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 * 合同上传
 * 
 * @author K
 * 
 */
public class 合同上传附件 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2989251259891382138L;

	private String info;
	private List<String> pathList;

	private String attachIds;

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public List<String> getPathList() {
		return pathList;
	}

	public void setPathList(List<String> pathList) {
		this.pathList = pathList;
	}

	public String getAttachIds() {
		return attachIds;
	}

	public void setAttachIds(String attachIds) {
		this.attachIds = attachIds;
	}

	public 合同上传附件(String info, String attachIds) {
		super();
		this.info = info;
		this.attachIds = attachIds;
		this.pathList = new ArrayList<String>();
	}

}
