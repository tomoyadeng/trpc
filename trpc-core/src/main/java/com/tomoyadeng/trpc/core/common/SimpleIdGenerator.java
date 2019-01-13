package com.tomoyadeng.trpc.core.common;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleIdGenerator implements IdGenerator {
    private AtomicLong generator;

    public SimpleIdGenerator() {
        generator = new AtomicLong();
        generator.set(new Random().nextInt());
    }

    @Override
    public long nextId() {
        return generator.incrementAndGet();
    }
}
