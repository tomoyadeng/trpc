package com.tomoyadeng.trpc.core.client.loadbalance;

import com.tomoyadeng.trpc.core.common.EndPoint;
import com.tomoyadeng.trpc.core.common.TRpcRequest;

import java.util.List;
import java.util.Random;

public class RandomLBStrategy implements LBStrategy {
    private static final Random random = new Random();

    @Override
    public EndPoint pick(List<EndPoint> endPoints, TRpcRequest request) {
        return endPoints.get(random.nextInt(endPoints.size()));
    }
}
