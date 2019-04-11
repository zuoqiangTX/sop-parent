package com.gitee.sop.gatewaycommon.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.bean.SopConstants;
import com.gitee.sop.gatewaycommon.bean.TargetRoute;
import com.gitee.sop.gatewaycommon.manager.RouteRepositoryContext;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import com.gitee.sop.gatewaycommon.message.ErrorMeta;
import com.gitee.sop.gatewaycommon.param.ParamNames;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author tanghc
 */
public abstract class BaseExecutorAdapter<T, R> implements ResultExecutor<T, R> {
    private static final ErrorMeta SUCCESS_META = ErrorEnum.SUCCESS.getErrorMeta();
    private static final ErrorMeta ISP_UNKNOW_ERROR_META = ErrorEnum.ISP_UNKNOW_ERROR.getErrorMeta();
    private static final ErrorMeta ISP_BIZ_ERROR = ErrorEnum.BIZ_ERROR.getErrorMeta();

    private static final char DOT = '.';
    private static final char UNDERLINE = '_';
    public static final String GATEWAY_CODE_NAME = "code";
    public static final String GATEWAY_MSG_NAME = "msg";
    public static final String DATA_SUFFIX = "_response";
    public static final String ARRAY_START = "[";
    public static final String ARRAY_END = "]";
    public static final String ROOT_JSON = "{'items':%s}".replace("'", "\"");


    /**
     * 获取业务方约定的返回码
     * @param t
     * @return 返回返回码
     */
    public abstract int getResponseStatus(T t);

    /**
     * 返回Api参数
     * @param t
     * @return 返回api参数
     */
    public abstract Map<String, ?> getApiParam(T t);

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
            jsonObjectService = JSON.parseObject(serviceResult);
            jsonObjectService.put(GATEWAY_CODE_NAME, ISP_BIZ_ERROR.getCode());
            jsonObjectService.put(GATEWAY_MSG_NAME, ISP_BIZ_ERROR.getError().getMsg());
        } else {
            // 微服务端有可能返回500错误
            // {"path":"/book/getBook3","error":"Internal Server Error","message":"id不能为空","timestamp":"2019-02-13T07:41:00.495+0000","status":500}
            jsonObjectService = new JSONObject();
            jsonObjectService.put(GATEWAY_CODE_NAME, ISP_UNKNOW_ERROR_META.getCode());
            jsonObjectService.put(GATEWAY_MSG_NAME, ISP_UNKNOW_ERROR_META.getError().getMsg());
        }
        return this.merge(request, jsonObjectService);
    }

    /**
     * 该路由是否合并结果
     * @param request
     * @return true：需要合并
     */
    protected boolean isMergeResult(T request) {
        // 默认全局设置
        Boolean defaultSetting = ApiContext.getApiConfig().getMergeResult();
        if (defaultSetting != null) {
            return defaultSetting;
        }
        Map<String, ?> params = this.getApiParam(request);
        if (params == null) {
            return true;
        }
        Object name = params.get(ParamNames.API_NAME);
        Object version = params.get(ParamNames.VERSION_NAME);
        if(name == null) {
            // 随便生成一个name
            name = System.currentTimeMillis();
        }
        TargetRoute targetRoute = RouteRepositoryContext.getRouteRepository().get(String.valueOf(name) + version);
        if (targetRoute == null) {
            return true;
        } else {
            int mergeResult = targetRoute.getRouteDefinition().getMergeResult();
            return BooleanUtils.toBoolean(mergeResult);
        }
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
        String sign = "";
        Map<String, ?> params = this.getApiParam(exchange);
        if (params != null) {
            Object method = params.get(ParamNames.API_NAME);
            if (method != null) {
                name = String.valueOf(method);
            }
            Object clientSign = params.get(ParamNames.SIGN_NAME);
            if (clientSign != null) {
                sign = String.valueOf(clientSign);
            }
        }

        // 点换成下划线
        String method = name.replace(DOT, UNDERLINE);
        ret.put(method + DATA_SUFFIX, jsonObjectService);
        ret.put(ParamNames.SIGN_NAME, sign);
        return ret.toJSONString();
    }

}
