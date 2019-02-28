package com.gitee.sop.servercommon.manager;

import com.gitee.sop.servercommon.bean.ServiceApiInfo;

/**
 * @author tanghc
 */
public interface ApiMetaManager {
    String API_STORE_KEY = "com.gitee.sop.api";

    /**
     * 上传API
     * @param serviceApiInfo
     */
    void uploadApi(ServiceApiInfo serviceApiInfo);
}
