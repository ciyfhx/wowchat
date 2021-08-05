package com.ciyfhx.main;

import com.ciyfhx.chat.network.PacketDecoder;
import com.ciyfhx.chat.network.PacketEncoder;
import com.ciyfhx.chat.security.ServerSecurityProvider;
import com.ciyfhx.chat.server.ServerInboundMessageProcessingHandler;
import com.ciyfhx.chat.server.ServerOutboundMessageProcessingHandler;
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
        System.setProperty("jdk.tls.server.protocols", "TLSv1.2");
        if(connected) throw new IllegalStateException("Server is already running!");
        connected = true;
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();

        ServerSecurityProvider.initSSLContext();
        var serverBootstrap = new ServerBootstrap();
        this.inboundHandler = new ServerInboundMessageProcessingHandler();
        this.outboundHandler = new ServerOutboundMessageProcessingHandler();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(
                                ServerSecurityProvider.getSSLHandler(),
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
