package com.gitee.sop.servercommon.mapping;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ApiMappingInfo {
    private String name;
    private String version;
    private boolean ignoreValidate;

    public ApiMappingInfo(String name, String version) {
        this.name = name;
        this.version = version;
    }
}
