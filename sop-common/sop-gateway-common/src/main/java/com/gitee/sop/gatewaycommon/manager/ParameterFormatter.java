package com.gitee.sop.gatewaycommon.manager;

import com.alibaba.fastjson.JSONObject;

/**
 * 参数格式化
 *
 * @author tanghc
 */
public interface ParameterFormatter<T> {

    /**
     * 参数格式化，即动态修改请求参数
     *
     * @param requestParams 原始请求参数，在此基础上追加或修改参数
     * @param requestContext requestContext
     */
    void format(JSONObject requestParams, T requestContext);
}
