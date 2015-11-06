package action.rpc;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;
import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * 服务注册
 */
public class ServiceRegistry {

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
        createNode(zk, data);
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

    // 创建znode
    private void createNode(ZooKeeper zk, String data) {
            byte[] bytes = data.getBytes();
        try {
            String path = zk.create(Constants.ZK_REGISTRY_PATH, bytes, OPEN_ACL_UNSAFE, EPHEMERAL);
            System.out.println(path);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
