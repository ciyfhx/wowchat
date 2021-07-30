package com.ciyfhx.chat;

import io.netty.channel.ChannelFuture;

public interface IChat {

    void sendUserInfo(String username);

    void setChatGroup(ChatGroup chatGroup);
    ChatGroup getChatGroup();

    void listChatGroups();

    void setListChatGroupsListener(IListChatGroups listChatGroups);
    IListChatGroups getListChatGroupsListener();


    ChannelFuture createGroup(String chatGroupName);

    ChannelFuture joinChatGroup(ChatGroup chatGroup);
    ChannelFuture leaveChatGroup(ChatGroup chatGroup);

    void sendMessage(String message);

    void setMessageReceivedListener(IMessageReceived messageReceived);
    IMessageReceived getMessageReceivedListener();

}
