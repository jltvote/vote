package com.jlt.vote.bis.wx.service.impl;

import com.alibaba.fastjson.JSON;
import com.jlt.vote.bis.wx.PayStatusEnum;
import com.jlt.vote.bis.wx.entity.VotePayOrder;
import com.jlt.vote.bis.wx.sdk.common.util.RandomStringGenerator;
import com.jlt.vote.bis.wx.sdk.common.util.XmlObjectMapper;
import com.jlt.vote.bis.wx.sdk.pay.base.PaySetting;
import com.jlt.vote.bis.wx.sdk.pay.payment.Payments;
import com.jlt.vote.bis.wx.sdk.pay.payment.bean.PaymentNotification;
import com.jlt.vote.bis.wx.sdk.pay.payment.bean.UnifiedOrderRequest;
import com.jlt.vote.bis.wx.sdk.pay.payment.bean.UnifiedOrderResponse;
import com.jlt.vote.bis.wx.sdk.pay.util.SignatureUtil;
import com.jlt.vote.bis.wx.service.IWxPayService;
import com.jlt.vote.bis.wx.service.IWxService;
import com.jlt.vote.bis.wx.vo.WxPayOrder;
import com.jlt.vote.config.SysConfig;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 微信service
 * @Author gaoyan
 * @Date: 2017/6/12
 */
@Service
@Transactional
public class WxServiceImpl implements IWxService {

	private static Logger logger = Logger.getLogger(WxServiceImpl.class);

    /**
     * 微信回调返回处理成功
     */
    public static final String RET_S = "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
    /**
     * 微信回调返回处理失败
     */
    public static final String RET_F = "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";

	@Autowired
	private IWxPayService wxPayService;

    @Autowired
    private SysConfig sysConfig;


	@Override
	public String jsOnPay(WxPayOrder wxPayOrder) throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String noncestr = RandomStringGenerator.getRandomStringByLength(16);
        //订单编号
        String orderCode = wxPayOrder.getOrderCode();
        //支付订单流水号
        String payCode = wxPayService.getPayCode();

        UnifiedOrderRequest unifiedOrderRequest = new UnifiedOrderRequest();
        unifiedOrderRequest.setBody(wxPayOrder.getTitle());
        unifiedOrderRequest.setTradeNumber(payCode);
        unifiedOrderRequest.setTotalFee(getWxPayMoney(wxPayOrder.getPayMoney()));
        unifiedOrderRequest.setBillCreatedIp("127.0.0.1");
        unifiedOrderRequest.setNotifyUrl(sysConfig.getWxPayCallbackUrl());
        unifiedOrderRequest.setTradeType("JSAPI");
        unifiedOrderRequest.setOpenId(wxPayOrder.getOpenId());
        PaySetting paySetting = getPaySetting();
        UnifiedOrderResponse response = Payments.with(paySetting).unifiedOrder(unifiedOrderRequest);

        Map<String,Object> params2 = new TreeMap<>();
        params2.put("appId", sysConfig.getWxAppId());
        params2.put("timeStamp", timestamp);
        params2.put("nonceStr", noncestr);
        params2.put("package", "prepay_id="+response.getPrepayId());
        params2.put("signType", "MD5");
        String paySign = SignatureUtil.sign(params2, sysConfig.getWxMerchantKey());
        params2.put("paySign", paySign);
        //save db
        VotePayOrder pay = new VotePayOrder();
        pay.setBuyerId(wxPayOrder.getOpenId());
        pay.setCreated(new Timestamp(System.currentTimeMillis()));
        pay.setOrderCode(orderCode);
        pay.setPayCode(payCode);
        pay.setPayMoney(wxPayOrder.getPayMoney());
        pay.setPayStatus(PayStatusEnum.WAIT_BUYER_PAY.value());
        pay.setChainId(wxPayOrder.getChainId());
        pay.setTitle(wxPayOrder.getTitle());
        wxPayService.saveVotePayOrder(pay);
        return JSON.toJSONString(params2);
    }

    @Override
    public String optWxPayCallback(String xml) {
	    try {
            PaymentNotification paymentNotification = XmlObjectMapper.nonEmptyMapper().fromXml(xml,PaymentNotification.class);
            if (paymentNotification == null || paymentNotification.getReturnCode() == null) {
                logger.error("【支付失败】支付请求逻辑错误，请仔细检测传过去的每一个参数是否合法，或是看API能否被正常访问");
                return RET_F;
            }

            if (paymentNotification.getReturnCode().equals("FAIL")) {
                //注意：一般这里返回FAIL是出现系统级参数错误，请检测Post给API的数据是否规范合法
                logger.error("【支付失败】支付API系统返回失败，请检测Post给API的数据是否规范合法, return_msg={}",paymentNotification.getReturnMessage());
                return RET_F;
            }

            PaySetting paySetting = getPaySetting();
            if (!Payments.with(paySetting).checkSignature(paymentNotification)) {
                logger.error("【支付失败】支付请求API返回的数据签名验证失败，有可能数据被篡改了");
                return RET_F;
            }

            String payCode = paymentNotification.getTradeNumber();
            String tradeNo = paymentNotification.getTransactionId();
            String nonce = paymentNotification.getNonce();
            BigDecimal totalFee = BigDecimal.valueOf(paymentNotification.getTotalFee()).divide(BigDecimal.valueOf(100));
            BigDecimal cashFee = BigDecimal.valueOf(paymentNotification.getCashFee()).divide(BigDecimal.valueOf(100));
            String sellerId = paymentNotification.getMchId();
            int result = wxPayService.updatePayForCallBack(payCode
                    , nonce
                    , tradeNo
                    , PayStatusEnum.TRADE_SUCCESS.value()
                    , paymentNotification.getOpenId()
                    , totalFee
                    , cashFee
                    , paymentNotification.getTimeEndString()
                    , paymentNotification.getBankType()
                    , sellerId
                    , paymentNotification.getAppId()
                    , paymentNotification.getIsSubscribed());
            if(result == -1){
                //支付信息未找到
                return RET_F;
            }

            if(result > 0){
                //回调业务处理
//                super.paySuccess(pay.getOrderCode(), pay.getBizType());
            }
            return RET_S;
        }catch(Exception e){
            logger.error("WX-JSAPI-NOTIFY error.wx callback data: "+xml,e);
        }
        return RET_F;
    }

    private PaySetting getPaySetting(){
        PaySetting paySetting = new PaySetting();
        paySetting.setAppId(sysConfig.getWxAppId());
        paySetting.setKey(sysConfig.getWxMerchantKey());
        paySetting.setMchId(sysConfig.getWxMchId());
        paySetting.setWxEndpoint(sysConfig.getWxPayUrl());
        return paySetting;
    }

    /**
     * 获得微信支付金额 将系统BigDecimal（元） 转换为（分）
     * @param payMoney
     * @return
     */
    public int getWxPayMoney(BigDecimal payMoney) {
        Assert.notNull(payMoney, "payMoney is required");
        BigDecimal orderPayMoney = payMoney.multiply(BigDecimal.valueOf(100L));
        return orderPayMoney.intValue();
    }

}
