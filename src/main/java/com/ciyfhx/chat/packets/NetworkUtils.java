package com.ciyfhx.chat.packets;

import com.ciyfhx.chat.BasicChatGroup;
import com.ciyfhx.chat.ChatGroup;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class NetworkUtils {

    public static void writeUUID(UUID uuid, ByteBuf out){
        out.writeLong(uuid.getMostSignificantBits());
        out.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteBuf in){
        long mSb = in.readLong();
        long lSb = in.readLong();
        UUID uuid = new UUID(mSb, lSb);
        return uuid;
    }

    public static void writeString(String content, ByteBuf out){
        byte[] data = content.getBytes(StandardCharsets.UTF_8);
        out.writeInt(data.length);
        out.writeBytes(data);
    }

    public static String readString(ByteBuf in){
        int length = in.readInt();
        byte[] data = new byte[length];
        in.readBytes(data);
        String content = new String(data, StandardCharsets.UTF_8);
        return content;
    }


    public static void writeChatGroup(ChatGroup chatGroup, ByteBuf out){
        NetworkUtils.writeUUID(chatGroup.getChatGroupId(), out);
        NetworkUtils.writeString(chatGroup.getChatGroupName(), out);
        out.writeInt(chatGroup.getSizeOfChatGroup());
        out.writeInt(chatGroup.getNumberOfUsersInChatGroup());
    }

    public static ChatGroup readChatGroup(ByteBuf in){
        UUID chatGroupId = NetworkUtils.readUUID(in);
        String chatGroupName = NetworkUtils.readString(in);
        int sizeOfChatGroup = in.readInt();
        int numberOfUsers = in.readInt();

        return new BasicChatGroup(chatGroupId, chatGroupName, sizeOfChatGroup, numberOfUsers);
    }


}
