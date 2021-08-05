package com.ciyfhx.main;

import com.ciyfhx.chat.client.ClientInboundMessageProcessingHandler;
import com.ciyfhx.chat.client.ClientOutboundMessageProcessingHandler;
import com.ciyfhx.chat.client.IChatLobby;
import com.ciyfhx.chat.network.*;
import com.ciyfhx.chat.security.ClientSecurityProvider;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;

public class WowChatClient {

    public interface ClientConnected {
        void connected(IChatLobby chat) throws IOException;
    }

    private static Logger logger = LoggerFactory.getLogger(WowChatClient.class);

    private int PORT = 6000;
    private static String HOST = "localhost";

    private ClientConnected clientConnected;

    private ClientInboundMessageProcessingHandler inboundHandler;
    private ClientOutboundMessageProcessingHandler outboundHandler;
    private IChatLobby chatLobby;

    private EventLoopGroup workerGroup;
    private boolean connected = false;


   public static void main(String[] args) throws Exception {
        String username = askForUsername();
        String chatGroupName = askForChatGroupName();
        var client = new WowChatClient();

        try{
            client.setClientConnected(chat -> {
                chat.sendUserInfo(username);
                chat.createChatGroup(chatGroupName);
            });

            client.start(HOST);
            listenForInput(client);
        }finally {
            client.stop();
        }

    }

    public ChannelFuture start(String host) throws Exception {
        System.setProperty("jdk.tls.server.protocols", "TLSv1.2");
        if(connected) throw new IllegalStateException("Already connected to server!");
        connected = true;
        workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

        ClientSecurityProvider.initSSLContext();
        this.inboundHandler = new ClientInboundMessageProcessingHandler();
        this.outboundHandler = new ClientOutboundMessageProcessingHandler();
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(
                        ClientSecurityProvider.getSSLHandler(),
                        new PacketEncoder(),
                        outboundHandler,
                        new PacketDecoder(),
                        inboundHandler
                );
            }
        });

        ChannelFuture future = bootstrap.connect(host, PORT).sync();
        future.addListener((ChannelFutureListener) channelFuture -> {
            logger.info("Connected to server");
            this.chatLobby = inboundHandler.getChatHandler();
            if(clientConnected!=null)clientConnected.connected(this.chatLobby);
        });
        return future;
    }

    public void stop() {
        if(!connected) throw new IllegalStateException("Not connected to server!");
        workerGroup.shutdownGracefully();
        logger.info("Disconnected from server");
        this.connected = false;
    }

    public void setClientConnected(ClientConnected clientConnected) {
        this.clientConnected = clientConnected;
    }

    public IChatLobby getChatLobby() {
        return chatLobby;
    }

    public static void listenForInput(WowChatClient client) {
        var scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Please type your message:");
            if (scanner.hasNext()) {
                String messageToSend = scanner.nextLine();
                if (messageToSend.equals("exit")) break;
//                client.chatLobby.sendMessage(messageToSend);
            }
        }
    }

    public static String askForUsername() {
        var scanner = new Scanner(System.in);
        System.out.println("Username:");
        String username = scanner.nextLine();
        return username;
    }

    public static String askForChatGroupName() {
        var scanner = new Scanner(System.in);
        System.out.println("Chat Group Name:");
        String chatGroupName = scanner.nextLine();
        return chatGroupName;
    }

}
