package com.ciyfhx.network;

import com.ciyfhx.chat.BasicChatGroup;
import com.ciyfhx.chat.ClientChatHandler;
import com.ciyfhx.chat.IChat;
import com.ciyfhx.chat.packets.ListChatGroupIdsPacket;
import com.ciyfhx.chat.packets.JoinedChatGroupPacket;
import com.ciyfhx.chat.packets.NewMessagePacket;
import com.ciyfhx.chat.packets.NewServerMessagePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.UUID;

public class ClientInboundMessageProcessingHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext ctx;
    private IChat handler;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        this.handler = new ClientChatHandler(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Packet packet = (Packet) msg;

        if(packet instanceof NewMessagePacket messagePacket){
            var listener = this.handler.getMessageReceivedListener();
            if(listener != null)listener.onMessageReceived(messagePacket);
            System.out.println("[" + messagePacket.getUsername() + "]: " + messagePacket.getMessage());
        }else if(packet instanceof NewServerMessagePacket serverMessagePacket){
            var listener = this.handler.getMessageReceivedListener();
            if(listener != null)listener.onServerMessageReceived(serverMessagePacket);
            System.out.println("SERVER: " + serverMessagePacket.getMessage());
        }
        else if(packet instanceof JoinedChatGroupPacket joinedChatGroupPacket) {
            BasicChatGroup chatGroup = joinedChatGroupPacket.getChatGroup();
            handler.setChatGroup(chatGroup);
        }else if(packet instanceof ListChatGroupIdsPacket listChatGroupIdsPacket){
            var listener = this.handler.getListChatGroupsListener();
            if(listener != null)listener.onListChatGroups(listChatGroupIdsPacket.getChatGroups());
            for (BasicChatGroup chatGroup : listChatGroupIdsPacket.getChatGroups()){
                System.out.println(chatGroup.getChatGroupId() +  ":" + chatGroup.getChatGroupName());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    public IChat getChatHandler() {
        return handler;
    }
}
