package com.czg.seckillsystem.service.impl;

import com.czg.seckillsystem.service.CacheService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
public class CacheServiceImpl implements CacheService {

    private Cache<String, Object> commonCache = null;

    @PostConstruct
    public void init() {
        commonCache = CacheBuilder.newBuilder()
                // 设置缓存容器初始容量
                .initialCapacity(10)
                // 设置缓存中key的最大存储数，超过会按照LRU的策略移除缓存项
                .maximumSize(100)
                // 设置缓存过期时间
                .expireAfterWrite(1, TimeUnit.MINUTES).build();
    }

    @Override
    public void setCommonCache(String key, Object value) {
        commonCache.put(key, value);
    }

    @Override
    public Object getCommonCache(String key) {
        return commonCache.getIfPresent(key);
    }
}
