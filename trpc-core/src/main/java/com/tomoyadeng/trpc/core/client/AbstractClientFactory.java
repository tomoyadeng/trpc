package com.tomoyadeng.trpc.core.client;

import com.tomoyadeng.trpc.core.common.EndPoint;
import com.tomoyadeng.trpc.core.config.Configuration;
import io.netty.channel.EventLoopGroup;

public abstract class AbstractClientFactory implements ClientFactory {

    protected Client newClient(EndPoint endPoint, EventLoopGroup group) {
        Client client;
        switch (this.getConfiguration().getClientType()) {
            case ASYNC:
                client = new AsyncClient(endPoint, group);
                break;
            case SIMPLE:
            default:
                client = new SimpleClient(endPoint, group);
        }
        return client;
    }

}
