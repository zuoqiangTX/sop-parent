package com.gitee.sop.gatewaycommon.easyopen;

import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.gateway.configuration.BaseGatewayConfiguration;
import com.gitee.sop.gatewaycommon.param.ParamNames;
import com.gitee.sop.gatewaycommon.validate.taobao.TaobaoSigner;
import com.gitee.sop.gatewaycommon.zuul.configuration.BaseZuulConfiguration;

/**
 * @author tanghc
 */
public class EasyopenZuulConfiguration extends BaseZuulConfiguration {

    static {
        ParamNames.APP_KEY_NAME = "app_key";
        ParamNames.API_NAME = "name";
        ParamNames.SIGN_TYPE_NAME = "sign_type";
        ParamNames.APP_AUTH_TOKEN_NAME = "access_token";
        ApiConfig apiConfig = ApiContext.getApiConfig();
        apiConfig.setSigner(new EasyopenSigner());
        apiConfig.setZuulResultExecutor(new EasyopenResultExecutor());
        apiConfig.setMergeResult(false);
    }

}
