package master.netty.server;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

public class NioHttpServer {

    private static final Range<Integer> allowingPortRange = Range.closed(1000, 65535);
    private static final Integer DEFAULT_LISTENING_PORT = 8080;

    private int port;
    private boolean enableSSL = false;

    public NioHttpServer() {
        this(DEFAULT_LISTENING_PORT, false);
    }

    public NioHttpServer(int port) {
        this(port, false);
    }

    public NioHttpServer(int port, boolean enableSSL) {
        Preconditions.checkArgument(allowingPortRange.contains(port), "port(%s) is out of range %s", port, allowingPortRange);
        this.port = port;
        this.enableSSL = enableSSL;
    }

    public boolean enableSSL() {
        return enableSSL;
    }

    public static void main(String[] args) {

    }
}
