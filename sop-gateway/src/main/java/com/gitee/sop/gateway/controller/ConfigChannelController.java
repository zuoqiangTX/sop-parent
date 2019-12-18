package com.gitee.sop.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.gateway.manager.DbEnvGrayManager;
import com.gitee.sop.gateway.manager.DbIPBlacklistManager;
import com.gitee.sop.gateway.manager.DbIsvManager;
import com.gitee.sop.gateway.manager.DbIsvRoutePermissionManager;
import com.gitee.sop.gateway.manager.DbLimitConfigManager;
import com.gitee.sop.gateway.manager.DbRouteConfigManager;
import com.gitee.sop.gatewaycommon.bean.GatewayPushDTO;
import com.gitee.sop.gatewaycommon.bean.NacosConfigs;
import com.gitee.sop.gatewaycommon.bean.SpringContext;
import com.gitee.sop.gatewaycommon.manager.ChannelMsgProcessor;
import com.gitee.sop.gatewaycommon.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 通过nacos查出所有节点，然后进行配置的修改
 *
 * @author tanghc
 */
@Slf4j
@RestController
public class ConfigChannelController {

    private static Map<String, Class<? extends ChannelMsgProcessor>> processorMap = new HashMap<>(16);

    static {
        processorMap.put(NacosConfigs.GROUP_CHANNEL + NacosConfigs.DATA_ID_GRAY, DbEnvGrayManager.class);
        processorMap.put(NacosConfigs.GROUP_CHANNEL + NacosConfigs.DATA_ID_IP_BLACKLIST, DbIPBlacklistManager.class);
        processorMap.put(NacosConfigs.GROUP_CHANNEL + NacosConfigs.DATA_ID_ISV, DbIsvManager.class);
        processorMap.put(NacosConfigs.GROUP_CHANNEL + NacosConfigs.DATA_ID_ROUTE_PERMISSION, DbIsvRoutePermissionManager.class);
        processorMap.put(NacosConfigs.GROUP_CHANNEL + NacosConfigs.DATA_ID_LIMIT_CONFIG, DbLimitConfigManager.class);
        processorMap.put(NacosConfigs.GROUP_CHANNEL + NacosConfigs.DATA_ID_ROUTE_CONFIG, DbRouteConfigManager.class);
    }

    @Value("${zuul.secret}")
    private String secret;

    /**
     * 网关配置修改
     *
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/configChannelMsg")
    public String configChannel(HttpServletRequest request) throws IOException {
        String requestJson = RequestUtil.getText(request);
        String sign = request.getHeader("sign");
        try {
            RequestUtil.checkResponseBody(requestJson, sign, secret);
        } catch (Exception e) {
            log.error("configChannelMsg错误", e);
            return e.getMessage();
        }
        GatewayPushDTO gatewayPushDTO = JSON.parseObject(requestJson, GatewayPushDTO.class);
        ChannelMsgProcessor channelMsgProcessor = getChannelMsgProcessor(gatewayPushDTO);
        //进行具体的推送工作
        channelMsgProcessor.process(gatewayPushDTO.getChannelMsg());
        return "ok";
    }

    private ChannelMsgProcessor getChannelMsgProcessor(GatewayPushDTO gatewayPushDTO) {
        //查出操作类型
        String key = gatewayPushDTO.getGroupId() + gatewayPushDTO.getDataId();
        //选择具体的处理类
        Class<? extends ChannelMsgProcessor> aClass = processorMap.get(key);
        return SpringContext.getBean(aClass);
    }

}
