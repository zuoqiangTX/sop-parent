package com.gitee.sop.adminserver.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.gitee.sop.adminserver.api.service.param.RouteSearchParam;
import com.gitee.sop.adminserver.bean.GatewayRouteDefinition;
import com.gitee.sop.adminserver.bean.NacosConfigs;
import com.gitee.sop.adminserver.bean.ServiceRouteInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author tanghc
 */
@Service
public class RouteService {

    @NacosInjected
    private ConfigService configService;

    public List<GatewayRouteDefinition> getRouteDefinitionList(RouteSearchParam param) throws Exception {
        String serviceId = param.getServiceId();
        if (StringUtils.isBlank(serviceId)) {
            return Collections.emptyList();
        }
        String configData = configService.getConfig(NacosConfigs.getRouteDataId(serviceId), NacosConfigs.GROUP_ROUTE, 3000);
        if (StringUtils.isBlank(configData)) {
            return Collections.emptyList();
        }
        ServiceRouteInfo serviceRouteInfo = JSON.parseObject(configData, ServiceRouteInfo.class);

        return serviceRouteInfo.getRouteDefinitionList()
                .stream()
                .filter(gatewayRouteDefinition -> {
                    boolean isRoute = gatewayRouteDefinition.getOrder() != Integer.MIN_VALUE;
                    String id = param.getId();
                    if (StringUtils.isBlank(id)) {
                        return isRoute;
                    } else {
                        return isRoute && gatewayRouteDefinition.getId().contains(id);
                    }
                })
                .collect(toList());
    }

}
