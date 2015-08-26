package master.netty.api.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class NioHttpServerInitializer extends ChannelInitializer<SocketChannel> {

    public NioHttpServerInitializer(boolean enableSSL) {
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {


    }

}
