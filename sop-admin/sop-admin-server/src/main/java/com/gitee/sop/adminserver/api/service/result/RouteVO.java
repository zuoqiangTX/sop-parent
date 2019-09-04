package com.gitee.sop.adminserver.api.service.result;

import com.gitee.sop.adminserver.api.isv.result.RoleVO;
import com.gitee.sop.adminserver.bean.RouteDefinition;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author tanghc
 */
@Getter
@Setter
public class RouteVO extends RouteDefinition {
    private List<RoleVO> roles;
}
