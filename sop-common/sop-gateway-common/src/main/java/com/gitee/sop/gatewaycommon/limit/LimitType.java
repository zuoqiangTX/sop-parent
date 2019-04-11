package com.gitee.sop.gatewaycommon.limit;

/**
 * 限流策略
 * 
 * @author tanghc
 */
public enum LimitType {
    /**
     * 漏桶策略。每秒处理固定数量的请求，超出请求返回错误信息。
     */
    LEAKY_BUCKET(1),
    /**
     * 令牌桶策略，每秒放置固定数量的令牌数，不足的令牌数做等待处理，直到拿到令牌为止。
     */
    TOKEN_BUCKET(2);

    private byte type;

    LimitType(int type) {
        this.type = (byte)type;
    }

    public byte getType() {
        return type;
    }

}
