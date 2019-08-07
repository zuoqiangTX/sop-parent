package com.gitee.sop.registryapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.gitee.sop.registryapi.bean.HttpTool;
import com.gitee.sop.registryapi.bean.ServiceInfo;
import com.gitee.sop.registryapi.bean.ServiceInstance;
import com.gitee.sop.registryapi.service.RegistryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * nacos接口实现, https://nacos.io/zh-cn/docs/open-api.html
 * @author tanghc
 */
public class RegistryServiceNacos implements RegistryService {

    static HttpTool httpTool = new HttpTool();

    @Value("${registry.nacos-server-addr:}")
    private String nacosAddr;

    private NamingService namingService;

    @PostConstruct
    public void after() throws NacosException {
        if (StringUtils.isNotBlank(nacosAddr)) {
            namingService = NamingFactory.createNamingService(nacosAddr);
        }
    }

    @Override
    public List<ServiceInfo> listAllService(int pageNo, int pageSize) throws Exception {
        ListView<String> servicesOfServer = namingService.getServicesOfServer(pageNo, pageSize);
        List<String> serverList = servicesOfServer.getData();
        List<ServiceInfo> serviceInfoList = new ArrayList<>();
        for (String serviceName : serverList) {
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setServiceId(serviceName);
            List<Instance> instanceList = namingService.getAllInstances(serviceName);
            if (!CollectionUtils.isEmpty(instanceList)) {
                serviceInfo.setInstances(new ArrayList<>(instanceList.size()));
                for (Instance instance : instanceList) {
                    ServiceInstance serviceInstance = new ServiceInstance();
                    serviceInstance.setInstanceId(instance.getInstanceId());
                    serviceInstance.setServiceId(serviceInfo.getServiceId());
                    serviceInstance.setIp(instance.getIp());
                    serviceInstance.setPort(instance.getPort());
                    boolean isOnline = instance.getWeight() > 0;
                    String status = isOnline ? "UP" : "OUT_OF_SERVICE";
                    serviceInstance.setStatus(status);
                    serviceInstance.setMetadata(instance.getMetadata());
                    serviceInfo.getInstances().add(serviceInstance);
                }
            }
            serviceInfoList.add(serviceInfo);
        }
        return serviceInfoList;
    }

    @Override
    public void onlineInstance(ServiceInstance serviceInstance) throws Exception {
        // 查询实例
        Instance instance = this.getInstance(serviceInstance);
        // 上线，把权重设置成1
        instance.setWeight(1);
        this.updateInstance(instance);
    }

    @Override
    public void offlineInstance(ServiceInstance serviceInstance) throws Exception {
        // 查询实例
        Instance instance = this.getInstance(serviceInstance);
        // 下线，把权重设置成0
        instance.setWeight(0);
        this.updateInstance(instance);
    }

    @Override
    public void setMetadata(ServiceInstance serviceInstance, String key, String value) throws Exception {
        // 查询实例
        Instance instance = this.getInstance(serviceInstance);
        // 设置metadata
        Map<String, String> metadata = instance.getMetadata();
        metadata.put(key, value);
        this.updateInstance(instance);
    }

    protected void updateInstance(Instance instance) throws IOException {
        String json = JSON.toJSONString(instance);
        JSONObject jsonObject = JSON.parseObject(json);
        httpTool.request("http://" + nacosAddr + "/nacos/v1/ns/instance", jsonObject, null, HttpTool.HTTPMethod.PUT);
    }

    /**
     * 查询实例
     * @param serviceInstance 实例信息
     * @return 返回nacos实例
     * @throws IOException 查询异常
     */
    protected Instance getInstance(ServiceInstance serviceInstance) throws IOException {
        Map<String, String> params = new HashMap<>(8);
        params.put("serviceName", serviceInstance.getServiceId());
        params.put("ip", serviceInstance.getIp());
        params.put("port", String.valueOf(serviceInstance.getPort()));
        String instanceJson = httpTool.request("http://" + nacosAddr + "/nacos/v1/ns/instance", params, null, HttpTool.HTTPMethod.GET);
        return JSON.parseObject(instanceJson, Instance.class);
    }
}
