package com.gitee.sop.adminserver.api.service.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import com.gitee.fastmybatis.core.query.Operator;
import com.gitee.fastmybatis.core.query.annotation.Condition;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author tanghc
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LimitNewParam extends ServiceSearchParam {
    @ApiDocField(description = "路由id")
    @Condition(operator = Operator.like)
    private String routeId;

    /**  数据库字段：app_key */
    @ApiDocField(description = "appKey")
    @Condition(operator = Operator.like)
    private String appKey;

    /** 限流ip，多个用英文逗号隔开, 数据库字段：limit_ip */
    @ApiDocField(description = "限流ip，多个用英文逗号隔开")
    @Condition(operator = Operator.like)
    private String limitIp;
}
