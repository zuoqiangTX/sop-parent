package com.gitee.sop.servercommon.configuration;

import com.gitee.sop.servercommon.bean.ServiceConfig;
import com.gitee.sop.servercommon.exception.ServiceException;
import com.gitee.sop.servercommon.result.ServiceResultBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tanghc
 */
@RestController
public class ErrorController {

    @RequestMapping("/error")
    public Object error(HttpServletRequest request, HttpServletResponse response) {
        return getResult(request, response);
    }

    protected Object getResult(HttpServletRequest request, HttpServletResponse response) {
        ServiceResultBuilder serviceResultBuilder = ServiceConfig.getInstance().getServiceResultBuilder();
        return serviceResultBuilder.buildError(request, response, new ServiceException("系统繁忙"));
    }
}
