package com.gitee.sop.adminserver.api.service;

import com.alibaba.fastjson.JSON;
import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;
import com.gitee.easyopen.exception.ApiException;
import com.gitee.sop.adminserver.api.service.param.RouteSearchParam;
import com.gitee.sop.adminserver.api.service.param.RouteParam;
import com.gitee.sop.adminserver.api.service.result.ServiceInfo;
import com.gitee.sop.adminserver.bean.GatewayRouteDefinition;
import com.gitee.sop.adminserver.bean.ZookeeperContext;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@ApiService
@ApiDoc("服务管理")
public class RouteApi {

    @Api(name = "route.list")
    @ApiDocMethod(description = "路由列表", elementClass = GatewayRouteDefinition.class)
    List<GatewayRouteDefinition> listRoute(RouteSearchParam param) throws Exception {
        if (StringUtils.isBlank(param.getServiceId())) {
            return Collections.emptyList();
        }

        String searchPath = ZookeeperContext.getSopRouteRootPath(param.getProfile()) + "/" + param.getServiceId();

        List<ChildData> childDataList = ZookeeperContext.getChildrenData(searchPath);

        List<GatewayRouteDefinition> routeDefinitionList = childDataList.stream()
                .map(childData -> {
                    String serviceNodeData = new String(childData.getData());
                    GatewayRouteDefinition routeDefinition = JSON.parseObject(serviceNodeData, GatewayRouteDefinition.class);
                    return routeDefinition;
                })
                .filter(gatewayRouteDefinition -> {
                    boolean isRoute = gatewayRouteDefinition.getOrder() != Integer.MIN_VALUE;
                    String id = param.getId();
                    if (StringUtils.isBlank(id)) {
                        return isRoute;
                    }else {
                        return isRoute && gatewayRouteDefinition.getId().contains(id);
                    }
                })
                .collect(Collectors.toList());

        return routeDefinitionList;
    }

    @Api(name = "route.update")
    @ApiDocMethod(description = "修改路由")
    void updateRoute(RouteParam param) throws Exception {
        String serviceIdPath = ZookeeperContext.getSopRouteRootPath(param.getProfile()) + "/" + param.getServiceId();
        String zookeeperRoutePath = serviceIdPath + "/" + param.getId();
        String data = ZookeeperContext.getData(zookeeperRoutePath);
        GatewayRouteDefinition routeDefinition = JSON.parseObject(data, GatewayRouteDefinition.class);
        BeanUtils.copyProperties(param, routeDefinition);
        ZookeeperContext.updatePathData(zookeeperRoutePath, JSON.toJSONString(routeDefinition));
    }

    @Api(name = "route.add")
    @ApiDocMethod(description = "新增路由")
    void addRoute(RouteParam param) throws Exception {
        String serviceIdPath = ZookeeperContext.getSopRouteRootPath(param.getProfile()) + "/" + param.getServiceId();
        String zookeeperRoutePath = serviceIdPath + "/" + param.getId();
        if (ZookeeperContext.isPathExist(zookeeperRoutePath)) {
            throw new ApiException("id已存在");
        }
        GatewayRouteDefinition routeDefinition = new GatewayRouteDefinition();
        BeanUtils.copyProperties(param, routeDefinition);
        ZookeeperContext.createNewData(zookeeperRoutePath, JSON.toJSONString(routeDefinition));
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setServiceId(param.getServiceId());
        serviceInfo.setDescription(param.getServiceId());
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        serviceInfo.setCreateTime(now);
        serviceInfo.setUpdateTime(now);
        ZookeeperContext.updatePathData(serviceIdPath, JSON.toJSONString(serviceInfo));
    }

}
