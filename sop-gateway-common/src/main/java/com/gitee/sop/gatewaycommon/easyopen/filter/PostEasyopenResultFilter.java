package com.gitee.sop.gatewaycommon.easyopen.filter;

import com.gitee.sop.gatewaycommon.filter.PostResultFilter;

/**
 * 合并微服务结果，统一返回格式
 *
 * @author tanghc
 */
public class PostEasyopenResultFilter extends PostResultFilter {
    private static final String EASYOPEN_SUCCESS_CODE = "0";
//
//    protected Result processServiceResult(InputStream responseDataStream, ResultBuilder resultBuilder) throws IOException {
//        String responseBody = IOUtils.toString(responseDataStream, SopConstants.CHARSET_UTF8);
//        ApiResult result = this.parseApiResult(responseBody);
//        Result finalResult;
//        if (EASYOPEN_SUCCESS_CODE.equals(result.getCode())) {
//            finalResult = resultBuilder.buildSuccessResult(GATEWAY_SUCCESS_CODE, null, result.getData());
//        } else {
//            // 业务出错
//            finalResult = resultBuilder.buildServiceError(result.getCode(), result.getMsg());
//        }
//        return finalResult;
//    }
//
//    protected ApiResult parseApiResult(String responseBody) {
//        ApiParam apiParam = ApiContext.getApiParam();
//        String format = apiParam.fetchFormat();
//        return SopConstants.FORMAT_XML.equalsIgnoreCase(format)
//                ? XmlUtil.unserialize(responseBody, ApiResult.class)
//                : JSON.parseObject(responseBody, ApiResult.class);
//    }
//
//    protected ResultSerializer buildResultSerializer(HttpServletRequest request, ApiConfig apiConfig) {
//        ApiParam apiParam = ApiContext.getApiParam();
//        String format = apiParam.fetchFormat();
//        if (SopConstants.FORMAT_JSON.equalsIgnoreCase(format)) {
//            return apiConfig.getJsonResultSerializer();
//        } else if (SopConstants.FORMAT_XML.equalsIgnoreCase(format)) {
//            // xml格式输出
//            return apiConfig.getXmlResultSerializer();
//        } else {
//            throw ErrorEnum.isv_invalid_format.getErrorMeta().getException();
//        }
//    }
}
