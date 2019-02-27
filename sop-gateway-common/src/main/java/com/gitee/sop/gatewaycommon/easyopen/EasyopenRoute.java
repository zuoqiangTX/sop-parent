package com.gitee.sop.gatewaycommon.easyopen;

import org.springframework.cloud.netflix.zuul.filters.Route;

import java.util.Collections;
import java.util.Set;

/**
 * @author tanghc
 */
public class EasyopenRoute extends Route {
    public EasyopenRoute(String id, String path, String location, String prefix, Boolean retryable, Set<String> ignoredHeaders) {
        super(id, path, location, prefix, retryable, ignoredHeaders);
    }

    public EasyopenRoute(String id,  String location) {
        this(id, "/" + location + "/", location, "", false, Collections.emptySet());
    }

}
