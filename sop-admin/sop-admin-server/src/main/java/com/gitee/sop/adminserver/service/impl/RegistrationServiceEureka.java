package com.gitee.sop.adminserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.adminserver.bean.EurekaApplication;
import com.gitee.sop.adminserver.bean.EurekaApps;
import com.gitee.sop.adminserver.bean.EurekaInstance;
import com.gitee.sop.adminserver.bean.EurekaUri;
import com.gitee.sop.adminserver.bean.ServiceInfo;
import com.gitee.sop.adminserver.bean.ServiceInstance;
import com.gitee.sop.adminserver.common.BizException;
import com.gitee.sop.adminserver.service.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tanghc
 */
@Slf4j
@Service
public class RegistrationServiceEureka implements RegistrationService {

    OkHttpClient client = new OkHttpClient();

    @Autowired
    private Environment environment;

    private String eurekaUrl;

    @Override
    public List<ServiceInfo> listAllService(int pageNo, int pageSize) throws Exception {
        String json = this.requestEurekaServer(EurekaUri.QUERY_APPS);
        EurekaApps eurekaApps = JSON.parseObject(json, EurekaApps.class);

        List<ServiceInfo> serviceInfoList = new ArrayList<>();
        List<EurekaApplication> applicationList = eurekaApps.getApplications().getApplication();
        for (EurekaApplication eurekaApplication : applicationList) {
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setServiceId(eurekaApplication.getName());
            List<EurekaInstance> instanceList = eurekaApplication.getInstance();
            if (CollectionUtils.isNotEmpty(instanceList)) {
                serviceInfo.setInstances(new ArrayList<>(instanceList.size()));
                for (EurekaInstance eurekaInstance : instanceList) {
                    ServiceInstance serviceInstance = new ServiceInstance();
                    serviceInstance.setInstanceId(eurekaInstance.getInstanceId());
                    serviceInstance.setServiceId(serviceInfo.getServiceId());
                    serviceInstance.setIpPort(eurekaInstance.getIpAddr() + ":" + eurekaInstance.fetchPort());
                    serviceInstance.setStatus(eurekaInstance.getStatus());
                    serviceInfo.getInstances().add(serviceInstance);
                }
            }
            serviceInfoList.add(serviceInfo);
        }
        return serviceInfoList;
    }



    @Override
    public void onlineInstance(ServiceInstance serviceInstance) throws Exception {
        this.requestEurekaServer(EurekaUri.ONLINE_SERVICE, serviceInstance.getServiceId(), serviceInstance.getInstanceId());
    }

    @Override
    public void offlineInstance(ServiceInstance serviceInstance) throws Exception {
        this.requestEurekaServer(EurekaUri.OFFLINE_SERVICE, serviceInstance.getServiceId(), serviceInstance.getInstanceId());
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
        // 取第一个
        String url = eurekaUrls.split("\\,")[0];
        if (url.endsWith("/")) {
            url = eurekaUrls.substring(0, eurekaUrls.length() - 1);
        }
        this.eurekaUrl = url;
    }
}
