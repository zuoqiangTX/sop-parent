package com.gitee.sop.gatewaycommon.zuul.loadbalancer;

import com.gitee.sop.gatewaycommon.param.ParamNames;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.io.IOException;

/**
 * @author tanghc
 */
public class EnvHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
        String newVersion = HystrixRequestVariableContext.getVersion();
        requestWrapper.getHeaders().add(ParamNames.GRAY_HEADER_VERSION_NAME, newVersion);
        return execution.execute(requestWrapper, body);
    }
}