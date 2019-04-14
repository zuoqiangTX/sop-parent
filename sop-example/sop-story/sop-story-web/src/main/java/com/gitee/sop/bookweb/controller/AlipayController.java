package com.gitee.sop.bookweb.controller;

import com.gitee.sop.bookweb.controller.param.StoryParam;
import com.gitee.sop.servercommon.annotation.ApiMapping;
import com.gitee.sop.story.api.domain.Story;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 支付宝服务端，假设签名验证通过后，到达这里进行具体的业务处理。
 * 这里演示如何接受业务参数。
 * @author tanghc
 */
@RestController
public class AlipayController {

    @ApiMapping(value = "alipay.story.get")
    public Story getStory() {
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队(alipay.story.get)");
        return story;
    }

    @ApiOperation(value="获取故事信息", notes = "说明接口的详细信息，介绍，用途，注意事项等。")
    @ApiMapping(value = "alipay.story.find")
    public StoryVO getStory2(StoryParam story) {
        StoryVO storyVO = new StoryVO();
        storyVO.id = 1L;
        storyVO.name = "白雪公主";
        storyVO.gmt_create = new Date();
        return storyVO;
    }

    @ApiMapping(value = "alipay.story.get", version = "1.2")
    public Story getStory11(Story story) {
        return story;
    }

    @Data
    public static class StoryVO {
        @ApiModelProperty(value = "故事ID", example = "1")
        private Long id;
        @ApiModelProperty(value = "故事名称", example = "海底小纵队")
        private String name;
        @ApiModelProperty(value = "创建时间", example = "2019-04-14 19:02:12")
        private Date gmt_create;
    }
}
