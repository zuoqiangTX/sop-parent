package com.gitee.app.config;

import com.gitee.sop.servercommon.configuration.AlipayServiceConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 使用支付宝开放平台功能
 *
 * @author tanghc
 */
public class OpenServiceConfig extends AlipayServiceConfiguration {

    @Bean
    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        return super.requestMappingHandlerMapping();
    }

}
