package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.message.ErrorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

/**
 * @author tanghc
 */
public class AbstractConfiguration {
    @Autowired
    protected Environment environment;

    @Autowired
    protected RouteManager apiMetaManager;

    @PostConstruct
    public final void after() {
        if (RouteRepositoryContext.getRouteRepository() == null) {
            throw new IllegalArgumentException("RouteRepositoryContext.setRouteRepository()方法未使用");
        }
        EnvironmentContext.setEnvironment(environment);
        ZookeeperContext.setEnvironment(environment);

        initMessage();
        apiMetaManager.refresh();
        doAfter();
    }

    protected void doAfter() {

    }

    protected void initMessage() {
        ErrorFactory.initMessageSource(ApiContext.getApiConfig().getI18nModules());
    }
}
