package com.gitee.sop.bookweb.controller;

import com.gitee.sop.servercommon.annotation.ApiMapping;
import com.gitee.sop.story.api.domain.Story;
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

    @ApiMapping(value = "alipay.story.find")
    public StoryVO getStory2(Story story) {
        StoryVO storyVO = new StoryVO();
        storyVO.id = 1L;
        storyVO.name = "白雪公主";
        storyVO.gmtCreate = new Date();
        return storyVO;
    }

    @ApiMapping(value = "alipay.story.get", version = "1.2")
    public Story getStory11(Story story) {
        return story;
    }

    @Data
    public static class StoryVO {
        private Long id;
        private String name;
        private Date gmtCreate;
    }
}
