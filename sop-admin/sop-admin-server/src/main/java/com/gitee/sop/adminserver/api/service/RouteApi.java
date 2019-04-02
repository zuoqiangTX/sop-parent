package com.gitee.sop.adminserver.api.service;

import com.alibaba.fastjson.JSON;
import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;
import com.gitee.easyopen.exception.ApiException;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.sop.adminserver.api.isv.result.RoleVO;
import com.gitee.sop.adminserver.api.service.param.RouteParam;
import com.gitee.sop.adminserver.api.service.param.RoutePermissionParam;
import com.gitee.sop.adminserver.api.service.param.RouteSearchParam;
import com.gitee.sop.adminserver.api.service.result.RouteVO;
import com.gitee.sop.adminserver.api.service.result.ServiceInfo;
import com.gitee.sop.adminserver.bean.GatewayRouteDefinition;
import com.gitee.sop.adminserver.bean.ZookeeperContext;
import com.gitee.sop.adminserver.entity.PermRole;
import com.gitee.sop.adminserver.entity.PermRolePermission;
import com.gitee.sop.adminserver.mapper.PermRoleMapper;
import com.gitee.sop.adminserver.mapper.PermRolePermissionMapper;
import com.gitee.sop.adminserver.service.RoutePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@ApiService
@ApiDoc("服务管理")
@Slf4j
public class RouteApi {

    @Autowired
    PermRolePermissionMapper permRolePermissionMapper;

    @Autowired
    PermRoleMapper permRoleMapper;

    @Autowired
    RoutePermissionService routePermissionService;

    @Api(name = "route.list")
    @ApiDocMethod(description = "路由列表")
    List<RouteVO> listRoute(RouteSearchParam param) throws Exception {
        if (StringUtils.isBlank(param.getServiceId())) {
            return Collections.emptyList();
        }

        String searchPath = ZookeeperContext.getSopRouteRootPath() + "/" + param.getServiceId();

        List<ChildData> childDataList = ZookeeperContext.getChildrenData(searchPath);

        List<RouteVO> routeDefinitionList = childDataList.stream()
                .map(childData -> {
                    String serviceNodeData = new String(childData.getData());
                    GatewayRouteDefinition routeDefinition = JSON.parseObject(serviceNodeData, GatewayRouteDefinition.class);
                    return routeDefinition;
                })
                .filter(gatewayRouteDefinition -> {
                    boolean isRoute = gatewayRouteDefinition.getOrder() != Integer.MIN_VALUE;
                    String id = param.getId();
                    if (StringUtils.isBlank(id)) {
                        return isRoute;
                    } else {
                        return isRoute && gatewayRouteDefinition.getId().contains(id);
                    }
                })
                .map(gatewayRouteDefinition->{
                    RouteVO vo = new RouteVO();
                    BeanUtils.copyProperties(gatewayRouteDefinition, vo);
                    vo.setRoles(this.getRouteRole(gatewayRouteDefinition.getId()));
                    return vo;
                })
                .collect(Collectors.toList());

        return routeDefinitionList;
    }

    @Api(name = "route.update")
    @ApiDocMethod(description = "修改路由")
    void updateRoute(RouteParam param) throws Exception {
        String serviceIdPath = ZookeeperContext.getSopRouteRootPath() + "/" + param.getServiceId();
        String zookeeperRoutePath = serviceIdPath + "/" + param.getId();
        String data = ZookeeperContext.getData(zookeeperRoutePath);
        GatewayRouteDefinition routeDefinition = JSON.parseObject(data, GatewayRouteDefinition.class);
        BeanUtils.copyProperties(param, routeDefinition);
        ZookeeperContext.updatePathData(zookeeperRoutePath, JSON.toJSONString(routeDefinition));
    }

    @Api(name = "route.add")
    @ApiDocMethod(description = "新增路由")
    void addRoute(RouteParam param) throws Exception {
        String serviceIdPath = ZookeeperContext.getSopRouteRootPath() + "/" + param.getServiceId();
        String zookeeperRoutePath = serviceIdPath + "/" + param.getId();
        if (ZookeeperContext.isPathExist(zookeeperRoutePath)) {
            throw new ApiException("id已存在");
        }
        GatewayRouteDefinition routeDefinition = new GatewayRouteDefinition();
        BeanUtils.copyProperties(param, routeDefinition);
        ZookeeperContext.createNewData(zookeeperRoutePath, JSON.toJSONString(routeDefinition));
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setServiceId(param.getServiceId());
        serviceInfo.setDescription(param.getServiceId());
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        serviceInfo.setCreateTime(now);
        serviceInfo.setUpdateTime(now);
        ZookeeperContext.updatePathData(serviceIdPath, JSON.toJSONString(serviceInfo));
    }

    @Api(name = "route.role.get")
    @ApiDocMethod(description = "获取路由对应的角色", elementClass = RoleVO.class)
    List<RoleVO> getRouteRole(RouteSearchParam param) {
        if (StringUtils.isBlank(param.getId())) {
            throw new ApiException("id不能为空");
        }
        return this.getRouteRole(param.getId());
    }

    private List<RoleVO> getRouteRole(String id) {
        return permRolePermissionMapper.listByColumn("route_id", id)
                .stream()
                .map(permRolePermission -> {
                    RoleVO vo = new RoleVO();
                    String roleCode = permRolePermission.getRoleCode();
                    PermRole permRole = permRoleMapper.getByColumn("role_code", roleCode);
                    BeanUtils.copyProperties(permRole, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Api(name = "route.role.update")
    @ApiDocMethod(description = "更新路由对应的角色")
    @Transactional(rollbackFor = Exception.class)
    public void updateRouteRole(RoutePermissionParam param) {
        String routeId = param.getRouteId();
        // 删除所有数据
        Query delQuery = new Query();
        delQuery.eq("route_id", routeId);
        permRolePermissionMapper.deleteByQuery(delQuery);

        List<String> roleCodes = param.getRoleCode();
        if (CollectionUtils.isNotEmpty(roleCodes)) {
            List<PermRolePermission> tobeSave = new ArrayList<>(roleCodes.size());
            for (String roleCode : roleCodes) {
                PermRolePermission permRolePermission = new PermRolePermission();
                permRolePermission.setRoleCode(roleCode);
                permRolePermission.setRouteId(routeId);
                tobeSave.add(permRolePermission);
            }
            // 批量添加
            permRolePermissionMapper.saveBatch(tobeSave);
        }

        try {
            routePermissionService.sendRoutePermissionReloadToZookeeper();
        } catch (Exception e) {
            log.info("消息推送--路由权限(reload)失败",e);
        }
    }

}
