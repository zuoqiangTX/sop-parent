package com.gitee.sop.websiteserver.manager;

import com.gitee.sop.websiteserver.bean.DocModule;
import com.gitee.sop.websiteserver.bean.DocItem;

import java.util.Collection;

/**
 * @author tanghc
 */
public interface DocManager {

    void load();

    DocItem get(String method, String version);

    DocModule getByTitle(String title);

    Collection<DocModule> listAll();
}
