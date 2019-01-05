package com.tomoyadeng.trpc.sample.server;

import com.tomoyadeng.trpc.core.config.Configuration;
import com.tomoyadeng.trpc.core.server.ServerStarter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class ServerBootstrap {
    @Autowired
    private ConfigHolder configHolder;

    @PostConstruct
    public void init() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Configuration configuration = configHolder.getConfiguration();
        try {
            ServerStarter starter = new ServerStarter(configuration, configuration.getRegistry(), "com.tomoyadeng.trpc.sample.server.impl");
            executorService.execute(starter::start);
        } catch (Exception e) {
            log.error("exception", e);
        }
        finally {
            executorService.shutdown();
        }
    }
}
