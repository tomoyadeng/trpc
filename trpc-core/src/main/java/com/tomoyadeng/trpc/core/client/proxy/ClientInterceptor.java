package com.tomoyadeng.trpc.core.client.proxy;

import com.tomoyadeng.trpc.core.client.Client;
import com.tomoyadeng.trpc.core.client.ClientFactory;
import com.tomoyadeng.trpc.core.common.IdGenerator;
import com.tomoyadeng.trpc.core.common.SimpleIdGenerator;
import com.tomoyadeng.trpc.core.common.TRpcRequest;
import com.tomoyadeng.trpc.core.common.TRpcResponse;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class ClientInterceptor implements MethodInterceptor {
    private static final IdGenerator SHARED_ID_GENERATOR = new SimpleIdGenerator();
    private ClientFactory clientFactory;
    private IdGenerator idGenerator;

    public ClientInterceptor(ClientFactory clientFactory) {
        this(clientFactory, SHARED_ID_GENERATOR);
    }

    public ClientInterceptor(ClientFactory clientFactory, IdGenerator idGenerator) {
        this.clientFactory = clientFactory;
        this.idGenerator = idGenerator;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        TRpcRequest request = new TRpcRequest();
        Class clazz = method.getDeclaringClass();
        String clazzName = clazz.getName();

        request.setId(idGenerator.nextId());
        request.setClassName(clazzName);
        request.setMethodName(method.getName());
        request.setParamTypes(method.getParameterTypes());
        request.setParams(args);

        Client client = clientFactory.getClient(request);

        TRpcResponse response = client.send(request);
        if (response.hasException()) {
            throw response.getException();
        }
        return response.getResult();
    }
}
