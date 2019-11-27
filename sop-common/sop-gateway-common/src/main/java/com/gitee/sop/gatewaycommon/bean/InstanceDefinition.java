package com.gitee.sop.gatewaycommon.bean;

import lombok.Data;

import java.util.Map;

/**
 * @author tanghc
 */
@Data
public class InstanceDefinition {
    private String instanceId;
    //    接口服务ID
    private String serviceId;
    //    ip
    private String ip;
    //    端口号
    private int port;
    //    元数据
    private Map<String, String> metadata;
}
