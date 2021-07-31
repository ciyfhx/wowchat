package com.ciyfhx.chat.packets;


import com.ciyfhx.chat.network.Packet;
import com.ciyfhx.chat.network.PacketId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class NewUserPacket extends Packet {

    private String username;

    public NewUserPacket() {
        super(new PacketId(Packets.NEW_USER));
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf out) {
        NetworkUtils.writeString(username, out);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        username = NetworkUtils.readString(in);

        out.add(this);
    }
}
