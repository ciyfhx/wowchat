package com.ciyfhx.network;

import com.ciyfhx.chat.ChatGroup;
import com.ciyfhx.chat.ClientChatHandler;
import com.ciyfhx.chat.IChat;
import com.ciyfhx.chat.packets.JoinedChatGroupPacket;
import com.ciyfhx.chat.packets.ListChatGroupIdsPacket;
import com.ciyfhx.chat.packets.NewMessagePacket;
import com.ciyfhx.chat.packets.NewServerMessagePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientInboundMessageProcessingHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(ClientInboundMessageProcessingHandler.class);

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
            logger.info("[" + messagePacket.getUsername() + "]: " + messagePacket.getMessage());
        }else if(packet instanceof NewServerMessagePacket serverMessagePacket){
            var listener = this.handler.getMessageReceivedListener();
            if(listener != null)listener.onServerMessageReceived(serverMessagePacket);
            logger.info("SERVER: " + serverMessagePacket.getMessage());
        }
        else if(packet instanceof JoinedChatGroupPacket joinedChatGroupPacket) {
            ChatGroup chatGroup = joinedChatGroupPacket.getChatGroup();
            handler.setChatGroup(chatGroup);
        }else if(packet instanceof ListChatGroupIdsPacket listChatGroupIdsPacket){
            var listener = this.handler.getListChatGroupsListener();
            if(listener != null)listener.onListChatGroups(listChatGroupIdsPacket.getChatGroups());
            for (ChatGroup chatGroup : listChatGroupIdsPacket.getChatGroups()){
                logger.info(chatGroup.getChatGroupId() +  ":" + chatGroup.getChatGroupName());
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
