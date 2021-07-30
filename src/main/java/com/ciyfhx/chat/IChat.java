package com.ciyfhx.chat;

import io.netty.channel.ChannelFuture;

public interface IChat {

    void sendUserInfo(String username);

    void setChatGroup(BasicChatGroup chatGroup);
    BasicChatGroup getChatGroup();

    void listChatGroups();

    void setListChatGroupsListener(IListChatGroups listChatGroups);
    IListChatGroups getListChatGroupsListener();


    ChannelFuture createGroup(String chatGroupName);

    ChannelFuture joinChatGroup(BasicChatGroup chatGroup);
    ChannelFuture leaveChatGroup(BasicChatGroup chatGroup);

    void sendMessage(String message);

    void setMessageReceivedListener(IMessageReceived messageReceived);
    IMessageReceived getMessageReceivedListener();

}
