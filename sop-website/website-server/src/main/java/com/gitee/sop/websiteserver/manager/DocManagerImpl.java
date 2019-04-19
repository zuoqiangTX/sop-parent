package com.gitee.sop.websiteserver.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.sop.websiteserver.bean.DocInfo;
import com.gitee.sop.websiteserver.bean.DocItem;
import com.gitee.sop.websiteserver.bean.EurekaApplication;
import com.gitee.sop.websiteserver.bean.EurekaApps;
import com.gitee.sop.websiteserver.bean.EurekaInstance;
import com.gitee.sop.websiteserver.bean.EurekaUri;
import com.gitee.sop.websiteserver.bean.ZookeeperContext;
import com.gitee.sop.websiteserver.vo.ServiceInfoVO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
@Slf4j
public class DocManagerImpl implements DocManager {

    // key:title
    Map<String, DocInfo> docDefinitionMap = new HashMap<>();

    // key: name+version
    Map<String, DocItem> docItemMap = new HashMap<>();


    OkHttpClient client = new OkHttpClient();

    RestTemplate restTemplate = new RestTemplate();

    DocParser swaggerDocParser = new SwaggerDocParser();

    DocParser easyopenDocParser = new EasyopenDocParser();

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    DelayQueue<Msg> queue = new DelayQueue<>();

    private String secret = "b749a2ec000f4f29";

    @Autowired
    private Environment environment;

    private String eurekaUrl;

    private volatile boolean listenInited;

    @Override
    public void load() {
        try {
            Map<String, List<ServiceInfoVO>> listMap = this.getAllServiceList();
            log.info("服务列表：{}", JSON.toJSONString(listMap.keySet()));
            // {"STORY-SERVICE":[{"ipAddr":"10.1.30.54","name":"STORY-SERVICE","serverPort":"2222"}],"API-GATEWAY":[{"ipAddr":"10.1.30.54","name":"API-GATEWAY","serverPort":"8081"}]}
            for (Map.Entry<String, List<ServiceInfoVO>> entry : listMap.entrySet()) {
                ServiceInfoVO serviceInfoVo = entry.getValue().get(0);
                loadDocInfo(serviceInfoVo);
            }
        } catch (Exception e) {
            log.error("加载失败", e);
        }
    }

    protected void loadDocInfo(ServiceInfoVO serviceInfoVo) {
        String query = this.buildQuery();
        String url = "http://" + serviceInfoVo.getIpAddr() + ":" + serviceInfoVo.getServerPort() + "/v2/api-docs" + query;
        try {
            ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
            if (entity.getStatusCode() != HttpStatus.OK) {
                throw new IllegalAccessException("无权访问");
            }
            String docInfoJson = entity.getBody();
            JSONObject docRoot = JSON.parseObject(docInfoJson);
            DocParser docParser = this.buildDocParser(docRoot);
            DocInfo docInfo = docParser.parseJson(docRoot);
            docDefinitionMap.put(docInfo.getTitle(), docInfo);
        } catch (Exception e) {
            // 这里报错可能是因为有些微服务没有配置swagger文档，导致404访问不到
            // 这里catch跳过即可
            log.warn("读取文档失败, url:{}, msg:{}", url, e.getMessage());
        }
    }

    protected String buildQuery() {
        String time = String.valueOf(System.currentTimeMillis());
        String source = secret + time + secret;
        String sign = DigestUtils.md5DigestAsHex(source.getBytes());
        return "?time=" + time + "&sign=" + sign;
    }

    protected DocParser buildDocParser(JSONObject rootDoc) {
        Object easyopen = rootDoc.get("easyopen");
        if (easyopen != null) {
            return easyopenDocParser;
        } else {
            return swaggerDocParser;
        }
    }

    @Override
    public DocItem get(String method, String version) {
        return docItemMap.get(method + version);
    }

    @Override
    public DocInfo getByTitle(String title) {
        return docDefinitionMap.get(title);
    }

