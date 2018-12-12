package com.tomoyadeng.trpc.sample.client;

import com.tomoyadeng.trpc.core.client.Client;
import com.tomoyadeng.trpc.core.common.TRpcRequest;
import com.tomoyadeng.trpc.core.common.TRpcResponse;
import com.tomoyadeng.trpc.sample.api.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class ClientBootstrap {
    private Client client;

    @PostConstruct
    public void init() {
        Client client = new Client("localhost", 8989);
        this.client = client;

        Executors.newSingleThreadExecutor().execute(() -> {
            for (int i = 0; i < 10; i++) {
                TRpcRequest request = new TRpcRequest();
                request.setId(i);
                request.setClassName(HelloService.class.getName());
                request.setMethodName(HelloService.class.getDeclaredMethods()[0].getName());
                request.setParamTypes(new Class[]{String.class});
                request.setParams(new Object[]{"hello"});

                send(request);
            }
        });
    }

    private void send(TRpcRequest request) {
        try {
            log.info("send request..");
            TRpcResponse response = client.send(request);
            log.info("receive rsp id={}, result={}", response.getId(), response.getResult());
        } catch (Exception e) {
            log.error("exception", e);
        }
    }
}
