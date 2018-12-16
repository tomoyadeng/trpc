package com.tomoyadeng.trpc.sample.server;

import com.tomoyadeng.trpc.core.config.Configuration;
import com.tomoyadeng.trpc.core.registry.EtcdRegistry;
import com.tomoyadeng.trpc.core.registry.Registry;
import com.tomoyadeng.trpc.core.server.ServerStarter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class ServerBootstrap {

    @Value("${app.trpc.providerHost:#{null}}")
    private String providerHost;

    @Value("${app.trpc.providerPort:#{null}}")
    private String providerPort;

    @Value("${app.trpc.etcdRegistryAddr:#{null}}")
    private String etcdResgitryAddress;

    @PostConstruct
    public void init() {
        Configuration configuration = new Configuration();
        if (providerHost != null) {
            configuration.setProviderHost(providerHost);
        }

        if (providerPort != null) {
            configuration.setProviderPort(Integer.parseInt(providerPort));
        }

        Registry registry = etcdResgitryAddress == null ? new EtcdRegistry() : new EtcdRegistry(etcdResgitryAddress);
        start(configuration, registry);
    }

    private void start(Configuration configuration, Registry registry) {
        try {
            ServerStarter starter = new ServerStarter(configuration, registry, "com.tomoyadeng.trpc.sample.server.impl");
            Executors.newSingleThreadExecutor().execute(starter::start);
        } catch (Exception e) {
            log.error("exception", e);
        }
    }
}
