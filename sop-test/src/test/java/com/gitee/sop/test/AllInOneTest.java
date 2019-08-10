package com.gitee.sop.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.sop.test.alipay.AlipayApiException;
import com.gitee.sop.test.alipay.AlipaySignature;
import org.junit.Assert;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 涵盖所有测试情况，发版前运行这个类，确保功能没有问题。
 *
 * @author tanghc
 */
public class AllInOneTest extends TestBase {

    static String url = "http://localhost:8081/api";
    static String appId = "2019032617262200001";
    static String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCXJv1pQFqWNA/++OYEV7WYXwexZK/J8LY1OWlP9X0T6wHFOvxNKRvMkJ5544SbgsJpVcvRDPrcxmhPbi/sAhdO4x2PiPKIz9Yni2OtYCCeaiE056B+e1O2jXoLeXbfi9fPivJZkxH/tb4xfLkH3bA8ZAQnQsoXA0SguykMRZntF0TndUfvDrLqwhlR8r5iRdZLB6F8o8qXH6UPDfNEnf/K8wX5T4EB1b8x8QJ7Ua4GcIUqeUxGHdQpzNbJdaQvoi06lgccmL+PHzminkFYON7alj1CjDN833j7QMHdPtS9l7B67fOU/p2LAAkPMtoVBfxQt9aFj7B8rEhGCz02iJIBAgMBAAECggEARqOuIpY0v6WtJBfmR3lGIOOokLrhfJrGTLF8CiZMQha+SRJ7/wOLPlsH9SbjPlopyViTXCuYwbzn2tdABigkBHYXxpDV6CJZjzmRZ+FY3S/0POlTFElGojYUJ3CooWiVfyUMhdg5vSuOq0oCny53woFrf32zPHYGiKdvU5Djku1onbDU0Lw8w+5tguuEZ76kZ/lUcccGy5978FFmYpzY/65RHCpvLiLqYyWTtaNT1aQ/9pw4jX9HO9NfdJ9gYFK8r/2f36ZE4hxluAfeOXQfRC/WhPmiw/ReUhxPznG/WgKaa/OaRtAx3inbQ+JuCND7uuKeRe4osP2jLPHPP6AUwQKBgQDUNu3BkLoKaimjGOjCTAwtp71g1oo+k5/uEInAo7lyEwpV0EuUMwLA/HCqUgR4K9pyYV+Oyb8d6f0+Hz0BMD92I2pqlXrD7xV2WzDvyXM3s63NvorRooKcyfd9i6ccMjAyTR2qfLkxv0hlbBbsPHz4BbU63xhTJp3Ghi0/ey/1HQKBgQC2VsgqC6ykfSidZUNLmQZe3J0p/Qf9VLkfrQ+xaHapOs6AzDU2H2osuysqXTLJHsGfrwVaTs00ER2z8ljTJPBUtNtOLrwNRlvgdnzyVAKHfOgDBGwJgiwpeE9voB1oAV/mXqSaUWNnuwlOIhvQEBwekqNyWvhLqC7nCAIhj3yvNQKBgQCqYbeec56LAhWP903Zwcj9VvG7sESqXUhIkUqoOkuIBTWFFIm54QLTA1tJxDQGb98heoCIWf5x/A3xNI98RsqNBX5JON6qNWjb7/dobitti3t99v/ptDp9u8JTMC7penoryLKK0Ty3bkan95Kn9SC42YxaSghzqkt+uvfVQgiNGQKBgGxU6P2aDAt6VNwWosHSe+d2WWXt8IZBhO9d6dn0f7ORvcjmCqNKTNGgrkewMZEuVcliueJquR47IROdY8qmwqcBAN7Vg2K7r7CPlTKAWTRYMJxCT1Hi5gwJb+CZF3+IeYqsJk2NF2s0w5WJTE70k1BSvQsfIzAIDz2yE1oPHvwVAoGAA6e+xQkVH4fMEph55RJIZ5goI4Y76BSvt2N5OKZKd4HtaV+eIhM3SDsVYRLIm9ZquJHMiZQGyUGnsvrKL6AAVNK7eQZCRDk9KQz+0GKOGqku0nOZjUbAu6A2/vtXAaAuFSFx1rUQVVjFulLexkXR3KcztL1Qu2k5pB6Si0K/uwQ=";

    Client client = new Client()
            .url(url)
            .appId(appId)
            .privateKey(privateKey)
            .callback(AllInOneTest::assertResult);

    /**
     * 校验基本功能
     *
     * @throws AlipayApiException
     */
    public void testBase() {
        client.request(
                "alipay.story.get"
                , "1.0"
                , new BizContent().add("id", "1").add("name", "葫芦娃")
                , HttpTool.HTTPMethod.GET
        );

        client.request(
                "alipay.story.get"
                , "1.0"
                , new BizContent().add("id", "1").add("name", "葫芦娃")
                , HttpTool.HTTPMethod.POST
        );

        client.request(
                "alipay.story.get"
                , "1.0"
                , new BizContent().add("id", "1").add("name", "葫芦娃")
                , HttpTool.HTTPMethod.JSON
        );
    }

