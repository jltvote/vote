package com.jlt.vote.util;

/**
 * cache模块常量
 * @Author gaoyan
 * @Date: 2017/7/13
 */
public interface CacheConstants {

	/**
	 * cache 投票活动基本信息
	 * 使用实例: C_BASE_{chainId}
	 */
	String CAMPAIGN_BASE = "cb_";

	/**
	 * cache 投票活动参与用户详情
	 * 使用实例:vud_{userId}
     */
	String VOTE_USER_DETAIL = "vud_";

	/**
	 * cache 投票活动参与用户图片列表
	 * 使用实例:vup_{userId}
	 */
	String VOTE_USER_PICS = "vup_";

	/**
	 * cache 投票活动参与用户投票数
	 * 使用实例:vuvc_{userId}
	 */
	String VOTE_USER_VOTE_COUNT = "vuvc_";


}
