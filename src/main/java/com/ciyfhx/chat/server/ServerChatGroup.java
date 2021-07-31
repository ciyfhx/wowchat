package com.ciyfhx.chat.server;

import com.ciyfhx.chat.ChatGroup;
import com.ciyfhx.chat.User;
import com.ciyfhx.chat.packets.*;
import com.ciyfhx.chat.network.Packet;
import io.netty.channel.ChannelFuture;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ServerChatGroup extends ChatGroup {

    private final Set<ServerUser> users;
    private final ChannelGroup channelGroup;

    private static final int SIZE_OF_CHAT_GROUP = 5;

    public ServerChatGroup(UUID chatGroupId, String chatGroupName){
        this(chatGroupId, chatGroupName, SIZE_OF_CHAT_GROUP);
    }

    public ServerChatGroup(UUID chatGroupId, String chatGroupName, int sizeOfChatGroup) {
        super(chatGroupId, chatGroupName, sizeOfChatGroup);
        users = new HashSet<>();
        this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public void joinChatGroup(ServerUser user){
        if(users.contains(user))
            throw new IllegalStateException("User is already part of this chat group");
        else if(getNumberOfUsersInChatGroup() >= getSizeOfChatGroup())
            throw new IllegalStateException("Max chat users reached");

        //Add user to chat group
        users.add(user);
        channelGroup.add(user.channel());
        user.addChatGroup(this);

        //Tell the recently joined user that chat group id
        sendJoinedPacketToUser(user).addListener(future -> {
            //Send the list of current users inside the chat group to the newly joined user
            sendListOfUsersInChat(user).addListener(future1 -> {
                //Broadcast server message to other users that a user has joined
                this.sendServerMessage(user.getUsername() + " has joined the group");

                // Broadcast joined packet to other users in the chat group
                sendUserJoinPacketToAllUsers(user);
            });
        });
    }

    private void sendUserJoinPacketToAllUsers(ServerUser userJoined){
        var userJoinedChatGroupPacket = new UserJoinChatGroupPacket();
        userJoinedChatGroupPacket.setChatGroupId(this.getChatGroupId());
        userJoinedChatGroupPacket.setUserJoined(userJoined);
        channelGroup.writeAndFlush(userJoinedChatGroupPacket, channel -> channel!=userJoined.channel());
    }

    private ChannelFuture sendJoinedPacketToUser(ServerUser user){
        var joinedChatGroupPacket = new JoinedChatGroupPacket();
        joinedChatGroupPacket.setChatGroup(this);
        return user.channel().writeAndFlush(joinedChatGroupPacket);
    }

    public void leaveChatGroup(ServerUser user){
        if(!users.contains(user)) {
            throw new IllegalStateException("User is not part of this chat group");
        }

        //Remove user from chat group
        users.remove(user);
        channelGroup.remove(user.channel());
        user.removeChatGroup(this);

        //Broadcast server message to other users that a user has left
        this.sendServerMessage(user.getUsername() + " has left the group");

        // Broadcast leave packet to other users in the chat group
        sendUserLeavePacket(user);
    }

    private void sendUserLeavePacket(ServerUser userLeave){
        var userLeaveChatGroupPacket = new UserLeaveChatGroupPacket();
        userLeaveChatGroupPacket.setChatGroupId(this.getChatGroupId());
        userLeaveChatGroupPacket.setUserLeft(userLeave);
        channelGroup.writeAndFlush(userLeaveChatGroupPacket, channel -> channel!=userLeave.channel());
    }

    public void sendMessageInGroup(User sender, String message) {
        if (!users.contains(sender)) {
            throw new IllegalStateException("User not inside the chat group");
        }

        var messageData = new NewMessagePacket();
        messageData.setMessage(message);
        messageData.setChatGroupId(this.getChatGroupId());
        messageData.setUsername(sender.getUsername());
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

    private ChannelFuture sendListOfUsersInChat(ServerUser user){
        //Send users inside chat group
        var listOfUserInGroupChat = new ListOfUsersInChatGroupPacket();
        listOfUserInGroupChat.setChatGroupId(this.getChatGroupId());
        listOfUserInGroupChat.setUsers(this.getUsers());
        return user.channel().writeAndFlush(listOfUserInGroupChat);
    }

    public Set<ServerUser> getUsers() {
        return users;
    }

    @Override
    public int getNumberOfUsersInChatGroup() {
        return users.size();
    }
}
