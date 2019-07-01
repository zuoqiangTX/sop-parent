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
public class ServiceInstanceVO {
    @ApiDocField(description = "id")
    private Integer id;

    @ApiDocField(description = "服务名称(serviceId)")
    private String serviceId;

    @ApiDocField(description = "instanceId")
    private String instanceId;

    @ApiDocField(description = "ipPort")
    private String ipPort;

    @ApiDocField(description = "ip")
    private String ip;

    @ApiDocField(description = "port")
    private int port;

    @ApiDocField(description = "status，服务状态，UP：已上线，OUT_OF_SERVICE：已下线")
    private String status;

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

    public String getIpPort() {
        return ip != null && port > 0 ? ip + ":" + port : "";
    }

}
