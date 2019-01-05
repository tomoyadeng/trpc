package com.tomoyadeng.trpc.core.server;

public class DefaultInstanceFactory implements InstanceFactory {
    @Override
    public <T> T newInstance(Class<T> clazz) throws Exception {
        return clazz.getConstructor().newInstance();
    }
}
