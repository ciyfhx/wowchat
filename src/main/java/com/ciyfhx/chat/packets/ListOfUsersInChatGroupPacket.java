package com.ciyfhx.chat.packets;

import com.ciyfhx.chat.User;
import com.ciyfhx.chat.network.Packet;
import com.ciyfhx.chat.network.PacketId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ListOfUsersInChatGroupPacket extends Packet {

    private Set<? extends User> users;
    private UUID chatGroupId;

    public ListOfUsersInChatGroupPacket() {
        super(new PacketId(Packets.GET_USERS_IN_CHAT_GROUP));
    }

    public UUID getChatGroupId() {
        return chatGroupId;
    }

    public void setChatGroupId(UUID chatGroupId) {
        this.chatGroupId = chatGroupId;
    }

    public Set<? extends User> getUsers() {
        return users;
    }

    public void setUsers(Set<? extends User> users) {
        this.users = users;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf out) throws Exception {
        //Write Chat Group Id
        NetworkUtils.writeUUID(chatGroupId, out);

        //Total number of users in chat group
        out.writeInt(users.size());

        for(var user : users){
            NetworkUtils.writeString(user.getUsername(), out);
        }
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        var users = new HashSet<User>();

        UUID chatGroupId = NetworkUtils.readUUID(in);

        //Total number of users in chat group
        int sizeOfUsersInGroup = in.readInt();

        for (int i = 0; i < sizeOfUsersInGroup; i++) {
            String username = NetworkUtils.readString(in);
            var user = new User(username);
            users.add(user);
        }

        this.chatGroupId = chatGroupId;
        this.users = users;
        out.add(this);
    }
}
