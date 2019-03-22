package com.gitee.sop.adminserver.api.service.result;

import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * @author tanghc
 */
@Data
public class ServiceInfoVo {
    @ApiDocField(description = "id")
    private Integer id;

    @ApiDocField(description = "服务名称")
    private String name;

    @ApiDocField(description = "instanceId")
    private String instanceId;

    @ApiDocField(description = "ip")
    private String ipAddr;

    @ApiDocField(description = "端口")
    private String serverPort;

    @ApiDocField(description = "status")
    private String status;

    @ApiDocField(description = "statusPageUrl")
    private String statusPageUrl;

    @ApiDocField(description = "healthCheckUrl")
    private String healthCheckUrl;

    @ApiDocField(description = "最后更新时间")
    private String lastUpdatedTimestamp;

    @ApiDocField(description = "parentId")
    private Integer parentId;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date getUpdateTime() {
        if (StringUtils.isBlank(lastUpdatedTimestamp)) {
            return null;
        }
        return new Date(Long.valueOf(lastUpdatedTimestamp));
    }
}
