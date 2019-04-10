package com.gitee.sop.adminserver.api.service.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Getter
@Setter
public class RouteParam {

    @NotBlank(message = "serviceId不能为空")
    @ApiDocField(description = "serviceId")
    private String serviceId;

    /**
     * 路由的Id
     */
    @NotBlank(message = "id不能为空")
    @ApiDocField(description = "路由id")
    private String id = "";

    /**
     * 路由规则转发的目标uri
     */
    @NotBlank(message = "uri不能为空")
    @ApiDocField(description = "路由uri")
    private String uri;

    /**
     * uri后面跟的path
     */
    @ApiDocField(description = "路由path")
    private String path;

    /**
     * 状态
     */
    @NotNull
    @ApiDocField(description = "状态，0：审核，1：启用，2：禁用")
    private Integer status;

}
