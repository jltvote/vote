package com.jlt.vote.bis.campaign.service.impl;

import com.alibaba.fastjson.JSON;
import com.jlt.vote.bis.campaign.entity.Campaign;
import com.jlt.vote.bis.campaign.entity.CampaignAward;
import com.jlt.vote.bis.campaign.entity.Voter;
import com.jlt.vote.bis.campaign.service.ICampaignService;
import com.jlt.vote.bis.campaign.vo.*;
import com.jlt.vote.bis.wx.entity.UserGiftRecord;
import com.jlt.vote.util.CacheConstants;
import com.jlt.vote.util.RedisDaoSupport;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.BaseQuery;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.cloud.database.db.query.expression.Restrictions;
import com.xcrm.common.page.Pagination;
import com.xcrm.common.util.ListUtil;
import com.xcrm.log.Logger;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 投票service
 * @Author gaoyan
 * @Date: 2017/6/12
 */
@Service
@Transactional
public class CampaignServiceImpl implements ICampaignService {

	private static Logger logger = Logger.getLogger(CampaignServiceImpl.class);

	@Autowired
	private BaseDaoSupport baseDaoSupport;

	@Autowired
	private RedisDaoSupport redisDaoSupport;

	@Autowired
	private TaskExecutor taskExecutor;


	@Override
	public Map<String ,Object> queryCampaignDetail(Long chainId) {//redisDaoSupport.del(CacheConstants.CAMPAIGN_BASE+chainId);
		Map<String ,Object> campaignMap = redisDaoSupport.hgetAll(CacheConstants.CAMPAIGN_BASE+chainId);
		if(MapUtils.isEmpty(campaignMap)){
			if(campaignMap == null){
				campaignMap = new HashMap<>();
			}
			Ssqb queryDetailSqb = Ssqb.create("com.jlt.vote.queryCampaignDetail").setParam("chainId",chainId);
			CampaignDetailVo campaignDetail = baseDaoSupport.findForObj(queryDetailSqb,CampaignDetailVo.class);
			campaignMap.put("sponsorPic",campaignDetail.getSponsorPic());
			campaignMap.put("signCount",campaignDetail.getSignCount());
			campaignMap.put("voteCount",campaignDetail.getVoteCount());
			campaignMap.put("startTime",campaignDetail.getStartTime());
			campaignMap.put("sponsorIntro",campaignDetail.getSponsorIntro());
			campaignMap.put("endTime",campaignDetail.getEndTime());
			campaignMap.put("sponsorPicUrls",campaignDetail.getSponsorPicUrls());
			campaignMap.put("viewCount",campaignDetail.getViewCount() + 1);
			redisDaoSupport.hmset(CacheConstants.CAMPAIGN_BASE+chainId,campaignMap);
		}else{
			//缓存累加1
			redisDaoSupport.hinc(CacheConstants.CAMPAIGN_BASE+chainId,"viewCount",1);
			//浏览量 加载首页累计加1
			campaignMap.put("viewCount",MapUtils.getIntValue(campaignMap,"viewCount") + 1);
		}
		//数据库中浏览量异步加1
		updateViewCount(chainId,1,null,null);
		return campaignMap;
	}

