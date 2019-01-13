package com.tomoyadeng.trpc.core.client.proxy;

import com.tomoyadeng.trpc.core.client.ClientFactory;
import com.tomoyadeng.trpc.core.common.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

@Slf4j
public class ClientProxy {
    public static <T> T create(Class<T> clazz, ClientFactory clientFactory) {
        return create(clazz, new ClientInterceptor(clientFactory));
    }

    public static <T> T create(Class<T> clazz, ClientFactory clientFactory, IdGenerator idGenerator) {
        return create(clazz, new ClientInterceptor(clientFactory, idGenerator));
    }

    private static <T> T create(Class<?> clazz, MethodInterceptor interceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(interceptor);
        return (T) enhancer.create();
    }
}
