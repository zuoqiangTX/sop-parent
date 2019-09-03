package com.gitee.sop.gatewaycommon.manager;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.gatewaycommon.bean.RouteDefinition;
import com.gitee.sop.gatewaycommon.bean.ServiceRouteInfo;
import com.gitee.sop.gatewaycommon.bean.TargetRoute;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author tanghc
 */
@Slf4j
public abstract class BaseRouteCache<T extends TargetRoute> implements RouteLoader {

    /**
     * KEY:serviceId, value: md5
     */
    private Map<String, String> serviceIdMd5Map = new HashMap<>();

    private RouteRepository<T> routeRepository;

    /**
     * 构建目标路由对象，zuul和gateway定义的路由对象
     *
     * @param serviceRouteInfo       路由服务对象
     * @param gatewayRouteDefinition 路由对象
     * @return 返回目标路由对象
     */
    protected abstract T buildTargetRoute(ServiceRouteInfo serviceRouteInfo, RouteDefinition gatewayRouteDefinition);

    public BaseRouteCache(RouteRepository<T> routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public void load(ServiceRouteInfo serviceRouteInfo, Consumer<Object> callback) {
        try {
            String serviceId = serviceRouteInfo.getServiceId();
            String newMd5 = serviceRouteInfo.getMd5();
            String oldMd5 = serviceIdMd5Map.get(serviceId);
            if (Objects.equals(newMd5, oldMd5)) {
                return;
            }
            serviceIdMd5Map.put(serviceId, newMd5);
            List<RouteDefinition> routeDefinitionList = serviceRouteInfo.getRouteDefinitionList();
            for (RouteDefinition routeDefinition : routeDefinitionList) {
                T targetRoute = this.buildTargetRoute(serviceRouteInfo, routeDefinition);
                routeRepository.add(targetRoute);
                if (log.isDebugEnabled()) {
                    log.debug("新增路由：{}", JSON.toJSONString(routeDefinition));
                }
            }
            callback.accept(null);
        } catch (Exception e) {
            log.error("加载路由信息失败，serviceRouteInfo:{}", serviceRouteInfo, e);
        }
    }

    @Override
    public void remove(String serviceId) {
        serviceIdMd5Map.remove(serviceId);
        routeRepository.deleteAll(serviceId);
    }
}
