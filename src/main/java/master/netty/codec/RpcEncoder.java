package master.netty.codec;

import action.rpc.simple.util.HessianUtil;
import action.rpc.simple.util.ProtoStuffSerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder {

    private Class genericClass;

    public RpcEncoder(Class genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            byte[] data = HessianUtil.serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }

}
