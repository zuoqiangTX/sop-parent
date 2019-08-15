package com.gitee.sop.gateway.controller;

import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import com.gitee.sop.gatewaycommon.bean.ErrorEntity;
import com.gitee.sop.gatewaycommon.manager.ServiceErrorManager;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.result.ApiResult;
import com.gitee.sop.gatewaycommon.result.JsonResult;
import com.gitee.sop.gatewaycommon.validate.taobao.TaobaoSigner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author tanghc
 */
@RestController
public class ErrorLogController {

    TaobaoSigner signer = new TaobaoSigner();

    @Value("${zuul.secret}")
    private String secret;

    @GetMapping("listErrors")
    public ApiResult listErrors(ServerWebExchange request) {
        try {
            this.check(request);
            ServiceErrorManager serviceErrorManager = ApiConfig.getInstance().getServiceErrorManager();
            Collection<ErrorEntity> allErrors = serviceErrorManager.listAllErrors();
            JsonResult apiResult = new JsonResult();
            apiResult.setData(allErrors);
            return apiResult;
        } catch (Exception e) {
            ApiResult apiResult = new ApiResult();
            apiResult.setCode("505050");
            apiResult.setMsg(e.getMessage());
            return apiResult;
        }
    }

    @GetMapping("clearErrors")
    public ApiResult clearErrors(ServerWebExchange request) {
        try {
            this.check(request);
            ServiceErrorManager serviceErrorManager = ApiConfig.getInstance().getServiceErrorManager();
            serviceErrorManager.clear();
            return new ApiResult();
        } catch (Exception e) {
            ApiResult apiResult = new ApiResult();
            apiResult.setCode("505050");
            apiResult.setMsg(e.getMessage());
            return apiResult;
        }
    }

    private void check(ServerWebExchange request) {
        MultiValueMap<String, String> queryParams = request.getRequest().getQueryParams();
        ApiParam apiParam = new ApiParam();
        for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            apiParam.put(entry.getKey(), entry.getValue().get(0));
        }
        boolean right = signer.checkSign(apiParam, secret);
        if (!right) {
            throw new RuntimeException("签名校验失败");
        }
    }

}
