package com.tomoyadeng.trpc.core.config;

import lombok.Data;

@Data
public class Configuration {
    private ClientType clientType = ClientType.SIMPLE;
    private ServerType serverType = ServerType.SIMPLE;

    private String registryType = "com.tomoyadeng.trpc.core.registry.LocalRegistry";
    private String registryHost = "localhost";
    private int registryPort = 8989;

    public enum ClientType {
        SIMPLE
    }

    public enum ServerType {
        SIMPLE
    }
}
