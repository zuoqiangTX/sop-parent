package com.gitee.sop.storyweb.controller.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CategoryParam {
    @ApiModelProperty(value = "分类名称", example = "娱乐")
    private String categoryName;

    @ApiModelProperty(value = "分类故事")
    private StoryParam storyParam;
}