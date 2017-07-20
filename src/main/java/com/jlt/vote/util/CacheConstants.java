package com.jlt.vote.util;

/**
 * cache模块常量
 * @Author gaoyan
 * @Date: 2017/7/13
 */
public interface CacheConstants {

	/**
	 * cache 投票活动基本信息
	 * 使用实例: cb_{chainId}
	 */
	String CAMPAIGN_BASE = "cb_";

	/**
	 * cache 投票活动 奖品列表
	 * 使用实例:ca_{chainId}
	 */
	String CAMPAIGN_AWARD = "ca_";

	/**
	 * cache 投票活动 奖品json信息
	 * 使用实例:cg_{chainId}
	 */
	String CAMPAIGN_GIFT = "cg_";

	/**
	 * cache 投票活动 奖品详情
	 * 使用实例:cgd_{giftId}
	 */
	String CAMPAIGN_GIFT_DETAIL = "cgd_";

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
	 * cache 投票活动投票人信息
	 * 使用实例:vvo_{openId}
	 */
	String VOTE_VOTER_DETAIL = "vvd_";


}
