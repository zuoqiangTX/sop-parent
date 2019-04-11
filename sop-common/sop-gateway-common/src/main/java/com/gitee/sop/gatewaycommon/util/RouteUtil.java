package com.gitee.sop.gatewaycommon.util;

import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import com.gitee.sop.gatewaycommon.bean.RouteConfig;
import com.gitee.sop.gatewaycommon.manager.RouteConfigManager;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import com.gitee.sop.gatewaycommon.param.ApiParam;

/**
 * @author tanghc
 */
public class RouteUtil {

    private RouteUtil(){}

    private static final String REGEX = "\\#";

    public static final String PROTOCOL_LOAD_BALANCE = "lb://";

    /**
     * 检测能否访问
     * @param param 接口参数
     */
    public static void checkEnable(ApiParam param) {
        String routeId = param.fetchNameVersion();
        RouteConfigManager routeConfigManager = ApiConfig.getInstance().getRouteConfigManager();
        RouteConfig routeConfig = routeConfigManager.get(routeId);
        if (!routeConfig.enable()) {
            throw ErrorEnum.ISP_API_DISABLED.getErrorMeta().getException();
        }
    }

    public static String findPath(String uri) {
        // #后面是对应的path
        String[] uriArr = uri.split(REGEX);
        if (uriArr.length == 2) {
            return uriArr[1];
        } else {
            return null;
        }
    }

    public static String getZuulLocation(String uri) {
        if (uri.toLowerCase().startsWith(PROTOCOL_LOAD_BALANCE)) {
            return uri.substring(PROTOCOL_LOAD_BALANCE.length());
        }
        return uri;
    }

}
