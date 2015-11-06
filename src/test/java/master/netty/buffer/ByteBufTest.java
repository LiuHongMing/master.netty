package master.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;

/**
 * Created by liuhongming on 10/30/2015.
 */
public class ByteBufTest {

    Charset utf8 = CharsetUtil.UTF_8;

    ByteBuf buf;

    @Before
    public void setUp() throws Exception {
        buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
    }

    @Test
    public void testSlice() {
        ByteBuf slice = buf.slice(0, 15);
        System.out.println(slice.toString(utf8));

        buf.setByte(0, (byte) 'J');
        assert buf.getByte(0) == slice.getByte(0);
    }

    @Test
    public void testCopy() {
        ByteBuf copy = buf.copy(0, 15);
        System.out.println(copy.toString(utf8));

        buf.setByte(0, (byte) 'J');
        assert buf.getByte(0) == copy.getByte(0);
    }

    @Test
    public void testSetGet() {
        System.out.println((char) buf.getByte(0));

        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();

        buf.setByte(0, (byte) 'B');
        System.out.println((char) buf.getByte(0));

        assert readerIndex == buf.readerIndex();
        assert writerIndex == buf.writerIndex();
    }

    @Test
    public void testReadWrite() {
        System.out.println((char) buf.readByte());

        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();

        buf.writeByte((byte) '?');
        System.out.println(buf.toString(utf8));

        assert readerIndex == buf.readerIndex();
        assert writerIndex != buf.writerIndex();
    }
}
