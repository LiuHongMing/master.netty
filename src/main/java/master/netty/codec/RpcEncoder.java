package master.netty.codec;

import action.rpc.simple.util.HessianUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class RpcEncoder extends MessageToByteEncoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcEncoder.class);

    private Class genericClass;

    public RpcEncoder(Class genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            byte[] data = HessianUtil.serialize(in);
            LOGGER.info("{}", Arrays.toString(data));
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }

}
