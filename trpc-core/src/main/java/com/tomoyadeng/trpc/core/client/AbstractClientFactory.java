package com.tomoyadeng.trpc.core.client;

import com.tomoyadeng.trpc.core.common.EndPoint;
import com.tomoyadeng.trpc.core.config.Configuration;
import io.netty.channel.EventLoopGroup;

public abstract class AbstractClientFactory implements ClientFactory {

    protected Client newClient(EndPoint endPoint, EventLoopGroup group) {
        if (this.getConfiguration().getClientType() == Configuration.ClientType.SIMPLE) {
            return new SimpleClient(endPoint, group);
        }
        throw new IllegalArgumentException();
    }

}
