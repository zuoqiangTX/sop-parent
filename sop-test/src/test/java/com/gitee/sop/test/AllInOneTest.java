package com.gitee.sop.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 涵盖所有测试情况，发版前运行这个类，确保功能没有问题。
 *
 * @author tanghc
 */
public class AllInOneTest extends TestBase {

    String url = "http://localhost:8081";
    String appId = "2019032617262200001";
    String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCXJv1pQFqWNA/++OYEV7WYXwexZK/J8LY1OWlP9X0T6wHFOvxNKRvMkJ5544SbgsJpVcvRDPrcxmhPbi/sAhdO4x2PiPKIz9Yni2OtYCCeaiE056B+e1O2jXoLeXbfi9fPivJZkxH/tb4xfLkH3bA8ZAQnQsoXA0SguykMRZntF0TndUfvDrLqwhlR8r5iRdZLB6F8o8qXH6UPDfNEnf/K8wX5T4EB1b8x8QJ7Ua4GcIUqeUxGHdQpzNbJdaQvoi06lgccmL+PHzminkFYON7alj1CjDN833j7QMHdPtS9l7B67fOU/p2LAAkPMtoVBfxQt9aFj7B8rEhGCz02iJIBAgMBAAECggEARqOuIpY0v6WtJBfmR3lGIOOokLrhfJrGTLF8CiZMQha+SRJ7/wOLPlsH9SbjPlopyViTXCuYwbzn2tdABigkBHYXxpDV6CJZjzmRZ+FY3S/0POlTFElGojYUJ3CooWiVfyUMhdg5vSuOq0oCny53woFrf32zPHYGiKdvU5Djku1onbDU0Lw8w+5tguuEZ76kZ/lUcccGy5978FFmYpzY/65RHCpvLiLqYyWTtaNT1aQ/9pw4jX9HO9NfdJ9gYFK8r/2f36ZE4hxluAfeOXQfRC/WhPmiw/ReUhxPznG/WgKaa/OaRtAx3inbQ+JuCND7uuKeRe4osP2jLPHPP6AUwQKBgQDUNu3BkLoKaimjGOjCTAwtp71g1oo+k5/uEInAo7lyEwpV0EuUMwLA/HCqUgR4K9pyYV+Oyb8d6f0+Hz0BMD92I2pqlXrD7xV2WzDvyXM3s63NvorRooKcyfd9i6ccMjAyTR2qfLkxv0hlbBbsPHz4BbU63xhTJp3Ghi0/ey/1HQKBgQC2VsgqC6ykfSidZUNLmQZe3J0p/Qf9VLkfrQ+xaHapOs6AzDU2H2osuysqXTLJHsGfrwVaTs00ER2z8ljTJPBUtNtOLrwNRlvgdnzyVAKHfOgDBGwJgiwpeE9voB1oAV/mXqSaUWNnuwlOIhvQEBwekqNyWvhLqC7nCAIhj3yvNQKBgQCqYbeec56LAhWP903Zwcj9VvG7sESqXUhIkUqoOkuIBTWFFIm54QLTA1tJxDQGb98heoCIWf5x/A3xNI98RsqNBX5JON6qNWjb7/dobitti3t99v/ptDp9u8JTMC7penoryLKK0Ty3bkan95Kn9SC42YxaSghzqkt+uvfVQgiNGQKBgGxU6P2aDAt6VNwWosHSe+d2WWXt8IZBhO9d6dn0f7ORvcjmCqNKTNGgrkewMZEuVcliueJquR47IROdY8qmwqcBAN7Vg2K7r7CPlTKAWTRYMJxCT1Hi5gwJb+CZF3+IeYqsJk2NF2s0w5WJTE70k1BSvQsfIzAIDz2yE1oPHvwVAoGAA6e+xQkVH4fMEph55RJIZ5goI4Y76BSvt2N5OKZKd4HtaV+eIhM3SDsVYRLIm9ZquJHMiZQGyUGnsvrKL6AAVNK7eQZCRDk9KQz+0GKOGqku0nOZjUbAu6A2/vtXAaAuFSFx1rUQVVjFulLexkXR3KcztL1Qu2k5pB6Si0K/uwQ=";

    private Client client = new Client(url, appId, privateKey, AllInOneTest::assertResult);

