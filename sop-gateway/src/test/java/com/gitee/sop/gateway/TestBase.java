package com.gitee.sop.gateway;

import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
public class TestBase extends TestCase {
    public void post(String url, String postJson) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        // 构造消息头

        post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        // 构建消息实体
        StringEntity entity = new StringEntity(postJson, Charset.forName("UTF-8"));
        entity.setContentEncoding("UTF-8");
        // 发送Json格式的数据请求
        entity.setContentType("application/json");
        post.setEntity(entity);

        HttpResponse response = httpClient.execute(post);
        HttpEntity responseEntity = response.getEntity();
        String content = IOUtils.toString(responseEntity.getContent(), "UTF-8");
        System.out.println(content);
    }

    /**
     * 发送POST请求
     * @param url
     * @return JSON或者字符串
     * @throws Exception
     */
    public static Object post(String url, Map<String, Object> params) throws Exception{
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try{
            /**
             *  创建一个httpclient对象
             */
            client = HttpClients.createDefault();
            /**
             * 创建一个post对象
             */
            HttpPost post = new HttpPost(url);
            List<NameValuePair> nameValuePairs = params.entrySet().stream().map(entry -> {
                return new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));
            }).collect(Collectors.toList());
            /**
             * 包装成一个Entity对象
             */
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            /**
             * 设置请求的内容
             */
            post.setEntity(entity);
            /**
             * 设置请求的报文头部的编码
             */
            post.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));
            /**
             * 执行post请求
             */
            response = client.execute(post);
            /**
             * 通过EntityUitls获取返回内容
             */
            String result = EntityUtils.toString(response.getEntity(),"UTF-8");
            System.out.println(result);
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            IOUtils.closeQuietly(client);
            IOUtils.closeQuietly(response);
        }
        return null;
    }


}
