package com.ciyfhx.chat;

import com.ciyfhx.chat.packets.*;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

public class ClientChatHandler implements IChat {

    private ChannelHandlerContext ctx;
    private BasicChatGroup chatGroup;

    private IMessageReceived messageReceived;
    private IListChatGroups listChatGroups;

    public ClientChatHandler(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void sendUserInfo(String username) {
        //Send join packet
        var newUserPacket = new NewUserPacket();
        newUserPacket.setUsername(username);
        ctx.writeAndFlush(newUserPacket);
    }

    @Override
    public void setChatGroup(BasicChatGroup chatGroup) {
        this.chatGroup = chatGroup;
    }

    @Override
    public BasicChatGroup getChatGroup() {
        return this.chatGroup;
    }

    @Override
    public void listChatGroups() {
        var getChatGroupsIdsPacket = new GetChatGroupsIdsPacket();
        ctx.writeAndFlush(getChatGroupsIdsPacket);
    }

    @Override
    public void setListChatGroupsListener(IListChatGroups listChatGroups) {
        this.listChatGroups = listChatGroups;
    }

    @Override
    public IListChatGroups getListChatGroupsListener() {
        return this.listChatGroups;
    }

    @Override
    public ChannelFuture createGroup(String chatGroupName) {
        var newChatGroup = new NewChatGroupPacket();
        newChatGroup.setChatGroupName(chatGroupName);
        return ctx.writeAndFlush(newChatGroup);
    }

    @Override
    public void removeGroup(ServerChatGroup serverChatGroup) {

    }

    @Override
    public ChannelFuture joinChatGroup(BasicChatGroup chatGroup) {
        var joinChatGroupPacket = new JoinChatGroupPacket();
        joinChatGroupPacket.setChatGroupIdToJoin(chatGroup.getChatGroupId());
        return ctx.writeAndFlush(joinChatGroupPacket);
    }

    @Override
    public void leaveChatGroup(BasicChatGroup chatGroup) {

    }

    @Override
    public void sendMessage(String message) {
        var messagePacket = new SendMessagePacket();
        messagePacket.setMessage(message);
        messagePacket.setChatGroupId(this.getChatGroup().getChatGroupId());
        ctx.writeAndFlush(messagePacket);
    }

    @Override
    public void setMessageReceivedListener(IMessageReceived messageReceived) {
        this.messageReceived = messageReceived;
    }

    @Override
    public IMessageReceived getMessageReceivedListener() {
        return this.messageReceived;
    }
}
