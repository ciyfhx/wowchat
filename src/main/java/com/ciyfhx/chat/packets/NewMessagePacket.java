package com.ciyfhx.chat.packets;

import com.ciyfhx.chat.ChatGroupNotFoundException;
import com.ciyfhx.network.Packet;
import com.ciyfhx.network.PacketId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class NewMessagePacket extends Packet {

    private UUID chatGroupId;
    private String username;
    private String message;

    public NewMessagePacket() {
        super(new PacketId(Packets.NEW_MESSAGE));
    }

    public UUID getChatGroupId() {
        return chatGroupId;
    }

    public void setChatGroupId(UUID chatGroupId) {
        this.chatGroupId = chatGroupId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf out) throws ChatGroupNotFoundException {
        if(this.chatGroupId == null) throw new ChatGroupNotFoundException("No chat group id set");

        NetworkUtils.writeUUID(chatGroupId, out);

        NetworkUtils.writeString(username, out);
        NetworkUtils.writeString(message, out);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

        chatGroupId = NetworkUtils.readUUID(in);

        username = NetworkUtils.readString(in);
        message = NetworkUtils.readString(in);

        out.add(this);
    }
}
