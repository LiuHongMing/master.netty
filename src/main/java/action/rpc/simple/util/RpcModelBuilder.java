package action.rpc.simple.util;

import action.rpc.simple.model.RpcRequest;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by liuhongming on 12/1/2015.
 */
public class RpcModelBuilder {

    private Method method;

    private RpcModelBuilder(Method method) {
        this.method = method;
    }

    public static RpcModelBuilder requestMethod(Method method) {
        return new RpcModelBuilder(method);
    }

    public RpcRequest build(Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        return request;
    }

}
