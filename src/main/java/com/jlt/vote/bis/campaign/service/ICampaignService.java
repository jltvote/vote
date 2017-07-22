package com.jlt.vote.bis.campaign.service;

import com.jlt.vote.bis.campaign.entity.Campaign;
import com.jlt.vote.bis.campaign.entity.CampaignAward;
import com.jlt.vote.bis.campaign.vo.CampaignGiftVo;
import com.xcrm.common.page.Pagination;

import java.util.List;
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
     * 保存微信用户信息
     * @param wxUserMap
     */
    void saveVoter(Map<String, Object> wxUserMap);

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

    /**
     * 查询活动奖品信息
     * @param chainId
     * @return
     */
    List<CampaignAward> queryCampaignAward(Long chainId);

    /**
     * 查询活动礼物信息
     * @param chainId
     * @return
     */
    List<CampaignGiftVo> queryCampaignGiftList(Long chainId);
}

