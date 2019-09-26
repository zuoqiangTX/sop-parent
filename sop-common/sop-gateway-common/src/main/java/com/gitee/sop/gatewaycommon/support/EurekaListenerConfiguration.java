package com.gitee.sop.gatewaycommon.support;

import com.gitee.sop.gatewaycommon.route.EurekaRoutesListener;
import com.gitee.sop.gatewaycommon.route.RegistryListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

/**
 * eureka注册中心事件监听
 * @author tanghc
 */
public class EurekaListenerConfiguration {

    @Autowired
    private RegistryListener registryListener;

    /**
     * 客户端注册触发
     * @param event
     */
    @EventListener
    public void onEurekaInstanceRegisteredEvent(EurekaInstanceRegisteredEvent event) {
        registryListener.onRegister(event);
    }

    /**
     * 客户端下线触发
     * @param event
     */
    @EventListener
    public void onEurekaInstanceCanceledEvent(EurekaInstanceCanceledEvent event) {
        registryListener.onDeregister(event);
    }

    @Bean
    RegistryListener registryListener() {
        return new EurekaRoutesListener();
    }

}
