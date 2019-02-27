package com.gitee.sop.gatewaycommon.configuration;

import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.result.ResultExecutor;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理网关自身异常
 *
 * @author tanghc
 */
@Controller
@Slf4j
public class BaseZuulController implements ErrorController {

    public static final String ERROR_PATH = "/error";

    // 错误最终会到这里来
    @RequestMapping(ERROR_PATH)
    @ResponseBody
    public Object error(HttpServletRequest request, HttpServletResponse response) {
        RequestContext ctx = RequestContext.getCurrentContext();
        Throwable throwable = ctx.getThrowable();
        return this.buildResult(throwable);
    }

    protected Object buildResult(Throwable throwable) {
        ResultExecutor resultExecutor = ApiContext.getApiConfig().getResultExecutor();
        return resultExecutor.mergeError(throwable);
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
