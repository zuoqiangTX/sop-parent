package com.gitee.sop.adminserver.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.gitee.sop.adminserver.bean.ChannelMsg;
import com.gitee.sop.adminserver.bean.GatewayPushDTO;
import com.gitee.sop.adminserver.bean.HttpTool;
import com.gitee.sop.adminserver.common.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tanghc
 */
@Slf4j
@Service
public class ConfigPushService {

    private static final String GATEWAY_PUSH_URL = "http://%s/configChannelMsg";

    private static HttpTool httpTool = new HttpTool();

    @NacosInjected
    private ConfigService configService;

    @Value("${gateway.host:}")
    private String gatewayHost;

    @Value("${zuul.secret}")
    private String secret;

    public void publishConfig(String dataId, String groupId, ChannelMsg channelMsg) {
        if (StringUtils.isNotBlank(gatewayHost)) {
            String[] hosts = gatewayHost.split(",");
            for (String host : hosts) {
                GatewayPushDTO gatewayPushDTO = new GatewayPushDTO(dataId, groupId, channelMsg);
                String url = String.format(GATEWAY_PUSH_URL, host);
                try {
                    String requestBody = JSON.toJSONString(gatewayPushDTO);
                    Map<String, String> header = new HashMap<>(8);
                    header.put("sign", buildRequestBodySign(requestBody, secret));
                    String resp = httpTool.requestJson(url, requestBody, header);
                    if (!"ok".equals(resp)) {
                        throw new IOException(resp);
                    }
                } catch (IOException e) {
                    log.error("nacos配置失败, dataId={}, groupId={}, operation={}， url={}", dataId, groupId, channelMsg.getOperation(), url, e);
                    throw new BizException("推送配置失败");
                }
            }
        } else {
            try {
                log.info("nacos配置, dataId={}, groupId={}, operation={}", dataId, groupId, channelMsg.getOperation());
                configService.publishConfig(dataId, groupId, JSON.toJSONString(channelMsg));
            } catch (NacosException e) {
                log.error("nacos配置失败, dataId={}, groupId={}, operation={}", dataId, groupId, channelMsg.getOperation(), e);
                throw new BizException("nacos配置失败");
            }
        }

    }

    public static String buildRequestBodySign(String requestBody, String secret) {
        String signContent = secret + requestBody + secret;
        return DigestUtils.md5Hex(signContent);
    }

}
