package com.tomoyadeng.trpc.core.registry;

import com.tomoyadeng.trpc.core.common.EndPoint;

import java.util.List;

public interface Registry {
    void register(EndPoint endPoint, String serviceName) throws Exception;

    List<EndPoint> discovery(String serviceName) throws Exception;
}
