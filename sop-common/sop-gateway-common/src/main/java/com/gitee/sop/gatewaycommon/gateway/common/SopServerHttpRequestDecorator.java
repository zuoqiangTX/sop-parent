package com.gitee.sop.gatewaycommon.gateway.common;

import io.netty.buffer.ByteBufAllocator;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

/**
 * @author tanghc
 */
public class SopServerHttpRequestDecorator extends ServerHttpRequestDecorator {

    private Flux<DataBuffer> bodyData;
    private HttpHeaders httpHeaders;

    public SopServerHttpRequestDecorator(ServerHttpRequest delegate, byte[] bodyData, HttpHeaders httpHeaders) {
        super(delegate);
        if (httpHeaders == null) {
            throw new IllegalArgumentException("httpHeaders can not be null.");
        }
        if (bodyData == null) {
            throw new IllegalArgumentException("bodyData can not be null.");
        }
        // 由于请求体已改变，这里要重新设置contentLength
        int contentLength = bodyData.length;
        httpHeaders.setContentLength(contentLength);

        if (contentLength <= 0) {
            // TODO: this causes a 'HTTP/1.1 411 Length Required' on httpbin.org
            httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
        }

        this.httpHeaders = httpHeaders;
        this.bodyData = stringBuffer(bodyData);
    }

    /**
     * 字符串转DataBuffer
     * @param bytes 请求体
     * @return 返回buffer
     */
    private static Flux<DataBuffer> stringBuffer(byte[] bytes) {
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return Flux.just(buffer);
    }

    private SopServerHttpRequestDecorator(ServerHttpRequest delegate) {
        super(delegate);
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return bodyData == null ? super.getBody() : bodyData;
    }

    @Override
    public HttpHeaders getHeaders() {
        return httpHeaders == null ? super.getHeaders() : httpHeaders;
    }
}
