package com.gitee.sop.gatewaycommon.gateway.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 * @author tanghc
 */
@Slf4j
public class ServletFileUploadUtil {

    public static HttpServletRequest getFileUploadRequest(ServerWebExchange exchange, String requestBody) {
        byte[] data = requestBody.getBytes(StandardCharsets.UTF_8);
        return  new FileUploadHttpServletRequest(exchange.getRequest(), data);
    }

}
