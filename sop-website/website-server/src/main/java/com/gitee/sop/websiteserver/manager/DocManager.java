package com.gitee.sop.websiteserver.manager;

import com.gitee.sop.websiteserver.bean.DocInfo;

import java.util.Collection;

/**
 * @author tanghc
 */
public interface DocManager {

    void load(String serviceId);

    DocInfo getByTitle(String title);

    Collection<DocInfo> listAll();
}
