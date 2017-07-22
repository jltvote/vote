package com.jlt.vote.bis.wx.service.impl;

import com.alibaba.fastjson.JSON;
import com.jlt.vote.bis.wx.PayStatusEnum;
import com.jlt.vote.bis.wx.entity.VotePay;
import com.jlt.vote.bis.wx.sdk.common.util.RandomStringGenerator;
import com.jlt.vote.bis.wx.sdk.pay.base.PaySetting;
import com.jlt.vote.bis.wx.sdk.pay.payment.Payments;
import com.jlt.vote.bis.wx.sdk.pay.payment.bean.UnifiedOrderRequest;
import com.jlt.vote.bis.wx.sdk.pay.payment.bean.UnifiedOrderResponse;
import com.jlt.vote.bis.wx.sdk.pay.util.SignatureUtil;
import com.jlt.vote.bis.wx.service.IWxService;
import com.jlt.vote.bis.wx.vo.WxPayOrder;
import com.jlt.vote.config.SysConfig;
import com.jlt.vote.util.RedisDaoSupport;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.expression.Restrictions;
import com.xcrm.log.Logger;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
	 * 两次支付间隔，单位：毫秒
	 */
	private static final int MAX_PAY_INTERVAL = 3;

	public static final String date_pattern = "yyyyMMdd";

	@Autowired
	private BaseDaoSupport baseDaoSupport;

	@Autowired
	private RedisDaoSupport redisDaoSupport;

    @Autowired
    private SysConfig sysConfig;


	@Override
	public String jsOnPay(WxPayOrder wxPayOrder) throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String noncestr = RandomStringGenerator.getRandomStringByLength(16);
        //订单编号
        String orderCode = wxPayOrder.getOrderCode();
        //支付订单流水号
        String payCode = getPayCode();
        //订单号没有则使用支付订单号
        if(StringUtils.isEmpty(orderCode)){
            orderCode = payCode;
        }else{
            //检查时间间隔
            if(isPayIntervalTooShort(orderCode)){
                throw new Exception("两次提交支付的间隔过小");
            }
        }

        UnifiedOrderRequest unifiedOrderRequest = new UnifiedOrderRequest();
        unifiedOrderRequest.setBody(wxPayOrder.getTitle());
        unifiedOrderRequest.setTradeNumber(orderCode);
        unifiedOrderRequest.setTotalFee(getWxPayMoney(wxPayOrder.getPayMoney()));
        unifiedOrderRequest.setBillCreatedIp("127.0.0.1");
        unifiedOrderRequest.setNotifyUrl(sysConfig.getWxPayCallbackUrl());
        unifiedOrderRequest.setTradeType("JSAPI");
        unifiedOrderRequest.setOpenId(wxPayOrder.getOpenId());
        PaySetting paySetting = new PaySetting();
        paySetting.setAppId(sysConfig.getWxAppId());
        paySetting.setKey(sysConfig.getWxAppSecret());
        paySetting.setMchId(sysConfig.getWxMchId());
        paySetting.setWxEndpoint(sysConfig.getWxPayUrl());
        UnifiedOrderResponse response = Payments.with(paySetting).unifiedOrder(unifiedOrderRequest);

        Map<String,Object> params2 = new HashMap<String,Object>();
        params2.put("appId", sysConfig.getWxAppId());
        params2.put("timeStamp", timestamp);
        params2.put("nonceStr", noncestr);
        params2.put("package", "prepay_id="+response.getPrepayId());
        params2.put("signType", "MD5");
        String paySign = SignatureUtil.sign(params2, sysConfig.getWxMerchantKey());
        params2.put("paySign", paySign);
        String retStr = JSON.toJSONString(params2);

        VotePay pay = new VotePay();
        pay.setCreated(new Timestamp(System.currentTimeMillis()));
        pay.setOrderCode(orderCode);
        pay.setPayCode(payCode);
        pay.setPayMoney(wxPayOrder.getPayMoney());
        pay.setPayStatus(PayStatusEnum.WAIT_BUYER_PAY.value());
        pay.setChainId(wxPayOrder.getChainId());
        pay.setTitle(wxPayOrder.getTitle());

        //save db

        return retStr;
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

	/**
	 * 检查同一个订单两次提交支付时间间隔
	 * 小于3秒提示错误
	 * @param orderCode
	 * @return
	 */
	private boolean isPayIntervalTooShort(String orderCode) {
		Long compareTime = System.currentTimeMillis() - MAX_PAY_INTERVAL;
		QueryBuilder query = QueryBuilder.where(Restrictions.ge("created",compareTime))
				.and(Restrictions.eq("orderCode",orderCode));
		int tooShortCount = baseDaoSupport.queryForInt(query,WxPayOrder.class);
		return tooShortCount >0;
	}


	/**
	 * 获取支付订单编号
	 * @return
	 */
	private String getPayCode() {
		SimpleDateFormat formatter = new SimpleDateFormat(date_pattern);
		String dateString = formatter.format(new Date());
        return new StringBuffer("o").append(dateString)
				.append(System.currentTimeMillis() / 1000)
				.append(RandomStringUtils.randomNumeric(4))
				.toString();
	}
}
