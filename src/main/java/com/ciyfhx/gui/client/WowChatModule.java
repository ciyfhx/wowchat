package com.ciyfhx.gui.client;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class WowChatModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(WowChatConnection.class)
                .in(Scopes.SINGLETON);
    }
}
