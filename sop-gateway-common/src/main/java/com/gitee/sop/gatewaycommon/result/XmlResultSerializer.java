package com.gitee.sop.gatewaycommon.result;

import com.gitee.sop.gatewaycommon.util.XmlUtil;

/**
 * 序列化成xml
 * @author tanghc
 */
public class XmlResultSerializer implements ResultSerializer {

    @Override
    public String serialize(Object obj) {
        return XmlUtil.serialize(obj);
    }

}
