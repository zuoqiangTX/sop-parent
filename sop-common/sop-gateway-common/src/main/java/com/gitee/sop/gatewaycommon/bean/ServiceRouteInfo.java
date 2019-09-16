package com.gitee.sop.gatewaycommon.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class ServiceRouteInfo {

    /**
     * 服务名称，对应spring.application.name
     */
    private String serviceId;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime = new Date();

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime = new Date();

    private String description;

    private List<RouteDefinition> routeDefinitionList;

    public String fetchServiceIdLowerCase() {
        return serviceId.toLowerCase();
    }
}