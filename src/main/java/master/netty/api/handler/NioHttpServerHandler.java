package master.netty.api.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class NioHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(NioHttpServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        
    }

}
