package com.gitee.sop.gateway.manager;

import com.gitee.sop.gatewaycommon.bean.ChannelMsg;

/**
 * @author tanghc
 */
public interface ChannelMsgProcessor {
    void process(ChannelMsg channelMsg);
}
