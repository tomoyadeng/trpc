package com.tomoyadeng.trpc.core.client;

import io.netty.channel.Channel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public interface ResponseFuture<V> extends Future<V> {
    long getId();

    Channel getChannel();

    CompletableFuture<V> getCompletableFuture(ExecutorService executorService);

    V getUninterruptedly();
}
