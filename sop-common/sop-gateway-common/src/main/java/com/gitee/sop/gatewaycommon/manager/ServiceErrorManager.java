package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.ErrorDefinition;
import com.gitee.sop.gatewaycommon.bean.ErrorEntity;

import java.util.Collection;

/**
 * @author tanghc
 */
public interface ServiceErrorManager {

    /**
     * 保存错误信息
     * @param errorDefinition
     */
    void saveError(ErrorDefinition errorDefinition);

    /**
     * 清除日志
     */
    void clear();

    /**
     * 获取所有错误信息
     * @return
     */
    Collection<ErrorEntity> listAllErrors();
}
