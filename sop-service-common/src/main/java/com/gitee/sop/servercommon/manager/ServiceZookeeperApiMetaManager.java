package com.gitee.sop.servercommon.manager;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.servercommon.bean.ServiceApiInfo;
import com.gitee.sop.servercommon.route.GatewayPredicateDefinition;
import com.gitee.sop.servercommon.route.GatewayRouteDefinition;
import com.gitee.sop.servercommon.route.ServiceRouteInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author tanghc
 */
@Slf4j
@Getter
@Setter
public class ServiceZookeeperApiMetaManager implements ApiMetaManager {


    /**
     * NameVersion=alipay.story.get1.0
     * see com.gitee.sop.gatewaycommon.route.NameVersionRoutePredicateFactory
     */
    private static String QUERY_PREDICATE_DEFINITION_TPL = "NameVersion=%s";

    private static ServiceApiInfo.ApiMeta FIRST_API_META = new ServiceApiInfo.ApiMeta("_" + System.currentTimeMillis() + "_", "/", "0.0");

    private String sopServiceApiPath = "/sop-service-api";

    private Environment environment;

    public ServiceZookeeperApiMetaManager(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void uploadApi(ServiceApiInfo serviceApiInfo) {
        ServiceRouteInfo serviceRouteInfo = this.buildServiceGatewayInfo(serviceApiInfo);
        this.uploadServiceRouteInfoToZookeeper(serviceRouteInfo);
    }

    /**
     * 构建接口信息，符合spring cloud gateway的格式
     *
     * @param serviceApiInfo
     * @return
     */
    protected ServiceRouteInfo buildServiceGatewayInfo(ServiceApiInfo serviceApiInfo) {
        List<ServiceApiInfo.ApiMeta> apis = serviceApiInfo.getApis();
        List<GatewayRouteDefinition> routeDefinitionList = new ArrayList<>(apis.size() + 1);
        routeDefinitionList.add(this.buildReadBodyRouteDefinition(serviceApiInfo));
        for (ServiceApiInfo.ApiMeta apiMeta : apis) {
            GatewayRouteDefinition gatewayRouteDefinition = this.buildGatewayRouteDefinition(serviceApiInfo, apiMeta);
            routeDefinitionList.add(gatewayRouteDefinition);
        }
        ServiceRouteInfo serviceRouteInfo = new ServiceRouteInfo();
        serviceRouteInfo.setAppName(serviceApiInfo.getAppName());
        serviceRouteInfo.setRouteDefinitionList(routeDefinitionList);
        serviceRouteInfo.setMd5(serviceApiInfo.getMd5());
        return serviceRouteInfo;
    }

    /**
     * 添加com.gitee.sop.gatewaycommon.route.ReadBodyRoutePredicateFactory,解决form表单获取不到问题
     *
     * @return
     */
    protected GatewayRouteDefinition buildReadBodyRouteDefinition(ServiceApiInfo serviceApiInfo) {
        GatewayRouteDefinition gatewayRouteDefinition = this.buildGatewayRouteDefinition(serviceApiInfo, FIRST_API_META);

        GatewayPredicateDefinition gatewayPredicateDefinition = new GatewayPredicateDefinition();
        gatewayPredicateDefinition.setName("ReadBody");
        GatewayPredicateDefinition readerBodyPredicateDefinition = this.buildNameVersionPredicateDefinition(FIRST_API_META);
        List<GatewayPredicateDefinition> predicates = Arrays.asList(gatewayPredicateDefinition, readerBodyPredicateDefinition);
        gatewayRouteDefinition.setPredicates(predicates);

        return gatewayRouteDefinition;
    }

    protected GatewayRouteDefinition buildGatewayRouteDefinition(ServiceApiInfo serviceApiInfo, ServiceApiInfo.ApiMeta apiMeta) {
        GatewayRouteDefinition gatewayRouteDefinition = new GatewayRouteDefinition();
        // 唯一id规则：接口名 + 版本号
        gatewayRouteDefinition.setId(apiMeta.fetchNameVersion());
        gatewayRouteDefinition.setFilters(Collections.emptyList());
        gatewayRouteDefinition.setOrder(0);
        List<GatewayPredicateDefinition> predicates = Arrays.asList(this.buildNameVersionPredicateDefinition(apiMeta));
        gatewayRouteDefinition.setPredicates(predicates);
        // lb://story-service#/alipay.story.get/
        String servletPath = "#" + apiMeta.getPath();
        gatewayRouteDefinition.setUri("lb://" + serviceApiInfo.getAppName() + servletPath);
        return gatewayRouteDefinition;
    }

    protected GatewayPredicateDefinition buildNameVersionPredicateDefinition(ServiceApiInfo.ApiMeta apiMeta) {
        return new GatewayPredicateDefinition(String.format(QUERY_PREDICATE_DEFINITION_TPL, apiMeta.fetchNameVersion()));
    }

    /**
     * 上传接口信息到zookeeper
     *
     * @param serviceRouteInfo
     */
    protected void uploadServiceRouteInfoToZookeeper(ServiceRouteInfo serviceRouteInfo) {
        String zookeeperServerAddr = environment.getProperty("spring.cloud.zookeeper.connect-string");
        if (StringUtils.isEmpty(zookeeperServerAddr)) {
            throw new RuntimeException("未指定spring.cloud.zookeeper.connect-string参数");
        }
        String serviceRouteInfoJson = JSON.toJSONString(serviceRouteInfo);
        CuratorFramework client = null;
        try {
            // 保存路径
            String savePath = sopServiceApiPath + "/" + serviceRouteInfo.getAppName();

            client = CuratorFrameworkFactory.builder()
                    .connectString(zookeeperServerAddr)
                    .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                    .build();

            client.start();

            log.info("上传接口信息到zookeeper，path:{}, appName：{}, md5：{}, 接口数量：{}",
                    savePath,
                    serviceRouteInfo.getAppName(),
                    serviceRouteInfo.getMd5(),
                    serviceRouteInfo.getRouteDefinitionList().size());

            client.create()
                    // 如果节点存在则Curator将会使用给出的数据设置这个节点的值
                    .orSetData()
                    // 如果指定节点的父节点不存在，则Curator将会自动级联创建父节点
                    .creatingParentContainersIfNeeded()
                    .forPath(savePath, serviceRouteInfoJson.getBytes());
        } catch (Exception e) {
            log.error("更新接口信息到zookeeper失败, appName:{}", serviceRouteInfo.getAppName(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

}
