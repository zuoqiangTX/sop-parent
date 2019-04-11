package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.RouteConfig;
import com.gitee.sop.gatewaycommon.bean.RouteConfigDto;

/**
 * 路由配置管理
 * @author tanghc
 */
public interface RouteConfigManager {
    /**
     * 加载
     */
    void load();

    /**
     * 更新路由配置
     * @param routeConfigDto 路由配置
     */
    void update(RouteConfigDto routeConfigDto);

    /**
     * 获取路由配置
     * @param routeId 路由id
     * @return 返回RouteConfig
     */
    RouteConfig get(String routeId);
}
