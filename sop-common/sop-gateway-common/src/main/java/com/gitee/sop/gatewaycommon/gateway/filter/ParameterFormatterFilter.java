package com.gitee.sop.gatewaycommon.gateway.filter;

import com.gitee.sop.gatewaycommon.gateway.ServerWebExchangeUtil;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.param.ParamNames;
import com.gitee.sop.gatewaycommon.param.ParameterFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.gitee.sop.gatewaycommon.gateway.filter.Orders.PARAMETER_FORMATTER_FILTER_ORDER;

/**
 * @author tanghc
 */
@Slf4j
public class ParameterFormatterFilter implements GlobalFilter, Ordered {

    @Autowired(required = false)
    private ParameterFormatter sopParameterFormatter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ApiParam apiParam = ServerWebExchangeUtil.getApiParam(exchange);
        if (apiParam == null) {
            return chain.filter(exchange);
        }
        // 校验成功后进行参数转换
        if (sopParameterFormatter != null) {
            ServerWebExchange formatExchange = ServerWebExchangeUtil.format(
                    exchange
                    , apiParam
                    , sopParameterFormatter::format
                    , httpHeaders -> {
                        httpHeaders.remove(ParamNames.HEADER_VERSION_NAME);
                        httpHeaders.add(ParamNames.HEADER_VERSION_NAME, apiParam.fetchVersion());
                    });
            if (formatExchange == null) {
                return chain.filter(exchange);
            }
            return chain.filter(formatExchange);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return PARAMETER_FORMATTER_FILTER_ORDER;
    }
}
