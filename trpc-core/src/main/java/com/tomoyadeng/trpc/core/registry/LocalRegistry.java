package com.tomoyadeng.trpc.core.registry;

import com.tomoyadeng.trpc.core.common.EndPoint;

import java.util.List;

public class LocalRegistry implements Registry {
    private EndPoint endPoint;

    public LocalRegistry() {}

    public LocalRegistry(EndPoint endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    public void register(EndPoint endPoint, String serviceName) throws Exception {

    }

    @Override
    public List<EndPoint> discovery(String serviceName) throws Exception {
        return List.of(this.endPoint);
    }
}
