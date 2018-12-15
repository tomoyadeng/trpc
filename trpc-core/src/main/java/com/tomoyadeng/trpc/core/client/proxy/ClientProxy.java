package com.tomoyadeng.trpc.core.client.proxy;

import com.tomoyadeng.trpc.core.client.ClientFactory;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;

@Slf4j
public class ClientProxy {
    public static <T> T create(Class<T> clazz, ClientFactory clientFactory) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new ClientInterceptor(clientFactory));
        return (T) enhancer.create();
    }
}
