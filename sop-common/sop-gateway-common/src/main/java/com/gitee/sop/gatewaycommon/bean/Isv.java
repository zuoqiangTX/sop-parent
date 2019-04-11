package com.gitee.sop.gatewaycommon.bean;

/**
 * @author tanghc
 */
public interface Isv {

    /**
     * appKey
     * @return 返回appKey
     */
    String getAppKey();

    /**
     * 秘钥
     * @return 返回秘钥
     */
    String getSecretInfo();

    /**
     * 0启用，1禁用
     * @return 返回状态
     */
    Byte getStatus();
}
