package com.jlt.vote.bis.service.impl;

import com.jlt.vote.bis.entity.Campaign;
import com.jlt.vote.bis.service.ICampaignService;
import com.jlt.vote.bis.vo.UserDetailVo;
import com.jlt.vote.util.CacheConstants;
import com.jlt.vote.util.RedisDaoSupport;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.cloud.database.db.query.expression.Restrictions;
import com.xcrm.common.page.Pagination;
import com.xcrm.log.Logger;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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
	public Map queryCampaignDetail(Long chainId) {
		Map<String ,Object> campaignMap = redisDaoSupport.hgetAll(CacheConstants.CAMPAIGN_BASE+chainId);
		boolean isNeedSaveRedis = false;
		if(MapUtils.isEmpty(campaignMap)){
			isNeedSaveRedis = true;
			Ssqb queryDetailSqb = Ssqb.create("com.jlt.vote.queryCampaignDetail").setParam("chainId",chainId);
			campaignMap = baseDaoSupport.findForObj(queryDetailSqb,Map.class);
		}
		if(MapUtils.isNotEmpty(campaignMap)){
			//浏览量 加载首页累计加1
			int viewCount = MapUtils.getIntValue(campaignMap,"viewCount");
			int incrViewCount = viewCount + 1;
			campaignMap.put("viewCount",incrViewCount);
			if(isNeedSaveRedis){
				redisDaoSupport.hmset(CacheConstants.CAMPAIGN_BASE+chainId,campaignMap);
			}else{
				//缓存累加1
				redisDaoSupport.hinc(CacheConstants.CAMPAIGN_BASE+chainId,"viewCount",1);
			}
			//数据库中浏览量异步加1
			updateCampaignViewCount(chainId);
		}
		return campaignMap;
	}

	/**
	 * 活动浏览量增加1
	 * @param chainId
	 */
	private void updateCampaignViewCount(Long chainId){
		taskExecutor.execute(new Runnable() {
                @Override
		public void run() {
			Ssqb updateSqb = Ssqb.create("com.jlt.vote.updateCampaignViewCount").setParam("chainId",chainId);
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
	public UserDetailVo queryUserDetail(Long chainId, Long userId) {
		Ssqb queryUsersSqb = Ssqb.create("com.jlt.vote.queryUserDetail")
				.setParam("chainId",chainId)
				.setParam("userId",userId);
		return baseDaoSupport.findForObj(queryUsersSqb,UserDetailVo.class);
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

}
