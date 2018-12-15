package com.tomoyadeng.trpc.core.server;

import com.tomoyadeng.trpc.core.codec.TRpcDecoder;
import com.tomoyadeng.trpc.core.codec.TRpcEncoder;
import com.tomoyadeng.trpc.core.common.EndPoint;
import com.tomoyadeng.trpc.core.common.TRpcRequest;
import com.tomoyadeng.trpc.core.common.TRpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class SimpleServer implements Server {
    private EndPoint endPoint;
    private final Map<String, Object> clazzMap;

    public SimpleServer(String host, int port, Map<String, Object> clazzMap) {
        this(new EndPoint(host, port), clazzMap);
    }

    public SimpleServer(EndPoint endPoint, Map<String, Object> clazzMap) {
        this.endPoint = endPoint;
        this.clazzMap = clazzMap;
    }

    public void start() {
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new TRpcDecoder(TRpcRequest.class))
                                    .addLast(new TRpcEncoder(TRpcResponse.class))
                                    .addLast(new ServerHandler(clazzMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);

            log.info("Server start at {}", endPoint);
            ChannelFuture future = bootstrap.bind(endPoint.getHost(), endPoint.getPort()).sync();

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("exception", e);
        } finally {
            workerGroup.shutdownGracefully();
            boosGroup.shutdownGracefully();
        }
    }
}
