package com.jlt.vote.util;

import com.jlt.vote.config.SysConfig;
import com.xcrm.cache.SimpleCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 缓存的工具类
 * @Author gaoyan
 * @Date: 2017/7/13
 */
@Component
public class CacheUtils {

    @Autowired
    private SysConfig sysConfig;

    public SimpleCache getCache(){
        return SimpleCache.instance(sysConfig.getCacheHost(),sysConfig.getCacheProvider());
    }

}