    /**
     * 以get方式提交
     */
    public void testGet() {
        Client.RequestBuilder requestBuilder = new Client.RequestBuilder()
                .method("alipay.story.get")
                .version("1.0")
                .bizContent(new BizContent().add("id", "1").add("name", "葫芦娃"))
                .httpMethod(HttpTool.HTTPMethod.GET);

        client.execute(requestBuilder);
    }

    /**
     * 以表单方式提交(application/x-www-form-urlencoded)
     */
    public void testPostForm() {
        Client.RequestBuilder requestBuilder = new Client.RequestBuilder()
                .method("alipay.story.get")
                .version("1.0")
                .bizContent(new BizContent().add("id", "1").add("name", "葫芦娃"))
                .httpMethod(HttpTool.HTTPMethod.POST);

        client.execute(requestBuilder);
    }

    /**
     * 以json方式提交(application/json)
     */
    public void testPostJSON() {
        Client.RequestBuilder requestBuilder = new Client.RequestBuilder()
                .method("alipay.story.get")
                .version("1.0")
                // 以json方式提交
                .postJson(true)
                .bizContent(new BizContent().add("id", "1").add("name", "葫芦娃"))
                .httpMethod(HttpTool.HTTPMethod.POST);

        client.execute(requestBuilder);
    }

    /**
     * 测试feign。gateway -> book-service(consumer) -> story-service(provider)
     */
    public void testFeign() {
        Client.RequestBuilder requestBuilder = new Client.RequestBuilder()
                .method("alipay.book.story.get")
                .version("1.0")
                .bizContent(new BizContent())
                .httpMethod(HttpTool.HTTPMethod.GET);

        client.execute(requestBuilder);
    }

    /**
     * 测试dubbo服务，book会调用story提供的服务。参见：DemoConsumerController.java
     */
    public void testDubbo() {
        Client.RequestBuilder requestBuilder = new Client.RequestBuilder()
                .method("dubbo.story.get")
                .version("1.0")
                .bizContent(new BizContent().add("id", "222"))
                .httpMethod(HttpTool.HTTPMethod.GET);

        client.execute(requestBuilder);

    }

    /**
     * 忽略验证,不校验签名，只需传接口名、版本号、业务参数
     */
    public void testIgnoreSign() {
        Client.RequestBuilder requestBuilder = new Client.RequestBuilder()
                .method("story.get")
                .version("2.1")
                .ignoreSign(true)
                .bizContent(new BizContent().add("id", "222").add("name", "忽略222"))
                .httpMethod(HttpTool.HTTPMethod.GET);

        client.execute(requestBuilder);
    }

    /**
     * OpenContext参数绑定
     */
    public void testOpenContext() {
        Client client = new Client(url, appId, privateKey);
        Client.RequestBuilder requestBuilder = new Client.RequestBuilder()
                .method("story.get")
                .version("2.2")
                .bizContent(new BizContent().add("id", "222").add("name", "openContext"))
                .httpMethod(HttpTool.HTTPMethod.GET)
                .callback((requestInfo, responseData) -> {
                    System.out.println(responseData);
                    JSONObject jsonObject = JSON.parseObject(responseData);
                    String name = jsonObject.getJSONObject(requestInfo.getDataNode()).getString("name");
                    Assert.assertEquals(name, "appId:" + appId + ", openContext");
                });

        client.execute(requestBuilder);
    }

    /**
     * 其它参数绑定
     */
    public void testOtherParam() {
        Client.RequestBuilder requestBuilder = new Client.RequestBuilder()
                .method("story.get")
                .version("2.3")
                .bizContent(new BizContent().add("id", "222").add("name", "request param"))
                .httpMethod(HttpTool.HTTPMethod.GET);

        client.execute(requestBuilder);
    }

    /**
     * JSR-303参数校验
     */
    public void testJSR303() {
        Client client = new Client(url, appId, privateKey);
        Client.RequestBuilder requestBuilder = new Client.RequestBuilder()
                .method("goods.add")
                .version("1.0")
                .bizContent(new BizContent().add("goods_name", "iphone6").add("goods_remark", "iphone6").add("goods_comment", "1"))
                .httpMethod(HttpTool.HTTPMethod.POST)
                .callback((requestInfo, responseData) -> {
                    System.out.println(responseData);
                    JSONObject jsonObject = JSON.parseObject(responseData);
                    String sub_msg = jsonObject.getJSONObject(requestInfo.getDataNode()).getString("sub_msg");
                    Assert.assertEquals(sub_msg, "商品评论长度必须在3和20之间");
                });

        client.execute(requestBuilder);
    }

