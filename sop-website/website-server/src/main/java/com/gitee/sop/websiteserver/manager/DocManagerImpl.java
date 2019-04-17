package com.gitee.sop.websiteserver.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gitee.sop.websiteserver.bean.DocItem;
import com.gitee.sop.websiteserver.bean.DocModule;
import com.gitee.sop.websiteserver.bean.DocParameter;
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
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
@Slf4j
public class DocManagerImpl implements DocManager {

    // key:module
    Map<String, DocModule> docDefinitionMap = new HashMap<>();

    // key: name+version
    Map<String, DocItem> docItemMap = new HashMap<>();


    OkHttpClient client = new OkHttpClient();

    RestTemplate restTemplate = new RestTemplate();

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
            Map<String, DocItem> itemMap = docDefinitionMap.values()
                    .stream()
                    .map(DocModule::getDocItems)
                    .flatMap(docItems -> docItems.stream())
                    .collect(Collectors.toMap(DocItem::getNameVersion, Function.identity()));
            this.docItemMap.putAll(itemMap);
        } catch (IOException e) {
            log.error("加载失败", e);
        }
    }

    private void loadDocInfo(ServiceInfoVO serviceInfoVo) {
        String url = "http://" + serviceInfoVo.getIpAddr() + ":" + serviceInfoVo.getServerPort() + "/v2/api-docs";
        try {
            ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
            String docInfoJson = entity.getBody();
            DocModule docDefinition = this.parseDocJson(docInfoJson);
            docDefinitionMap.put(docDefinition.getModule(), docDefinition);
        } catch (RestClientException e) {
            // 这里报错可能是因为有些微服务没有配置swagger文档，导致404访问不到
            // 这里catch跳过即可
            log.warn("读取文档失败, url:{}", url, e);
        }
    }

    private DocModule parseDocJson(String docInfoJson) {
        JSONObject docRoot = JSON.parseObject(docInfoJson);
        String title = docRoot.getJSONObject("info").getString("title");
        List<DocItem> docItems = new ArrayList<>();

        JSONObject paths = docRoot.getJSONObject("paths");
        Set<String> pathNameSet = paths.keySet();
        for (String pathName : pathNameSet) {
            JSONObject pathInfo = paths.getJSONObject(pathName);
            Set<String> pathSet = pathInfo.keySet();
            Optional<String> first = pathSet.stream().findFirst();
            if (first.isPresent()) {
                String path = first.get();
                JSONObject docInfo = pathInfo.getJSONObject(path);
                DocItem docItem = buildDocItem(docInfo, docRoot);
                docItems.add(docItem);
            }
        }

        DocModule docDefinition = new DocModule();
        docDefinition.setModule(title);
        docDefinition.setDocItems(docItems);
        return docDefinition;
    }

    private DocItem buildDocItem(JSONObject docInfo, JSONObject docRoot) {
        DocItem docItem = new DocItem();
        docItem.setName(docInfo.getString("sop_name"));
        docItem.setVersion(docInfo.getString("sop_version"));
        docItem.setSummary(docInfo.getString("summary"));
        docItem.setDescription(docInfo.getString("description"));
        Optional<JSONArray> parametersOptional = Optional.ofNullable(docInfo.getJSONArray("parameters"));
        JSONArray parameters = parametersOptional.orElse(new JSONArray());
        List<DocParameter> docParameterList = parameters.toJavaList(DocParameter.class);
        docItem.setRequestParameters(docParameterList);

        List<DocParameter> responseParameterList = this.buildResponseParameterList(docInfo, docRoot);
        docItem.setResponseParameters(responseParameterList);

        return docItem;
    }

    private List<DocParameter> buildResponseParameterList(JSONObject docInfo, JSONObject docRoot) {
        String responseRef = getResponseRef(docInfo);
        List<DocParameter> respParameterList = new ArrayList<>();
        if (StringUtils.isNotBlank(responseRef)) {
            JSONObject responseObject = docRoot.getJSONObject("definitions").getJSONObject(responseRef);
            JSONObject properties = responseObject.getJSONObject("properties");
            Set<String> fieldNames = properties.keySet();
            for (String fieldName : fieldNames) {
                JSONObject fieldInfo = properties.getJSONObject(fieldName);
                DocParameter respParam = fieldInfo.toJavaObject(DocParameter.class);
                respParam.setName(fieldName);
                respParameterList.add(respParam);
            }
        }
        return respParameterList;
    }

    private String getResponseRef(JSONObject docInfo) {
        String ref = Optional.ofNullable(docInfo.getJSONObject("responses"))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getJSONObject("200")))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getJSONObject("schema")))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getString("originalRef")))
                .orElse("");
        return ref;
    }

    @Override
    public DocItem get(String method, String version) {
        return docItemMap.get(method + version);
    }

    @Override
    public DocModule getByTitle(String title) {
        return docDefinitionMap.get(title);
    }

    @Override
    public Collection<DocModule> listAll() {
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

    private String requestEurekaServer(EurekaUri eurekaUri, String... args) throws IOException {
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
