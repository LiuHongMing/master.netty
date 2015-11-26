package action.rpc.simple.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import com.google.common.collect.Maps;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import java.util.Map;

/**
 * <pre>
 *     序列化工具
 * </pre>
 *
 * @author J.Ming
 */
public class ProtoStuffSerializeUtil {

    private static Map<Class<?>, Schema<?>> cacheSchema = Maps.newConcurrentMap();

    private static Objenesis objenesis = new ObjenesisStd(true);

    private static <T> Schema<T> getSchema(Class<T> targetClass) {
        Schema<T> schema = (Schema<T>) cacheSchema.get(targetClass);
        if (schema == null) {
            schema = RuntimeSchema.getSchema(targetClass);
            if (schema != null)
                cacheSchema.put(targetClass, schema);
        }
        return schema;
    }

    public static <T> byte[] serialize(T obj) {
        if (obj == null) {
            throw new RuntimeException("obj == null");
        }
        Schema schema = getSchema(obj.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            return ProtobufIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    public static <T> T deserialize(byte[] data, Class<T> targetClass) {
        if (data == null || data.length == 0) {
            throw new RuntimeException("byte[] is empty");
        }
        try {
            T obj = objenesis.newInstance(targetClass);
            Schema schema = getSchema(targetClass);
            ProtostuffIOUtil.mergeFrom(data, obj, schema);
            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
