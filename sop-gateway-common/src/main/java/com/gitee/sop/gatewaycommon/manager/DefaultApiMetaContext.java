package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.ServiceApiInfo;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.Route;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author tanghc
 */
@Slf4j
public class DefaultApiMetaContext implements ApiMetaContext {

    /** key：nameVersion */
    private Map<String, Route> nameVersionServiceIdMap = new HashMap<>(128);

    /** key: serviceId , value: md5 */
    private Map<String, String> serviceIdMd5Map = new HashMap<>(16);

    @Override
    public void reload(String serviceId, ServiceApiInfo serviceApiInfo) {
        String md5 = serviceIdMd5Map.get(serviceId);
        if (md5 != null && md5.equals(serviceApiInfo.getMd5())) {
            log.info("MD5相同，无需更改本地接口信息，appName:{}, md5:{}", serviceApiInfo.getAppName(), serviceApiInfo.getMd5());
            return;
        }
        log.info("更新本地接口信息，appName:{}, md5:{}", serviceApiInfo.getAppName(), serviceApiInfo.getMd5());
        // 移除原来的
        Iterator<Map.Entry<String, Route>> iterator = nameVersionServiceIdMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Route> entry = iterator.next();
            if (entry.getValue().getLocation().equals(serviceId)) {
                iterator.remove();
            }
        }
        List<ServiceApiInfo.ApiMeta> apis = serviceApiInfo.getApis();
        for (ServiceApiInfo.ApiMeta apiMeta : apis) {
            Route route = this.buildRoute(serviceId, apiMeta);
            nameVersionServiceIdMap.put(apiMeta.fetchNameVersion(), route);
        }
        serviceIdMd5Map.put(serviceId, serviceApiInfo.getMd5());
    }

    @Override
    public Route getRoute(String nameVersion) {
        Route route = nameVersionServiceIdMap.get(nameVersion);
        if (route == null) {
            throw ErrorEnum.ISV_INVALID_METHOD.getErrorMeta().getException();
        }
        return route;
    }

    protected Route buildRoute(String serviceId, ServiceApiInfo.ApiMeta apiMeta) {
        return new Route(apiMeta.getName(), apiMeta.getPath(), serviceId, null, false, null);
    }

}
