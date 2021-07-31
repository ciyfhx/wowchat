package com.ciyfhx.chat.packets;

import com.ciyfhx.chat.network.Packet;
import com.ciyfhx.chat.network.PacketId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.UUID;

public class JoinChatGroupPacket extends Packet {

    private UUID chatGroupIdToJoin;

    public JoinChatGroupPacket() {
        super(new PacketId(Packets.JOIN_CHAT_GROUP));
    }

    public UUID getChatGroupIdToJoin() {
        return chatGroupIdToJoin;
    }

    public void setChatGroupIdToJoin(UUID chatGroupIdToJoin) {
        this.chatGroupIdToJoin = chatGroupIdToJoin;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf out) throws Exception {
        NetworkUtils.writeUUID(chatGroupIdToJoin, out);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        chatGroupIdToJoin = NetworkUtils.readUUID(in);
        out.add(this);
    }
}
