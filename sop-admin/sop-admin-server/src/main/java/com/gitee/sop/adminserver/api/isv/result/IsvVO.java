package com.gitee.sop.adminserver.api.isv.result;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author thc
 */
@Data
public class IsvVO {
    /**  数据库字段：id */
    @ApiDocField(description = "id", example = "1")
    private Long id;

    /** appKey, 数据库字段：app_key */
    @ApiDocField(description = "appKey", example = "aaaa")
    private String appKey;

    /** secret, 数据库字段：secret */
    @ApiDocField(description = "secret", example = "bbbb")
    private String secret;

    /** 公钥, 数据库字段：pub_key */
    @ApiDocField(description = "pubKey")
    private String pubKey;

    /** 私钥, 数据库字段：pri_key */
    @ApiDocField(description = "priKey")
    private String priKey;

    /** 0启用，1禁用, 数据库字段：status */
    @ApiDocField(description = "状态：0启用，1禁用")
    private Byte status;

    @ApiDocField(description = "签名类型：1:RSA2,2:MD5")
    private Byte signType;

    /**  数据库字段：gmt_create */
    @ApiDocField(description = "添加时间")
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    @ApiDocField(description = "修改时间")
    private Date gmtModified;

    @ApiDocField(description = "角色列表")
    private List<RoleVO> roleList;
}
