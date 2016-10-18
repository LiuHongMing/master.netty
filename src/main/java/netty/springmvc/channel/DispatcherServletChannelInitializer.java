package netty.springmvc.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import netty.springmvc.handler.HttpServletHandler;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletException;

/**
 * @ClassName: DispatcherServletChannelInitializer
 * @Description: 服务启动控制器
 */
public class DispatcherServletChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final String DEFAULT_SERVLET_NAME = "apiServlet";
    private static final String DEFAULT_SERVLET_NAMESPACE = "API";

    private final DispatcherServlet dispatcherServlet;

    private final String charset;

    /**
     * 构造器，初始化上下文
     *
     * @param charset
     * @param env
     * @param configLocation
     * @throws ServletException
     */
    public DispatcherServletChannelInitializer(String charset, String env, String... configLocation) throws ServletException {
        this.charset = charset;

        MockServletContext servletContext = new MockServletContext();
        MockServletConfig servletConfig = new MockServletConfig(servletContext, DEFAULT_SERVLET_NAME);

        XmlWebApplicationContext wac = new XmlWebApplicationContext();
        wac.setServletContext(servletContext);
        wac.setServletConfig(servletConfig);
        wac.setConfigLocations(configLocation);
        // 加载用户定义的环境bean
        wac.getEnvironment().addActiveProfile(env);

        this.dispatcherServlet = new DispatcherServlet(wac);
        this.dispatcherServlet.setNamespace(DEFAULT_SERVLET_NAMESPACE);
        this.dispatcherServlet.init(servletConfig);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
//        pipeline.addLast("logging", new LoggingHandler(LogLevel.WARN));
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
        pipeline.addLast("handler", new HttpServletHandler(dispatcherServlet, charset));
    }

}
