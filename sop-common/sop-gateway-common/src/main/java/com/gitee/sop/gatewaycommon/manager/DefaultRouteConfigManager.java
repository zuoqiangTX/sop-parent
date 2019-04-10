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
        String key = routeConfigDto.getRouteId();
        RouteConfig routeConfig = routeConfigMap.get(key);
        if (routeConfig == null) {
            routeConfig = newRouteConfig();
            routeConfigMap.put(key, routeConfig);
        }
        MyBeanUtil.copyPropertiesIgnoreNull(routeConfigDto, routeConfig);
    }

    protected RouteConfig newRouteConfig() {
        return new RouteConfig();
    }

    @Override
    public RouteConfig get(String routeId) {
        return routeConfigMap.getOrDefault(routeId, DEFAULT_CONFIG);
    }
}
