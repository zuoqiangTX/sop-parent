package com.gitee.sop.adminserver.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gitee.easyopen.ApiConfig;
import com.gitee.easyopen.interceptor.ApiInterceptor;
import com.gitee.easyopen.session.ApiSessionManager;
import com.gitee.sop.adminserver.interceptor.LoginInterceptor;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


/**
 * @author thc
 */
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    @Value("${admin.access-token.timeout-minutes}")
    private String accessTokenTimeout;

    @Bean
    ApiConfig apiConfig() {
        ApiConfig apiConfig = new ApiConfig();
        apiConfig.setJsonResultSerializer(obj -> {
            return JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat);
        });
        ApiSessionManager apiSessionManager = new ApiSessionManager();
        // session有效期
        int timeout = NumberUtils.toInt(accessTokenTimeout, 30);
        apiSessionManager.setSessionTimeout(timeout);
        apiConfig.setSessionManager(apiSessionManager);
        // 登录拦截器
        apiConfig.setInterceptors(new ApiInterceptor[]{new LoginInterceptor()});
        return apiConfig;
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //配置映射关系
        registry.addResourceHandler("/conf/**").addResourceLocations("classpath:/META-INF/resources/webjars/sop-admin-front/1.0.0-SNAPSHOT/");
        registry.addResourceHandler("/opendoc/**").addResourceLocations("classpath:/META-INF/resources/opendoc/");
    }

    @Controller
    public static class ConfController {

        private static final String REDIRECT_INDEX = "redirect:/conf/index.html";

        @GetMapping("/")
        public String index() {
            return REDIRECT_INDEX;
        }

        @GetMapping("/conf")
        public String conf() {
            return REDIRECT_INDEX;
        }
    }
}