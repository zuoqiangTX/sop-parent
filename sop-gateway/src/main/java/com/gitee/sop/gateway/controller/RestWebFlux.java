package com.gitee.sop.gateway.controller;

import com.gitee.sop.gatewaycommon.param.ParamNames;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

/**
 * @author tanghc
 */
@Configuration
public class RestWebFlux {

    @Value("${sop.restful.path:/rest}")
    private String restPath;

    /**
     * 307 Temporary Redirect（临时重定向）:
     * <p>
     * 在这种情况下，请求应该与另一个URI重复，但后续的请求应仍使用原始的URI。
     * 与302相反，当重新发出原始请求时，不允许更改请求方法。 例如，应该使用另一个POST请求来重复POST请求
     * <p>
     * 308 Permanent Redirect （永久重定向）:
     * <p>
     * 请求和所有将来的请求应该使用另一个URI重复。
     * 307和308重复302和301的行为，但不允许HTTP方法更改。 例如，将表单提交给永久重定向的资源可能会顺利进行。
     * <p>
     * https://www.cnblogs.com/wuguanglin/p/redirect.html
     *
     * @return
     */
    @Bean
    RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route(RequestPredicates.GET(restPath + "/**"), (serverRequest) -> {
            String url = serverRequest.path();
            int index = url.indexOf(restPath);
            // 取/rest的后面部分
            String path = url.substring(index + restPath.length());
            String query = ParamNames.API_NAME + "=" + path + "&" + ParamNames.VERSION_NAME + "=";
            return ServerResponse
                    .temporaryRedirect(URI.create("/?" + query))
                    .build();
        });
    }

}
