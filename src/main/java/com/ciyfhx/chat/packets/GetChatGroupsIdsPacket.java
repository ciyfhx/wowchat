package com.ciyfhx.chat.packets;

import com.ciyfhx.chat.network.Packet;
import com.ciyfhx.chat.network.PacketId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class GetChatGroupsIdsPacket extends Packet {
    public GetChatGroupsIdsPacket() {
        super(new PacketId(Packets.GET_CHAT_GROUPS_ID));
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf out) throws Exception {

    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        out.add(this);
    }
}
