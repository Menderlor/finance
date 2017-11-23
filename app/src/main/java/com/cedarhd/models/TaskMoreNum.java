package com.cedarhd.models;


/**
 * 更多任务页面的数据
 * 
 * @author hb
 * 
 */
public class TaskMoreNum {
	public int 所有任务已完成;
	public int 所有任务未完成;
	public int 我的任务已完成;
	public int 我的任务未完成;
	public int 我下达的任务未完成;
	public int 我下达的任务已完成;
	public int 延期任务;
	public int 所有任务;
	public int 所有未读任务;

	public TaskMoreNum() {
		// TODO Auto-generated constructor stub
	}

	public TaskMoreNum(int 所有任务已完成, int 所有任务未完成, int 我的任务已完成, int 我的任务未完成,
			int 我下达的任务未完成, int 我下达的任务已完成, int 延期任务) {
		super();
		this.所有任务已完成 = 所有任务已完成;
		this.所有任务未完成 = 所有任务未完成;
		this.我的任务已完成 = 我的任务已完成;
		this.我的任务未完成 = 我的任务未完成;
		this.我下达的任务未完成 = 我下达的任务未完成;
		this.我下达的任务已完成 = 我下达的任务已完成;
		this.延期任务 = 延期任务;
	}

}
