package com.gitee.sop.gatewaycommon.bean;

import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class ServiceApiInfo {
    private String md5;
    private String appName;
    private List<ApiMeta> apis;

    @Data
    public static class ApiMeta {
        /** 接口名 */
        private String name;
        /** 请求path */
        private String path;
        /** 版本号 */
        private String version;

        public String fetchNameVersion() {
            return name + version;
        }
    }
}
