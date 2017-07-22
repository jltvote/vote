package com.jlt.vote.bis.campaign;

/**
 * 投票类型
 * @Author gaoyan
 * @Date: 2017/7/20
 */    
public enum VoteTypeEnum {

	/**
	 * 礼物投票
	 */
	VOTE_TYPE_GIFT("VOTE_TYPE_GIFT"),

	/**
	 * 普通
	 */
	VOTE_TYPE_COMMON("VOTE_TYPE_COMMON");

	private final String value;

	private VoteTypeEnum(final String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
}
