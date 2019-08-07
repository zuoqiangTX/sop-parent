package com.gitee.sop.storyweb.controller;

import com.gitee.sop.servercommon.annotation.ApiAbility;
import com.gitee.sop.servercommon.annotation.ApiMapping;
import com.gitee.sop.servercommon.bean.OpenContext;
import com.gitee.sop.servercommon.bean.ServiceContext;
import com.gitee.sop.story.api.domain.Story;
import com.gitee.sop.storyweb.controller.param.StoryParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 支付宝服务端，假设签名验证通过后，到达这里进行具体的业务处理。
 * @author tanghc
 */
@RestController
@Slf4j
@Api(tags = "故事接口")
public class AlipayController {

    // http://localhost:2222/story_get
    // 原生的接口，可正常调用
    @RequestMapping("story_get")
    public Story story_get() {
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队(原生)");
        return story;
    }

    // http://localhost:2222/story.get/
    // 接口名，使用默认版本号
    @ApiMapping(value = "story.get")
    public Story storyget() {
        // 获取开放平台参数
        OpenContext openContext = ServiceContext.getCurrentContext().getOpenContext();
        String appId = openContext.getAppId();
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队(默认版本号), app_id:" + appId);
        return story;
    }

    // http://localhost:2222/story.get/?version=1.1
    // 接口名 + 版本号
    @ApiMapping(value = "story.get", version = "1.1")
    public Story getStory2() {
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队1.0");
        return story;
    }

    // http://localhost:2222/story.get/?name=111&version=2.0
    // 接口名 + 版本号
    @ApiMapping(value = "story.get", version = "2.0")
    public Story getStory20(Story story) {
        return story;
    }

    // 忽略验证
    @ApiMapping(value = "story.get", version = "2.1", ignoreValidate = true)
    public Story getStory21(Story story) {
        OpenContext openContext = ServiceContext.getCurrentContext().getOpenContext();
        // 此处的param和story参数是一样的
        Story param = openContext.getBizObject(Story.class);
        boolean isSame = story == param;
        story.setName(story.getName() + ", story.get2.1, ignoreValidate = true, story==param:" + isSame);
        return story;
    }

    /**
     * 另一种方式，OpenContext泛型参数填bizObject类<br>
     * 调用openContext.getBizObject()可直接获得对象<br>
     * 此方式等价于：
     * <pre>
     * public Story getStory22(Story bizObject) {
     *     OpenContext openContext = ServiceContext.getCurrentContext().getOpenContext();
     *     // 获取appid，更多方法查看OpenContext类
     *     String appId = openContext.getAppId();
     *     System.out.println(appId);
     *     return bizObject;
     * }
     * </pre>
     * @param openContext
     * @return
     */
    @ApiMapping(value = "story.get", version = "2.2")
    public Story getStory22(OpenContext<Story> openContext) {
        Story bizObject = openContext.getBizObject();
        // 获取appid，更多方法查看OpenContext类
        String appId = openContext.getAppId();
        System.out.println(appId);
        return bizObject;
    }

    // http://localhost:2222/getStory2
    // 遗留接口具备开放平台能力
    @ApiAbility
    @GetMapping("getStory2")
    public Story getStory2_0() {
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队(默认版本号)");
        return story;
    }

    // http://localhost:2222/getStory2?version=2.1
    // 遗留接口具备开放平台能力，在原来的基础上加版本号
    @ApiAbility(version = "2.1")
    @GetMapping("getStory2")
    public Story getStory2_1() {
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队2.1");
        return story;
    }

    // http://localhost:2222/alipay.story.get/
    @ApiMapping(value = "alipay.story.get")
    public Story getStory() {
        Story story = new Story();
        story.setId(1);
        story.setName("海底小纵队(alipay.story.get1.0)");
        return story;
    }

    /**
     * 参数绑定
     * @param story 对应biz_content中的内容，并自动JSR-303校验
     * @return
     */
    @ApiOperation(value = "获取故事信息", notes = "说明接口的详细信息，介绍，用途，注意事项等。")
    @ApiMapping(value = "alipay.story.find")
    public StoryVO getStory2(StoryParam story) {
        log.info("获取故事信息参数, story: {}", story);
        // 获取其它参数
        OpenContext openContext = ServiceContext.getCurrentContext().getOpenContext();
        String app_id = openContext.getAppId();
        StoryVO storyVO = new StoryVO();
        storyVO.id = 1L;
        storyVO.name = "白雪公主, app_id:" + app_id;
        storyVO.gmt_create = new Date();
        return storyVO;
    }

    /**
     *
     * @param story 对应biz_content中的内容，并自动JSR-303校验
     * @return
     */
    @ApiMapping(value = "alipay.story.get", version = "1.2")
    public Story getStory11(Story story) {
        Story story2 = new Story();
        story2.setId(1);
        story2.setName("海底小纵队(alipay.story.get1.2)");
        return story2;
    }

    /**
     * 演示文档表格树
     * @param story
     * @return
     */
    @ApiOperation(value="获取分类信息", notes = "演示表格树")
    @ApiMapping(value = "alipay.category.get", method = RequestMethod.POST)
    public Category getCategory(Category story) {
        StoryVO storyVO = new StoryVO();
        storyVO.id = 1L;
        storyVO.name = "白雪公主";
        storyVO.gmt_create = new Date();
        Category category = new Category();
        category.setCategoryName("娱乐");
        category.setStory(storyVO);
        return category;
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

    @Data
    public static class Category {
        @ApiModelProperty(value = "分类名称", example = "娱乐")
        private String categoryName;

        @ApiModelProperty(value = "分类故事")
        private StoryVO story;
    }
}
