package action.rpc.simple.util;

import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

public class UnsafeSupport {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(UnsafeSupport.class);

    private static final Unsafe UNSAFE = initUnsafe();

    private static Unsafe initUnsafe() {
        try {
            java.lang.reflect.Field f = Unsafe.class
                    .getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            LOGGER.error("Get Unsafe instance occur error", e);
        }
        return Unsafe.getUnsafe();
    }

    public static Unsafe getInstance() {
        return UNSAFE;
    }
}
