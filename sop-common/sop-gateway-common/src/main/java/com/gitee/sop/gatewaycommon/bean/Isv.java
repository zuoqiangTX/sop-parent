package com.gitee.sop.gatewaycommon.bean;

/**
 * @author tanghc
 */
public interface Isv {

    /**
     * appKey
     * @return
     */
    String getAppKey();

    /**
     * 秘钥
     * @return
     */
    String getSecretInfo();

    /**
     * 0启用，1禁用
     * @return
     */
    Byte getStatus();
}
