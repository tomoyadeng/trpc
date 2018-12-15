package com.tomoyadeng.trpc.sample.controller;


import com.tomoyadeng.trpc.sample.api.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/trpcsample/hello")
public class HelloController {

    @Autowired
    private HelloService helloService;

    @GetMapping("")
    public String hello() {
        return helloService.hello("hello");
    }
}
