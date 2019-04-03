package com.gitee.sop.sdk.response;

import lombok.Data;

import java.util.Date;

@Data
public class GetStoryResponse extends BaseResponse {
    private Long id;
    private String name;
    private Date gmt_create;
}
