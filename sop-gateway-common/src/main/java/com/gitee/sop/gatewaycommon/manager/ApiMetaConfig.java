package com.gitee.sop.gatewaycommon.manager;

import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author tanghc
 */
public class ApiMetaConfig {

    private StringRedisTemplate redisTemplate;

    public ApiMetaConfig(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void loadApiMetas() {

    }

}
