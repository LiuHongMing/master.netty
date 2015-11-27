package action.rpc.simple;

import action.rpc.simple.server.RpcRequest;
import action.rpc.simple.server.RpcResponse;
import action.rpc.simple.util.HessianUtil;
import com.google.gson.*;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.UUID;

public class SerializationTest {

    RequestService service;

    @Before
    public void setUp() throws Exception {
        service = (RequestService) Proxy.newProxyInstance(RequestService.class.getClassLoader(), new Class[]{RequestService.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RequestDomain request = new RequestDomain();
                request.setRequestId(UUID.randomUUID().toString());
                request.setClassName(method.getDeclaringClass().getName());
                request.setMethodName(method.getName());
                request.setParameterTypes(method.getParameterTypes());
                request.setParameters(args);
                return request;
            }
        });
    }

    @Test
    public void testSerializeDeserialize() throws Exception {
//        RequestDomain request = service.getStatus(200);
//        byte[] data = HessianUtil.serialize(request);
//        byte[] data = new byte[] {77, 116, 0, 35, 97, 99, 116, 105, 111, 110, 46, 114, 112, 99, 46, 115, 105, 109, 112, 108, 101, 46, 115, 101, 114, 118, 101, 114, 46, 82, 112, 99, 82, 101, 113, 117, 101, 115, 116, 83, 0, 9, 114, 101, 113, 117, 101, 115, 116, 73, 100, 83, 0, 36, 48, 51, 99, 53, 49, 97, 55, 99, 45, 57, 56, 51, 52, 45, 52, 55, 102, 51, 45, 56, 50, 56, 52, 45, 53, 54, 99, 102, 51, 100, 56, 98, 48, 100, 50, 54, 83, 0, 9, 99, 108, 97, 115, 115, 78, 97, 109, 101, 83, 0, 37, 97, 99, 116, 105, 111, 110, 46, 114, 112, 99, 46, 115, 105, 109, 112, 108, 101, 46, 115, 101, 114, 118, 101, 114, 46, 72, 101, 108, 108, 111, 83, 101, 114, 118, 105, 99, 101, 83, 0, 10, 109, 101, 116, 104, 111, 100, 78, 97, 109, 101, 83, 0, 5, 104, 101, 108, 108, 111, 83, 0, 14, 112, 97, 114, 97, 109, 101, 116, 101, 114, 84, 121, 112, 101, 115, 86, 116, 0, 16, 91, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 67, 108, 97, 115, 115, 108, 0, 0, 0, 1, 77, 116, 0, 15, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 67, 108, 97, 115, 115, 83, 0, 4, 110, 97, 109, 101, 83, 0, 16, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 83, 116, 114, 105, 110, 103, 122, 122, 83, 0, 10, 112, 97, 114, 97, 109, 101, 116, 101, 114, 115, 86, 116, 0, 7, 91, 111, 98, 106, 101, 99, 116, 108, 0, 0, 0, 1, 83, 0, 5, 87, 111, 114, 108, 100, 122, 122};
//        RpcRequest result = (RpcRequest) HessianUtil.deserialize(data);

        byte[] data = new byte[] {77, 116, 0, 36, 97, 99, 116, 105, 111, 110, 46, 114, 112, 99, 46, 115, 105, 109, 112, 108, 101, 46, 115, 101, 114, 118, 101, 114, 46, 82, 112, 99, 82, 101, 115, 112, 111, 110, 115, 101, 83, 0, 9, 114, 101, 113, 117, 101, 115, 116, 73, 100, 83, 0, 36, 55, 54, 53, 51, 97, 100, 99, 52, 45, 53, 57, 53, 48, 45, 52, 50, 49, 55, 45, 57, 52, 97, 52, 45, 98, 57, 52, 55, 50, 99, 55, 97, 49, 55, 98, 57, 83, 0, 5, 101, 114, 114, 111, 114, 78, 83, 0, 7, 105, 115, 69, 114, 114, 111, 114, 70, 83, 0, 6, 114, 101, 115, 117, 108, 116, 83, 0, 12, 72, 101, 108, 108, 111, 33, 32, 87, 111, 114, 108, 100, 122};
        RpcResponse result = (RpcResponse) HessianUtil.deserialize(data);

        // TODO(特殊或复杂类型需要转换)
        // 异常：Attempted to serialize java.lang.Class: java.lang.String. Forgot to register a type adapter
        JsonSerializer jsonSerializer = new JsonSerializer<Class>() {
            @Override
            public JsonElement serialize(Class src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.getName());
            }
        };

        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, jsonSerializer).create();
        System.out.println(gson.toJson(result));
    }

}

interface RequestService {
    RequestDomain getRequest(String name);
    RequestDomain getStatus(int status);
}

class RequestDomain implements Serializable {

    private String requestId;
    private String className;
    private String methodName;
    private Class[] parameterTypes;
    private Object[] parameters;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}