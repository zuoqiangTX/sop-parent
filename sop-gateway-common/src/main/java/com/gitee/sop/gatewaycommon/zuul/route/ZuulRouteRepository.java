package com.gitee.sop.gatewaycommon.zuul.route;

import com.gitee.sop.gatewaycommon.bean.ServiceRouteRepository;
import com.gitee.sop.gatewaycommon.manager.RouteRepository;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import org.springframework.cloud.netflix.zuul.filters.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tanghc
 */
public class ZuulRouteRepository implements RouteRepository<ZuulServiceRouteInfo, Route> {
    /**
     * key：nameVersion
     */
    private Map<String, Route> nameVersionServiceIdMap = new ConcurrentHashMap<>(128);

    private ServiceRouteRepository<ZuulServiceRouteInfo, Route> serviceRouteRepository = new ServiceRouteRepository<ZuulServiceRouteInfo, Route>() {
        @Override
        public String getServiceId(ZuulServiceRouteInfo serviceRouteInfo) {
            return serviceRouteInfo.getAppName();
        }
    };

    public List<Route> listAll() {
        return new ArrayList<>(nameVersionServiceIdMap.values());
    }

    @Override
    public Route get(String id) {
        Route route = nameVersionServiceIdMap.get(id);
        if (route == null) {
            throw ErrorEnum.ISV_INVALID_METHOD.getErrorMeta().getException();
        }
        return route;
    }

    @Override
    public String add(ZuulServiceRouteInfo serviceRouteInfo, Route route) {
        nameVersionServiceIdMap.put(route.getId(), route);
        serviceRouteRepository.saveRouteDefinition(serviceRouteInfo, route);
        return null;
    }

    @Override
    public void deleteAll(ZuulServiceRouteInfo serviceRouteInfo) {
        serviceRouteRepository.deleteAll(serviceRouteInfo, route -> {
            this.delete(route.getId());
        });
    }

    @Override
    public void delete(String id) {
        nameVersionServiceIdMap.remove(id);
    }
}
