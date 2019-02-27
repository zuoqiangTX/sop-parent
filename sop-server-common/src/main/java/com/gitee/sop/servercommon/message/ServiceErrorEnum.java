package com.gitee.sop.servercommon.message;

/**
 * @author tanghc
 */
public enum ServiceErrorEnum {
    /** 系统繁忙 */
    ISP_UNKNOW_ERROR("isp.unknow-error"),
    ISP_PARAM_ERROR("isv.invalid-parameter"),
    ;
    private ServiceErrorMeta errorMeta;

    ServiceErrorEnum(String sub_code) {
        this.errorMeta = new ServiceErrorMeta("isp.error_", sub_code);
    }

    public ServiceErrorMeta getErrorMeta() {
        return errorMeta;
    }
}
