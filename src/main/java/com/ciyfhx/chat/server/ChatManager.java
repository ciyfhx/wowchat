package com.ciyfhx.chat.server;

import com.ciyfhx.chat.ChatGroup;
import com.ciyfhx.chat.ChatGroupNotFoundException;
import com.ciyfhx.chat.User;
import com.ciyfhx.chat.UserNotFoundException;
import io.netty.channel.Channel;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChatManager {

    private ConcurrentMap<UUID, ServerChatGroup> chatGroups;
    private ConcurrentMap<Channel, ServerUser> userChannels;

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
        var user = new ServerUser(username, channel);
        userChannels.put(channel, user);
        return user;
    }

    public void removeUser(ServerUser user){
        for(ServerChatGroup serverChatGroup : user.getChatGroups()){
            serverChatGroup.leaveChatGroup(user);
        }
        userChannels.remove(user.channel());
    }

    public void removeUserFromChannel(Channel channel) throws UserNotFoundException {
        ServerUser user = this.getUserFromChannel(channel);
        if(user == null) throw new UserNotFoundException("User cannot be found!");
        this.removeUser(user);
    }

    public ServerUser getUserFromChannel(Channel channel){
        return userChannels.get(channel);
    }

    public Collection<? extends ChatGroup> getAvailableChatGroups() {
        return this.chatGroups.values();
    }

}
