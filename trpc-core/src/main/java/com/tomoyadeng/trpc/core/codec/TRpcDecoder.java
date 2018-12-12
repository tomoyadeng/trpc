package com.tomoyadeng.trpc.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class TRpcDecoder extends ByteToMessageDecoder {
    private Class<?> targetClazz;

    public TRpcDecoder(Class<?> targetClazz) {
        this.targetClazz = targetClazz;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLen = in.readInt();
        if (dataLen < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLen) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLen];
        in.readBytes(data);
        Object obj = SerializationUtil.desrialize(data, targetClazz);
        out.add(obj);
    }
}
