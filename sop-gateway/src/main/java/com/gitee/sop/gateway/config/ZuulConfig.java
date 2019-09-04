package com.gitee.sop.gateway.config;

/**
 * 使用Spring Cloud Zuul，推荐使用
 *
 * 注意：下面两个只能使用一个
 */

import com.gitee.sop.gateway.loadbalancer.SopPropertiesFactory;
import com.gitee.sop.gatewaycommon.zuul.configuration.AlipayZuulConfiguration;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 开通支付宝开放平台能力
 * @author tanghc
 */
@Configuration
public class ZuulConfig extends AlipayZuulConfiguration {

    @Bean
    PropertiesFactory propertiesFactory() {
        return new SopPropertiesFactory();
    }

}

/**
 * 开通淘宝开放平台能力
 * @author tanghc
 */
//@Configuration
//public class ZuulConfig extends TaobaoZuulConfiguration {
//  static {
//        new ManagerInitializer();
//        }
//}

/**
 * 对接easyopen
 */
//@Configuration
//public class ZuulConfig extends EasyopenZuulConfiguration {
//    static {
//        Map<String, String> appSecretMap = new HashMap<>();
//        appSecretMap.put("easyopen_test", "G9w0BAQEFAAOCAQ8AMIIBCgKCA");
//        ApiConfig.getInstance().addAppSecret(appSecretMap);
//    }
//
//}
