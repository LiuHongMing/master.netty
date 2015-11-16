package action.rpc.simple.client;

import action.rpc.simple.server.RpcRequest;
import action.rpc.simple.server.RpcResponse;
import com.google.common.base.Splitter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.UUID;

public class RpcProxy {

    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<T> interfaceCls) {
        T newInstance = (T) Proxy.newProxyInstance(interfaceCls.getClassLoader(), new Class[] {interfaceCls}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcRequest request = new RpcRequest();
                request.setRequestId(UUID.randomUUID().toString());
                request.setClassName(method.getDeclaringClass().getName());
                request.setMethodName(method.getName());
                request.setParameterTypes(method.getParameterTypes());
                request.setParameters(args);

                if (serviceDiscovery != null) {
                    serverAddress = serviceDiscovery.discovery();
                }

                List<String> position = Splitter.on(":").splitToList(serverAddress);
                String host = position.get(0);
                int port = Integer.valueOf(position.get(1));

                RpcClient client = new RpcClient(host, port);
                RpcResponse response = client.send(request);

                if (response.isError()) {
                    throw response.getError();
                }

                return response.getResult();
            }
        });
        return newInstance;
    }

}
