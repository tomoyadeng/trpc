package com.tomoyadeng.trpc.core.client;

import com.tomoyadeng.trpc.core.annotation.RpcApi;
import com.tomoyadeng.trpc.core.common.EndPoint;
import com.tomoyadeng.trpc.core.common.TRpcRequest;
import com.tomoyadeng.trpc.core.config.Configuration;
import com.tomoyadeng.trpc.core.registry.Registry;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Slf4j
public class DefaultClientFactory extends AbstractClientFactory {

    private Configuration configuration;
    private Registry registry;
    private String packagePath;

    private EventLoopGroup group;
    private ConcurrentMap<String, List<EndPoint>> serviceMap;
    private ConcurrentMap<EndPoint, Client> clientPool;

    public DefaultClientFactory(Configuration configuration, Registry registry, String packagePath) {
        this.configuration = configuration;
        this.registry = registry;
        this.packagePath = packagePath;
        this.group = new NioEventLoopGroup();
        this.serviceMap = new ConcurrentHashMap<>();
        this.clientPool = new ConcurrentHashMap<>();
    }

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    @Override
    public Client getClient(TRpcRequest request) {
        String serviceName = request.getClassName();
        List<EndPoint> endPoints = serviceMap.get(serviceName);
        if (endPoints == null || endPoints.size() == 0) {
            throw new IllegalStateException(serviceName + " not find");
        }

        EndPoint endPoint = getConfiguration().getLbStrategy().pick(endPoints, request);

        Client client = clientPool.get(endPoint);
        if (client != null) {
            return client;
        }
        return newClient(endPoint, this.group);
    }

    @Override
    public void init() {
        Reflections reflections = new Reflections(packagePath);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RpcApi.class);

        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(2);
        classes.forEach(clazz -> executorService.scheduleAtFixedRate(() -> {
            try {
                if (clazz.isInterface()) {
                    String className = clazz.getName();
                    List<EndPoint> endPoints = registry.discovery(className);
                    log.info("discovery {} endPoints for {}", endPoints.size(), className);
                    serviceMap.put(className, endPoints);
                    endPoints.forEach(endPoint -> {
                        if (clientPool.get(endPoint) == null) {
                            Client client = newClient(endPoint, this.group);
                            clientPool.put(endPoint, client);
                        }
                    });
                }
            } catch (Exception e) {
                log.error("exception in register clazz " + clazz.getName(), e);
            }
        }, 0, 60, TimeUnit.SECONDS));
    }
}
