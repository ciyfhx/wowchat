package com.ciyfhx.chat;

import com.ciyfhx.chat.packets.NewMessagePacket;
import com.ciyfhx.chat.packets.NewServerMessagePacket;
import com.ciyfhx.network.Packet;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ServerChatGroup extends BasicChatGroup {

    private final Set<User> users;
    private final ChannelGroup channelGroup;

    public ServerChatGroup(UUID chatGroupId, String chatGroupName) {
        super(chatGroupId, chatGroupName);
        users = new HashSet<>();
        this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public void joinChatGroup(User user){
        if(users.contains(user)) {
            throw new IllegalStateException("User is already part of this chat group");
        }
        users.add(user);
        channelGroup.add(user.channel());
        user.groups().add(this);

    }

    public void leaveChatGroup(User user){
        if(!users.contains(user)) {
            throw new IllegalStateException("User is not part of this chat group");
        }
        users.remove(user);
        channelGroup.remove(user.channel());
        user.groups().remove(this);

        this.sendServerMessage(user.username() + " has left the group");
    }

    public void sendMessageInGroup(User sender, String message) {
        if (!users.contains(sender)) {
            throw new IllegalStateException("User not inside the chat group");
        }

        var messageData = new NewMessagePacket();
        messageData.setMessage(message);
        messageData.setChatGroupId(this.getChatGroupId());
        messageData.setUsername(sender.username());
        broadcast(messageData);

    }

    public ChannelGroupFuture sendServerMessage(String message){
        var messageData = new NewServerMessagePacket();
        messageData.setMessage(message);
        messageData.setChatGroupId(this.getChatGroupId());
        return broadcast(messageData);
    }

    private ChannelGroupFuture broadcast(Packet message) {
        return channelGroup.writeAndFlush(message);
    }

}
