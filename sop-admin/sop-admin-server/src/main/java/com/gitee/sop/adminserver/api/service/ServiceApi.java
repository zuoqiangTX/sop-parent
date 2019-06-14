package com.gitee.sop.adminserver.api.service;

import com.alibaba.fastjson.JSON;
import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;
import com.gitee.sop.adminserver.api.service.param.ServiceAddParam;
import com.gitee.sop.adminserver.api.service.param.ServiceInstanceParam;
import com.gitee.sop.adminserver.api.service.param.ServiceSearchParam;
import com.gitee.sop.adminserver.api.service.result.ServiceInfo;
import com.gitee.sop.adminserver.api.service.result.ServiceInfoVo;
import com.gitee.sop.adminserver.bean.EurekaApplication;
import com.gitee.sop.adminserver.bean.EurekaApps;
import com.gitee.sop.adminserver.bean.EurekaInstance;
import com.gitee.sop.adminserver.bean.EurekaUri;
import com.gitee.sop.adminserver.bean.ServiceRouteInfo;
import com.gitee.sop.adminserver.bean.ZookeeperContext;
import com.gitee.sop.adminserver.common.BizException;
import com.gitee.sop.adminserver.common.ZookeeperPathExistException;
import com.gitee.sop.adminserver.common.ZookeeperPathNotExistException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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


    OkHttpClient client = new OkHttpClient();

    @Autowired
    private Environment environment;

    private String eurekaUrl;

    @Api(name = "service.list")
    @ApiDocMethod(description = "服务列表（旧）", elementClass = ServiceInfo.class)
    List<ServiceInfo> listServiceInfo(ServiceSearchParam param) throws Exception {
        String routeRootPath = ZookeeperContext.getSopRouteRootPath();
        List<ChildData> childDataList = ZookeeperContext.getChildrenData(routeRootPath);
        List<ServiceInfo> serviceInfoList = childDataList.stream()
                .filter(childData -> childData.getData() != null && childData.getData().length > 0)
                .map(childData -> {
                    String serviceNodeData = new String(childData.getData());
                    ServiceInfo serviceInfo = JSON.parseObject(serviceNodeData, ServiceInfo.class);
                    return serviceInfo;
                })
                .filter(serviceInfo -> {
                    if (StringUtils.isBlank(param.getServiceId())) {
                        return true;
                    } else {
                        return serviceInfo.getServiceId().contains(param.getServiceId());
                    }
                })
                .collect(Collectors.toList());

        return serviceInfoList;
    }

    @Api(name = "service.custom.add")
    @ApiDocMethod(description = "添加服务")
    void addService(ServiceAddParam param) {
        String serviceId = param.getServiceId();
        String servicePath = ZookeeperContext.buildServiceIdPath(serviceId);
        ServiceRouteInfo serviceRouteInfo = new ServiceRouteInfo();
        Date now = new Date();
        serviceRouteInfo.setServiceId(serviceId);
        serviceRouteInfo.setDescription("自定义服务");
        serviceRouteInfo.setCreateTime(now);
        serviceRouteInfo.setUpdateTime(now);
        serviceRouteInfo.setCustom(BooleanUtils.toInteger(true));
        String serviceData = JSON.toJSONString(serviceRouteInfo);
        try {
            ZookeeperContext.addPath(servicePath, serviceData);
        } catch (ZookeeperPathExistException e) {
            throw new BizException("服务已存在");
        }
    }

    @Api(name = "service.custom.del")
    @ApiDocMethod(description = "删除自定义服务")
    void delService(ServiceSearchParam param) {
        String serviceId = param.getServiceId();
        String servicePath = ZookeeperContext.buildServiceIdPath(serviceId);
        String data = null;
        try {
            data = ZookeeperContext.getData(servicePath);
        } catch (ZookeeperPathNotExistException e) {
            throw new BizException("服务不存在");
        }
        if (StringUtils.isBlank(data)) {
            throw new BizException("非自定义服务，无法删除");
        }
        ServiceRouteInfo serviceRouteInfo = JSON.parseObject(data, ServiceRouteInfo.class);
        int custom = serviceRouteInfo.getCustom();
        if (!BooleanUtils.toBoolean(custom)) {
            throw new BizException("非自定义服务，无法删除");
        }
        ZookeeperContext.deletePathDeep(servicePath);
    }

    @Api(name = "service.instance.list")
    @ApiDocMethod(description = "服务列表", elementClass = ServiceInfoVo.class)
    List<ServiceInfoVo> listService(ServiceSearchParam param) throws Exception {
        String json = this.requestEurekaServer(EurekaUri.QUERY_APPS);
        EurekaApps eurekaApps = JSON.parseObject(json, EurekaApps.class);

        List<ServiceInfoVo> serviceInfoVoList = new ArrayList<>();
        List<EurekaApplication> applicationList = eurekaApps.getApplications().getApplication();
        AtomicInteger idGen = new AtomicInteger(1);
        applicationList.stream()
                .filter(eurekaApplication -> {
                    if (StringUtils.isBlank(param.getServiceId())) {
                        return true;
                    }
                    return StringUtils.containsIgnoreCase(eurekaApplication.getName(), param.getServiceId());
                })
                .forEach(eurekaApplication -> {
                    int pid = idGen.getAndIncrement();
                    String name = eurekaApplication.getName();
                    ServiceInfoVo parent = new ServiceInfoVo();
                    parent.setId(pid);
                    parent.setName(name);
                    parent.setParentId(0);
                    serviceInfoVoList.add(parent);
                    List<EurekaInstance> instanceList = eurekaApplication.getInstance();
                    for (EurekaInstance instance : instanceList) {
                        ServiceInfoVo vo = new ServiceInfoVo();
                        BeanUtils.copyProperties(instance, vo);
                        int id = idGen.getAndIncrement();
                        vo.setId(id);
                        vo.setName(name);
                        vo.setParentId(pid);
                        vo.setServerPort(instance.fetchPort());
                        serviceInfoVoList.add(vo);
                    }
                });

        return serviceInfoVoList;
    }

    @Api(name = "service.instance.offline")
    @ApiDocMethod(description = "服务下线")
    void serviceOffline(ServiceInstanceParam param) throws IOException {
        this.requestEurekaServer(EurekaUri.OFFLINE_SERVICE, param.getServiceId(), param.getInstanceId());
    }

    @Api(name = "service.instance.online")
    @ApiDocMethod(description = "服务上线")
    void serviceOnline(ServiceInstanceParam param) throws IOException {
        this.requestEurekaServer(EurekaUri.ONLINE_SERVICE, param.getServiceId(), param.getInstanceId());
    }

    private String requestEurekaServer(EurekaUri eurekaUri, String... args) throws IOException {
        Request request = eurekaUri.getRequest(this.eurekaUrl, args);
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            log.error("操作失败，url:{}, msg:{}, code:{}", eurekaUri.getUri(args), response.message(), response.code());
            throw new BizException("操作失败", String.valueOf(response.code()));
        }
    }

    @PostConstruct
    protected void after() {
        String eurekaUrls = environment.getProperty("eureka.client.serviceUrl.defaultZone");
        if (StringUtils.isBlank(eurekaUrls)) {
            throw new BizException("未指定eureka.client.serviceUrl.defaultZone参数");
        }
        String url = eurekaUrls.split("\\,")[0];
        if (url.endsWith("/")) {
            url = eurekaUrls.substring(0, eurekaUrls.length() - 1);
        }
        this.eurekaUrl = url;
    }

}
