package com.gitee.sop.gatewaycommon.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.exception.ApiException;
import com.gitee.sop.gatewaycommon.message.Error;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import com.gitee.sop.gatewaycommon.message.ErrorMeta;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.param.ParamNames;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * @author tanghc
 */
@Slf4j
public class ApiResultExecutor implements ResultExecutor {

    private static final ErrorMeta SUCCESS_META = ErrorEnum.SUCCESS.getErrorMeta();
    private static final ErrorMeta ISP_UNKNOW_ERROR_META = ErrorEnum.ISP_UNKNOW_ERROR.getErrorMeta();
    private static final ErrorMeta ISP_BIZ_ERROR = ErrorEnum.BIZ_ERROR.getErrorMeta();


    public static final int BIZ_ERROR_STATUS = 4000;
    private static final char DOT = '.';
    private static final char UNDERLINE = '_';
    public static final String GATEWAY_CODE_NAME = "code";
    public static final String GATEWAY_MSG_NAME = "msg";
    public static final String DATA_SUFFIX = "_response";

    @Override
    public String mergeResult(int responseStatus, String responseData) {
        if (responseStatus == HttpStatus.OK.value() || responseStatus == BIZ_ERROR_STATUS) {
            return mergeSuccess(responseStatus, responseData);
        } else {
            // 微服务端有可能返回500错误
            // {"path":"/book/getBook3","error":"Internal Server Error","message":"id不能为空","timestamp":"2019-02-13T07:41:00.495+0000","status":500}
            return mergeError(responseData);
        }
    }

    @Override
    public String mergeError(Throwable throwable) {
        Error error = null;
        if (throwable instanceof ZuulException) {
            ZuulException ex = (ZuulException) throwable;
            Throwable cause = ex.getCause();
            if (cause instanceof ApiException) {
                ApiException apiException = (ApiException) cause;
                error = apiException.getError();
            }
        }
        if (error == null) {
            error = ErrorEnum.AOP_UNKNOW_ERROR.getErrorMeta().getError();
        }
        JSONObject jsonObject = (JSONObject)JSON.toJSON(error);
        return this.merge(jsonObject);
    }

    /*
        成功示例
        {
            "alipay_trade_fastpay_refund_query_response": {
                "code": "10000",
                "msg": "Success",
                "trade_no": "2014112611001004680073956707",
                "out_trade_no": "20150320010101001",
                "out_request_no": "20150320010101001",
                "refund_reason": "用户退款请求",
                "total_amount": 100.2,
                "refund_amount": 12.33
            },
            "sign": "ERITJKEIJKJHKKKKKKKHJEREEEEEEEEEEE"
        }
         */
    public String mergeSuccess(int responseStatus, String serviceResult) {
        JSONObject jsonObjectService;
        // 如果是业务出错
        if (responseStatus == BIZ_ERROR_STATUS) {
            jsonObjectService = JSON.parseObject(serviceResult);
            jsonObjectService.put(GATEWAY_CODE_NAME, ISP_BIZ_ERROR.getCode());
            jsonObjectService.put(GATEWAY_MSG_NAME, ISP_BIZ_ERROR.getError().getMsg());
        } else {
            // 200正常返回
            jsonObjectService = JSON.parseObject(serviceResult);
            jsonObjectService.put(GATEWAY_CODE_NAME, SUCCESS_META.getCode());
            jsonObjectService.put(GATEWAY_MSG_NAME, SUCCESS_META.getError().getMsg());
        }
        return this.merge(jsonObjectService);
    }

    /*
    异常示例
    {
        "alipay_trade_fastpay_refund_query_response": {
            "code": "20000",
            "msg": "Service Currently Unavailable",
            "sub_code": "isp.unknow-error",
            "sub_msg": "系统繁忙"
        },
        "sign": "ERITJKEIJKJHKKKKKKKHJEREEEEEEEEEEE"
    }
     */
    public String mergeError(String serviceResult) {
        JSONObject jsonObjectService = new JSONObject();
        jsonObjectService.put(GATEWAY_CODE_NAME, ISP_UNKNOW_ERROR_META.getCode());
        jsonObjectService.put(GATEWAY_MSG_NAME, ISP_UNKNOW_ERROR_META.getError().getMsg());
        return this.merge(jsonObjectService);
    }

    private String merge(JSONObject jsonObjectService) {
        JSONObject ret = new JSONObject();
        ApiParam apiParam = ApiContext.getApiParam();
        // 点换成下划线
        String apiName = apiParam.fetchName().replace(DOT, UNDERLINE);
        ret.put(apiName + DATA_SUFFIX, jsonObjectService);
        ret.put(ParamNames.SIGN_NAME, apiParam.fetchSign());
        return ret.toJSONString();
    }


}
