package netty.springmvc;

import netty.springmvc.server.Environment;
import netty.springmvc.server.NettyServer;

import java.io.IOException;
import java.util.Properties;

public class Bootstrap {

    public static void main(String[] args) throws IOException {
//        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());

        // 读取服务配置文件
        Properties properties = new Properties();
        properties.load(Bootstrap.class.getClass().getResourceAsStream("/env.properties"));
        Integer port = Integer.parseInt(properties.get("port").toString());
        String charset = properties.get("charset").toString();
        String env = properties.get("env").toString();
        String configLocation = properties.get("contextConfigLocation").toString();
        // 从输入参数获取端口和环境配置
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                switch (i) {
                    case 0:
                        port = Integer.parseInt(args[i]);
                    case 1:
                        env = args[1];
                }
            }
        }

        NettyServer server = new NettyServer(port, charset, Environment.valueOf(env), configLocation.split(","));
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
