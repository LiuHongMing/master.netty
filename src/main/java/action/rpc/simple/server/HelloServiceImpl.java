package action.rpc.simple.server;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    String hello;

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }

    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }
}
