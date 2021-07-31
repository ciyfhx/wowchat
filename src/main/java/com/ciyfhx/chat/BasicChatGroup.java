package com.ciyfhx.chat;

import java.util.UUID;

public class BasicChatGroup extends ChatGroup {

    private int numberOfUsersInChatGroup = 0;

    public BasicChatGroup(UUID chatGroupId, String chatGroupName, int sizeOfChatGroup, int numberOfUsersInChatGroup) {
        super(chatGroupId, chatGroupName, sizeOfChatGroup);
        this.numberOfUsersInChatGroup = numberOfUsersInChatGroup;
    }

    @Override
    public int getNumberOfUsersInChatGroup() {
        return numberOfUsersInChatGroup;
    }
}
