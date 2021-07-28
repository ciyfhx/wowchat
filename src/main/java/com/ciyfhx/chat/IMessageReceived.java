package com.ciyfhx.chat;

import com.ciyfhx.chat.packets.NewMessagePacket;
import com.ciyfhx.chat.packets.NewServerMessagePacket;

public interface IMessageReceived {

    void onMessageReceived(NewMessagePacket message);
    void onServerMessageReceived(NewServerMessagePacket message);

}
