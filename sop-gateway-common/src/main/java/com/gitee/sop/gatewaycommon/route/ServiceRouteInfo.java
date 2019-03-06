package com.gitee.sop.gatewaycommon.route;

import lombok.Data;

import java.util.List;

@Data
public class ServiceRouteInfo {
    private String appName;
    private String md5;
    private List<GatewayRouteDefinition> routeDefinitionList;
}