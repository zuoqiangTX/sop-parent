package com.gitee.sop.servercommon.configuration;

import com.gitee.sop.servercommon.bean.ServiceConfig;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.List;

/**
 * 具备支付宝开放平台服务提供能力
 * @author tanghc
 */
public class AlipayServerConfiguration extends BaseServerConfiguration {

    public AlipayServerConfiguration() {
        // 默认版本号为1.0
        ServiceConfig.getInstance().setDefaultVersion("1.0");
    }

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(ServiceConfig.getInstance().getMethodArgumentResolver());
    }

}
