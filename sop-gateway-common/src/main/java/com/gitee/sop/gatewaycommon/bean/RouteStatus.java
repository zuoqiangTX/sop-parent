package com.gitee.sop.gatewaycommon.bean;

/**
 * @author tanghc
 */
public enum RouteStatus {
    AUDIT(0, "待审核"),
    ENABLE(1, "已启用"),
    DISABLE(2, "已禁用"),
    ;
    private int status;
    private String description;

    RouteStatus(int status, String description) {
        this.status = status;
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }}
