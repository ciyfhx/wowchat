package com.ciyfhx.gui.server;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class WowChatServerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(WowChatServerConnection.class)
                .in(Scopes.SINGLETON);
    }
}
