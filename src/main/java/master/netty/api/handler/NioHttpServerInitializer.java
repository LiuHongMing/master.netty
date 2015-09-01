package master.netty.api.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;

public class NioHttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private boolean enableSSL;

    public NioHttpServerInitializer(boolean enableSSL) {
        this.enableSSL = enableSSL;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("encoder", new HttpRequestEncoder());
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65535));
        pipeline.addLast("handler", new NioHttpServerHandler());

    }

}
