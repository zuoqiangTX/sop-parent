# easyopen支持

SOP对easyopen项目提供了很好的支持，如果您的服务端使用了easyopen框架，相关配置步骤如下：

## 服务端配置

首先是服务端相关配置

- pom添加依赖

```xml
<!-- sop接入依赖 -->
<dependency>
    <groupId>com.gitee.sop</groupId>
    <artifactId>sop-service-common</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>net.oschina.durcframework</groupId>
    <artifactId>easyopen</artifactId>
    <version>1.16.1</version>
</dependency>
<!-- sop接入依赖 end -->
```

easyopen版本必须升级到1.16.1

- 启动类上面添加注解@EnableDiscoveryClient，将自己注册到注册中心
- 新增一个配置类，继承EasyopenServiceConfiguration，内容为空

```java
@Configuration
public class SopConfig extends EasyopenServiceConfiguration {
}
```

服务端配置完毕，重启服务。

## 网关端配置

接下来是网关的配置

- 打开ZuulConfig.java，注释掉原本的@Configuration，新增如下Configuration

```java
@Configuration
public class ZuulConfig extends EasyopenZuulConfiguration {
    static {
        new ManagerInitializer();
    }
}
```

配置完毕，重启网关服务，可运行测试用例`EasyopenClientPostTest.java`验证

**注：** 配置完成后easyopen签名校验将会关闭，改用网关端来校验；网关对easyopen返回的结果不进行处理，直接返回服务端的结果。

完整配置可查看sop-example/sop-easyopen项目
