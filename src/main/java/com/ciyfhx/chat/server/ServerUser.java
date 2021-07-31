package com.ciyfhx.chat.server;

import com.ciyfhx.chat.UserChannel;
import io.netty.channel.Channel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ServerUser extends UserChannel {

    private Set<ServerChatGroup> groups;

    public ServerUser(String username, Channel channel) {
        super(username, channel);
        this.groups = new HashSet<>();
    }

    public Collection<ServerChatGroup> getChatGroups() {
        return groups;
    }

    public void addChatGroup(ServerChatGroup group) {
        this.groups.add(group);
    }

    public void removeChatGroup(ServerChatGroup group) {
        this.groups.remove(group);
    }

}
