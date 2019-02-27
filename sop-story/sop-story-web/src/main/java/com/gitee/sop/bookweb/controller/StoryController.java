package com.gitee.sop.bookweb.controller;

import com.gitee.sop.servercommon.annotation.ApiMapping;
import com.gitee.sop.servercommon.annotation.ApiAbility;
import com.gitee.sop.story.api.domain.Story;
import com.gitee.sop.story.api.service.StoryService;
import org.springframework.web.bind.annotation.RequestMapping;
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

    // http://localhost:2222/story/story_get
    // 原生的接口，可正常调用
    @RequestMapping("story_get")
    public Story getStory4() {
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队(原生)");
        return story;
    }

    // http://localhost:2222/story/story.get/
    // 接口名，使用默认版本号
    @ApiMapping(value = "story.get")
    public Story storyget() {
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队(默认版本号)");
        return story;
    }

    // http://localhost:2222/story/story.get/?version=1.1
    // 接口名 + 版本号
    @ApiMapping(value = "story.get", version = "1.1")
    public Story getStory2() {
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队1.0");
        return story;
    }

    // http://localhost:2222/story/story.get/?version=2.0
    // 接口名 + 版本号
    @ApiMapping(value = "story.get", version = "2.0")
    public Story getStory20(Story story) {
        return story;
    }

    // http://localhost:2222/story/getStory2
    // 遗留接口具备开放平台能力
    @ApiAbility
    @RequestMapping("getStory2")
    public Story getStory2_0() {
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队(默认版本号)");
        return story;
    }

    // http://localhost:2222/story/getStory2?version=2.1
    // 在原来的基础上加版本号
    @ApiAbility(version = "2.1")
    @RequestMapping("getStory2")
    public Story getStory2_1() {
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队2.1");
        return story;
    }


}
