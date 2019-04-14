package com.gitee.app.config;

import com.gitee.sop.servercommon.bean.ServiceConfig;
import com.gitee.sop.servercommon.configuration.SpringMvcServiceConfiguration;

/**
 * 使用支付宝开放平台功能
 *
 * @author tanghc
 */
public class OpenServiceConfig extends SpringMvcServiceConfiguration {
    static {
        ServiceConfig.getInstance().setDefaultVersion("1.0");
    }
}
