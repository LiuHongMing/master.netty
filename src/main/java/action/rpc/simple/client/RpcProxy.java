package action.rpc.simple.client;

import action.rpc.simple.model.RpcRequest;
import action.rpc.simple.model.RpcResponse;
import action.rpc.simple.util.RpcModelBuilder;
import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class RpcProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);

    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> targetInterface) {
        T newInstance = (T) Proxy.newProxyInstance(targetInterface.getClassLoader(), new Class[]{targetInterface}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                LOGGER.info("Method:{}", method.getName());

                RpcRequest request = RpcModelBuilder.requestMethod(method).build(args);

                if (serviceDiscovery != null) {
                    serverAddress = serviceDiscovery.discovery();
                }

                List<String> position = Splitter.on(":").splitToList(serverAddress);
                String host = position.get(0);
                int port = Integer.valueOf(position.get(1));

                RpcClient client = new RpcClient(host, port);
                RpcResponse response = client.send(request);

                if (response.isError()) throw response.getError();

                LOGGER.info("response.getResult():{}", response.getResult());

                return response.getResult();
            }
        });
        return newInstance;
    }

}
