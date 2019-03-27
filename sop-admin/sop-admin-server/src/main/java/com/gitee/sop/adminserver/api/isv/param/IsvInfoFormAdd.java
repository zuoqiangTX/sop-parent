package com.gitee.sop.adminserver.api.isv.param;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author tanghc
 */
@Getter
@Setter
public class IsvInfoFormAdd extends IsvInfoForm {
    /** appKey, 数据库字段：app_key */
    @ApiDocField(description = "appKey", required = true)
    @NotBlank(message = "appKey不能为空")
    @Length(max = 100,message = "appKey长度不能超过100")
    private String appKey;
}
