package com.gitee.sop.adminserver.service;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.adminserver.api.service.param.ServiceSearchParam;
import com.gitee.sop.adminserver.api.service.result.ServiceInstanceVO;
import com.gitee.sop.adminserver.bean.ChannelMsg;
import com.gitee.sop.adminserver.bean.GatewayPushDTO;
import com.gitee.sop.adminserver.bean.HttpTool;
import com.gitee.sop.adminserver.common.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 网关后台配置推送类
 *
 * @author tanghc
 */
@Slf4j
@Service
public class ConfigPushService {

    private static final String GATEWAY_PUSH_URL = "http://%s/configChannelMsg";
    private static final String API_GATEWAY_SERVICE_ID = "sop-gateway";

    private static HttpTool httpTool = new HttpTool();

    @Autowired
    private ServerService serverService;

    @Value("${gateway.host:}")
    private String gatewayHost;

    @Value("${zuul.secret}")
    private String secret;

    public void publishConfig(String dataId, String groupId, ChannelMsg channelMsg) {
        GatewayPushDTO gatewayPushDTO = new GatewayPushDTO(dataId, groupId, channelMsg);
        ServiceSearchParam serviceSearchParam = new ServiceSearchParam();
        serviceSearchParam.setServiceId(API_GATEWAY_SERVICE_ID);
        //遍历网关节点，查询出所有网关实例
        List<ServiceInstanceVO> serviceInstanceList = serverService.listService(serviceSearchParam);
        Collection<String> hostList = serviceInstanceList
                .stream()
                .filter(serviceInstanceVO -> StringUtils.isNotBlank(serviceInstanceVO.getInstanceId()))
                .map(ServiceInstanceVO::getIpPort)
                .collect(Collectors.toList());
        //根据查出来的host列表进行配置推送
        this.pushByHost(hostList, gatewayPushDTO);
    }

    /**
     * 推送修改配置信息给网关下各个实例，进行配置修改（本地缓存的修改）
     *
     * @param hosts
     * @param gatewayPushDTO
     */
    private void pushByHost(Collection<String> hosts, GatewayPushDTO gatewayPushDTO) {
        for (String host : hosts) {
            //构建推送实例url
            String url = String.format(GATEWAY_PUSH_URL, host);
            try {
                //网关请求体构造（json请求体）
                String requestBody = JSON.toJSONString(gatewayPushDTO);
                Map<String, String> header = new HashMap<>(8);
                header.put("sign", buildRequestBodySign(requestBody, secret));
                String resp = httpTool.requestJson(url, requestBody, header);
                if (!"ok".equals(resp)) {
                    throw new IOException(resp);
                }
            } catch (IOException e) {
                log.error("nacos配置失败, dataId={}, groupId={}, operation={}， url={}",
                        gatewayPushDTO.getDataId()
                        , gatewayPushDTO.getGroupId()
                        , gatewayPushDTO.getChannelMsg().getOperation()
                        , url
                        , e);
                throw new BizException("推送配置失败");
            }
        }
    }

    public static String buildRequestBodySign(String requestBody, String secret) {
//        签名加签
        String signContent = secret + requestBody + secret;
        return DigestUtils.md5Hex(signContent);
    }

}
