package action.rpc.simple.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * RPC
 */
public class RpcBootstrap {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring/server-context.xml");
    }

}
