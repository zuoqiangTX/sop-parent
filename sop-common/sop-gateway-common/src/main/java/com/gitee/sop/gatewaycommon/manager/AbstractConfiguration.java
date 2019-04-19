package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.bean.BeanInitializer;
import com.gitee.sop.gatewaycommon.message.ErrorFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

/**
 * @author tanghc
 */
public class AbstractConfiguration implements ApplicationContextAware {
    @Autowired
    protected Environment environment;

    @Autowired
    protected RouteManager apiMetaManager;

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;
    }

    @PostConstruct
    public final void after() {
        if (RouteRepositoryContext.getRouteRepository() == null) {
            throw new IllegalArgumentException("RouteRepositoryContext.setRouteRepository()方法未使用");
        }
        EnvironmentContext.setEnvironment(environment);
        ZookeeperContext.setEnvironment(environment);

        initMessage();
        initBeanInitializer();
        apiMetaManager.refresh();
        doAfter();

    }

    protected void initBeanInitializer() {
        String[] beanNames = applicationContext.getBeanNamesForType(BeanInitializer.class);
        if (beanNames != null) {
            for (String beanName : beanNames) {
                BeanInitializer beanInitializer = applicationContext.getBean(beanName, BeanInitializer.class);
                beanInitializer.load();
            }
        }
    }

    protected void doAfter() {

    }

    protected void initMessage() {
        ErrorFactory.initMessageSource(ApiContext.getApiConfig().getI18nModules());
    }
}
