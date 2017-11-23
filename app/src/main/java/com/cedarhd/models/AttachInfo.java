package com.cedarhd.models;

import com.cedarhd.constants.enums.EnumAttachType;

import java.io.Serializable;

/***
 * 附件信息，上传附件专用
 * 
 * @author K 2016-04-18
 */
public class AttachInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6985018807275743612L;

	public AttachInfo() {
		super();
	}

	/***
	 * 
	 * @param id
	 *            附件号
	 * @param localPath
	 *            本地路径
	 * @param url
	 *            附件网络路径
	 */
	public AttachInfo(int id, String localPath, String url) {
		super();
		this.id = id;
		this.localPath = localPath;
		this.url = url;
	}

	/***
	 * 
	 * @param idStr
	 *            附件号
	 * @param localPath
	 *            本地路径
	 * @param url
	 *            附件网络路径
	 */
	public AttachInfo(String idStr, String localPath, String url) {
		super();
		this.localPath = localPath;
		this.url = url;
		setId(idStr);
	}

	/** 附件号 */
	private int id;

	/** 附件本地路径 */
	private String localPath;

	/** 附件网络路径 */
	private String url;

	/** 附件类型 @EnumAttachType */
	private EnumAttachType type;

	/** 附件信息 */
	private Attach attach;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setId(String idStr) {
		try {
			this.id = Integer.parseInt(idStr);
		} catch (Exception e) {
			this.id = 0;
		}
	}

	public void setIdAndUpdateType(String idStr) {
		setId(idStr);
		setType(EnumAttachType.附件号);
	}

	public void setIdAndUpdateType(int id) {
		setId(id);
		setType(EnumAttachType.附件号);
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public void setLocalPathUpdateType(String localPath) {
		setLocalPath(localPath);
		setType(EnumAttachType.本地路径);
	}

	public String getUrl() {
		return url;
	}

	public void setUrlUpdateType(String url) {
		setUrl(url);
		setType(EnumAttachType.附件URL);
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public EnumAttachType getType() {
		return type;
	}

	public void setType(EnumAttachType type) {
		this.type = type;
	}

	public Attach getAttach() {
		return attach;
	}

	public void setAttach(Attach attach) {
		this.attach = attach;
	}
}
