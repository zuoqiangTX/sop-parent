package com.gitee.sop.websiteserver.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.sop.websiteserver.bean.DocInfo;
import com.gitee.sop.websiteserver.bean.DocItem;
import com.gitee.sop.websiteserver.bean.EurekaApplication;
import com.gitee.sop.websiteserver.bean.EurekaApps;
import com.gitee.sop.websiteserver.bean.EurekaInstance;
import com.gitee.sop.websiteserver.bean.EurekaUri;
import com.gitee.sop.websiteserver.vo.ServiceInfoVO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private Environment environment;

    private String eurekaUrl;

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
//            Map<String, DocItem> itemMap = docDefinitionMap.values()
//                    .stream()
//                    .map(DocInfo::getDocModuleList)
//                    .map(list->{
//                        for (DocModule docModule : list) {
//
//                        }
//                    })
//                    .map(DocModule::getDocItems)
//                    .flatMap(docItems -> docItems.stream())
//                    .collect(Collectors.toMap(DocItem::getNameVersion, Function.identity()));
//            this.docItemMap.putAll(itemMap);
        } catch (IOException e) {
            log.error("加载失败", e);
        }
    }

    protected void loadDocInfo(ServiceInfoVO serviceInfoVo) {
        String url = "http://" + serviceInfoVo.getIpAddr() + ":" + serviceInfoVo.getServerPort() + "/v2/api-docs";
        try {
            ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
            String docInfoJson = entity.getBody();
            JSONObject docRoot = JSON.parseObject(docInfoJson);
            DocParser docParser = this.buildDocParser(docRoot);
            DocInfo docInfo = docParser.parseJson(docRoot);
            docDefinitionMap.put(docInfo.getTitle(), docInfo);
        } catch (RestClientException e) {
            // 这里报错可能是因为有些微服务没有配置swagger文档，导致404访问不到
            // 这里catch跳过即可
            log.warn("读取文档失败, url:{}, msg:{}", url, e.getMessage());
        }
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
                        ServiceInfoVO vo = new ServiceInfoVO();
                        vo.setName(eurekaApplication.getName());
                        vo.setIpAddr(instance.getIpAddr());
                        vo.setServerPort(instance.fetchPort());
                        serviceInfoVoList.add(vo);
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
    protected void after() {
        String eurekaUrls = environment.getProperty("eureka.client.serviceUrl.defaultZone");
        if (StringUtils.isBlank(eurekaUrls)) {
            throw new IllegalArgumentException("未指定eureka.client.serviceUrl.defaultZone参数");
        }
        String url = eurekaUrls.split("\\,")[0];
        if (url.endsWith("/")) {
            url = eurekaUrls.substring(0, eurekaUrls.length() - 1);
        }
        this.eurekaUrl = url;
    }
}
