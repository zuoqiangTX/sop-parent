package com.gitee.sop.adminserver.api.service;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingService;
import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;
import com.gitee.sop.adminserver.api.service.param.ServiceAddParam;
import com.gitee.sop.adminserver.api.service.param.ServiceGrayConfigParam;
import com.gitee.sop.adminserver.api.service.param.ServiceIdParam;
import com.gitee.sop.adminserver.api.service.param.ServiceInstanceParam;
import com.gitee.sop.adminserver.api.service.param.ServiceSearchParam;
import com.gitee.sop.adminserver.api.service.result.RouteServiceInfo;
import com.gitee.sop.adminserver.api.service.result.ServiceInfoVo;
import com.gitee.sop.adminserver.api.service.result.ServiceInstanceVO;
import com.gitee.sop.adminserver.bean.ChannelMsg;
import com.gitee.sop.adminserver.bean.MetadataEnum;
import com.gitee.sop.adminserver.bean.NacosConfigs;
import com.gitee.sop.adminserver.bean.ServiceGrayDefinition;
import com.gitee.sop.adminserver.common.BizException;
import com.gitee.sop.adminserver.common.ChannelOperation;
import com.gitee.sop.adminserver.common.StatusEnum;
import com.gitee.sop.adminserver.entity.ConfigGray;
import com.gitee.sop.adminserver.entity.ConfigGrayInstance;
import com.gitee.sop.adminserver.mapper.ConfigGrayInstanceMapper;
import com.gitee.sop.adminserver.mapper.ConfigGrayMapper;
import com.gitee.sop.adminserver.service.ConfigPushService;
import com.gitee.sop.registryapi.bean.ServiceInfo;
import com.gitee.sop.registryapi.bean.ServiceInstance;
import com.gitee.sop.registryapi.service.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@ApiService
@ApiDoc("服务管理-服务列表")
@Slf4j
public class ServiceApi {


    @Autowired
    private RegistryService registryService;

    @Autowired
    private ConfigGrayMapper configGrayMapper;

    @Autowired
    private ConfigGrayInstanceMapper configGrayInstanceMapper;

    @Autowired
    private ConfigPushService configPushService;

    @NacosInjected
    private NamingService namingService;

    @Api(name = "registry.service.list")
    @ApiDocMethod(description = "注册中心的服务列表", elementClass = RouteServiceInfo.class)
    List<RouteServiceInfo> listServiceInfo(ServiceSearchParam param) {
        List<ServiceInfo> servicesOfServer = null;
        try {
            servicesOfServer = registryService.listAllService(1, Integer.MAX_VALUE);
        } catch (Exception e) {
            log.error("获取服务列表失败", e);
            throw new BizException("获取服务列表失败");
        }

        return servicesOfServer
                .stream()
                .filter(serviceInfo -> {
                    String serviceId = serviceInfo.getServiceId();
                    if ("api-gateway".equalsIgnoreCase(serviceId)) {
                        return false;
                    }
                    // 隐藏空服务
                    if (CollectionUtils.isEmpty(serviceInfo.getInstances())) {
                        return false;
                    }
                    if (StringUtils.isBlank(param.getServiceId())) {
                        return true;
                    } else {
                        return serviceId.contains(param.getServiceId());
                    }
                })
                .map(serviceInfo -> {
                    RouteServiceInfo routeServiceInfo = new RouteServiceInfo();
                    routeServiceInfo.setServiceId(serviceInfo.getServiceId());
                    return routeServiceInfo;
                })
                .collect(Collectors.toList());
    }

    @Api(name = "service.custom.add")
    @ApiDocMethod(description = "添加服务")
    void addService(ServiceAddParam param) {
        // TODO: 添加服务
        throw new BizException("该功能已下线");
    }

    @Api(name = "service.custom.del")
    @ApiDocMethod(description = "删除自定义服务")
    void delService(ServiceSearchParam param) {
        // TODO: 删除自定义服务
        throw new BizException("该功能已下线");
    }

