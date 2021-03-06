package master.zookeeper;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.apache.zookeeper.*;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.AsyncCallback.*;
import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class ZkClientTest {

    public static final int ZK_SESSION_TIMEOUT = 50000;

    static Joiner getJoiner(String separator) {
        return Joiner.on(separator);
    }

    CountDownLatch latch = new CountDownLatch(1);

    static Map getStatMap(Stat stat) {
        Map map = Maps.newHashMap();
        if (stat != null) {
            map.put("czxid", stat.getCzxid());
            map.put("mzxid", stat.getMzxid());
            map.put("ctime", stat.getCtime());
            map.put("mtime", stat.getMtime());
            map.put("version", stat.getVersion());
            map.put("cversion", stat.getCversion());
            map.put("aversion", stat.getAversion());
            map.put("ephemeralOwne", stat.getEphemeralOwner());
            map.put("dataLength", stat.getDataLength());
            map.put("numChildren", stat.getNumChildren());
            map.put("pzxid", stat.getPzxid());
        }
        return map;
    }

    @Test
    public void testWatcher() throws Exception {
        Joiner.MapJoiner statJoiner = getJoiner(",").withKeyValueSeparator("=");

        ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", ZK_SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.printf("Watcher回调: path=%s, type=%s, keeperState=%s\n",
                        event.getPath(), event.getType(), event.getState());
                if (event.getState() == SyncConnected) {
                    latch.countDown();
                }
            }
        });

        latch.await();

        Stat stat;

        System.out.println("---------------");

        stat = zk.exists("/root", true);
        if (null != stat) {
            // 存在子节点时无法删除
            zk.delete("/root", -1);
        }
        zk.create("/root", "mydata".getBytes(), OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        zk.getData("/root", false, new DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println("获取数据(DataCallback), result code=" + rc + ",path=" + path + ",ctx=" + ctx
                        + ",data=" + (data == null ? null : new String(data)) + ",stat=" + stat);
            }
        }, null);

        zk.setData("/root", "changedata".getBytes(), -1, new StatCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, Stat stat) {
                System.out.println("更新数据(StatCallback), result code=" + rc + ",path=" + path + ",ctx=" + ctx + ",stat=" + stat);
            }
        }, "Root context");

        System.out.println("---------------");

        stat = zk.exists("/root/childone", true);
        if (null != stat) {
            zk.delete("/root/childone", stat.getVersion());
        }
        zk.create("/root/childone", "childone".getBytes(), OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        System.out.println("---------------");

        zk.exists("/root/childone", true);
        zk.delete("/root/childone", -1);

        System.out.println("---------------");

        zk.exists("/root", true);
        zk.delete("/root", -1);

    }

}
