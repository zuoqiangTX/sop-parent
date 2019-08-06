package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.zuul.loadbalancer.ServiceGrayConfig;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author tanghc
 */
public class DefaultEnvGrayManager implements EnvGrayManager {

    /**
     * KEY:instanceId
     */
    private Map<String, ServiceGrayConfig> serviceUserKeyMap = Maps.newConcurrentMap();

    private Map<String, List<String>> serviceInstanceIdMap = Maps.newConcurrentMap();

    @Override
    public void addServiceInstance(String serviceId, String instanceId) {
        List<String> instanceIdList = serviceInstanceIdMap.computeIfAbsent(serviceId, key -> new ArrayList<>());
        instanceIdList.add(instanceId);
    }

    @Override
    public List<String> listGrayInstanceId(String serviceId) {
        return serviceInstanceIdMap.get(serviceId);
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
            serviceGrayConfig.setUserKeys(Sets.newConcurrentHashSet());
            serviceGrayConfig.setGrayNameVersion(Maps.newConcurrentMap());
            return serviceGrayConfig;
        });
    }

    @Override
    public void load() {

    }
}
