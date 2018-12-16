package com.tomoyadeng.trpc.core.server;

import com.tomoyadeng.trpc.core.annotation.RpcService;
import com.tomoyadeng.trpc.core.common.EndPoint;
import com.tomoyadeng.trpc.core.config.Configuration;
import com.tomoyadeng.trpc.core.registry.Registry;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServerStarter implements Server {
    private Configuration configuration;
    private Registry registry;
    private String packagePath;

    private EndPoint endPoint;

    public ServerStarter(Configuration configuration, Registry registry, String packagePath) {
        this.configuration = configuration;
        this.endPoint = configuration.getProviderEndPoint();
        this.registry = registry;
        this.packagePath = packagePath;
    }

    private Server getServer(Map<String, Object> clazzMap) {
        if (this.configuration.getServerType() == Configuration.ServerType.SIMPLE) {
            return new SimpleServer(endPoint, clazzMap);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void start() {
        Reflections reflections = new Reflections(packagePath);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RpcService.class);

        Map<String, Object> serviceMap = new ConcurrentHashMap<>();
        classes.forEach(clazz -> {
            RpcService rpcService = clazz.getAnnotation(RpcService.class);
            Class<?> rpcClazz = rpcService.value();
            String clazzName = rpcClazz.getName();

            try {
                Object obj = clazz.getConstructor().newInstance();
                serviceMap.put(clazzName, obj);
                registry.register(endPoint, clazzName);
            } catch (Exception e) {
                log.error("exception in register class " + clazzName, e);
            }
        });

        Server server = getServer(serviceMap);
        server.start();
    }
}
