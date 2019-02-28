package com.gitee.sop.gatewaycommon.validate;

import com.gitee.sop.gatewaycommon.bean.ApiConfig;
import com.gitee.sop.gatewaycommon.bean.ApiContext;
import com.gitee.sop.gatewaycommon.message.ErrorEnum;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.param.ParamNames;
import com.gitee.sop.gatewaycommon.param.UploadContext;
import com.gitee.sop.gatewaycommon.secret.AppSecretManager;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 负责校验,校验工作都在这里
 *
 * @author tanghc
 */
public class ApiValidator implements Validator {
    private static final Logger logger = LoggerFactory.getLogger(ApiValidator.class);

    private static final int MILLISECOND_OF_ONE_SECOND = 1000;


    private static List<String> FORMAT_LIST = Arrays.asList("json", "xml");


    @Override
    public void validate(ApiParam param) {
        ApiConfig apiConfig = ApiContext.getApiConfig();
        if (apiConfig.isIgnoreValidate() || param.fetchIgnoreValidate()) {
            logger.debug("忽略所有验证(ignoreValidate=true), name:{}, version:{}", param.fetchName(), param.fetchVersion());
            return;
        }
        if (param.fetchIgnoreSign()) {
            logger.debug("忽略签名验证, name:{}, version:{}", param.fetchName(), param.fetchVersion());
        } else {
            // 需要验证签名
            checkAppKey(param);
            checkSign(param);
        }
        checkUploadFile(param);
        checkTimeout(param);
        checkFormat(param);
    }

    /**
     * 校验上传文件内容
     *
     * @param param
     */
    protected void checkUploadFile(ApiParam param) {
        UploadContext uploadContext = ApiContext.getUploadContext();
        if (uploadContext != null) {
            try {
                List<MultipartFile> files = uploadContext.getAllFile();
                for (MultipartFile file : files) {
                    // 客户端传来的文件md5
                    String clientMd5 = param.getString(file.getName());
                    if (clientMd5 != null) {
                        String fileMd5 = DigestUtils.md5Hex(file.getBytes());
                        if (!clientMd5.equals(fileMd5)) {
                            throw ErrorEnum.ISV_UPLOAD_FAIL.getErrorMeta().getException();
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("验证上传文件MD5错误", e);
                throw ErrorEnum.ISV_UPLOAD_FAIL.getErrorMeta().getException();
            }
        }

    }

    protected void checkTimeout(ApiParam param) {
        int timeoutSeconds = ApiContext.getApiConfig().getTimeoutSeconds();
        // 如果设置为0，表示不校验
        if (timeoutSeconds == 0) {
            return;
        }
        if (timeoutSeconds < 0) {
            throw new IllegalArgumentException("服务端timeoutSeconds设置错误");
        }
        String requestTime = param.fetchTimestamp();
        try {
            Date requestDate = new SimpleDateFormat(ParamNames.TIMESTAMP_PATTERN).parse(requestTime);
            long requestMilliseconds = requestDate.getTime();
            if (System.currentTimeMillis() - requestMilliseconds > timeoutSeconds * MILLISECOND_OF_ONE_SECOND) {
                throw ErrorEnum.ISV_INVALID_TIMESTAMP.getErrorMeta().getException();
            }
        } catch (ParseException e) {
            throw ErrorEnum.ISV_INVALID_TIMESTAMP.getErrorMeta().getException(param.fetchNameVersion());
        }
    }

    protected void checkAppKey(ApiParam param) {
        if (StringUtils.isEmpty(param.fetchAppKey())) {
            throw ErrorEnum.ISV_MISSING_APP_ID.getErrorMeta().getException();
        }
        AppSecretManager appSecretManager = ApiContext.getApiConfig().getAppSecretManager();
        Assert.notNull(appSecretManager, "appSecretManager未初始化");
        boolean isTrueAppKey = appSecretManager.isValidAppKey(param.fetchAppKey());
        if (!isTrueAppKey) {
            throw ErrorEnum.ISV_INVALID_APP_ID.getErrorMeta().getException();
        }
    }

    protected void checkSign(ApiParam param) {
        String clientSign = param.fetchSign();
        try {
            if (StringUtils.isEmpty(clientSign)) {
                throw ErrorEnum.ISV_MISSING_SIGNATURE.getErrorMeta().getException(param.fetchNameVersion(), ParamNames.SIGN_NAME);
            }
            ApiConfig apiConfig = ApiContext.getApiConfig();
            AppSecretManager appSecretManager = apiConfig.getAppSecretManager();
            // 根据appId获取秘钥
            String secret = appSecretManager.getSecret(param.fetchAppKey());
            if (StringUtils.isEmpty(secret)) {
                throw ErrorEnum.ISV_MISSING_SIGNATURE_CONFIG.getErrorMeta().getException();
            }
            Signer signer = apiConfig.getSigner();
            boolean isRightSign = signer.checkSign(ApiContext.getRequest(), secret);
            // 错误的sign
            if (!isRightSign) {
                throw ErrorEnum.ISV_INVALID_SIGNATURE.getErrorMeta().getException(param.fetchNameVersion());
            }
        } finally {
            // 校验过程中会移除sign，这里需要重新设置进去
            param.setSign(clientSign);
        }
    }


    protected void checkFormat(ApiParam param) {
        String format = param.fetchFormat();
        boolean contains = FORMAT_LIST.contains(format.toLowerCase());

        if (!contains) {
            throw ErrorEnum.ISV_INVALID_FORMAT.getErrorMeta().getException(param.fetchNameVersion(), format);
        }
    }

}
