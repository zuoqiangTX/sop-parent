package com.gitee.sop.adminserver.api.service.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author tanghc
 */
@Data
public class RouteParam {

    @NotBlank(message = "profile不能为空")
    @ApiDocField(description = "profile")
    private String profile;

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
     * 是否忽略验证，业务参数验证除外
     */
    @NotNull
    @ApiDocField(description = "是否忽略验证,1：是，0：否")
    private Integer ignoreValidate;

    /**
     * 状态
     */
    @NotNull
    @ApiDocField(description = "状态，0：审核，1：启用，2：禁用")
    private Integer status;

    /**
     * 状态
     */
    @NotNull
    @ApiDocField(description = "是否合并结果,1：是，0：否")
    private Integer mergeResult;
}
