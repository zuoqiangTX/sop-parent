package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.BeanInitializer;
import com.gitee.sop.gatewaycommon.zuul.loadbalancer.ServiceGrayConfig;

import java.util.List;

/**
 * @author tanghc
 */
public interface EnvGrayManager extends BeanInitializer {

    void addServiceInstance(String serviceId, String instanceId);

    List<String> listGrayInstanceId(String serviceId);

    boolean containsKey(String instanceId, Object userKey);

    String getVersion(String instanceId, String nameVersion);

    ServiceGrayConfig getServiceGrayConfig(String instanceId);
}
