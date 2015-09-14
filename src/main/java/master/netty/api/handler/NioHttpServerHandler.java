package master.netty.api.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class NioHttpServerHandler extends ChannelInboundHandlerAdapter {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(NioHttpServerHandler.class);

    private ByteBuf reader = null;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            if (HttpHeaders.isContentLengthSet(request)) {
                long length = HttpHeaders.getContentLength(request);
                reader = Unpooled.buffer((int) length);
            }
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            ByteBuf content = httpContent.content();

            content.readBytes(reader, content.readableBytes());
            content.release();

            if (reader.writableBytes() == 0) {
                byte[] contentByte = new byte[reader.readableBytes()];
                reader.readBytes(contentByte);
                reader.release();

                String resultContent = new String(contentByte);
                System.out.println("RequestContent：" + resultContent);

                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                        Unpooled.wrappedBuffer("It's ok".getBytes()));
                response.headers().add(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
                response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
                response.headers().add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);

                ctx.write(response);
                ctx.flush();
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("NioHttpServerHandler.channelReadComplete");
        ctx.flush();
    }
}
