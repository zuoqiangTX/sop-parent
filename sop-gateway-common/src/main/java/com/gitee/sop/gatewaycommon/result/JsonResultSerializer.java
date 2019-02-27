package com.gitee.sop.gatewaycommon.result;

import com.alibaba.fastjson.JSON;

/**
 * 序列化json
 * @author tanghc
 */
public class JsonResultSerializer implements ResultSerializer {

    @Override
    public String serialize(Object obj) {
        return JSON.toJSONString(obj);
    }

}
