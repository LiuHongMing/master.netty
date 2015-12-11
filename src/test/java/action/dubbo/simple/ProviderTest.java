package action.dubbo.simple;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ProviderTest {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/dubbo-provider.xml");
        ctx.start();
        synchronized (ProviderTest.class) {
            while(true) {
                try {
                    ProviderTest.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
