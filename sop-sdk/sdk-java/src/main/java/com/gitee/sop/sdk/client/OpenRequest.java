package com.gitee.sop.sdk.client;

import com.gitee.sop.sdk.common.OpenConfig;
import com.gitee.sop.sdk.common.RequestForm;
import com.gitee.sop.sdk.common.UploadFile;
import com.gitee.sop.sdk.response.BaseResponse;
import com.gitee.sop.sdk.util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 负责请求操作
 *
 * @author tanghc
 */
public class OpenRequest {

    private static final String HTTP_ERROR_CODE = "-400";

    private OpenHttp openHttp;

    public OpenRequest(OpenConfig openConfig) {
        this.openHttp = new OpenHttp(openConfig);
    }

    public String request(String url, RequestForm requestForm, Map<String, String> header) {
        return this.doPost(url, requestForm, header);
    }

    protected String doPost(String url, RequestForm requestForm, Map<String, String> header) {
        try {
            Map<String, String> form = requestForm.getForm();
            List<UploadFile> files = requestForm.getFiles();
            if (files != null && files.size() > 0) {
                return openHttp.postFile(url, form, header, files);
            } else {
                return openHttp.postFormBody(url, form, header);
            }
        } catch (IOException e) {
            return this.causeException(e);
        }
    }

    protected String causeException(Exception e) {
        ErrorResponse result = new ErrorResponse();
        result.setCode(HTTP_ERROR_CODE);
        result.setSubCode(HTTP_ERROR_CODE);
        result.setSubMsg(e.getMessage());
        result.setMsg(e.getMessage());
        return JsonUtil.toJSONString(result);
    }

    static class ErrorResponse extends BaseResponse {
    }
}
