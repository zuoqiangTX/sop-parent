package com.gitee.sop.websiteserver.manager;

import com.gitee.sop.websiteserver.bean.DocInfo;
import com.gitee.sop.websiteserver.bean.DocItem;

import java.util.Collection;

/**
 * @author tanghc
 */
public interface DocManager {

    void load(String serviceId);

    DocItem get(String method, String version);

    DocInfo getByTitle(String title);

    Collection<DocInfo> listAll();
}
