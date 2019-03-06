package com.gitee.sop.gatewaycommon.bean;

import com.gitee.sop.gatewaycommon.param.ApiParamBuilder;
import com.gitee.sop.gatewaycommon.param.ParamBuilder;
import com.gitee.sop.gatewaycommon.result.ApiResultExecutor;
import com.gitee.sop.gatewaycommon.result.ResultExecutor;
import com.gitee.sop.gatewaycommon.secret.AppSecretManager;
import com.gitee.sop.gatewaycommon.secret.CacheAppSecretManager;
import com.gitee.sop.gatewaycommon.session.ApiSessionManager;
import com.gitee.sop.gatewaycommon.session.SessionManager;
import com.gitee.sop.gatewaycommon.validate.ApiEncrypter;
import com.gitee.sop.gatewaycommon.validate.ApiSigner;
import com.gitee.sop.gatewaycommon.validate.ApiValidator;
import com.gitee.sop.gatewaycommon.validate.Encrypter;
import com.gitee.sop.gatewaycommon.validate.Signer;
import com.gitee.sop.gatewaycommon.validate.Validator;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author tanghc
 */
@Data
public class ApiConfig {

    private static ApiConfig instance = new ApiConfig();

    private ApiConfig() {
    }

    /**
     * 合并结果处理
     */
    private ResultExecutor resultExecutor = new ApiResultExecutor();

    /**
     * app秘钥管理
     */
    private AppSecretManager appSecretManager = new CacheAppSecretManager();

    /**
     * 加密工具
     */
    private Encrypter encrypter = new ApiEncrypter();

    /**
     * 签名工具
     */
    private Signer signer = new ApiSigner();

    /**
     * 参数解析，gateway
     */
    private ParamBuilder paramBuilder = new ApiParamBuilder();

    /**
     * 验证
     */
    private Validator validator = new ApiValidator();

    /**
     * session管理
     */
    private SessionManager sessionManager = new ApiSessionManager();

    /**
     * 错误模块
     */
    private List<String> i18nModules = new ArrayList<String>();

    // -------- fields ---------
    /**
     * 忽略验证
     */
    private boolean ignoreValidate;

    /**
     * 超时时间
     */
    private int timeoutSeconds = 60 * 5;

    public void addAppSecret(Map<String, String> appSecretPair) {
        this.appSecretManager.addAppSecret(appSecretPair);
    }

    public static ApiConfig getInstance() {
        return instance;
    }

    public static void setInstance(ApiConfig apiConfig) {
        instance = apiConfig;
    }

}
