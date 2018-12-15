package com.tomoyadeng.trpc.sample.server;

import com.tomoyadeng.trpc.core.common.EndPoint;
import com.tomoyadeng.trpc.core.config.Configuration;
import com.tomoyadeng.trpc.core.registry.Registry;
import com.tomoyadeng.trpc.core.server.ServerStarter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class ServerBootstrap {
    @PostConstruct
    public void init() {
        start();
    }

    private void start() {
        Configuration configuration = new Configuration();
        try {
            Registry registry = Registry.build(configuration);
            registry.register(new EndPoint(configuration.getRegistryHost(), configuration.getRegistryPort()), null);

            ServerStarter starter = new ServerStarter(configuration, registry, "com.tomoyadeng.trpc.sample.server.impl");
            Executors.newSingleThreadExecutor().execute(starter::start);
        } catch (Exception e) {
            log.error("exception", e);
        }
    }
}
