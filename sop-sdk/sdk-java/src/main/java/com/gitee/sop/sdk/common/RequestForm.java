package com.gitee.sop.sdk.common;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RequestForm  {

    /** 请求表单内容 */
    private Map<String, String> form;
    /** 上传文件 */
    private List<UploadFile> files;

    public RequestForm(Map<String, String> m) {
        this.form = m;
    }
}
