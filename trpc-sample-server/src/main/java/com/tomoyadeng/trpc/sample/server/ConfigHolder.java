package com.tomoyadeng.trpc.sample.server;

import com.tomoyadeng.trpc.core.config.Configuration;
import com.tomoyadeng.trpc.core.registry.EtcdRegistry;
import com.tomoyadeng.trpc.core.registry.Registry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class ConfigHolder {
    private Configuration configuration;

    @Value("${app.trpc.providerHost:#{null}}")
    private String providerHost;

    @Value("${app.trpc.providerPort:#{null}}")
    private String providerPort;

    @Value("${app.trpc.etcdRegistryAddr:#{null}}")
    private String etcdResgitryAddress;

    @PostConstruct
    public void init() {
        configuration = new Configuration();
        if (providerHost != null) {
            configuration.setProviderHost(providerHost);
        }

        if (providerPort != null) {
            configuration.setProviderPort(Integer.parseInt(providerPort));
        }

        Registry registry = etcdResgitryAddress == null ? new EtcdRegistry() : new EtcdRegistry(etcdResgitryAddress);
        configuration.setRegistry(registry);
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }
}
