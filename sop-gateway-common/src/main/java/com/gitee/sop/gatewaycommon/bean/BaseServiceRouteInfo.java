package com.gitee.sop.gatewaycommon.bean;

import lombok.Data;

import java.util.List;

/**
 * @author thc
 */
@Data
public class BaseServiceRouteInfo<T extends BaseRouteDefinition> {
    private String appName;
    private String md5;
    private List<T> routeDefinitionList;
}