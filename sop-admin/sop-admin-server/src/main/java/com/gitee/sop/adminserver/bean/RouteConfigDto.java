package com.gitee.sop.adminserver.bean;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class RouteConfigDto {

    private String routeId;

    /** 限流策略，1：漏桶策略，2：令牌桶策略, 数据库字段：limit_type */
    private Byte limitType;

    /** 每秒可处理请求数, 数据库字段：exec_count_per_second */
    private Integer execCountPerSecond;

    /** 返回的错误码, 数据库字段：limit_code */
    private String limitCode;

    /** 返回的错误信息, 数据库字段：limit_msg */
    private String limitMsg;

    /** 令牌桶容量, 数据库字段：token_bucket_count */
    private Integer tokenBucketCount;

    /** 限流开启状态，1:开启，0关闭, 数据库字段：limit_status */
    private Byte limitStatus;

    /**
     * 状态，0：待审核，1：启用，2：禁用
     */
    private Integer status;


}
