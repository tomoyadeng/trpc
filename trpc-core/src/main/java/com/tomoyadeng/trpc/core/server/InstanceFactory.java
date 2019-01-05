package com.tomoyadeng.trpc.core.server;

public interface InstanceFactory {
    <T> T newInstance(Class<T> clazz) throws Exception;
}
