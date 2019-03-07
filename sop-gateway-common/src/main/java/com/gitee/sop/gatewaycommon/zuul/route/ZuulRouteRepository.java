package com.gitee.sop.gatewaycommon.zuul.route;

import com.gitee.sop.gatewaycommon.manager.RouteRepository;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import org.springframework.cloud.netflix.zuul.filters.Route;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tanghc
 */
public class ZuulRouteRepository implements RouteRepository<Route> {
    /** key：nameVersion */
    private Map<String, Route> nameVersionServiceIdMap = new HashMap<>(128);

    @Override
    public Route get(String id) {
        Route route = nameVersionServiceIdMap.get(id);
        if (route == null) {
            throw ErrorEnum.ISV_INVALID_METHOD.getErrorMeta().getException();
        }
        return route;
    }

    @Override
    public String add(Route route) {
        return this.update(route);
    }

    @Override
    public String update(Route route) {
        nameVersionServiceIdMap.put(route.getId(), route);
        return null;
    }

    @Override
    public void delete(String id) {
        nameVersionServiceIdMap.remove(id);
    }
}
