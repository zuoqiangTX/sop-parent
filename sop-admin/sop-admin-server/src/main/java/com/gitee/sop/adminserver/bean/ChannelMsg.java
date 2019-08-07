package com.gitee.sop.adminserver.bean;

import com.gitee.sop.adminserver.common.ChannelOperation;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ChannelMsg {

    public ChannelMsg(ChannelOperation channelOperation, Object data) {
        this.operation = channelOperation.getOperation();
        this.data = data;
    }

    private String operation;
    private Object data;
}
