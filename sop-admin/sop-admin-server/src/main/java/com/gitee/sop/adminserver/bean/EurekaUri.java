package com.gitee.sop.adminserver.bean;

/**
 * https://github.com/Netflix/eureka/wiki/Eureka-REST-operations
 * @author tanghc
 */
public enum EurekaUri {

    /** Query for all instances */
    QUERY_APPS("/apps"),
    ;
    String uri;

    EurekaUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }}
