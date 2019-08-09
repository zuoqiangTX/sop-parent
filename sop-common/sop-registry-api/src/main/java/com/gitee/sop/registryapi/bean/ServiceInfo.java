package com.gitee.sop.registryapi.bean;

import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class ServiceInfo {
    /** 服务名称 */
    private String serviceId;
    /** 实例列表 */
    private List<ServiceInstance> instances;
}
