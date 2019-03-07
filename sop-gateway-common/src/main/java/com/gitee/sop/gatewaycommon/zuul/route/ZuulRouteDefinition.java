package com.gitee.sop.gatewaycommon.zuul.route;

import com.gitee.sop.gatewaycommon.bean.BaseRouteDefinition;
import lombok.Getter;
import lombok.Setter;

/**
 * @author tanghc
 */
@Getter
@Setter
public class ZuulRouteDefinition extends BaseRouteDefinition {
    private String path;
}
