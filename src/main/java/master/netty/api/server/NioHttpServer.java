package master.netty.api.server;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import master.netty.api.handler.NioHttpServerInitializer;

public class NioHttpServer {

    private static final Range<Integer> allowingPortRange = Range.closed(1000, 65535);
    private static final Integer DEFAULT_LISTENING_PORT = 8080;

    private int port;
    private boolean enableSSL = false;

    public NioHttpServer() {
        this(DEFAULT_LISTENING_PORT, false);
    }

    public NioHttpServer(int port) throws IllegalArgumentException {
        this(port, false);
    }

    public NioHttpServer(int port, boolean enableSSL) {
        Preconditions.checkArgument(allowingPortRange.contains(port), "port(%s) is out of range %s", port, allowingPortRange);
        this.port = port;
        this.enableSSL = enableSSL;
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NioHttpServerInitializer(enableSSL()))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = serverBootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public boolean enableSSL() {
        return enableSSL;
    }

    public static void main(String[] args) {

    }
}
