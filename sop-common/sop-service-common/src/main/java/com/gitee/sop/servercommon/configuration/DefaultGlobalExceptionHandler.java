package com.gitee.sop.servercommon.configuration;

import com.gitee.sop.servercommon.bean.ServiceConfig;
import com.gitee.sop.servercommon.exception.ServiceException;
import com.gitee.sop.servercommon.result.ServiceResultBuilder;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class DefaultGlobalExceptionHandler implements GlobalExceptionHandler {

    @RequestMapping("/error")
    @ResponseBody
    public Object error(HttpServletRequest request, HttpServletResponse response) {
        ServiceResultBuilder serviceResultBuilder = ServiceConfig.getInstance().getServiceResultBuilder();
        return serviceResultBuilder.buildError(request, response, new RuntimeException("系统繁忙"));
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Object serviceExceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        return this.processError(request, response, exception);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        log.error("系统错误", exception);
        return this.processError(request, response, new RuntimeException("系统错误"));
    }

    /**
     * 处理异常
     *
     * @param request
     * @param response
     * @param exception
     * @return 返回最终结果
     */
    protected Object processError(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        ServiceResultBuilder serviceResultBuilder = ServiceConfig.getInstance().getServiceResultBuilder();
        return serviceResultBuilder.buildError(request, response, exception);
    }
}
