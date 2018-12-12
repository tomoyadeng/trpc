package com.tomoyadeng.trpc.core.common;

import lombok.Data;

@Data
public class TRpcRequest {
    private long id;
    private String className;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] params;
}
