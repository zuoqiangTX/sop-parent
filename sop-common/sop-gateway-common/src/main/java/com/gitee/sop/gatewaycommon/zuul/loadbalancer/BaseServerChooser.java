package com.gitee.sop.gatewaycommon.zuul.loadbalancer;

import com.google.common.base.Optional;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务实例选择器
 *
 * @author tanghc
 */
@Slf4j
public abstract class BaseServerChooser extends ZoneAvoidanceRule {


    /**
     * 是否是预发布服务器
     * @param server 服务器
     * @return true：是
     */
    protected abstract boolean isPreServer(Server server);

    /**
     * 是否是灰度发布服务器
     * @param server 服务器
     * @return true：是
     */
    protected abstract boolean isGrayServer(Server server);

    /**
     * 能否访问预发布
     * @param server 预发布服务器
     * @param request request
     * @return true：能
     */
    protected abstract boolean canVisitPre(Server server, HttpServletRequest request);

    @Override
    public Server choose(Object key) {
        ILoadBalancer lb = getLoadBalancer();
        // 获取服务实例列表
        List<Server> allServers = lb.getAllServers();
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();

        List<Server> preServers = allServers.stream()
                .filter(this::isPreServer)
                .filter(server -> canVisitPre(server, request))
                .collect(Collectors.toList());
//        int index = -1;
//        for (int i = 0; i < allServers.size(); i++) {
//            Server server = allServers.get(i);
//            if (isPreServer(server)) {
//                preServers.add(server);
//            }
//            if (match(server)) {
//                index = i;
//                if (canVisit(server, RequestContext.getCurrentContext().getRequest())) {
//                    return server;
//                }
//            }
//        }

        if (!preServers.isEmpty()) {
            return this.doChoose(preServers, key);
        }

        List<Server> grayServers = allServers.stream()
                .filter(this::isGrayServer)
                // 这句暂时不需要，放到了PreVersionDecisionFilter中判断
                //.filter(server -> canVisitGray(server, request))
                .collect(Collectors.toList());
        if (!grayServers.isEmpty()) {
            return doChoose(grayServers, key);
        }

        return super.choose(key);
        // 调用默认的算法
        // 如果选出了特殊环境服务器，需要移除命中的服务器
//        if (index > -1) {
//            allServers.remove(index);
//        }
//        if (CollectionUtils.isEmpty(allServers)) {
//            log.error("无可用服务实例，key:", key);
//            return null;
//        }

    }

    protected Server doChoose(List<Server> servers, Object key) {
        Optional<Server> server = getPredicate().chooseRoundRobinAfterFiltering(servers, key);
        if (server.isPresent()) {
            return server.get();
        } else {
            return null;
        }
    }

}
