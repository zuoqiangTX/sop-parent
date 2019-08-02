package com.gitee.sop.gateway.loadbalancer;

import com.netflix.loadbalancer.IRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.core.env.Environment;

/**
 * 自定义PropertiesFactory，用来动态添加LoadBalance规则
 * @author tanghc
 */
public class SopPropertiesFactory extends PropertiesFactory {

    /**
     * 可在配置文件中设置<code>zuul.custom-rule-classname=com.xx.ClassName</code>指定负载均衡规则类
     * 默认使用com.gitee.sop.gateway.loadbalancer.PreEnvironmentServerChooser
     */
    private static final String PROPERTIES_KEY = "zuul.custom-rule-classname";

    private static final String CUSTOM_RULE_CLASSNAME = EnvironmentServerChooser.class.getName();

    @Autowired
    private Environment environment;

    /**
     * 配置文件配置：<serviceId>.ribbon.NFLoadBalancerRuleClassName=com.gitee.sop.gateway.loadbalancer.EnvironmentServerChooser
     * @param clazz
     * @param name serviceId
     * @return 返回class全限定名
     */
    @Override
    public String getClassName(Class clazz, String name) {
        if (clazz == IRule.class) {
            return this.environment.getProperty(PROPERTIES_KEY, CUSTOM_RULE_CLASSNAME);
        } else {
            return super.getClassName(clazz, name);
        }
    }
}
