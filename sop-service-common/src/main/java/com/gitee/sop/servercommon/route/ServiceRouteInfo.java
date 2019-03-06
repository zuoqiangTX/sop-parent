package com.gitee.sop.servercommon.route;

import lombok.Data;

import java.util.List;

/**
 * @author thc
 */
@Data
public class ServiceRouteInfo {
    private String appName;
    private String md5;
    private List<GatewayRouteDefinition> routeDefinitionList;
}