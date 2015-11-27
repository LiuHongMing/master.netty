package master.netty.codec;

import action.rpc.simple.util.HessianUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcDecoder.class);

    private Class genericClass;

    public RpcDecoder(Class genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }

        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }

        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
        }

        byte[] data = new byte[dataLength];
        in.readBytes(data);

        LOGGER.info("{}", Arrays.toString(data));

        Object obj = HessianUtil.deserialize(data);
        out.add(obj);
    }
}
