package com.gitee.sop.gateway.manager;

import com.alibaba.fastjson.JSON;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.sop.gateway.entity.IsvInfo;
import com.gitee.sop.gateway.mapper.IsvInfoMapper;
import com.gitee.sop.gatewaycommon.bean.ChannelMsg;
import com.gitee.sop.gatewaycommon.bean.IsvDefinition;
import com.gitee.sop.gatewaycommon.manager.ZookeeperContext;
import com.gitee.sop.gatewaycommon.secret.CacheIsvManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;

/**
 * @author tanghc
 */
@Component
@Slf4j
public class DbIsvManager extends CacheIsvManager {

    @Autowired
    IsvInfoMapper isvInfoMapper;

    @Autowired
    Environment environment;

    @Override
    public void load(Function<Object, String> secretGetter) {
        List<IsvInfo> isvInfoList = isvInfoMapper.list(new Query());
        isvInfoList.stream()
                .forEach(isvInfo -> {
                    IsvDefinition isvDefinition = new IsvDefinition();
                    BeanUtils.copyProperties(isvInfo, isvDefinition);
                    String secret = secretGetter.apply(isvInfo);
                    isvDefinition.setSecret(secret);
                    this.getIsvCache().put(isvDefinition.getAppKey(), isvDefinition);
                });
    }

    @PostConstruct
    protected void after() throws Exception {
        ZookeeperContext.setEnvironment(environment);
        String isvChannelPath = ZookeeperContext.getIsvInfoChannelPath();
        ZookeeperContext.listenPath(isvChannelPath, nodeCache -> {
            String nodeData = new String(nodeCache.getCurrentData().getData());
            ChannelMsg channelMsg = JSON.parseObject(nodeData, ChannelMsg.class);
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

            }
        });
    }

}
