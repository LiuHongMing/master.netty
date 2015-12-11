package action.dubbo.simple;

import action.rpc.simple.server.HelloService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConsumerTest {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/dubbo-consumer.xml");
        ctx.start();

        HelloService hs = (HelloService) ctx.getBean("helloService");
        System.out.println(hs.hello("J.Ming"));
    }
}
