package com.ciyfhx.chat.packets;

import com.ciyfhx.chat.network.Packet;
import com.ciyfhx.chat.network.PacketId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.UUID;

public class LeaveChatGroupPacket extends Packet {

    private UUID chatGroupIdToLeave;

    public LeaveChatGroupPacket() {
        super(new PacketId(Packets.LEAVE_CHAT_GROUP));
    }

    public UUID getChatGroupIdToLeave() {
        return chatGroupIdToLeave;
    }

    public void setChatGroupIdToLeave(UUID chatGroupIdToLeave) {
        this.chatGroupIdToLeave = chatGroupIdToLeave;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf out) throws Exception {
        NetworkUtils.writeUUID(chatGroupIdToLeave, out);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        chatGroupIdToLeave = NetworkUtils.readUUID(in);

        out.add(this);
    }
}