    @Override
    public Collection<DocInfo> listAll() {
        return docDefinitionMap.values();
    }

    protected Map<String, List<ServiceInfoVO>> getAllServiceList() throws IOException {
        String json = this.requestEurekaServer(EurekaUri.QUERY_APPS);
        EurekaApps eurekaApps = JSON.parseObject(json, EurekaApps.class);

        List<ServiceInfoVO> serviceInfoVoList = new ArrayList<>();
        List<EurekaApplication> applicationList = eurekaApps.getApplications().getApplication();
        applicationList.stream()
                .forEach(eurekaApplication -> {
                    List<EurekaInstance> instanceList = eurekaApplication.getInstance();
                    for (EurekaInstance instance : instanceList) {
                        if ("UP".equals(instance.getStatus())) {
                            ServiceInfoVO vo = new ServiceInfoVO();
                            vo.setName(eurekaApplication.getName());
                            vo.setIpAddr(instance.getIpAddr());
                            vo.setServerPort(instance.fetchPort());
                            serviceInfoVoList.add(vo);
                        }
                    }
                });

        Map<String, List<ServiceInfoVO>> listMap = serviceInfoVoList.stream()
                .collect(Collectors.groupingBy(ServiceInfoVO::getName));

        return listMap;
    }

    protected String requestEurekaServer(EurekaUri eurekaUri, String... args) throws IOException {
        Request request = eurekaUri.getRequest(this.eurekaUrl, args);
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            log.error("操作失败，url:{}, msg:{}, code:{}", eurekaUri.getUri(args), response.message(), response.code());
            throw new RuntimeException("操作失败");
        }
    }

    @PostConstruct
    protected void after() throws Exception {
        String eurekaUrls = environment.getProperty("eureka.client.serviceUrl.defaultZone");
        if (StringUtils.isBlank(eurekaUrls)) {
            throw new IllegalArgumentException("未指定eureka.client.serviceUrl.defaultZone参数");
        }
        String url = eurekaUrls.split("\\,")[0];
        if (url.endsWith("/")) {
            url = eurekaUrls.substring(0, eurekaUrls.length() - 1);
        }
        this.eurekaUrl = url;

        this.listenServiceId();
    }

    /**
     * 监听serviceId更改
     *
     * @throws Exception
     */
    protected void listenServiceId() throws Exception {

        executorService.execute(new Consumer(queue, this));

        ZookeeperContext.setEnvironment(environment);
        String routeRootPath = ZookeeperContext.getRouteRootPath();
        // 如果节点内容有变化则自动更新文档
        ZookeeperContext.listenChildren(routeRootPath, 1, (client, event) -> {
            if (listenInited) {
                long id = System.currentTimeMillis();
                // 延迟20秒执行
                queue.offer(new Msg(id, 1000 * 20));
            }
            TreeCacheEvent.Type type = event.getType();
            if (type == TreeCacheEvent.Type.INITIALIZED) {
                listenInited = true;
            }
        });
    }

    static class Msg implements Delayed {
        private long id;
        private long delay;

        // 自定义实现比较方法返回 1 0 -1三个参数


        public Msg(long id, long delay) {
            this.id = id;
            this.delay = delay + System.currentTimeMillis();
        }

        @Override
        public int compareTo(Delayed delayed) {
            Msg msg = (Msg) delayed;
            return Long.valueOf(this.id) > Long.valueOf(msg.id) ? 1
                    : (Long.valueOf(this.id) < Long.valueOf(msg.id) ? -1 : 0);
        }

        // 延迟任务是否到时就是按照这个方法判断如果返回的是负数则说明到期否则还没到期
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.delay - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }
    }

    @Slf4j
    static class Consumer implements Runnable {
        private DelayQueue<Msg> queue;
        private DocManager docManager;

        public Consumer(DelayQueue<Msg> queue, DocManager docManager) {
            this.queue = queue;
            this.docManager = docManager;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    queue.take();
                    log.info("延迟队列触发--重新加载文档信息");
                    docManager.load();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
