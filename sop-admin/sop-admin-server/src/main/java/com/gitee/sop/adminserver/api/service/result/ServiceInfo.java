package com.gitee.sop.adminserver.api.service.result;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ServiceInfo {
    @ApiDocField(description = "serviceId")
    private String serviceId;

    @ApiDocField(description = "创建时间")
    private String createTime;

    @ApiDocField(description = "修改时间")
    private String updateTime;

    @ApiDocField(description = "描述")
    private String description;
}
