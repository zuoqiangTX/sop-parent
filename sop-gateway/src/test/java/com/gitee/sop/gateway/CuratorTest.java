package com.gitee.sop.gateway;

import com.gitee.sop.gatewaycommon.bean.SopConstants;
import junit.framework.TestCase;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author tanghc
 */
public class CuratorTest extends TestCase {


    private String zookeeperServerAddr = "127.0.0.1:2181";

    /**
     * 递归删除节点，只能在测试环境用。
     *
     * @throws Exception
     */
    public void testDel() throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(zookeeperServerAddr)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();

        client.start();

        try {
            client.delete().deletingChildrenIfNeeded().forPath(SopConstants.SOP_SERVICE_ROUTE_PATH);
        } catch (Exception e) {
        }
        try {
            client.delete().deletingChildrenIfNeeded().forPath(SopConstants.SOP_SERVICE_ROUTE_PATH + "-default");
        } catch (Exception e) {
        }
        try {
            client.delete().deletingChildrenIfNeeded().forPath(SopConstants.SOP_SERVICE_ROUTE_PATH + "-dev");
        } catch (Exception e) {
        }
        try {
            client.delete().deletingChildrenIfNeeded().forPath(SopConstants.SOP_SERVICE_ROUTE_PATH + "-test");
        } catch (Exception e) {
        }
    }
}
