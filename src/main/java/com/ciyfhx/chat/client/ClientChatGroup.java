package com.ciyfhx.chat.client;

import com.ciyfhx.chat.ChatGroup;
import com.ciyfhx.chat.User;
import com.ciyfhx.chat.packets.SendMessagePacket;
import io.netty.channel.Channel;

import java.util.*;

public class ClientChatGroup extends ChatGroup implements IChat, IListUsersInChatGroup {

    private Channel channel;

    private Set<User> users;
    private int currentNumberOfUsersInChatGroup = 0;

    private List<IMessageReceived> messageReceived;
    private List<IListUsersInChatGroup> listUsersInChatGroup;

    public ClientChatGroup(ChatGroup chatGroup, Channel channel){
        this(chatGroup.getChatGroupId(), chatGroup.getChatGroupName(), chatGroup.getSizeOfChatGroup(), chatGroup.getNumberOfUsersInChatGroup(), channel);
    }

    public ClientChatGroup(UUID chatGroupId, String chatGroupName, int sizeOfChatGroup, int currentNumberOfUsersInChatGroup, Channel channel) {
        super(chatGroupId, chatGroupName, sizeOfChatGroup);
        this.channel = channel;
        this.currentNumberOfUsersInChatGroup = currentNumberOfUsersInChatGroup;
        this.messageReceived = new ArrayList<>();
        this.listUsersInChatGroup = new ArrayList<>();
        this.users = new HashSet<>();

        this.addListUsersInChatGroupListener(this);
    }

    public Channel channel() { return channel; }

    @Override
    public void sendMessage(String message) {
        var messagePacket = new SendMessagePacket();
        messagePacket.setMessage(message);
        messagePacket.setChatGroupId(this.getChatGroupId());
        channel.writeAndFlush(messagePacket);
    }

    @Override
    public void addMessageReceivedListener(IMessageReceived messageReceived) {
        this.messageReceived.add(messageReceived);
    }

    @Override
    public void removeMessageReceivedListener(IMessageReceived messageReceived) {
        this.messageReceived.remove(messageReceived);
    }

    @Override
    public void addListUsersInChatGroupListener(IListUsersInChatGroup listUsersInChatGroupListener) {
        this.listUsersInChatGroup.add(listUsersInChatGroupListener);
    }

    @Override
    public void removeListUsersInChatGroupListener(IListUsersInChatGroup listUsersInChatGroupListener) {
        this.listUsersInChatGroup.remove(listUsersInChatGroupListener);
    }

    public List<IListUsersInChatGroup> getListUsersInChatGroupListeners() {
        return this.listUsersInChatGroup;
    }

    public List<IMessageReceived> getMessageReceivedListeners() {
        return this.messageReceived;
    }

    @Override
    public void onListUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public void onUserJoined(User userJoined) {
        this.users.add(userJoined);
        this.currentNumberOfUsersInChatGroup++;
    }

    @Override
    public void onUserLeave(User userLeave) {
        this.users.remove(userLeave);
        this.currentNumberOfUsersInChatGroup--;
    }

    public Set<User> getUsers(){
        return this.users;
    }

    @Override
    public int getNumberOfUsersInChatGroup() {
        return this.currentNumberOfUsersInChatGroup;
    }


}
