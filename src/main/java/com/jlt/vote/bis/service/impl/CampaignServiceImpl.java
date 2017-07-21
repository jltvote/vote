package com.jlt.vote.bis.service.impl;

import com.alibaba.fastjson.JSON;
import com.jlt.vote.bis.entity.Campaign;
import com.jlt.vote.bis.entity.CampaignAward;
import com.jlt.vote.bis.entity.Voter;
import com.jlt.vote.bis.service.ICampaignService;
import com.jlt.vote.bis.vo.CampaignDetailVo;
import com.jlt.vote.bis.vo.CampaignGiftVo;
import com.jlt.vote.bis.vo.UserDetailVo;
import com.jlt.vote.bis.vo.UserPicVo;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
	public Map queryCampaignDetail(Long chainId) {//redisDaoSupport.del(CacheConstants.CAMPAIGN_BASE+chainId);
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
		String nickname = MapUtils.getString(wxUserMap,"nickname");
		String headimgurl = MapUtils.getString(wxUserMap,"headimgurl");
		Map<String ,Object> voterDetailMap = redisDaoSupport.hgetAll(CacheConstants.VOTE_VOTER_DETAIL+openid);
		if((MapUtils.isEmpty(voterDetailMap))
				||(!Objects.equals(MapUtils.getString(voterDetailMap,"nickName"),nickname))
				||(!Objects.equals(MapUtils.getString(voterDetailMap,"headimgurl"),headimgurl))){
			Map<String ,Object> voterMap = new HashMap<>();
			voterMap.put("nickname",nickname);
			voterMap.put("headimgurl",headimgurl);
			redisDaoSupport.hmset(CacheConstants.VOTE_VOTER_DETAIL+openid,voterMap);
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
	public Map queryUserDetail(Long chainId, Long userId) {redisDaoSupport.hgetAll(CacheConstants.VOTE_USER_DETAIL+userId);
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
			Ssqb queryUsersSqb = Ssqb.create("com.jlt.vote.queryCampaignGiftList")
					.setParam("chainId",chainId);
			campaignGiftList = baseDaoSupport.findForList(queryUsersSqb,CampaignGiftVo.class);
			redisDaoSupport.set(CacheConstants.CAMPAIGN_GIFT+chainId,campaignGiftList);
			for (CampaignGiftVo campaignGiftVo : campaignGiftList) {
				if((campaignGiftVo.getGiftId() > 0)&&(campaignGiftVo.getGiftPoint() > 0)&&(campaignGiftVo.getVoteCount() > 0)){
					redisDaoSupport.set(CacheConstants.CAMPAIGN_GIFT_DETAIL+campaignGiftVo.getGiftId(),campaignGiftVo);
				}
			}
		}
		return campaignGiftList;
	}

}
