package com.gitee.sop.servercommon.manager;

import com.gitee.sop.servercommon.bean.ServiceApiInfo;
import com.gitee.sop.servercommon.route.ServiceRouteInfo;
import com.gitee.sop.servercommon.util.OpenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tanghc
 */
@Slf4j
@RestController
public class ServiceRouteController {

    private static final String SECRET = "a3d9sf!1@odl90zd>fkASwq";

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private Environment environment;


    @RequestMapping("/sop/routes")
    public ServiceRouteInfo listRoutes(HttpServletRequest request, HttpServletResponse response) {
        if (!OpenUtil.validateSimpleSign(request, SECRET)) {
            log.error("签名验证失败, params:{}", request.getQueryString());
            return null;
        }
        String serviceId = environment.getProperty("spring.application.name");
        ApiMetaBuilder apiMetaBuilder = new ApiMetaBuilder();
        ServiceApiInfo serviceApiInfo = apiMetaBuilder.getServiceApiInfo(serviceId, requestMappingHandlerMapping);
        ServiceRouteInfoBuilder serviceRouteInfoBuilder = new ServiceRouteInfoBuilder(environment);
        return serviceRouteInfoBuilder.build(serviceApiInfo);
    }


}
