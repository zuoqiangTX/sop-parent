package com.gitee.sop.servercommon.manager;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.servercommon.bean.ServiceApiInfo;
import com.gitee.sop.servercommon.route.RouteDefinition;
import com.gitee.sop.servercommon.route.ServiceRouteInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Slf4j
public class ServiceRouteInfoBuilder {

    /**
     * 网关对应的LoadBalance协议
     */
    private static final String PROTOCOL_LOAD_BALANCE = "lb://";

    private static final String PATH_SPLIT = "/";

    private static final String DEFAULT_CONTEXT_PATH = "/";

    private static ServiceApiInfo.ApiMeta FIRST_API_META = new ServiceApiInfo.ApiMeta("_first_route_", "/", "1.0.0");

    private Environment environment;

    public ServiceRouteInfoBuilder(Environment environment) {
        this.environment = environment;
    }

    public ServiceRouteInfo build(ServiceApiInfo serviceApiInfo) {
        return this.buildServiceGatewayInfo(serviceApiInfo);
    }

    /**
     * 构建接口信息，符合spring cloud gateway的格式
     *
     * @param serviceApiInfo 服务接口信息
     * @return 返回服务路由信息
     */
    protected ServiceRouteInfo buildServiceGatewayInfo(ServiceApiInfo serviceApiInfo) {
        List<ServiceApiInfo.ApiMeta> apis = serviceApiInfo.getApis();
        List<RouteDefinition> routeDefinitionList = new ArrayList<>(apis.size() + 1);
        // 在第一个位置放一个没用的路由，SpringCloudGateway会从第二个路由开始找，原因不详
        routeDefinitionList.add(this.getFirstRoute(serviceApiInfo));
        for (ServiceApiInfo.ApiMeta apiMeta : apis) {
            RouteDefinition gatewayRouteDefinition = this.buildGatewayRouteDefinition(serviceApiInfo, apiMeta);
            routeDefinitionList.add(gatewayRouteDefinition);
        }
        ServiceRouteInfo serviceRouteInfo = new ServiceRouteInfo();
        serviceRouteInfo.setServiceId(serviceApiInfo.getServiceId());
        serviceRouteInfo.setRouteDefinitionList(routeDefinitionList);
        String md5 = buildMd5(routeDefinitionList);
        serviceRouteInfo.setMd5(md5);
        return serviceRouteInfo;
    }

    /**
     * 添加com.gitee.sop.gatewaycommon.routeDefinition.ReadBodyRoutePredicateFactory,解决form表单获取不到问题
     *
     * @return 返回路由定义
     */
    protected RouteDefinition getFirstRoute(ServiceApiInfo serviceApiInfo) {
        RouteDefinition firstRoute = this.buildGatewayRouteDefinition(serviceApiInfo, FIRST_API_META);
        firstRoute.setOrder(Integer.MIN_VALUE);
        return firstRoute;
    }

    /**
     * 构建路由id MD5
     *
     * @param routeDefinitionList 路由列表
     * @return 返回MD5
     */
    protected String buildMd5(List<RouteDefinition> routeDefinitionList) {
        List<String> routeIdList = routeDefinitionList.stream()
                .map(JSON::toJSONString)
                .sorted()
                .collect(Collectors.toList());
        String md5Source = org.apache.commons.lang3.StringUtils.join(routeIdList, "");
        return DigestUtils.md5DigestAsHex(md5Source.getBytes(StandardCharsets.UTF_8));
    }

    protected RouteDefinition buildGatewayRouteDefinition(ServiceApiInfo serviceApiInfo, ServiceApiInfo.ApiMeta apiMeta) {
        RouteDefinition routeDefinition = new RouteDefinition();
        // 唯一id规则：接口名 + 版本号
        String routeId = apiMeta.fetchNameVersion();
        this.checkPath(routeId, "接口定义（" + routeId + "）不能有斜杠字符'/'");
        BeanUtils.copyProperties(apiMeta, routeDefinition);
        routeDefinition.setId(routeId);
        routeDefinition.setFilters(Collections.emptyList());
        routeDefinition.setPredicates(Collections.emptyList());
        String uri = this.buildUri(serviceApiInfo, apiMeta);
        String path = this.buildServletPath(serviceApiInfo, apiMeta);
        routeDefinition.setUri(uri);
        routeDefinition.setPath(path);
        return routeDefinition;
    }

    protected String buildUri(ServiceApiInfo serviceApiInfo, ServiceApiInfo.ApiMeta apiMeta) {
        return PROTOCOL_LOAD_BALANCE + serviceApiInfo.getServiceId();
    }

    protected String buildServletPath(ServiceApiInfo serviceApiInfo, ServiceApiInfo.ApiMeta apiMeta) {
        String contextPath = environment.getProperty("server.servlet.context-path", DEFAULT_CONTEXT_PATH);
        String servletPath = apiMeta.getPath();
        if (servletPath == null) {
            servletPath = "";
        }
        if (!servletPath.startsWith(PATH_SPLIT)) {
            servletPath = PATH_SPLIT + servletPath;
        }
        if (DEFAULT_CONTEXT_PATH.equals(contextPath)) {
            return servletPath;
        } else {
            return contextPath + servletPath;
        }
    }

    private void checkPath(String path, String errorMsg) {
        if (path.contains(PATH_SPLIT)) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

    public Environment getEnvironment() {
        return environment;
    }
}
