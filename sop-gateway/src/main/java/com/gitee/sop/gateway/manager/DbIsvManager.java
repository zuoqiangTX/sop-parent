package com.gitee.sop.gateway.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.gitee.sop.gateway.entity.IsvDetailDTO;
import com.gitee.sop.gateway.mapper.IsvInfoMapper;
import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import com.gitee.sop.gatewaycommon.bean.ChannelMsg;
import com.gitee.sop.gatewaycommon.bean.IsvDefinition;
import com.gitee.sop.gatewaycommon.bean.NacosConfigs;
import com.gitee.sop.gatewaycommon.secret.CacheIsvManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author tanghc
 */
@Slf4j
@Service
public class DbIsvManager extends CacheIsvManager {

    @Autowired
    private IsvInfoMapper isvInfoMapper;

    @NacosInjected
    private ConfigService configService;

    @Override
    public void load() {
        List<IsvDetailDTO> isvInfoList = isvInfoMapper.listIsvDetail();
        isvInfoList.stream()
                .forEach(isvInfo -> {
                    IsvDefinition isvDefinition = new IsvDefinition();
                    BeanUtils.copyProperties(isvInfo, isvDefinition);
                    this.getIsvCache().put(isvDefinition.getAppKey(), isvDefinition);
                });
    }

    @PostConstruct
    protected void after() throws Exception {
        ApiConfig.getInstance().setIsvManager(this);

        configService.addListener(NacosConfigs.DATA_ID_ISV, NacosConfigs.GROUP_CHANNEL, new AbstractListener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                ChannelMsg channelMsg = JSON.parseObject(configInfo, ChannelMsg.class);
                final IsvDefinition isvDefinition = JSON.parseObject(channelMsg.getData(), IsvDefinition.class);
                switch (channelMsg.getOperation()) {
                    case "update":
                        log.info("更新ISV信息，isvDefinition:{}", isvDefinition);
                        update(isvDefinition);
                        break;
                    case "remove":
                        log.info("删除ISV，isvDefinition:{}", isvDefinition);
                        remove(isvDefinition.getAppKey());
                        break;
                    default:

                }
            }
        });
    }

}
