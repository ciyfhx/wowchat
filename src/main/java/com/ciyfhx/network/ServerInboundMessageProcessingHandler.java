package com.ciyfhx.network;

import com.ciyfhx.chat.ServerChatGroup;
import com.ciyfhx.chat.ChatManager;
import com.ciyfhx.chat.User;
import com.ciyfhx.chat.packets.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

@ChannelHandler.Sharable
public class ServerInboundMessageProcessingHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(ServerInboundMessageProcessingHandler.class);
    private ChatManager chatManager;

    public ServerInboundMessageProcessingHandler(){
        chatManager = new ChatManager();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Packet packet = (Packet) msg;
        User user = chatManager.getUserFromChannel(ctx.channel());
        packet.setSender(user);

        if(packet instanceof NewUserPacket userPacket){
            chatManager.createUser(userPacket.getUsername(), ctx.channel());
            logger.info("New User from " + userPacket.getUsername() + "[" + ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().toString() +"]");
        } else if(packet instanceof SendMessagePacket messagePacket){
            logger.info("[" + messagePacket.getSender().username() + "]: " + messagePacket.getMessage());
            ServerChatGroup serverChatGroup = chatManager.getChatGroupFromId(messagePacket.getChatGroupId());
            serverChatGroup.sendMessageInGroup(messagePacket.getSender(), messagePacket.getMessage());
        }else if (packet instanceof NewChatGroupPacket chatGroupPacket) {
            logger.info("New chat group: " + chatGroupPacket.getChatGroupName());
            var chatGroup = chatManager.createChatGroup(chatGroupPacket.getChatGroupName());
            chatGroup.joinChatGroup(user);

            // Send Chat Group id
            sendJoinedChatGroupPacket(ctx, user, chatGroup);


        }else if(packet instanceof JoinChatGroupPacket joinChatGroupPacket){
            var chatGroup = chatManager.getChatGroupFromId(joinChatGroupPacket.getChatGroupIdToJoin());
            logger.info(user.username() + " joined chat group: " + chatGroup.getChatGroupName());
            chatGroup.joinChatGroup(user);

            // Send Chat Group id
            sendJoinedChatGroupPacket(ctx, user, chatGroup);

        }else if(packet instanceof GetChatGroupsIdsPacket) {
            var listChatGroupsIdsPacket = new ListChatGroupIdsPacket();
            listChatGroupsIdsPacket.setChatGroups(chatManager.getAvailableChatGroups());

            ctx.writeAndFlush(listChatGroupsIdsPacket);
        }

    }

    private void sendJoinedChatGroupPacket(ChannelHandlerContext ctx, User user, ServerChatGroup chatGroup) {
        var joinedChatGroupPacket = new JoinedChatGroupPacket();
        joinedChatGroupPacket.setChatGroup(chatGroup);
        var future = ctx.writeAndFlush(joinedChatGroupPacket);

        future.addListener(_future -> {
            chatGroup.sendServerMessage(user.username() + " has joined the group");
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Remove user if there is an exception
        try {
            User user = chatManager.getUserFromChannel(ctx.channel());
            logger.error("User removed " + user.username() + "[" + ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().toString() +"]");
            chatManager.removeUser(user);
        }finally {
            ctx.close();
        }
    }
}
