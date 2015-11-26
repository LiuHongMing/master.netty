package action.rpc.simple.util;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * <pre>
 *     Hessian序列化
 * </pre>
 *
 * @author J.Ming
 */
public class HessianUtil {

    /**
     * 序列化
     *
     * @param obj
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> byte[] serialize(Object obj) throws IOException {
        if (obj == null) throw new NullPointerException();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(os);
        ho.writeObject(obj);
        return os.toByteArray();
    }

    /**
     * 反序列化
     *
     * @param data
     * @return
     * @throws IOException
     */
    public static Object deserialize(byte[] data) throws IOException {
        if (data == null || data.length == 0) throw new NullPointerException();

        ByteArrayInputStream is = new ByteArrayInputStream(data);
        HessianInput hi = new HessianInput(is);
        return hi.readObject();
    }

}
