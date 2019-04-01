package com.gitee.sop.gateway.manager;

import com.gitee.sop.gateway.entity.IsvInfo;
import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author tanghc
 */
@Component
@Slf4j
public class ManagerInitializer {
    @Autowired
    private DbIsvManager dbIsvManager;

    @Autowired
    private DbIsvRoutePermissionManager dbIsvRoutePermissionManager;

    public void init() {
        ApiConfig apiConfig = ApiConfig.getInstance();
        apiConfig.setIsvManager(dbIsvManager);
        apiConfig.setIsvRoutePermissionManager(dbIsvRoutePermissionManager);

        // 从数据库加载isv信息
        log.debug("从数据库加载isv信息");
        dbIsvManager.load((isvInfoObj) -> {
            IsvInfo isvInfo = (IsvInfo)isvInfoObj;
            return isvInfo.getPubKey();
        });

        log.debug("从数据库加载路由权限信息");
        dbIsvRoutePermissionManager.load();
    }
}
