package com.gitee.sop.gateway.loadbalancer;

import com.gitee.sop.gateway.manager.UserKeyManager;
import com.gitee.sop.gatewaycommon.bean.SpringContext;
import com.gitee.sop.gatewaycommon.param.ApiParam;
import com.gitee.sop.gatewaycommon.param.Param;
import com.gitee.sop.gatewaycommon.param.ParamNames;
import com.gitee.sop.gatewaycommon.zuul.ZuulContext;
import com.gitee.sop.gatewaycommon.zuul.loadbalancer.BaseServerChooser;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 预发布、灰度环境选择，参考自：https://segmentfault.com/a/1190000017412946
 *
 * @author tanghc
 */
public class EnvironmentServerChooser extends BaseServerChooser {

    private static final String MEDATA_KEY_ENV = "env";
    private static final String ENV_PRE_VALUE = "pre";
    private static final String ENV_GRAY_VALUE = "gray";
    /**
     * 预发布机器域名
     */
    private static final String PRE_DOMAIN = "localhost";

    @Override
    protected boolean match(Server server) {
        // eureka存储的metadata
        Map<String, String> metadata = ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata();
        String env = metadata.get(MEDATA_KEY_ENV);
        return StringUtils.isNotBlank(env);
    }

    /**
     * 这里判断客户端能否访问，可以根据ip地址，域名，header内容来决定是否可以访问预发布环境
     *
     * @param server  服务器实例
     * @param request request
     * @return
     */
    @Override
    protected boolean canVisit(Server server, HttpServletRequest request) {
        // eureka存储的metadata
        Map<String, String> metadata = ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata();
        String env = metadata.get(MEDATA_KEY_ENV);
        boolean canVisit;
        switch (env) {
            case ENV_PRE_VALUE:
                canVisit = canVisitPre(server, request);
                break;
            case ENV_GRAY_VALUE:
                canVisit = canVisitGray(server, request);
                break;
            default:
                canVisit = false;
        }
        return canVisit;
    }

    /**
     * 通过判断hostname来确定是否是预发布请求，可修改此方法实现自己想要的
     *
     * @param request request
     * @return 返回true：可以进入到预发环境
     */
    protected boolean canVisitPre(Server server, HttpServletRequest request) {
        String serverName = request.getServerName();
        return PRE_DOMAIN.equals(serverName);
    }

    /**
     * 能否进入灰度环境，可修改此方法实现自己想要的
     *
     * @param request request
     * @return 返回true：可以进入到预发环境
     */
    protected boolean canVisitGray(Server server, HttpServletRequest request) {
        ApiParam apiParam = ZuulContext.getApiParam();
        UserKeyManager userKeyManager = SpringContext.getBean(UserKeyManager.class);
        boolean canVisit = false;
        if (this.isGrayUser(apiParam, userKeyManager, server, request)) {
            // 指定灰度版本号
            String instanceId = server.getId();
            String newVersion = userKeyManager.getVersion(instanceId, apiParam.fetchNameVersion());
            if (newVersion != null) {
                RequestContext.getCurrentContext().getZuulRequestHeaders().put(ParamNames.HEADER_VERSION_NAME, newVersion);
                canVisit = true;
            }
        }
        return canVisit;
    }


    /**
     * 是否是灰度用户
     *
     * @param param          接口参数
     * @param userKeyManager userKey管理
     * @param server   服务器实例
     * @param request        request
     * @return true：是
     */
    protected boolean isGrayUser(Param param, UserKeyManager userKeyManager, Server server, HttpServletRequest request) {
        String instanceId = server.getId();
        // 这里的灰度用户为appKey，包含此appKey则为灰度用户，允许访问
        String appKey = param.fetchAppKey();
        return userKeyManager.containsKey(instanceId, appKey);
    }
}
