package com.tomoyadeng.trpc.sample.config;

import com.tomoyadeng.trpc.core.client.ClientFactory;
import com.tomoyadeng.trpc.core.client.DefaultClientFactory;
import com.tomoyadeng.trpc.core.client.proxy.ClientProxy;
import com.tomoyadeng.trpc.core.common.EndPoint;
import com.tomoyadeng.trpc.core.config.Configuration;
import com.tomoyadeng.trpc.core.registry.LocalRegistry;
import com.tomoyadeng.trpc.core.registry.Registry;
import com.tomoyadeng.trpc.sample.api.HelloService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;


@org.springframework.context.annotation.Configuration
public class ClientConfiguration {

    @Bean
    public ClientFactory clientFactory() {
        Configuration configuration = new Configuration();
        Registry registry = registry(configuration);
        ClientFactory clientFactory = new DefaultClientFactory(configuration, registry, "com.tomoyadeng.trpc.sample.api");
        clientFactory.init();
        return clientFactory;
    }

    @Bean
    public Registry registry(Configuration configuration) {
        return new LocalRegistry(new EndPoint(configuration.getRegistryHost(), configuration.getRegistryPort()));
    }

    @Bean
    @Scope(scopeName = "prototype")
    public HelloService helloService(ClientFactory clientFactory) {
        return ClientProxy.create(HelloService.class, clientFactory);
    }
}
