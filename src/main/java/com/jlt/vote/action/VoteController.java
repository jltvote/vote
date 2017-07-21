package com.jlt.vote.action;

import com.alibaba.fastjson.JSON;
import com.jlt.vote.bis.service.ICampaignService;
import com.jlt.vote.bis.vo.VotePrepayRequest;
import com.jlt.vote.config.SysConfig;
import com.jlt.vote.util.CommonConstants;
import com.jlt.vote.util.CookieUtils;
import com.jlt.vote.util.HTTPUtil;
import com.jlt.vote.util.ResponseUtils;
import com.jlt.vote.validation.ValidateFiled;
import com.jlt.vote.validation.ValidateGroup;
import com.xcrm.log.Logger;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Controller
public class VoteController {

    private static Logger logger = Logger.getLogger(VoteController.class);

    @Autowired
    private ICampaignService campaignService;

    @Autowired
    private SysConfig sysConfig;

    /**
     * 首页登陆
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/{chainId}/index",method = {RequestMethod.GET})
    public void index(@PathVariable Long chainId,HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("VoteController.index({})",chainId);

        StringBuffer wxAuthUrl = new StringBuffer(sysConfig.getWxAuthUrl());
        wxAuthUrl.append("?appid="+sysConfig.getWxAppId());
        wxAuthUrl.append("&redirect_uri="+ URLEncoder.encode(sysConfig.getWxCallbackUrl()));
        wxAuthUrl.append("&response_type=code");
        wxAuthUrl.append("&scope=snsapi_userinfo");
        wxAuthUrl.append("&state="+chainId);
        wxAuthUrl.append("#wechat_redirect");
        response.sendRedirect(wxAuthUrl.toString());
    }

    /**
     * 微信授权回调
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/auth/callback",method = {RequestMethod.GET})
    public void wxRedirect(String code, String state,HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("VoteController.wxRedirect({},{})",code,state);
        if(StringUtils.isNotBlank(code) && StringUtils.isNotBlank(state)) {
            Long chainId = Long.valueOf(state);
            //通过回调获取的code,获取授权的accessToken和openId
            Map<String,Object> outhTokenParaMap = new HashMap<>();
            outhTokenParaMap.put("appid",sysConfig.getWxAppId());
            outhTokenParaMap.put("code",code);
            outhTokenParaMap.put("grant_type","authorization_code");
            outhTokenParaMap.put("secret",sysConfig.getWxAppSecret());
            Map<String,Object> resultMap = HTTPUtil.sendGet(sysConfig.getWxAuthTokenUrl(),outhTokenParaMap);

            if(MapUtils.isNotEmpty(resultMap)){
                logger.info("WxAuthController.wxRedirect resultMap:" + resultMap);
                if(StringUtils.isNotEmpty(MapUtils.getString(resultMap,"errmsg"))){
                    String errmsg = MapUtils.getString(resultMap,"errmsg","获取用户信息失败");
                    logger.error("VoteController.wxRedirect get token error :" + errmsg);
                    ResponseUtils.createBadResponse(response,errmsg);
                }else{
                    String accessToken = MapUtils.getString(resultMap,"access_token");
                    String openId = MapUtils.getString(resultMap,"openid");

                    if(StringUtils.isNotBlank(openId)) {
                        logger.debug("get openId from wx and save it to cookie, openId is ={}" ,openId);
                        CookieUtils.addCookie(request, response, CommonConstants.WX_OPEN_ID_COOKIE
                                , openId, null, sysConfig.getVoteCookieHost());
                    }

                    //获取用户信息
                    Map<String,Object> userInfoParaMap = new HashMap<>();
                    userInfoParaMap.put("access_token",accessToken);
                    userInfoParaMap.put("openid",openId);
                    userInfoParaMap.put("lang","zh_CN");
                    Map<String, Object> wxUserMap = HTTPUtil.sendGet(sysConfig.getWxUserInfoUrl(),userInfoParaMap);

                    if(MapUtils.isNotEmpty(wxUserMap)){
                        if(wxUserMap.containsKey("errmsg")){
                            String errmsg = MapUtils.getString(wxUserMap,"errmsg","获取用户信息失败");
                            logger.error("WxAuthController.queryWxUser occurs error.chainId:{},openId:{},userInfoParaMap:{},errmsg:{}",
                                    chainId,openId,userInfoParaMap,errmsg);
                            ResponseUtils.createBadResponse(response,errmsg);
                            return;
                        }
                        logger.info("WxAuthController.queryWxUser user:" + wxUserMap);
                        //保存用户信息到redis db
                        campaignService.saveVoter(wxUserMap);
                        String redirectHomeUrl = MessageFormat.format(sysConfig.getWxRedirectUrl(), String.valueOf(chainId));
                        logger.info("WxAuthController reirect url:" + redirectHomeUrl);
                        response.sendRedirect(response.encodeRedirectURL(redirectHomeUrl));

                    }else{
                        logger.error("WxAuthController.queryWxUser occurs error.chainId:{},openId:{},userInfoParaMap:{}",
                                chainId,openId,userInfoParaMap);
                        ResponseUtils.createBadResponse(response,"获取用户信息失败");
                    }
                }

            }
        }
    }

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

    @RequestMapping(value = "/vote/v_pay", method = RequestMethod.GET)
    public String v_pay(HttpServletRequest request,HttpServletResponse response,ModelMap model) {
        logger.debug("--------------/vote/v_pay({})--------------------");

        String openId = "";
        Cookie cookie = CookieUtils.getCookie(request, CommonConstants.WX_OPEN_ID_COOKIE);
        if (cookie != null) {
            openId = cookie.getValue();
        }
        model.put("openId", openId);
        return "gift";
    }

    /**
     * 投票活动预支付
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/prepay",method = {RequestMethod.POST})
    public void votePrepay(@RequestBody @Valid VotePrepayRequest votePrepayRequest, HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.votePrepay({})",votePrepayRequest);
        ResponseUtils.createSuccessResponse(response,votePrepayRequest);
    }

}
