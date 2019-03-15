package com.gitee.sop.gatewaycommon.manager;

import com.alibaba.fastjson.JSON;
import com.gitee.sop.gatewaycommon.bean.BaseRouteDefinition;
import com.gitee.sop.gatewaycommon.bean.BaseServiceRouteInfo;
import com.gitee.sop.gatewaycommon.bean.TargetRoute;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.gitee.sop.gatewaycommon.bean.SopConstants.SOP_SERVICE_ROUTE_PATH;


/**
 * 路由管理，采用zookeeper实现，监听路由的增删改，并适时更新到本地。路由的存储格式为：
 * <pre>
 * /sop-service-route  根节点
 * /serviceId       服务节点，名字为服务名
 * /route1      路由节点，名字为：name+version，存放路由信息
 * /route2
 * /...
 * </pre>
 *
 * @param <R> 路由根对象，可以理解为最外面的大json：{....,routeDefinitionList:[]}
 * @param <E> 路由Item对象，对应大json里面的具体路由信息，routeDefinitionList:[]
 * @param <T> 目标路由对象，返回最终的路由对象
 * @author tanghc
 */
@Slf4j
public abstract class BaseRouteManager<R extends BaseServiceRouteInfo<E>, E extends BaseRouteDefinition, T extends TargetRoute> implements RouteManager {

    protected String sopRouteRootPath = SOP_SERVICE_ROUTE_PATH;

    protected Environment environment;

    protected RouteRepository<T> routeRepository;

    /**
     * 返回路由根对象class
     *
     * @return
     */
    protected abstract Class<R> getServiceRouteInfoClass();

    /**
     * 返回路由Item对象class
     *
     * @return
     */
    protected abstract Class<E> getRouteDefinitionClass();

    /**
     * 构建目标路由对象，zuul和gateway定义的路由对象
     *
     * @param serviceRouteInfo
     * @param routeDefinition
     * @return
     */
    protected abstract T buildRouteDefinition(R serviceRouteInfo, E routeDefinition);

    public BaseRouteManager(Environment environment, RouteRepository<T> routeRepository) {
        this.environment = environment;
        this.routeRepository = routeRepository;
    }

