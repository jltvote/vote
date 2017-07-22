package com.jlt.vote.bis.wx.service;


import com.jlt.vote.bis.wx.sdk.pay.base.PaySetting;
import com.jlt.vote.bis.wx.vo.WxPayOrder;

/**
 * 微信服务
 * @Author gaoyan
 * @Date: 2017/7/8
 */
public interface IWxService {

    /**
     * 生成跳转至第三方支付平台的html和脚本
     * @param onPayOrder 可支付的对象
     * @return 跳转到第三方支付平台的html和脚本
     */
    String jsOnPay(WxPayOrder onPayOrder) throws Exception;

}

