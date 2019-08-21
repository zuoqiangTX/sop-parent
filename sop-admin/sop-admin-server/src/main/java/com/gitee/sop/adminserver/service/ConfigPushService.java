package com.gitee.sop.adminserver.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.gitee.sop.adminserver.bean.ChannelMsg;
import com.gitee.sop.adminserver.common.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author tanghc
 */
@Slf4j
@Service
public class ConfigPushService {

    @NacosInjected
    private ConfigService configService;

    public void publishConfig(String dataId, String groupId, ChannelMsg channelMsg) {
        try {
            log.info("nacos配置, dataId={}, groupId={}, operation={}", dataId, groupId, channelMsg.getOperation());
            configService.publishConfig(dataId, groupId, JSON.toJSONString(channelMsg));
        } catch (NacosException e) {
            log.error("nacos配置失败, dataId={}, groupId={}, operation={}", dataId, groupId, channelMsg.getOperation(), e);
            throw new BizException("nacos配置失败");
        }
    }

}
