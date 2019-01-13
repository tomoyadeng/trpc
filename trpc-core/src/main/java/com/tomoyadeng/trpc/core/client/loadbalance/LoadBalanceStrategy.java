package com.tomoyadeng.trpc.core.client.loadbalance;

import com.tomoyadeng.trpc.core.common.EndPoint;
import com.tomoyadeng.trpc.core.common.TRpcRequest;

import java.util.List;

public interface LoadBalanceStrategy {
    EndPoint pick(List<EndPoint> endPoints, TRpcRequest request);
}
