package com.gitee.sop.gatewaycommon.manager;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.gatewaycommon.route.DynamicRouteServiceManager;
import com.gitee.sop.gatewaycommon.route.GatewayFilterDefinition;
import com.gitee.sop.gatewaycommon.route.GatewayPredicateDefinition;
import com.gitee.sop.gatewaycommon.route.GatewayRouteDefinition;
import com.gitee.sop.gatewaycommon.route.ServiceRouteInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 从zookeeper监听route信息
 *
 * @author tanghc
 */
@Getter
@Slf4j
public class GatewayZookeeperApiMetaManager implements ApiMetaManager {

    private String sopServiceApiPath = "/sop-service-api";

    private Environment environment;

    private DynamicRouteServiceManager dynamicRouteServiceManager;


    public GatewayZookeeperApiMetaManager(Environment environment, DynamicRouteServiceManager dynamicRouteServiceManager) {
        this.environment = environment;
        this.dynamicRouteServiceManager = dynamicRouteServiceManager;
    }

    @Override
    public void refresh() {
        log.info("刷新本地接口信息");
        try {
            String zookeeperServerAddr = environment.getProperty("spring.cloud.zookeeper.connect-string");
            if (StringUtils.isEmpty(zookeeperServerAddr)) {
                throw new RuntimeException("未指定spring.cloud.zookeeper.connect-string参数");
            }
            CuratorFramework client = CuratorFrameworkFactory.builder()
                    .connectString(zookeeperServerAddr)
                    .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                    .build();

            client.start();

            client.create()
                    // 如果节点存在则Curator将会使用给出的数据设置这个节点的值
                    .orSetData()
                    // 如果指定节点的父节点不存在，则Curator将会自动级联创建父节点
                    .creatingParentContainersIfNeeded()
                    .forPath(sopServiceApiPath, "".getBytes());

            this.watchChildren(client, sopServiceApiPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void watchChildren(CuratorFramework client, String sopServiceApiPath) throws Exception {
        // 为子节点添加watcher
        // PathChildrenCache: 监听数据节点的增删改，可以设置触发的事件
        final PathChildrenCache childrenCache = new PathChildrenCache(client, sopServiceApiPath, true);

        /**
         * StartMode: 初始化方式
         * POST_INITIALIZED_EVENT：异步初始化，初始化之后会触发事件
         * NORMAL：异步初始化
         * BUILD_INITIAL_CACHE：同步初始化
         */
        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);

        // 列出子节点数据列表，需要使用BUILD_INITIAL_CACHE同步初始化模式才能获得，异步是获取不到的
        List<ChildData> childDataList = childrenCache.getCurrentData();
        log.info("微服务API详细数据列表：");
        for (ChildData childData : childDataList) {
            String nodeData = new String(childData.getData());
            log.info("\t* 子节点路径：" + childData.getPath() + "，该节点的数据为：" + nodeData);
            ServiceRouteInfo serviceRouteInfo = JSON.parseObject(nodeData, ServiceRouteInfo.class);
            for (GatewayRouteDefinition gatewayRouteDefinition : serviceRouteInfo.getRouteDefinitionList()) {
                RouteDefinition routeDefinition = buildRouteDefinition(gatewayRouteDefinition);
                dynamicRouteServiceManager.add(routeDefinition);
            }
        }
        // 添加事件监听器
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                PathChildrenCacheEvent.Type type = event.getType();
                // 通过判断event type的方式来实现不同事件的触发
                if (PathChildrenCacheEvent.Type.CHILD_ADDED.equals(type)) {
                    String nodeData = new String(event.getData().getData());
                    ServiceRouteInfo serviceRouteInfo = JSON.parseObject(nodeData, ServiceRouteInfo.class);
                    // 添加子节点时触发
                    log.info("子节点：{}添加成功，数据为:{}", event.getData().getPath(), nodeData);
                    for (GatewayRouteDefinition gatewayRouteDefinition : serviceRouteInfo.getRouteDefinitionList()) {
                        RouteDefinition routeDefinition = buildRouteDefinition(gatewayRouteDefinition);
                        dynamicRouteServiceManager.add(routeDefinition);
                    }
                } else if (PathChildrenCacheEvent.Type.CHILD_UPDATED.equals(type)) {
                    String nodeData = new String(event.getData().getData());
                    ServiceRouteInfo serviceRouteInfo = JSON.parseObject(nodeData, ServiceRouteInfo.class);
                    // 修改子节点数据时触发
                    log.info("子节点：{}修改成功，数据为:{}", event.getData().getPath(), nodeData);
                    for (GatewayRouteDefinition gatewayRouteDefinition : serviceRouteInfo.getRouteDefinitionList()) {
                        RouteDefinition routeDefinition = buildRouteDefinition(gatewayRouteDefinition);
                        dynamicRouteServiceManager.update(routeDefinition);
                    }
                } else if (PathChildrenCacheEvent.Type.CHILD_REMOVED.equals(type)) {
                    String nodeData = new String(event.getData().getData());
                    ServiceRouteInfo serviceRouteInfo = JSON.parseObject(nodeData, ServiceRouteInfo.class);
                    // 删除子节点时触发
                    log.info("子节点：{}删除成功，数据为:{}", event.getData().getPath(), nodeData);
                    for (GatewayRouteDefinition gatewayRouteDefinition : serviceRouteInfo.getRouteDefinitionList()) {
                        dynamicRouteServiceManager.delete(gatewayRouteDefinition.getId());
                    }
                }
            }
        });
    }

    protected RouteDefinition buildRouteDefinition(GatewayRouteDefinition gatewayRouteDefinition) {
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId(gatewayRouteDefinition.getId());
        routeDefinition.setUri(URI.create(gatewayRouteDefinition.getUri()));
        routeDefinition.setOrder(gatewayRouteDefinition.getOrder());
        List<FilterDefinition> filterDefinitionList = new ArrayList<>(gatewayRouteDefinition.getFilters().size());
        List<PredicateDefinition> predicateDefinitionList = new ArrayList<>(gatewayRouteDefinition.getPredicates().size());
        for (GatewayFilterDefinition filter : gatewayRouteDefinition.getFilters()) {
            FilterDefinition filterDefinition = new FilterDefinition();
            BeanUtils.copyProperties(filter, filterDefinition);
            filterDefinitionList.add(filterDefinition);
        }

        for (GatewayPredicateDefinition predicate : gatewayRouteDefinition.getPredicates()) {
            PredicateDefinition predicateDefinition = new PredicateDefinition();
            BeanUtils.copyProperties(predicate, predicateDefinition);
            predicateDefinitionList.add(predicateDefinition);
        }

        routeDefinition.setFilters(filterDefinitionList);
        routeDefinition.setPredicates(predicateDefinitionList);
        return routeDefinition;
    }

    @Override
    public void onChange(String serviceApiInfoJson) {
    }

}
