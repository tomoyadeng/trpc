package com.tomoyadeng.trpc.core.client;

        import com.tomoyadeng.trpc.core.common.TRpcRequest;
        import com.tomoyadeng.trpc.core.common.TRpcResponse;

public interface Client {
    TRpcResponse send(TRpcRequest request) throws Exception;

    void connect() throws InterruptedException;

    void disconnect() throws InterruptedException;
}
