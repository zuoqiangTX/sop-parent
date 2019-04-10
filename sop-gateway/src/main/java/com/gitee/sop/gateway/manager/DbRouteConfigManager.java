package com.gitee.sop.gateway.manager;

import com.alibaba.fastjson.JSON;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.sop.gateway.mapper.ConfigRouteBaseMapper;
import com.gitee.sop.gateway.mapper.ConfigRouteLimitMapper;
import com.gitee.sop.gatewaycommon.bean.ChannelMsg;
import com.gitee.sop.gatewaycommon.bean.RouteConfig;
import com.gitee.sop.gatewaycommon.bean.RouteConfigDto;
import com.gitee.sop.gatewaycommon.manager.DefaultRouteConfigManager;
import com.gitee.sop.gatewaycommon.manager.ZookeeperContext;
import com.gitee.sop.gatewaycommon.util.MyBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author tanghc
 */
@Component
@Slf4j
public class DbRouteConfigManager extends DefaultRouteConfigManager {

    @Autowired
    ConfigRouteBaseMapper configRouteBaseMapper;

    @Autowired
    ConfigRouteLimitMapper configRouteLimitMapper;

    @Autowired
    Environment environment;

    @Override
    public void load() {
        Query query = new Query();

        configRouteBaseMapper.list(query)
                .stream()
                .forEach(configRouteBase -> {
                    String key = configRouteBase.getRouteId();
                    putVal(key, configRouteBase);
                });

        configRouteLimitMapper.list(query)
                .stream()
                .forEach(configRouteLimit -> {
                    String key = configRouteLimit.getRouteId();
                    putVal(key, configRouteLimit);
                });

    }

    protected void putVal(String key, Object object) {
        RouteConfig routeConfig = routeConfigMap.getOrDefault(key, newRouteConfig());
        MyBeanUtil.copyPropertiesIgnoreNull(object, routeConfig);
        routeConfigMap.put(key, routeConfig);
    }


    @PostConstruct
    protected void after() throws Exception {
        ZookeeperContext.setEnvironment(environment);
        String path = ZookeeperContext.getRouteConfigChannelPath();
        ZookeeperContext.listenPath(path, nodeCache -> {
            String nodeData = new String(nodeCache.getCurrentData().getData());
            ChannelMsg channelMsg = JSON.parseObject(nodeData, ChannelMsg.class);
            final RouteConfigDto routeConfigDto = JSON.parseObject(channelMsg.getData(), RouteConfigDto.class);
            switch (channelMsg.getOperation()) {
                case "reload":
                    log.info("重新加载路由配置信息，routeConfigDto:{}", routeConfigDto);
                    load();
                    break;
                case "update":
                    log.info("更新路由配置信息，routeConfigDto:{}", routeConfigDto);
                    update(routeConfigDto);
                    break;
            }
        });
    }
}
