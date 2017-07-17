package com.jlt.vote.bis.service;

import com.jlt.vote.bis.entity.Campaign;
import com.jlt.vote.bis.vo.CampaignDetailVo;
import com.jlt.vote.bis.vo.UserDetailVo;
import com.xcrm.common.page.Pagination;

import java.util.Map;

/**
 * 主办方服务
 * @Author gaoyan
 * @Date: 2017/7/8
 */
public interface ICampaignService {

	/**
	 * 查询活动详情
	 * @param chainId 店铺标识
	 */
	Map queryCampaignDetail(Long chainId);

	/**
	 * 查询用户列表信息
	 * @param chainId 店铺标识
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	Pagination querySimpleUserList(Long chainId,String queryKey, Integer pageNo, Integer pageSize);

	/**
	 * 查询用户详情
	 * @param chainId 店铺标识
	 * @param userId 用户标识
	 * @return
	 */
	Map queryUserDetail(Long chainId, Long userId);

	/**
	 * 查询用户礼物列表
	 * @param chainId 店铺标识
	 * @param userId 用户id
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	Pagination queryUserGiftList(Long chainId,Long userId, Integer pageNo, Integer pageSize);

	/**
	 * 查询活动信息
	 * @param chainId 店铺标识
	 */
	Campaign queryCampaignInfo(Long chainId);

}

