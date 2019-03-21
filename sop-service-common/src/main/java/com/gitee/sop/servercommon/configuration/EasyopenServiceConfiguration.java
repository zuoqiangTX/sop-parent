package com.gitee.sop.servercommon.configuration;

import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.util.ReflectionUtil;
import com.gitee.sop.servercommon.bean.ServiceApiInfo;
import com.gitee.sop.servercommon.manager.ApiMetaManager;
import com.gitee.sop.servercommon.manager.DefaultRequestMappingEvent;
import com.gitee.sop.servercommon.manager.RequestMappingEvent;
import com.gitee.sop.servercommon.mapping.ApiMappingHandlerMapping;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 提供给easyopen项目使用
 *
 * @author tanghc
 */
public class EasyopenServiceConfiguration extends BaseServiceConfiguration {

    @Override
    protected RequestMappingEvent getRequestMappingEvent(ApiMetaManager apiMetaManager, Environment environment) {
        return new EasyopenRequestMappingEvent(apiMetaManager, environment);
    }

    class EasyopenRequestMappingEvent extends DefaultRequestMappingEvent {
        String prefixPath;

        public EasyopenRequestMappingEvent(ApiMetaManager apiMetaManager, Environment environment) {
            super(apiMetaManager, environment);

            String prefixPath = getEnvironment().getProperty("easyopen.prefix-path");
            if (prefixPath == null) {
                throw new IllegalArgumentException("请在application.propertis中设置easyopen.prefix-path属性，填IndexController上面的@RequestMapping()中的值");
            }
            this.prefixPath = prefixPath;
        }

        @Override
        protected List<ServiceApiInfo.ApiMeta> buildApiMetaList(ApiMappingHandlerMapping apiMappingHandlerMapping) {
            ApplicationContext ctx = getApplicationContext();
            String[] apiServiceNames = ReflectionUtil.findApiServiceNames(ctx);
            List<ServiceApiInfo.ApiMeta> apiMetaList = new ArrayList<>();
            for (String apiServiceName : apiServiceNames) {
                Object bean = ctx.getBean(apiServiceName);
                doWithMethods(bean.getClass(), method -> {
                    Api api = AnnotationUtils.findAnnotation(method, Api.class);
                    ServiceApiInfo.ApiMeta apiMeta = new ServiceApiInfo.ApiMeta();
                    apiMeta.setName(api.name());
                    apiMeta.setVersion(api.version());
                    apiMeta.setIgnoreValidate(BooleanUtils.toInteger(api.ignoreValidate()));
                    // /api/goods.get/
                    String servletPath = this.buildPath(api);
                    apiMeta.setPath(servletPath);
                    apiMetaList.add(apiMeta);
                });
            }
            return apiMetaList;
        }

        protected void doWithMethods(Class<?> clazz, Consumer<Method> consumer) {
            // Keep backing up the inheritance hierarchy.
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                boolean match = !method.isSynthetic() && AnnotationUtils.findAnnotation(method, Api.class) != null;
                if (match) {
                    consumer.accept(method);
                }
            }
        }

        protected String buildPath(Api api) {
            // /api/goods.get/
            String servletPath = prefixPath + "/" + api.name() + "/";
            String version = api.version();
            if (StringUtils.hasLength(version)) {
                // /api/goods.get/1.0/
                servletPath = servletPath + version + "/";
            }
            return servletPath;
        }
    }

    @Override
    public void after() {
        super.after();
        // 取消验证
        // todo:需要在easyopen端修改
        //ApiContext.getApiConfig().setIgnoreValidate(true);
    }
}
