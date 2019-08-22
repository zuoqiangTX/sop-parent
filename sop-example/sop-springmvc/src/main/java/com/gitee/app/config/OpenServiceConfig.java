package com.gitee.app.config;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.client.naming.utils.NetUtils;
import com.alibaba.nacos.spring.context.annotation.discovery.EnableNacosDiscovery;
import com.gitee.sop.servercommon.bean.ServiceConfig;
import com.gitee.sop.servercommon.configuration.SpringMvcServiceConfiguration;
import lombok.extern.slf4j.Slf4j;

/**
 * 使用支付宝开放平台功能
 *
 * @author tanghc
 */
@Slf4j
@EnableNacosDiscovery(globalProperties = @NacosProperties(serverAddr = "127.0.0.1:8848"))
public class OpenServiceConfig extends SpringMvcServiceConfiguration {
    static {
        ServiceConfig.getInstance().setDefaultVersion("1.0");
    }

    // 这两个参数需要从配置文件中获取
    private String serviceId = "sop-springmvc";
    private int port = 2223;

    @NacosInjected
    private NamingService namingService;

    @Override
    protected void doAfter() {
        super.doAfter();
        try {
            String ip = NetUtils.localIP();
            namingService.registerInstance(serviceId, ip, port);
            log.info("注册到nacos, serviceId:{}, ip:{}, port:{}", serviceId, ip, port);
        } catch (NacosException e) {
            log.error("注册nacos失败", e);
            throw new RuntimeException("注册nacos失败", e);
        }
    }
}
