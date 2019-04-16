package com.gitee.sop.gateway.config;

/**
 * 使用Spring Cloud Zuul，推荐使用
 *
 * 注意：下面两个只能使用一个
 */

import com.gitee.sop.gateway.manager.ManagerInitializer;
import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.zuul.configuration.AlipayZuulConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 开通支付宝开放平台能力
 * @author tanghc
 */
@Configuration
public class ZuulConfig extends AlipayZuulConfiguration {

    {
        Map<String, String> appSecretStore = new HashMap();
        // 这里存放商户给的公钥，同时商户那边会存对应的私钥
        // key：应用ID(app_id)：建议个格式为`yyyyMMddHHmmss+自增ID`，如2019032617262200001
        // value：公钥
        appSecretStore.put("2019032617262200001", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlyb9aUBaljQP/vjmBFe1mF8HsWSvyfC2NTlpT/V9E+sBxTr8TSkbzJCeeeOEm4LCaVXL0Qz63MZoT24v7AIXTuMdj4jyiM/WJ4tjrWAgnmohNOegfntTto16C3l234vXz4ryWZMR/7W+MXy5B92wPGQEJ0LKFwNEoLspDEWZ7RdE53VH7w6y6sIZUfK+YkXWSwehfKPKlx+lDw3zRJ3/yvMF+U+BAdW/MfECe1GuBnCFKnlMRh3UKczWyXWkL6ItOpYHHJi/jx85op5BWDje2pY9QowzfN94+0DB3T7UvZeweu3zlP6diwAJDzLaFQX8ULfWhY+wfKxIRgs9NoiSAQIDAQAB");
        ApiContext.getApiConfig().addAppSecret(appSecretStore);
    }

    @Autowired
    ManagerInitializer managerInitializer;

    @Override
    protected void doAfter() {
        managerInitializer.init();
    }

}

/**
 * 开通淘宝开放平台能力
 * @author tanghc
 */
//@Configuration
//public class ZuulConfig extends TaobaoZuulConfiguration {
//
//    {
//        Map<String, String> appSecretStore = new HashMap();
//        appSecretStore.put("taobao_test", "G9w0BAQEFAAOCAQ8AMIIBCgKCA");
//        ApiContext.getApiConfig().addAppSecret(appSecretStore);
//    }
//}

/**
 * 对接easyopen
 */
//@Configuration
//public class ZuulConfig extends EasyopenZuulConfiguration {
//    {
//        Map<String, String> appSecretStore = new HashMap();
//        appSecretStore.put("easyopen_test", "G9w0BAQEFAAOCAQ8AMIIBCgKCA");
//        ApiContext.getApiConfig().addAppSecret(appSecretStore);
//    }
//}
