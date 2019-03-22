package com.gitee.sop.adminserver.api.service;

import com.alibaba.fastjson.JSON;
import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;
import com.gitee.easyopen.exception.ApiException;
import com.gitee.sop.adminserver.api.service.param.ServiceSearchParam;
import com.gitee.sop.adminserver.api.service.result.ServiceInfo;
import com.gitee.sop.adminserver.api.service.result.ServiceInfoVo;
import com.gitee.sop.adminserver.bean.EurekaApplication;
import com.gitee.sop.adminserver.bean.EurekaApps;
import com.gitee.sop.adminserver.bean.EurekaInstance;
import com.gitee.sop.adminserver.bean.EurekaUri;
import com.gitee.sop.adminserver.bean.ZookeeperContext;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@ApiService
@ApiDoc("服务管理")
@Slf4j
public class ServiceApi {


    OkHttpClient client = new OkHttpClient();

    @Autowired
    private Environment environment;

    private String eurekaUrl;

    // eureka.client.serviceUrl.defaultZone

    @Api(name = "service.list")
    @ApiDocMethod(description = "服务列表", elementClass = ServiceInfo.class)
    List<ServiceInfo> listServiceInfo(ServiceSearchParam param) throws Exception {
        String json = this.requestEurekaServer(EurekaUri.QUERY_APPS);
        EurekaApps eurekaApps = JSON.parseObject(json, EurekaApps.class);


        String routeRootPath = ZookeeperContext.getSopRouteRootPath(param.getProfile());
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

    @Api(name = "service.instance.list")
    @ApiDocMethod(description = "服务列表", elementClass = ServiceInfo.class)
    List<ServiceInfoVo> listService(ServiceSearchParam param) throws Exception {
        String json = this.requestEurekaServer(EurekaUri.QUERY_APPS);
        EurekaApps eurekaApps = JSON.parseObject(json, EurekaApps.class);

        List<ServiceInfoVo> serviceInfoVoList = new ArrayList<>();
        List<EurekaApplication> applicationList = eurekaApps.getApplications().getApplication();
        AtomicInteger idGen = new AtomicInteger(1);
        applicationList.stream()
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

    private String requestEurekaServer(EurekaUri uri) throws IOException {
        Request request = this.buildRequest(EurekaUri.QUERY_APPS);
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            return "{}";
        }
    }

    private Request buildRequest(EurekaUri uri) {
        String apiUrl = this.eurekaUrl + uri.getUri();
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        return request;
    }

    @PostConstruct
    protected void after() {
        String eurekaUrls = environment.getProperty("eureka.client.serviceUrl.defaultZone");
        if (StringUtils.isBlank(eurekaUrls)) {
            throw new ApiException("未指定eureka.client.serviceUrl.defaultZone参数");
        }
        String url = eurekaUrls.split("\\,")[0];
        if (url.endsWith("/")) {
            url = eurekaUrls.substring(0, eurekaUrls.length() - 1);
        }
        this.eurekaUrl = url;
    }

}
