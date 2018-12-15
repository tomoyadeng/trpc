package com.tomoyadeng.trpc.core.client;

import com.tomoyadeng.trpc.core.codec.TRpcDecoder;
import com.tomoyadeng.trpc.core.codec.TRpcEncoder;
import com.tomoyadeng.trpc.core.common.EndPoint;
import com.tomoyadeng.trpc.core.common.TRpcRequest;
import com.tomoyadeng.trpc.core.common.TRpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleClient extends SimpleChannelInboundHandler<TRpcResponse> implements Client{
    private EndPoint endPoint;
    private EventLoopGroup group;

    private TRpcResponse response;
    private final Object obj = new Object();
    private ChannelFuture channelFuture;

    public SimpleClient(String host, int port) {
        this(new EndPoint(host, port), null);
    }

    public SimpleClient(EndPoint endPoint) {
        this(endPoint, null);
    }

    public SimpleClient(EndPoint endPoint, EventLoopGroup group) {
        this.endPoint = endPoint;
        this.group = group == null ? new NioEventLoopGroup() : group;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TRpcResponse msg) throws Exception {
        this.response = msg;
        log.info("receive rsp id={}, result={}", msg.getId(), msg.getResult());
        synchronized (obj) {
            obj.notifyAll();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client caught exception", cause);
        ctx.close();
    }

    @Override
    public TRpcResponse send(TRpcRequest request) throws Exception {
        if (channelFuture == null) {
            connect();
        }

        channelFuture.channel().writeAndFlush(request).sync();

        synchronized (obj) {
            obj.wait();
        }

        return response;
    }

    @Override
    public void connect() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new TRpcEncoder(TRpcRequest.class))
                                .addLast(new TRpcDecoder(TRpcResponse.class))
                                .addLast(SimpleClient.this);
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true);

        channelFuture = bootstrap.connect(endPoint.getHost(), endPoint.getPort()).sync();
    }

    @Override
    public void disconnect() throws InterruptedException {
        group.shutdownGracefully();
    }
}
