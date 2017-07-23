package com.jlt.vote.action;

import com.jlt.vote.bis.campaign.service.ICampaignService;
import com.jlt.vote.bis.wx.service.IWxService;
import com.jlt.vote.bis.wx.vo.VotePrepayRequest;
import com.jlt.vote.bis.wx.vo.WxPayOrder;
import com.jlt.vote.config.SysConfig;
import com.jlt.vote.util.CommonConstants;
import com.jlt.vote.util.CookieUtils;
import com.jlt.vote.util.HTTPUtil;
import com.jlt.vote.util.ResponseUtils;
import com.xcrm.log.Logger;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    /**
     * 投票活动预支付
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/prepay",method = {RequestMethod.POST})
    public void votePrepay(@RequestBody @Valid VotePrepayRequest votePrepayRequest,BindingResult bindingResult,
                           HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.votePrepay({})",votePrepayRequest);
        if (bindingResult.hasErrors()) {
            ResponseUtils.createValidFailResponse(response, bindingResult);
            return;
        }
        WxPayOrder onPayOrder = new WxPayOrder();
        onPayOrder.setChainId(5910417230L);
        onPayOrder.setOpenId("oTMo21YNuO1BZqdPOIWGO1l6c5v0");
        onPayOrder.setTitle("支付测试");
        onPayOrder.setPayMoney(BigDecimal.ONE);
        HashMap<String,Object> resultMap = new HashMap<String,Object>();
        try {
            String payResult = wxService.jsOnPay(onPayOrder);
            resultMap.put("payResult", payResult);
        } catch (Exception e) {
            logger.error("VoteController.votePrepay error",e);
        }
        ResponseUtils.createSuccessResponse(response,resultMap);
    }


}
