package master.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(HttpServerInboundHandler.class);

    private ByteBuf reader = null;

    private HttpRequest request;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {
           /* System.out.println("HttpRequest：" + msg);
            HttpRequest request = (HttpRequest) msg;
            if (HttpHeaders.isContentLengthSet(request)) {
                long length = HttpHeaders.getContentLength(request);
                reader = Unpooled.buffer((int) length);
            }*/

            request = (HttpRequest) msg;
            String uri = request.getUri();
            System.out.println("Request uri:" + uri);
        }

        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            System.out.println(buf.toString(CharsetUtil.UTF_8));
            buf.release();

            String res = "I am ok!";
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, Unpooled.wrappedBuffer(res.getBytes()));
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());

            if (HttpHeaders.isKeepAlive(request)) {
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }

            ctx.write(response);
            ctx.flush();
        }

        /*if (msg instanceof HttpContent) {
            System.out.println("HttpContent：" + msg);

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
        }*/
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("HttpServerInboundHandler.channelReadComplete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
