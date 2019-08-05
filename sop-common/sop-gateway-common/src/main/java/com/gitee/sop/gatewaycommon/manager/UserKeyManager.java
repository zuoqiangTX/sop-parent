package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.BeanInitializer;
import com.gitee.sop.gatewaycommon.zuul.loadbalancer.ServiceGrayConfig;

/**
 * @author tanghc
 */
public interface UserKeyManager extends BeanInitializer {
    boolean containsKey(String instanceId, Object userKey);

    String getVersion(String instanceId, String nameVersion);

    ServiceGrayConfig getServiceGrayConfig(String instanceId);
}
