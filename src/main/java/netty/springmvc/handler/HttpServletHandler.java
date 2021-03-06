package netty.springmvc.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedStream;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @ClassName: HttpServletHandler
 * @Description: Mock servlet
 */
public class HttpServletHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Servlet servlet;

    private final ServletContext servletContext;

    private final String charset;

    public HttpServletHandler(Servlet servlet, String charset) {
        this.servlet = servlet;
        this.servletContext = servlet.getServletConfig().getServletContext();
        this.charset = charset;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        if (fullHttpRequest.getDecoderResult().isFailure()) {
            printError(channelHandlerContext, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        MockHttpServletRequest  servletRequest  = createHttpRequest(fullHttpRequest);
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        servlet.service(servletRequest, servletResponse);

        HttpResponseStatus responseStatus = HttpResponseStatus.valueOf(servletResponse.getStatus());
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, responseStatus);

        for (String name : servletResponse.getHeaderNames()) {
            for (Object value : servletResponse.getHeaderValues(name)) {
                response.headers().add(name, value);
            }
        }

        byte[] responseContent = servletResponse.getContentAsByteArray();
        if (response.getStatus().code() != HttpResponseStatus.OK.code()) {
            responseContent = (response.getStatus().code() + response.getStatus().reasonPhrase() + "  errorMessage:" + servletResponse.getErrorMessage()).getBytes(charset);
        }

        channelHandlerContext.write(response);
        InputStream inputStream = new ByteArrayInputStream(responseContent);
        ChannelFuture channelFuture = channelHandlerContext.writeAndFlush(new ChunkedStream(inputStream));
        channelFuture.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            printError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 包装HttpServletRequest
     *
     * @param fullHttpRequest
     */
    private MockHttpServletRequest createHttpRequest(FullHttpRequest fullHttpRequest) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(fullHttpRequest.getUri()).build();

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI(uriComponents.getPath());
        servletRequest.setPathInfo(uriComponents.getPath());
        servletRequest.setMethod(fullHttpRequest.getMethod().name());

        if (uriComponents.getScheme() != null) {
            servletRequest.setScheme(uriComponents.getScheme());
        }
        if (uriComponents.getPort() != -1) {
            servletRequest.setServerPort(uriComponents.getPort());
        }
        if (uriComponents.getHost() != null) {
            servletRequest.setServerName(uriComponents.getHost());
        }

        //请求头
        for (String name : fullHttpRequest.headers().names()) {
            servletRequest.addHeader(name, fullHttpRequest.headers().get(name));
        }

        //请求内容
        ByteBuf content = fullHttpRequest.content();
        if (content != null && content.isReadable()) {
            byte[] byteContent = new byte[content.readableBytes()];
            content.readBytes(byteContent);
            servletRequest.setContent(byteContent);
        }

        //请求参数
        try {
            if (uriComponents.getQuery() != null) {
                String query = UriUtils.decode(uriComponents.getQuery(), charset);
            }

            for (java.util.Map.Entry<String, List<String>> entry : uriComponents.getQueryParams().entrySet()) {
                for (String value : entry.getValue()) {
                    servletRequest.addParameter(UriUtils.decode(entry.getKey(), charset), UriUtils.decode(value, charset));
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return servletRequest;
    }

    /**
     * 响应错误信息
     *
     * @param handlerContext
     * @param status
     */
    private void printError(ChannelHandlerContext handlerContext, HttpResponseStatus status) {
        ByteBuf content = Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", Charset.forName(charset));
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
        httpResponse.headers().add(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=" + charset);
        handlerContext.write(httpResponse).addListener(ChannelFutureListener.CLOSE);
    }

}
