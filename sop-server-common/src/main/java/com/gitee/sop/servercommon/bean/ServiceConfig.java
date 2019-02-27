package com.gitee.sop.servercommon.bean;

import com.gitee.sop.servercommon.configuration.DefaultGlobalExceptionHandler;
import com.gitee.sop.servercommon.configuration.GlobalExceptionHandler;
import com.gitee.sop.servercommon.param.ApiArgumentResolver;
import com.gitee.sop.servercommon.result.DefaultServiceResultBuilder;
import com.gitee.sop.servercommon.result.ServiceResultBuilder;
import lombok.Data;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class ServiceConfig {

    private static ServiceConfig instance = new ServiceConfig();

    private ServiceConfig() {
    }

    /**
     * 默认版本号
     */
    private String defaultVersion = "";

    /**
     * 错误模块
     */
    private List<String> i18nModules = new ArrayList<String>();

    /**
     * 解析业务参数
     */
    private HandlerMethodArgumentResolver methodArgumentResolver = new ApiArgumentResolver();

    /**
     * 返回结果处理
     */
    private ServiceResultBuilder serviceResultBuilder = new DefaultServiceResultBuilder();

    /**
     * 全局异常处理
     */
    private GlobalExceptionHandler globalExceptionHandler = new DefaultGlobalExceptionHandler();

    public static ServiceConfig getInstance() {
        return instance;
    }

    public static void setInstance(ServiceConfig instance) {
        ServiceConfig.instance = instance;
    }
}
