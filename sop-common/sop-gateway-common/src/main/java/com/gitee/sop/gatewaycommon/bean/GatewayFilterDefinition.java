package com.gitee.sop.gatewaycommon.bean;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 网关过滤器定义规则
 *
 * @author tanghc
 */
@Data
public class GatewayFilterDefinition {
    /**
     * Filter Name
     */
    private String name;
    /**
     * 对应的路由规则
     */
    private Map<String, String> args = new LinkedHashMap<>();
}