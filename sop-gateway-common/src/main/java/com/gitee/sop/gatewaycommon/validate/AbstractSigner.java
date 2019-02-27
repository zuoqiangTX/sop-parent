package com.gitee.sop.gatewaycommon.validate;

import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tanghc
 */
@Slf4j
public abstract class AbstractSigner implements Signer {

    /**
     * 构建服务端签名串
     *
     * @param params
     * @param secret
     * @return
     */
    protected abstract String buildServerSign(ApiParam params, String secret);

    @Override
    public boolean checkSign(HttpServletRequest request, String secret) {
        ApiParam apiParam = ApiContext.getApiParam();
        String clientSign = apiParam.fetchSignAndRemove();
        if (StringUtils.isBlank(clientSign)) {
            throw ErrorEnum.ISV_MISSING_SIGNATURE.getErrorMeta().getException();
        }
        String serverSign = buildServerSign(apiParam, secret);
        return clientSign.equals(serverSign);
    }
}
