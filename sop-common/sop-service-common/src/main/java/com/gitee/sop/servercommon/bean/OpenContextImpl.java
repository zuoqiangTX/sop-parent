package com.gitee.sop.servercommon.bean;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

import static com.gitee.sop.servercommon.bean.ParamNames.API_NAME;
import static com.gitee.sop.servercommon.bean.ParamNames.APP_KEY_NAME;
import static com.gitee.sop.servercommon.bean.ParamNames.BIZ_CONTENT_NAME;
import static com.gitee.sop.servercommon.bean.ParamNames.CHARSET_NAME;
import static com.gitee.sop.servercommon.bean.ParamNames.FORMAT_NAME;
import static com.gitee.sop.servercommon.bean.ParamNames.SIGN_TYPE_NAME;
import static com.gitee.sop.servercommon.bean.ParamNames.TIMESTAMP_NAME;
import static com.gitee.sop.servercommon.bean.ParamNames.VERSION_NAME;

/**
 * @author tanghc
 */
public class OpenContextImpl<T> implements OpenContext<T> {
    private JSONObject jsonObject;
    private T bizObject;

    public OpenContextImpl(JSONObject jsonObject, Class<?> bizClass) {
        this.jsonObject = jsonObject;
        JSONObject bizJsonObj = this.jsonObject.getJSONObject(BIZ_CONTENT_NAME);
        bizObject = (bizClass == null || bizJsonObj == null) ? null : (T) bizJsonObj.toJavaObject(bizClass);
    }

    @Override
    public String getAppId() {
        return jsonObject.getString(APP_KEY_NAME);
    }

    @Override
    public T getBizObject() {
        return bizObject;
    }

    @Override
    public String getBizContent() {
        return jsonObject.getString(BIZ_CONTENT_NAME);
    }

    @Override
    public String getCharset() {
        return jsonObject.getString(CHARSET_NAME);
    }

    @Override
    public String getMethod() {
        return jsonObject.getString(API_NAME);
    }

    @Override
    public String getVersion() {
        return jsonObject.getString(VERSION_NAME);
    }

    @Override
    public String getFormat() {
        return jsonObject.getString(FORMAT_NAME);
    }

    @Override
    public String getSignType() {
        return jsonObject.getString(SIGN_TYPE_NAME);
    }

    @Override
    public Date getTimestamp() {
        return jsonObject.getDate(TIMESTAMP_NAME);
    }
}
