package com.ciyfhx.main;

import com.ciyfhx.network.PacketDecoder;
import com.ciyfhx.network.PacketEncoder;
import com.ciyfhx.network.ServerInboundMessageProcessingHandler;
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

    private EventLoopGroup bossGroup, workerGroup;
    private boolean connected = false;

    public static void main(String[] args) throws Exception {
        var server = new WowChatServer();
        try{
            server.start().sync();
        }finally {
            server.stop();
        }
    }

    public ChannelFuture start() throws Exception{
        if(connected) throw new IllegalStateException("Server is already running!");
        connected = true;
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();

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

        return serverBootstrap.bind(PORT).sync().channel().closeFuture();
    }

    public void stop(){
        if(!connected) throw new IllegalStateException("Server is not running!");
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        connected = false;
    }


}
