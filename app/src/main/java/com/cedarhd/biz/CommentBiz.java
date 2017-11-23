package com.cedarhd.biz;

import com.cedarhd.models.changhui.FormComment;
import com.cedarhd.models.日志评论;

public class CommentBiz {

	public static 日志评论 FormCommentTo日志评论(FormComment comment) {
		日志评论 workComment = new 日志评论();
		workComment.Id = comment.get编号();
		workComment.内容 = comment.get内容();
		workComment.发表人 = comment.get员工();
		workComment.发表时间 = comment.get时间();
		workComment.日志编号 = comment.get流程编号() + "";
		return workComment;
	}
}
