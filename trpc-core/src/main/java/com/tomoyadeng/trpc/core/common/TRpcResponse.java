package com.tomoyadeng.trpc.core.common;

import lombok.Data;

@Data
public class TRpcResponse {
    private long id;
    private Throwable exception;
    private Object result;

    public boolean hasException() {
        return exception != null;
    }
}
