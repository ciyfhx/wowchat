package com.ciyfhx.chat.network;

import com.ciyfhx.chat.User;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public abstract class Packet {

    private PacketId packetId;

    public Packet(PacketId packetId) {
        this.packetId = packetId;
    }

    public PacketId getPacketId() {
        return packetId;
    }

    public abstract void encode(ChannelHandlerContext ctx, ByteBuf out) throws Exception;
    public abstract void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception;

}
