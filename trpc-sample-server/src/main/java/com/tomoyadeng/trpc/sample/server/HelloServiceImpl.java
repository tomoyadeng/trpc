package com.tomoyadeng.trpc.sample.server;

import com.tomoyadeng.trpc.sample.api.HelloService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String s) {
        return "server: " + s;
    }
}
