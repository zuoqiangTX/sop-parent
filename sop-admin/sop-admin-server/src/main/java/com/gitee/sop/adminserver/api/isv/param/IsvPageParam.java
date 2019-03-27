package com.gitee.sop.adminserver.api.isv.param;

import com.gitee.fastmybatis.core.query.param.PageParam;
import lombok.Getter;
import lombok.Setter;

/**
 * @author tanghc
 */
@Getter
@Setter
public class IsvPageParam extends PageParam {
    private String appKey;
}
