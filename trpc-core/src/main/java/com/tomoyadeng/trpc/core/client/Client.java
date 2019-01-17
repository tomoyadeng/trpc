package com.tomoyadeng.trpc.core.client;

import com.tomoyadeng.trpc.core.common.TRpcRequest;
import com.tomoyadeng.trpc.core.common.TRpcResponse;

import java.util.concurrent.CompletableFuture;

public interface Client {
    TRpcResponse send(TRpcRequest request) throws Exception;

    default CompletableFuture<TRpcResponse> sendAsync(TRpcRequest request) {
        CompletableFuture<TRpcResponse> future = new CompletableFuture<>();
        try {
            TRpcResponse response = send(request);
            future.complete(response);
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    void connect() throws InterruptedException;

    default CompletableFuture<Void> connectAsync() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            connect();
            future.complete(null);
        } catch (InterruptedException e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    void disconnect() throws InterruptedException;

    default CompletableFuture<Void> disconnectAsync() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            disconnect();
            future.complete(null);
        } catch (InterruptedException e) {
            future.completeExceptionally(e);
        }
        return future;
    }
}
