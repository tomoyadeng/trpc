package com.tomoyadeng.trpc.sample.server;

import com.tomoyadeng.trpc.core.server.Server;
import com.tomoyadeng.trpc.sample.api.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class ServerBootstrap {
    @PostConstruct
    public void init() {
        start();
    }

    private void start() {
        Map<String, Object> clazzMap = new HashMap<>();
        clazzMap.put(HelloService.class.getName(), new HelloServiceImpl());

        Server server = new Server("localhost", 8989, clazzMap);
        log.warn("Start server on {}:{}", "localhost", 8989);
        Executors.newSingleThreadExecutor().execute(server::start);
    }
}
