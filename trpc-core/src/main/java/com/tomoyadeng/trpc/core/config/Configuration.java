package com.tomoyadeng.trpc.core.config;

import com.tomoyadeng.trpc.core.common.EndPoint;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.UnknownHostException;

@Data
@Slf4j
public class Configuration {
    private static final String LOCALHOST = "localhost";

    private ClientType clientType = ClientType.SIMPLE;
    private ServerType serverType = ServerType.SIMPLE;

    private String providerHost = LOCALHOST;
    private int providerPort = 8989;

    public enum ClientType {
        SIMPLE
    }

    public enum ServerType {
        SIMPLE
    }

    public EndPoint getProviderEndPoint() {
        String host = this.providerHost;
        if (LOCALHOST.equals(host)) {
            try {
                host = Inet4Address.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                log.error("getProviderEndPoint exception", e);
            }
        }
        return new EndPoint(host, this.providerPort);
    }
}
