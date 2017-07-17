package com.jlt.vote.bis.vo;import java.io.Serializable;import java.util.List;/** * 用户详情 * @Author gaoyan * @Date: 2017/7/8 */public class UserDetailVo implements Serializable {	/**	 * 主键id	 */	private Long userId;	/**	 * 编号	 */	private Integer number;    /**     * 投票数量	 */	private Integer voteCount;	/**	 * 热度	 */	private Integer viewCount;	/**	 * 礼物数	 */	private Integer giftCount;    /**     * 用户名称	 */	private String name;	/**	 * 用户图片列表	 */	private List<UserPicVo> userPicVos;	public Integer getNumber() {		return number;	}	public void setNumber(Integer number) {		this.number = number;	}	public Integer getVoteCount() {		return voteCount;	}	public void setVoteCount(Integer voteCount) {		this.voteCount = voteCount;	}	public String getName() {		return name;	}	public void setName(String name) {		this.name = name;	}	public Long getUserId() {		return userId;	}	public void setUserId(Long userId) {		this.userId = userId;	}	public Integer getViewCount() {		return viewCount;	}	public void setViewCount(Integer viewCount) {		this.viewCount = viewCount;	}	public Integer getGiftCount() {		return giftCount;	}	public void setGiftCount(Integer giftCount) {		this.giftCount = giftCount;	}	public List<UserPicVo> getUserPicVos() {		return userPicVos;	}	public void setUserPicVos(List<UserPicVo> userPicVos) {		this.userPicVos = userPicVos;	}}