package com.gitee.sop.gatewaycommon.route;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.gatewaycommon.bean.InstanceDefinition;
import com.gitee.sop.gatewaycommon.bean.ServiceRouteInfo;
import com.gitee.sop.gatewaycommon.manager.BaseRouteCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tanghc
 */
@Slf4j
public abstract class BaseRoutesListener implements RegistryListener {

    private static final String SOP_ROUTES_PATH = "/sop/routes";

    private static final String SECRET = "a3d9sf!1@odl90zd>fkASwq";

    private static final int FIVE_SECONDS = 1000 * 5;

    private static final String METADATA_SERVER_CONTEXT_PATH = "server.servlet.context-path";

    private static final String METADATA_SOP_ROUTES_PATH = "sop.routes.path";

    private static RestTemplate restTemplate = new RestTemplate();

    static {
        // 解决statusCode不等于200，就抛异常问题
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            protected boolean hasError(HttpStatus statusCode) {
                return statusCode == null;
            }
        });
    }

    private Map<String, Long> updateTimeMap = new ConcurrentHashMap<>(16);

    @Autowired
    private BaseRouteCache<?> baseRouteCache;

    @Autowired
    private RoutesProcessor routesProcessor;

    /**
     * 移除路由信息
     *
     * @param serviceId serviceId
     */
    public void removeRoutes(String serviceId) {
        doOperator(serviceId, () -> {
            log.info("服务下线，删除路由配置，serviceId: {}", serviceId);
            baseRouteCache.remove(serviceId);
            routesProcessor.removeAllRoutes(serviceId);
        });

    }

    /**
     * 拉取路由信息
     *
     * @param instance 服务实例
     */
    public void pullRoutes(InstanceDefinition instance) {
        String serviceName = instance.getServiceId();
        doOperator(serviceName, () -> {
            String url = getRouteRequestUrl(instance);
            log.info("拉取路由配置，serviceId: {}, url: {}", serviceName, url);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String body = responseEntity.getBody();
                ServiceRouteInfo serviceRouteInfo = JSON.parseObject(body, ServiceRouteInfo.class);
                baseRouteCache.load(serviceRouteInfo, callback -> routesProcessor.saveRoutes(serviceRouteInfo, instance));
            } else {
                log.error("拉取路由配置异常，url: {}, status: {}, body: {}", url, responseEntity.getStatusCodeValue(), responseEntity.getBody());
            }
        });

    }


    private void doOperator(String serviceId, Runnable runnable) {
        if (canOperator(serviceId)) {
            runnable.run();
        }
    }

    private boolean canOperator(String serviceId) {
        // nacos会不停的触发事件，这里做了一层拦截
        // 同一个serviceId5秒内允许访问一次
        Long lastUpdateTime = updateTimeMap.getOrDefault(serviceId, 0L);
        long now = System.currentTimeMillis();
        boolean can = now - lastUpdateTime > FIVE_SECONDS;
        if (can) {
            updateTimeMap.put(serviceId, now);
        }
        return can;
    }

    /**
     * 拉取路由请求url
     *
     * @param instance 服务实例
     * @return 返回最终url
     */
    private static String getRouteRequestUrl(InstanceDefinition instance) {
        Map<String, String> metadata = instance.getMetadata();
        String customPath = metadata.get(METADATA_SOP_ROUTES_PATH);
        String homeUrl;
        String servletPath;
        // 如果metadata中指定了获取路由的url
        if (StringUtils.isNotBlank(customPath)) {
            // 自定义完整的url
            if (customPath.startsWith("http")) {
                homeUrl = customPath;
                servletPath = "";
            } else {
                homeUrl = getHomeUrl(instance);
                servletPath = customPath;
            }
        } else {
            // 默认处理
            homeUrl = getHomeUrl(instance);
            String contextPath = metadata.getOrDefault(METADATA_SERVER_CONTEXT_PATH, "");
            servletPath = contextPath + SOP_ROUTES_PATH;
        }
        if (StringUtils.isNotBlank(servletPath) && !servletPath.startsWith("/")) {
            servletPath = '/' + servletPath;
        }
        String query = buildQuery(SECRET);
        return homeUrl + servletPath + query;
    }

    private static String getHomeUrl(InstanceDefinition instance) {
        return "http://" + instance.getIp() + ":" + instance.getPort();
    }

    private static String buildQuery(String secret) {
        String time = String.valueOf(System.currentTimeMillis());
        String source = secret + time + secret;
        String sign = DigestUtils.md5DigestAsHex(source.getBytes());
        return "?time=" + time + "&sign=" + sign;
    }
}