    @Api(name = "service.instance.list")
    @ApiDocMethod(description = "获取注册中心的服务列表", elementClass = ServiceInfoVo.class)
    List<ServiceInstanceVO> listService(ServiceSearchParam param) {
        List<ServiceInfo> serviceInfos;
        try {
            serviceInfos = registryService.listAllService(1, Integer.MAX_VALUE);
        } catch (Exception e) {
            log.error("获取服务实例失败", e);
            return Collections.emptyList();
        }
        List<ServiceInstanceVO> serviceInfoVoList = new ArrayList<>();
        AtomicInteger idGen = new AtomicInteger(1);
        serviceInfos.stream()
                .filter(serviceInfo -> {
                    if (StringUtils.isBlank(param.getServiceId())) {
                        return true;
                    }
                    return StringUtils.containsIgnoreCase(serviceInfo.getServiceId(), param.getServiceId());
                })
                .forEach(serviceInfo -> {
                    int pid = idGen.getAndIncrement();
                    String serviceId = serviceInfo.getServiceId();
                    ServiceInstanceVO parent = new ServiceInstanceVO();
                    parent.setId(pid);
                    parent.setServiceId(serviceId);
                    parent.setParentId(0);
                    serviceInfoVoList.add(parent);
                    List<ServiceInstance> instanceList = serviceInfo.getInstances();
                    for (ServiceInstance instance : instanceList) {
                        ServiceInstanceVO instanceVO = new ServiceInstanceVO();
                        BeanUtils.copyProperties(instance, instanceVO);
                        int id = idGen.getAndIncrement();
                        instanceVO.setId(id);
                        instanceVO.setParentId(pid);
                        if (instanceVO.getMetadata() == null) {
                            instanceVO.setMetadata(Collections.emptyMap());
                        }
                        serviceInfoVoList.add(instanceVO);
                    }
                });

        return serviceInfoVoList;
    }

    @Api(name = "service.instance.offline")
    @ApiDocMethod(description = "服务禁用")
    void serviceOffline(ServiceInstanceParam param) {
        try {
            registryService.offlineInstance(param.buildServiceInstance());
        } catch (Exception e) {
            log.error("服务禁用失败，param:{}", param, e);
            throw new BizException("服务禁用失败，请查看日志");
        }
    }

    @Api(name = "service.instance.online")
    @ApiDocMethod(description = "服务启用")
    void serviceOnline(ServiceInstanceParam param) throws IOException {
        try {
            registryService.onlineInstance(param.buildServiceInstance());
        } catch (Exception e) {
            log.error("服务启用失败，param:{}", param, e);
            throw new BizException("服务启用失败，请查看日志");
        }
    }

    @Api(name = "service.instance.env.pre.open")
    @ApiDocMethod(description = "预发布")
    void serviceEnvPre(ServiceInstanceParam param) throws IOException {
        try {
            MetadataEnum envPre = MetadataEnum.ENV_PRE;
            registryService.setMetadata(param.buildServiceInstance(), envPre.getKey(), envPre.getValue());
        } catch (Exception e) {
            log.error("预发布失败，param:{}", param, e);
            throw new BizException("预发布失败，请查看日志");
        }
    }

    @Api(name = "service.gray.config.get")
    @ApiDocMethod(description = "灰度配置--获取")
    ConfigGray serviceEnvGrayConfigGet(ServiceIdParam param) throws IOException {
        return this.getConfigGray(param.getServiceId());
    }

    @Api(name = "service.gray.config.save")
    @ApiDocMethod(description = "灰度配置--保存")
    void serviceEnvGrayConfigSave(ServiceGrayConfigParam param) throws IOException {
        String serviceId = param.getServiceId().toLowerCase();
        ConfigGray configGray = configGrayMapper.getByColumn("service_id", serviceId);
        if (configGray == null) {
            configGray = new ConfigGray();
            configGray.setServiceId(serviceId);
            configGray.setNameVersionContent(param.getNameVersionContent());
            configGray.setUserKeyContent(param.getUserKeyContent());
            configGrayMapper.save(configGray);
        } else {
            configGray.setNameVersionContent(param.getNameVersionContent());
            configGray.setUserKeyContent(param.getUserKeyContent());
            configGrayMapper.update(configGray);
        }
        this.sendServiceGrayMsg(serviceId, ChannelOperation.GRAY_USER_KEY_SET);
    }

