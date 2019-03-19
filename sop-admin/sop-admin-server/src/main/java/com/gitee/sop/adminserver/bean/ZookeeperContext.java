package com.gitee.sop.adminserver.bean;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

/**
 * @author tanghc
 */
@Configuration
public class ZookeeperContext {

    public static final String SOP_ROUTE_ROOT_PATH = SopAdminConstants.SOP_SERVICE_ROUTE_PATH + "-%s";

    private static CuratorFramework client;

    @Value("${spring.cloud.zookeeper.connect-string}")
    private String zookeeperServerAddr;

    @Value("${spring.cloud.zookeeper.baseSleepTimeMs}")
    private String baseSleepTimeMs;

    @Value("${spring.cloud.zookeeper.maxRetries}")
    private String maxRetries;

    @PostConstruct
    protected void after() {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(zookeeperServerAddr)
                .retryPolicy(new ExponentialBackoffRetry(NumberUtils.toInt(baseSleepTimeMs, 3000), NumberUtils.toInt(maxRetries, 3)))
                .build();

        client.start();

        setClient(client);
    }

    public static String getSopRouteRootPath(String profile) {
        if (StringUtils.isBlank(profile)) {
            profile = "default";
        }
        return String.format(SOP_ROUTE_ROOT_PATH, profile);
    }

    public static CuratorFramework getClient() {
        return client;
    }

    public static void setClient(CuratorFramework client) {
        ZookeeperContext.client = client;
    }

    public static boolean isPathExist(String path) {
        try {
            return client.checkExists().forPath(path) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static Stat setData(String path, String data) throws Exception {
        return getClient().setData().forPath(path, data.getBytes());
    }

    public static String getData(String path) throws Exception {
        if (!isPathExist(path)) {
            return null;
        }
        byte[] data = getClient().getData().forPath(path);
        return new String(data);
    }

    /**
     * 获取子节点数据
     * @param parentPath 父节点
     * @return
     * @throws Exception
     */
    public static List<ChildData> getChildrenData(String parentPath) throws Exception {
        PathChildrenCache pathChildrenCache = buildPathChildrenCache(parentPath);
        if (pathChildrenCache == null) {
            return Collections.emptyList();
        }
        return pathChildrenCache.getCurrentData();
    }

    public static PathChildrenCache buildPathChildrenCache(String path) throws Exception {
        if (!isPathExist(path)) {
            return null;
        }
        // PathChildrenCache: 监听数据节点的增删改，可以设置触发的事件
        // 且第三个参数要设置为true，不然ChildData对象中的getData返回null
        PathChildrenCache childrenCache = new PathChildrenCache(client, path, true);
        // 列出子节点数据列表，需要使用BUILD_INITIAL_CACHE同步初始化模式才能获得，异步是获取不到的
        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        return childrenCache;
    }

}
