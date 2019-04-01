package com.gitee.sop.adminserver.bean;

import com.gitee.easyopen.exception.ApiException;
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

import static com.gitee.sop.adminserver.bean.SopAdminConstants.SOP_MSG_CHANNEL_PATH;
import static com.gitee.sop.adminserver.bean.SopAdminConstants.SOP_ROUTE_PERMISSION_PATH;

/**
 * @author tanghc
 */
@Configuration
public class ZookeeperContext {

    private static CuratorFramework client;

    @Value("${spring.cloud.zookeeper.connect-string}")
    private String zookeeperServerAddr;

    @Value("${spring.cloud.zookeeper.baseSleepTimeMs}")
    private String baseSleepTimeMs;

    @Value("${spring.cloud.zookeeper.maxRetries}")
    private String maxRetries;

    @PostConstruct
    protected void after() {
        if (StringUtils.isBlank(zookeeperServerAddr)) {
            throw new IllegalArgumentException("未指定spring.cloud.zookeeper.connect-string参数");
        }
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(zookeeperServerAddr)
                .retryPolicy(new ExponentialBackoffRetry(NumberUtils.toInt(baseSleepTimeMs, 3000), NumberUtils.toInt(maxRetries, 3)))
                .build();

        client.start();

        setClient(client);
    }

    public static String getSopRouteRootPath() {
        return SopAdminConstants.SOP_SERVICE_ROUTE_PATH;
    }

    public static String getRoutePermissionPath() {
        return SOP_ROUTE_PERMISSION_PATH;
    }

    public static String getIsvInfoChannelPath() {
        return SOP_MSG_CHANNEL_PATH + "/isvinfo";
    }

    public static String getIsvRoutePermissionChannelPath() {
        return SOP_MSG_CHANNEL_PATH + "/isv-route-permission";
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
     * 对已存在的path赋值。如果path不存在抛异常
     *
     * @param path 已存在的
     * @param data
     * @return
     * @throws Exception
     */
    public static Stat updatePathData(String path, String data) throws Exception {
        if (!isPathExist(path)) {
            throw new ApiException("path " + path + " 不存在");
        }
        return getClient().setData().forPath(path, data.getBytes());
    }

    /**
     * 创建新的path，并赋值。如果path已存在抛异常
     * @param path 待创建的path
     * @param data 值
     */
    public static String createNewData(String path, String data) throws Exception {
        if (isPathExist(path)) {
            throw new ApiException("path " + path + " 已存在");
        }
        return  getClient().create()
                // 如果指定节点的父节点不存在，则Curator将会自动级联创建父节点
                .creatingParentContainersIfNeeded()
                .forPath(path, data.getBytes());
    }

    /**
     * 新建或保存节点
     * @param path
     * @param data
     * @return
     * @throws Exception
     */
    public static String createOrUpdateData(String path, String data) throws Exception {
        return getClient().create()
                // 如果节点存在则Curator将会使用给出的数据设置这个节点的值
                .orSetData()
                // 如果指定节点的父节点不存在，则Curator将会自动级联创建父节点
                .creatingParentContainersIfNeeded()
                .forPath(path, data.getBytes());
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
     *
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
