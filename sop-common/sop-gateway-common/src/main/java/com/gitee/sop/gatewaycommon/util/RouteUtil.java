package com.gitee.sop.gatewaycommon.util;

/**
 * @author tanghc
 */
public class RouteUtil {

    private RouteUtil(){}

    public static final String REGEX = "\\#";

    public static final String PROTOCOL_LOAD_BALANCE = "lb://";

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
