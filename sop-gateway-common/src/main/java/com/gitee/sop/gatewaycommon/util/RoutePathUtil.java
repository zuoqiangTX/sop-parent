package com.gitee.sop.gatewaycommon.util;

import org.springframework.cloud.gateway.route.Route;

import java.net.URI;

/**
 * @author tanghc
 */
public class RoutePathUtil {

    public static final String REGEX = "\\#";

    public static String findPath(String uri) {
        // #后面是对应的path
        String[] uriArr = uri.split(REGEX);
        if (uriArr.length == 2) {
            return uriArr[1];
        } else {
            return null;
        }
    }
}
