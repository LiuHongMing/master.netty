package master.netty.api.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class NioHttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private boolean enableSSL;

    public NioHttpServerInitializer(boolean enableSSL) {
        this.enableSSL = enableSSL;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("handler", new NioHttpServerHandler());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65535));
        pipeline.addLast("encoder", null);
        pipeline.addLast("decoder", null);

    }

}
