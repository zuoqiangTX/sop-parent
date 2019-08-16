package com.gitee.sop.gatewaycommon.gateway;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.gatewaycommon.bean.SopConstants;
import com.gitee.sop.gatewaycommon.gateway.common.FileUploadHttpServletRequest;
import com.gitee.sop.gatewaycommon.gateway.common.SopServerHttpRequestDecorator;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.util.RequestUtil;
import io.netty.buffer.ByteBufAllocator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.gitee.sop.gatewaycommon.bean.SopConstants.CACHE_REQUEST_BODY_FOR_MAP;
import static com.gitee.sop.gatewaycommon.bean.SopConstants.CACHE_REQUEST_BODY_OBJECT_KEY;

/**
 * @author tanghc
 */
public class ServerWebExchangeUtil {

    /**
     * 获取请求参数
     * @param exchange ServerWebExchange
     * @return 返回请求参数
     */
    public static ApiParam getApiParam(ServerWebExchange exchange) {
        return exchange.getAttribute(SopConstants.CACHE_API_PARAM);
    }

    /**
     * 设置请求参数
     * @param exchange ServerWebExchange
     * @param apiParam 请求参数
     */
    public static void setApiParam(ServerWebExchange exchange, ApiParam apiParam) {
        exchange.getAttributes().put(SopConstants.CACHE_API_PARAM, apiParam);
    }

    /**
     * 获取Spring Cloud Gateway请求的原始参数。前提是要使用ReadBodyRoutePredicateFactory
     * @param exchange ServerWebExchange
     * @return 没有参数返回null
     * @see com.gitee.sop.gatewaycommon.gateway.route.ReadBodyRoutePredicateFactory
     */
    public static Map<String, ?> getRequestParams(ServerWebExchange exchange) {
        Map<String, ?> params = exchange.getAttribute(CACHE_REQUEST_BODY_FOR_MAP);
        if (params != null) {
            return params;
        }
        if (exchange.getRequest().getMethod() == HttpMethod.GET) {
            MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
            params = buildParams(queryParams);
        } else {
            String cachedBody = exchange.getAttribute(CACHE_REQUEST_BODY_OBJECT_KEY);
            if (cachedBody != null) {
                MediaType contentType = exchange.getRequest().getHeaders().getContentType();
                String contentTypeStr = contentType == null ? "" : contentType.toString().toLowerCase();
                // 如果是json方式提交
                if (StringUtils.containsAny(contentTypeStr, "json", "text")) {
                    params = JSON.parseObject(cachedBody);
                } else if (StringUtils.containsIgnoreCase(contentTypeStr, "multipart")) {
                    // 如果是文件上传请求
                    HttpServletRequest fileUploadRequest = getFileUploadRequest(exchange, cachedBody);
                    params = RequestUtil.convertMultipartRequestToMap(fileUploadRequest);
                } else {
                    params = RequestUtil.parseQueryToMap(cachedBody);
                }
            }
        }
        if (params != null) {
            exchange.getAttributes().put(CACHE_REQUEST_BODY_FOR_MAP, params);
        }
        return params;
    }

    public static Map<String, String> buildParams(MultiValueMap<String, String> queryParams) {
        if (queryParams == null || queryParams.size() == 0) {
            return null;
        }
        Map<String, String> params = new HashMap<>(queryParams.size());
        for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            params.put(entry.getKey(), entry.getValue().get(0));
        }
        return params;
    }

    /**
     * 添加header
     * @param exchange 当前ServerWebExchange
     * @param headersConsumer headers
     * @return 返回一个新的ServerWebExchange
     */
    public static ServerWebExchange addHeaders(ServerWebExchange exchange, Consumer<HttpHeaders> headersConsumer) {
        // 创建一个新的request
        ServerHttpRequest serverHttpRequestNew = exchange.getRequest()
                .mutate()
                .headers(headersConsumer)
                .build();
        // 将现在的request 变成 change对象
        return exchange
                .mutate()
                .request(serverHttpRequestNew)
                .build();
    }

    /**
     * 获取一个文件上传request
     *
     * @param exchange 当前ServerWebExchange
     * @param requestBody 上传文件请求体内容
     * @return 返回文件上传request
     */
    public static HttpServletRequest getFileUploadRequest(ServerWebExchange exchange, String requestBody) {
        byte[] data = requestBody.getBytes(StandardCharsets.UTF_8);
        return  new FileUploadHttpServletRequest(exchange.getRequest(), data);
    }

    /**
     * 修改请求参数。参考自：https://blog.csdn.net/fuck487/article/details/85166162
     * @param exchange ServerWebExchange
     * @param apiParam 请求参数
     * @param paramsConsumer 执行参数更改
     * @param headerConsumer header更改
     * @param <T> 参数类型
     * @return 返回新的ServerWebExchange，参数没有被修改则返回null
     */
    public static <T extends Map<String, Object>> ServerWebExchange format(
            ServerWebExchange exchange
            , T apiParam
            , Consumer<T> paramsConsumer
            , Consumer<HttpHeaders> headerConsumer
    ) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        if (serverHttpRequest.getMethod() == HttpMethod.GET) {
            paramsConsumer.accept(apiParam);

        } else {
            MediaType mediaType = serverHttpRequest.getHeaders().getContentType();
            if (mediaType == null) {
                return null;
            }
            paramsConsumer.accept(apiParam);
            String contentType = mediaType.toString().toLowerCase();
            if (StringUtils.containsAny(contentType, "json", "text")) {
                //下面的将请求体再次封装写回到request里，传到下一级，否则，由于请求体已被消费，后续的服务将取不到值
                URI uri = serverHttpRequest.getURI();
                URI newUri = UriComponentsBuilder.fromUri(uri).build(true).toUri();
                ServerHttpRequest request = exchange.getRequest().mutate().uri(newUri).build();

                // 定义新的消息头
                HttpHeaders headers = new HttpHeaders();
                headers.putAll(exchange.getRequest().getHeaders());

                // 自定义header
                headerConsumer.accept(headers);
                // 修改后的请求体
                String bodyStr = JSON.toJSONString(apiParam);
                byte[] bodyStrBytes = bodyStr.getBytes(StandardCharsets.UTF_8);

                // 由于post的body只能订阅一次，由于上面代码中已经订阅过一次body。
                // 所以要再次封装请求到request才行，不然会报错请求已经订阅过
                request = new SopServerHttpRequestDecorator(request, bodyStrBytes, headers);

                return exchange.mutate().request(request).build();
            } else {

            }
        }
        return null;
    }

    /**
     * 字符串转DataBuffer
     * @param value 值
     * @return 返回buffer
     */
    private static DataBuffer stringBuffer(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }

}
