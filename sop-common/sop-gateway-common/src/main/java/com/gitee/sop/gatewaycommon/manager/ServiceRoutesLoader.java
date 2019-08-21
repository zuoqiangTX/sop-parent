package com.gitee.sop.gatewaycommon.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.gitee.sop.gatewaycommon.bean.NacosConfigs;
import com.gitee.sop.gatewaycommon.bean.ServiceRouteInfo;
import com.gitee.sop.gatewaycommon.bean.TargetRoute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alibaba.nacos.NacosConfigProperties;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.context.ApplicationEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

/**
 * 发现新服务，更新路由信息
 *
 * @author tanghc
 */
@Slf4j
public class ServiceRoutesLoader<T extends TargetRoute> {

    private static final String SECRET = "a3d9sf!1@odl90zd>fkASwq";

    private static final int FIVE_SECONDS = 1000 * 5;

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Autowired
    private NacosConfigProperties nacosConfigProperties;

    @Autowired
    private BaseRouteCache<T> baseRouteCache;

    private RestTemplate restTemplate = new RestTemplate();

    private volatile long lastUpdateTime;

    public synchronized void load(ApplicationEvent event) {
        long now = System.currentTimeMillis();
        // 5秒内只能执行一次，解决重启应用连续加载4次问题
        if (now - lastUpdateTime < FIVE_SECONDS) {
            return;
        }
        lastUpdateTime = now;
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
        ConfigService configService = nacosConfigProperties.configServiceInstance();
        for (ServiceInfo serviceInfo : subscribes) {
            String serviceName = serviceInfo.getName();
            // 如果是本机服务，跳过
            if (Objects.equals(thisServiceId, serviceName)) {
                continue;
            }
            try {
                String dataId = NacosConfigs.getRouteDataId(serviceName);
                String groupId = NacosConfigs.GROUP_ROUTE;
                List<Instance> allInstances = namingService.getAllInstances(serviceName);
                if (CollectionUtils.isEmpty(allInstances)) {
                    log.info("{}服务下线，删除路由信息", serviceName);
                    // 如果没有服务列表，则删除所有路由信息
                    baseRouteCache.remove(serviceName);
                    configService.removeConfig(dataId, groupId);
                } else {
                    for (Instance instance : allInstances) {
                        log.info("加载服务路由，instance:{}", instance);
                        String url = getRouteRequestUrl(instance);
                        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
                        if (responseEntity.getStatusCode() == HttpStatus.OK) {
                            String body = responseEntity.getBody();
                            log.debug("加载{}路由，路由信息：{}", serviceName, body);
                            ServiceRouteInfo serviceRouteInfo = JSON.parseObject(body, ServiceRouteInfo.class);
                            baseRouteCache.load(serviceRouteInfo);
                            configService.publishConfig(dataId, groupId, body);
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
        return "http://" + instance.getIp() + ":" + instance.getPort() + "/sop/routes" + query;
    }

    private static String buildQuery(String secret) {
        String time = String.valueOf(System.currentTimeMillis());
        String source = secret + time + secret;
        String sign = DigestUtils.md5DigestAsHex(source.getBytes());
        return "?time=" + time + "&sign=" + sign;
    }

}