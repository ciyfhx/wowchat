package com.ciyfhx.chat;

import java.util.UUID;

public class BasicChatGroup {

    private final String chatGroupName;
    private final UUID chatGroupId;

    public BasicChatGroup(UUID chatGroupId, String chatGroupName) {
        this.chatGroupId = chatGroupId;
        this.chatGroupName = chatGroupName;
    }

    public UUID getChatGroupId() {
        return chatGroupId;
    }

    public String getChatGroupName() {
        return chatGroupName;
    }

}
