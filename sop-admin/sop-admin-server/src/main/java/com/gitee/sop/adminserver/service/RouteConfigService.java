package com.gitee.sop.adminserver.service;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.adminserver.bean.ChannelMsg;
import com.gitee.sop.adminserver.bean.ConfigLimitDto;
import com.gitee.sop.adminserver.bean.RouteConfigDto;
import com.gitee.sop.adminserver.bean.ZookeeperContext;
import com.gitee.sop.adminserver.common.ChannelOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author tanghc
 */
@Service
@Slf4j
public class RouteConfigService {

    /**
     * 发送路由配置消息
     * @param routeConfigDto
     * @throws Exception
     */
    public void sendRouteConfigMsg(RouteConfigDto routeConfigDto) {
        ChannelMsg channelMsg = new ChannelMsg(ChannelOperation.ROUTE_CONFIG_UPDATE, routeConfigDto);
        String jsonData = JSON.toJSONString(channelMsg);
        String path = ZookeeperContext.getRouteConfigChannelPath();
        log.info("消息推送--路由配置(update), path:{}, data:{}", path, jsonData);
        ZookeeperContext.createOrUpdateData(path, jsonData);
    }

    /**
     * 推送路由配置
     * @param routeConfigDto
     * @throws Exception
     */
    public void sendLimitConfigMsg(ConfigLimitDto routeConfigDto) throws Exception {
        ChannelMsg channelMsg = new ChannelMsg(ChannelOperation.LIMIT_CONFIG_UPDATE, routeConfigDto);
        String jsonData = JSON.toJSONString(channelMsg);
        String path = ZookeeperContext.getLimitConfigChannelPath();
        log.info("消息推送--限流配置(update), path:{}, data:{}", path, jsonData);
        ZookeeperContext.createOrUpdateData(path, jsonData);
    }
}
