package com.ciyfhx.chat;

import io.netty.channel.Channel;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChatManager {

    private ConcurrentMap<UUID, ServerChatGroup> chatGroups;
    private ConcurrentMap<Channel, User> userChannels;

    public ChatManager(){
        chatGroups = new ConcurrentHashMap<>();
        userChannels = new ConcurrentHashMap<>();
    }

    public ServerChatGroup createChatGroup(String chatGroupName){
        var chatGroup = new ServerChatGroup(UUID.randomUUID(), chatGroupName);
        chatGroups.put(chatGroup.getChatGroupId(), chatGroup);
        return chatGroup;
    }

    public ServerChatGroup getChatGroupFromId(UUID chatGroupId) throws ChatGroupNotFoundException {
        ServerChatGroup serverChatGroup = chatGroups.get(chatGroupId);
        if(serverChatGroup == null) throw new ChatGroupNotFoundException("Group chat of " + chatGroupId.toString() + " cannot be found!");
        return serverChatGroup;
    }

    public User createUser(String username, Channel channel){
        var user = new User(username, channel, new HashSet<>());
        userChannels.put(channel, user);
        return user;
    }

    public void removeUser(User user){
        for(ServerChatGroup serverChatGroup : user.groups()){
            serverChatGroup.leaveChatGroup(user);
        }
        userChannels.remove(user.channel());
    }

    public void removeUserFromChannel(Channel channel) throws UserNotFoundException {
        User user = this.getUserFromChannel(channel);
        if(user == null) throw new UserNotFoundException("User cannot be found!");
        this.removeUser(user);
    }

    public User getUserFromChannel(Channel channel){
        return userChannels.get(channel);
    }

    public Collection<? extends BasicChatGroup> getAvailableChatGroups() {
        return this.chatGroups.values();
    }

}
