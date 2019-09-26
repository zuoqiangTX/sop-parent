package com.gitee.sop.gatewaycommon.route;

import com.gitee.sop.gatewaycommon.bean.InstanceDefinition;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.context.ApplicationEvent;

/**
 * 加载服务路由，eureka实现
 *
 * @author tanghc
 */
public class EurekaRoutesListener extends BaseRoutesListener {

    @Override
    public void onRegister(ApplicationEvent applicationEvent) {
        EurekaInstanceRegisteredEvent event = (EurekaInstanceRegisteredEvent)applicationEvent;
        InstanceInfo instanceInfo = event.getInstanceInfo();
        InstanceDefinition instanceDefinition = new InstanceDefinition();
        instanceDefinition.setInstanceId(instanceInfo.getInstanceId());
        instanceDefinition.setServiceId(instanceInfo.getAppName());
        instanceDefinition.setIp(instanceInfo.getIPAddr());
        instanceDefinition.setPort(instanceInfo.getPort());
        instanceDefinition.setMetadata(instanceInfo.getMetadata());
        pullRoutes(instanceDefinition);
    }

    @Override
    public void onDeregister(ApplicationEvent applicationEvent) {
        EurekaInstanceCanceledEvent event = (EurekaInstanceCanceledEvent)applicationEvent;
        removeRoutes(event.getServerId());
    }
}
