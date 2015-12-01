package action.rpc.simple;

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
        RequestDomain request = service.getStatus(200);
        byte[] data = HessianUtil.serialize(request);
        RequestDomain result = (RequestDomain) HessianUtil.deserialize(data);

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