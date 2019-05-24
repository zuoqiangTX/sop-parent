package com.gitee.sop.storyweb.controller;

import com.gitee.sop.servercommon.annotation.ApiAbility;
import com.gitee.sop.story.api.domain.Story;
import com.gitee.sop.story.api.service.StoryService;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tanghc
 */
@RestController
public class StoryController implements StoryService {

    // 提供给Feign的服务
    @Override
    public Story getStory(int id) {
        Story story = new Story();
        story.setId(id);
        story.setName("海底小纵队(Feign)");
        return story;
    }

    // 测试参数绑定
    @ApiAbility
    @GetMapping("getStory3")
    public Story getStory3(String id) {
        System.out.println(id);
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队(默认版本号)");
        return story;
    }

    // 测试参数绑定，http://localhost:2222/story/getStory4?biz_content={"id":1, "name":"aaaa"}
    @ApiAbility
    @GetMapping("getStory4")
    public Story getStory4(Story param, P p2) {
        System.out.println(param + ", p2=" + p2);
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队(默认版本号)");
        return story;
    }

    @Data
    public static class P {
        private String name;
    }

}
