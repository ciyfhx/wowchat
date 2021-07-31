package com.ciyfhx.chat.packets;

import com.ciyfhx.chat.ChatGroup;
import com.ciyfhx.chat.network.Packet;
import com.ciyfhx.chat.network.PacketId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class JoinedChatGroupPacket extends Packet {

    private ChatGroup chatGroup;

    public JoinedChatGroupPacket() {
        super(new PacketId(Packets.JOINED_CHAT_GROUP));
    }

    public ChatGroup getChatGroup() {
        return chatGroup;
    }

    public void setChatGroup(ChatGroup chatGroup) {
        this.chatGroup = chatGroup;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf out) {
        NetworkUtils.writeChatGroup(chatGroup, out);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        this.chatGroup = NetworkUtils.readChatGroup(in);
        out.add(this);
    }
}
