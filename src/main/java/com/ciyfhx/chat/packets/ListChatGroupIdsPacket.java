package com.ciyfhx.chat.packets;

import com.ciyfhx.chat.BasicChatGroup;
import com.ciyfhx.chat.ChatGroup;
import com.ciyfhx.network.Packet;
import com.ciyfhx.network.PacketId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;

public class ListChatGroupIdsPacket extends Packet {

    private Collection<? extends ChatGroup> chatGroups;

    public ListChatGroupIdsPacket() {
        super(new PacketId(Packets.LIST_CHAT_GROUPS_ID));
    }

    public Collection<? extends ChatGroup> getChatGroups() {
        return chatGroups;
    }

    public void setChatGroups(Collection<? extends ChatGroup> chatGroups) {
        this.chatGroups = chatGroups;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf out) throws Exception {
        int length = chatGroups.size();
        // Total Chat Groups
        out.writeInt(length);

        for(ChatGroup chatGroup : chatGroups){
            NetworkUtils.writeChatGroup(chatGroup, out);
        }

    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        var chatGroups = new ArrayList<ChatGroup>();
        //Total Chat Groups
        int length = in.readInt();

        for (int i = 0; i < length; i++) {
            ChatGroup chatGroup = NetworkUtils.readChatGroup(in);
            chatGroups.add(chatGroup);
        }

        this.chatGroups = chatGroups;
        out.add(this);
    }
}
