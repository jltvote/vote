package com.jlt.vote.bis.campaign.vo;import java.io.Serializable;import java.util.Date;import java.util.List;/** * 活动详情 * @Author gaoyan * @Date: 2017/7/8 */public class CampaignDetailVo implements Serializable {	/**	 * 主办方主页图片	 */	private String sponsorPic;	/**	 * 报名数量     */	private String signCount;	/**	 * 投票数量     */	private Integer voteCount;	/**	 * 访问数量	 */	private Integer viewCount;	/**	 * 活动开始时间     */	private Date startTime;	/**	 * 活动结束时间	 */	private Date endTime;	/**	 * 主办方介绍	 */	private String sponsorIntro;	/**	 * 主办方图片列表	 */	private List<SponsorPicVo> sponsorPicUrls;	public String getSponsorPic() {		return sponsorPic;	}	public void setSponsorPic(String sponsorPic) {		this.sponsorPic = sponsorPic;	}	public String getSignCount() {		return signCount;	}	public void setSignCount(String signCount) {		this.signCount = signCount;	}	public Integer getVoteCount() {		return voteCount;	}	public void setVoteCount(Integer voteCount) {		this.voteCount = voteCount;	}	public Integer getViewCount() {		return viewCount;	}	public void setViewCount(Integer viewCount) {		this.viewCount = viewCount;	}	public Date getStartTime() {		return startTime;	}	public void setStartTime(Date startTime) {		this.startTime = startTime;	}	public String getSponsorIntro() {		return sponsorIntro;	}	public void setSponsorIntro(String sponsorIntro) {		this.sponsorIntro = sponsorIntro;	}	public Date getEndTime() {		return endTime;	}	public void setEndTime(Date endTime) {		this.endTime = endTime;	}	public List<SponsorPicVo> getSponsorPicUrls() {		return sponsorPicUrls;	}	public void setSponsorPicUrls(List<SponsorPicVo> sponsorPicUrls) {		this.sponsorPicUrls = sponsorPicUrls;	}	@Override	public String toString() {		final StringBuilder sb = new StringBuilder("CampaignDetailVo{");		sb.append("sponsorPic='").append(sponsorPic).append('\'');		sb.append(", signCount=").append(signCount);		sb.append(", voteCount=").append(voteCount);		sb.append(", viewCount=").append(viewCount);		sb.append(", startTime=").append(startTime);		sb.append(", endTime=").append(endTime);		sb.append(", sponsorIntro='").append(sponsorIntro).append('\'');		sb.append(", sponsorPicUrls=").append(sponsorPicUrls);		sb.append('}');		return sb.toString();	}}