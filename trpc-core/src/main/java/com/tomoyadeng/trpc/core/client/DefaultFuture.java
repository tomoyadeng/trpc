package com.tomoyadeng.trpc.core.client;

import com.tomoyadeng.trpc.core.common.TRpcRequest;
import com.tomoyadeng.trpc.core.common.TRpcResponse;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultFuture implements ResponseFuture<TRpcResponse> {
    private static final long DEFAULT_TIMEOUT = 1000;
    private static Map<Long, DefaultFuture> FUTURES = new ConcurrentHashMap<>();

    private final long id;
    private final TRpcRequest request;
    private volatile TRpcResponse response;
    private final Channel channel;

    private Lock lock = new ReentrantLock();
    private Condition done = lock.newCondition();

    private DefaultFuture(TRpcRequest request, Channel channel) {
        this.id = request.getId();
        this.request = request;
        this.channel = channel;
        FUTURES.put(id, this);
    }

    public static DefaultFuture newFuture(TRpcRequest request, Channel channel) {
        return new DefaultFuture(request, channel);
    }

    public static void receive(TRpcResponse response) {
        DefaultFuture future = FUTURES.remove(response.getId());
        if (future != null) {
            future.doReceived(response);
        }
    }

    private void doReceived(TRpcResponse res) {
        lock.lock();
        try {
            response = res;
            if (done != null) {
                done.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return FUTURES.remove(getId()) != null;
    }

    @Override
    public boolean isCancelled() {
        return FUTURES.get(getId()) == null;
    }

    public boolean isDone() {
        return response != null;
    }

    @Override
    public TRpcResponse get() throws InterruptedException, ExecutionException {
        try {
            return get0(DEFAULT_TIMEOUT);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TRpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Objects.requireNonNull(unit);
        return get0(unit.toMicros(timeout));
    }

    private TRpcResponse get0(long timeout) throws TimeoutException, InterruptedException {
        if (!isDone()) {
            long start = System.currentTimeMillis();
            lock.lock();
            try {
                while (!isDone()) {
                    done.await(timeout, TimeUnit.MICROSECONDS);
                    if (isDone() || System.currentTimeMillis() - start > timeout) {
                        break;
                    }
                }
            } finally {
                lock.unlock();
            }

            if (!isDone()) {
                throw new TimeoutException("timeout");
            }
        }

        if (response == null) {
            throw new IllegalStateException("response is null");
        }

        return response;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public CompletableFuture<TRpcResponse> getCompletableFuture(ExecutorService executorService) {
        return CompletableFuture.supplyAsync(this::getUninterruptedly, executorService);
    }

    @Override
    public TRpcResponse getUninterruptedly() {
        try {
            return get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
