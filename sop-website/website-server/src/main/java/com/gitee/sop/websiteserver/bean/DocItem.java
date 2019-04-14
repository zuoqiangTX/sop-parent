package com.gitee.sop.websiteserver.bean;

import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class DocItem {
    private String name;
    private String version;
    private String summary;
    private String description;

    List<DocParameter> requestParameters;
    List<DocParameter> responseParameters;

    public String getNameVersion() {
        return name + version;
    }
}
