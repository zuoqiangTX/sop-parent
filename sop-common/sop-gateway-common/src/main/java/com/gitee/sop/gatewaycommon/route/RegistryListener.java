package com.gitee.sop.gatewaycommon.route;

import org.springframework.context.ApplicationEvent;

/**
 * 发现新服务，更新路由信息
 *
 * @author tanghc
 */
public interface RegistryListener {

    /**
     * 触发新服务注册
     *
     * @param applicationEvent 事件，可能是nacos事件，也可能是eureka事件
     */
    void onRegister(ApplicationEvent applicationEvent);

    /**
     * 注销服务
     *
     * @param applicationEvent
     */
    void onDeregister(ApplicationEvent applicationEvent);

}
