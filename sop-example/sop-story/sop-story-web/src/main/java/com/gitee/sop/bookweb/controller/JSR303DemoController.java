package com.gitee.sop.bookweb.controller;

import com.gitee.sop.bookweb.controller.param.GoodsParam;
import com.gitee.sop.servercommon.annotation.ApiMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 演示参数验证
 * @author tanghc
 */
@RestController
public class JSR303DemoController {

    @ApiMapping(value = "goods.add")
    public Object addGoods(GoodsParam param) {
        return param;
    }
}
