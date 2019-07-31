package com.gitee.sop.gatewaycommon.zuul.loadbalancer;

import com.google.common.base.Optional;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务实例选择器
 *
 * @author tanghc
 */
@Slf4j
public abstract class BaseServerChooser extends ZoneAvoidanceRule {

    /**
     * 是否匹配对应的服务器，可在此判断是否是预发布，灰度环境
     *
     * @param server 指定服务器
     * @return 返回true：是
     */
    protected abstract boolean match(Server server);

    /**
     * 客户端能否够访问服务器
     *
     * @param server  服务器实例
     * @param request request
     * @return 返回true：能访问
     */
    protected abstract boolean canVisit(Server server, HttpServletRequest request);

    @Override
    public Server choose(Object key) {
        ILoadBalancer lb = getLoadBalancer();
        // 获取服务实例列表
        List<Server> allServers = new ArrayList<>(lb.getAllServers());
        int index = -1;
        for (int i = 0; i < allServers.size(); i++) {
            Server server = allServers.get(i);
            if (match(server)) {
                index = i;
                if (canVisit(server, RequestContext.getCurrentContext().getRequest())) {
                    return server;
                }
            }
        }
        // 调用默认的算法
        // 如果选出了特殊环境服务器，需要移除命中的服务器
        if (index > -1) {
            allServers.remove(index);
        }
        if (CollectionUtils.isEmpty(allServers)) {
            log.error("无可用服务实例，key:", key);
            return null;
        }
        Optional<Server> server = getPredicate().chooseRoundRobinAfterFiltering(allServers, key);
        if (server.isPresent()) {
            return server.get();
        } else {
            return null;
        }
    }

}
