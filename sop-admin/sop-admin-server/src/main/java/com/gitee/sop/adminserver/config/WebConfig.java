package com.gitee.sop.adminserver.config;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * @author thc
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {



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