    @Api(name = "service.instance.env.gray.open")
    @ApiDocMethod(description = "开启灰度发布")
    void serviceEnvGray(ServiceInstanceParam param) throws IOException {
        String serviceId = param.getServiceId().toLowerCase();
        ConfigGray configGray = this.getConfigGray(serviceId);
        if (configGray == null) {
            throw new BizException("请先设置灰度参数");
        }
        try {
            MetadataEnum envPre = MetadataEnum.ENV_GRAY;
            registryService.setMetadata(param.buildServiceInstance(), envPre.getKey(), envPre.getValue());

            String instanceId = param.getInstanceId();

            ConfigGrayInstance configGrayInstance = configGrayInstanceMapper.getByColumn("instance_id", instanceId);
            if (configGrayInstance == null) {
                configGrayInstance = new ConfigGrayInstance();
                configGrayInstance.setServiceId(serviceId);
                configGrayInstance.setInstanceId(instanceId);
                configGrayInstance.setStatus(StatusEnum.STATUS_ENABLE.getStatus());
                configGrayInstanceMapper.save(configGrayInstance);
            } else {
                configGrayInstance.setStatus(StatusEnum.STATUS_ENABLE.getStatus());
                configGrayInstance.setServiceId(serviceId);
                configGrayInstanceMapper.update(configGrayInstance);
            }
            this.sendServiceGrayMsg(instanceId, serviceId, ChannelOperation.GRAY_USER_KEY_OPEN);
        } catch (Exception e) {
            log.error("灰度发布失败，param:{}", param, e);
            throw new BizException("灰度发布失败，请查看日志");
        }
    }

    @Api(name = "service.instance.env.online")
    @ApiDocMethod(description = "上线")
    void serviceEnvOnline(ServiceInstance param) throws IOException {
        try {
            MetadataEnum envPre = MetadataEnum.ENV_ONLINE;
            registryService.setMetadata(param, envPre.getKey(), envPre.getValue());

            ConfigGrayInstance configGrayInstance = configGrayInstanceMapper.getByColumn("instance_id", param.getInstanceId());
            if (configGrayInstance != null) {
                configGrayInstance.setStatus(StatusEnum.STATUS_DISABLE.getStatus());
                configGrayInstanceMapper.update(configGrayInstance);
                this.sendServiceGrayMsg(param.getInstanceId(), param.getServiceId().toLowerCase(), ChannelOperation.GRAY_USER_KEY_CLOSE);
            }
        } catch (Exception e) {
            log.error("上线失败，param:{}", param, e);
            throw new BizException("上线失败，请查看日志");
        }
    }

    private void sendServiceGrayMsg(String serviceId, ChannelOperation channelOperation) {
        this.sendServiceGrayMsg(null, serviceId, channelOperation);
    }

    private void sendServiceGrayMsg(String instanceId, String serviceId, ChannelOperation channelOperation) {
        ServiceGrayDefinition serviceGrayDefinition = new ServiceGrayDefinition();
        serviceGrayDefinition.setInstanceId(instanceId);
        serviceGrayDefinition.setServiceId(serviceId);
        ChannelMsg channelMsg = new ChannelMsg(channelOperation, serviceGrayDefinition);
        configPushService.publishConfig(NacosConfigs.DATA_ID_GRAY, NacosConfigs.GROUP_CHANNEL, channelMsg);
    }

    private ConfigGray getConfigGray(String serviceId) {
        return configGrayMapper.getByColumn("service_id", serviceId);
    }

}
