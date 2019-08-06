package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.zuul.loadbalancer.ServiceGrayConfig;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
public class DefaultEnvGrayManager implements EnvGrayManager {

    /**
     * KEY:instanceId
     */
    private Map<String, ServiceGrayConfig> serviceUserKeyMap = Maps.newConcurrentMap();

    @Override
    public List<String> listGrayInstanceId(String serviceId) {
        return serviceUserKeyMap
                .values()
                .stream()
                .map(ServiceGrayConfig::getInstanceId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean containsKey(String instanceId, Object userKey) {
        if (instanceId == null || userKey == null) {
            return false;
        }
        return this.getServiceGrayConfig(instanceId).containsKey(userKey);
    }

    @Override
    public String getVersion(String instanceId, String nameVersion) {
        if (instanceId == null || nameVersion == null) {
            return null;
        }
        return this.getServiceGrayConfig(instanceId).getVersion(nameVersion);
    }

    @Override
    public ServiceGrayConfig getServiceGrayConfig(String instanceId) {
        return serviceUserKeyMap.computeIfAbsent(instanceId, key -> {
            ServiceGrayConfig serviceGrayConfig = new ServiceGrayConfig();
            serviceGrayConfig.setInstanceId(instanceId);
            serviceGrayConfig.setUserKeys(Sets.newConcurrentHashSet());
            serviceGrayConfig.setGrayNameVersion(Maps.newConcurrentMap());
            return serviceGrayConfig;
        });
    }

    @Override
    public void load() {

    }
}
