package com.gitee.sop.servercommon.route;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class GatewayRouteDefinition {
    /**
     * 路由的Id
     */
    private String id;

    /**
     * 路由断言集合配置
     */
    private List<GatewayPredicateDefinition> predicates = new ArrayList<>();

    /**
     * 路由过滤器集合配置
     */
    private List<GatewayFilterDefinition> filters = new ArrayList<>();

    /**
     * 路由规则转发的目标uri
     */
    private String uri;

    /**
     * uri后面跟的path
     */
    private String path;

    /**
     * 路由执行的顺序
     */
    private int order = 0;

    /**
     * 是否忽略验证，业务参数验证除外
     */
    private int ignoreValidate;

    /**
     * 状态，0：待审核，1：启用，2：禁用
     */
    private int status = 1;

    /**
     * 是否合并结果
     */
    private int mergeResult;
}