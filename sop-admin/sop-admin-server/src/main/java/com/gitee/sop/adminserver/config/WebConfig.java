package com.gitee.sop.adminserver.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gitee.easyopen.ApiConfig;
import com.gitee.easyopen.ResultSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


/**
 * @author thc
 */
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {


    @Bean
    ApiConfig apiConfig() {
        ApiConfig apiConfig = new ApiConfig();
        apiConfig.setJsonResultSerializer(obj -> {
            return JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat);
        });
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