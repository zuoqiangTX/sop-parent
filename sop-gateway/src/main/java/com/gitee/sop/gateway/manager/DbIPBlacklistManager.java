package com.gitee.sop.gateway.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.gitee.sop.gateway.mapper.IPBlacklistMapper;
import com.gitee.sop.gatewaycommon.bean.ChannelMsg;
import com.gitee.sop.gatewaycommon.bean.NacosConfigs;
import com.gitee.sop.gatewaycommon.manager.DefaultIPBlacklistManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 限流配置管理
 *
 * @author tanghc
 */
@Slf4j
@Service
public class DbIPBlacklistManager extends DefaultIPBlacklistManager implements ChannelMsgProcessor {

    @Autowired
    private IPBlacklistMapper ipBlacklistMapper;

    @NacosInjected
    private ConfigService configService;

    @Override
    public void load() {
        List<String> ipList = ipBlacklistMapper.listAllIP();
        log.info("加载IP黑名单, size:{}", ipList.size());
        ipList.forEach(this::add);

    }

    @Override
    public void process(ChannelMsg channelMsg) {
        final IPDto ipDto = channelMsg.toObject(IPDto.class);
        String ip = ipDto.getIp();
        switch (channelMsg.getOperation()) {
            case "add":
                log.info("添加IP黑名单，ip:{}", ip);
                add(ip);
                break;
            case "delete":
                log.info("移除IP黑名单，ip:{}", ip);
                remove(ip);
                break;
            default:
        }
    }

    @PostConstruct
    protected void after() throws Exception {
        configService.addListener(NacosConfigs.DATA_ID_IP_BLACKLIST, NacosConfigs.GROUP_CHANNEL, new AbstractListener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
            ChannelMsg channelMsg = JSON.parseObject(configInfo, ChannelMsg.class);
            process(channelMsg);
            }
        });
    }

    @Data
    private static class IPDto {
        private String ip;
    }
}
