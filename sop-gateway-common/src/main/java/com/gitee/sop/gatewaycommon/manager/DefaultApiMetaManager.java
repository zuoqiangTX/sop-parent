package com.gitee.sop.gatewaycommon.manager;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.gatewaycommon.bean.ServiceApiInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;

/**
 * 保存在redis中，结构为HSET。格式如下：
 * <pre>
 * com.gitee.sop.api
 *     &lt;serviceId&gt;:{ md5:"xxx", apis:[{name:"", version:""}] }
 * </pre>
 * @author tanghc
 */
@Getter
@Slf4j
public class DefaultApiMetaManager implements ApiMetaManager {

    private StringRedisTemplate redisTemplate;
    private ApiMetaContext apiMetaContext;

    public DefaultApiMetaManager(StringRedisTemplate redisTemplate, ApiMetaContext apiMetaContext) {
        this.redisTemplate = redisTemplate;
        this.apiMetaContext = apiMetaContext;
    }

    @Override
    public void refresh() {
        log.info("刷新本地接口信息");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(API_STORE_KEY);
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            log.info("更新微服务接口，appName:{}", entry.getKey());
            String serviceId = entry.getKey().toString();
            String serviceApiInfoJson = entry.getValue().toString();
            ServiceApiInfo serviceApiInfo = JSON.parseObject(serviceApiInfoJson, ServiceApiInfo.class);
            apiMetaContext.reload(serviceId, serviceApiInfo);
        }
    }

    @Override
    public void onChange(String serviceApiInfoJson) {
        ServiceApiInfo serviceApiInfo = JSON.parseObject(serviceApiInfoJson, ServiceApiInfo.class);
        log.info("Redis订阅推送接口信息，appName:{}, md5:{}", serviceApiInfo.getAppName(), serviceApiInfo.getMd5());
        this.apiMetaContext.reload(serviceApiInfo.getAppName(), serviceApiInfo);
    }

}
