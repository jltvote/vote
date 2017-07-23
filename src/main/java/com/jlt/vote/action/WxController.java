package com.jlt.vote.action;

import com.jlt.vote.bis.campaign.service.ICampaignService;
import com.jlt.vote.bis.campaign.vo.CampaignGiftDetailVo;
import com.jlt.vote.bis.wx.service.IWxService;
import com.jlt.vote.bis.wx.vo.GiftWxPrePayOrder;
import com.jlt.vote.bis.wx.vo.VotePrepayRequest;
import com.jlt.vote.bis.wx.vo.WxPrePayOrder;
import com.jlt.vote.config.SysConfig;
import com.jlt.vote.util.*;
import com.xcrm.common.util.InputStreamUtils;
import com.xcrm.log.Logger;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WxController {

    private static Logger logger = Logger.getLogger(WxController.class);

    @Autowired
    private ICampaignService campaignService;

    @Autowired
    private IWxService wxService;

    @Autowired
    private SysConfig sysConfig;

    /**
     * 首页登陆
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/{chainId}/index",method = {RequestMethod.GET})
    public void index(@PathVariable Long chainId, HttpServletRequest request, HttpServletResponse response) throws IOException {
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

    @RequestMapping(value = "/vote/pay/{chainId}/v_pay", method = RequestMethod.GET)
    public String v_pay(@PathVariable Long chainId,Long userId,HttpServletRequest request,HttpServletResponse response,ModelMap model) {
        logger.debug("--------------/vote/v_pay({},{})--------------------",chainId,userId);
        String openId = "";
        Cookie cookie = CookieUtils.getCookie(request, CommonConstants.WX_OPEN_ID_COOKIE);
        if (cookie != null) {
            openId = cookie.getValue();
        }
        model.put("openId", openId);
        model.put("chainId", chainId);
        //通过chainId userId查询用户详情,同时用户热度+1,活动热度+2
        Map<String,Object> userDetail = campaignService.queryUserDetail(chainId,userId);
        model.putAll(userDetail);
        return "gift";
    }

    /**
     * 投票活动预支付
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/pay/prepay",method = {RequestMethod.POST})
    public void votePrepay(@RequestBody @Valid VotePrepayRequest votePrepayRequest,BindingResult bindingResult,
                           HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.votePrepay({})",votePrepayRequest);
        if (bindingResult.hasErrors()) {
            ResponseUtils.createValidFailResponse(response, bindingResult);
            return;
        }
        Long giftId = votePrepayRequest.getGiftId();
        Long chainId = votePrepayRequest.getChainId();
        Integer giftCount = votePrepayRequest.getGiftCount();
        //查询礼物是否存在
        CampaignGiftDetailVo giftDetailVo = campaignService.queryCampaignGiftDetail(chainId,giftId);
        if(giftDetailVo == null){
            ResponseUtils.createBadResponse(response,"礼物不存在");
            return;
        }
        BigDecimal giftFee = giftDetailVo.getGiftFee();
        if((giftFee == null)||(giftFee.compareTo(BigDecimal.ZERO)<=0)){
            ResponseUtils.createBadResponse(response,"礼物金额错误");
            return;
        }

        if((giftCount == null)||(giftCount <= 0)){
            giftCount = 1;
        }

        Long userId = votePrepayRequest.getUserId();
        boolean userExist = campaignService.checkUserExist(chainId,userId);
        if(!userExist){
            ResponseUtils.createBadResponse(response,"用户不存在");
            return;
        }

        GiftWxPrePayOrder wxPrePayOrder = new GiftWxPrePayOrder();
        wxPrePayOrder.setChainId(chainId);
        wxPrePayOrder.setOrderCode(OrderCodeCreater.createTradeNO());
        wxPrePayOrder.setOpenId(votePrepayRequest.getOpenid());
        wxPrePayOrder.setTitle("礼物-"+giftDetailVo.getGiftName()+"支付");
        wxPrePayOrder.setPayMoney(giftFee.multiply(BigDecimal.valueOf(giftCount)));
        wxPrePayOrder.setGiftCount(giftCount);
        wxPrePayOrder.setGiftId(giftId);
        wxPrePayOrder.setGiftName(giftDetailVo.getGiftName());
        wxPrePayOrder.setUserId(userId);
        HashMap<String,Object> resultMap = new HashMap<String,Object>();
        try {

            String payResult = wxService.jsOnPay(wxPrePayOrder);
            resultMap.put("payResult", payResult);
        } catch (Exception e) {
            logger.error("VoteController.votePrepay error",e);
        }
        ResponseUtils.createSuccessResponse(response,resultMap);
    }

    /**
     * 微信支付回调
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/pay/callback",method = {RequestMethod.POST})
    public void wxPayCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String xml = InputStreamUtils.InputStreamTOString(request.getInputStream(), "UTF-8");
            logger.info("~~~~~~~~~~~~~~~~~~callback_xml:" + xml);
            wxService.optWxPayCallback(xml);
        } catch (Exception e) {
            logger.error("wxAuthReceive occurs exception ",e);
        }
    }



}
