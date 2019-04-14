package com.gitee.sop.bookweb.controller;

import com.gitee.sop.servercommon.annotation.ApiAbility;
import com.gitee.sop.servercommon.annotation.ApiMapping;
import com.gitee.sop.story.api.domain.Story;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tanghc
 */
@ApiAbility // 放在这里，下面所有的接口都具备接口提供能力
@RestController
@RequestMapping("story2")
public class Story2Controller{

    @RequestMapping("getStory4")
    public Story getStory4() {
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队(getStory4)");
        return story;
    }

    // 优先使用方法上@ApiAbility
    @ApiOperation(value="获取故事信息2", notes = "获取故事信息2的详细信息")
    @ApiAbility(version = "1.4")
    @RequestMapping("getStory4")
    public Story storyget() {
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队(1.4)");
        return story;
    }

    // 优先使用@ApiMapping
    @ApiMapping(value = "story.get2")
    public Story getStory2() {
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队story.get2");
        return story;
    }


}
