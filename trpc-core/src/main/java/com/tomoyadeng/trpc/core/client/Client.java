package com.tomoyadeng.trpc.core.client;

import com.tomoyadeng.trpc.core.common.TRpcRequest;
import com.tomoyadeng.trpc.core.common.TRpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public interface Client {
    ExecutorService getExecutor();

    TRpcResponse send(TRpcRequest request) throws Exception;

    default CompletableFuture<TRpcResponse> sendAsync(TRpcRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return send(request);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, getExecutor());
    }

    void connect() throws InterruptedException;

    default CompletableFuture<Void> connectAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                connect();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, getExecutor());
    }

    void disconnect() throws InterruptedException;

    default CompletableFuture<Void> disconnectAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                disconnect();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, getExecutor());
    }
}
