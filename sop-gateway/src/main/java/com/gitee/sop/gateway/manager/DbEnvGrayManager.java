package com.gitee.sop.gateway.manager;

import com.alibaba.fastjson.JSON;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.sop.gateway.entity.ConfigGrayUserkey;
import com.gitee.sop.gateway.mapper.ConfigGrayUserkeyMapper;
import com.gitee.sop.gatewaycommon.bean.ChannelMsg;
import com.gitee.sop.gatewaycommon.bean.UserKeyDefinition;
import com.gitee.sop.gatewaycommon.manager.DefaultEnvGrayManager;
import com.gitee.sop.gatewaycommon.manager.ZookeeperContext;
import com.gitee.sop.gatewaycommon.zuul.loadbalancer.ServiceGrayConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 存放用户key，这里放在本机内容，如果灰度发布保存的用户id数量偏多，可放在redis中
 *
 * @author tanghc
 */
@Slf4j
@Service
public class DbEnvGrayManager extends DefaultEnvGrayManager {

    private static final int STATUS_ENABLE = 1;

    @Autowired
    private Environment environment;

    @Autowired
    private ConfigGrayUserkeyMapper configGrayUserkeyMapper;

    @Override
    public void load() {
        Query query = new Query();
        query.eq("status", STATUS_ENABLE);
        List<ConfigGrayUserkey> list = configGrayUserkeyMapper.list(query);
        for (ConfigGrayUserkey configGrayUserkey : list) {
            this.setServiceGrayConfig(configGrayUserkey);
            this.addServiceInstance(configGrayUserkey.getServiceId(), configGrayUserkey.getInstanceId());
        }
    }

    /**
     * 设置用户key
     *
     * @param configGrayUserkey 灰度配置
     */
    public void setServiceGrayConfig(ConfigGrayUserkey configGrayUserkey) {
        if (configGrayUserkey == null) {
            return;
        }
        String instanceId = configGrayUserkey.getInstanceId();
        this.clear(instanceId);
        String userKeyData = configGrayUserkey.getUserKeyContent();
        String nameVersionContent = configGrayUserkey.getNameVersionContent();
        String[] userKeys = StringUtils.split(userKeyData, ',');
        String[] nameVersionList = StringUtils.split(nameVersionContent, ',');
        log.info("添加userKey，userKeys.length:{}, nameVersionList:{}", userKeys.length, Arrays.toString(nameVersionList));

        List<String> list = Stream.of(userKeys).collect(Collectors.toList());
        ServiceGrayConfig serviceGrayConfig = getServiceGrayConfig(instanceId);
        serviceGrayConfig.getUserKeys().addAll(list);

        Map<String, String> grayNameVersion = serviceGrayConfig.getGrayNameVersion();
        for (String nameVersion : nameVersionList) {
            String[] nameVersionInfo = StringUtils.split(nameVersion, '=');
            String name = nameVersionInfo[0];
            String version = nameVersionInfo[1];
            grayNameVersion.put(name, version);
        }

    }

    /**
     * 清空用户key
     */
    public void clear(String instanceId) {
        getServiceGrayConfig(instanceId).clear();
    }


    @PostConstruct
    protected void after() throws Exception {
        ZookeeperContext.setEnvironment(environment);
        String isvChannelPath = ZookeeperContext.getUserKeyChannelPath();
        ZookeeperContext.listenPath(isvChannelPath, nodeCache -> {
            String nodeData = new String(nodeCache.getCurrentData().getData());
            ChannelMsg channelMsg = JSON.parseObject(nodeData, ChannelMsg.class);
            String data = channelMsg.getData();
            UserKeyDefinition userKeyDefinition = JSON.parseObject(data, UserKeyDefinition.class);
            String instanceId = userKeyDefinition.getInstanceId();
            switch (channelMsg.getOperation()) {
                case "set":
                    ConfigGrayUserkey configGrayUserkey = configGrayUserkeyMapper.getByColumn("instance_id", instanceId);
                    this.setServiceGrayConfig(configGrayUserkey);
                    break;
                case "clear":
                    clear(instanceId);
                    break;
                default:
                    log.error("userKey消息，错误的消息指令，nodeData：{}", nodeData);

            }
        });
    }

}
