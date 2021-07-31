package com.ciyfhx.chat.client;

import com.ciyfhx.chat.ChatGroup;
import io.netty.channel.ChannelFuture;

import java.util.List;

public interface IChatLobby {

    void sendUserInfo(String username);

    void listChatGroups();

    ChannelFuture createChatGroup(String chatGroupName);

    ChannelFuture joinChatGroup(ChatGroup chatGroup);
    ChannelFuture leaveChatGroup(ChatGroup chatGroup);

    void addListChatGroupsListener(IListChatGroups listChatGroups);
    void removeListChatGroupsListener(IListChatGroups listChatGroups);
    List<IListChatGroups> getListChatGroupsListeners();

    void addJoinedChatGroupListener(IJoinedChatGroup joinedChatGroup);
    void removeJoinedChatGroupListener(IJoinedChatGroup joinedChatGroup);
    List<IJoinedChatGroup> getJoinedChatGroupListeners();

}
