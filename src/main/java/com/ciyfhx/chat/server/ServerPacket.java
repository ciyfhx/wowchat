package com.ciyfhx.chat.server;

import com.ciyfhx.chat.network.Packet;

public class ServerPacket {

    private Packet packet;
    private ServerUser sender;

    public ServerPacket(Packet packet, ServerUser sender) {
        this.packet = packet;
        this.sender = sender;
    }

    public Packet getPacket() {
        return packet;
    }

    public ServerUser getSender() {
        return sender;
    }
}
