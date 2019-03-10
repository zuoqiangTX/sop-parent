package com.gitee.sop.servercommon.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.sop.servercommon.route.GatewayRouteDefinition;
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
    private List<GatewayRouteDefinition> routeDefinitionList;

    @Data
    public static class ApiMeta {
        /** 接口名 */
        private String name;
        /** 请求path */
        private String path;
        /** 版本号 */
        private String version;
        /** 是否忽略验证 */
        private boolean ignoreValidate;

        public ApiMeta() {
        }

        public ApiMeta(String name, String path, String version) {
            this.name = name;
            this.path = path;
            this.version = version;
        }

        public String fetchNameVersion() {
            return this.name + this.version;
        }
    }
}
