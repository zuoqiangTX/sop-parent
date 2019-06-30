package com.gitee.sop.adminserver.bean;

import lombok.Data;

@Data
public class ServiceInstance {
    /**
     * 实例id
     */
    private String instanceId;

    /**
     * 服务名称
     */
    private String serviceId;

    /**
     * IP 端口
     */
    private String ipPort;

    /**
     * 状态，1：上线，2：下线
     */
    private String status;
}