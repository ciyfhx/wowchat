package com.ciyfhx.chat.packets;

import com.ciyfhx.chat.network.Packet;
import com.ciyfhx.chat.network.PacketId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class NewChatGroupPacket extends Packet {

    private String chatGroupName;

    public NewChatGroupPacket() {
        super(new PacketId(Packets.NEW_CHAT_GROUP));
    }

    public String getChatGroupName() {
        return chatGroupName;
    }

    public void setChatGroupName(String chatGroupName) {
        this.chatGroupName = chatGroupName;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf out) {
        NetworkUtils.writeString(chatGroupName, out);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        chatGroupName = NetworkUtils.readString(in);
        out.add(this);
    }
}
