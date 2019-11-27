package com.gitee.sop.gatewaycommon.bean;

import lombok.Data;

/**
 * 错误定义类
 *
 * @author tanghc
 */
@Data
public class ErrorDefinition {
    /**
     * 接口名
     */
    private String name;
    /**
     * 版本号
     */
    private String version;
    /**
     * 服务名
     */
    private String serviceId;
    /**
     * 错误内容
     */
    private String errorMsg;

}