	@Override
	public void saveVoter(Map<String, Object> wxUserMap) {
		String openid = MapUtils.getString(wxUserMap,"openid");
		String nickname = MapUtils.getString(wxUserMap,"nickname","");
		String headimgurl = MapUtils.getString(wxUserMap,"headimgurl","");
		VoterVo voterDetail = redisDaoSupport.get(CacheConstants.VOTE_VOTER_DETAIL+openid,VoterVo.class);
		if((voterDetail == null)
				||(!Objects.equals(voterDetail.getNickname(),nickname))
				||(!Objects.equals(voterDetail.getHeadimgurl(),headimgurl))){
			VoterVo voterVo = new VoterVo();
			voterVo.setNickname(nickname);
			voterVo.setHeadimgurl(headimgurl);
			redisDaoSupport.set(CacheConstants.VOTE_VOTER_DETAIL+openid,voterVo);
			//异步保存db
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					Ssqb updateSqb = Ssqb.create("com.jlt.vote.updateVoter")
							.setParam("openid",openid)
							.setParam("nickname",nickname)
							.setParam("headimgurl",headimgurl)
							.setParam("rawData",JSON.toJSONString(wxUserMap));
					baseDaoSupport.updateByMybatis(updateSqb);
				}
			});
		}
	}

	@Override
	public VoterVo queryVoter(String openId) {
		VoterVo voterDetail = redisDaoSupport.get(CacheConstants.VOTE_VOTER_DETAIL+openId,VoterVo.class);
		if(voterDetail == null){
			Ssqb querySqb = Ssqb.create("com.jlt.vote.queryVoter")
					.setParam("openid",openId);
			voterDetail = baseDaoSupport.findForObj(querySqb,VoterVo.class);
			if(voterDetail != null){
				redisDaoSupport.set(CacheConstants.VOTE_VOTER_DETAIL+openId,voterDetail);
			}
		}
		return voterDetail;
	}

	/**
	 * 活动浏览量增加1
	 * @param chainId
	 */
	private void updateViewCount(Long chainId,Integer incrCampaignViewCount,Long userId,Integer incrUserViewCount){
		taskExecutor.execute(new Runnable() {
                @Override
		public void run() {
			Ssqb updateSqb = Ssqb.create("com.jlt.vote.updateCampaignViewCount")
					.setParam("chainId",chainId)
					.setParam("incrCampaignViewCount",incrCampaignViewCount)
					.setParam("userId",userId)
					.setParam("incrUserViewCount",incrUserViewCount);
			baseDaoSupport.updateByMybatis(updateSqb);
		}
	});
	}

	@Override
	public Pagination querySimpleUserList(Long chainId, String queryKey, Integer pageNo, Integer pageSize) {
		Ssqb queryUsersSqb = Ssqb.create("com.jlt.vote.querySimpleUserList")
				.setParam("queryKey",queryKey)
				.setParam("chainId",chainId)
				.setParam("pageNo",pageNo)
				.setParam("pageSize",pageSize);
		return baseDaoSupport.findForPage(queryUsersSqb);
	}

	@Override
	public Map<String,Object> queryUserDetail(Long chainId, Long userId) {redisDaoSupport.del(CacheConstants.VOTE_USER_DETAIL+userId);
		Map<String,Object> result = new HashMap<>();
		Map<String,Object> userDetailMap = redisDaoSupport.hgetAll(CacheConstants.VOTE_USER_DETAIL+userId);
		List userPicList = redisDaoSupport.getList(CacheConstants.VOTE_USER_PICS+userId, UserPicVo.class);
		if((MapUtils.isEmpty(userDetailMap))||(ListUtil.isEmpty(userPicList))){
			Ssqb queryUsersSqb = Ssqb.create("com.jlt.vote.queryUserDetail")
					.setParam("chainId",chainId)
					.setParam("userId",userId);
			UserDetailVo userDetail = baseDaoSupport.findForObj(queryUsersSqb,UserDetailVo.class);
			result.put("name",userDetail.getName());
			result.put("number",userDetail.getNumber());
			result.put("userId",userDetail.getUserId());
			result.put("giftCount",userDetail.getGiftCount());
			result.put("viewCount",userDetail.getViewCount() + 1);
			result.put("voteCount",userDetail.getVoteCount());
			result.put("headPic",userDetail.getHeadPic());
			redisDaoSupport.set(CacheConstants.VOTE_USER_PICS+userId,userDetail.getUserPicVos());
			redisDaoSupport.hmset(CacheConstants.VOTE_USER_DETAIL+userId,result);
			result.put("userPicVos",userDetail.getUserPicVos());
		}else{
			//用户浏览量加1
			redisDaoSupport.hinc(CacheConstants.VOTE_USER_DETAIL+chainId,"viewCount",1);
			result.put("userPicVos",userPicList);
			result.putAll(userDetailMap);
			result.put("viewCount",MapUtils.getInteger(result,"viewCount") + 1);
		}
		//活动浏览量缓存累加2
		redisDaoSupport.hinc(CacheConstants.CAMPAIGN_BASE+chainId,"viewCount",2);
		//数据库中活动浏览量异步加2,用户浏览量加1
		updateViewCount(chainId,2,userId,1);
		return result;
	}

	@Override
	public boolean checkUserExist(Long chainId, Long userId) {
		Map<String,Object> userDetailMap = redisDaoSupport.hgetAll(CacheConstants.VOTE_USER_DETAIL+userId);
		if(MapUtils.isEmpty(userDetailMap)){
			Ssqb queryUserCountSqb = Ssqb.create("com.jlt.vote.queryUserCount")
					.setParam("chainId",chainId)
					.setParam("userId",userId);
			int userCount = baseDaoSupport.findForInt(queryUserCountSqb);
			return userCount >0;
		}
		return false;
	}

	@Override
	public Pagination queryUserGiftList(Long chainId, Long userId, Integer pageNo, Integer pageSize) {
		Ssqb queryUsersSqb = Ssqb.create("com.jlt.vote.queryUserGiftList")
				.setParam("userId",userId)
				.setParam("chainId",chainId)
				.setParam("pageNo",pageNo)
				.setParam("pageSize",pageSize);
		return baseDaoSupport.findForPage(queryUsersSqb);
	}

	@Override
	public Campaign queryCampaignInfo(Long chainId) {
		logger.debug("CampaignServiceImpl.queryCampaignInfo({})",chainId);
		QueryBuilder queryCamQb = QueryBuilder.where(Restrictions.eq("chainId",chainId));
		return baseDaoSupport.query(queryCamQb,Campaign.class);
	}

	@Override
	public List<CampaignAward> queryCampaignAward(Long chainId) {//redisDaoSupport.del(CacheConstants.CAMPAIGN_AWARD+chainId);
		List<CampaignAward> campaignAwardList = redisDaoSupport.getList(CacheConstants.CAMPAIGN_AWARD+chainId,CampaignAward.class);
		if(ListUtil.isEmpty(campaignAwardList)){
			QueryBuilder queryCamAwardQb = QueryBuilder.where(Restrictions.eq("chainId",chainId))
					.and(Restrictions.eq("dataStatus",1))
					.setOrderBys("priority", BaseQuery.OrderByType.desc);
			campaignAwardList = baseDaoSupport.queryList(queryCamAwardQb,CampaignAward.class);

			//保存redis
			redisDaoSupport.set(CacheConstants.CAMPAIGN_AWARD+chainId,campaignAwardList);
		}
		return campaignAwardList;
	}

	@Override
	public List<CampaignGiftVo> queryCampaignGiftList(Long chainId) {
		List<CampaignGiftVo> campaignGiftList = redisDaoSupport.getList(CacheConstants.CAMPAIGN_GIFT+chainId,CampaignGiftVo.class);
		if(ListUtil.isEmpty(campaignGiftList)){
			if(campaignGiftList == null){
				campaignGiftList = new ArrayList<>();
			}
			Ssqb queryUsersSqb = Ssqb.create("com.jlt.vote.queryCampaignGift")
					.setParam("chainId",chainId);
			List<CampaignGiftDetailVo> campaignGiftDetailList = baseDaoSupport.findForList(queryUsersSqb,CampaignGiftDetailVo.class);
			for (CampaignGiftDetailVo detailVo : campaignGiftDetailList) {
				if((detailVo.getGiftId() > 0)
						&&(detailVo.getGiftPoint() > 0)
						&&(detailVo.getVoteCount() > 0)
						&&(detailVo.getGiftFee().compareTo(BigDecimal.ZERO) > 0)){
					redisDaoSupport.set(CacheConstants.CAMPAIGN_GIFT_DETAIL+detailVo.getGiftId(),detailVo);
					CampaignGiftVo campaignGiftVo = new CampaignGiftVo();
					BeanUtils.copyProperties(detailVo,campaignGiftVo);
					campaignGiftList.add(campaignGiftVo);
				}
			}
			redisDaoSupport.set(CacheConstants.CAMPAIGN_GIFT+chainId,campaignGiftList);
		}
		return campaignGiftList;
	}

	@Override
	public CampaignGiftDetailVo queryCampaignGiftDetail(Long chainId,Long giftId) {
		CampaignGiftDetailVo detailVo = redisDaoSupport.get(CacheConstants.CAMPAIGN_GIFT_DETAIL+giftId,CampaignGiftDetailVo.class);
		if(detailVo == null){
			Ssqb queryUsersSqb = Ssqb.create("com.jlt.vote.queryCampaignGift")
					.setParam("chainId",chainId)
					.setParam("giftId",giftId);
			detailVo = baseDaoSupport.findForObj(queryUsersSqb,CampaignGiftDetailVo.class);
			if((detailVo != null)
					&&(detailVo.getGiftId() > 0)
					&&(detailVo.getGiftPoint() > 0)
					&&(detailVo.getVoteCount() > 0)
					&&(detailVo.getGiftFee() != null)
					&&(detailVo.getGiftFee().compareTo(BigDecimal.ZERO) > 0)){
				redisDaoSupport.set(CacheConstants.CAMPAIGN_GIFT_DETAIL+giftId,detailVo);
			}
		}
		return detailVo;
	}

	@Override
	public void saveUserGiftRecord(UserGiftRecord userGiftRecord) {
		String openId = userGiftRecord.getOpenId();
		if(StringUtils.isNotEmpty(openId)){
			VoterVo voterVo = queryVoter(openId);
			userGiftRecord.setHeadImgUrl(voterVo.getHeadimgurl());
			userGiftRecord.setNickName(voterVo.getNickname());
		}
		baseDaoSupport.save(userGiftRecord);
	}

	@Override
	public void updateUserGiftRecord(Long orderId) {
		Ssqb updateSqb = Ssqb.create("com.jlt.vote.updateGiftRecord")
				.setParam("orderId",orderId);
		baseDaoSupport.updateByMybatis(updateSqb);
	}

	@Override
	public void deleteAllRedisKeys() {
		Set<String> keys = redisDaoSupport.keys("*");
		if(keys != null && keys.size() > 0){
			keys.forEach(a->{
				redisDaoSupport.del(a);
			});
		}
	}

}
