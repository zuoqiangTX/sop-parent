package com.gitee.sop.gatewaycommon.bean;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ChannelMsg {
    //    操作
    private String operation;
    //    数据
    private JSONObject data;

    public <T> T toObject(Class<T> clazz) {
        return data.toJavaObject(clazz);
    }
}
