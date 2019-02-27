package com.gitee.sop.gatewaycommon.message;

import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.exception.ApiException;
import lombok.Getter;

/**
 * 错误对象
 *
 * @author tanghc
 */
@Getter
public class ErrorMeta {

    private String modulePrefix;
    private String code;
    private String subCode;

    public ErrorMeta(String modulePrefix, String code, String subCode) {
        this.modulePrefix = modulePrefix;
        this.code = code;
        this.subCode = subCode;
    }

    public Error getError() {
        return ErrorFactory.getError(this, ApiContext.getLocale());
    }

    /**
     * 返回网关exception
     *
     * @param params 参数
     * @return 返回exception
     */
    public ApiException getException(Object... params) {
        if (params != null && params.length == 1) {
            Object param = params[0];
            if (param instanceof Throwable) {
                Error error = ErrorFactory.getError(this, ApiContext.getLocale());
                return new ApiException((Throwable) param, error);
            }
        }
        Error error = ErrorFactory.getError(this, ApiContext.getLocale(), params);
        return new ApiException(error);
    }

}
