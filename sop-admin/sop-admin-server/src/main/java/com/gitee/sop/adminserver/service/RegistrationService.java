package com.gitee.sop.adminserver.service;

import com.gitee.sop.adminserver.bean.ServiceInfo;
import com.gitee.sop.adminserver.bean.ServiceInstance;

import java.util.List;

/**
 * @author tanghc
 */
public interface RegistrationService {
    /**
     * 获取所有服务列表
     *
     * @param pageNo   当前页码
     * @param pageSize 分页大小
     * @return 返回服务列表
     */
    List<ServiceInfo> listAllService(int pageNo, int pageSize) throws Exception;

    /**
     * 服务上线
     *
     * @param serviceInstance
     */
    void onlineInstance(ServiceInstance serviceInstance) throws Exception;

    /**
     * 服务下线
     *
     * @param serviceInstance
     */
    void offlineInstance(ServiceInstance serviceInstance) throws Exception;


}
