package action.rpc.simple.server;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;
import static org.apache.zookeeper.CreateMode.EPHEMERAL_SEQUENTIAL;
import static org.apache.zookeeper.CreateMode.PERSISTENT;
import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * 服务注册
 */
public class ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private String registryAddress;

    public ServiceRegistry(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public void register(String data) throws Exception {
        ZooKeeper zk = null;
        if (data != null) {
            zk = connectServer();
        }

        Stat stat = zk.exists(Constants.ZK_REGISTRY_PATH, true);
        if (stat == null) {
            createRootNode(zk, null);
        }
        createChildNode(zk, data);
    }

    // 连接zookeeper
    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, Constants.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return zk;
    }

    // 创建父节点
    private void createRootNode(ZooKeeper zk, String data) {
        createNode(zk, Constants.ZK_REGISTRY_PATH, data, PERSISTENT);
    }

    // 创建子节点
    private void createChildNode(ZooKeeper zk, String data) {
        createNode(zk, Constants.ZK_DATA_PATH, data, EPHEMERAL);
    }

    private void createNode(ZooKeeper zk, String path, String data, CreateMode createMode) {
        byte[] bytes = null;
        if (StringUtils.isNotEmpty(data)) {
            bytes = data.getBytes();
        }
        try {
            String zkPath = zk.create(path, bytes, OPEN_ACL_UNSAFE, createMode);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("create zookeeper node ({} => {})", zkPath, data);
            }
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("", e);
        }
    }

}
