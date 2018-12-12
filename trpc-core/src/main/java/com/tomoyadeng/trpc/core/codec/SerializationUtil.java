package com.tomoyadeng.trpc.core.codec;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.experimental.UtilityClass;


@UtilityClass
public class SerializationUtil {

    public static <T> byte[] serialize(T o) {
        Schema schema = RuntimeSchema.getSchema(o.getClass());
        return ProtostuffIOUtil.toByteArray(o, schema, LinkedBuffer.allocate());
    }

    public static <T> T desrialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.createFrom(clazz);
        T message = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, message, schema);
        return message;
    }
}
