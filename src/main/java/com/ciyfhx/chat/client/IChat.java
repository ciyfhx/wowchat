package com.ciyfhx.chat.client;

public interface IChat {
    void sendMessage(String message);
    void addMessageReceivedListener(IMessageReceived messageReceived);
    void removeMessageReceivedListener(IMessageReceived messageReceived);

    void addListUsersInChatGroupListener(IListUsersInChatGroup listUsersInChatGroupListener);
    void removeListUsersInChatGroupListener(IListUsersInChatGroup listUsersInChatGroupListener);

}
