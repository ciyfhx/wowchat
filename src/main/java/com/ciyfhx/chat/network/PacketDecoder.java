package com.ciyfhx.chat.network;

import com.ciyfhx.chat.packets.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketDecoder extends ReplayingDecoder<Packet> {

    private static Map<Integer, Class<? extends Packet>> registeredPackets = new HashMap<>();

    static {
        registeredPackets.put(Packets.NEW_USER, NewUserPacket.class);
        registeredPackets.put(Packets.SEND_MESSAGE, SendMessagePacket.class);
        registeredPackets.put(Packets.NEW_MESSAGE, NewMessagePacket.class);
        registeredPackets.put(Packets.NEW_SERVER_MESSAGE, NewServerMessagePacket.class);
        registeredPackets.put(Packets.NEW_CHAT_GROUP, NewChatGroupPacket.class);
        registeredPackets.put(Packets.JOIN_CHAT_GROUP, JoinChatGroupPacket.class);
        registeredPackets.put(Packets.LEAVE_CHAT_GROUP, LeaveChatGroupPacket.class);
        registeredPackets.put(Packets.JOINED_CHAT_GROUP, JoinedChatGroupPacket.class);
        registeredPackets.put(Packets.LIST_CHAT_GROUPS_ID, ListChatGroupIdsPacket.class);
        registeredPackets.put(Packets.GET_CHAT_GROUPS_ID, GetChatGroupsIdsPacket.class);
        registeredPackets.put(Packets.GET_USERS_IN_CHAT_GROUP, ListOfUsersInChatGroupPacket.class);
        registeredPackets.put(Packets.USER_JOIN_CHAT_GROUP, UserJoinChatGroupPacket.class);
        registeredPackets.put(Packets.USER_LEAVE_CHAT_GROUP, UserLeaveChatGroupPacket.class);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int id = in.readInt();

        Class<? extends Packet> clazz = registeredPackets.get(id);

        if(clazz != null){
            Constructor<? extends Packet> ctor = clazz.getConstructor();
            Packet packet = ctor.newInstance();
            packet.decode(ctx, in, out);
        }else{
            throw new UnknownPacketException("Unknown packet id of " + id);
        }

    }
}
