package com.ciyfhx.chat.packets;

import com.ciyfhx.chat.User;
import com.ciyfhx.chat.network.Packet;
import com.ciyfhx.chat.network.PacketId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.UUID;

public class UserLeaveChatGroupPacket extends Packet {

    private User userLeft;
    private UUID chatGroupId;

    public UserLeaveChatGroupPacket() {
        super(new PacketId(Packets.USER_LEAVE_CHAT_GROUP));
    }

    public User getUserLeft() {
        return userLeft;
    }

    public void setUserLeft(User userLeft) {
        this.userLeft = userLeft;
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
        NetworkUtils.writeString(userLeft.getUsername(), out);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        UUID chatGroupId = NetworkUtils.readUUID(in);
        String username = NetworkUtils.readString(in);
        this.chatGroupId = chatGroupId;
        this.userLeft = new User(username);

        out.add(this);
    }
}