    @Override
    public void refresh() {
        log.info("刷新本地接口信息");
        try {
            String zookeeperServerAddr = environment.getProperty("spring.cloud.zookeeper.connect-string");
            if (StringUtils.isEmpty(zookeeperServerAddr)) {
                throw new RuntimeException("未指定spring.cloud.zookeeper.connect-string参数");
            }
            CuratorFramework client = CuratorFrameworkFactory.builder()
                    .connectString(zookeeperServerAddr)
                    .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                    .build();

            client.start();

            client.create()
                    // 如果节点存在则Curator将会使用给出的数据设置这个节点的值
                    .orSetData()
                    // 如果指定节点的父节点不存在，则Curator将会自动级联创建父节点
                    .creatingParentContainersIfNeeded()
                    .forPath(sopRouteRootPath, "".getBytes());

            this.watchServiceChange(client, sopRouteRootPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听微服务更改
     *
     * @param client
     * @param sopServiceApiPath
     * @throws Exception
     */
    protected void watchServiceChange(CuratorFramework client, String sopServiceApiPath) throws Exception {
        // 为子节点添加watcher
        // PathChildrenCache: 监听数据节点的增删改，可以设置触发的事件
        PathChildrenCache childrenCache = new PathChildrenCache(client, sopServiceApiPath, true);

        /**
         * StartMode: 初始化方式
         * POST_INITIALIZED_EVENT：异步初始化，初始化之后会触发事件
         * NORMAL：异步初始化
         * BUILD_INITIAL_CACHE：同步初始化
         */
        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);

        // 列出子节点数据列表，需要使用BUILD_INITIAL_CACHE同步初始化模式才能获得，异步是获取不到的
        List<ChildData> childDataList = childrenCache.getCurrentData();
        log.info("========== 加载路由信息 ==========");
        log.info("{}  # 根节点", sopRouteRootPath);
        for (ChildData childData : childDataList) {
            String serviceNodeData = new String(childData.getData());
            R serviceRouteInfo = JSON.parseObject(serviceNodeData, getServiceRouteInfoClass());
            String servicePath = childData.getPath();
            log.info("\t{}  # service节点，节点数据:{}", servicePath, serviceNodeData);
            this.loadServiceRouteItem(client, serviceRouteInfo, servicePath);
        }
        log.info("监听服务节点增删改，rootPath:{}", sopRouteRootPath);
        // 监听根节点下面的子节点
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                PathChildrenCacheEvent.Type type = event.getType();
                synchronized (type) {
                    // 通过判断event type的方式来实现不同事件的触发
                    if (PathChildrenCacheEvent.Type.CHILD_ADDED.equals(type)) {
                        String serviceNodeData = new String(event.getData().getData());
                        R serviceRouteInfo = JSON.parseObject(serviceNodeData, getServiceRouteInfoClass());
                        // 添加子节点时触发
                        String servicePath = event.getData().getPath();
                        log.info("新增serviceId节点：{}，节点数据:{}", servicePath, serviceNodeData);
                        loadServiceRouteItem(client, serviceRouteInfo, servicePath);
                    } else if (PathChildrenCacheEvent.Type.CHILD_UPDATED.equals(type)) {
                        // 修改子节点数据时触发，暂时没有什么操作
                        String nodeData = new String(event.getData().getData());
                        log.info("修改serviceId节点：{}，节点数据:{}", event.getData().getPath(), nodeData);
                    } else if (PathChildrenCacheEvent.Type.CHILD_REMOVED.equals(type)) {
                        // 删除service节点
                        String nodeData = new String(event.getData().getData());
                        log.info("删除serviceId节点：{}，节点数据:{}", event.getData().getPath(), nodeData);
                        R serviceRouteInfo = JSON.parseObject(nodeData, getServiceRouteInfoClass());
                        routeRepository.deleteAll(serviceRouteInfo.getServiceId());
                    }
                }
            }
        });
    }

    /**
     * 加载service下的路由信息
     *
     * @param servicePath
     */
    protected void loadServiceRouteItem(CuratorFramework client, R serviceRouteInfo, String servicePath) throws Exception {
        // 获取service节点下所有的路由节点，里面保存的是路由名称，前面没有斜杠"/"
        List<String> pathNameList = client.getChildren().forPath(servicePath);
        for (String pathName : pathNameList) {
            // 完整的路径
            String routeItemPath = servicePath + "/" + pathName;
            byte[] routeItemData = client.getData().forPath(routeItemPath);
            String routeDataJson = buildZookeeperData(routeItemData);
            log.info("\t\t{}  # 路由节点，节点数据:{}", routeItemPath, routeDataJson);
            this.saveRouteItem(serviceRouteInfo, routeDataJson);
        }
        this.watchRouteItems(client, serviceRouteInfo, servicePath);
    }

    /**
     * 监听serviceId目录下面的子节点
     *
     * @param client
     * @param serviceRouteInfo
     * @param servicePath      serviceId节点
     */
    protected void watchRouteItems(CuratorFramework client, R serviceRouteInfo, String servicePath) throws Exception {
        log.info("监听路由节点增删改，servicePath:{}", servicePath);
        PathChildrenCache childrenCache = new PathChildrenCache(client, servicePath, true);
        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        // 添加事件监听器
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                PathChildrenCacheEvent.Type type = event.getType();
                synchronized (type) {
                    // 通过判断event type的方式来实现不同事件的触发
                    if (PathChildrenCacheEvent.Type.CHILD_ADDED.equals(type)) {
                        // 新增单个路由
                        String routeDataJson = buildZookeeperData(event.getData().getData());
                        log.info("新增单个路由，serviceId:{}, 路由数据:{}", serviceRouteInfo.getServiceId(), routeDataJson);
                        saveRouteItem(serviceRouteInfo, routeDataJson);
                    } else if (PathChildrenCacheEvent.Type.CHILD_UPDATED.equals(type)) {
                        // 修改单个路由
                        String routeDataJson = buildZookeeperData(event.getData().getData());
                        log.info("修改单个路由，serviceId:{}, 路由数据:{}", serviceRouteInfo.getServiceId(), routeDataJson);
                        updateRouteItem(serviceRouteInfo, routeDataJson);
                    } else if (PathChildrenCacheEvent.Type.CHILD_REMOVED.equals(type)) {
                        // 删除单个路由
                        String routeDataJson = buildZookeeperData(event.getData().getData());
                        log.info("删除单个路由，serviceId:{}, 路由数据:{}", serviceRouteInfo.getServiceId(), routeDataJson);
                        deleteRouteItem(serviceRouteInfo, routeDataJson);
                    }
                }
            }
        });

    }

    protected void saveRouteItem(R serviceRouteInfo, String nodeDataJson) {
        T routeDefinition = getRouteDefinition(serviceRouteInfo, nodeDataJson);
        routeRepository.add(routeDefinition);
    }

    protected void updateRouteItem(R serviceRouteInfo, String nodeDataJson) {
        T routeDefinition = getRouteDefinition(serviceRouteInfo, nodeDataJson);
        routeRepository.update(routeDefinition);
    }

    protected void deleteRouteItem(R serviceRouteInfo, String nodeDataJson) {
        E routeDefinitionItem = getRouteDefinitionItem(nodeDataJson);
        routeRepository.delete(routeDefinitionItem.getId());
    }

    protected T getRouteDefinition(R serviceRouteInfo, String nodeDataJson) {
        E routeDefinitionItem = getRouteDefinitionItem(nodeDataJson);
        T routeDefinition = buildRouteDefinition(serviceRouteInfo, routeDefinitionItem);
        return routeDefinition;
    }

    protected E getRouteDefinitionItem(String nodeDataJson) {
        return JSON.parseObject(nodeDataJson, getRouteDefinitionClass());
    }

    protected String buildZookeeperData(byte[] data) {
        return new String(data);
    }

}
