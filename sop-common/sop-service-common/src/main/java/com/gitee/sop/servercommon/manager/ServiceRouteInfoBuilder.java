package com.gitee.sop.servercommon.manager;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.servercommon.bean.ServiceApiInfo;
import com.gitee.sop.servercommon.route.GatewayPredicateDefinition;
import com.gitee.sop.servercommon.route.GatewayRouteDefinition;
import com.gitee.sop.servercommon.route.ServiceRouteInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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

    /**
     * NameVersion=alipay.story.get1.0
     * see com.gitee.sop.gatewaycommon.routeDefinition.NameVersionRoutePredicateFactory
     */
    private static String QUERY_PREDICATE_DEFINITION_TPL = "NameVersion=%s";

    private static ServiceApiInfo.ApiMeta FIRST_API_META = new ServiceApiInfo.ApiMeta("_first.route_", "/", "v_000");

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
        List<GatewayRouteDefinition> routeDefinitionList = new ArrayList<>(apis.size());
        routeDefinitionList.add(this.buildReadBodyRouteDefinition(serviceApiInfo));
        for (ServiceApiInfo.ApiMeta apiMeta : apis) {
            GatewayRouteDefinition gatewayRouteDefinition = this.buildGatewayRouteDefinition(serviceApiInfo, apiMeta);
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
     * 构建路由id MD5
     *
     * @param routeDefinitionList 路由列表
     * @return 返回MD5
     */
    protected String buildMd5(List<GatewayRouteDefinition> routeDefinitionList) {
        List<String> routeIdList = routeDefinitionList.stream()
                .map(JSON::toJSONString)
                .sorted()
                .collect(Collectors.toList());
        String md5Source = org.apache.commons.lang3.StringUtils.join(routeIdList, "");
        return DigestUtils.md5DigestAsHex(md5Source.getBytes(StandardCharsets.UTF_8));
    }

    protected GatewayRouteDefinition buildGatewayRouteDefinition(ServiceApiInfo serviceApiInfo, ServiceApiInfo.ApiMeta apiMeta) {
        GatewayRouteDefinition gatewayRouteDefinition = new GatewayRouteDefinition();
        // 唯一id规则：接口名 + 版本号
        String routeId = apiMeta.fetchNameVersion();
        this.checkPath(routeId, "接口定义（" + routeId + "）不能有斜杠字符'/'");
        BeanUtils.copyProperties(apiMeta, gatewayRouteDefinition);
        gatewayRouteDefinition.setId(routeId);
        gatewayRouteDefinition.setFilters(Collections.emptyList());
        gatewayRouteDefinition.setPredicates(this.buildPredicates(apiMeta));
        String uri = this.buildUri(serviceApiInfo, apiMeta);
        String path = this.buildServletPath(serviceApiInfo, apiMeta);
        gatewayRouteDefinition.setUri(uri);
        gatewayRouteDefinition.setPath(path);
        return gatewayRouteDefinition;
    }

    protected List<GatewayPredicateDefinition> buildPredicates(ServiceApiInfo.ApiMeta apiMeta) {
        GatewayPredicateDefinition gatewayPredicateDefinition = new GatewayPredicateDefinition();
        gatewayPredicateDefinition.setName("ReadBody");
        return Arrays.asList(gatewayPredicateDefinition, this.buildNameVersionPredicateDefinition(apiMeta));
    }

    protected GatewayPredicateDefinition buildNameVersionPredicateDefinition(ServiceApiInfo.ApiMeta apiMeta) {
        return new GatewayPredicateDefinition(String.format(QUERY_PREDICATE_DEFINITION_TPL, apiMeta.fetchNameVersion()));
    }

    /**
     * 添加com.gitee.sop.gatewaycommon.routeDefinition.ReadBodyRoutePredicateFactory,解决form表单获取不到问题
     *
     * @return 返回路由定义
     */
    protected GatewayRouteDefinition buildReadBodyRouteDefinition(ServiceApiInfo serviceApiInfo) {
        GatewayRouteDefinition readBodyRouteDefinition = this.buildGatewayRouteDefinition(serviceApiInfo, FIRST_API_META);
        readBodyRouteDefinition.setOrder(Integer.MIN_VALUE);

        readBodyRouteDefinition.setPredicates(this.buildPredicates(FIRST_API_META));

        return readBodyRouteDefinition;
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
        servletPath = StringUtils.trimLeadingCharacter(servletPath, '/');
        return contextPath + servletPath;
    }

    private void checkPath(String path, String errorMsg) {
        if (path.contains(PATH_SPLIT)) {
            throw new IllegalArgumentException(errorMsg);
        }
    }
}
