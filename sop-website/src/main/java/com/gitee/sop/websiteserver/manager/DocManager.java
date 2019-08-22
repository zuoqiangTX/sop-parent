package com.gitee.sop.websiteserver.manager;

import com.gitee.sop.websiteserver.bean.DocInfo;

import java.util.Collection;

/**
 * @author tanghc
 */
public interface DocManager {

    void addDocInfo(String serviceId, String docJson);

    DocInfo getByTitle(String title);

    Collection<DocInfo> listAll();

    void remove(String serviceId);
}
