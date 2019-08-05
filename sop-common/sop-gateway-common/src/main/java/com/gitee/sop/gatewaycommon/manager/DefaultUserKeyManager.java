package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.zuul.loadbalancer.ServiceGrayConfig;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;

/**
 * @author tanghc
 */
public class DefaultUserKeyManager implements UserKeyManager {

    /**
     * KEY:instanceId
     */
    private Map<String, ServiceGrayConfig> serviceUserKeyMap = Maps.newConcurrentMap();

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
        ServiceGrayConfig serviceGrayConfig = serviceUserKeyMap.get(instanceId);
        if (serviceGrayConfig == null) {
            serviceGrayConfig = new ServiceGrayConfig();
            serviceGrayConfig.setUserKeys(Sets.newConcurrentHashSet());
            serviceGrayConfig.setGrayNameVersion(Maps.newConcurrentMap());
            serviceUserKeyMap.put(instanceId, serviceGrayConfig);
        }
        return serviceGrayConfig;
    }

    @Override
    public void load() {

    }
}
