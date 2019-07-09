package com.gitee.sop.servercommon.manager;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.servercommon.bean.ServiceApiInfo;
import com.gitee.sop.servercommon.bean.ServiceConstants;
import com.gitee.sop.servercommon.bean.ZookeeperTool;
import com.gitee.sop.servercommon.route.GatewayRouteDefinition;
import com.gitee.sop.servercommon.route.ServiceRouteInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 上传路由到zookeeper
 *
 * @author tanghc
 */
@Slf4j
@Getter
@Setter
public class ServiceZookeeperApiMetaManager implements ApiMetaManager {

    /**
     * zookeeper存放接口路由信息的根目录
     */
    public static final String SOP_SERVICE_ROUTE_PATH = ServiceConstants.SOP_SERVICE_ROUTE_PATH;
    public static final String PATH_START_CHAR = "/";

    private final String routeRootPath = SOP_SERVICE_ROUTE_PATH;

    private Environment environment;

    private ZookeeperTool zookeeperTool;

    private String serviceId;

    public ServiceZookeeperApiMetaManager(Environment environment) {
        this.environment = environment;
        serviceId = environment.getProperty("spring.application.name");
        if (StringUtils.isEmpty(serviceId)) {
            throw new IllegalArgumentException("请在application.properties中指定spring.application.name属性");
        }
        this.zookeeperTool = new ZookeeperTool(environment);

        this.uploadServiceId(environment);
    }

    /**
     * 上传serviceId目录
     *
     * @param environment
     */
    protected void uploadServiceId(Environment environment) {
        try {
            ServiceRouteInfo serviceRouteInfo = this.buildServiceRouteInfo();
            // 保存路径
            String savePath = routeRootPath + "/" + serviceId;
            String nodeData = JSON.toJSONString(serviceRouteInfo);
            log.info("zookeeper创建serviceId路径，path:{}, nodeData:{}", savePath, nodeData);
            this.zookeeperTool.createPath(savePath, nodeData);
        } catch (Exception e) {
            throw new IllegalStateException("zookeeper操作失败");
        }
    }

    @Override
    public void uploadApi(ServiceApiInfo serviceApiInfo) {
        try {
            ServiceRouteInfo serviceRouteInfo = this.buildServiceGatewayInfo(serviceApiInfo);
            this.uploadServiceRouteInfoToZookeeper(serviceRouteInfo);
        } finally {
            IOUtils.closeQuietly(zookeeperTool);
        }
    }

    /**
     * 构建接口信息，符合spring cloud gateway的格式
     *
     * @param serviceApiInfo
     * @return 返回服务路由信息
     */
    protected ServiceRouteInfo buildServiceGatewayInfo(ServiceApiInfo serviceApiInfo) {
        List<ServiceApiInfo.ApiMeta> apis = serviceApiInfo.getApis();
        List<GatewayRouteDefinition> routeDefinitionList = new ArrayList<>(apis.size());
        for (ServiceApiInfo.ApiMeta apiMeta : apis) {
            GatewayRouteDefinition gatewayRouteDefinition = this.buildGatewayRouteDefinition(serviceApiInfo, apiMeta);
            routeDefinitionList.add(gatewayRouteDefinition);
        }
        ServiceRouteInfo serviceRouteInfo = this.buildServiceRouteInfo();
        serviceRouteInfo.setRouteDefinitionList(routeDefinitionList);
        return serviceRouteInfo;
    }

    protected ServiceRouteInfo buildServiceRouteInfo() {
        ServiceRouteInfo serviceRouteInfo = new ServiceRouteInfo();
        serviceRouteInfo.setServiceId(serviceId);
        String description = environment.getProperty("spring.application.description");
        serviceRouteInfo.setDescription(description);
        return serviceRouteInfo;
    }

    protected GatewayRouteDefinition buildGatewayRouteDefinition(ServiceApiInfo serviceApiInfo, ServiceApiInfo.ApiMeta apiMeta) {
        GatewayRouteDefinition gatewayRouteDefinition = new GatewayRouteDefinition();
        // 唯一id规则：接口名 + 版本号
        BeanUtils.copyProperties(apiMeta, gatewayRouteDefinition);
        gatewayRouteDefinition.setId(apiMeta.fetchNameVersion());
        gatewayRouteDefinition.setFilters(Collections.emptyList());
        String uri = this.buildUri(serviceApiInfo, apiMeta);
        String path = this.buildServletPath(serviceApiInfo, apiMeta);
        gatewayRouteDefinition.setUri(uri);
        gatewayRouteDefinition.setPath(path);
        return gatewayRouteDefinition;
    }

    protected String buildUri(ServiceApiInfo serviceApiInfo, ServiceApiInfo.ApiMeta apiMeta) {
        return "lb://" + serviceApiInfo.getServiceId();
    }

    protected String buildServletPath(ServiceApiInfo serviceApiInfo, ServiceApiInfo.ApiMeta apiMeta) {
        String servletPath = apiMeta.getPath();
        if (servletPath == null) {
            servletPath = "";
        }
        if (!servletPath.startsWith(PATH_START_CHAR)) {
            servletPath = PATH_START_CHAR + servletPath;
        }
        return servletPath;
    }

    /**
     * 上传接口信息到zookeeper
     *
     * @param serviceRouteInfo
     */
    protected void uploadServiceRouteInfoToZookeeper(ServiceRouteInfo serviceRouteInfo) {
        try {
            // 保存路径
            String savePath = routeRootPath + "/" + serviceRouteInfo.getServiceId();
            log.info("上传接口信息到zookeeper，path:{}, serviceId：{}, 接口数量：{}",
                    savePath,
                    serviceRouteInfo.getServiceId(),
                    serviceRouteInfo.getRouteDefinitionList().size());

            String parentPath = this.uploadFolder(serviceRouteInfo);
            this.uploadRouteItems(serviceRouteInfo, parentPath);
        } catch (Exception e) {
            log.error("更新接口信息到zookeeper失败, serviceId:{}", serviceRouteInfo.getServiceId(), e);
        }
    }

    /**
     * 上传文件夹内容
     *
     * @param serviceRouteInfo
     * @return 返回文件夹路径
     */
    protected String uploadFolder(ServiceRouteInfo serviceRouteInfo) throws Exception {
        // 保存路径
        String savePath = routeRootPath + "/" + serviceRouteInfo.getServiceId();
        String serviceRouteInfoJson = JSON.toJSONString(serviceRouteInfo);
        log.info("上传service目录到zookeeper，路径:{}，内容:{}", savePath, serviceRouteInfoJson);
        this.zookeeperTool.createOrUpdateData(savePath, serviceRouteInfoJson);
        return savePath;
    }

    /**
     * 上传路由信息
     *
     * @param serviceRouteInfo
     * @throws Exception
     */
    protected void uploadRouteItems(ServiceRouteInfo serviceRouteInfo, String parentPath) throws Exception {
        List<GatewayRouteDefinition> routeDefinitionList = serviceRouteInfo.getRouteDefinitionList();
        for (GatewayRouteDefinition routeDefinition : routeDefinitionList) {
            // 父目录/子目录
            String savePath = parentPath + PATH_START_CHAR + routeDefinition.getId();
            String routeDefinitionJson = JSON.toJSONString(routeDefinition);
            log.info("上传路由配置到zookeeper，路径:{}，路由数据:{}", savePath, routeDefinitionJson);
            this.zookeeperTool.createOrUpdateData(savePath, routeDefinitionJson);
        }
    }

}
