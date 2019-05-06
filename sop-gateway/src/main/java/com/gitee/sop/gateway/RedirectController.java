package com.gitee.sop.gateway;

import com.gitee.sop.gatewaycommon.bean.SopConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tanghc
 */
@Controller
public class RedirectController {

    @Value("${zuul.servlet-path}")
    private String path;

    @RequestMapping("/{method}/{version}/")
    public String redirect(
            @PathVariable("method") String method
            , @PathVariable("version") String version
            , HttpServletRequest request
    ) {
        request.setAttribute(SopConstants.REDIRECT_METHOD_KEY, method);
        request.setAttribute(SopConstants.REDIRECT_VERSION_KEY, version);
        return "forward:" + path;
    }

}
