package com.gitee.sop.gateway.manager;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.gateway.entity.ConfigGrayUserkey;
import com.gitee.sop.gateway.loadbalancer.ServiceGrayConfig;
import com.gitee.sop.gateway.mapper.ConfigGrayUserkeyMapper;
import com.gitee.sop.gatewaycommon.bean.ChannelMsg;
import com.gitee.sop.gatewaycommon.bean.UserKeyDefinition;
import com.gitee.sop.gatewaycommon.manager.ZookeeperContext;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
@Service
@Slf4j
public class UserKeyManager {

    /**
     * KEY:instanceId
     */
    private Map<String, ServiceGrayConfig> serviceUserKeyMap = Maps.newConcurrentMap();

    @Autowired
    private Environment environment;

    @Autowired
    private ConfigGrayUserkeyMapper configGrayUserkeyMapper;

    public boolean containsKey(String serviceId, Object userKey) {
        if (serviceId == null || userKey == null) {
            return false;
        }
        return this.getServiceGrayConfig(serviceId).containsKey(userKey);
    }

    public String getVersion(String serviceId, String nameVersion) {
        if (serviceId == null || nameVersion == null) {
            return null;
        }
        return this.getServiceGrayConfig(serviceId).getVersion(nameVersion);
    }

    /**
     * 设置用户key
     *
     * @param configGrayUserkey 灰度配置
     */
    public void setServiceGrayConfig(String serviceId, ConfigGrayUserkey configGrayUserkey) {
        if (configGrayUserkey == null) {
            return;
        }
        this.clear(serviceId);
        String userKeyData = configGrayUserkey.getUserKeyContent();
        String nameVersionContent = configGrayUserkey.getNameVersionContent();
        String[] userKeys = StringUtils.split(userKeyData, ',');
        String[] nameVersionList = StringUtils.split(nameVersionContent, ',');
        log.info("添加userKey，userKeys.length:{}, nameVersionList:{}", userKeys.length, Arrays.toString(nameVersionList));

        List<String> list = Stream.of(userKeys).collect(Collectors.toList());
        ServiceGrayConfig serviceGrayConfig = getServiceGrayConfig(serviceId);
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
    public void clear(String serviceId) {
        getServiceGrayConfig(serviceId).clear();
    }

    public ServiceGrayConfig getServiceGrayConfig(String serviceId) {
        ServiceGrayConfig serviceGrayConfig = serviceUserKeyMap.get(serviceId);
        if (serviceGrayConfig == null) {
            serviceGrayConfig = new ServiceGrayConfig();
            serviceGrayConfig.setUserKeys(Sets.newConcurrentHashSet());
            serviceGrayConfig.setGrayNameVersion(Maps.newConcurrentMap());
            serviceUserKeyMap.put(serviceId, serviceGrayConfig);
        }
        return serviceGrayConfig;
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
            String serviceId = userKeyDefinition.getServiceId();
            switch (channelMsg.getOperation()) {
                case "set":
                    ConfigGrayUserkey configGrayUserkey = configGrayUserkeyMapper.getByColumn("service_id", serviceId);
                    this.setServiceGrayConfig(serviceId, configGrayUserkey);
                    break;
                case "clear":
                    clear(serviceId);
                    break;
                default:
                    log.error("userKey消息，错误的消息指令，nodeData：{}", nodeData);

            }
        });
    }

}
