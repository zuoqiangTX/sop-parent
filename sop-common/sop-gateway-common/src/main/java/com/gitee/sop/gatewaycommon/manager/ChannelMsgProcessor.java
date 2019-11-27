package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.ChannelMsg;

/**
 * 操作信息处理类
 *
 * @author tanghc
 */
public interface ChannelMsgProcessor {
    default void process(ChannelMsg channelMsg) {
    }
}
