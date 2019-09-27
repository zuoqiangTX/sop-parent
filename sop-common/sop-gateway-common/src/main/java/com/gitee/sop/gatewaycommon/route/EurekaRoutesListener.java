package com.gitee.sop.gatewaycommon.route;

import com.gitee.sop.gatewaycommon.bean.InstanceDefinition;
import com.gitee.sop.gatewaycommon.loadbalancer.EurekaEnvironmentServerChooser;
import com.gitee.sop.gatewaycommon.loadbalancer.SopPropertiesFactory;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.eureka.CloudEurekaClient;
import org.springframework.context.ApplicationEvent;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 加载服务路由，eureka实现
 *
 * @author tanghc
 */
public class EurekaRoutesListener extends BaseRoutesListener {

    static {
        System.setProperty(SopPropertiesFactory.PROPERTIES_KEY, EurekaEnvironmentServerChooser.class.getName());
    }

    private Set<String> cacheServices = new HashSet<>();

    @Value("${spring.application.name:api-gateway}")
    private String appName;

    @Override
    public void onEvent(ApplicationEvent applicationEvent) {
        Object source = applicationEvent.getSource();
        CloudEurekaClient cloudEurekaClient = (CloudEurekaClient) source;
        Applications applications = cloudEurekaClient.getApplications();
        List<Application> registeredApplications = applications.getRegisteredApplications();
        List<String> serviceList = registeredApplications
                .stream()
                .map(Application::getName)
                .collect(Collectors.toList());

        Set<String> currentServices = new HashSet<>(serviceList);
        currentServices.removeAll(cacheServices);
        // 如果有新的服务注册进来
        if (currentServices.size() > 0) {
            this.doRegister(registeredApplications, currentServices);
        }

        currentServices = new HashSet<>(serviceList);
        cacheServices.removeAll(currentServices);
        // 如果有服务删除
        if (cacheServices.size() > 0) {
            this.doRemove(cacheServices);
        }

        cacheServices = new HashSet<>(serviceList);
    }

    private void doRegister(List<Application> registeredApplications, Set<String> newServices) {
        registeredApplications
                .stream()
                .filter(application -> !appName.equalsIgnoreCase(application.getName())
                        && newServices.contains(application.getName()))
                .forEach(application -> {
                    List<InstanceInfo> instances = application.getInstances();
                    if (CollectionUtils.isNotEmpty(instances)) {
                        instances.sort(Comparator.comparing(InstanceInfo::getLastUpdatedTimestamp).reversed());
                        InstanceInfo instanceInfo = instances.get(0);
                        InstanceDefinition instanceDefinition = new InstanceDefinition();
                        instanceDefinition.setInstanceId(instanceInfo.getInstanceId());
                        instanceDefinition.setServiceId(instanceInfo.getAppName());
                        instanceDefinition.setIp(instanceInfo.getIPAddr());
                        instanceDefinition.setPort(instanceInfo.getPort());
                        instanceDefinition.setMetadata(instanceInfo.getMetadata());
                        pullRoutes(instanceDefinition);
                    }
                });
    }

    private void doRemove(Set<String> deletedServices) {
        deletedServices.forEach(this::removeRoutes);
    }

}