    /**
     * 测试feign。gateway -> book-service(consumer) -> story-service(provider)
     */
    public void testFeign() {
        client.request(
                "alipay.book.story.get"
                , "1.0"
                , new BizContent()
                , HttpTool.HTTPMethod.GET
        );
    }

    /**
     * 测试dubbo服务，book会调用story提供的服务。参见：DemoConsumerController.java
     */
    public void testDubbo() {
        client.request(
                "dubbo.story.get"
                , "1.0"
                , new BizContent().add("id", "222")
                , HttpTool.HTTPMethod.GET
        );
    }

    /**
     * 忽略验证,不校验签名，只需传接口名、版本号、业务参数
     */
    public void testIgnoreSign() {
        new Client()
                .url(url)
                .appId(appId)
                .privateKey(privateKey)
                .ignoreSign(true)
                .request(
                        "story.get"
                        , "2.1"
                        , new BizContent().add("id", "222").add("name", "忽略222")
                        , HttpTool.HTTPMethod.GET);
    }

    /**
     * JSR-303参数校验
     */
    public void testJSR303() {
        client.request(
                "goods.add"
                , "1.0"
                , new BizContent().add("goods_name", "iphone6").add("goods_remark", "iphone6").add("goods_comment", "1")
                , HttpTool.HTTPMethod.POST
        );

    }

    /**
     * 测试是否有权限访问，可在sop-admin中设置权限
     */
    public void testPermission() {
        client.request("permission.story.get"
                , "1.0"
                , new BizContent()
                , HttpTool.HTTPMethod.GET
        );
    }

    /**
     * 演示将接口名版本号跟在url后面，规则:http://host:port/{method}/{version}/
     */
    public void testRestful() {
        new Client()
                .url("http://localhost:8081/alipay.story.get/1.0/")
                .appId(appId)
                .privateKey(privateKey)
                .callback(((method, responseData) -> {
                    assertResult("alipay.story.get", responseData);
                }))
                .request(new BizContent().add("name", "name111"), HttpTool.HTTPMethod.GET);
    }

    /**
     * 限流测试，根据路由id限流
     *
     * @throws InterruptedException
     */
    public void testLimit() throws InterruptedException {
        int threadsCount = 10; // threadsCount个线程同时提交
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CountDownLatch count = new CountDownLatch(threadsCount);
        final AtomicInteger success = new AtomicInteger();
        for (int i = 0; i < threadsCount; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        countDownLatch.await(); // 等在这里，执行countDownLatch.countDown();集体触发
                        // 业务方法
                        client.request(
                                "alipay.story.get"
                                , "1.0"
                                , new BizContent().add("id", "1").add("name", "葫芦娃")
                                , HttpTool.HTTPMethod.GET
                        );
                        success.incrementAndGet();
                    } catch (Exception e) {
                    } finally {
                        count.countDown();
                    }
                }
            }).start();
        }
        countDownLatch.countDown();
        count.await();
        System.out.println("成功次数：" + success);
    }

    static class Client {
        private String url;
        private String appId;
        private String privateKey;
        private Callback callback;
        private boolean ignoreSign;

        public Client url(String url) {
            this.url = url;
            return this;
        }

        public Client appId(String appId) {
            this.appId = appId;
            return this;
        }

        public Client privateKey(String privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public Client callback(Callback callback) {
            this.callback = callback;
            return this;
        }

        public Client ignoreSign(boolean ignoreSign) {
            this.ignoreSign = ignoreSign;
            return this;
        }

        public String request(Map<String, String> bizContent, HttpTool.HTTPMethod httpMethod) {
            return request(null, null, bizContent, httpMethod);
        }

        public String request(String method, String version, Map<String, String> bizContent, HttpTool.HTTPMethod httpMethod) {
            // 公共请求参数
            Map<String, String> params = new HashMap<String, String>();
            params.put("app_id", appId);
            if (method != null) {
                params.put("method", method);
            }
            if (version != null) {
                params.put("version", version);
            }
            params.put("format", "json");
            params.put("charset", "utf-8");
            params.put("sign_type", "RSA2");
            params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            // 业务参数
            params.put("biz_content", JSON.toJSONString(bizContent == null ? Collections.emptyMap() : bizContent));

            if (!ignoreSign) {
                String content = AlipaySignature.getSignContent(params);
                String sign = null;
                try {
                    sign = AlipaySignature.rsa256Sign(content, privateKey, "utf-8");
                } catch (AlipayApiException e) {
                    throw new RuntimeException(e);
                }
                params.put("sign", sign);
            }
            String responseData = null;
            try {
                // 发送请求
                responseData = httpTool.request(url, params, null, httpMethod);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (callback != null) {
                callback.callback(method, responseData);
            }
            return responseData;
        }

        interface Callback {
            void callback(String method, String responseData);
        }
    }

    class BizContent extends HashMap<String, String> {
        public BizContent add(String key, String value) {
            this.put(key, value);
            return this;
        }
    }

    public static void assertResult(String method, String responseData) {
        if (method == null) {
            return;
        }
        System.out.println(responseData);
        String node = method.replace('.', '_') + "_response";
        JSONObject jsonObject = JSON.parseObject(responseData).getJSONObject(node);
        String code = Optional.ofNullable(jsonObject).map(json -> json.getString("code")).orElse("20000");
        Assert.assertEquals("10000", code);
    }

}
