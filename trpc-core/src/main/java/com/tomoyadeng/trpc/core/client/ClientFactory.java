package com.tomoyadeng.trpc.core.client;

import com.tomoyadeng.trpc.core.common.TRpcRequest;
import com.tomoyadeng.trpc.core.config.Configuration;

public interface ClientFactory {
    Configuration getConfiguration();

    Client getClient(TRpcRequest request);

    void init();
}
