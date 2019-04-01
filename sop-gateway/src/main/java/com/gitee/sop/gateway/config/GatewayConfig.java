package com.gitee.sop.gateway.config;

import com.gitee.sop.gateway.entity.IsvInfo;
import com.gitee.sop.gateway.manager.ManagerInitializer;
import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.gateway.configuration.AlipayGatewayConfiguration;
import com.gitee.sop.gatewaycommon.manager.IsvRoutePermissionManager;
import com.gitee.sop.gatewaycommon.secret.IsvManager;
import com.gitee.sop.gatewaycommon.zuul.configuration.AlipayZuulConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * 使用Spring Cloud Gateway
 *
 * 注意：下面两个只能使用一个。
 *
 * 使用前，前往启动类SopGatewayApplication.java 注释掉@EnableZuulProxy
 */

/**
 * 开通支付宝开放平台能力
 * @author tanghc
 */

//@Configuration
public class GatewayConfig extends AlipayGatewayConfiguration {

    {
        Map<String, String> appSecretStore = new HashMap();
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
 * 开通淘宝开放平能力
 */
//@Configuration
//public class GatewayConfig extends TaobaoGatewayConfiguration {
//
//    {
//        Map<String, String> appSecretStore = new HashMap();
//        appSecretStore.put("taobao_test", "G9w0BAQEFAAOCAQ8AMIIBCgKCA");
//        ApiContext.getApiConfig().addAppSecret(appSecretStore);
//    }
//}

