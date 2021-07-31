package com.ciyfhx.chat.client;

import com.ciyfhx.chat.ChatGroup;
import com.ciyfhx.chat.packets.*;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;


public class ClientChatHandler implements IChatLobby {

    private ChannelHandlerContext ctx;
    private Map<UUID, ClientChatGroup> chatGroups;

    private List<IListChatGroups> listChatGroups;
    private List<IJoinedChatGroup> joinedChatGroup;


    public ClientChatHandler(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.chatGroups = new HashMap();
        this.listChatGroups = new ArrayList<>();
        this.joinedChatGroup = new ArrayList<>();
    }

    @Override
    public void sendUserInfo(String username) {
        //Send join packet
        var newUserPacket = new NewUserPacket();
        newUserPacket.setUsername(username);
        ctx.writeAndFlush(newUserPacket);
    }

    protected void addChatGroup(com.ciyfhx.chat.client.ClientChatGroup chatGroup){
        this.chatGroups.put(chatGroup.getChatGroupId(), chatGroup);
    }

    @Override
    public void listChatGroups() {
        var getChatGroupsIdsPacket = new GetChatGroupsIdsPacket();
        ctx.writeAndFlush(getChatGroupsIdsPacket);
    }

    @Override
    public void addListChatGroupsListener(IListChatGroups listChatGroups) {
        this.listChatGroups.add(listChatGroups);
    }

    @Override
    public void removeListChatGroupsListener(IListChatGroups listChatGroups) {
        this.listChatGroups.remove(listChatGroups);
    }

    @Override
    public List<IListChatGroups> getListChatGroupsListeners() {
        return this.listChatGroups;
    }

    @Override
    public void addJoinedChatGroupListener(IJoinedChatGroup joinedChatGroup) {
        this.joinedChatGroup.add(joinedChatGroup);
    }

    @Override
    public void removeJoinedChatGroupListener(IJoinedChatGroup joinedChatGroup) {
        this.joinedChatGroup.remove(joinedChatGroup);
    }

    @Override
    public List<IJoinedChatGroup> getJoinedChatGroupListeners() {
        return this.joinedChatGroup;
    }


    @Override
    public ChannelFuture createChatGroup(String chatGroupName) {
        var newChatGroup = new NewChatGroupPacket();
        newChatGroup.setChatGroupName(chatGroupName);
        return ctx.writeAndFlush(newChatGroup);
    }

    @Override
    public ChannelFuture joinChatGroup(ChatGroup chatGroup) {
        var joinChatGroupPacket = new JoinChatGroupPacket();
        joinChatGroupPacket.setChatGroupIdToJoin(chatGroup.getChatGroupId());
        return ctx.writeAndFlush(joinChatGroupPacket);
    }

    @Override
    public ChannelFuture leaveChatGroup(ChatGroup chatGroup) {
        var leaveChatGroup = new LeaveChatGroupPacket();
        leaveChatGroup.setChatGroupIdToLeave(chatGroup.getChatGroupId());
        return ctx.writeAndFlush(leaveChatGroup);
    }

    public com.ciyfhx.chat.client.ClientChatGroup getChatGroupFromId(UUID chatGroupId){
        return chatGroups.get(chatGroupId);
    }

}
