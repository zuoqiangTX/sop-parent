package com.gitee.sop.websiteserver.bean;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author tanghc
 */
@Slf4j
public class ZookeeperContext {

    private static CuratorFramework client;

    public static void setEnvironment(Environment environment) {
        Assert.notNull(environment, "environment不能为null");
        initZookeeperClient(environment);
    }

    public synchronized static void initZookeeperClient(Environment environment) {
        if (client != null) {
            return;
        }
        String zookeeperServerAddr = environment.getProperty("spring.cloud.zookeeper.connect-string");
        if (StringUtils.isBlank(zookeeperServerAddr)) {
            throw new RuntimeException("未指定spring.cloud.zookeeper.connect-string参数");
        }
        String baseSleepTimeMs = environment.getProperty("spring.cloud.zookeeper.baseSleepTimeMs");
        String maxRetries = environment.getProperty("spring.cloud.zookeeper.maxRetries");
        log.info("初始化zookeeper客户端，zookeeperServerAddr:{}, baseSleepTimeMs:{}, maxRetries:{}",
                zookeeperServerAddr, baseSleepTimeMs, maxRetries);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(zookeeperServerAddr)
                .retryPolicy(new ExponentialBackoffRetry(NumberUtils.toInt(baseSleepTimeMs, 3000), NumberUtils.toInt(maxRetries, 3)))
                .build();

        client.start();

        setClient(client);
    }

    public static String getRouteRootPath() {
        return WebsiteConstants.SOP_SERVICE_ROUTE_PATH;
    }

    public static String getServiceTempRootPath() {
        return WebsiteConstants.SOP_SERVICE_TEMP_PATH;
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

    /**
     * 是否有子节点
     * @param parentPath
     * @return
     * @throws Exception
     */
    public static boolean hasChildren(String parentPath) throws Exception {
        List<String> children = client.getChildren().forPath(parentPath);
        return !CollectionUtils.isEmpty(children);
    }

    /**
     * 创建path，如果path存在不报错，静默返回path名称
     *
     * @param path
     * @param data
     * @return
     * @throws Exception
     */
    public static String createPath(String path, String data) throws Exception {
        if (isPathExist(path)) {
            return path;
        }
        return getClient().create()
                // 如果指定节点的父节点不存在，则Curator将会自动级联创建父节点
                .creatingParentContainersIfNeeded()
                .forPath(path, data.getBytes());
    }

    /**
     * 获取子节点信息并监听子节点
     *
     * @param parentPath   父节点路径
     * @param listConsumer 子节点数据
     * @param listener     监听事件
     * @throws Exception
     */
    public static void getChildrenAndListen(String parentPath, Consumer<List<ChildData>> listConsumer, PathChildrenCacheListener listener) throws Exception {
        // 为子节点添加watcher
        // PathChildrenCache: 监听数据节点的增删改，可以设置触发的事件
        PathChildrenCache childrenCache = new PathChildrenCache(client, parentPath, true);

        /**
         * StartMode: 初始化方式
         * POST_INITIALIZED_EVENT：异步初始化，初始化之后会触发事件
         * NORMAL：异步初始化
         * BUILD_INITIAL_CACHE：同步初始化
         */
        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);

        // 列出子节点数据列表，需要使用BUILD_INITIAL_CACHE同步初始化模式才能获得，异步是获取不到的
        List<ChildData> childDataList = childrenCache.getCurrentData();
        listConsumer.accept(childDataList);
        log.info("监听子节点增删改，监听路径:{}", parentPath);
        // 监听根节点下面的子节点
        childrenCache.getListenable().addListener(listener);
    }

    /**
     * 监听子节点的增删改
     *
     * @param parentPath 父节点路径
     * @param listener
     * @throws Exception
     */
    public static void listenChildren(String parentPath, PathChildrenCacheListener listener) throws Exception {
        // 为子节点添加watcher
        // PathChildrenCache: 监听数据节点的增删改，可以设置触发的事件
        PathChildrenCache childrenCache = new PathChildrenCache(client, parentPath, true);

        /**
         * StartMode: 初始化方式
         * POST_INITIALIZED_EVENT：异步初始化，初始化之后会触发事件
         * NORMAL：异步初始化
         * BUILD_INITIAL_CACHE：同步初始化
         */
        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        // 监听根节点下面的子节点
        childrenCache.getListenable().addListener(listener);
    }

    /**
     * 监听子节点，可以自定义层级
     * @param parentPath 父节点路径
     * @param maxDepth 层级，从1开始。比如当前监听节点/t1，目录最深为/t1/t2/t3/t4,则maxDepth=3,说明下面3级子目录全部监听
     * @param listener
     * @throws Exception
     */
    public static void listenChildren(String parentPath, int maxDepth, TreeCacheListener listener) throws Exception {
        final TreeCache treeCache = TreeCache
                .newBuilder(client, parentPath)
                .setCacheData(true)
                .setMaxDepth(maxDepth)
                .build();

        treeCache.getListenable().addListener(listener);
        treeCache.start();
    }

}
