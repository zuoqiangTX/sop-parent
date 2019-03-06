package com.gitee.sop.gatewaycommon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.filter.LoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.containsEncodedParts;

/**
 * 在LoadBalancerClientFilter后面处理，处理成我们想要的uri
 *
 * @author tanghc
 */
@Slf4j
public class LoadBalancerClientExtFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER + 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        URI routeUri = route.getUri();

        URI requestUrl = url;

        String uriStr = routeUri.toString();
        String[] uriArr = uriStr.split("\\#");
        if (uriArr.length == 2) {
            String path = uriArr[1];
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(url);
            if (StringUtils.hasLength(path)) {
                uriComponentsBuilder.path(path);
            }
            requestUrl = uriComponentsBuilder.build(true).toUri();
        }

        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
        return chain.filter(exchange);
    }

}
