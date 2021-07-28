package com.ciyfhx.chat;

import io.netty.channel.Channel;

import java.util.Objects;
import java.util.Set;

public record User(
        String username,
        Channel channel,
        Set<ServerChatGroup> groups
) {

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
