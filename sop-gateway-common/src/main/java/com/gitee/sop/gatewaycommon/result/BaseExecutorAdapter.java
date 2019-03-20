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
     * @return
     */
    public abstract int getBizHeaderCode(T t);

    /**
     * 返回Api参数
     * @param t
     * @return
     */
    public abstract Map<String, ?> getApiParam(T t);

    @Override
    public String mergeResult(T request, String serviceResult) {
        boolean isMergeResult = this.isRouteMergeResult(request);
        if (!isMergeResult) {
            return serviceResult;
        }
        serviceResult = wrapResult(serviceResult);
        int responseStatus = this.getBizHeaderCode(request);
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
     * @return
     */
    protected boolean isRouteMergeResult(T request) {
        boolean defaultSetting = ApiContext.getApiConfig().isMergeResult();
        boolean isMergeResult = true;
        Map<String, ?> params = this.getApiParam(request);
        Object name = params.get(ParamNames.API_NAME);
        Object version = params.get(ParamNames.VERSION_NAME);
        if(name == null) {
            name = System.currentTimeMillis();
        }
        TargetRoute targetRoute = RouteRepositoryContext.getRouteRepository().get(String.valueOf(name) + version);
        if (targetRoute == null) {
            return defaultSetting;
        } else {
            isMergeResult = targetRoute.getRouteDefinition().isMergeResult();
        }
        // 如果路由说合并，还得看网关全局设置，网关全局设置优先级最大
        if (isMergeResult) {
            isMergeResult = defaultSetting;
        }
        return isMergeResult;
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
        Map<String, ?> params = this.getApiParam(exchange);
        Object name = params.get(ParamNames.API_NAME);
        if (name == null) {
            name = "error";
        }
        Object sign = params.get(ParamNames.SIGN_NAME);
        if (sign == null) {
            sign = "";
        }
        // 点换成下划线
        String method = String.valueOf(name).replace(DOT, UNDERLINE);
        ret.put(method + DATA_SUFFIX, jsonObjectService);
        ret.put(ParamNames.SIGN_NAME, sign);
        return ret.toJSONString();
    }

}
