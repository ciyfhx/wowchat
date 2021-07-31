package com.ciyfhx.gui.client;

import com.ciyfhx.chat.client.ClientChatGroup;
import com.ciyfhx.main.WowChatClient;

public class WowChatClientConnection {

    private WowChatClient client;
    private ClientChatGroup currentChatGroup;

    public WowChatClient getClient() {
        return client;
    }

    public void setClient(WowChatClient client) {
        this.client = client;
    }

    public ClientChatGroup getCurrentChatGroup() {
        return currentChatGroup;
    }

    public void setCurrentChatGroup(ClientChatGroup currentChatGroup) {
        this.currentChatGroup = currentChatGroup;
    }
}