    /**
     * 测试是否有权限访问，可在sop-admin中设置权限
     */
    public void testPermission() {
        Client.RequestBuilder requestBuilder = new Client.RequestBuilder()
                .method("permission.story.get")
                .version("1.0")
                .bizContent(new BizContent())
                .httpMethod(HttpTool.HTTPMethod.GET);

        client.execute(requestBuilder);
    }

    /**
     * 演示将接口名版本号跟在url后面，规则:http://host:port/{method}/{version}/
     */
    public void testRestful() {
        Client.RequestBuilder requestBuilder = new Client.RequestBuilder()
                .url("http://localhost:8081/alipay.story.get/1.0/")
                .bizContent(new BizContent().add("name", "name111"))
                .httpMethod(HttpTool.HTTPMethod.GET);

        client.execute(requestBuilder);
    }

    /**
     * 演示文件上传
     */
    public void testFile() {
        Client client = new Client(url, appId, privateKey);
        String root = System.getProperty("user.dir");
        Client.RequestBuilder requestBuilder = new Client.RequestBuilder()
                .method("demo.file.upload")
                .version("1.0")
                .bizContent(new BizContent().add("remark", "test file upload"))
                // 添加文件
                .addFile("file1", new File(root + "/src/main/resources/file1.txt"))
                .addFile("file2", new File(root + "/src/main/resources/file2.txt"))
                .callback((requestInfo, responseData) -> {
                    System.out.println(responseData);
                    JSONObject jsonObject = JSON.parseObject(responseData);
                    JSONObject data = jsonObject.getJSONObject(requestInfo.getDataNode());
                    Assert.assertEquals(data.getString("code"), "10000");
                    Assert.assertEquals(data.getJSONArray("files").size(), 2);
                })
                ;

        client.execute(requestBuilder);
    }

    /**
     * 演示文件上传2
     */
    public void testFile2() {
        Client client = new Client(url, appId, privateKey);
        String root = System.getProperty("user.dir");
        Client.RequestBuilder requestBuilder = new Client.RequestBuilder()
                .method("demo.file.upload2")
                .version("1.0")
                .bizContent(new BizContent().add("remark", "test file upload"))
                // 添加文件
                .addFile("file1", new File(root + "/src/main/resources/file1.txt"))
                .addFile("file2", new File(root + "/src/main/resources/file2.txt"))
                .callback((requestInfo, responseData) -> {
                    JSONObject jsonObject = JSON.parseObject(responseData);
                    JSONObject data = jsonObject.getJSONObject(requestInfo.getDataNode());
                    Assert.assertEquals(data.getString("code"), "10000");
                    Assert.assertEquals(data.getJSONArray("files").size(), 2);
                })
                ;

        client.execute(requestBuilder);
    }

    /**
     * 验证中文乱码问题
     */
    public void testString() {
        Client.RequestBuilder requestBuilder = new Client.RequestBuilder()
                .method("story.string.get")
                .version("1.0")
                .bizContent(new BizContent().add("name", "name111"))
                .httpMethod(HttpTool.HTTPMethod.GET)
                .callback((requestInfo, responseData) -> {
                    JSONObject jsonObject = JSON.parseObject(responseData);
                    JSONObject data = jsonObject.getJSONObject("story_string_get_response");
                    Assert.assertEquals("海底小纵队", data.getString("name"));
                });

        client.execute(requestBuilder);
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
                        Client.RequestBuilder requestBuilder = new Client.RequestBuilder()
                                .method("alipay.story.get")
                                .version("1.2")
                                .bizContent(new BizContent().add("id", "1").add("name", "葫芦娃"))
                                .httpMethod(HttpTool.HTTPMethod.GET);

                        client.execute(requestBuilder);
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

    class BizContent extends HashMap<String, String> {
        public BizContent add(String key, String value) {
            this.put(key, value);
            return this;
        }
    }

    public static void assertResult(Client.RequestInfo requestInfo, String responseData) {
        System.out.println(responseData);
        String method = requestInfo.getMethod();
        if (method == null) {
            return;
        }
        String node = requestInfo.getDataNode();
        JSONObject jsonObject = JSON.parseObject(responseData).getJSONObject(node);
        String code = Optional.ofNullable(jsonObject).map(json -> json.getString("code")).orElse("20000");
        Assert.assertEquals("10000", code);
    }

}
