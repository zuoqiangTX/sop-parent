package com.gitee.sop.adminserver.api.service;

import com.alibaba.fastjson.JSON;
import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;
import com.gitee.sop.adminserver.api.service.param.ServiceSearchParam;
import com.gitee.sop.adminserver.api.service.result.ServiceInfo;
import com.gitee.sop.adminserver.bean.ZookeeperContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.stream.Collectors;

import static com.gitee.sop.adminserver.bean.SopAdminConstants.SOP_SERVICE_ROUTE_PATH;

/**
 * @author tanghc
 */
@ApiService
@ApiDoc("服务管理")
@Slf4j
public class ServiceApi {


    @Autowired
    private Environment environment;

    @Api(name = "service.list")
    @ApiDocMethod(description = "服务列表", elementClass = ServiceInfo.class)
    List<ServiceInfo> listServiceInfo(ServiceSearchParam param) throws Exception {
        String routeRootPath = ZookeeperContext.getSopRouteRootPath(param.getProfile());
        List<ChildData> childDataList = ZookeeperContext.getChildrenData(routeRootPath);
        List<ServiceInfo> serviceInfoList = childDataList.stream()
                .map(childData -> {
                    String serviceNodeData = new String(childData.getData());
                    ServiceInfo serviceInfo = JSON.parseObject(serviceNodeData, ServiceInfo.class);
                    return serviceInfo;
                })
                .filter(serviceInfo -> {
                    if (StringUtils.isBlank(param.getServiceId())) {
                        return true;
                    } else {
                        return serviceInfo.getServiceId().contains(param.getServiceId());
                    }
                })
                .collect(Collectors.toList());

        return serviceInfoList;
    }

}
