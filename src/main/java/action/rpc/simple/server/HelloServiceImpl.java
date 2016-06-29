package action.rpc.simple.server;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    String hello;

    @Override
    public String hello(String name) {
        if (name.equals("666")) {
            return "I am 666";
        }
        return "Hello! " + name;
    }

    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }
}
