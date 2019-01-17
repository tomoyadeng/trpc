package com.tomoyadeng.trpc.core.client;

import com.tomoyadeng.trpc.core.codec.TRpcDecoder;
import com.tomoyadeng.trpc.core.codec.TRpcEncoder;
import com.tomoyadeng.trpc.core.common.EndPoint;
import com.tomoyadeng.trpc.core.common.TRpcRequest;
import com.tomoyadeng.trpc.core.common.TRpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class AsyncClient extends SimpleChannelInboundHandler<TRpcResponse> implements Client {
    private EndPoint endPoint;
    private EventLoopGroup group;
    private Channel channel;

    public AsyncClient(EndPoint endPoint, EventLoopGroup group) {
        this.endPoint = endPoint;
        this.group = group;
    }

    @Override
    public TRpcResponse send(TRpcRequest request) throws Exception {
        return sendAsync(request).get();
    }

    @Override
    public CompletableFuture<TRpcResponse> sendAsync(TRpcRequest request) {
        if (channel == null) {
            try {
                connect();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        DefaultFuture future = DefaultFuture.newFuture(request, this.channel);
        channel.writeAndFlush(request);
        return CompletableFuture.supplyAsync(future::get, group);
    }

    @Override
    public void connect() throws InterruptedException {
        try {
            connectAsync().get();
        } catch (ExecutionException e) {
            log.warn("ExecutionException happened", e);
        }
    }

    @Override
    public CompletableFuture<Void> connectAsync() {
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new TRpcEncoder(TRpcRequest.class))
                                .addLast(new TRpcDecoder(TRpcResponse.class))
                                .addLast(AsyncClient.this);
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture channelFuture = bootstrap.connect(endPoint.getHost(), endPoint.getPort());
        this.channel = channelFuture.channel();

        return CompletableFuture.runAsync(() -> {
            try {
                channelFuture.sync();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }, group);
    }

    @Override
    public void disconnect() throws InterruptedException {
        channel.close().sync();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TRpcResponse msg) throws Exception {
        DefaultFuture.receive(msg);
    }
}
