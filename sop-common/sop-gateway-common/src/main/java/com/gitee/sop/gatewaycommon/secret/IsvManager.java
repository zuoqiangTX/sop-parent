package com.gitee.sop.gatewaycommon.secret;

import com.gitee.sop.gatewaycommon.bean.Isv;

import java.util.function.Function;

/**
 * Isv管理
 * @author tanghc
 */
public interface IsvManager<T extends Isv> {
    /**
     * 加载isv信息
     * @param secretGetter secret获取
     */
    void load(Function<Object, String> secretGetter);

    void update(T t);

    void remove(String appKey);

    T getIsv(String appKey);

}
