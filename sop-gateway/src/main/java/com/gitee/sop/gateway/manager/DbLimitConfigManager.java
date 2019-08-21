package com.gitee.sop.gateway.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.sop.gateway.mapper.ConfigLimitMapper;
import com.gitee.sop.gatewaycommon.bean.ChannelMsg;
import com.gitee.sop.gatewaycommon.bean.ConfigLimitDto;
import com.gitee.sop.gatewaycommon.bean.NacosConfigs;
import com.gitee.sop.gatewaycommon.manager.DefaultLimitConfigManager;
import com.gitee.sop.gatewaycommon.util.MyBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alibaba.nacos.NacosConfigProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 限流配置管理
 * @author tanghc
 */
@Slf4j
@Service
public class DbLimitConfigManager extends DefaultLimitConfigManager {

    @Autowired
    ConfigLimitMapper configLimitMapper;

    @Autowired
    Environment environment;

    @Autowired
    private NacosConfigProperties nacosConfigProperties;

    @Override
    public void load() {
        Query query = new Query();
        configLimitMapper.list(query)
                .stream()
                .forEach(configLimit -> putVal(configLimit));

    }

    protected void putVal(Object object) {
        ConfigLimitDto configLimitDto = new ConfigLimitDto();
        MyBeanUtil.copyPropertiesIgnoreNull(object, configLimitDto);
        this.update(configLimitDto);
    }


    @PostConstruct
    protected void after() throws Exception {
        ConfigService configService = nacosConfigProperties.configServiceInstance();
        configService.addListener(NacosConfigs.DATA_ID_LIMIT_CONFIG, NacosConfigs.GROUP_CHANNEL, new AbstractListener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                ChannelMsg channelMsg = JSON.parseObject(configInfo, ChannelMsg.class);
                final ConfigLimitDto configLimitDto = JSON.parseObject(channelMsg.getData(), ConfigLimitDto.class);
                switch (channelMsg.getOperation()) {
                    case "reload":
                        log.info("重新加载限流配置信息，configLimitDto:{}", configLimitDto);
                        load();
                        break;
                    case "update":
                        log.info("更新限流配置信息，configLimitDto:{}", configLimitDto);
                        update(configLimitDto);
                        break;
                    default:
                }
            }
        });
    }

}
