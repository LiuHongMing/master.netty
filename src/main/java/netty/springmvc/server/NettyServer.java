package netty.springmvc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.springmvc.channel.DispatcherServletChannelInitializer;

/**
 * @ClassName: NettyServer
 * @Description: netty服务端
 */
public class NettyServer {
    //服务端口
    private final int port;
    //内容字符编码
    private final String charset;
    //环境类型（开发、测试、压测、生产）
    private final String env;
    //配置文件路径
    private final String[] configLocation;

    /**
     * 初始化服务端
     *
     * @param port           服务端口
     * @param charset        字符编码
     * @param env            系统环境类型
     * @param configLocation 配置文件路径*
     */
    public NettyServer(int port, String charset, Environment env, String... configLocation) {
        this.port = port;
        this.configLocation = configLocation;
        this.charset = charset;
        this.env = env.toString();
    }

    /**
     * 启动服务
     *
     * @throws Exception
     */
    public void start() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            bootstrap.localAddress(port);
            bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup());
            bootstrap.channel(NioServerSocketChannel.class)
                    .childHandler(new DispatcherServletChannelInitializer(charset, env, configLocation));
            bootstrap.bind().sync().channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}