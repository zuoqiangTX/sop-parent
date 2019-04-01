package com.gitee.sop.adminserver.api.isv.result;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class IsvFormVO {
    @ApiDocField(description = "appKey")
    private String appKey;

    @ApiDocField(description = "secret")
    private String secret;

    @ApiDocField(description = "pubKey")
    private String pubKey;

    @ApiDocField(description = "priKey")
    private String priKey;

}
