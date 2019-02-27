package com.gitee.sop.servercommon.configuration;

import com.gitee.sop.servercommon.bean.ServiceConfig;
import com.gitee.sop.servercommon.exception.ServiceException;
import com.gitee.sop.servercommon.result.ServiceResultBuilder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理
 * @author tanghc
 */
@ControllerAdvice
public class DefaultGlobalExceptionHandler implements GlobalExceptionHandler {

    @RequestMapping("/error")
    @ResponseBody
    public Object error(HttpServletRequest request, HttpServletResponse response) {
        ServiceResultBuilder serviceResultBuilder = ServiceConfig.getInstance().getServiceResultBuilder();
        return serviceResultBuilder.buildError(request, response, new RuntimeException("系统繁忙"));
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Object exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception exception) throws Exception {
        return this.processError(request, response, exception);
    }

    /**
     * 处理异常
     *
     * @param request
     * @param response
     * @param exception
     * @return
     */
    protected Object processError(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        ServiceResultBuilder serviceResultBuilder = ServiceConfig.getInstance().getServiceResultBuilder();
        return serviceResultBuilder.buildError(request, response, exception);
    }
}
