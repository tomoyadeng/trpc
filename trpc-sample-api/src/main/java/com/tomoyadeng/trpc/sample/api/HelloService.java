package com.tomoyadeng.trpc.sample.api;

import com.tomoyadeng.trpc.core.annotation.RpcApi;

@RpcApi
public interface HelloService {
    String hello(String hello);
}
