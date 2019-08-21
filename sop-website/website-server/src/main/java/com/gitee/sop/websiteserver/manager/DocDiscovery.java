package com.gitee.sop.websiteserver.manager;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author tanghc
 */
@Slf4j
@Service
public class DocDiscovery {

    private static final String SECRET = "b749a2ec000f4f29";

    private static final int FIVE_SECONDS = 1000 * 5;

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    private RestTemplate restTemplate = new RestTemplate();

    private Map<String, Long> updateTimeMap = new HashMap<>(16);

    public synchronized void refresh(DocManager docManager) {
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
            if (Objects.equals(thisServiceId, serviceName) || "api-gateway".equalsIgnoreCase(serviceName)) {
                continue;
            }
            // nacos会不停的触发事件，这里做了一层拦截
            // 同一个serviceId5秒内允许访问一次
            Long lastUpdateTime = updateTimeMap.getOrDefault(serviceName, 0L);
            long now = System.currentTimeMillis();
            if (now - lastUpdateTime < FIVE_SECONDS) {
                continue;
            }
            updateTimeMap.put(serviceName, now);
            try {
                List<Instance> allInstances = namingService.getAllInstances(serviceName);
                if (CollectionUtils.isEmpty(allInstances)) {
                    log.info("{}服务下线，删除文档信息", serviceName);
                    // 如果没有服务列表，则删除所有路由信息
                    docManager.remove(serviceName);
                } else {
                    for (Instance instance : allInstances) {
                        log.info("加载服务文档，instance:{}", instance);
                        String url = getRouteRequestUrl(instance);
                        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
                        if (responseEntity.getStatusCode() == HttpStatus.OK) {
                            String body = responseEntity.getBody();
                            log.debug("加载{}文档，文档信息：{}", serviceName, body);
                            docManager.addDocInfo(serviceName, body);
                        }
                    }
                }
            } catch (NacosException e) {
                log.error("选择服务实例失败，serviceName:{}", serviceName, e);
            }
        }
    }

    private static String getRouteRequestUrl(Instance instance) {
        String query = buildQuery(SECRET);
        return "http://" + instance.getIp() + ":" + instance.getPort() + "/v2/api-docs" + query;
    }

    private static String buildQuery(String secret) {
        String time = String.valueOf(System.currentTimeMillis());
        String source = secret + time + secret;
        String sign = DigestUtils.md5DigestAsHex(source.getBytes());
        return "?time=" + time + "&sign=" + sign;
    }

}
