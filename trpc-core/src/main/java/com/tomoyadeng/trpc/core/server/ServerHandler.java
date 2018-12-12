package com.tomoyadeng.trpc.core.server;

import com.tomoyadeng.trpc.core.common.TRpcRequest;
import com.tomoyadeng.trpc.core.common.TRpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.io.IOException;
import java.util.Map;

@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<TRpcRequest> {
    private final Map<String, Object> clazzMap;

    public ServerHandler(Map<String, Object> clazzMap) {
        this.clazzMap = clazzMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("connected!");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TRpcRequest msg) throws Exception {
        log.info("receive a request, id={}", msg.getId());
        TRpcResponse response = getResponse(msg);

        ctx.writeAndFlush(response).addListener((GenericFutureListener<ChannelFuture>) future -> {
            if (!future.isSuccess()) {
                log.error("exception", future.cause());
            }
        });
    }

    private TRpcResponse getResponse(TRpcRequest request) {
        TRpcResponse response = new TRpcResponse();
        response.setId(request.getId());

        try {
            Object target = clazzMap.get(request.getClassName());
            if (target == null) {
                throw new IllegalArgumentException(request.getClassName() + " not registered");
            }

            Class<?> clazz = Class.forName(request.getClassName());
            FastClass fastClass = FastClass.create(clazz);
            FastMethod fastMethod = fastClass.getMethod(request.getMethodName(), request.getParamTypes());
            Object result = fastMethod.invoke(target, request.getParams());
            response.setResult(result);

        } catch (Exception e) {
            response.setException(e);
            log.error("invoke exception", e);
        }
        return response;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            log.info("exception", cause);
        }
        super.exceptionCaught(ctx, cause);
    }
}
