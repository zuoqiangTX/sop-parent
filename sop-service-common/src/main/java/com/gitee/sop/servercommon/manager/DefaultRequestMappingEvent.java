package com.gitee.sop.servercommon.manager;

import com.gitee.sop.servercommon.bean.ServiceApiInfo;
import com.gitee.sop.servercommon.mapping.ApiMappingHandlerMapping;
import com.gitee.sop.servercommon.mapping.ApiMappingInfo;
import com.gitee.sop.servercommon.mapping.ApiMappingRequestCondition;
import lombok.Getter;
import org.springframework.core.env.Environment;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author tanghc
 */
@Getter
public class DefaultRequestMappingEvent implements RequestMappingEvent {

    private ApiMetaManager apiMetaManager;
    private Environment environment;

    public DefaultRequestMappingEvent(ApiMetaManager apiMetaManager, Environment environment) {
        this.apiMetaManager = apiMetaManager;
        this.environment = environment;
    }

    @Override
    public void onRegisterSuccess(ApiMappingHandlerMapping apiMappingHandlerMapping) {
        String serviceId = environment.getProperty("spring.application.name");
        if (serviceId == null) {
            throw new RuntimeException("请在application.properties中指定spring.application.name属性");
        }
        List<ServiceApiInfo.ApiMeta> apis = this.buildApiMetaList(apiMappingHandlerMapping);
        // 排序
        Collections.sort(apis, new Comparator<ServiceApiInfo.ApiMeta>() {
            @Override
            public int compare(ServiceApiInfo.ApiMeta o1, ServiceApiInfo.ApiMeta o2) {
                return o1.fetchNameVersion().compareTo(o2.fetchNameVersion());
            }
        });

        ServiceApiInfo serviceApiInfo = new ServiceApiInfo();
        serviceApiInfo.setServiceId(serviceId);
        serviceApiInfo.setApis(apis);

        apiMetaManager.uploadApi(serviceApiInfo);
    }

    protected List<ServiceApiInfo.ApiMeta> buildApiMetaList(ApiMappingHandlerMapping apiMappingHandlerMapping) {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = apiMappingHandlerMapping.getHandlerMethods();
        Set<RequestMappingInfo> requestMappingInfos = handlerMethods.keySet();
        List<String> store = new ArrayList<>();
        List<ServiceApiInfo.ApiMeta> apis = new ArrayList<>(requestMappingInfos.size());

        for (Map.Entry<RequestMappingInfo, HandlerMethod> handlerMethodEntry : handlerMethods.entrySet()) {
            ServiceApiInfo.ApiMeta apiMeta = this.buildApiMeta(handlerMethodEntry);
            if (apiMeta == null) {
                continue;
            }
            String key = apiMeta.fetchNameVersion();
            if (store.contains(key)) {
                throw new RuntimeException("重复申明接口，请检查path和version，path:" + apiMeta.getPath() + ", version:" + apiMeta.getVersion());
            } else {
                store.add(key);
            }
            apis.add(apiMeta);
        }
        return apis;
    }

    protected ServiceApiInfo.ApiMeta buildApiMeta(Map.Entry<RequestMappingInfo, HandlerMethod> handlerMethodEntry) {
        RequestMappingInfo requestMappingInfo = handlerMethodEntry.getKey();
        Set<String> patterns = requestMappingInfo.getPatternsCondition().getPatterns();
        RequestCondition<?> customCondition = requestMappingInfo.getCustomCondition();
        if (customCondition instanceof ApiMappingRequestCondition) {
            ApiMappingRequestCondition condition = (ApiMappingRequestCondition) customCondition;
            ApiMappingInfo apiMappingInfo = condition.getApiMappingInfo();
            String name = apiMappingInfo.getName();
            String version = apiMappingInfo.getVersion();
            String path = patterns.iterator().next();
            // 不是ApiMapping注解的接口，name属性是null
            if (name == null) {
                name = buildName(path);
            }
            ServiceApiInfo.ApiMeta apiMeta = new ServiceApiInfo.ApiMeta(name, path, version);
            apiMeta.setIgnoreValidate(apiMappingInfo.isIgnoreValidate());
            apiMeta.setMergeResult(apiMappingInfo.isMergeResult());
            return apiMeta;
        }
        return null;
    }

    protected String buildName(String path) {
        path = StringUtils.trimLeadingCharacter(path, '/');
        path = StringUtils.trimTrailingCharacter(path, '/');
        path = path.replace("/", ".");
        return path;
    }

}
