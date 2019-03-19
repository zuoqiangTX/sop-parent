package com.gitee.sop.adminserver.config;

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



    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //配置映射关系
        registry.addResourceHandler("/conf/**").addResourceLocations("classpath:/META-INF/resources/webjars/sop-admin-front/1.0.0-SNAPSHOT/");
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