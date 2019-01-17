package com.tomoyadeng.trpc.core.client;

import com.tomoyadeng.trpc.core.common.TRpcRequest;
import com.tomoyadeng.trpc.core.common.TRpcResponse;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultFuture {
    public static final int DEFAULT_TIMEOUT = 1000;
    private static Map<Long, DefaultFuture> FUTURES = new ConcurrentHashMap<>();

    private final long id;
    private final TRpcRequest request;
    private volatile TRpcResponse response;
    private final Channel channel;

    private Lock lock = new ReentrantLock();
    private Condition done = lock.newCondition();

    public DefaultFuture(TRpcRequest request, Channel channel) {
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

    public boolean isDone() {
        return response != null;
    }

    public TRpcResponse get() {
        try {
            return get0(DEFAULT_TIMEOUT);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private TRpcResponse get0(int timeout) throws TimeoutException {
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
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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
}
