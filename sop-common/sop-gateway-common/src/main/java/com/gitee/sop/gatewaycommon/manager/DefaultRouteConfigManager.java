package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.RouteConfig;
import com.gitee.sop.gatewaycommon.bean.RouteConfigDto;
import com.gitee.sop.gatewaycommon.util.MyBeanUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tanghc
 */
public class DefaultRouteConfigManager implements RouteConfigManager {
    /**
     * key: routeId
     */
    protected static Map<String, RouteConfig> routeConfigMap = new ConcurrentHashMap<>(64);

    private static RouteConfig DEFAULT_CONFIG;

    public DefaultRouteConfigManager() {
        DEFAULT_CONFIG = this.newRouteConfig();
    }

    @Override
    public void load() {

    }

    @Override
    public void update(RouteConfigDto routeConfigDto) {
        this.doUpdate(routeConfigDto.getRouteId(), routeConfigDto);
    }

    protected void doUpdate(String routeId, Object res) {
        RouteConfig routeConfig = routeConfigMap.get(routeId);
        if (routeConfig == null) {
            routeConfig = newRouteConfig();
            routeConfig.setRouteId(routeId);
            routeConfigMap.put(routeId, routeConfig);
        }
        MyBeanUtil.copyPropertiesIgnoreNull(res, routeConfig);
        routeConfig.initRateLimiter();
    }

    protected RouteConfig newRouteConfig() {
        return new RouteConfig();
    }

    @Override
    public RouteConfig get(String routeId) {
        return routeConfigMap.getOrDefault(routeId, DEFAULT_CONFIG);
    }
}
