package com.gitee.sop.websiteserver.controller;

import com.gitee.sop.websiteserver.sign.AlipayApiException;
import com.gitee.sop.websiteserver.sign.AlipaySignature;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 沙箱环境代理类
 * @author tanghc
 */
@RestController
@RequestMapping("sandbox")
public class SandboxController {

    @Value("${api.url-sandbox}")
    private String url;

    @RequestMapping("/test")
    public SandboxResult proxy(
            @RequestParam String appId
            , @RequestParam String privateKey
            , @RequestParam String method
            , @RequestParam String version
            , @RequestParam String bizContent) throws AlipayApiException {

        Assert.isTrue(StringUtils.isNotBlank(appId), "appId不能为空");
        Assert.isTrue(StringUtils.isNotBlank(privateKey), "privateKey不能为空");
        Assert.isTrue(StringUtils.isNotBlank(method), "method不能为空");

        // 公共请求参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("app_id", appId);
        params.put("method", method);
        params.put("format", "json");
        params.put("charset", "utf-8");
        params.put("sign_type", "RSA2");
        params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        params.put("version", version);

        // 业务参数
        params.put("biz_content", bizContent);

        SandboxResult result = new SandboxResult();

        result.params = buildParamQuery(params);

        String content = AlipaySignature.getSignContent(params);
        result.beforeSign = content;

        String sign = AlipaySignature.rsa256Sign(content, privateKey, "utf-8");
        result.sign = sign;

        params.put("sign", sign);

        String responseData = get(url, params);// 发送请求
        result.apiResult = responseData;
        return result;
    }

    @Data
    public static class SandboxResult {
        private String params;
        private String beforeSign;
        private String sign;

        private String apiResult;
    }

    /**
     * 发送get请求
     * @param url
     * @return JSON或者字符串
     * @throws Exception
     */
    public static String get(String url, Map<String, String> params) {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try{
            /**
             *  创建一个httpclient对象
             */
            client = HttpClients.createDefault();

            List<NameValuePair> nameValuePairs = params.entrySet()
                    .stream()
                    .map(entry -> new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())))
                    .collect(Collectors.toList());
            /**
             * 包装成一个Entity对象
             */
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            //参数转换为字符串
            String paramsStr = EntityUtils.toString(entity);
            url = url + "?" + paramsStr;
            /**
             * 创建一个post对象
             */
            HttpGet get = new HttpGet(url);

            /**
             * 执行post请求
             */
            response = client.execute(get);
            /**
             * 通过EntityUitls获取返回内容
             */
            return EntityUtils.toString(response.getEntity(),"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            IOUtils.closeQuietly(client);
            IOUtils.closeQuietly(response);
        }
        return null;
    }

    protected String buildParamQuery(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString().substring(1);
    }
}
