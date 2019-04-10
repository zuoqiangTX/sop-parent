package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.RouteConfig;
import com.gitee.sop.gatewaycommon.bean.RouteConfigDto;

/**
 * 路由配置管理
 * @author tanghc
 */
public interface RouteConfigManager {
    void load();

    /**
     * 更新路由配置
     * @param routeConfigDto
     */
    void update(RouteConfigDto routeConfigDto);

    /**
     * 获取路由配置
     * @param routeId
     * @return
     */
    RouteConfig get(String routeId);
}
