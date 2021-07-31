package com.ciyfhx.chat.server;

import com.ciyfhx.chat.ChatGroupNotFoundException;
import com.ciyfhx.chat.network.Packet;
import com.ciyfhx.chat.packets.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@ChannelHandler.Sharable
public class ServerInboundMessageProcessingHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(ServerInboundMessageProcessingHandler.class);
    private ChatManager chatManager;

    private LinkedBlockingQueue<ServerPacket> backlog = new LinkedBlockingQueue<>(100);
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    public ServerInboundMessageProcessingHandler() {
        chatManager = new ChatManager();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("New connection receive from " + ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        removeUserFromChannelIfExist(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Packet packet = (Packet) msg;
        ServerUser sender = chatManager.getUserFromChannel(ctx.channel());
        var serverPacket = new ServerPacket(packet, sender);
        try{
            backlog.offer(serverPacket);
            executorService.submit(() -> {
                try {
                    processPacket(ctx, backlog.take());
                } catch (InterruptedException | ChatGroupNotFoundException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
            });
        }catch (IllegalStateException e){
            logger.warn("Exceeded maximum packets backlog, dropping packet");
        }


    }

    private void processPacket(ChannelHandlerContext ctx, ServerPacket serverPacket) throws ChatGroupNotFoundException {
        Packet packet = serverPacket.getPacket();
        ServerUser sender = serverPacket.getSender();
        if (packet instanceof NewUserPacket userPacket) {
            chatManager.createUser(userPacket.getUsername(), ctx.channel());
            logger.info("New User from " + userPacket.getUsername() + "[" + ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().toString() + "]");
        } else if (packet instanceof SendMessagePacket messagePacket) {
            logger.info("[" + sender.getUsername() + "]: " + messagePacket.getMessage());
            ServerChatGroup serverChatGroup = chatManager.getChatGroupFromId(messagePacket.getChatGroupId());
            serverChatGroup.sendMessageInGroup(sender, messagePacket.getMessage());
        } else if (packet instanceof NewChatGroupPacket chatGroupPacket) {
            var chatGroup = chatManager.createChatGroup(chatGroupPacket.getChatGroupName());
            chatGroup.joinChatGroup(sender);

            logger.info("New chat group: " + chatGroupPacket.getChatGroupName());

//            // Send Chat Group id
//            sendJoinedChatGroupPacket(ctx, user, chatGroup);


        } else if (packet instanceof JoinChatGroupPacket joinChatGroupPacket) {
            var chatGroup = chatManager.getChatGroupFromId(joinChatGroupPacket.getChatGroupIdToJoin());
            chatGroup.joinChatGroup(sender);

//            // Send Chat Group id
//            sendJoinedChatGroupPacket(ctx, user, chatGroup);
            logger.info(sender.getUsername() + " joined chat group: " + chatGroup.getChatGroupName());

        }else if (packet instanceof LeaveChatGroupPacket leaveChatGroupPacket){
            var chatGroup = chatManager.getChatGroupFromId(leaveChatGroupPacket.getChatGroupIdToLeave());
            chatGroup.leaveChatGroup(sender);

            logger.info(sender.getUsername() + " left chat group: " + chatGroup.getChatGroupName());
        }else if (packet instanceof GetChatGroupsIdsPacket) {
            var listChatGroupsIdsPacket = new ListChatGroupIdsPacket();
            listChatGroupsIdsPacket.setChatGroups(chatManager.getAvailableChatGroups());

            ctx.writeAndFlush(listChatGroupsIdsPacket);
        }
    }

//    private void sendJoinedChatGroupPacket(ChannelHandlerContext ctx, User user, ServerChatGroup chatGroup) {
//        var joinedChatGroupPacket = new JoinedChatGroupPacket();
//        joinedChatGroupPacket.setChatGroup(chatGroup);
//        var future = ctx.writeAndFlush(joinedChatGroupPacket);
//
//        future.addListener(_future -> {
//            chatGroup.sendServerMessage(user.getUsername() + " has joined the group");
//
//            //Send users inside chat group
//            var listOfUserInGroupChat = new ListOfUsersInChatGroupPacket();
//            listOfUserInGroupChat.setUsers(chatGroup.getUsers());
//            ctx.writeAndFlush(listOfUserInGroupChat);
//        });
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        removeUserFromChannelIfExist(ctx.channel());
        logger.error(cause.getMessage());
    }

    private void removeUserFromChannelIfExist(Channel channel) {
        // Remove user if there is an exception
        try {
            ServerUser user = chatManager.getUserFromChannel(channel);
            if(user == null) logger.error("Connection removed without user");
            else{
                logger.error("User removed " + user.getUsername() + "[" + ((InetSocketAddress) channel.remoteAddress()).getAddress().toString() + "]");
                chatManager.removeUser(user);
            }
        } finally {
            channel.close();
        }
    }
}
