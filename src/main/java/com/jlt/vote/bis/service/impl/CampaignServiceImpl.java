package com.jlt.vote.bis.service.impl;


import com.jlt.vote.bis.entity.Campaign;
import com.jlt.vote.bis.service.ICampaignService;
import com.jlt.vote.bis.vo.CampaignDetailVo;
import com.jlt.vote.bis.vo.UserDetailVo;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.cloud.database.db.query.expression.Restrictions;
import com.xcrm.common.page.Pagination;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Override
	public Campaign queryCampaignInfo(Long chainId) {
		QueryBuilder queryCamQb = QueryBuilder.where(Restrictions.eq("chainId",chainId));
		return baseDaoSupport.query(queryCamQb,Campaign.class);
	}

	@Override
	public CampaignDetailVo queryCampaignDetail(Long chainId) {
		Ssqb queryDetailSqb = Ssqb.create("com.jlt.vote.queryCampaignDetail").setParam("chainId",chainId);
		return baseDaoSupport.findForObj(queryDetailSqb,CampaignDetailVo.class);
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
}
