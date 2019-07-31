package com.gitee.sop.gateway.loadbalancer;

import com.gitee.sop.gatewaycommon.zuul.loadbalancer.BaseServerChooser;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * 预发布环境选择，参考自：https://segmentfault.com/a/1190000017412946
 *
 * @author tanghc
 */
public class PreEnvironmentServerChooser extends BaseServerChooser {

    private static final String MEDATA_KEY_ENV = "env";
    private static final String ENV_PRE_VALUE = "pre";

    private static final String HOST_NAME = "localhost";

    @Override
    protected boolean match(Server server) {
        // eureka存储的metadata
        Map<String, String> metadata = ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata();
        String env = metadata.get(MEDATA_KEY_ENV);
        return StringUtils.isNotBlank(env);
    }

    /**
     * 这里判断客户端能否访问，可以根据ip地址，域名，header内容来决定是否可以访问预发布环境
     * @param server  服务器实例
     * @param request request
     * @return
     */
    @Override
    protected boolean canVisit(Server server, HttpServletRequest request) {
        // eureka存储的metadata
        Map<String, String> metadata = ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata();
        String env = metadata.get(MEDATA_KEY_ENV);
        return Objects.equals(ENV_PRE_VALUE, env) && canClientVisit(request);
    }

    /**
     * 通过判断hostname来确定是否是预发布请求，如果需要通过其它条件判断，修改此方法
     * @param request request
     * @return 返回true：可以进入到预发环境
     */
    protected boolean canClientVisit(HttpServletRequest request) {
        String serverName = request.getServerName();
        return HOST_NAME.equals(serverName);
    }
}
