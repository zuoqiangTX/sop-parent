package com.gitee.sop.gatewaycommon.validate;

import javax.servlet.http.HttpServletRequest;

/**
 * 负责签名校验
 * @author tanghc
 *
 */
public interface Signer {

    /**
     * 签名校验
     * @param request
     * @param secret 秘钥
     * @return true签名正确
     */
    boolean checkSign(HttpServletRequest request, String secret);
    
}
