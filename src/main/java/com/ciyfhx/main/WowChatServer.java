package com.ciyfhx.main;

import com.ciyfhx.network.PacketEncoder;
import com.ciyfhx.network.ServerInboundMessageProcessingHandler;
import com.ciyfhx.network.PacketDecoder;
import com.ciyfhx.network.ServerOutboundMessageProcessingHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class WowChatServer {

    private int PORT = 6000;
    private ServerInboundMessageProcessingHandler inboundHandler;
    private ServerOutboundMessageProcessingHandler outboundHandler;

    public static void main(String[] args) throws Exception {
        new WowChatServer().run();
    }

    public void run() throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            var serverBootstrap = new ServerBootstrap();
            this.inboundHandler = new ServerInboundMessageProcessingHandler();
            this.outboundHandler = new ServerOutboundMessageProcessingHandler();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new PacketEncoder(),
                                    outboundHandler,
                                    new PacketDecoder(),
                                    inboundHandler
                                    );
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = serverBootstrap.bind(PORT).sync();
            future.channel().closeFuture().sync();

        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

}
