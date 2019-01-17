package com.tomoyadeng.trpc.core.config;

import com.tomoyadeng.trpc.core.client.loadbalance.LoadBalanceStrategy;
import com.tomoyadeng.trpc.core.client.loadbalance.RandomLoadBalanceStrategy;
import com.tomoyadeng.trpc.core.common.EndPoint;
import com.tomoyadeng.trpc.core.registry.Registry;
import com.tomoyadeng.trpc.core.server.DefaultInstanceFactory;
import com.tomoyadeng.trpc.core.server.InstanceFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.UnknownHostException;

@Data
@Slf4j
public class Configuration {
    private static final String LOCALHOST = "localhost";

    private InstanceFactory instanceFactory = new DefaultInstanceFactory();
    private ClientType clientType = ClientType.SIMPLE;
    private ServerType serverType = ServerType.SIMPLE;
    private LoadBalanceStrategy loadBalanceStrategy = new RandomLoadBalanceStrategy();

    private String providerHost = LOCALHOST;
    private int providerPort = 8989;
    private Registry registry;

    public enum ClientType {
        SIMPLE,
        ASYNC
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
