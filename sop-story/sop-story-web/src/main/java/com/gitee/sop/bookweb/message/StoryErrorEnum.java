package com.gitee.sop.bookweb.message;

import com.gitee.sop.servercommon.message.ServiceErrorMeta;

/**
 * @author tanghc
 */
public enum  StoryErrorEnum {
    /** 参数错误 */
    param_error("isv.invalid-parameter"),
    ;
    private ServiceErrorMeta errorMeta;

    StoryErrorEnum(String sub_code) {
        this.errorMeta = new ServiceErrorMeta("isp.error_", sub_code);
    }

    public ServiceErrorMeta getErrorMeta() {
        return errorMeta;
    }
}
