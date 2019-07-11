package com.gitee.sop.websiteserver.bean;

import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class DocItem {
    private String module;
    private String name;
    private String version;
    private String summary;
    private String description;
    // 是否多文件上传
    private boolean multiple;

    List<DocParameter> requestParameters;
    List<DocParameter> responseParameters;

    public String getNameVersion() {
        return name + version;
    }
}
