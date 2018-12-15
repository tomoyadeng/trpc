package com.tomoyadeng.trpc.sample.server.impl;

import com.tomoyadeng.trpc.core.annotation.RpcService;
import com.tomoyadeng.trpc.sample.api.HelloService;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.UnknownHostException;

@Slf4j
@RpcService
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String s) {

        return "[" + getHostName() + "]: " + s;
    }

    private String getHostName() {
        try {
            return Inet4Address.getLocalHost().toString();
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }
}
