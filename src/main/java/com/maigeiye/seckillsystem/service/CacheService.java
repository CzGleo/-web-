package com.maigeiye.seckillsystem.service;

/**
 * 本地缓存操作类
 */
public interface CacheService {
    // 存
    void setCommonCache(String key, Object value);

    // 取
    Object getCommonCache(String key);
}
