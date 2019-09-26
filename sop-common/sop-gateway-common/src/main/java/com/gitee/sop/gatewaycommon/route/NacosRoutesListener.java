package com.gitee.sop.gatewaycommon.route;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.gitee.sop.gatewaycommon.bean.InstanceDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.context.ApplicationEvent;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * 加载服务路由，nacos实现
 *
 * @author tanghc
 */
@Slf4j
public class NacosRoutesListener extends BaseRoutesListener {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @NacosInjected
    private ConfigService configService;

    @Override
    public void onRegister(ApplicationEvent applicationEvent) {
        NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
        List<ServiceInfo> subscribes = null;
        try {
            subscribes = namingService.getSubscribeServices();
        } catch (NacosException e) {
            log.error("namingService.getSubscribeServices()错误", e);
        }
        if (CollectionUtils.isEmpty(subscribes)) {
            return;
        }
        // subscribe
        String thisServiceId = nacosDiscoveryProperties.getService();
        for (ServiceInfo serviceInfo : subscribes) {
            String serviceName = serviceInfo.getName();
            // 如果是本机服务，跳过
            if (Objects.equals(thisServiceId, serviceName)) {
                continue;
            }
            try {
                List<Instance> allInstances = namingService.getAllInstances(serviceName);
                if (CollectionUtils.isEmpty(allInstances)) {
                    // 如果没有服务列表，则删除所有路由信息
                    removeRoutes(serviceName);
                } else {
                    for (Instance instance : allInstances) {
                        InstanceDefinition instanceDefinition = new InstanceDefinition();
                        instanceDefinition.setInstanceId(instance.getInstanceId());
                        instanceDefinition.setServiceId(serviceName);
                        instanceDefinition.setIp(instance.getIp());
                        instanceDefinition.setPort(instance.getPort());
                        instanceDefinition.setMetadata(instance.getMetadata());
                        pullRoutes(instanceDefinition);
                    }
                }
            } catch (Exception e) {
                log.error("选择服务实例失败，serviceName: {}", serviceName, e);
            }
        }
    }

    @Override
    public void onDeregister(ApplicationEvent applicationEvent) {

    }

}
