package com.ciyfhx.chat.client;

import com.ciyfhx.chat.ChatGroup;
import com.ciyfhx.chat.User;
import com.ciyfhx.chat.network.Packet;
import com.ciyfhx.chat.packets.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ClientInboundMessageProcessingHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(ClientInboundMessageProcessingHandler.class);

    private ChannelHandlerContext ctx;
    private ClientChatHandler handler;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        this.handler = new ClientChatHandler(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Packet packet = (Packet) msg;


        if (packet instanceof NewMessagePacket messagePacket) {
            var chatGroup = this.handler.getChatGroupFromId(messagePacket.getChatGroupId());
            var listeners = chatGroup.getMessageReceivedListeners();
            listeners.forEach(listener -> listener.onMessageReceived(messagePacket));
            logger.info("[" + messagePacket.getUsername() + "]: " + messagePacket.getMessage());
        } else if (packet instanceof NewServerMessagePacket serverMessagePacket) {
            var chatGroup = this.handler.getChatGroupFromId(serverMessagePacket.getChatGroupId());
            var listeners = chatGroup.getMessageReceivedListeners();
            listeners.forEach(listener -> listener.onServerMessageReceived(serverMessagePacket));
            logger.info("SERVER: " + serverMessagePacket.getMessage());
        } else if (packet instanceof JoinedChatGroupPacket joinedChatGroupPacket) {
            ChatGroup chatGroup = joinedChatGroupPacket.getChatGroup();
            var clientChatGroup = new ClientChatGroup(chatGroup, ctx.channel());
            handler.addChatGroup(clientChatGroup);
            handler.getJoinedChatGroupListeners().forEach(listener -> listener.onJoinedChatGroup(clientChatGroup));
        } else if (packet instanceof ListChatGroupIdsPacket listChatGroupIdsPacket) {
            var listeners = this.handler.getListChatGroupsListeners();
            listeners.forEach(listener -> listener.onListChatGroups(
                    listChatGroupIdsPacket.getChatGroups()
                            .stream()
                            .map(cg -> new ClientChatGroup(cg, ctx.channel()))
                            .collect(Collectors.toList())
            ));
            for (ChatGroup chatGroup : listChatGroupIdsPacket.getChatGroups()) {
                logger.info(chatGroup.getChatGroupId() + ":" + chatGroup.getChatGroupName());
            }
        } else if (packet instanceof ListOfUsersInChatGroupPacket listOfUsersInChatGroupPacket) {
            var chatGroup = this.handler.getChatGroupFromId(listOfUsersInChatGroupPacket.getChatGroupId());
            var listeners = chatGroup.getListUsersInChatGroupListeners();
            listeners.forEach(listener -> listener.onListUsers(
                    listOfUsersInChatGroupPacket.getUsers()
                            .stream().map(user -> (User) user).collect(Collectors.toSet())
            ));
            for (User user : listOfUsersInChatGroupPacket.getUsers()) {
                logger.info(user.getUsername());
            }
        } else if (packet instanceof UserJoinChatGroupPacket userJoinChatGroupPacket) {
            var chatGroup = this.handler.getChatGroupFromId(userJoinChatGroupPacket.getChatGroupId());
            var listeners = chatGroup.getListUsersInChatGroupListeners();
            listeners.forEach(listener -> listener.onUserJoined(userJoinChatGroupPacket.getUserJoined()));
            logger.info("User joined: " + userJoinChatGroupPacket.getUserJoined().getUsername());
        } else if (packet instanceof UserLeaveChatGroupPacket userLeaveChatGroupPacket) {
            var chatGroup = this.handler.getChatGroupFromId(userLeaveChatGroupPacket.getChatGroupId());
            var listeners = chatGroup.getListUsersInChatGroupListeners();
            listeners.forEach(listener -> listener.onUserLeave(userLeaveChatGroupPacket.getUserLeft()));
            logger.info("User left: " + userLeaveChatGroupPacket.getUserLeft().getUsername());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    public IChatLobby getChatHandler() {
        return handler;
    }
}
