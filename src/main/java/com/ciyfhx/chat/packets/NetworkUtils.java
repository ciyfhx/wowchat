package com.ciyfhx.chat.packets;

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

}
