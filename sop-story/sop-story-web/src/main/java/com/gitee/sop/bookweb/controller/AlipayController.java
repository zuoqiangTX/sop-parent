package com.gitee.sop.bookweb.controller;

import com.gitee.sop.bookweb.message.StoryErrorEnum;
import com.gitee.sop.servercommon.annotation.ApiMapping;
import com.gitee.sop.story.api.domain.Story;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付宝服务端，假设签名验证通过后，到达这里进行具体的业务处理。
 * 这里演示如何接受业务参数。
 * @author tanghc
 */
@RestController
public class AlipayController {

    @ApiMapping(value = "alipay.story.get")
    public Story getStory(Story story) {
        if (story.getId() == 0) {
            throw StoryErrorEnum.param_error.getErrorMeta().getException("id无效");
        }
        return story;
    }

    @ApiMapping(value = "alipay.story.get", version = "1.2")
    public Story getStory11(Story story) {
        return story;
    }
}
