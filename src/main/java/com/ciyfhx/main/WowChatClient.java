package com.ciyfhx.main;

import com.ciyfhx.chat.IChat;
import com.ciyfhx.network.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class WowChatClient {

    public interface ClientConnected {
        void connected(IChat chat);
    }

    private static Logger logger = LoggerFactory.getLogger(WowChatClient.class);

    private int PORT = 6000;
    private static String HOST = "localhost";

    private ClientConnected clientConnected;

    private ClientInboundMessageProcessingHandler inboundHandler;
    private ClientOutboundMessageProcessingHandler outboundHandler;
    private IChat chat;

    private EventLoopGroup workerGroup;


    public static void main(String[] args) throws Exception {
        String username = askForUsername();
        String chatGroupName = askForChatGroupName();
        var client = new WowChatClient();

        try{
            client.setClientConnected(chat -> {
                chat.sendUserInfo(username);
                chat.createGroup(chatGroupName);
            });

            client.start(HOST);
            listenForInput(client);
        }finally {
            client.stop();
        }

    }

    public void start(String host) throws Exception {
        workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        this.inboundHandler = new ClientInboundMessageProcessingHandler();
        this.outboundHandler = new ClientOutboundMessageProcessingHandler();
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(
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
            this.chat = inboundHandler.getChatHandler();
            if(clientConnected!=null)clientConnected.connected(this.chat);
        });


//            future.channel().closeFuture().sync();


    }

    public void stop() {
        workerGroup.shutdownGracefully();
        logger.info("Disconnected from server");
    }

    public void setClientConnected(ClientConnected clientConnected) {
        this.clientConnected = clientConnected;
    }

    public IChat getChat() {
        return chat;
    }

    public static void listenForInput(WowChatClient client) {
        var scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Please type your message:");
            if (scanner.hasNext()) {
                String messageToSend = scanner.nextLine();
                if (messageToSend.equals("exit")) break;
                client.chat.sendMessage(messageToSend);
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
