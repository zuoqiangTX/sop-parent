package com.gitee.sop.gatewaycommon.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.bean.BaseRouteDefinition;
import com.gitee.sop.gatewaycommon.bean.BaseServiceRouteInfo;
import com.gitee.sop.gatewaycommon.bean.ErrorDefinition;
import com.gitee.sop.gatewaycommon.bean.SopConstants;
import com.gitee.sop.gatewaycommon.bean.TargetRoute;
import com.gitee.sop.gatewaycommon.manager.RouteRepositoryContext;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import com.gitee.sop.gatewaycommon.message.ErrorMeta;
import com.gitee.sop.gatewaycommon.param.ParamNames;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * @author tanghc
 */
public abstract class BaseExecutorAdapter<T, R> implements ResultExecutor<T, R> {
    private static final ErrorMeta SUCCESS_META = ErrorEnum.SUCCESS.getErrorMeta();
    private static final ErrorMeta ISP_UNKNOW_ERROR_META = ErrorEnum.ISP_UNKNOW_ERROR.getErrorMeta();
    private static final ErrorMeta ISP_BIZ_ERROR = ErrorEnum.BIZ_ERROR.getErrorMeta();

    public static final String GATEWAY_CODE_NAME = "code";
    public static final String GATEWAY_MSG_NAME = "msg";
    public static final String ARRAY_START = "[";
    public static final String ARRAY_END = "]";
    public static final String ROOT_JSON = "{'items':%s}".replace("'", "\"");


    /**
     * 获取业务方约定的返回码
     *
     * @param t
     * @return 返回返回码
     */
    public abstract int getResponseStatus(T t);

    /**
     * 获取微服务端的错误信息
     *
     * @param t
     * @return
     */
    public abstract String getResponseErrorMessage(T t);

    /**
     * 返回Api参数
     *
     * @param t
     * @return 返回api参数
     */
    public abstract Map<String, Object> getApiParam(T t);

    @Override
    public String mergeResult(T request, String serviceResult) {
        boolean isMergeResult = this.isMergeResult(request);
        if (!isMergeResult) {
            return serviceResult;
        }
        serviceResult = wrapResult(serviceResult);
        int responseStatus = this.getResponseStatus(request);
        JSONObject jsonObjectService;
        if (responseStatus == HttpStatus.OK.value()) {
            // 200正常返回
            jsonObjectService = JSON.parseObject(serviceResult);
            jsonObjectService.put(GATEWAY_CODE_NAME, SUCCESS_META.getCode());
            jsonObjectService.put(GATEWAY_MSG_NAME, SUCCESS_META.getError().getMsg());
        } else if (responseStatus == SopConstants.BIZ_ERROR_STATUS) {
            // 如果是业务出错
            this.storeError(request, ErrorType.BIZ);
            jsonObjectService = JSON.parseObject(serviceResult);
            jsonObjectService.put(GATEWAY_CODE_NAME, ISP_BIZ_ERROR.getCode());
            jsonObjectService.put(GATEWAY_MSG_NAME, ISP_BIZ_ERROR.getError().getMsg());
        } else {
            this.storeError(request, ErrorType.UNKNOWN);
            // 微服务端有可能返回500错误
            // {"path":"/book/getBook3","error":"Internal Server Error","message":"id不能为空","timestamp":"2019-02-13T07:41:00.495+0000","status":500}
            jsonObjectService = new JSONObject();
            jsonObjectService.put(GATEWAY_CODE_NAME, ISP_UNKNOW_ERROR_META.getCode());
            jsonObjectService.put(GATEWAY_MSG_NAME, ISP_UNKNOW_ERROR_META.getError().getMsg());
        }
        return this.merge(request, jsonObjectService);
    }

    /**
     * 保存错误信息
     *
     * @param request
     */
    protected void storeError(T request, ErrorType errorType) {
        ApiInfo apiInfo = this.getApiInfo(request);
        String errorMsg = this.getResponseErrorMessage(request);
        ErrorDefinition errorDefinition = new ErrorDefinition();
        BeanUtils.copyProperties(apiInfo, errorDefinition);
        errorDefinition.setErrorMsg(errorMsg);
        if (errorType == ErrorType.UNKNOWN) {
            ApiConfig.getInstance().getServiceErrorManager().saveUnknownError(errorDefinition);
        }
        if (errorType == ErrorType.BIZ) {
            ApiConfig.getInstance().getServiceErrorManager().saveBizError(errorDefinition);
        }
    }


