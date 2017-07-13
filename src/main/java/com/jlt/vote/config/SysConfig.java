package com.jlt.vote.config;

import com.xcrm.cache.CacheProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 本地资源属性配置
 * @Author gaoyan
 * @Date: 2017/5/23
 */
@Configuration
@PropertySource("classpath:sys-config-${spring.profiles.active}.properties")
public class SysConfig {

    /**
     * 微信授权
     */
    @Value("${wxAuthUrl}")
    private String wxAuthUrl;

    /**
     * 微信appid
     */
    @Value("${wxAppId}")
    private String wxAppId;

    /**
     * 微信AppSecret
     */
    @Value("${wxAppSecret}")
    private String wxAppSecret;

    /**
     * 微信授权回调url
     */
    @Value("${wxCallbackUrl}")
    private String wxCallbackUrl;

    /**
     * 微信授权token的url
     */
    @Value("${wxAuthTokenUrl}")
    private String wxAuthTokenUrl;

    /**
     * 微信授权后重定向url
     */
    @Value("${wxRedirectUrl}")
    private String wxRedirectUrl;

    /**
     * 微信获取用户信息url
     */
    @Value("${wxUserInfoUrl}")
    private String wxUserInfoUrl;

    /**
     * memcache的host配置
     */
    @Value("${cacheHost}")
    private String cacheHost;

    /**
     * memcache的host配置
     */
    @Value("${cacheProvider}")
    private String cacheProvider;

    public String getWxAuthUrl() {
        return wxAuthUrl;
    }

    public void setWxAuthUrl(String wxAuthUrl) {
        this.wxAuthUrl = wxAuthUrl;
    }

    public String getWxAppId() {
        return wxAppId;
    }

    public void setWxAppId(String wxAppId) {
        this.wxAppId = wxAppId;
    }

    public String getWxAppSecret() {
        return wxAppSecret;
    }

    public void setWxAppSecret(String wxAppSecret) {
        this.wxAppSecret = wxAppSecret;
    }

    public String getWxCallbackUrl() {
        return wxCallbackUrl;
    }

    public void setWxCallbackUrl(String wxCallbackUrl) {
        this.wxCallbackUrl = wxCallbackUrl;
    }

    public String getWxAuthTokenUrl() {
        return wxAuthTokenUrl;
    }

    public void setWxAuthTokenUrl(String wxAuthTokenUrl) {
        this.wxAuthTokenUrl = wxAuthTokenUrl;
    }

    public String getWxRedirectUrl() {
        return wxRedirectUrl;
    }

    public void setWxRedirectUrl(String wxRedirectUrl) {
        this.wxRedirectUrl = wxRedirectUrl;
    }

    public String getWxUserInfoUrl() {
        return wxUserInfoUrl;
    }

    public void setWxUserInfoUrl(String wxUserInfoUrl) {
        this.wxUserInfoUrl = wxUserInfoUrl;
    }

    public String getCacheHost() {
        return cacheHost;
    }

    public void setCacheHost(String cacheHost) {
        this.cacheHost = cacheHost;
    }

    public String getCacheProvider() {
        return cacheProvider;
    }

    public void setCacheProvider(String cacheProvider) {
        this.cacheProvider = cacheProvider;
    }
}