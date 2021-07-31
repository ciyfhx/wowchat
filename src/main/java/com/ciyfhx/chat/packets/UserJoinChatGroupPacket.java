package com.ciyfhx.chat.packets;

import com.ciyfhx.chat.User;
import com.ciyfhx.chat.network.Packet;
import com.ciyfhx.chat.network.PacketId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.UUID;

public class UserJoinChatGroupPacket extends Packet {

    private User userJoined;
    private UUID chatGroupId;

    public UserJoinChatGroupPacket() {
        super(new PacketId(Packets.USER_JOIN_CHAT_GROUP));
    }

    public User getUserJoined() {
        return userJoined;
    }

    public void setUserJoined(User userJoined) {
        this.userJoined = userJoined;
    }

    public UUID getChatGroupId() {
        return chatGroupId;
    }

    public void setChatGroupId(UUID chatGroupId) {
        this.chatGroupId = chatGroupId;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf out) throws Exception {
        NetworkUtils.writeUUID(chatGroupId, out);
        NetworkUtils.writeString(userJoined.getUsername(), out);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        UUID chatGroupId = NetworkUtils.readUUID(in);
        String username = NetworkUtils.readString(in);
        this.chatGroupId = chatGroupId;
        this.userJoined = new User(username);

        out.add(this);
    }
}
