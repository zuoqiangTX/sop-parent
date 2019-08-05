package com.gitee.sop.gateway.manager;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.gateway.mapper.IPBlacklistMapper;
import com.gitee.sop.gatewaycommon.bean.ChannelMsg;
import com.gitee.sop.gatewaycommon.manager.DefaultIPBlacklistManager;
import com.gitee.sop.gatewaycommon.manager.ZookeeperContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
public class DbIPBlacklistManager extends DefaultIPBlacklistManager {

    @Autowired
    IPBlacklistMapper ipBlacklistMapper;

    @Autowired
    Environment environment;

    @Override
    public void load() {
        List<String> ipList = ipBlacklistMapper.listAllIP();
        log.info("加载IP黑名单, size:{}", ipList.size());
        ipList.stream().forEach(this::add);

    }

    @PostConstruct
    protected void after() throws Exception {
        ZookeeperContext.setEnvironment(environment);
        String path = ZookeeperContext.getIpBlacklistChannelPath();
        ZookeeperContext.listenPath(path, nodeCache -> {
            String nodeData = new String(nodeCache.getCurrentData().getData());
            ChannelMsg channelMsg = JSON.parseObject(nodeData, ChannelMsg.class);
            final IPDto ipDto = JSON.parseObject(channelMsg.getData(), IPDto.class);
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
                    log.error("IP黑名单，错误的消息指令，nodeData：{}", nodeData);
            }
        });
    }

    @Data
    private static class IPDto {
        private String ip;
    }

}
