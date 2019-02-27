package com.gitee.sop.gatewaycommon.manager;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author tanghc
 */
public class ApiMetaChangeListener implements MessageListener {

    private ApiMetaManager apiMetaManager;
    private StringRedisTemplate redisTemplate;

    public ApiMetaChangeListener(ApiMetaManager apiMetaManager, StringRedisTemplate redisTemplate) {
        this.apiMetaManager = apiMetaManager;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
        String msg = stringSerializer.deserialize(message.getBody());
        this.apiMetaManager.onChange(msg);
    }

}