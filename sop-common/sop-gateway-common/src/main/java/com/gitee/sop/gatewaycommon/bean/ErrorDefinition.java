package com.gitee.sop.gatewaycommon.bean;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ErrorDefinition {
    private String name;
    private String version;
    private String serviceId;
    private String errorMsg;

}
