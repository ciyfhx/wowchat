package com.ciyfhx.network;

import io.netty.channel.*;

@ChannelHandler.Sharable
public class ServerOutboundMessageProcessingHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        promise.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        super.write(ctx, msg, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