    /**
     * 该路由是否合并结果
     *
     * @param request
     * @return true：需要合并
     */
    protected boolean isMergeResult(T request) {
        // 默认全局设置
        Boolean defaultSetting = ApiContext.getApiConfig().getMergeResult();
        if (defaultSetting != null) {
            return defaultSetting;
        }
        ApiInfo apiInfo = this.getApiInfo(request);
        BaseRouteDefinition baseRouteDefinition = apiInfo.baseRouteDefinition;
        return Optional.ofNullable(baseRouteDefinition)
                .map(routeDefinition -> {
                    int mergeResult = baseRouteDefinition.getMergeResult();
                    return BooleanUtils.toBoolean(mergeResult);
                })
                .orElse(true);
    }

    protected ApiInfo getApiInfo(T request) {
        Map<String, Object> params = this.getApiParam(request);
        String name = Optional.ofNullable(params)
                .map(map -> (String) map.get(ParamNames.API_NAME))
                .orElse("method.unknown");

        String version = Optional.ofNullable(params)
                .map(map -> (String) map.get(ParamNames.VERSION_NAME))
                .orElse("version.unknown");

        TargetRoute targetRoute = RouteRepositoryContext.getRouteRepository().get(name + version);

        String serviceId = Optional.ofNullable(targetRoute)
                .flatMap(route -> Optional.ofNullable(route.getServiceRouteInfo()))
                .map(BaseServiceRouteInfo::getServiceId)
                .orElse("serviceId.unknown");

        BaseRouteDefinition baseRouteDefinition = Optional.ofNullable(targetRoute)
                .map(route -> route.getRouteDefinition())
                .orElse(null);

        ApiInfo apiInfo = new ApiInfo();
        apiInfo.name = name;
        apiInfo.version = version;
        apiInfo.serviceId = serviceId;
        apiInfo.baseRouteDefinition = baseRouteDefinition;
        return apiInfo;
    }

    protected String wrapResult(String serviceResult) {
        if (serviceResult == null) {
            serviceResult = "";
        }
        serviceResult = serviceResult.trim();
        if (StringUtils.isEmpty(serviceResult)) {
            return SopConstants.EMPTY_JSON;
        }
        if (serviceResult.startsWith(ARRAY_START) && serviceResult.endsWith(ARRAY_END)) {
            return String.format(ROOT_JSON, serviceResult);
        }
        return serviceResult;
    }

    public String merge(T exchange, JSONObject jsonObjectService) {
        JSONObject ret = new JSONObject();
        String name = "error";
        Map<String, Object> params = this.getApiParam(exchange);
        if (params != null) {
            Object method = params.get(ParamNames.API_NAME);
            if (method != null) {
                name = String.valueOf(method);
            }
        }
        ApiConfig apiConfig = ApiConfig.getInstance();
        // 点换成下划线
        DataNameBuilder dataNameBuilder = apiConfig.getDataNameBuilder();
        String method = dataNameBuilder.build(name);
        ret.put(method, jsonObjectService);
        this.appendReturnSign(apiConfig, params, ret);
        ResultAppender resultAppender = apiConfig.getResultAppender();
        if (resultAppender != null) {
            resultAppender.append(ret, params, exchange);
        }
        return ret.toJSONString();
    }

    protected void appendReturnSign(ApiConfig apiConfig, Map<String, ?> params, JSONObject ret) {
        if (apiConfig.isShowReturnSign() && params != null) {
            Object appKey = params.get(ParamNames.APP_KEY_NAME);
            String sign = this.createReturnSign(String.valueOf(appKey));
            ret.put(ParamNames.SIGN_NAME, sign);
        }
    }

    /**
     * 这里需要使用平台的私钥生成一个sign，需要配置两套公私钥。目前暂未实现
     *
     * @param appKey
     * @return
     */
    protected String createReturnSign(String appKey) {
        // TODO: 返回sign
        return null;
    }

    @Getter
    @Setter
    protected static class ApiInfo {
        private String name;
        private String version;
        private String serviceId;
        private BaseRouteDefinition baseRouteDefinition;
    }

    enum ErrorType {
        UNKNOWN, BIZ
    }
}
