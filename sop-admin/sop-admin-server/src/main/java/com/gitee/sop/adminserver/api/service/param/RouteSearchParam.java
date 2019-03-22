package com.gitee.sop.adminserver.api.service.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author tanghc
 */
@Getter
@Setter
public class RouteSearchParam extends ServiceSearchParam {
    @ApiDocField(description = "id")
    private String id;
}
