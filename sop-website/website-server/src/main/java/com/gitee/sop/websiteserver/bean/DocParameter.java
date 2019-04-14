package com.gitee.sop.websiteserver.bean;

import lombok.Data;

/**
 * 参数	类型	是否必填	最大长度	描述	示例值
 * @author tanghc
 */
@Data
public class DocParameter {
    private String name;
    private String type;
    private boolean required;
    private String description;
    private String example = "";

}
