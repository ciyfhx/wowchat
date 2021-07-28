package com.ciyfhx.chat.packets;

import com.ciyfhx.chat.BasicChatGroup;
import com.ciyfhx.network.Packet;
import com.ciyfhx.network.PacketId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.UUID;

public class JoinedChatGroupPacket extends Packet {

    private BasicChatGroup chatGroup;

    public JoinedChatGroupPacket() {
        super(new PacketId(Packets.JOINED_CHAT_GROUP));
    }

    public BasicChatGroup getChatGroup() {
        return chatGroup;
    }

    public void setChatGroup(BasicChatGroup chatGroup) {
        this.chatGroup = chatGroup;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf out) {
        NetworkUtils.writeUUID(chatGroup.getChatGroupId(), out);
        NetworkUtils.writeString(chatGroup.getChatGroupName(), out);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        UUID chatGroupId = NetworkUtils.readUUID(in);
        String chatGroupName = NetworkUtils.readString(in);

        this.chatGroup = new BasicChatGroup(chatGroupId, chatGroupName);
        out.add(this);
    }
}
