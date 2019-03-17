package com.gitee.sop.adminserver.api.service.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ServiceSearchParam {

    @ApiDocField(description = "profile")
    private String profile;

    @ApiDocField(description = "serviceId")
    private String serviceId;
}
