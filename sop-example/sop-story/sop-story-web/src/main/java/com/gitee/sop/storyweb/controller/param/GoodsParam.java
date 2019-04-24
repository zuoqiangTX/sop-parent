package com.gitee.sop.storyweb.controller.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class GoodsParam {
//    @NotEmpty(message = "商品名称不能为空")
    private String goods_name;

    @NotEmpty(message = "{goods.remark.notNull}")
    private String goods_remark;

    // 传参的格式：{xxx}=value1,value2...
    @Length(min = 3, max = 20, message = "{goods.comment.length}=3,20")
    private String goods_comment;
}