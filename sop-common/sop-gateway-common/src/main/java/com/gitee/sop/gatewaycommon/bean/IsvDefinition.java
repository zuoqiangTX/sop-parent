package com.gitee.sop.gatewaycommon.bean;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author tanghc
 */
@Getter
@Setter
public class IsvDefinition implements Isv {

    public IsvDefinition() {
    }

    public IsvDefinition(String appKey, String secret) {
        this.appKey = appKey;
        this.secret = secret;
    }

    private Long id;

    private String appKey;

    /** 秘钥，如果是支付宝开放平台，对应的pubKey */
    private String secret;

    private String pubKey;

    /** 0启用，1禁用 */
    private Byte status;

    private Date gmtCreate;

    private Date gmtModified;

    @Override
    public String getSecretInfo() {
        return StringUtils.isBlank(pubKey) ? secret : pubKey;
    }

    @Override
    public String toString() {
        return "IsvDefinition{" +
                "id=" + id +
                ", appKey='" + appKey + '\'' +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                '}';
    }
}
