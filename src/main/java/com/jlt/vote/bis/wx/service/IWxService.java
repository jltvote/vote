package com.jlt.vote.bis.wx.service;


import com.jlt.vote.bis.wx.vo.GiftWxPrePayOrder;

/**
 * 微信服务
 * @Author gaoyan
 * @Date: 2017/7/8
 */
public interface IWxService {

    /**
     * 生成跳转至第三方支付平台的html和脚本
     * @param giftWxPrePayOrder 可支付的对象
     * @return 跳转到第三方支付平台的html和脚本
     */
    String jsOnPay(GiftWxPrePayOrder giftWxPrePayOrder) throws Exception;

    /**
     * 处理微信支付回调
     * @param xml
     */
    String optWxPayCallback(String xml);
}

