package com.gitee.sop.gatewaycommon.route;

import com.gitee.sop.gatewaycommon.bean.SopConstants;
import com.gitee.sop.gatewaycommon.param.ParamNames;
import com.gitee.sop.gatewaycommon.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author tanghc
 */
@Slf4j
public class NameVersionRoutePredicateFactory extends AbstractRoutePredicateFactory<NameVersionRoutePredicateFactory.Config> {

    public static final String PARAM_KEY = "param";
    public static final String REGEXP_KEY = "regexp";

    private static final String CACHE_REQUEST_BODY_OBJECT_KEY = SopConstants.CACHE_REQUEST_BODY_OBJECT_KEY;
    public static final String CACHE_REQUEST_BODY_FOR_MAP = SopConstants.CACHE_REQUEST_BODY_FOR_MAP;


    public NameVersionRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(PARAM_KEY, REGEXP_KEY);
    }

    /**
     * config.param为nameVersion
     *
     * @param config
     * @return
     */
    @Override
    public Predicate<ServerWebExchange> apply(Config config) {

        return exchange -> {
            String cachedBody = exchange.getAttribute(CACHE_REQUEST_BODY_OBJECT_KEY);
            if (cachedBody == null) {
                return false;
            }
            Map<String, String> params = exchange.getAttribute(CACHE_REQUEST_BODY_FOR_MAP);
            if (params == null) {
                params = RequestUtil.parseQueryToMap(cachedBody);
                exchange.getAttributes().put(CACHE_REQUEST_BODY_FOR_MAP, params);
            }

            String nameVersion = config.param;
            String name = params.getOrDefault(ParamNames.API_NAME, "");
            String version = params.getOrDefault(ParamNames.VERSION_NAME, "");
            return (name + version).equals(nameVersion);
        };
    }

    @Validated
    public static class Config {
        @NotEmpty
        private String param;

        private String regexp;

        public String getParam() {
            return param;
        }

        public Config setParam(String param) {
            this.param = param;
            return this;
        }

        public String getRegexp() {
            return regexp;
        }

        public Config setRegexp(String regexp) {
            this.regexp = regexp;
            return this;
        }
    }
}
