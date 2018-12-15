package com.tomoyadeng.trpc.core.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EndPoint {
    private String host;
    private int port;

    public String toString() {
        return host + ":" + port;
    }
}
