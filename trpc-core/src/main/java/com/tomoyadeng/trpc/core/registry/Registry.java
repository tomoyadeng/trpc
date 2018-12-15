package com.tomoyadeng.trpc.core.registry;

import com.tomoyadeng.trpc.core.common.EndPoint;
import com.tomoyadeng.trpc.core.config.Configuration;

import java.util.List;

public interface Registry {
    void register(EndPoint endPoint, String serviceName) throws Exception;

    List<EndPoint> discovery(String serviceName) throws Exception;

    static Registry build(Configuration configuration) throws Exception {
        String type = configuration.getRegistryType();
        Class<?> clazz = Class.forName(type);
        Object obj = clazz.getConstructor().newInstance();
        if (obj instanceof Registry) {
            return (Registry) obj;
        }
        return null;
    }
}
