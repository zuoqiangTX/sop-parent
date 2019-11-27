package com.gitee.sop.gatewaycommon.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author tanghc
 */
@Getter
@Setter
@ToString
public class ErrorEntity {
    //    接口ID
    private String id;
    //    接口名称

    private String name;
    //    接口版本号

    private String version;
    //    接口服务ID

    private String serviceId;
    //    错误信息

    private String errorMsg;
    //    次数
    private long count;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ErrorEntity that = (ErrorEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}