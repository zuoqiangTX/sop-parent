package com.gitee.sop.gatewaycommon.manager;

import com.gitee.sop.gatewaycommon.bean.BeanInitializer;

/**
 * @author tanghc
 */
public interface IPBlacklistManager extends BeanInitializer {

    void add(String ip);

    void remove(String ip);

    boolean contains(String ip);

}
