package com.gitee.easyopen.server.config;

import com.gitee.sop.servercommon.configuration.EasyopenDocSupportController;
import com.gitee.sop.servercommon.configuration.EasyopenServiceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;

/**
 * @author tanghc
 */
@Configuration
public class SopConfig extends EasyopenServiceConfiguration {

    @Controller
    public static class SopDocController extends EasyopenDocSupportController {
        @Override
        public String getDocTitle() {
            return "商品API";
        }
    }
}
