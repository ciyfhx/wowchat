package com.ciyfhx.chat;

import java.util.UUID;

public abstract class ChatGroup {

    private final String chatGroupName;
    private final UUID chatGroupId;

    private final int sizeOfChatGroup;

    public ChatGroup(UUID chatGroupId, String chatGroupName, int size) {
        this.chatGroupId = chatGroupId;
        this.chatGroupName = chatGroupName;
        this.sizeOfChatGroup = size;
    }

    public UUID getChatGroupId() {
        return chatGroupId;
    }

    public String getChatGroupName() {
        return chatGroupName;
    }

    public int getSizeOfChatGroup() {
        return sizeOfChatGroup;
    }

    public abstract int getNumberOfUsersInChatGroup();
}
