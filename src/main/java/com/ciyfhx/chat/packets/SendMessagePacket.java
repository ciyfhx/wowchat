package com.ciyfhx.chat.packets;

import com.ciyfhx.chat.ChatGroupNotFoundException;
import com.ciyfhx.chat.network.Packet;
import com.ciyfhx.chat.network.PacketId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.UUID;

public class SendMessagePacket extends Packet {

    private UUID chatGroupId;
    private String message;

    public SendMessagePacket() {
        super(new PacketId(Packets.SEND_MESSAGE));
    }

    public UUID getChatGroupId() {
        return chatGroupId;
    }

    public void setChatGroupId(UUID chatGroupId) {
        this.chatGroupId = chatGroupId;
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
        NetworkUtils.writeString(message, out);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        chatGroupId = NetworkUtils.readUUID(in);
        message = NetworkUtils.readString(in);

        out.add(this);
    }
}
