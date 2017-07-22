package com.jlt.vote.action;

import com.alibaba.fastjson.JSON;
import com.jlt.vote.bis.campaign.service.ICampaignService;
import com.jlt.vote.config.SysConfig;
import com.jlt.vote.util.CommonConstants;
import com.jlt.vote.util.CookieUtils;
import com.jlt.vote.util.ResponseUtils;
import com.jlt.vote.validation.ValidateFiled;
import com.jlt.vote.validation.ValidateGroup;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class VoteController {

    private static Logger logger = Logger.getLogger(VoteController.class);

    @Autowired
    private ICampaignService campaignService;

    /**
     * 首页登陆
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/{chainId}/home",method = {RequestMethod.GET})
    public String v_home(@PathVariable Long chainId, HttpServletRequest request, HttpServletResponse response,ModelMap model){
        logger.info("VoteController.v_home,chainId:{}",chainId);
        //通过chainId 查询 发起人信息
        Map campaignDetail = campaignService.queryCampaignDetail(chainId);
        campaignDetail.put("chainId",chainId);
        model.addAttribute("campaignDetail", JSON.toJSONString(campaignDetail));
        return "index";
    }

    /**
     * 查询活动奖品信息
     * @param request
     * @param response
     */
    @ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, desc = "活动id")})
    @RequestMapping(value ="/vote/{chainId}/award",method = {RequestMethod.GET})
    public void queryCampaignAward(@PathVariable Long chainId,HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.queryCampaignAward");
        //通过chainId 查询 活动奖品信息
        ResponseUtils.createSuccessResponse(response,campaignService.queryCampaignAward(chainId));
    }

    /**
     * 查询活动礼物信息
     * @param request
     * @param response
     */
    @ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, desc = "活动id")})
    @RequestMapping(value ="/vote/{chainId}/gift",method = {RequestMethod.GET})
    public void queryCampaignGift(@PathVariable Long chainId,HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.queryCampaignAward");
        //通过chainId 查询 活动礼物信息
        ResponseUtils.createSuccessResponse(response,campaignService.queryCampaignGiftList(chainId));
    }

    /**
     * 首页用户列表
     * @param request
     * @param response
     */
    @ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, desc = "活动id"),
            @ValidateFiled(index = 2, notNull = true, desc = "页码"),
            @ValidateFiled(index = 3, notNull = true, desc = "页大小")})
    @RequestMapping(value ="/vote/{chainId}/users",method = {RequestMethod.GET})
    public void getVoteUserList(@PathVariable Long chainId,String queryKey, Integer pageNo, Integer pageSize, HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.getVoteUserList,chainId:{}",chainId);
        ResponseUtils.createSuccessResponse(response,campaignService.querySimpleUserList(chainId,queryKey, pageNo, pageSize));
    }

    /**
     * 查询用户详情
     * @param request
     * @param response
     */
    @ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, desc = "活动id"),
            @ValidateFiled(index = 1, notNull = true, desc = "用户id")})
    @RequestMapping(value ="/vote/{chainId}/user",method = {RequestMethod.GET})
    public void getVoteUserDetail(@PathVariable Long chainId,Long userId, HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.getVoteUserDetail,chainId:{},useId:{}",chainId,userId);
        ResponseUtils.createSuccessResponse(response,campaignService.queryUserDetail(chainId,userId));
    }

    /**
     * 查询用户礼物列表
     * @param request
     * @param response
     */
    @ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, desc = "活动id"),
            @ValidateFiled(index = 1, notNull = true, desc = "用户id"),
            @ValidateFiled(index = 2, notNull = true, desc = "页码"),
            @ValidateFiled(index = 3, notNull = true, desc = "页大小")})
    @RequestMapping(value ="/vote/{chainId}/gifts",method = {RequestMethod.GET})
    public void getUserGiftList(@PathVariable Long chainId,Long userId,Integer pageNo, Integer pageSize, HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.getUserGiftList,chainId:{},useId:{},pageNo:{},pageSize:{}",chainId,userId,pageNo,pageSize);
        ResponseUtils.createSuccessResponse(response,campaignService.queryUserGiftList(chainId,userId,pageNo,pageSize));
    }

    @RequestMapping(value = "/vote/{chainId}/v_pay", method = RequestMethod.GET)
    public String v_pay(@PathVariable Long chainId,HttpServletRequest request,HttpServletResponse response,ModelMap model) {
        logger.debug("--------------/vote/v_pay({})--------------------",chainId);

        String openId = "";
        Cookie cookie = CookieUtils.getCookie(request, CommonConstants.WX_OPEN_ID_COOKIE);
        if (cookie != null) {
            openId = cookie.getValue();
        }
        model.put("openId", openId);
        model.put("chainId", chainId);
        return "gift";
    }



}
