package com.gitee.sop.gatewaycommon.secret;

import com.gitee.sop.gatewaycommon.bean.BeanInitializer;
import com.gitee.sop.gatewaycommon.bean.Isv;

/**
 * Isv管理
 * @author tanghc
 */
public interface IsvManager<T extends Isv> extends BeanInitializer {

    void update(T t);

    void remove(String appKey);

    T getIsv(String appKey);

}
