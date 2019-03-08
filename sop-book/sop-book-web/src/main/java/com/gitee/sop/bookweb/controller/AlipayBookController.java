package com.gitee.sop.bookweb.controller;

import com.gitee.sop.bookweb.consumer.StoryServiceConsumer;
import com.gitee.sop.servercommon.annotation.ApiMapping;
import com.gitee.sop.story.api.domain.Story;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * 支付宝服务端，假设签名验证通过后，到达这里进行具体的业务处理。
 * 这里演示如何接受业务参数。
 * @author tanghc
 */
@RestController
public class AlipayBookController {

    @Autowired
    StoryServiceConsumer storyServiceConsumer;

    @ApiMapping(value = "alipay.book.get")
    public Story getBook() {
        Story story = new Story();
        story.setId(1);
        story.setName("白雪公主(alipay.book.get)");
        return story;
    }

    // 调用story服务
    @ApiMapping(value = "alipay.book.story.get")
    public Object getBook2() {
        Story story = new Story();
        story.setId(1);
        story.setName("白雪公主(alipay.book.story.get)");
        Story story2 = storyServiceConsumer.getStory(1);
        return Arrays.asList(story, story2);
    }

}
