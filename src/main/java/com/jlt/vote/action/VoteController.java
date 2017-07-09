package com.jlt.vote.action;

import com.jlt.vote.bis.entity.Campaign;
import com.jlt.vote.bis.service.ICampaignService;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    @RequestMapping(value ="/vote/index",method = {RequestMethod.GET})
    public String login(HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.login");
        return "index";
    }

    /**
     * 首页登陆
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/home",method = {RequestMethod.GET})
    public String v_home(Long chainId, HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.v_home,chainId:{}",chainId);
        //通过chainId 查询 发起人信息
        Campaign campaign = campaignService.queryCampaignInfo(chainId);

        return "home";
    }

}
