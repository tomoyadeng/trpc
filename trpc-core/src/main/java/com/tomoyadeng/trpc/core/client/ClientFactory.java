package com.tomoyadeng.trpc.core.client;

import com.tomoyadeng.trpc.core.config.Configuration;

public interface ClientFactory {
    Configuration getConfiguration();

    Client getClient(String serviceName);

    void init();
}
