package com.gitee.sop.servercommon.route;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author thc
 */
@Data
public class ServiceRouteInfo {
    /** 服务名称，对应spring.application.name */
    private String serviceId;
    @JSONField(serialize = false)
    private List<GatewayRouteDefinition> routeDefinitionList;
}