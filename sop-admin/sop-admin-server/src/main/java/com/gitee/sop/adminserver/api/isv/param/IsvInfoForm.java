package com.gitee.sop.adminserver.api.isv.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class IsvInfoForm {
    /** secret, 数据库字段：secret */
    @ApiDocField(description = "secret", required = true)
    @NotBlank(message = "secret不能为空")
    @Length(max = 100,message = "secret长度不能超过100")
    private String secret;

    /** 公钥, 数据库字段：pub_key */
    @ApiDocField(description = "pubKey", required = true)
    @NotBlank(message = "pubKey不能为空")
    private String pubKey;

    /** 私钥, 数据库字段：pri_key */
    @ApiDocField(description = "priKey", required = true)
    @NotBlank(message = "priKey不能为空")
    private String priKey;

    /** 0启用，1禁用, 数据库字段：status */
    @ApiDocField(description = "状态：0：启用，1：禁用")
    private Byte status = 0;

    @NotEmpty(message = "角色不能为空")
    private List<String> roleCode;
}
