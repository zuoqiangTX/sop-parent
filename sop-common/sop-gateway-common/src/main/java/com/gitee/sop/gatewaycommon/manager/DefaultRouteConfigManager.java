package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.RouteConfig;
import com.gitee.sop.gatewaycommon.bean.RouteStatus;
import com.gitee.sop.gatewaycommon.util.MyBeanUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 路由配置管理器
 *
 * @author tanghc
 */
public class DefaultRouteConfigManager implements RouteConfigManager {
    /**
     * key: routeId
     */
    protected static Map<String, RouteConfig> routeConfigMap = new ConcurrentHashMap<>(64);

    private static final RouteConfig DEFAULT_ROUTE_CONFIG;

    static {
        DEFAULT_ROUTE_CONFIG = new RouteConfig();
        DEFAULT_ROUTE_CONFIG.setStatus(RouteStatus.ENABLE.getStatus());
    }

    @Override
    public void load() {

    }

    @Override
    public void update(RouteConfig routeConfig) {
        this.doUpdate(routeConfig.getRouteId(), routeConfig);
    }

    protected void doUpdate(String routeId, Object res) {
        RouteConfig routeConfig = routeConfigMap.get(routeId);
        if (routeConfig == null) {
            routeConfig = newRouteConfig();
            routeConfig.setRouteId(routeId);
            routeConfigMap.put(routeId, routeConfig);
        }
        MyBeanUtil.copyPropertiesIgnoreNull(res, routeConfig);
    }

    protected RouteConfig newRouteConfig() {
        return new RouteConfig();
    }

    @Override
    public RouteConfig get(String routeId) {
        return routeConfigMap.getOrDefault(routeId, DEFAULT_ROUTE_CONFIG);
    }
}
