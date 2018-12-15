package com.tomoyadeng.trpc.core.client.proxy;

import com.tomoyadeng.trpc.core.client.Client;
import com.tomoyadeng.trpc.core.client.ClientFactory;
import com.tomoyadeng.trpc.core.common.TRpcRequest;
import com.tomoyadeng.trpc.core.common.TRpcResponse;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class ClientInterceptor implements MethodInterceptor {
    private ClientFactory clientFactory;

    public ClientInterceptor(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        TRpcRequest request = new TRpcRequest();
        Class clazz = method.getDeclaringClass();
        String clazzName = clazz.getName();

        request.setId(System.currentTimeMillis());
        request.setClassName(clazzName);
        request.setMethodName(method.getName());
        request.setParamTypes(method.getParameterTypes());
        request.setParams(args);

        Client client = clientFactory.getClient(clazzName);

        TRpcResponse response = client.send(request);
        if (response.hasException()) {
            throw response.getException();
        }
        return response.getResult();
    }
}
