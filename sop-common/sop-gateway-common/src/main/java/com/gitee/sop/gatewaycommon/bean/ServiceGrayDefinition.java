package com.gitee.sop.gatewaycommon.bean;

import lombok.Data;

/**
 * 服务灰度定义
 *
 * @author tanghc
 */
@Data
public class ServiceGrayDefinition {
    private String serviceId;
    private String instanceId;
    private String data;
}
