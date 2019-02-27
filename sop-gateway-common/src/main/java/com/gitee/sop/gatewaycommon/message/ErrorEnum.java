package com.gitee.sop.gatewaycommon.message;

/**
 * @author tanghc
 */
public enum ErrorEnum {
    SUCCESS("10000", ""),

    ISP_UNKNOW_ERROR("20000", "isp.unknow-error"),
    AOP_UNKNOW_ERROR("20000", "aop.unknow-error"),

    AOP_INVALID_AUTH_TOKEN("20001", "aop.invalid-auth-token"),
    AOP_INVALID_AUTH_OKEN("20001", "aop.invalid-auth-token"),
    AOP_AUTH_TOKEN_TIME_OUT("20001", "aop.auth-token-time-out"),
    AOP_INVALID_APP_AUTH_TOKEN("20001", "aop.invalid-app-auth-token"),
    AOP_INVALID_APP_AUTH_TOKEN_NO_API("20001", "aop.invalid-app-auth-token-no-api"),
    AOP_APP_AUTH_TOKEN_TIME_OUT("20001", "aop.app-auth-token-time-out"),
    AOP_NO_PRODUCT_REG_BY_PARTNER("20001", "aop.no-product-reg-by-partner"),

    ISV_MISSING_METHOD("40001", "isv.missing-method"),
    ISV_MISSING_SIGNATURE("40001", "isv.missing-signature"),
    ISV_MISSING_SIGNATURE_TYPE("40001", "isv.missing-signature-type"),
    ISV_MISSING_SIGNATURE_KEY("40001", "isv.missing-signature-key"),
    ISV_MISSING_APP_ID("40001", "isv.missing-app-id"),
    ISV_MISSING_TIMESTAMP("40001", "isv.missing-timestamp"),
    ISV_MISSING_VERSION("40001", "isv.missing-version"),
    ISV_DECRYPTION_ERROR_MISSING_ENCRYPT_TYPE("40001", "isv.decryption-error-missing-encrypt-type"),

    ISV_INVALID_PARAMETER("40002", "isv.invalid-parameter"),
    ISV_UPLOAD_FAIL("40002", "isv.upload-fail"),
    ISV_INVALID_FILE_EXTENSION("40002", "isv.invalid-file-extension"),
    ISV_INVALID_FILE_SIZE("40002", "isv.invalid-file-size"),
    ISV_INVALID_METHOD("40002", "isv.invalid-method"),
    ISV_INVALID_FORMAT("40002", "isv.invalid-format"),
    ISV_INVALID_SIGNATURE_TYPE("40002", "isv.invalid-signature-type"),
    ISV_INVALID_SIGNATURE("40002", "isv.invalid-signature"),
    ISV_INVALID_ENCRYPT_TYPE("40002", "isv.invalid-encrypt-type"),
    ISV_INVALID_ENCRYPT("40002", "isv.invalid-encrypt"),
    ISV_INVALID_APP_ID("40002", "isv.invalid-app-id"),
    ISV_INVALID_TIMESTAMP("40002", "isv.invalid-timestamp"),
    ISV_INVALID_CHARSET("40002", "isv.invalid-charset"),
    ISV_INVALID_DIGEST("40002", "isv.invalid-digest"),
    ISV_DECRYPTION_ERROR_NOT_VALID_ENCRYPT_TYPE("40002", "isv.decryption-error-not-valid-encrypt-type"),
    ISV_DECRYPTION_ERROR_NOT_VALID_ENCRYPT_KEY("40002", "isv.decryption-error-not-valid-encrypt-key"),
    ISV_DECRYPTION_ERROR_UNKNOWN("40002", "isv.decryption-error-unknown"),
    ISV_MISSING_SIGNATURE_CONFIG("40002", "isv.missing-signature-config"),
    ISV_NOT_SUPPORT_APP_AUTH("40002", "isv.not-support-app-auth"),
    ISV_SUSPECTED_ATTACK("40002", "isv.suspected-attack"),
    ISV_INVALID_CONTENT_TYPE("40002", "isv.invalid-content-type"),

    BIZ_ERROR("40004", ""),

    ISV_INSUFFICIENT_ISV_PERMISSIONS("40006", "isv.insufficient-isv-permissions"),
    ISV_INSUFFICIENT_USER_PERMISSIONS("40006", "isv.insufficient-user-permissions"),

    ;
    private ErrorMeta errorMeta;

    ErrorEnum(String code, String sub_code) {
        this.errorMeta = new ErrorMeta("open.error_", code, sub_code);
    }

    public ErrorMeta getErrorMeta() {
        return errorMeta;
    }
}
