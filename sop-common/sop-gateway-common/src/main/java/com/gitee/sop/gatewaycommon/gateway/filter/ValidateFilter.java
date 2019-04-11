package com.gitee.sop.gatewaycommon.gateway.filter;

import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.bean.RouteConfig;
import com.gitee.sop.gatewaycommon.bean.SopConstants;
import com.gitee.sop.gatewaycommon.exception.ApiException;
import com.gitee.sop.gatewaycommon.manager.RouteConfigManager;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.util.RouteUtil;
import com.gitee.sop.gatewaycommon.validate.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author tanghc
 */
@Slf4j
public class ValidateFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ApiConfig apiConfig = ApiContext.getApiConfig();
        // 解析参数
        ApiParam param = apiConfig.getGatewayParamBuilder().build(exchange);
        exchange.getAttributes().put(SopConstants.CACHE_API_PARAM, param);
        RouteUtil.checkEnable(param);
        // 验证操作，这里有负责验证签名参数
        Validator validator = apiConfig.getValidator();
        try {
            validator.validate(param);
        } catch (ApiException e) {
            log.error("验证失败，params:{}", param.toJSONString(), e);
            throw e;
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 最优先执行
        return Orders.VALIDATE_ORDER;
    }
}
