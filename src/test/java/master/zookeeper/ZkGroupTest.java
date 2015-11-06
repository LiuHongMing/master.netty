package master.zookeeper;

import org.apache.zookeeper.AsyncCallback.Children2Callback;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;
import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class ZkGroupTest implements Watcher {

    private static final int SESSION_TIMEOUT = 5000;

    private ZooKeeper zk;
    private CountDownLatch connectedSignal = new CountDownLatch(1);

    public Watcher wh = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            getThreadName("Watcher::process");
            System.out.printf("Callback Watcher: path=%s, type=%s\n", event.getPath(), event.getType());
            connectedSignal.countDown();
        }
    };

    private void getThreadName(String methodName) {
        Thread thread = Thread.currentThread();
        System.out.printf("%s is call in %s\n", methodName, thread.getName());
    }

    public void connect(String hosts) throws IOException, InterruptedException {
        getThreadName("connect");
        zk = new ZooKeeper(hosts, SESSION_TIMEOUT, wh);
        connectedSignal.await();
    }

    public void process(WatchedEvent event) {
        getThreadName("process");
        System.out.println(event.getType());
        if (event.getState() == SyncConnected) {
            connectedSignal.countDown();
        }
    }

    public void join(String groupName, String meberName) throws KeeperException, InterruptedException {
        String path = '/' + groupName + '/' + meberName;

        //EPHEMERAL断开后将被删除
        String createPath = zk.create(path, null, OPEN_ACL_UNSAFE, EPHEMERAL);
        System.out.println("join:" + createPath);
    }

    public List<String> getChilds(String path) {
        if (zk != null) {
            zk.getChildren(path, true, new Children2Callback() {
                @Override
                public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
                    System.out.println("***");
                    for (String str : children) {
                        System.out.printf("mempath = %s%d", str, stat);
                    }
                }
            }, null);
        }
        return null;
    }

    public void create(String path) throws KeeperException, InterruptedException {
        String createPath = zk.create(path, null, OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("create:" + createPath);
    }

    public void close() throws InterruptedException {
        if (zk != null) {
            zk.close();
        }
    }

    public static class ChildWatcher implements Children2Callback {

        private ChildrenCallback processResult;

        @Override
        public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
            System.out.println("*** path=" + stat);
        }
    }

    public void delete(String path) throws KeeperException, InterruptedException {
        zk.delete(path, -1);
    }

    public Stat isExist(String path) throws KeeperException, InterruptedException {
        return zk.exists(path, true);
    }

    public void write(String path, String value) throws Exception {
        Stat stat = zk.exists(path, false);
        if (stat == null) {
            zk.create(path, value.getBytes("UTF-8"), OPEN_ACL_UNSAFE, EPHEMERAL);
        } else {
            zk.setData(path, value.getBytes(), -1);
        }
    }

    public String read(String path, Watcher watcher) throws Exception {
        byte[] datas = zk.getData(path, watcher, null);
        return new String(datas, "UTF-8");
    }

    public static void main(String[] args) throws Exception {
        String hosts = "localhost";
        String groupName = "zkgrouptest";
        String memberName = String.valueOf(System.currentTimeMillis());
        String path = "/" + groupName;

        ZkGroupTest test = new ZkGroupTest();
        //连接
        test.connect(hosts);

        if (null != test.isExist(path)) {
            test.delete(path);
        }

        Stat stat = test.isExist(path);
        System.out.println("Before zooKeeper.create(), Stat=" + stat);
        test.create(path);

        stat = test.isExist(path);
        System.out.println("Before zooKeeper.write(), Stat.version()=" + stat.getVersion());
        test.write(path, "zktest");

        stat = test.isExist(path);
        System.out.println("Before zooKeeper.read(), Stat=" + stat);
        String result = test.read(path, test.wh);
        System.out.println("path=" + path + ",value=" + result);

        int sum = 0;
        for (int j = 0; j < 10000; j++) {
            sum++;
            Thread.sleep(10);
        }
        System.out.println("Sleep...=" + sum * 10);
        test.close();

        // System.exit(2);
        // 一个本地连接的znode
        test.connect(hosts);
        test.join(groupName, memberName);

        // 遍历
        List<String> memlist = test.getChilds(path);
        if (memlist != null) {
            for (int i = 0; i < memlist.size() - 1; i++) {
                System.out.println("mempath = " + memlist.get(i));
            }
        }
    }
}
