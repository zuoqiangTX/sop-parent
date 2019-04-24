package com.gitee.sop.storyweb.controller;

import com.gitee.sop.servercommon.annotation.ApiMapping;
import com.gitee.sop.story.api.domain.Story;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付宝服务端，假设签名验证通过后，到达这里进行具体的业务处理。
 * 这里演示如何接受业务参数。
 * @author tanghc
 */
@RestController
public class PermissionDemoController {

    @ApiMapping(value = "permission.story.get", permission = true)
    public Story getStory() {
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队(permission.story.get)");
        return story;
    }

}
